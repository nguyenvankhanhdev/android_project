package com.example.test.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.models.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private final List<Product> productList;

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_list, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivProduct;
        private final TextView tvName;
        private final TextView tvPrice;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.iv_dashboard_item_image);
            tvName = itemView.findViewById(R.id.tv_item_name);
            tvPrice = itemView.findViewById(R.id.tv_dashboard_item_price);
        }

        void bind(Product product) {
            tvName.setText(product.getTitle());
            tvPrice.setText(String.valueOf(product.getPrice()));
            Picasso.get().load(product.getImage()).into(ivProduct);
        }
    }
}

