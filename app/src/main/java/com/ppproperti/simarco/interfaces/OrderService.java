package com.ppproperti.simarco.interfaces;

import com.ppproperti.simarco.responses.ResponseOrder;
import com.ppproperti.simarco.responses.ResponseOrderList;
import com.ppproperti.simarco.responses.ResponseOrderListPagination;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface OrderService {
    @GET("api/orders")
    Call<ResponseOrderListPagination> orders(@QueryMap Map<String, String> map, @Query("page") int page);

    @FormUrlEncoded
    @POST("api/orders")
    Call<ResponseOrder> order(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("api/orders/{id}/installments")
    Call<ResponseOrder> installments(@Path("id") int id, @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("api/orders/{id}/identity")
    Call<ResponseOrder> identity(@Path("id") int id, @FieldMap Map<String, String> map);

    @FormUrlEncoded
    @POST("api/orders/{id}/process")
    Call<ResponseOrder> process(@Path("id") int id, @FieldMap Map<String, String> map);

    @GET("api/orders/{id}")
    Call<ResponseOrder> show(@Path("id") int id, @QueryMap Map<String, String> map);

    @GET("api/orders/last")
    Call<ResponseOrder> last(@QueryMap Map<String, String> map);

    @DELETE("api/orders/{id}")
    Call<ResponseOrder> delete(@Path("id") int id);
}
