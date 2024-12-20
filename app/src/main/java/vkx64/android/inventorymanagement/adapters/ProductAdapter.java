package vkx64.android.inventorymanagement.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vkx64.android.inventorymanagement.R;
import vkx64.android.inventorymanagement.database.TableGroup;
import vkx64.android.inventorymanagement.database.TableProduct;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_GROUP = 1;
    private static final int VIEW_TYPE_PRODUCT = 2;

    private final Context context;
    private List<Object> items;
    private final GroupClickListener groupClickListener;

    public ProductAdapter(Context context, List<Object> items, GroupClickListener listener) {
        this.context = context;
        this.items = items != null ? items : new ArrayList<>();
        this.groupClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof TableGroup) {
            return VIEW_TYPE_GROUP;
        } else if (items.get(position) instanceof TableProduct) {
            return VIEW_TYPE_PRODUCT;
        }
        throw new IllegalStateException("Unknown item type at position " + position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_GROUP) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_groups, parent, false);
            return new GroupViewHolder(view);
        } else if (viewType == VIEW_TYPE_PRODUCT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_products, parent, false);
            return new ProductViewHolder(view);
        }
        throw new IllegalStateException("Unexpected view type: " + viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GroupViewHolder) {
            TableGroup group = (TableGroup) items.get(position);
            GroupViewHolder groupHolder = (GroupViewHolder) holder;

            groupHolder.tvFolderName.setText(group.getName());
            groupHolder.ivFolderIcon.setImageResource(R.drawable.ic_folder);

            // Handle group clicks
            holder.itemView.setOnClickListener(v -> {
                if (groupClickListener != null) {
                    groupClickListener.onGroupClick(group);
                }
            });

        } else if (holder instanceof ProductViewHolder) {
            TableProduct product = (TableProduct) items.get(position);
            ProductViewHolder productHolder = (ProductViewHolder) holder;

            String SellingCount = "Selling: " + product.getSelling_quantity() + "/" + product.getStorage_quantity();
            String QuantityCount = "x" + product.getStorage_quantity();

            productHolder.tvProductName.setText(product.getName());
            productHolder.tvSellingCount.setText(SellingCount);
            productHolder.tvQuantity.setText(QuantityCount);

            // Handle product clicks
            holder.itemView.setOnClickListener(v -> {
                if (groupClickListener != null) {
                    groupClickListener.onProductClick(product);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateItems(List<Object> newItems) {
        this.items = newItems != null ? newItems : new ArrayList<>();
        notifyDataSetChanged();
    }

    // ViewHolder for Groups
    static class GroupViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFolderIcon;
        TextView tvFolderName;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFolderIcon = itemView.findViewById(R.id.ivFolderIcon);
            tvFolderName = itemView.findViewById(R.id.tvFolderName);
        }
    }

    // ViewHolder for Products
    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName, tvSellingCount, tvQuantity;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvSellingCount = itemView.findViewById(R.id.tvSellingCount);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
        }
    }

    // Listener interface
    public interface GroupClickListener {
        void onGroupClick(TableGroup group);
        void onProductClick(TableProduct product);
    }
}
