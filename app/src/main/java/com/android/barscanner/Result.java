package com.android.barscanner;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Result {

    @SerializedName("barcodes")
    @Expose
    private List<List<String>> barcodes = new ArrayList<List<String>>();

    public List<List<String>> getBarcodes() {
        return barcodes;
    }

    @SerializedName("newproducts")
    @Expose
    private List<List<String>> newBarcodes = new ArrayList<List<String>>();

    public List<List<String>> getNewBarcodes() {
        return newBarcodes;
    }



    @SerializedName("category")
    @Expose
    private List<List<String>> category = new ArrayList<List<String>>();

    public List<List<String>> getCategorys() {
        return category;
    }


}
