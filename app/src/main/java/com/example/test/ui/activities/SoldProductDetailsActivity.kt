package com.example.test.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.widget.Toolbar
import com.example.test.R
import com.example.test.models.SoldProduct
import com.example.test.utils.Constants
import com.example.test.utils.GlideLoader
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.*

class SoldProductDetailsActivity : BaseActivity() {

    private lateinit var toolbar_sold_product_details_activity: Toolbar
    private lateinit var tv_sold_product_details_id: TextView
    private lateinit var tv_sold_product_details_date: TextView
    private lateinit var iv_product_item_image: ImageView
    private lateinit var tv_product_item_price: TextView
    private lateinit var tv_product_item_size :TextView
    private lateinit var tv_sold_product_quantity: TextView
    private lateinit var tv_sold_details_address_type: TextView
    private lateinit var tv_sold_details_full_name: TextView
    private lateinit var tv_sold_details_address: TextView
    private lateinit var tv_sold_details_additional_note: TextView
    private lateinit var tv_sold_details_other_details: TextView
    private lateinit var tv_sold_details_mobile_number: TextView
    private lateinit var tv_sold_product_sub_total: TextView
    private lateinit var tv_sold_product_shipping_charge: TextView
    private lateinit var tv_sold_product_total_amount: TextView
    private lateinit var tv_product_item_name:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sold_product_details)
        toolbar_sold_product_details_activity = findViewById(R.id.toolbar_sold_product_details_activity)
        tv_sold_product_details_id = findViewById(R.id.tv_sold_product_details_id)
        tv_sold_product_details_date = findViewById(R.id.tv_sold_product_details_date)
        iv_product_item_image= findViewById(R.id.iv_product_item_image)
        tv_product_item_price= findViewById(R.id.tv_product_item_price)
        tv_product_item_size = findViewById(R.id.tv_product_item_size)
        tv_sold_product_quantity= findViewById(R.id.tv_sold_product_quantity)
        tv_sold_details_address_type= findViewById(R.id.tv_sold_details_address_type)
        tv_sold_details_full_name= findViewById(R.id.tv_sold_details_full_name)
        tv_sold_details_address= findViewById(R.id.tv_sold_details_address)
        tv_sold_details_additional_note= findViewById(R.id.tv_sold_details_additional_note)
        tv_sold_details_other_details= findViewById(R.id.tv_sold_details_other_details)
        tv_sold_details_mobile_number= findViewById(R.id.tv_sold_details_mobile_number)
        tv_sold_product_sub_total= findViewById(R.id.tv_sold_product_sub_total)
        tv_sold_product_shipping_charge= findViewById(R.id.tv_sold_product_shipping_charge)
        tv_sold_product_total_amount= findViewById(R.id.tv_sold_product_total_amount)
        tv_product_item_name = findViewById(R.id.tv_product_item_name)

        var productDetails: SoldProduct = SoldProduct()
        if (intent.hasExtra(Constants.EXTRA_SOLD_PRODUCT_DETAILS)) {
            productDetails =
                intent.getParcelableExtra<SoldProduct>(Constants.EXTRA_SOLD_PRODUCT_DETAILS)!!
        }

        setupActionBar()

        setupUI(productDetails)

    }
    private fun setupActionBar() {
        setSupportActionBar(toolbar_sold_product_details_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        toolbar_sold_product_details_activity.setNavigationOnClickListener { onBackPressed() }
    }
    private fun setupUI(productDetails: SoldProduct) {
        tv_sold_product_details_id.text = productDetails.order_id
        val dateFormat = "dd MMM yyyy HH:mm"
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = productDetails.order_date
        tv_sold_product_details_date.text = formatter.format(calendar.time)
        GlideLoader(this@SoldProductDetailsActivity).loadProductPicture(
            productDetails.image,
            iv_product_item_image
        )
        tv_product_item_name.text = productDetails.title
        tv_product_item_price.text ="$${productDetails.price}"
        tv_sold_product_quantity.text = productDetails.sold_quantity
        tv_sold_details_address_type.text = productDetails.address.type
        tv_sold_details_full_name.text = productDetails.address.name
//        tv_product_item_size.text= productDetails.size
        tv_sold_details_address.text =
            "${productDetails.address.address}, ${productDetails.address.zipCode}"
        tv_sold_details_additional_note.text = productDetails.address.additionalNote

        if (productDetails.address.otherDetails.isNotEmpty()) {
            tv_sold_details_other_details.visibility = View.VISIBLE
            tv_sold_details_other_details.text = productDetails.address.otherDetails
        } else {
            tv_sold_details_other_details.visibility = View.GONE
        }
        tv_sold_details_mobile_number.text = productDetails.address.mobileNumber
        tv_sold_product_sub_total.text = productDetails.sub_total_amount
        tv_sold_product_shipping_charge.text = productDetails.shipping_charge
        tv_sold_product_total_amount.text = productDetails.total_amount
    }

}