package vkx64.android.inventorymanagement.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import vkx64.android.inventorymanagement.R;
import vkx64.android.inventorymanagement.database.TableGroup;

public class AddGroupDialog extends Dialog {

    private final OnGroupAddedListener listener;
    private final int parentGroupId;

    public AddGroupDialog(Context context, int parentGroupId, OnGroupAddedListener listener) {
        super(context, R.style.CustomDialogTheme);
        this.listener = listener;
        this.parentGroupId = parentGroupId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_group);

        EditText edtGroupName = findViewById(R.id.etGroupName);
        CardView cvCancel = findViewById(R.id.cvCancel);
        CardView cvConfirm = findViewById(R.id.cvConfirm);

        cvCancel.setOnClickListener(view -> {
            // Handle cancel action
            dismiss();
        });

        cvConfirm.setOnClickListener(view -> {
            String name = edtGroupName.getText().toString().trim();

            if (!name.isEmpty()) {
                // Create a new group with the passed parentGroupId
                TableGroup newGroup = new TableGroup(name, parentGroupId == -1 ? null : parentGroupId);
                listener.onGroupAdded(newGroup);
                dismiss();
            } else {
                // Show a toast if the group name is empty
                Toast.makeText(getContext(), "Group name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public interface OnGroupAddedListener {
        void onGroupAdded(TableGroup group);
    }
}
