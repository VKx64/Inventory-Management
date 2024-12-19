package vkx64.android.inventorymanagement.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {TableProduct.class, TableGroup.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    // Abstract methods to access DAOs
    public abstract DaoProduct daoProduct();
    public abstract DaoGroup daoGroup();
}
