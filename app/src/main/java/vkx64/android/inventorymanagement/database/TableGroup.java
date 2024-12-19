package vkx64.android.inventorymanagement.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "group")
public class TableGroup {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;

    public Integer parentGroupId; // Nullable, for subgroups

    // Constructor
    public TableGroup(String name, Integer parentGroupId) {
        this.name = name;
        this.parentGroupId = parentGroupId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getParentGroupId() {
        return parentGroupId;
    }

    public void setParentGroupId(Integer parentGroupId) {
        this.parentGroupId = parentGroupId;
    }
}
