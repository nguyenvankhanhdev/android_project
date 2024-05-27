package com.example.test.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.test.R;
import com.example.test.models.Type;
import com.example.test.ui.fragment.DashboardFragment;

import java.util.ArrayList;

public class AdapterType extends BaseAdapter {
    LayoutInflater layoutInflater;
    ArrayList<Type> arrayType;
    int layoutItem;
    public AdapterType(DashboardFragment context, int
            layoutItem, ArrayList<Type> arrayType) {
        this.layoutInflater = context.getLayoutInflater();
        this.arrayType = arrayType;
        this.layoutItem = layoutItem;
    }
    @Override
    public int getCount() {
        return this.arrayType.size();
    }

    @Override
    public Object getItem(int position) {
        return this.arrayType.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Type type = arrayType.get(position);
        View rowView = layoutInflater.inflate(layoutItem,null,true);
        TextView tvName = (TextView)
                rowView.findViewById(R.id.txt_type);
        tvName.setText(type.getTenType());
        return rowView;
    }
}
