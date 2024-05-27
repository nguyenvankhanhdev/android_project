package com.example.test.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.firestoreclass.FirestoreClass;
import com.example.test.models.Address;
import com.example.test.models.Cart;
import com.example.test.models.Order;
import com.example.test.models.Product;
import com.example.test.models.SizeProduct;
import com.example.test.models.User;
import com.example.test.ui.adapters.CartItemsListAdapter;
import com.example.test.utils.ClothesButton;
import com.example.test.utils.ClothesTextView;
import com.example.test.utils.ClothesTextViewBold;
import com.example.test.utils.Constants;
import com.example.test.utils.JavaMailAPI;

import java.util.ArrayList;
import java.util.Objects;

public class CheckoutActivity extends BaseActivity {

    private Toolbar toolbar_checkout_activity;
    private ClothesTextView tv_product_items;
    private RecyclerView rv_cart_list_items;
    private ClothesTextView tv_selected_address;
    private LinearLayout ll_checkout_address_details;
    private ClothesTextView tv_checkout_address_type;
    private ClothesTextViewBold tv_checkout_full_name;
    private ClothesTextView tv_checkout_address;
    private ClothesTextView tv_checkout_additional_note;
    private ClothesTextView tv_checkout_other_details;
    private ClothesTextView tv_checkout_mobile_number;
    private ClothesTextView tv_items_receipt;
    private ClothesTextView tv_checkout_sub_total;
    private ClothesTextView tv_checkout_shipping_charge;
    private ClothesTextViewBold tv_checkout_total_amount;
    private LinearLayout ll_checkout_place_order;
    private ClothesTextViewBold tv_payment_mode;
    private ClothesButton btn_place_order;

    private Address mAddressDetails;
    private User mUserDetails;
    private ArrayList<Product> mProductsList;
    private ArrayList<Cart> mCartItemsList;
    private double mSubTotal = 0.0;
    private double mTotalAmount = 0.0;
    private Order mOrderDetails;
    private static final String TAG = "FirestoreHelper";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        enableEdgeToEdge();
        setContentView(R.layout.activity_checkout);

        toolbar_checkout_activity = findViewById(R.id.toolbar_checkout_activity);
        tv_product_items = findViewById(R.id.tv_product_items);
        rv_cart_list_items = findViewById(R.id.rv_cart_list_items);
        tv_selected_address = findViewById(R.id.tv_selected_address);
        ll_checkout_address_details = findViewById(R.id.ll_checkout_address_details);
        tv_checkout_address_type = findViewById(R.id.tv_checkout_address_type);
        tv_checkout_full_name = findViewById(R.id.tv_checkout_full_name);
        tv_checkout_address = findViewById(R.id.tv_checkout_address);
        tv_checkout_additional_note = findViewById(R.id.tv_checkout_additional_note);
        tv_checkout_other_details = findViewById(R.id.tv_checkout_other_details);
        tv_checkout_mobile_number = findViewById(R.id.tv_checkout_mobile_number);
        tv_items_receipt = findViewById(R.id.tv_items_receipt);
        tv_checkout_sub_total = findViewById(R.id.tv_checkout_sub_total);
        tv_checkout_shipping_charge = findViewById(R.id.tv_checkout_shipping_charge);
        tv_checkout_total_amount = findViewById(R.id.tv_checkout_total_amount);
        ll_checkout_place_order = findViewById(R.id.ll_checkout_place_order);
        tv_payment_mode = findViewById(R.id.tv_payment_mode);
        btn_place_order = findViewById(R.id.btn_place_order);

        setupActionBar();

        if (getIntent().hasExtra(Constants.EXTRA_SELECTED_ADDRESS)) {
            mAddressDetails = getIntent().getParcelableExtra(Constants.EXTRA_SELECTED_ADDRESS);
        }

        if (mAddressDetails != null) {
            tv_checkout_address_type.setText(mAddressDetails.getType());
            tv_checkout_full_name.setText(mAddressDetails.getName());
            tv_checkout_address.setText(mAddressDetails.getAddress() + ", " + mAddressDetails.getZipCode());
            tv_checkout_additional_note.setText(mAddressDetails.getAdditionalNote());
            if (!Objects.requireNonNull(mAddressDetails.getOtherDetails()).isEmpty()) {
                tv_checkout_other_details.setText(mAddressDetails.getOtherDetails());
            }
            tv_checkout_mobile_number.setText(mAddressDetails.getMobileNumber());
        }

        btn_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeAnOrder();
            }
        });

        getProductList();
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar_checkout_activity);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar_checkout_activity.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
        FirestoreClass.getSelectedCartList(this);
    }

    public void successCartItemsList(ArrayList<Cart> cartList, ArrayList<SizeProduct> sizeProductList) {
        hideProgressDialog();
        for (Cart cart : cartList) {
            for (SizeProduct sizeProduct : sizeProductList) {
                if (sizeProduct.getProduct_id().equals(cart.getProduct_id())) {
                    cart.setStock_quantity(String.valueOf(sizeProduct.getQuantity()));
                    break;
                }
            }
        }
        mCartItemsList = cartList;
        rv_cart_list_items.setLayoutManager(new LinearLayoutManager(this));
        rv_cart_list_items.setHasFixedSize(true);
        CartItemsListAdapter cartListAdapter = new CartItemsListAdapter(this, mCartItemsList, false);
        rv_cart_list_items.setAdapter(cartListAdapter);
        double subTotal = 0.0;

        for (Cart item : mCartItemsList) {
            if (item.isChecked()) {
                int availableQuantity = Integer.parseInt(item.getStock_quantity());
                if (availableQuantity > 0) {
                    double price = Double.parseDouble(item.getPrice());
                    int quantity = Integer.parseInt(item.getCart_quantity());
                    subTotal += (price * quantity);
                }
            }
        }

        tv_checkout_sub_total.setText("$" + subTotal);
        tv_checkout_shipping_charge.setText("$10.0");
        ll_checkout_place_order.setVisibility(View.VISIBLE);
        double total = subTotal + 10.0;
        tv_checkout_total_amount.setText("$" + total);

    }

    private void placeAnOrder() {
        showProgressDialog(getResources().getString(R.string.please_wait));
        mOrderDetails = new Order(
                FirestoreClass.getCurrentUserID(),
                mCartItemsList,
                mAddressDetails,
                "My order " + System.currentTimeMillis(),
                mCartItemsList.get(0).getImage(),
                String.valueOf(mSubTotal),
                "10.0",
                String.valueOf(mTotalAmount),
                String.valueOf(System.currentTimeMillis())
        );
        new FirestoreClass().placeOrder(this, mOrderDetails);
    }


    public void orderPlacedSuccess() {
        new FirestoreClass().updateAllDetails(this, mCartItemsList, mOrderDetails);
        for (Cart cartItem : mCartItemsList) {
            FirestoreClass.updateSizeProductQuantity(cartItem.getProduct_id(), Integer.parseInt(cartItem.getSize()), Integer.parseInt(cartItem.getCart_quantity()));
        }

        getUserDetailsAndSendEmail();

    }

    private void getUserDetailsAndSendEmail() {
        FirestoreClass.getUserDetails(this);
    }

    public void userDetailsSuccess(User user) {
        sendEmail(user);
    }

    private void sendEmail(User user) {
        String userEmail = user.getEmail();
        JavaMailAPI javaMailAPI = new JavaMailAPI(this, userEmail, "Confirmation of Order", "Thank you for placing your order. Your order has been confirmed.");
        javaMailAPI.execute();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    public void allDetailsUpdatedSuccessfully() {
        hideProgressDialog();
        Toast.makeText(this, "Your order placed successfully.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

