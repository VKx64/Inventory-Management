package vkx64.android.inventorymanagement.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.Executors;

import vkx64.android.inventorymanagement.R;
import vkx64.android.inventorymanagement.adapters.ProductAdapter;
import vkx64.android.inventorymanagement.database.DatabaseClient;
import vkx64.android.inventorymanagement.database.TableGroup;
import vkx64.android.inventorymanagement.database.TableProduct;
import vkx64.android.inventorymanagement.dialogs.AddGroupDialog;
import vkx64.android.inventorymanagement.dialogs.AddProductDialog;

/** @noinspection FieldCanBeLocal*/
public class MainMenu extends AppCompatActivity implements ProductAdapter.GroupClickListener {

    private ImageView ivNewFolder, ivNewItem;
    private RecyclerView rvItemList;
    private ProductAdapter groupAdapter;
    EditText etSearch;

    private final String TAG = "MainMenuActivity";
    private int currentParentGroupId = -1;
    private Stack<Integer> navigationStack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Views
        initializeViews();
        initializeBackPress();
        initializeRecyclerView();
    }

    private void initializeViews() {
        ivNewFolder = findViewById(R.id.ivNewFolder);
        ivNewFolder.setOnClickListener(view -> {
            AddGroupDialog dialog = new AddGroupDialog(this, currentParentGroupId, this::addGroupToDatabase);
            dialog.show();
        });

        ivNewItem = findViewById(R.id.ivNewItem);
        ivNewItem.setOnClickListener(view -> {
            AddProductDialog dialog = new AddProductDialog(this, currentParentGroupId, this::addProductToDatabase);
            dialog.show();
        });

        rvItemList = findViewById(R.id.rvItemList);
        navigationStack = new Stack<>();

        etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handleSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initializeRecyclerView() {
        rvItemList.setLayoutManager(new LinearLayoutManager(this));
        groupAdapter = new ProductAdapter(this, null, this);
        rvItemList.setAdapter(groupAdapter);
        loadGroups(currentParentGroupId);
    }

    private void addGroupToDatabase(TableGroup group) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Initialize Database Instance
            DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .daoGroup()
                    .insertGroup(group);

            runOnUiThread(() -> {
                Log.d(TAG, "Group Added");
                loadGroups(currentParentGroupId);
            });
        });
    }

    private void addProductToDatabase(TableProduct product) {
        // If the current parent group ID is -1, assign it to "No Group" (groupId = 1)
        product.setGroupId(currentParentGroupId == -1 ? 1 : currentParentGroupId);

        Executors.newSingleThreadExecutor().execute(() -> {
            DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .daoProduct()
                    .insertProduct(product);

            runOnUiThread(() -> {
                Log.d(TAG, "Product Added");
                loadGroups(currentParentGroupId);
            });
        });
    }

    private void loadGroups(int parentGroupId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Fetch groups
            List<TableGroup> groups = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .daoGroup()
                    .getSubGroupsByParentId(parentGroupId);

            // Fetch products for the current group
            List<TableProduct> products = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .daoProduct()
                    .getProductsByGroupId(parentGroupId);

            // Combine groups and products
            List<Object> combinedItems = new ArrayList<>();

            // Removed the manual "No Group" addition here to prevent duplicates.
            // This is because 'groups' already contains the "No Group" item when parentGroupId == -1.

            combinedItems.addAll(groups);
            combinedItems.addAll(products);

            runOnUiThread(() -> groupAdapter.updateItems(combinedItems));
        });
    }

    private void initializeBackPress() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!navigationStack.isEmpty()) {
                    // Pop the last parentGroupId and load the previous group
                    currentParentGroupId = navigationStack.pop();
                    loadGroups(currentParentGroupId);
                } else {
                    // Default back press behavior (close the activity)
                    finish();
                }
            }
        });
    }

    private void handleSearch(String query) {
        if (query.trim().isEmpty()) {
            // If the search box is empty, load your default data
            loadGroups(currentParentGroupId);
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {

            // 1) Search all groups (folders) by name
            List<TableGroup> matchingGroups = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .daoGroup()
                    .searchGroupsByName(query);

            // 2) Search all products by name (ignore groupId)
            List<TableProduct> matchingProducts = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .daoProduct()
                    .searchProductsByName(query);

            // 3) Combine them
            List<Object> combinedList = new ArrayList<>();
            combinedList.addAll(matchingGroups);
            combinedList.addAll(matchingProducts);

            // 4) Update UI on the main thread
            Log.d(TAG, "Query: " + query + ", Groups found=" + matchingGroups.size() + ", Products found=" + matchingProducts.size());
            runOnUiThread(() -> groupAdapter.updateItems(combinedList));
        });
    }

    @Override
    public void onGroupClick(TableGroup group) {
        Log.d(TAG, "Loading subgroups for: " + group.getName());

        // Push the current group ID to the stack before navigating
        navigationStack.push(currentParentGroupId);

        // Update parent group ID and load subgroups
        currentParentGroupId = group.getId();
        loadGroups(currentParentGroupId);
    }

    @Override
    public void onProductClick(TableProduct product) {
        // Handle product click (show a toast with the product ID)
        Toast.makeText(this, "Product ID: " + product.getId(), Toast.LENGTH_SHORT).show();
    }
}