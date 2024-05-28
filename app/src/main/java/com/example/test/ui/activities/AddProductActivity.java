package com.example.test.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.test.R;
import com.example.test.firestoreclass.FirestoreClassKT;
import com.example.test.models.Product;
import com.example.test.models.SizeProduct;
import com.example.test.utils.Constants;
import com.example.test.utils.GlideLoader;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;

public class AddProductActivity extends BaseActivity implements View.OnClickListener {

    private Uri mSelectedImageFileUri;
    private String mProductImageURL = "";
    private Toolbar toolbar_add_product_activity;
    private ImageView iv_add_update_product, iv_product_image;
    private EditText et_product_title, et_product_price, et_product_description, et_quantity;
    private Spinner spinnerSize;
    private Button btn_add_size, btn_submit_add_product;
    private LinearLayout sizes_container;
    private String selectedSize;
    private List<Pair<String, Integer>> sizeQuantityList = new ArrayList<>();
    private FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        toolbar_add_product_activity = findViewById(R.id.toolbar_add_product_activity);
        iv_add_update_product = findViewById(R.id.iv_add_update_product);
        iv_product_image = findViewById(R.id.iv_product_image);
        et_product_title = findViewById(R.id.et_product_title);
        et_product_price = findViewById(R.id.et_product_price);
        et_product_description = findViewById(R.id.et_product_description);
        spinnerSize = findViewById(R.id.spinner_size);
        et_quantity = findViewById(R.id.et_quantity);
        btn_add_size = findViewById(R.id.btn_add_size);
        sizes_container = findViewById(R.id.sizes_container);
        btn_submit_add_product = findViewById(R.id.btn_submit_add_product);

        setupActionBar();
        iv_add_update_product.setOnClickListener(this);
        btn_add_size.setOnClickListener(this);
        btn_submit_add_product.setOnClickListener(this);

        setupSpinner();
        spinnerSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSize = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar_add_product_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar_add_product_activity.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            int id = v.getId();
            if (id == R.id.iv_add_update_product) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                    Constants.showImageChooser(AddProductActivity.this);
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.READ_STORAGE_PERMISSION_CODE);
                }
            } else if (id == R.id.btn_add_size) {
                addSizeItem();
            } else if (id == R.id.btn_submit_add_product) {
                if (validateProductDetails()) {
                    uploadProductImage();
                }
            }
        }
    }


    private void addSizeItem() {
        String size = selectedSize;
        String quantity = et_quantity.getText().toString().trim();
        if (!TextUtils.isEmpty(size) && !TextUtils.isEmpty(quantity)) {
            addSizeView(size, Integer.parseInt(quantity));
            et_quantity.getText().clear();
        } else {
            Toast.makeText(this, "Please select a size and enter quantity", Toast.LENGTH_SHORT).show();
        }
    }

    private void addSizeView(String size, int quantity) {
        View sizeView = getLayoutInflater().inflate(R.layout.item_size, null);
        ((TextView) sizeView.findViewById(R.id.tv_size)).setText(size);
        ((TextView) sizeView.findViewById(R.id.tv_quantity)).setText(String.valueOf(quantity));
        sizes_container.addView(sizeView);
        sizeQuantityList.add(new Pair<>(size, quantity));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(AddProductActivity.this);
            } else {
                Toast.makeText(this, getResources().getString(R.string.read_storage_permission_denied), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    iv_add_update_product.setImageResource(R.drawable.ic_vector_edit);
                    mSelectedImageFileUri = data.getData();
                    new GlideLoader(AddProductActivity.this).loadUserPicture(mSelectedImageFileUri, iv_product_image);
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Request Cancelled", "Image selection cancelled");
        }
    }

    private void uploadProductImage() {
        showProgressDialog(getResources().getString(R.string.please_wait));
        new FirestoreClassKT().uploadImageToCloudStorage(AddProductActivity.this, mSelectedImageFileUri, Constants.PRODUCT_IMAGE);
    }

    private boolean validateProductDetails() {
        if (mSelectedImageFileUri == null) {
            showErrorSnackBar(getResources().getString(R.string.err_msg_select_product_image), true);
            return false;
        } else if (TextUtils.isEmpty(et_product_title.getText().toString().trim())) {
            showErrorSnackBar(getResources().getString(R.string.err_msg_enter_product_title), true);
            return false;
        } else if (TextUtils.isEmpty(et_product_price.getText().toString().trim())) {
            showErrorSnackBar(getResources().getString(R.string.err_msg_enter_product_price), true);
            return false;
        } else if (TextUtils.isEmpty(et_product_description.getText().toString().trim())) {
            showErrorSnackBar(getResources().getString(R.string.err_msg_enter_product_description), true);
            return false;
        } else if (sizes_container.getChildCount() == 0) {
            showErrorSnackBar(getResources().getString(R.string.err_msg_enter_product_sizes), true);
            return false;
        }
        return true;
    }

    public void imageUploadSuccess(String imageURL) {
        mProductImageURL = imageURL;
        uploadProductDetails();
    }

    private void uploadProductDetails() {
        Product product = new Product(
            "",
            et_product_title.getText().toString().trim(),
            et_product_price.getText().toString().trim(),
            et_product_description.getText().toString().trim(),
            mProductImageURL
        );

        final String productId = mFireStore.collection(Constants.PRODUCTS).document().getId();
        product.setProduct_id(productId);

        mFireStore.collection(Constants.PRODUCTS).document(productId).set(product, SetOptions.merge())
            .addOnSuccessListener(aVoid -> {
        uploadSizeProductDetails(productId);
        productUploadSuccess();
    })
        .addOnFailureListener(e -> {
        hideProgressDialog();
        Log.e(getClass().getSimpleName(), "Error while uploading the product details.", e);
    });
    }

    private void uploadSizeProductDetails(String productId) {
        for (Pair<String, Integer> pair : sizeQuantityList) {
        String size = pair.first;
        Integer quantity = pair.second;

        Integer sizeInt = Integer.valueOf(size);
        if (sizeInt != null) {
            SizeProduct sizeProduct = new SizeProduct("", sizeInt, quantity, productId);
            mFireStore.collection(Constants.SIZE_PRODUCTS).document().set(sizeProduct)
                .addOnFailureListener(e -> {
                hideProgressDialog();
                Log.e(getClass().getSimpleName(), "Error while uploading size product details.", e);
            });
        } else {
            Log.e(getClass().getSimpleName(), "Invalid size: " + size);
        }
    }
    }

    private void setupSpinner() {
        List<String> sizes = new ArrayList<>();
        for (int i = 36; i <= 43; i++) {
        sizes.add(String.valueOf(i));
    }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, sizes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSize.setAdapter(adapter);
    }

    public void productUploadSuccess() {
        hideProgressDialog();
        Toast.makeText(AddProductActivity.this, getResources().getString(R.string.product_uploaded_success_message), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AddProductActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
}
