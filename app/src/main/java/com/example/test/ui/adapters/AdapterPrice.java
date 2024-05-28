package com.example.test.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.test.R;
import com.example.test.models.Price;
import com.example.test.ui.fragment.DashboardFragment;

import java.util.ArrayList;

public class AdapterPrice extends BaseAdapter {

    LayoutInflater layoutInflater;
    ArrayList<Price> arrayListPrice;
    int layoutItem;
    public AdapterPrice(DashboardFragment context, int
            layoutItem, ArrayList<Price> arrayListPrice) {
        this.layoutInflater = context.getLayoutInflater();
        this.arrayListPrice = arrayListPrice;
        this.layoutItem = layoutItem;
    }
    @Override
    public int getCount() {
        return this.arrayListPrice.size();
    }

    @Override
    public Object getItem(int position) {
        return this.arrayListPrice.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Price price = arrayListPrice.get(position);
        View rowView = layoutInflater.inflate(layoutItem,null,true);
        ImageView imageView = (ImageView)
                rowView.findViewById(R.id.img_price_sort);
        imageView.setImageResource(price.getHinhAnhPrice());
        TextView tvName = (TextView)
                rowView.findViewById(R.id.txt_price_sort);
        tvName.setText(price.getTenPrice());

        return rowView;
    }
}
