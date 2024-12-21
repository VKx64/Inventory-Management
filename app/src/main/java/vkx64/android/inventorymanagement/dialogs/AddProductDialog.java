package vkx64.android.inventorymanagement.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import vkx64.android.inventorymanagement.R;
import vkx64.android.inventorymanagement.database.TableProduct;

public class AddProductDialog extends Dialog {

    private final OnProductAddedListener listener;
    private final int groupId;

    public AddProductDialog(Context context, int groupId, OnProductAddedListener listener) {
        super(context);
        this.groupId = groupId;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_product);

        // Find views
        EditText edtProductId = findViewById(R.id.edtProductId);
        EditText edtProductName = findViewById(R.id.edtProductName);
        EditText edtCategory = findViewById(R.id.edtCategory);
        EditText edtStorageQty = findViewById(R.id.edtStorageQty);
        EditText edtSellingQty = findViewById(R.id.edtSellingQty);

        CardView cvCancel = findViewById(R.id.cvCancel);
        CardView cvConfirm = findViewById(R.id.cvConfirm);

        // Cancel button action
        cvCancel.setOnClickListener(view -> dismiss());

        // Confirm button action
        cvConfirm.setOnClickListener(view -> {
            String productId = edtProductId.getText().toString().trim();
            String productName = edtProductName.getText().toString().trim();
            String category = edtCategory.getText().toString().trim();
            String storageQtyStr = edtStorageQty.getText().toString().trim();
            String sellingQtyStr = edtSellingQty.getText().toString().trim();

            // Validation for empty fields
            if (productId.isEmpty() || productName.isEmpty() || storageQtyStr.isEmpty() || sellingQtyStr.isEmpty()) {
                Toast.makeText(getContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                // Parse numeric inputs
                int storageQty = Integer.parseInt(storageQtyStr);
                int sellingQty = Integer.parseInt(sellingQtyStr);

                // Create new product object
                TableProduct product = new TableProduct(
                        productId,
                        category,
                        productName,
                        storageQty,
                        sellingQty,
                        String.valueOf(System.currentTimeMillis()), // Current timestamp as date_created
                        String.valueOf(System.currentTimeMillis()), // Current timestamp as date_updated
                        groupId == -1 ? 1 : groupId // Assign to "No Group" if groupId is -1
                );

                // Notify listener
                listener.onProductAdded(product);
                dismiss();
            } catch (NumberFormatException e) {
                // Handle invalid numeric input
                Toast.makeText(getContext(), "Enter valid numbers for quantities", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface OnProductAddedListener {
        void onProductAdded(TableProduct product);
    }
}
