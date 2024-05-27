package com.example.test.models;

public class Type {
    public String getTypeID() {
        return typeID;
    }

    public void setTypeID(String typeID) {
        this.typeID = typeID;
    }

    public String getTenType() {
        return tenType;
    }

    public void setTenType(String tenType) {
        this.tenType = tenType;
    }

    String typeID;
    String tenType;
    public Type(){

    }
    public Type(String typeID, String tenType){
        this.typeID = typeID;
        this.tenType = tenType;
    }
}
