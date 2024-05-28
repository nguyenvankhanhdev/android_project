package com.example.test.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.firestoreclass.FirestoreClass;
import com.example.test.firestoreclass.FirestoreClassKT;
import com.example.test.models.Cart;
import com.example.test.models.Product;
import com.example.test.models.SizeProduct;
import com.example.test.ui.adapters.CartItemsListAdapter;
import com.example.test.utils.Constants;

import java.util.ArrayList;

public class CartListActivity extends BaseActivity {

    private Toolbar toolbar_cart_list_activity;
    private RecyclerView rv_cart_items_list;
    private LinearLayout ll_checkout;
    private TextView tv_sub_total, tv_no_cart_item_found, tv_shipping_charge, tv_total_amount;
    private Button btn_checkout;
    private ArrayList<Cart> mCartListItems;
    private ArrayList<Product> mProductsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        enableEdgeToEdge();
        setContentView(R.layout.activity_cart_list);

        toolbar_cart_list_activity = findViewById(R.id.toolbar_cart_list_activity);
        rv_cart_items_list = findViewById(R.id.rv_cart_items_list);
        ll_checkout = findViewById(R.id.ll_checkout);
        tv_sub_total = findViewById(R.id.tv_sub_total);
        tv_no_cart_item_found = findViewById(R.id.tv_no_cart_item_found);
        tv_shipping_charge = findViewById(R.id.tv_shipping_charge);
        tv_total_amount = findViewById(R.id.tv_total_amount);
        btn_checkout = findViewById(R.id.btn_checkout);

        setSupportActionBar(toolbar_cart_list_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar_cart_list_activity.setNavigationOnClickListener(v -> onBackPressed());

        btn_checkout.setOnClickListener(v -> {
            Intent intent = new Intent(CartListActivity.this, AddressListActivity.class);
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS, true);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProductList();
    }

    private void getProductList() {
        showProgressDialog(getResources().getString(R.string.please_wait));
        FirestoreClass.getAllProductsList(this);
    }

    public void successProductsListFromFireStore(ArrayList<Product> productsList) {
        mProductsList = productsList;
        getCartItemsList();
    }

    private void getCartItemsList() {
        FirestoreClass.getCartList(this);
    }

    public void successCartItemsList(ArrayList<Cart> cartList, ArrayList<SizeProduct> sizeProductList) {
        hideProgressDialog();
        for (Cart cart : cartList) {
            for (SizeProduct sizeProduct : sizeProductList) {
                if (sizeProduct.getProduct_id().equals(cart.getProduct_id()) && sizeProduct.getSize() == Integer.parseInt(cart.getSize())) {
                    cart.setStock_quantity(String.valueOf(sizeProduct.getQuantity()));
                    if (Integer.parseInt(cart.getStock_quantity()) == 0) {
                        cart.setCart_quantity(cart.getStock_quantity());
                    }
                    break;
                }
            }
        }
        mCartListItems = cartList;
        if (mCartListItems.size() > 0) {
            rv_cart_items_list.setVisibility(View.VISIBLE);
            ll_checkout.setVisibility(View.VISIBLE);
            tv_no_cart_item_found.setVisibility(View.GONE);
            btn_checkout.setVisibility(View.VISIBLE);
            rv_cart_items_list.setLayoutManager(new LinearLayoutManager(CartListActivity.this));
            rv_cart_items_list.setHasFixedSize(true);
            CartItemsListAdapter cartListAdapter = new CartItemsListAdapter(CartListActivity.this, mCartListItems, true, this::onCheckboxChanged);
            rv_cart_items_list.setAdapter(cartListAdapter);
            updateTotals();
        } else {
            rv_cart_items_list.setVisibility(View.GONE);
            ll_checkout.setVisibility(View.GONE);
            tv_no_cart_item_found.setVisibility(View.VISIBLE);
        }
    }

    private void onCheckboxChanged(Cart cart, boolean isChecked) {
        updateTotals();
    }

    private void updateTotals() {
        double subTotal = 0.0;

        for (Cart item : mCartListItems) {
            if (item.isChecked()) {
                int availableQuantity = Integer.parseInt(item.getStock_quantity());
                if (availableQuantity > 0) {
                    double price = Double.parseDouble(item.getPrice());
                    int quantity = Integer.parseInt(item.getCart_quantity());
                    subTotal += (price * quantity);
                }
            }
        }

        tv_sub_total.setText("$" + subTotal);
        tv_shipping_charge.setText("$10.0");

        if (subTotal > 0) {
            btn_checkout.setVisibility(View.VISIBLE);
            ll_checkout.setVisibility(View.VISIBLE);
            double total = subTotal + 10;
            tv_total_amount.setText("$" + total);
        } else {
            btn_checkout.setVisibility(View.GONE);
            ll_checkout.setVisibility(View.VISIBLE);
            tv_total_amount.setText("$0.0");
            tv_shipping_charge.setText("$0.0");
        }
    }


    public void itemRemovedSuccess() {
        hideProgressDialog();
        Toast.makeText(this, getResources().getString(R.string.msg_item_removed_successfully), Toast.LENGTH_SHORT).show();
        getCartItemsList();
    }

    public void itemUpdateSuccess() {
        hideProgressDialog();
        getCartItemsList();
    }
}
