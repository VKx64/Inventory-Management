package vkx64.android.inventorymanagement.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import java.util.List;

@Dao
public interface DaoGroup {

    // Insert a new group
    @Insert
    void insertGroup(TableGroup group);

    // Retrieve all groups
    @Query("SELECT * FROM `group`")
    List<TableGroup> getAllGroups();

    // Retrieve subgroups by parent group ID
    @Query("SELECT * FROM `group` WHERE (parentGroupId = :parentGroupId OR (:parentGroupId = -1 AND parentGroupId IS NULL))")
    List<TableGroup> getSubGroupsByParentId(int parentGroupId);

    // Update a group
    @Update
    void updateGroup(TableGroup group);

    // Delete a specific group
    @Delete
    void deleteGroup(TableGroup group);

    // Delete all subgroups under a specific group
    @Query("DELETE FROM `group` WHERE parentGroupId = :parentGroupId")
    void deleteSubGroupsByParentId(int parentGroupId);

    @Query("SELECT * FROM `group` WHERE parentGroupId IS NULL")
    List<TableGroup> getRootGroups();

    @Query("SELECT * FROM `group` WHERE id = :groupId LIMIT 1")
    TableGroup getGroupById(int groupId);




}
