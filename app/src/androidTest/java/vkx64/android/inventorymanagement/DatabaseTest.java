package vkx64.android.inventorymanagement;

import android.content.Context;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.List;

import vkx64.android.inventorymanagement.database.*;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    private AppDatabase db;
    private DaoGroup daoGroup;
    private DaoProduct daoProduct;

    @Before
    public void setUp() {
        // Create an in-memory database (doesn't persist data)
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries() // For testing purposes only
                .build();

        // Get DAOs
        daoGroup = db.daoGroup();
        daoProduct = db.daoProduct();
    }

    @After
    public void tearDown() throws IOException {
        db.close(); // Close the database after tests
    }

    @Test
    public void testInsertAndRetrieveGroup() {
        // Insert a group
        TableGroup group = new TableGroup("Electronics", null);
        daoGroup.insertGroup(group);

        // Retrieve groups
        List<TableGroup> groups = daoGroup.getAllGroups();
        assertEquals(1, groups.size());
        assertEquals("Electronics", groups.get(0).getName());
    }

    @Test
    public void testInsertAndRetrieveProduct() {
        // Insert a group
        TableGroup group = new TableGroup("Electronics", null);
        daoGroup.insertGroup(group);
        int groupId = daoGroup.getAllGroups().get(0).getId();

        // Insert a product
        TableProduct product = new TableProduct(
                "P1",
                "Mobile",
                "Smartphone",
                100,
                10,
                "2024-12-19",
                "2024-12-19",
                groupId
        );
        daoProduct.insertProduct(product);

        // Retrieve products
        List<TableProduct> products = daoProduct.getAllProducts();
        assertEquals(1, products.size());
        assertEquals("Smartphone", products.get(0).getName());
    }
}
