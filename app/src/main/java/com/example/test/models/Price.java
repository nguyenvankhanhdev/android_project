package com.example.test.models;

import com.example.test.R;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class Price {
    public int getHinhAnhPrice() {
        return hinhAnhPrice;
    }

    public void setHinhAnhPrice(int hinhAnhPrice) {
        this.hinhAnhPrice = hinhAnhPrice;
    }

    public String getTenPrice() {
        return tenPrice;
    }

    public void setTenPrice(String tenPrice) {
        this.tenPrice = tenPrice;
    }

    int hinhAnhPrice;
    String tenPrice;
    public Price(){

    }
    public Price(int hinhAnhPrice, String tenPrice){
        this.hinhAnhPrice = hinhAnhPrice;
        this.tenPrice = tenPrice;
    }
    public static ArrayList<Price> initPrice(){
        ArrayList<Price> priceStrings = new ArrayList<>();
        priceStrings.add(new Price(R.drawable.sort, "Sắp xếp theo"));
        priceStrings.add(new Price(R.drawable.up, "Tăng dần"));
        priceStrings.add(new Price(R.drawable.down, "Giảm dần"));
        return priceStrings;
    }
}
