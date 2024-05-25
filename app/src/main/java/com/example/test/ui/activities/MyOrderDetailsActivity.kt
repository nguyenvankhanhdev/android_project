package com.example.test.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import com.example.test.models.Order
import com.example.test.ui.adapters.CartItemsListAdapter
import java.text.SimpleDateFormat
import com.example.test.utils.ClothesTextView
import com.example.test.utils.ClothesTextViewBold
import com.example.test.utils.Constants
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.*

class MyOrderDetailsActivity : AppCompatActivity() {

    private lateinit var toolbar_my_order_details_activity:Toolbar
    private lateinit var tv_order_details_id: ClothesTextView
    private lateinit var tv_order_details_date: ClothesTextView
    private lateinit var tv_order_status: ClothesTextView
    private lateinit var rv_my_order_items_list: RecyclerView
    private lateinit var tv_my_order_details_address_type: ClothesTextView
    private lateinit var tv_my_order_details_full_name: ClothesTextViewBold
    private lateinit var tv_my_order_details_address: ClothesTextView
    private lateinit var tv_my_order_details_additional_note: ClothesTextView
    private lateinit var tv_my_order_details_other_details: ClothesTextView
    private lateinit var tv_my_order_details_mobile_number: ClothesTextView
    private lateinit var tv_order_details_sub_total: ClothesTextView
    private lateinit var tv_order_details_shipping_charge: ClothesTextView
    private lateinit var tv_order_details_total_amount: ClothesTextViewBold

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_order_details)
        toolbar_my_order_details_activity =findViewById(R.id.toolbar_my_order_details_activity)
        tv_order_details_id = findViewById(R.id.tv_order_details_id)
        tv_order_details_date = findViewById(R.id.tv_order_details_date)
        tv_order_status = findViewById(R.id.tv_order_status)
        rv_my_order_items_list = findViewById(R.id.rv_my_order_items_list)
        tv_my_order_details_address_type = findViewById(R.id.tv_my_order_details_address_type)
        tv_my_order_details_full_name = findViewById(R.id.tv_my_order_details_full_name)
        tv_my_order_details_address = findViewById(R.id.tv_my_order_details_address)
        tv_my_order_details_additional_note = findViewById(R.id.tv_my_order_details_additional_note)
        tv_my_order_details_other_details = findViewById(R.id.tv_my_order_details_other_details)
        tv_my_order_details_mobile_number = findViewById(R.id.tv_my_order_details_mobile_number)
        tv_order_details_sub_total = findViewById(R.id.tv_order_details_sub_total)
        tv_order_details_shipping_charge = findViewById(R.id.tv_order_details_shipping_charge)
        tv_order_details_total_amount = findViewById(R.id.tv_order_details_total_amount)

        setupActionBar()
        var myOrderDetails: Order = Order()
        if (intent.hasExtra(Constants.EXTRA_MY_ORDER_DETAILS)) {
            myOrderDetails =
                intent.getParcelableExtra<Order>(Constants.EXTRA_MY_ORDER_DETAILS)!!
        }
        setupUI(myOrderDetails)
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_my_order_details_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }
        toolbar_my_order_details_activity.setNavigationOnClickListener { onBackPressed() }
    }
    private fun setupUI(orderDetails: Order) {

        tv_order_details_id.text = orderDetails.title
        val dateFormat = "dd MMM yyyy HH:mm"
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = orderDetails.order_datetime

        val orderDateTime = formatter.format(calendar.time)
        tv_order_details_date.text = orderDateTime
        val diffInMilliSeconds: Long = System.currentTimeMillis() - orderDetails.order_datetime
        val diffInHours: Long = TimeUnit.MILLISECONDS.toHours(diffInMilliSeconds)
        Log.e("Difference in Hours", "$diffInHours")
        when {
            diffInHours < 1 -> {
                tv_order_status.text = resources.getString(R.string.order_status_pending)
                tv_order_status.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorAccent
                    )
                )
            }
            diffInHours < 2 -> {
                tv_order_status.text = resources.getString(R.string.order_status_in_process)
                tv_order_status.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorOrderStatusInProcess
                    )
                )
            }
            else -> {
                tv_order_status.text = resources.getString(R.string.order_status_delivered)
                tv_order_status.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorOrderStatusDelivered
                    )
                )
            }
        }
        rv_my_order_items_list.layoutManager = LinearLayoutManager(this@MyOrderDetailsActivity)
        rv_my_order_items_list.setHasFixedSize(true)
        val cartListAdapter =
            CartItemsListAdapter(this@MyOrderDetailsActivity, orderDetails.items, false)
        rv_my_order_items_list.adapter = cartListAdapter

        tv_my_order_details_address_type.text = orderDetails.address.type
        tv_my_order_details_full_name.text = orderDetails.address.name
        tv_my_order_details_address.text =
            "${orderDetails.address.address}, ${orderDetails.address.zipCode}"
        tv_my_order_details_additional_note.text = orderDetails.address.additionalNote

        if (orderDetails.address.otherDetails.isNotEmpty()) {
            tv_my_order_details_other_details.visibility = View.VISIBLE
            tv_my_order_details_other_details.text = orderDetails.address.otherDetails
        } else {
            tv_my_order_details_other_details.visibility = View.GONE
        }
        tv_my_order_details_mobile_number.text = orderDetails.address.mobileNumber
        tv_order_details_sub_total.text = orderDetails.sub_total_amount
        tv_order_details_shipping_charge.text = orderDetails.shipping_charge
        tv_order_details_total_amount.text = orderDetails.total_amount
    }


}