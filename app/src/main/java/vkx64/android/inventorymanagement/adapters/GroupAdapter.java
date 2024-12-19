package vkx64.android.inventorymanagement.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import vkx64.android.inventorymanagement.R;
import vkx64.android.inventorymanagement.database.TableGroup;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder>{

    private Context context;
    private List<TableGroup> groupList;
    private GroupClickListener listener;

    public GroupAdapter(Context context, List<TableGroup> groupList, GroupClickListener listener) {
        this.context = context;
        this.groupList = groupList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the existing group layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_groups, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        TableGroup group = groupList.get(position);

        // Bind data to views
        holder.tvFolderName.setText(group.getName());
        holder.ivFolderIcon.setImageResource(R.drawable.ic_folder); // Optional: Set icon dynamically if needed

        // Set click listener for the item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGroupClick(group);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupList == null ? 0 : groupList.size();
    }

    // Method to update the group list dynamically
    public void updateGroupList(List<TableGroup> newGroupList) {
        this.groupList = newGroupList;
        notifyDataSetChanged();
    }

    // ViewHolder class
    static class GroupViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFolderIcon;
        TextView tvFolderName;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFolderIcon = itemView.findViewById(R.id.ivFolderIcon);
            tvFolderName = itemView.findViewById(R.id.tvFolderName);
        }
    }

    // Interface for click listener
    public interface GroupClickListener {
        void onGroupClick(TableGroup group);
    }

}
