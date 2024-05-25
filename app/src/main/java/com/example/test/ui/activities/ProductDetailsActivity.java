package com.example.test.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.test.R;
import com.example.test.firestoreclass.FirestoreClass;
import com.example.test.models.Cart;
import com.example.test.models.Product;
import com.example.test.utils.Constants;
import com.example.test.utils.GlideLoader;

import java.util.List;
import java.util.Map;

public class ProductDetailsActivity extends BaseActivity implements View.OnClickListener {
    private Toolbar toolbar_product_details_activity;
    private ImageView iv_product_detail_image;
    private TextView tv_product_details_title;
    private TextView tv_product_details_price;
    private TextView tv_product_details_description;
    private TextView tv_product_details_available_quantity;
    private Button btn_add_to_cart;
    private Button btn_go_to_cart;
    private Spinner spinner_product_size;
    private Product mProductDetails;
    private Cart mCartItem;
    private String mProductId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        enableEdgeToEdge();
        setContentView(R.layout.activity_product_details);

        btn_add_to_cart = findViewById(R.id.btn_add_to_cart);
        toolbar_product_details_activity = findViewById(R.id.toolbar_product_details_activity);
        iv_product_detail_image = findViewById(R.id.iv_product_detail_image);
        tv_product_details_title = findViewById(R.id.tv_product_details_title);
        tv_product_details_price = findViewById(R.id.tv_product_details_price);
        tv_product_details_description = findViewById(R.id.tv_product_details_description);
        tv_product_details_available_quantity = findViewById(R.id.tv_product_details_available_quantity);
        btn_go_to_cart = findViewById(R.id.btn_go_to_cart);
        spinner_product_size = findViewById(R.id.spinner_product_size);

        if (getIntent().hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductId = getIntent().getStringExtra(Constants.EXTRA_PRODUCT_ID);
        }

        String productOwnerId = "";
        if (getIntent().hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)) {
            productOwnerId = getIntent().getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID);
        }

        setupActionBar();

        if (new FirestoreClass().getCurrentUserID().equals(productOwnerId)) {
            btn_add_to_cart.setVisibility(View.GONE);
            btn_go_to_cart.setVisibility(View.GONE);
        } else {
            btn_add_to_cart.setVisibility(View.VISIBLE);
        }

        btn_add_to_cart.setOnClickListener(this);
        btn_go_to_cart.setOnClickListener(this);

        getProductDetails();
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            int id = v.getId();
            if (id == R.id.btn_add_to_cart) {
                addToCart();
            } else if (id == R.id.btn_go_to_cart) {
                startActivity(new Intent(this, CartListActivity.class));
            }
        }
    }

    private void addToCart() {
        String selectedSize = spinner_product_size.getSelectedItem().toString();
        Cart addToCart = new Cart(
                FirestoreClass.getCurrentUserID(),
                mProductId,
                mProductDetails.getTitle(),
                mProductDetails.getPrice(),
                mProductDetails.getImage(),
                true,
                Constants.DEFAULT_CART_QUANTITY,
                selectedSize
        );
        showProgressDialog(getResources().getString(R.string.please_wait));
        new FirestoreClass().addCartItems(this, addToCart);
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar_product_details_activity);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp);
        }
        toolbar_product_details_activity.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void getProductDetails() {
        new FirestoreClass().getProductDetails(ProductDetailsActivity.this, mProductId);
    }

    public void productDetailsSuccess(Product product, Map<String, Long> sizeQuantityMap) {
        mProductDetails = product;
        new GlideLoader(ProductDetailsActivity.this).loadProductPicture(
                product.getImage(),
                iv_product_detail_image
        );
        tv_product_details_title.setText(product.getTitle());
        tv_product_details_price.setText("$" + product.getPrice());
        tv_product_details_description.setText(product.getDescription());
        List<String> sizeList = new java.util.ArrayList<>(sizeQuantityMap.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(ProductDetailsActivity.this, android.R.layout.simple_spinner_item, sizeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_product_size.setAdapter(adapter);
        spinner_product_size.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedSize = parent.getItemAtPosition(position).toString();
                Long quantity = sizeQuantityMap.get(selectedSize);
                tv_product_details_available_quantity.setText(String.valueOf(quantity != null ? quantity : 0));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void productExistsInCart() {
        hideProgressDialog();
        btn_add_to_cart.setVisibility(View.GONE);
        btn_go_to_cart.setVisibility(View.VISIBLE);
    }

    public void addToCartSuccess() {
        hideProgressDialog();
        Toast.makeText(
                ProductDetailsActivity.this,
                getResources().getString(R.string.success_message_item_added_to_cart),
                Toast.LENGTH_SHORT
        ).show();
        btn_add_to_cart.setVisibility(View.GONE);
        btn_go_to_cart.setVisibility(View.VISIBLE);
    }
}
