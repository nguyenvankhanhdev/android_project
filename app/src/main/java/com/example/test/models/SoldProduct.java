package com.example.test.models;

import android.os.Parcel;
import android.os.Parcelable;

public class SoldProduct implements Parcelable {
    private String user_id;
    private String title;
    private String price;
    private String sold_quantity;
    private String size;
    private String image;
    private String order_id;
    private long order_date;
    private String sub_total_amount;
    private String shipping_charge;
    private String total_amount;
    private Address address;
    private String id;

    public SoldProduct() {
    }

    protected SoldProduct(Parcel in) {
        user_id = in.readString();
        title = in.readString();
        price = in.readString();
        sold_quantity = in.readString();
        size = in.readString();
        image = in.readString();
        order_id = in.readString();
        order_date = in.readLong();
        sub_total_amount = in.readString();
        shipping_charge = in.readString();
        total_amount = in.readString();
        address = in.readParcelable(Address.class.getClassLoader());
        id = in.readString();
    }

    public static final Creator<SoldProduct> CREATOR = new Creator<SoldProduct>() {
        @Override
        public SoldProduct createFromParcel(Parcel in) {
            return new SoldProduct(in);
        }

        @Override
        public SoldProduct[] newArray(int size) {
            return new SoldProduct[size];
        }
    };

    public SoldProduct(String currentUserID, String title, String price, String soldQuantity, String image, String size, String orderId, long orderDate, String subTotalAmount, String shippingCharge, String totalAmount, Address address) {
        this.user_id = currentUserID;
        this.title = title;
        this.price = price;
        this.sold_quantity = soldQuantity;
        this.image = image;
        this.order_id = orderId;
        this.size = size;
        this.order_date = orderDate;
        this.sub_total_amount = subTotalAmount;
        this.shipping_charge = shippingCharge;
        this.total_amount = totalAmount;
        this.address = address;
    }

//    public SoldProduct(String currentUserID, String title, String price, String cartQuantity, String image, String size, String title1, long orderDatetime, String subTotalAmount, String shippingCharge, String totalAmount, Address address) {
//
//    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public String getSold_quantity() {
        return sold_quantity;
    }

    public void setSold_quantity(String sold_quantity) {
        this.sold_quantity = sold_quantity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public long getOrder_date() {
        return order_date;
    }

    public void setOrder_date(long order_date) {
        this.order_date = order_date;
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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
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
        dest.writeString(title);
        dest.writeString(price);
        dest.writeString(sold_quantity);
        dest.writeString(size);
        dest.writeString(image);
        dest.writeString(order_id);
        dest.writeLong(order_date);
        dest.writeString(sub_total_amount);
        dest.writeString(shipping_charge);
        dest.writeString(total_amount);
        dest.writeParcelable(address, flags);
        dest.writeString(id);
    }
}
