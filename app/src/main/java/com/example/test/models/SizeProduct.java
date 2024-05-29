package com.example.test.models;

import android.os.Parcel;
import android.os.Parcelable;

public class SizeProduct implements Parcelable {
    private String size_id;
    private int size;
    private int quantity;
    private String product_id;

    public SizeProduct() {
    }

    public SizeProduct(Parcel in) {
        size_id = in.readString();
        size = in.readInt();
        quantity = in.readInt();
        product_id = in.readString();
    }

    public static final Creator<SizeProduct> CREATOR = new Creator<SizeProduct>() {
        @Override
        public SizeProduct createFromParcel(Parcel in) {
            return new SizeProduct(in);
        }

        @Override
        public SizeProduct[] newArray(int size) {
            return new SizeProduct[size];
        }
    };

    public SizeProduct(String s, Integer sizeInt, Integer quantity, String productId) {
        this.size_id = s;
        this.size = sizeInt;
        this.quantity = quantity;
        this.product_id = productId;
    }

    public String getSize_id() {
        return size_id;
    }

    public void setSize_id(String size_id) {
        this.size_id = size_id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(size_id);
        dest.writeInt(size);
        dest.writeInt(quantity);
        dest.writeString(product_id);
    }
}
