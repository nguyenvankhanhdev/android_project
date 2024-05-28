package com.example.test.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.firestoreclass.FirestoreClass;
import com.example.test.firestoreclass.FirestoreClassKT;
import com.example.test.models.Cart;
import com.example.test.ui.activities.CartListActivity;
import com.example.test.utils.Constants;
import com.example.test.utils.GlideLoader;

import java.util.ArrayList;
import java.util.HashMap;

public class CartItemsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final ArrayList<Cart> list;
    private final boolean updateCartItems;
    private OnCheckboxChangedListener checkboxListener;

    public CartItemsListAdapter(Context context, ArrayList<Cart> list, boolean updateCartItems, OnCheckboxChangedListener checkboxListener) {
        this.context = context;
        this.list = list;
        this.updateCartItems = updateCartItems;
        this.checkboxListener = checkboxListener;
    }

    public CartItemsListAdapter(Context context, ArrayList<Cart> list, boolean updateCartItems) {
        this.context = context;
        this.list = list;
        this.updateCartItems = updateCartItems;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart_layout, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Cart model = list.get(position);

        if (holder instanceof MyViewHolder) {
            MyViewHolder viewHolder = (MyViewHolder) holder;

            GlideLoader glideLoader = new GlideLoader(context);
            glideLoader.loadProductPicture(model.getImage(), viewHolder.iv_cart_item_image);

            viewHolder.tv_cart_item_title.setText(model.getTitle());
            viewHolder.tv_cart_item_price.setText("Price: " + model.getPrice() + " $");
            viewHolder.tv_cart_quantity.setText(model.getCart_quantity());
            viewHolder.tv_cart_item_size.setText("Size: " + model.getSize());

            if (model.getCart_quantity().equals("0")) {
                viewHolder.ib_remove_cart_item.setVisibility(View.GONE);
                viewHolder.ib_add_cart_item.setVisibility(View.GONE);

                if (updateCartItems) {
                    viewHolder.ib_delete_cart_item.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.ib_delete_cart_item.setVisibility(View.GONE);
                }

                viewHolder.tv_cart_quantity.setText(context.getResources().getString(R.string.lbl_out_of_stock));
                viewHolder.tv_cart_quantity.setTextColor(ContextCompat.getColor(context, R.color.colorSnackBarError));
            } else {
                if (updateCartItems) {
                    viewHolder.ib_remove_cart_item.setVisibility(View.VISIBLE);
                    viewHolder.ib_add_cart_item.setVisibility(View.VISIBLE);
                    viewHolder.ib_delete_cart_item.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.ib_remove_cart_item.setVisibility(View.GONE);
                    viewHolder.ib_add_cart_item.setVisibility(View.GONE);
                    viewHolder.ib_delete_cart_item.setVisibility(View.GONE);
                    viewHolder.checkBox.setVisibility(View.GONE);
                }
                viewHolder.tv_cart_quantity.setTextColor(ContextCompat.getColor(context, R.color.colorSecondaryText));
            }

            viewHolder.checkBox.setChecked(model.isChecked());
            viewHolder.checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                model.setChecked(isChecked);
                FirestoreClass.updateCheckboxStateInFirebase(model.getId(), isChecked);
                checkboxListener.onCheckboxChanged(model, isChecked);
            });


            viewHolder.ib_remove_cart_item.setOnClickListener(v -> {
                if (model.getCart_quantity().equals("1")) {
                    FirestoreClass.removeItemFromCart(context, model.getId());
                } else {
                    int cartQuantity = Integer.parseInt(model.getCart_quantity());
                    HashMap<String, Object> itemHashMap = new HashMap<>();
                    itemHashMap.put(Constants.CART_QUANTITY, String.valueOf(cartQuantity - 1));

//                    if (context instanceof CartListActivity) {
//                        ((CartListActivity) context).showProgressDialog(context.getResources().getString(R.string.please_wait));
//                    }

                    FirestoreClass.updateMyCart(context, model.getId(), itemHashMap);
                }
            });

            viewHolder.ib_add_cart_item.setOnClickListener(v -> {
                int cartQuantity = Integer.parseInt(model.getCart_quantity());
                int stockQuantity = Integer.parseInt(model.getStock_quantity());

                if (cartQuantity < stockQuantity) {
                    HashMap<String, Object> itemHashMap = new HashMap<>();
                    itemHashMap.put(Constants.CART_QUANTITY, String.valueOf(cartQuantity + 1));

//                    if (context instanceof CartListActivity) {
//                        ((CartListActivity) context).showProgressDialog(context.getResources().getString(R.string.please_wait));
//                    }

                    FirestoreClass.updateMyCart(context, model.getId(), itemHashMap);
                } else {
                    if (context instanceof CartListActivity) {
                        ((CartListActivity) context).showErrorSnackBar(context.getResources().getString(R.string.msg_for_available_stock, model.getStock_quantity()), true);
                    }
                }
            });

            viewHolder.ib_delete_cart_item.setOnClickListener(v -> {
                if (context instanceof CartListActivity) {
                    ((CartListActivity) context).showProgressDialog(context.getResources().getString(R.string.please_wait));
                }

                FirestoreClass.removeItemFromCart(context, model.getId());
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnCheckboxChangedListener {
        void onCheckboxChanged(Cart cart, boolean isChecked);
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_cart_item_image;
        TextView tv_cart_item_title, tv_cart_item_price, tv_cart_quantity, tv_cart_item_size;
        ImageButton ib_remove_cart_item, ib_add_cart_item, ib_delete_cart_item;
        CheckBox checkBox;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv_cart_item_image = itemView.findViewById(R.id.iv_cart_item_image);
            tv_cart_item_title = itemView.findViewById(R.id.tv_cart_item_title);
            tv_cart_item_price = itemView.findViewById(R.id.tv_cart_item_price);
            tv_cart_quantity = itemView.findViewById(R.id.tv_cart_quantity);
            tv_cart_item_size = itemView.findViewById(R.id.tv_cart_item_size);
            ib_remove_cart_item = itemView.findViewById(R.id.ib_remove_cart_item);
            ib_add_cart_item = itemView.findViewById(R.id.ib_add_cart_item);
            ib_delete_cart_item = itemView.findViewById(R.id.ib_delete_cart_item);
            checkBox = itemView.findViewById(R.id.payment_check);
        }
    }
}
