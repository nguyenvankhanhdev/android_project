package com.example.test.ui.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import com.example.test.models.Address
import com.example.test.ui.activities.AddEditAddressActivity
import com.example.test.ui.activities.CheckoutActivity
import com.example.test.utils.Constants


open class AddressListAdapter(
    private val context: Context,
    private var list: ArrayList<Address>,
    private val selectAddress: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_address_layout,
                parent,
                false
            )
        )
    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            holder.tv_address_full_name.text = model.name
            holder.tv_address_type.text = model.type
            holder.tv_address_details.text = "${model.address}, ${model.zipCode}"
            holder.tv_address_mobile_number.text = model.mobileNumber
            if (selectAddress) {
                holder.itemView.setOnClickListener {
                   val intent = Intent(context, CheckoutActivity::class.java)
                    intent.putExtra(Constants.EXTRA_SELECTED_ADDRESS, model)
                    context.startActivity(intent)
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tv_address_full_name:TextView =itemView.findViewById(R.id.tv_address_full_name)
        val tv_address_type:TextView =itemView.findViewById(R.id.tv_address_type)
        val tv_address_details:TextView =itemView.findViewById(R.id.tv_address_details)
        val tv_address_mobile_number:TextView =itemView.findViewById(R.id.tv_address_mobile_number)
    }
    fun notifyEditItem(activity: Activity, position: Int) {
        val intent = Intent(context, AddEditAddressActivity::class.java)
        // TODO Step 6: Pass the address details through intent to edit the address.
        // START
        intent.putExtra(Constants.EXTRA_ADDRESS_DETAILS, list[position])
        // END
        activity.startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)

        notifyItemChanged(position) // Notify any registered observers that the item at position has changed.
    }


}