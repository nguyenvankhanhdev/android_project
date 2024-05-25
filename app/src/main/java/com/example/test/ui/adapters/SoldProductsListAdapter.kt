package com.myshoppal.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import com.example.test.models.SoldProduct
import com.example.test.ui.activities.SoldProductDetailsActivity
import com.example.test.utils.Constants
import com.example.test.utils.GlideLoader

open class SoldProductsListAdapter(
    private val context: Context,
    private var list: ArrayList<SoldProduct>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_list_layout,
                parent,
                false
            )
        )
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder) {
            GlideLoader(context).loadProductPicture(
                model.image,
                holder.iv_item_image
            )
            holder.tv_item_name.text = model.title
            holder.tv_item_price.text = "$${model.price}"
            holder.tv_cart_item_size.text = "Size: ${model.size}"
            holder.ib_delete_product.visibility = View.GONE
            holder.ib_edit_product.visibility = View.GONE
            holder.itemView.setOnClickListener {
                val intent = Intent(context, SoldProductDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_SOLD_PRODUCT_DETAILS, model)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iv_item_image: ImageView = itemView.findViewById(R.id.iv_item_image)
        val tv_item_name: TextView = itemView.findViewById(R.id.tv_item_name)
        val tv_item_price: TextView = itemView.findViewById(R.id.tv_item_price)
        val ib_delete_product: ImageButton = itemView.findViewById(R.id.ib_delete_product)
        val tv_cart_item_size: TextView = itemView.findViewById(R.id.tv_cart_item_size)
        val ib_edit_product: ImageButton = itemView.findViewById(R.id.ib_edit_product)
    }
}