package com.example.android.repertoire;

import android.graphics.Bitmap;

/**
 * Created by Student on 07/11/2016.
 */

public class ProductImage {


    private Bitmap bmp;
    private String name;
    private int price;
    private int quantity;
    private int size;

    public ProductImage(Bitmap b, String n, int p, int q, int s){

        bmp = b;
        name = n;
        price = p;
        quantity = q;
        size = s;
    }

    public Bitmap getBmp() {
        return bmp;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getSize() {
        return size;
    }
}
