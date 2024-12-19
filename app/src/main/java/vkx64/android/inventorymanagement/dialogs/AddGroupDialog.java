package vkx64.android.inventorymanagement.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import vkx64.android.inventorymanagement.R;
import vkx64.android.inventorymanagement.database.TableGroup;

public class AddGroupDialog extends Dialog{

    private OnGroupAddedListener listener;
    private int parentGroupId;

    public AddGroupDialog(Context context, int parentGroupId, OnGroupAddedListener listener) {
        super(context);
        this.listener = listener;
        this.parentGroupId = parentGroupId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_group);

        EditText edtGroupName = findViewById(R.id.edtGroupName);
        Button btnAddGroup = findViewById(R.id.btnAddGroup);

        btnAddGroup.setOnClickListener(view -> {
            String name = edtGroupName.getText().toString().trim();

            if (!name.isEmpty()) {
                // Create a new group with the passed parentGroupId
                TableGroup newGroup = new TableGroup(name, parentGroupId == -1 ? null : parentGroupId);
                listener.onGroupAdded(newGroup);
                dismiss();
            }
        });
    }

    public interface OnGroupAddedListener {
        void onGroupAdded(TableGroup group);
    }
}
