package com.dragonide.raitplacementcell;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by Ankit on 7/5/2017.
 */
public interface FileUploadService {
    @Multipart
    @POST("academic.php")
   // @POST("hack.php")
    Call<ResponseBody> uploadMultipleFilesDynamic(
           // @Part("description") RequestBody description,
            @Part List<MultipartBody.Part> files);
}