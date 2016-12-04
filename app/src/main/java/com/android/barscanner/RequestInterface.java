package com.android.barscanner;


import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RequestInterface {
    @GET("barcodeinfo?getBarcodes")
    Call<Result> getBarcodeList();

    @GET("barcodeinfo?getCategoryJSONobj")
    Call<Result> getCatList();

    @Multipart
    @POST("/FileUploadServlet?phonePhoto=1")
    Call<ResponseBody> uploadImage(@Part MultipartBody.Part file);
}
