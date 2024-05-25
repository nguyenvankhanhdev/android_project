package com.example.test.ui.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.test.R;
import com.example.test.firestoreclass.FirestoreClass;
import com.example.test.models.Address;
import com.example.test.utils.Constants;
import com.google.android.material.textfield.TextInputLayout;

public class AddEditAddressActivity extends BaseActivity {

    private Address mAddressDetails = null;
    private Toolbar toolbar_add_edit_address_activity;
    private EditText et_full_name;
    private EditText et_phone_number;
    private EditText et_address;
    private EditText et_zip_code;
    private RadioButton rb_other;
    private TextView tv_title;
    private EditText et_additional_note;
    private EditText et_other_details;
    private RadioButton rb_home;
    private RadioButton rb_office;
    private RadioGroup rg_type;
    private TextInputLayout til_other_details;
    private Button btn_submit_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        enableEdgeToEdge();
        setContentView(R.layout.activity_add_edit_address);
        toolbar_add_edit_address_activity = findViewById(R.id.toolbar_add_edit_address_activity);
        et_full_name = findViewById(R.id.et_full_name);
        et_phone_number = findViewById(R.id.et_phone_number);
        et_address = findViewById(R.id.et_address);
        et_zip_code = findViewById(R.id.et_zip_code);
        rb_other = findViewById(R.id.rb_other);
        tv_title = findViewById(R.id.tv_title);
        rg_type = findViewById(R.id.rg_type);
        til_other_details = findViewById(R.id.til_other_details);
        btn_submit_address = findViewById(R.id.btn_submit_address);
        et_additional_note = findViewById(R.id.et_additional_note);
        et_other_details = findViewById(R.id.et_other_details);
        rb_home = findViewById(R.id.rb_home);
        rb_office = findViewById(R.id.rb_office);

        if (getIntent().hasExtra(Constants.EXTRA_ADDRESS_DETAILS)) {
            mAddressDetails = getIntent().getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS);
        }

        if (mAddressDetails != null) {
            if (!TextUtils.isEmpty(mAddressDetails.getId())) {
                tv_title.setText(getResources().getString(R.string.title_edit_address));
                btn_submit_address.setText(getResources().getString(R.string.btn_lbl_update));

                et_full_name.setText(mAddressDetails.getName());
                et_phone_number.setText(mAddressDetails.getMobileNumber());
                et_address.setText(mAddressDetails.getAddress());
                et_zip_code.setText(mAddressDetails.getZipCode());
                et_additional_note.setText(mAddressDetails.getAdditionalNote());

                switch (mAddressDetails.getType()) {
                    case Constants.HOME:
                    rb_home.setChecked(true);
                    break;
                    case Constants.OFFICE:
                    rb_office.setChecked(true);
                    break;
                    default:
                    rb_other.setChecked(true);
                    til_other_details.setVisibility(View.VISIBLE);
                    et_other_details.setText(mAddressDetails.getOtherDetails());
                    break;
                }
            }
        }

        setupActionBar();
        btn_submit_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAddressToFirestore();
            }
        });

        rg_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_other) {
                    til_other_details.setVisibility(View.VISIBLE);
                } else {
                    til_other_details.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar_add_edit_address_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp);

        toolbar_add_edit_address_activity.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private boolean validateData() {
        if (TextUtils.isEmpty(et_full_name.getText().toString().trim())) {
            showErrorSnackBar(getResources().getString(R.string.err_msg_please_enter_full_name), true);
            return false;
        } else if (TextUtils.isEmpty(et_phone_number.getText().toString().trim())) {
            showErrorSnackBar(getResources().getString(R.string.err_msg_please_enter_phone_number), true);
            return false;
        } else if (TextUtils.isEmpty(et_address.getText().toString().trim())) {
            showErrorSnackBar(getResources().getString(R.string.err_msg_please_enter_address), true);
            return false;
        } else if (TextUtils.isEmpty(et_zip_code.getText().toString().trim())) {
            showErrorSnackBar(getResources().getString(R.string.err_msg_please_enter_zip_code), true);
            return false;
        } else if (rb_other.isChecked() && TextUtils.isEmpty(et_zip_code.getText().toString().trim())) {
            showErrorSnackBar(getResources().getString(R.string.err_msg_please_enter_zip_code), true);
            return false;
        } else {
            return true;
        }
    }


    private void saveAddressToFirestore() {
        String fullName = et_full_name.getText().toString().trim();
        String phoneNumber = et_phone_number.getText().toString().trim();
        String address = et_address.getText().toString().trim();
        String zipCode = et_zip_code.getText().toString().trim();
        String additionalNote = et_additional_note.getText().toString().trim();
        String otherDetails = et_other_details.getText().toString().trim();

        if (validateData()) {
            showProgressDialog(getResources().getString(R.string.please_wait));

            String addressType;
            if (rb_home.isChecked()) {
                addressType = Constants.HOME;
            } else if (rb_office.isChecked()) {
                addressType = Constants.OFFICE;
            } else {
                addressType = Constants.OTHER;
            }

            Address addressModel = new Address(
                FirestoreClass.getCurrentUserID(),
                fullName,
                phoneNumber,
                address,
                zipCode,
                additionalNote,
                addressType,
                otherDetails
            );

            if (mAddressDetails != null && !TextUtils.isEmpty(mAddressDetails.getId())) {
                FirestoreClass.updateAddress(AddEditAddressActivity.this, addressModel, mAddressDetails.getId());
            } else {
                FirestoreClass.addAddress(AddEditAddressActivity.this, addressModel);
            }
        }
    }

    public void addUpdateAddressSuccess() {
        hideProgressDialog();
        String notifySuccessMessage = mAddressDetails != null && !TextUtils.isEmpty(mAddressDetails.getId()) ?
        getResources().getString(R.string.msg_your_address_update_successfully) :
        getResources().getString(R.string.err_your_address_added_successfully);

        Toast.makeText(this, notifySuccessMessage, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }
}

