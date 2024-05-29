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
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.test.R;
import com.example.test.firestoreclass.FirestoreClass;
import com.example.test.models.Product;
import com.example.test.models.SizeProduct;
import com.example.test.utils.Constants;
import com.example.test.utils.GlideLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;

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
    private Uri mSelectedImageFileUri = null;
    private String mProductImageURL = "";
    private ImageView iv_edit_product_image;
    private Spinner spinner_type_edit;
    private Product product;
    private FirebaseFirestore db;
    private CollectionReference productRef;
    private CollectionReference sizeProductRef;
    private List<Pair<String, Integer>> sizeQuantityList = new ArrayList<>();
    private List<Pair<String, String>> typeList = new ArrayList<>();

    private String selectedType = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        toolbar_add_product_activity = findViewById(R.id.toolbar_add_product_activity);
        etProductTitle = findViewById(R.id.et_edit_product_title);
        etProductPrice = findViewById(R.id.et_edit_product_price);
        etProductDescription = findViewById(R.id.et_edit_product_description);
        spinnerSize = findViewById(R.id.spinner_edit_size);
        etQuantity = findViewById(R.id.et_edit_quantity);
        btnAddSize = findViewById(R.id.btn_edit_add_size);
        sizesContainer = findViewById(R.id.sizes_edit_container);
        spinner_type_edit = findViewById(R.id.spinner_type_edit);
        btnSubmit = findViewById(R.id.btn_submit_edit_product);
        iv_edit_update_product = findViewById(R.id.iv_edit_update_product);
        iv_edit_product_image = findViewById(R.id.iv_edit_product_image);
        setupActionBar();
        setupSpinner();

        db = FirebaseFirestore.getInstance();
        productRef = db.collection("products");
        sizeProductRef = db.collection("size_product");

        spinner_type_edit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedType = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (getIntent().hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            String productId = getIntent().getStringExtra(Constants.EXTRA_PRODUCT_ID);
            loadProductDetails(productId);
        }

        iv_edit_update_product.setOnClickListener(this);
        btnAddSize.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        setSpinnerType();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
        if (v.getId() == R.id.iv_edit_update_product) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(EditProductActivity.this);
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        Constants.READ_STORAGE_PERMISSION_CODE
                );
            }
        } else if (v.getId() == R.id.btn_edit_add_size) {
            addSizeItem();
        } else if (v.getId() == R.id.btn_submit_edit_product) {
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
        String selectedSize = spinnerSize.getSelectedItem().toString();
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
        TextView tvSize = sizeView.findViewById(R.id.tv_size);
        TextView tvQuantity = sizeView.findViewById(R.id.tv_quantity);
        tvSize.setText(size);
        tvQuantity.setText(String.valueOf(quantity));
        sizesContainer.addView(sizeView);
        sizeQuantityList.add(new Pair<>(size, quantity));
    }


    private void updateProduct() {
        String title = etProductTitle.getText().toString().trim();
        String price = etProductPrice.getText().toString().trim();
        String description = etProductDescription.getText().toString().trim();
        String selectedTypeName = selectedType;
        String selectedTypeId = null;
        for (Pair<String, String> pair : typeList) {
            if (pair.first.equals(selectedTypeName)) {
                selectedTypeId = pair.second;
                break;
            }
        }

        if (selectedTypeId == null) {
            showErrorSnackBar("Invalid product type selected.", true);
            return;
        }

        if (!title.isEmpty() && !price.isEmpty() && !description.isEmpty()) {
            Product updatedProduct = new Product(
                    product.getProduct_id(),
                    title,
                    price,
                    description,
                    product.getImage(),
                    selectedTypeId
            );
            productRef.document(product.getProduct_id())
                    .set(updatedProduct, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            updateSizes();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideProgressDialog();
                            showErrorSnackBar(e.getMessage(), true);
                        }
                    });
        } else {
            hideProgressDialog();
            showErrorSnackBar("Please enter all the details.", true);
        }
    }


    private void updateSizes() {
        showProgressDialog(getResources().getString(R.string.please_wait));
        FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();
        mFireStore.collection("size_product")
                .whereEqualTo("product_id", product.getProduct_id())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Task<Void>> tasks = new ArrayList<>();
                    for (Pair<String, Integer> pair : sizeQuantityList) {
                        String size = pair.first;
                        int quantity = pair.second;

                        boolean sizeExists = false;
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            SizeProduct existingSize = document.toObject(SizeProduct.class);
                            if (existingSize != null && existingSize.getSize_id().equals(size)) {
                                updateSize(existingSize, quantity);
                                sizeExists = true;
                                break;
                            }
                        }

                        if (!sizeExists) {
                            tasks.add(addNewSize(size, quantity));
                        }
                    }

                    Tasks.whenAll(tasks)
                            .addOnSuccessListener(aVoid -> {
                                hideProgressDialog();
                                Intent intent = new Intent(EditProductActivity.this, AdminActivity.class);
                                startActivity(intent);
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                hideProgressDialog();
                                Log.e("FirestoreClass", "Failed to update sizes", e);
                                showErrorSnackBar("Failed to update sizes", true);
                            });
                })
                .addOnFailureListener(e -> {
                    hideProgressDialog();
                    Log.e("FirestoreClass", "Error while getting sizes", e);
                    showErrorSnackBar("Error while getting sizes", true);
                });
    }



    private Task<SizeProduct> checkSizeExistence(String size) {
        return Tasks.call(() -> {
            QuerySnapshot querySnapshot = Tasks.await(
                    sizeProductRef.whereEqualTo("product_id", product.getProduct_id())
                            .whereEqualTo("size_id", size)
                            .get()
            );

            if (!querySnapshot.isEmpty()) {
                DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                return documentSnapshot.toObject(SizeProduct.class);
            } else {
                return null;
            }
        });
    }


    private void updateSize(SizeProduct sizeProduct, int quantity) {
        int newQuantity = sizeProduct.getQuantity() + quantity;
        sizeProductRef.document(sizeProduct.getSize_id()).update("quantity", newQuantity)
                .addOnSuccessListener(aVoid -> {
                    Log.i("Update Size", "Successfully updated size: " + sizeProduct.getSize_id());
                })
                .addOnFailureListener(e -> {
                    Log.e("Update Size", "Failed to update size: " + sizeProduct.getSize_id(), e);
                });
    }


    private Task<Void> addNewSize(String size, int quantity) {
        SizeProduct newSizeProduct = new SizeProduct("", Integer.parseInt(size), quantity, product.getProduct_id());
        return sizeProductRef.add(newSizeProduct)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        Log.i("Add Size", "Successfully added size: " + size);
                        return Tasks.forResult(null); // Return a completed Task<Void>
                    } else {
                        Exception e = task.getException();
                        Log.e("Add Size", "Failed to add size: " + size, e);
                        return Tasks.forException(e); // Return a failed Task<Void>
                    }
                });
    }



    private void setupActionBar() {
        setSupportActionBar(toolbar_add_product_activity);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp);
        }
        toolbar_add_product_activity.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        if (document != null && document.exists()) {
                            product = document.toObject(Product.class);
                            GlideLoader glideLoader = new GlideLoader(EditProductActivity.this);
                            glideLoader.loadProductPicture(product.getImage(), iv_edit_product_image);
                            etProductTitle.setText(product.getTitle());
                            etProductPrice.setText(product.getPrice());
                            etProductDescription.setText(product.getDescription());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showErrorSnackBar(e.getMessage(), true);
                    }
                });
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


    private void setSpinnerType() {
        FirestoreClass.getType(new FirestoreClass.Callback<List<Pair<String, String>>>() {
            @Override
            public void onResult(List<Pair<String, String>> types) {
                typeList.clear();
                for (Pair<String, String> type : types) {
                    typeList.add(type);
                }
                List<String> typeNames = new ArrayList<>();
                for (Pair<String, String> type : types) {
                    typeNames.add(type.first);
                }
                setupSpinner(typeNames);
            }

            @Override
            public void onCallback(List<Pair<String, String>> result) {

            }
        });
    }


    private void setupSpinner(List<String> typeNames) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, typeNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_type_edit.setAdapter(adapter);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
        String productId = product.getProduct_id();
        String title = etProductTitle.getText().toString().trim();
        String price = etProductPrice.getText().toString().trim();
        String description = etProductDescription.getText().toString().trim();

        Product product = new Product(productId, title, price, description, mProductImageURL);

        productRef.document(productId)
                .set(product, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updateSizes();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hideProgressDialog();
                        showErrorSnackBar(e.getMessage(), true);
                    }
                });
    }

    public void imageUploadSuccess(String imageURL) {
        mProductImageURL = imageURL;
        uploadProductDetails();
    }

    private void uploadProductImage() {
        showProgressDialog(getResources().getString(R.string.please_wait));
        FirestoreClass.uploadImageToCloudStorage(
                this,
                mSelectedImageFileUri,
                Constants.PRODUCT_IMAGE
        );
    }

}

