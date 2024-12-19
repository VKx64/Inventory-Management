package vkx64.android.inventorymanagement.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "product",
        foreignKeys = @ForeignKey(
                entity = TableGroup.class,
                parentColumns = "id",
                childColumns = "groupId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("groupId")}
)
public class TableProduct {

    @PrimaryKey(autoGenerate = false)
    @NonNull
    public String id;

    public String category;
    public String name;
    public int storage_quantity;
    public int selling_quantity;
    public String date_created;
    public String date_updated;
    public Integer groupId;


    // Constructor
    public TableProduct(String id, String category, String name, int storage_quantity, int selling_quantity, String date_created, String date_updated, Integer groupId){
        this.id = id;
        this.category = category;
        this.name = name;
        this.storage_quantity = storage_quantity;
        this.selling_quantity = selling_quantity;
        this.date_created = date_created;
        this.date_updated = date_updated;
        this.groupId = groupId;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStorage_quantity() {
        return storage_quantity;
    }

    public void setStorage_quantity(int storage_quantity) {
        this.storage_quantity = storage_quantity;
    }

    public int getSelling_quantity() {
        return selling_quantity;
    }

    public void setSelling_quantity(int selling_quantity) {
        this.selling_quantity = selling_quantity;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getDate_updated() {
        return date_updated;
    }

    public void setDate_updated(String date_updated) {
        this.date_updated = date_updated;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
}
