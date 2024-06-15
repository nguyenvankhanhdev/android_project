package com.example.test.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.models.Banner;
import com.example.test.models.Product;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MultiTypeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_BANNER = 0;
    private static final int VIEW_TYPE_TITLE = 1;
    private static final int VIEW_TYPE_PRODUCT_LIST = 2;

    private final List<Object> items;
    private final Context context;

    public MultiTypeAdapter(Context context, List<Object> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof Banner) {
            return VIEW_TYPE_BANNER;
        } else if (items.get(position) instanceof String) {
            return VIEW_TYPE_TITLE;
        } else if (items.get(position) instanceof List) {
            return VIEW_TYPE_PRODUCT_LIST;
        }
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_BANNER:
                View bannerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
                return new BannerViewHolder(bannerView);
            case VIEW_TYPE_TITLE:
                View titleView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_title, parent, false);
                return new TitleViewHolder(titleView);
            case VIEW_TYPE_PRODUCT_LIST:
                View productListView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_list, parent, false);
                return new ProductListViewHolder(productListView);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_BANNER:
                Banner banner = (Banner) items.get(position);
                ((BannerViewHolder) holder).bind(banner);
                break;
            case VIEW_TYPE_TITLE:
                String title = (String) items.get(position);
                ((TitleViewHolder) holder).bind(title);
                break;
            case VIEW_TYPE_PRODUCT_LIST:
                List<Product> productList = (List<Product>) items.get(position);
                ((ProductListViewHolder) holder).bind(productList);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        private final ImageView ivBanner;

        BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBanner = itemView.findViewById(R.id.iv_banner);
        }

        void bind(Banner banner) {
            Picasso.get().load(banner.getImageUrl()).into(ivBanner);
        }
    }

    static class TitleViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;

        TitleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(android.R.id.text1);
        }

        void bind(String title) {
            tvTitle.setText(title);
        }
    }

    static class ProductListViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerView rvProductList;

        ProductListViewHolder(@NonNull View itemView) {
            super(itemView);
            rvProductList = itemView.findViewById(R.id.rv_product_list);
        }

        void bind(List<Product> productList) {
            rvProductList.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            ProductAdapter productAdapter = new ProductAdapter(productList);
            rvProductList.setAdapter(productAdapter);
        }
    }
}

