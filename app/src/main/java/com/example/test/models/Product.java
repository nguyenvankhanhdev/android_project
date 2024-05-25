package com.example.test.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    private String product_id;
    private String title;
    private String price;
    private String description;
    private String image;

    public Product() {
    }

    protected Product(Parcel in) {
        product_id = in.readString();
        title = in.readString();
        price = in.readString();
        description = in.readString();
        image = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public Product(String s, String product_title, String price, String description, String mProductImageURL) {
        this.product_id = s;
        this.title = product_title;
        this.price = price;
        this.description = description;
        this.image = mProductImageURL;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(product_id);
        dest.writeString(title);
        dest.writeString(price);
        dest.writeString(description);
        dest.writeString(image);
    }
}
