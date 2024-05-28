package com.example.test.ui.activities

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import com.example.test.firestoreclass.FirestoreClassKT
import com.example.test.models.Address
import com.example.test.models.Cart
import com.example.test.models.Order
import com.example.test.models.Product
import com.example.test.models.SizeProduct
import com.example.test.ui.adapters.CartItemsListAdapter
import com.example.test.utils.ClothesButton
import com.example.test.utils.ClothesTextView
import com.example.test.utils.ClothesTextViewBold
import com.example.test.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore

class CheckoutActivity : BaseActivity() {

    private lateinit var toolbar_checkout_activity: Toolbar
    private lateinit var tv_product_items: ClothesTextView

    private lateinit var rv_cart_list_items: RecyclerView

    private lateinit var tv_selected_address: ClothesTextView
    private lateinit var ll_checkout_address_details: LinearLayout
    private lateinit var tv_checkout_address_type: ClothesTextView
    private lateinit var tv_checkout_full_name: ClothesTextViewBold
    private lateinit var tv_checkout_address: ClothesTextView
    private lateinit var tv_checkout_additional_note: ClothesTextView
    private lateinit var tv_checkout_other_details: ClothesTextView
    private lateinit var tv_checkout_mobile_number: ClothesTextView
    private lateinit var tv_items_receipt: ClothesTextView
    private lateinit var tv_checkout_sub_total: ClothesTextView
    private lateinit var tv_checkout_shipping_charge: ClothesTextView
    private lateinit var tv_checkout_total_amount: ClothesTextViewBold
    private lateinit var ll_checkout_place_order: LinearLayout
    private lateinit var tv_payment_mode: ClothesTextViewBold
    private lateinit var btn_place_order: ClothesButton
    private var mAddressDetails: Address? = null
    private lateinit var mProductsList: ArrayList<Product>
    private lateinit var mCartItemsList: ArrayList<Cart>
    private var mSubTotal: Double = 0.0
    private var mTotalAmount: Double = 0.0
    private lateinit var mOrderDetails: Order
    private val mFireStore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_checkout)
        toolbar_checkout_activity = findViewById(R.id.toolbar_checkout_activity)
        tv_product_items = findViewById(R.id.tv_product_items)
        rv_cart_list_items = findViewById(R.id.rv_cart_list_items)
        tv_selected_address = findViewById(R.id.tv_selected_address)
        ll_checkout_address_details = findViewById(R.id.ll_checkout_address_details)
        tv_checkout_address_type = findViewById(R.id.tv_checkout_address_type)
        tv_checkout_full_name = findViewById(R.id.tv_checkout_full_name)
        tv_checkout_address = findViewById(R.id.tv_checkout_address)
        tv_checkout_additional_note = findViewById(R.id.tv_checkout_additional_note)
        tv_checkout_other_details = findViewById(R.id.tv_checkout_other_details)
        tv_checkout_mobile_number = findViewById(R.id.tv_checkout_mobile_number)
        tv_items_receipt = findViewById(R.id.tv_items_receipt)
        tv_checkout_sub_total = findViewById(R.id.tv_checkout_sub_total)
        tv_checkout_shipping_charge = findViewById(R.id.tv_checkout_shipping_charge)
        tv_checkout_total_amount = findViewById(R.id.tv_checkout_total_amount)
        ll_checkout_place_order = findViewById(R.id.ll_checkout_place_order)
        tv_payment_mode = findViewById(R.id.tv_payment_mode)
        btn_place_order = findViewById(R.id.btn_place_order)
        setupActionBar()
        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)) {
            mAddressDetails =
                intent.getParcelableExtra<Address>(Constants.EXTRA_SELECTED_ADDRESS)!!
        }
        if (mAddressDetails != null) {
            tv_checkout_address_type.text = mAddressDetails?.type
            tv_checkout_full_name.text = mAddressDetails?.name
            tv_checkout_address.text = "${mAddressDetails!!.address}, ${mAddressDetails!!.zipCode}"
            tv_checkout_additional_note.text = mAddressDetails?.additionalNote
            if (mAddressDetails?.otherDetails!!.isNotEmpty()) {
                tv_checkout_other_details.text = mAddressDetails?.otherDetails
            }
            tv_checkout_mobile_number.text = mAddressDetails?.mobileNumber
        }
        btn_place_order.setOnClickListener {
            placeAnOrder()
        }
        getProductList()

    }
    private fun setupActionBar() {
        setSupportActionBar(toolbar_checkout_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        toolbar_checkout_activity.setNavigationOnClickListener { onBackPressed() }
    }
    private fun getProductList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClassKT().getAllProductsList(this@CheckoutActivity)
    }
    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {
        mProductsList = productsList
        getCartItemsList()
    }
    private fun getCartItemsList() {
        FirestoreClassKT().getCartList(this@CheckoutActivity)
    }
    fun successCartItemsList(cartList: ArrayList<Cart>, sizeProductList: ArrayList<SizeProduct>) {
        hideProgressDialog()
        for (cart in cartList) {
            val sizeProduct = sizeProductList.find { it.product_id == cart.product_id }
            if (sizeProduct != null) {
                cart.stock_quantity = sizeProduct.quantity.toString()
            }
        }
        mCartItemsList = cartList
        rv_cart_list_items.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        rv_cart_list_items.setHasFixedSize(true)
        val cartListAdapter = CartItemsListAdapter(this@CheckoutActivity, mCartItemsList, false)
        rv_cart_list_items.adapter = cartListAdapter
        for (item in mCartItemsList) {
            val availableQuantity = item.stock_quantity.toInt()
            if (availableQuantity > 0) {
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()
                mSubTotal += (price * quantity)
            }
        }
        tv_checkout_sub_total.text = "$$mSubTotal"
        tv_checkout_shipping_charge.text = "$10.0"
        if (mSubTotal > 0) {
            ll_checkout_place_order.visibility = View.VISIBLE

            mTotalAmount = mSubTotal + 10.0
            tv_checkout_total_amount.text = "$$mTotalAmount"
        } else {
            ll_checkout_place_order.visibility = View.GONE
        }
    }
    private fun placeAnOrder() {
        val documentReference = mFireStore.collection(Constants.ORDERS).document()
        val newOrderId = documentReference.id
        showProgressDialog(resources.getString(R.string.please_wait))
        mOrderDetails = Order(
            FirestoreClassKT().getCurrentUserID(),
            mCartItemsList,
            mAddressDetails!!,
            "My order ${System.currentTimeMillis()}",
            mCartItemsList[0].image,
            mCartItemsList[0].size,
            mSubTotal.toString(),
            "10.0",
            mTotalAmount.toString(),
            System.currentTimeMillis(),
            id = newOrderId
        )
        FirestoreClassKT().placeOrder(this@CheckoutActivity, mOrderDetails)
    }
    fun orderPlacedSuccess() {
        FirestoreClassKT().updateAllDetails(this@CheckoutActivity, mCartItemsList, mOrderDetails)
        for (cartItem in mCartItemsList) {
            updateSizeProductQuantity(cartItem.product_id, cartItem.size, cartItem.cart_quantity.toInt())
        }
    }
    override fun onResume() {
        super.onResume()
    }
    fun allDetailsUpdatedSuccessfully() {
        hideProgressDialog()
        Toast.makeText(this@CheckoutActivity, "Your order placed successfully.", Toast.LENGTH_SHORT)
            .show()
        val intent = Intent(this@CheckoutActivity, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    fun updateSizeProductQuantity(productId: String, size: String, quantityToSubtract: Int) {
        val db = FirebaseFirestore.getInstance()
        val sizeProductRef = db.collection(Constants.SIZE_PRODUCTS)
        sizeProductRef
            .whereEqualTo("product_id", productId)
            .whereEqualTo("size", size)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val currentQuantity = document.getLong("quantity") ?: 0
                    val newQuantity = maxOf(0, currentQuantity - quantityToSubtract)
                    document.reference.update("quantity", newQuantity)
                        .addOnSuccessListener {
                            Log.d(TAG, "Quantity updated successfully.")
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Error updating quantity.", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error getting documents: ", e)
            }
    }

}