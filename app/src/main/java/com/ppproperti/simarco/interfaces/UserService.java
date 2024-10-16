package com.ppproperti.simarco.interfaces;

import com.ppproperti.simarco.responses.ResponseUser;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.QueryMap;

public interface UserService {
    @FormUrlEncoded
    @POST("api/login")
//    Call<ResponseUser> login(@FORM("email") String email, @Query("password") String password);
    Call<ResponseUser> login(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @PUT("api/users")
    Call<ResponseUser> updateUser(@FieldMap Map<String, String> map);

    @GET("api/users")
    Call<ResponseUser> users(@QueryMap Map<String, String> map);

    @FormUrlEncoded
    @POST("api/users/password")
    Call<ResponseUser> updatePassword(@FieldMap Map<String, String> map);
}
