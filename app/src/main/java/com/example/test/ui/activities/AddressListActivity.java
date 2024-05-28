package com.example.test.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.firestoreclass.FirestoreClassKT;
import com.example.test.models.Address;
import com.example.test.ui.adapters.AddressListAdapter;
import com.example.test.utils.Constants;
import com.example.test.utils.SwipeToDeleteCallback;
import com.example.test.utils.SwipeToEditCallback;

import java.util.ArrayList;

public class AddressListActivity extends BaseActivity {

    private Toolbar toolbar_address_list_activity;
    private TextView tv_add_address;
    private RecyclerView rv_address_list;
    private TextView tv_no_address_found;
    private TextView tv_title;
    private boolean mSelectAddress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_address_list);

        toolbar_address_list_activity = findViewById(R.id.toolbar_address_list_activity);
        setupActionBar();
        tv_add_address = findViewById(R.id.tv_add_address);
        rv_address_list = findViewById(R.id.rv_address_list);
        tv_no_address_found = findViewById(R.id.tv_no_address_found);
        tv_title = findViewById(R.id.tv_title);

        getAddressList();
        if (getIntent().hasExtra(Constants.EXTRA_SELECT_ADDRESS)) {
            mSelectAddress = getIntent().getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS, false);
        }

        if (mSelectAddress) {
            tv_title.setText(getResources().getString(R.string.title_select_address));
        }

        tv_add_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddressListActivity.this, AddEditAddressActivity.class);
                startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE);
            }
        });
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar_address_list_activity);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp);
        }
        toolbar_address_list_activity.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.ADD_ADDRESS_REQUEST_CODE) {
                getAddressList();
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Request Cancelled", "To add the address.");
        }
    }

    private void getAddressList() {
        showProgressDialog(getResources().getString(R.string.please_wait));
        new FirestoreClassKT().getAddressesList(AddressListActivity.this);
    }

    public void successAddressListFromFirestore(ArrayList<Address> addressList) {
        hideProgressDialog();
        if (addressList.size() > 0) {
            rv_address_list.setVisibility(View.VISIBLE);
            tv_no_address_found.setVisibility(View.GONE);
            rv_address_list.setLayoutManager(new LinearLayoutManager(AddressListActivity.this));
            rv_address_list.setHasFixedSize(true);
            AddressListAdapter addressAdapter = new AddressListAdapter(AddressListActivity.this, addressList, mSelectAddress);
            rv_address_list.setAdapter(addressAdapter);
            if (!mSelectAddress) {
                SwipeToEditCallback editSwipeHandler = new SwipeToEditCallback(AddressListActivity.this) {
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        AddressListAdapter adapter = (AddressListAdapter) rv_address_list.getAdapter();
                        adapter.notifyEditItem(AddressListActivity.this, viewHolder.getAdapterPosition());
                    }
                };
                ItemTouchHelper editItemTouchHelper = new ItemTouchHelper(editSwipeHandler);
                editItemTouchHelper.attachToRecyclerView(rv_address_list);
                SwipeToDeleteCallback deleteSwipeHandler = new SwipeToDeleteCallback(AddressListActivity.this) {
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        showProgressDialog(getResources().getString(R.string.please_wait));
                        new FirestoreClassKT().deleteAddress(AddressListActivity.this, addressList.get(viewHolder.getAdapterPosition()).getId());
                    }
                };
                ItemTouchHelper deleteItemTouchHelper = new ItemTouchHelper(deleteSwipeHandler);
                deleteItemTouchHelper.attachToRecyclerView(rv_address_list);
            }
        } else {
            rv_address_list.setVisibility(View.GONE);
            tv_no_address_found.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void deleteAddressSuccess() {
        hideProgressDialog();
        Toast.makeText(AddressListActivity.this, getResources().getString(R.string.err_your_address_deleted_successfully), Toast.LENGTH_SHORT).show();
        getAddressList();
    }
}
