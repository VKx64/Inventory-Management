package vkx64.android.inventorymanagement.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Stack;
import java.util.concurrent.Executors;

import vkx64.android.inventorymanagement.R;
import vkx64.android.inventorymanagement.adapters.GroupAdapter;
import vkx64.android.inventorymanagement.database.DatabaseClient;
import vkx64.android.inventorymanagement.database.TableGroup;
import vkx64.android.inventorymanagement.dialogs.AddGroupDialog;

/** @noinspection FieldCanBeLocal*/
public class MainMenu extends AppCompatActivity implements GroupAdapter.GroupClickListener {

    private ImageView ivNewFolder;
    private RecyclerView rvItemList;
    private GroupAdapter groupAdapter;

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

        rvItemList = findViewById(R.id.rvItemList);
        navigationStack = new Stack<>();
    }

    private void initializeRecyclerView() {
        rvItemList.setLayoutManager(new LinearLayoutManager(this));
        groupAdapter = new GroupAdapter(this, null, this);
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

    private void loadGroups(int parentGroupId) {
        Executors.newSingleThreadExecutor().execute(() -> {
            List<TableGroup> groups;
            if (parentGroupId == -1) {
                // Fetch root groups
                groups = DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .daoGroup()
                        .getRootGroups();
            } else {
                // Fetch subgroups for the given parentGroupId
                groups = DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .daoGroup()
                        .getSubGroupsByParentId(parentGroupId);
            }
            runOnUiThread(() -> groupAdapter.updateGroupList(groups));
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
}