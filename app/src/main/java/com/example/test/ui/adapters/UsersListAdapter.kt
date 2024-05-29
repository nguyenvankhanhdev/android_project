package com.example.test.ui.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import com.example.test.models.User
import com.example.test.ui.activities.UserProfileActivity
import com.example.test.ui.fragment.UsersFragment
import com.example.test.utils.ClothesTextView
import com.example.test.utils.ClothesTextViewBold
import com.example.test.utils.Constants
import com.example.test.utils.GlideLoader

open class UsersListAdapter (
        private val context: Context,
        private var list: ArrayList<User>,
        private val fragment: UsersFragment,
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
                        if (model.image != null) {
                                GlideLoader(context).loadUserPicture(model.image, holder.itemImage)
                        } else {
                                // Xử lý khi image là null, ví dụ hiển thị ảnh mặc định
                                // holder.itemImage.setImageResource(R.drawable.default_image)
                        }
                        holder.itemName.text = model.email
                        holder.itemPrice.text = model.firstName + model.lastName
                        //holder.deleteButton.visibility = View.GONE
                        holder.deleteButton.setOnClickListener{
                                val alertDialogBuilder = AlertDialog.Builder(context)
                                alertDialogBuilder.apply {
                                        setTitle("Xác nhận xóa")
                                        setMessage("Bạn có chắc chắn muốn xóa sản phẩm này không?")
                                        setPositiveButton("Có") { dialog, which ->
                                                // Gọi hàm xóa sản phẩm từ fragment ở đây
                                                fragment.activeDeleteUser(model.email)
                                        }
                                        setNegativeButton("Không") { dialog, which ->
                                                // Không làm gì cả
                                        }
                                        show()
                                }
                        }
                        holder.itemView.setOnClickListener {
                                val intent = Intent(context, UserProfileActivity::class.java)
                                intent.putExtra(Constants.EXTRA_USER_DETAILS, model)
                                context.startActivity(intent)
                        }
                        holder.editButton.visibility = View.GONE
                        holder.editButton.setOnClickListener {
                                val intent = Intent(context, UserProfileActivity::class.java)
                                intent.putExtra(Constants.EXTRA_USER_DETAILS, model.id)
                                context.startActivity(intent)
                        }
                }
        }

        override fun getItemCount(): Int {
                return list.size
        }
        class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
                val itemImage: ImageView = itemView.findViewById(R.id.iv_item_image)
                val itemName: ClothesTextViewBold = itemView.findViewById(R.id.tv_item_name)
                val itemPrice: ClothesTextView = itemView.findViewById(R.id.tv_item_price)
                val deleteButton: ImageButton = itemView.findViewById(R.id.ib_delete_product)
                val editButton :ImageButton = itemView.findViewById(R.id.ib_edit_product)
        }

}