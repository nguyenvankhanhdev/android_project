package com.example.test.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import com.example.test.firestoreclass.FirestoreClass
import com.example.test.models.Cart
import com.example.test.models.Product
import com.example.test.models.SizeProduct
import com.example.test.ui.adapters.CartItemsListAdapter
import com.example.test.utils.Constants

class CartListActivity : BaseActivity() {

    private lateinit var toolbar_cart_list_activity: Toolbar
    private lateinit var rv_cart_items_list: RecyclerView
    private lateinit var ll_checkout: LinearLayout
    private lateinit var tv_sub_total: TextView
    private lateinit var tv_no_cart_item_found: TextView
    private lateinit var tv_shipping_charge: TextView
    private lateinit var tv_total_amount: TextView
    private lateinit var btn_checkout: Button
    private lateinit var mCartListItems: ArrayList<Cart>
    private lateinit var mProductsList: ArrayList<Product>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart_list)
        toolbar_cart_list_activity = findViewById(R.id.toolbar_cart_list_activity)
        rv_cart_items_list = findViewById(R.id.rv_cart_items_list)
        ll_checkout = findViewById(R.id.ll_checkout)
        tv_sub_total = findViewById(R.id.tv_sub_total)
        tv_no_cart_item_found = findViewById(R.id.tv_no_cart_item_found)
        tv_shipping_charge = findViewById(R.id.tv_shipping_charge)
        tv_total_amount = findViewById(R.id.tv_total_amount)
        setupActionBar()
        btn_checkout = findViewById(R.id.btn_checkout)
        btn_checkout.setOnClickListener {
            val intent = Intent(this@CartListActivity, AddressListActivity::class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS, true)
            startActivity(intent) }

    } override fun onResume() {
        super.onResume()

        getProductList()
    }
    private fun setupActionBar() {
        setSupportActionBar(toolbar_cart_list_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        toolbar_cart_list_activity.setNavigationOnClickListener { onBackPressed() }
    }
    private fun getProductList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAllProductsList(this@CartListActivity)
    }
    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {
        mProductsList = productsList
        getCartItemsList()
    }
    private fun getCartItemsList() {
        FirestoreClass().getCartList(this@CartListActivity)
    }
    fun successCartItemsList(cartList: ArrayList<Cart>, sizeProductList: ArrayList<SizeProduct>) {
        hideProgressDialog()
        for (cart in cartList) {
            val sizeProduct = sizeProductList.find { it.product_id == cart.product_id && it.size == cart.size.toInt() }
            if (sizeProduct != null) {
                cart.stock_quantity = sizeProduct.quantity.toString()
                if (cart.stock_quantity.toIntOrNull() == 0) {
                    cart.cart_quantity = cart.stock_quantity
                }
            }
        }
        mCartListItems = cartList
        if (mCartListItems.size > 0) {
            rv_cart_items_list.visibility = View.VISIBLE
            ll_checkout.visibility = View.VISIBLE
            tv_no_cart_item_found.visibility = View.GONE
            rv_cart_items_list.layoutManager = LinearLayoutManager(this@CartListActivity)
            rv_cart_items_list.setHasFixedSize(true)
            val cartListAdapter = CartItemsListAdapter(this@CartListActivity, mCartListItems, true)
            rv_cart_items_list.adapter = cartListAdapter
            var subTotal: Double = 0.0
            for (item in mCartListItems) {
                val availableQuantity = item.stock_quantity.toIntOrNull() ?: 0
                if (availableQuantity > 0) {
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()
                    subTotal += (price * quantity)
                }
            }
            tv_sub_total.text = "$$subTotal"
            tv_shipping_charge.text = "$10.0"
            if (subTotal > 0) {
                ll_checkout.visibility = View.VISIBLE
                val total = subTotal + 10
                tv_total_amount.text = "$$total"
            } else {
                ll_checkout.visibility = View.GONE
            }
        } else {
            rv_cart_items_list.visibility = View.GONE
            ll_checkout.visibility = View.GONE
            tv_no_cart_item_found.visibility = View.VISIBLE
        }
    }

    fun itemRemovedSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this@CartListActivity,
            resources.getString(R.string.msg_item_removed_successfully),
            Toast.LENGTH_SHORT
        ).show()
        getCartItemsList()
    }
    fun itemUpdateSuccess() {
        hideProgressDialog()
        getCartItemsList()
    }


}