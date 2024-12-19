package vkx64.android.inventorymanagement.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.Executors;

import vkx64.android.inventorymanagement.R;
import vkx64.android.inventorymanagement.adapters.GroupAdapter;
import vkx64.android.inventorymanagement.database.DatabaseClient;
import vkx64.android.inventorymanagement.database.TableGroup;
import vkx64.android.inventorymanagement.dialogs.AddGroupDialog;

public class MainMenu extends AppCompatActivity implements GroupAdapter.GroupClickListener {

    private ImageView ivNewFolder;
    private RecyclerView rvItemList;
    private GroupAdapter groupAdapter;

    private String TAG = "MainMenuActivity";

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

        initializeViews();
    }

    private void initializeViews() {
        ivNewFolder = findViewById(R.id.ivNewFolder);
        ivNewFolder.setOnClickListener(view -> openAddGroupDialog());

        rvItemList = findViewById(R.id.rvItemList);
        rvItemList.setLayoutManager(new LinearLayoutManager(this));
        groupAdapter = new GroupAdapter(this, null, this);
        rvItemList.setAdapter(groupAdapter);
        loadRootGroups();
    }

    private void openAddGroupDialog() {
        // Add the group to the database
        AddGroupDialog dialog = new AddGroupDialog(this, this::addGroupToDatabase);
        dialog.show();
    }

    private void addGroupToDatabase(TableGroup group) {
        Executors.newSingleThreadExecutor().execute(() -> {
            DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .daoGroup()
                    .insertGroup(group);

            runOnUiThread(() -> {
                Log.d(TAG, "Group Added!");
                loadRootGroups();
            });
        });
    }

    private void loadRootGroups() {
        // Use a background thread to fetch data from the database
        Executors.newSingleThreadExecutor().execute(() -> {
            List<TableGroup> rootGroups = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .daoGroup()
                    .getRootGroups();

            // Update the RecyclerView on the UI thread
            runOnUiThread(() -> groupAdapter.updateGroupList(rootGroups));
        });
    }

    public void onGroupClick(TableGroup group) {
        // Handle group click events
        Toast.makeText(this, "Clicked on group: " + group.getName(), Toast.LENGTH_SHORT).show();

        // Example: Navigate to subgroup view or display subgroup
        // loadSubGroups(group.getId());
    }
}