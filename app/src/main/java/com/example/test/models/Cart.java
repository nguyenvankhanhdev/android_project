package com.example.test.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Cart implements Parcelable {

    private String user_id;
    private String product_id;
    private String title;
    private String price;
    private String image;
    private String cart_quantity;
    private String size;
    private String stock_quantity;
    private String id;
    private boolean isChecked = false;

    public Cart(String user_id, String product_id, String title, String price, String image, String cart_quantity, String size, String stock_quantity, String id) {
        this.user_id = user_id;
        this.product_id = product_id;
        this.title = title;
        this.price = price;
        this.image = image;
        this.cart_quantity = cart_quantity;
        this.size = size;
        this.stock_quantity = "";
        this.id = "";
    }

    public Cart(String user_id, String product_id, String title, String price, String image, String defaultCartQuantity, String selectedSize) {
        this.user_id = user_id;
        this.product_id = product_id;
        this.title = title;
        this.price = price;
        this.image = image;
        this.cart_quantity = defaultCartQuantity;
        this.size = selectedSize;
    }

    public Cart(String currentUserID, String mProductId, String title, String price, String image, boolean isChecked,  String defaultCartQuantity, String stock_quantity, String selectedSize) {
        this.user_id = currentUserID;
        this.product_id = mProductId;
        this.title = title;
        this.price = price;
        this.image = image;
        this.isChecked = isChecked;
        this.cart_quantity = defaultCartQuantity;
        this.stock_quantity = stock_quantity;
        this.size = selectedSize;
    }

    public Cart() {
    }

    protected Cart(Parcel in) {
        user_id = in.readString();
        product_id = in.readString();
        title = in.readString();
        price = in.readString();
        image = in.readString();
        cart_quantity = in.readString();
        size = in.readString();
        stock_quantity = in.readString();
        id = in.readString();
    }

    public static final Creator<Cart> CREATOR = new Creator<Cart>() {
        @Override
        public Cart createFromParcel(Parcel in) {
            return new Cart(in);
        }

        @Override
        public Cart[] newArray(int size) {
            return new Cart[size];
        }
    };

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCart_quantity() {
        return cart_quantity;
    }

    public void setCart_quantity(String cart_quantity) {
        this.cart_quantity = cart_quantity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getStock_quantity() {
        return stock_quantity;
    }

    public void setStock_quantity(String stock_quantity) {
        this.stock_quantity = stock_quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user_id);
        dest.writeString(product_id);
        dest.writeString(title);
        dest.writeString(price);
        dest.writeString(image);
        dest.writeString(cart_quantity);
        dest.writeString(size);
        dest.writeString(stock_quantity);
        dest.writeString(id);
    }
}
