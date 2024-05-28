package com.example.test.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Order implements Parcelable {
    private String user_id;
    private ArrayList<Cart> items;
    private Address address;
    private String title;
    private String image;
    private String size;
    private String sub_total_amount;
    private String shipping_charge;
    private String total_amount;
    private long order_datetime;
    private String id;

    public Order() {
    }

    protected Order(Parcel in) {
        user_id = in.readString();
        items = in.createTypedArrayList(Cart.CREATOR);
        address = in.readParcelable(Address.class.getClassLoader());
        title = in.readString();
        image = in.readString();
        size = in.readString();
        sub_total_amount = in.readString();
        shipping_charge = in.readString();
        total_amount = in.readString();
        order_datetime = in.readLong();
        id = in.readString();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public Order(String currentUserID, ArrayList<Cart> mCartItemsList, Address mAddressDetails, String title, String image, String size, String sub_total_amount, String shipping_charge, String total_amount) {
        this.user_id = currentUserID;
        this.items = mCartItemsList;
        this.address = mAddressDetails;
        this.title = title;
        this.image = image;
        this.sub_total_amount = sub_total_amount;
        this.shipping_charge = shipping_charge;
        this.total_amount = total_amount;
        this.order_datetime = System.currentTimeMillis();
        this.size = size;
        this.id = "";
    }

    public Order(String currentUserID, ArrayList<Cart> mCartItemsList, Address mAddressDetails, String title, String image, String size, String sub_total_amount, String shipping_charge, String total_amount, long order_datetime, String id) {
        this.user_id = currentUserID;
        this.items = mCartItemsList;
        this.address = mAddressDetails;
        this.title = title;
        this.image = image;
        this.size = size;
        this.sub_total_amount = sub_total_amount;
        this.shipping_charge = shipping_charge;
        this.total_amount = total_amount;
        this.order_datetime = order_datetime;
        this.id = id;
    }



    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public ArrayList<Cart> getItems() {
        return items;
    }

    public void setItems(ArrayList<Cart> items) {
        this.items = items;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSub_total_amount() {
        return sub_total_amount;
    }

    public void setSub_total_amount(String sub_total_amount) {
        this.sub_total_amount = sub_total_amount;
    }

    public String getShipping_charge() {
        return shipping_charge;
    }

    public void setShipping_charge(String shipping_charge) {
        this.shipping_charge = shipping_charge;
    }

    public String getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(String total_amount) {
        this.total_amount = total_amount;
    }

    public long getOrder_datetime() {
        return order_datetime;
    }

    public void setOrder_datetime(long order_datetime) {
        this.order_datetime = order_datetime;
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
        dest.writeTypedList(items);
        dest.writeParcelable(address, flags);
        dest.writeString(title);
        dest.writeString(image);
        dest.writeString(size);
        dest.writeString(sub_total_amount);
        dest.writeString(shipping_charge);
        dest.writeString(total_amount);
        dest.writeLong(order_datetime);
        dest.writeString(id);
    }
}
