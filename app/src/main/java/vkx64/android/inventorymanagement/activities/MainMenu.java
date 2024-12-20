package vkx64.android.inventorymanagement.activities;

import android.os.Bundle;
import android.util.Log;
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
            List<TableGroup> groups = parentGroupId == -1
                    ? DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .daoGroup()
                    .getRootGroups()
                    : DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .daoGroup()
                    .getSubGroupsByParentId(parentGroupId);

            List<TableProduct> products = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .daoProduct()
                    .getProductsByGroupId(parentGroupId);

            // Combine groups and products
            List<Object> combinedItems = new ArrayList<>();
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