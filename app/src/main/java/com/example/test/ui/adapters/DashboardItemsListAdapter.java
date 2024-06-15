package com.example.test.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.test.R;
import com.example.test.models.Product;
import com.example.test.models.ProductQuantity;
import com.example.test.utils.GlideLoader;

import java.util.ArrayList;
import java.util.List;

public class DashboardItemsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private final Context context;
    private ArrayList<Product> list;
    ArrayList<Product> products = new ArrayList<>();
    private ArrayList<ProductQuantity> list1;
    ArrayList<ProductQuantity> products1 = new ArrayList<>();

    private OnClickListener onClickListener;

    public DashboardItemsListAdapter(Context context, ArrayList<Product> list) {
        this.context = context;
        this.list = list;
        this.products = list;
    }
    public DashboardItemsListAdapter(Context context, ArrayList<ProductQuantity> list1, int a) {
        this.context = context;
        this.list1 = list1;
        a = 1;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    // Search product
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String strSearch = constraint.toString();
                if(strSearch.isEmpty()){
                    list = products;
                }
                else{
                    ArrayList<Product> newProduct = new ArrayList<>();
                    for (Product item : products){
                        if(item.getTitle().toLowerCase().contains(strSearch.toLowerCase())){
                            newProduct.add(item);
                        }
                    }
                    list = newProduct;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = list;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                list = (ArrayList<Product>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface OnClickListener {
        void onClick(int position, Product product);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dashboard_layout, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Product model = list.get(position);

        if (holder instanceof MyViewHolder) {
            MyViewHolder myViewHolder = (MyViewHolder) holder;

            new GlideLoader(context).loadProductPicture(model.getImage(), myViewHolder.iv_dashboard_item_image);
            myViewHolder.tv_dashboard_item_title.setText(model.getTitle());
            myViewHolder.tv_dashboard_item_price.setText("$" + model.getPrice());

            holder.itemView.setOnClickListener(v -> {
                if (onClickListener != null) {
                    onClickListener.onClick(position, model);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_dashboard_item_image;
        public TextView tv_dashboard_item_title;
        public TextView tv_dashboard_item_price;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_dashboard_item_image = itemView.findViewById(R.id.iv_dashboard_item_image);
            tv_dashboard_item_title = itemView.findViewById(R.id.tv_dashboard_item_title);
            tv_dashboard_item_price = itemView.findViewById(R.id.tv_dashboard_item_price);
        }
    }
}
