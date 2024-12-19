package vkx64.android.inventorymanagement.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface DaoProduct {

    // Insert a single product
    @Insert
    void insertProduct(TableProduct product);

    // Insert multiple products
    @Insert
    void insertAllProducts(List<TableProduct> products);

    // Retrieve all products
    @Query("SELECT * FROM product")
    List<TableProduct> getAllProducts();

    // Retrieve a product by ID
    @Query("SELECT * FROM product WHERE id = :productId")
    TableProduct getProductById(String productId);

    // Retrieve products by category
    @Query("SELECT * FROM product WHERE category = :categoryName")
    List<TableProduct> getProductsByCategory(String categoryName);

    // Update a product
    @Update
    void updateProduct(TableProduct product);

    // Delete a specific product
    @Delete
    void deleteProduct(TableProduct product);

    // Delete all products in the table
    @Query("DELETE FROM product")
    void deleteAllProducts();
}
