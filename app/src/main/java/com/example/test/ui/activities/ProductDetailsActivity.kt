package com.example.test.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.widget.Toolbar
import com.example.test.R
import com.example.test.firestoreclass.FirestoreClassKT
import com.example.test.models.Cart
import com.example.test.models.Product
import com.example.test.utils.Constants
import com.example.test.utils.GlideLoader

class ProductDetailsActivity : BaseActivity(), View.OnClickListener {
    private lateinit var toolbar_product_details_activity: Toolbar
    private lateinit var iv_product_detail_image: ImageView
    private lateinit var tv_product_details_title: TextView
    private lateinit var tv_product_details_price: TextView
    private lateinit var tv_product_details_description: TextView
    private lateinit var tv_product_details_available_quantity: TextView
    private lateinit var btn_add_to_cart: Button
    private lateinit var btn_go_to_cart: Button
    private lateinit var spinner_product_size: Spinner
    private lateinit var tv_type: TextView
    private lateinit var mProductDetails: Product
    private var mProductId: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_details)
        btn_add_to_cart = findViewById(R.id.btn_add_to_cart)
        toolbar_product_details_activity = findViewById(R.id.toolbar_product_details_activity)
        iv_product_detail_image = findViewById(R.id.iv_product_detail_image)
        tv_product_details_title = findViewById(R.id.tv_product_details_title)
        tv_product_details_price = findViewById(R.id.tv_product_details_price)
        tv_product_details_description = findViewById(R.id.tv_product_details_description)
        tv_product_details_available_quantity =
            findViewById(R.id.tv_product_details_available_quantity)
        btn_go_to_cart = findViewById(R.id.btn_go_to_cart)
        spinner_product_size = findViewById(R.id.spinner_product_size)
        tv_type = findViewById(R.id.tv_type)

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductId =
                intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
        }
        var productOwnerId: String = ""
        if (intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)) {
            productOwnerId =
                intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
        }

        setupActionBar()

        if (FirestoreClassKT().getCurrentUserID() == productOwnerId) {
            btn_add_to_cart.visibility = View.GONE
            btn_go_to_cart.visibility = View.GONE
        } else {
            btn_add_to_cart.visibility = View.VISIBLE
        }

        btn_add_to_cart.setOnClickListener(this)
        btn_go_to_cart.setOnClickListener(this)

        getProductDetails()


    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {

                R.id.btn_add_to_cart -> {
                    addToCart()
                }

                R.id.btn_go_to_cart -> {
                    startActivity(Intent(this@ProductDetailsActivity, CartListActivity::class.java))
                }
            }
        }
    }

    private fun addToCart() {
        val selectedSize = spinner_product_size.selectedItem.toString()
        val addToCart = Cart(
            FirestoreClassKT().getCurrentUserID(),
            mProductId,
            mProductDetails.title,
            mProductDetails.price,
            mProductDetails.image,
            Constants.DEFAULT_CART_QUANTITY,
            tv_product_details_available_quantity.text.toString(),
            selectedSize
        )
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClassKT().addCartItems(this@ProductDetailsActivity, addToCart)
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_product_details_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        toolbar_product_details_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getProductDetails() {
        FirestoreClassKT().getProductDetails(this@ProductDetailsActivity, mProductId)
    }

    fun productDetailsSuccess(product: Product, sizeQuantityMap: Map<String, Long>) {
        mProductDetails = product
        GlideLoader(this@ProductDetailsActivity).loadProductPicture(
            product.image,
            iv_product_detail_image
        )
        tv_product_details_title.text = product.title
        tv_product_details_price.text = "$${product.price}"
        tv_product_details_description.text = product.description
        val sizeList = sizeQuantityMap.keys.toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sizeList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        FirestoreClassKT().getTypeNameById(product.shoeTypeId) { typeName ->
            if (typeName != null) {
                tv_type.text = typeName
            } else {
                tv_type.text = "Unknown Type"
            }
        }
        spinner_product_size.adapter = adapter
        spinner_product_size.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                val selectedSize = parent?.getItemAtPosition(position).toString()
                val quantity = sizeQuantityMap[selectedSize] ?: 0
                tv_product_details_available_quantity.text = "$quantity"
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    fun productExistsInCart() {
        hideProgressDialog()
        btn_add_to_cart.visibility = View.GONE
        btn_go_to_cart.visibility = View.VISIBLE
    }

    fun addToCartSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this@ProductDetailsActivity,
            resources.getString(R.string.success_message_item_added_to_cart),
            Toast.LENGTH_SHORT
        ).show()
        btn_add_to_cart.visibility = View.GONE
        btn_go_to_cart.visibility = View.VISIBLE
    }


}