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

import java.util.concurrent.Executors;

import vkx64.android.inventorymanagement.R;
import vkx64.android.inventorymanagement.database.DatabaseClient;
import vkx64.android.inventorymanagement.database.TableGroup;
import vkx64.android.inventorymanagement.dialogs.AddGroupDialog;

public class MainMenu extends AppCompatActivity {

    private ImageView ivNewFolder;

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
//                refreshRecyclerView();
            });
        });
    }
}