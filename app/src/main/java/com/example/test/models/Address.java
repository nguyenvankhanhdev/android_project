package com.example.test.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Address implements Parcelable {
    private String user_id;
    private String name;
    private String mobileNumber;
    private String address;
    private String zipCode;
    private String additionalNote;
    private String type;
    private String otherDetails;
    private String id;

    public Address() {
    }

    protected Address(Parcel in) {
        user_id = in.readString();
        name = in.readString();
        mobileNumber = in.readString();
        address = in.readString();
        zipCode = in.readString();
        additionalNote = in.readString();
        type = in.readString();
        otherDetails = in.readString();
        id = in.readString();
    }

    public static final Creator<Address> CREATOR = new Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel in) {
            return new Address(in);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

    public Address(String currentUserID, String fullName, String phoneNumber, String address, String zipCode, String additionalNote, String addressType, String otherDetails) {
        this.user_id = currentUserID;
        this.name = fullName;
        this.mobileNumber = phoneNumber;
        this.address = address;
        this.zipCode = zipCode;
        this.additionalNote = additionalNote;
        this.type = addressType;
        this.otherDetails = otherDetails;
    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(String additionalNote) {
        this.additionalNote = additionalNote;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOtherDetails() {
        return otherDetails;
    }

    public void setOtherDetails(String otherDetails) {
        this.otherDetails = otherDetails;
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
        dest.writeString(name);
        dest.writeString(mobileNumber);
        dest.writeString(address);
        dest.writeString(zipCode);
        dest.writeString(additionalNote);
        dest.writeString(type);
        dest.writeString(otherDetails);
        dest.writeString(id);
    }
}
