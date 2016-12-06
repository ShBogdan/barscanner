package com.android.barscanner;


import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;

public interface RequestInterface {
    @GET("barcodeinfo?getBarcodes")
    Call<Result> getBarcodeList();

    @GET("barcodeinfo?getCategoryJSONobj")
    Call<Result> getCatList();

    @GET("barcodeinfo?createProdPhone&pass=androidapppass")
    Call<ResponseBody> createNewProd(@QueryMap Map<String, String> options);

    @Multipart
    @POST("/barcodeserver/FileUploadServlet?phonePhoto=1")
    Call<ResponseBody> uploadImage(@Part MultipartBody.Part file);
}

