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
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditProductActivity extends BaseActivity implements View.OnClickListener {
    private Toolbar toolbar_add_product_activity;
    private EditText etProductTitle;
    private EditText etProductPrice;
    private ImageView iv_edit_update_product;
    private EditText etProductDescription;
    private Spinner spinnerSize;
    private EditText etQuantity;
    private Button btnAddSize;
    private LinearLayout sizesContainer;
    private Button btnSubmit;
    private Uri mSelectedImageFileUri;
    private String mProductImageURL = "";
    private ImageView iv_edit_product_image;
    private Product product;
    private FirebaseFirestore db;
    private CollectionReference productRef;
    private CollectionReference sizeProductRef;
    private List<Pair<String, Integer>> sizeQuantityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        enableEdgeToEdge();
        setContentView(R.layout.activity_edit_product);

        toolbar_add_product_activity = findViewById(R.id.toolbar_add_product_activity);
        etProductTitle = findViewById(R.id.et_edit_product_title);
        etProductPrice = findViewById(R.id.et_edit_product_price);
        etProductDescription = findViewById(R.id.et_edit_product_description);
        spinnerSize = findViewById(R.id.spinner_edit_size);
        etQuantity = findViewById(R.id.et_edit_quantity);
        btnAddSize = findViewById(R.id.btn_edit_add_size);
        sizesContainer = findViewById(R.id.sizes_edit_container);
        btnSubmit = findViewById(R.id.btn_submit_edit_product);
        iv_edit_update_product = findViewById(R.id.iv_edit_update_product);
        iv_edit_product_image = findViewById(R.id.iv_edit_product_image);
        setupActionBar();
        setupSpinner();

        db = FirebaseFirestore.getInstance();
        productRef = db.collection("products");
        sizeProductRef = db.collection("size_product");

        if (getIntent().hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            String productId = getIntent().getStringExtra(Constants.EXTRA_PRODUCT_ID);
            loadProductDetails(productId);
        }
        iv_edit_update_product.setOnClickListener(this);
        btnAddSize.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    iv_edit_update_product.setImageDrawable(
                            ContextCompat.getDrawable(
                                    this,
                                    R.drawable.ic_vector_edit
                            )
                    );
                    mSelectedImageFileUri = data.getData();
                    new GlideLoader(this).loadUserPicture(
                            mSelectedImageFileUri,
                            iv_edit_product_image
                    );
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Request Cancelled", "Image selection cancelled");
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_edit_update_product) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this);
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        Constants.READ_STORAGE_PERMISSION_CODE
                );
            }
        } else if (id == R.id.btn_edit_add_size) {
            addSizeItem();
        } else if (id == R.id.btn_submit_edit_product) {
            if (validateProductDetails()) {
                showProgressDialog(getResources().getString(R.string.please_wait));
                if (mSelectedImageFileUri != null) {
                    uploadProductImage();
                } else {
                    updateProduct();
                }
            }
        }
    }



    private void addSizeItem() {
        String selectedSize = Objects.requireNonNull(spinnerSize.getSelectedItem()).toString();
        String quantity = etQuantity.getText().toString().trim();
        if (!selectedSize.isEmpty() && !quantity.isEmpty()) {
            addSizeView(selectedSize, Integer.parseInt(quantity));
            etQuantity.getText().clear();
        } else {
            Toast.makeText(this, "Please select a size and enter quantity", Toast.LENGTH_SHORT).show();
        }
    }

    private void addSizeView(String size, int quantity) {
        View sizeView = getLayoutInflater().inflate(R.layout.item_size, null);
        ((TextView) sizeView.findViewById(R.id.tv_size)).setText(size);
        ((TextView) sizeView.findViewById(R.id.tv_quantity)).setText(String.valueOf(quantity));
        sizesContainer.addView(sizeView);
        sizeQuantityList.add(new Pair<>(size, quantity));
    }

    private void updateProduct() {
        String title = etProductTitle.getText().toString().trim();
        String price = etProductPrice.getText().toString().trim();
        String description = etProductDescription.getText().toString().trim();

        if (!title.isEmpty() && !price.isEmpty() && !description.isEmpty()) {
            Product updatedProduct = new Product(
                    product.getProduct_id(),
                    title,
                    price,
                    description,
                    product.getImage()
            );
            productRef.document(product.getProduct_id())
                    .set(updatedProduct, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> updateSizes())
                    .addOnFailureListener(e -> {
                        hideProgressDialog();
                        showErrorSnackBar(e.getMessage(), true);
                    });
        } else {
            hideProgressDialog();
            showErrorSnackBar("Please enter all the details.", true);
        }
    }

    private void updateSizes() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (Pair<String, Integer> pair : sizeQuantityList) {
                        Task<SizeProduct> task = checkSizeExistence(pair.first);
                        SizeProduct existingSize = Tasks.await(task); // Wait for the task to complete

                        if (existingSize != null) {
                            updateSize(existingSize, pair.second);
                        } else {
                            addNewSize(pair.first, pair.second);
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            hideProgressDialog();
                            Intent intent = new Intent(EditProductActivity.this, DashboardActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }




    private Task<SizeProduct> checkSizeExistence(String size) {
        return sizeProductRef
                .whereEqualTo("product_id", product.getProduct_id())
                .whereEqualTo("size_id", size)
                .get()
                .continueWith(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                            return documentSnapshot.toObject(SizeProduct.class);
                        }
                    }
                    return null;
                });
    }



    private void updateSize(SizeProduct sizeProduct, int quantity) {
        int newQuantity = sizeProduct.getQuantity() + quantity;
        sizeProductRef.document(sizeProduct.getSize_id()).update("quantity", newQuantity)
                .addOnSuccessListener(aVoid -> Log.i("Update Size", "Successfully updated size: " + sizeProduct.getSize_id()))
                .addOnFailureListener(e -> Log.e("Update Size", "Failed to update size: " + sizeProduct.getSize_id(), e));
    }

    private void addNewSize(String size, int quantity) {
        SizeProduct newSizeProduct = new SizeProduct(
                "",
                Integer.parseInt(size),
                quantity,
                product.getProduct_id()
        );
        sizeProductRef.add(newSizeProduct)
                .addOnSuccessListener(documentReference -> Log.i("Add Size", "Successfully added size: " + size))
                .addOnFailureListener(e -> Log.e("Add Size", "Failed to add size: " + size, e));
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar_add_product_activity);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp);
        toolbar_add_product_activity.setNavigationOnClickListener(v -> onBackPressed());
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

    private void loadProductDetails(String productId) {
        productRef.document(productId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        product = documentSnapshot.toObject(Product.class);
                        new GlideLoader(EditProductActivity.this).loadProductPicture(
                                product.getImage(),
                                iv_edit_product_image
                        );
                        etProductTitle.setText(product.getTitle());
                        etProductPrice.setText(product.getPrice());
                        etProductDescription.setText(product.getDescription());
                    }
                })
                .addOnFailureListener(e -> showErrorSnackBar(e.getMessage(), true));
    }

    private boolean validateProductDetails() {
        if (mSelectedImageFileUri == null && product.getImage().isEmpty()) {
            showErrorSnackBar(getResources().getString(R.string.err_msg_select_product_image), true);
            return false;
        } else if (TextUtils.isEmpty(etProductTitle.getText().toString().trim())) {
            showErrorSnackBar(getResources().getString(R.string.err_msg_enter_product_title), true);
            return false;
        } else if (TextUtils.isEmpty(etProductPrice.getText().toString().trim())) {
            showErrorSnackBar(getResources().getString(R.string.err_msg_enter_product_price), true);
            return false;
        } else if (TextUtils.isEmpty(etProductDescription.getText().toString().trim())) {
            showErrorSnackBar(getResources().getString(R.string.err_msg_enter_product_description), true);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(EditProductActivity.this);
            } else {
                Toast.makeText(
                        this,
                        getResources().getString(R.string.read_storage_permission_denied),
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }

    private void uploadProductDetails() {
        Product product = new Product(
                this.product.getProduct_id(),
                etProductTitle.getText().toString().trim(),
                etProductPrice.getText().toString().trim(),
                etProductDescription.getText().toString().trim(),
                mProductImageURL
        );
        productRef.document(product.getProduct_id())
                .set(product, SetOptions.merge())
                .addOnSuccessListener(aVoid -> updateSizes())
                .addOnFailureListener(e -> {
                    hideProgressDialog();
                    showErrorSnackBar(e.getMessage(), true);
                });
    }


    public void imageUploadSuccess(String imageURL) {
        mProductImageURL = imageURL;
        uploadProductDetails();
    }

    private void uploadProductImage() {
        showProgressDialog(getResources().getString(R.string.please_wait));
        new FirestoreClassKT().uploadImageToCloudStorage(
                EditProductActivity.this,
        mSelectedImageFileUri,
                Constants.PRODUCT_IMAGE
        );
    }
}


