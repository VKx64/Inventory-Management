package vkx64.android.inventorymanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.concurrent.Executors;

import vkx64.android.inventorymanagement.activities.MainMenu;
import vkx64.android.inventorymanagement.database.DatabaseClient;
import vkx64.android.inventorymanagement.database.TableGroup;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = new Intent(MainActivity.this, MainMenu.class);
        startActivity(intent);

        initializeDefaultGroup();
    }

    private void initializeDefaultGroup() {
        Executors.newSingleThreadExecutor().execute(() -> {
            // Check if the default group already exists
            TableGroup defaultGroup = DatabaseClient.getInstance(getApplicationContext())
                    .getAppDatabase()
                    .daoGroup()
                    .getGroupById(1);

            // If "No Group" doesn't exist, create it
            if (defaultGroup == null) {
                TableGroup noGroup = new TableGroup("No Group", null);
                noGroup.setId(1); // Manually set the ID to 1
                DatabaseClient.getInstance(getApplicationContext())
                        .getAppDatabase()
                        .daoGroup()
                        .insertGroup(noGroup);
            }
        });
    }
}