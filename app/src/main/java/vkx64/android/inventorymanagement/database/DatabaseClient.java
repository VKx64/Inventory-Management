package vkx64.android.inventorymanagement.database;

import android.content.Context;
import androidx.room.Room;

import vkx64.android.inventorymanagement.database.AppDatabase;

public class DatabaseClient {

    private static DatabaseClient instance; // Singleton instance
    private final AppDatabase appDatabase; // Database instance

    private DatabaseClient(Context context) {
        // Initialize the database instance
        appDatabase = Room.databaseBuilder(context, AppDatabase.class, "inventory_management_db")
                .fallbackToDestructiveMigration()
                .build();
    }

    // Get the singleton instance
    public static synchronized DatabaseClient getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseClient(context);
        }
        return instance;
    }

    // Get the Room database instance
    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
