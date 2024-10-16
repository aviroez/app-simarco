package com.ppproperti.simarco.interfaces;

import com.ppproperti.simarco.responses.ResponseCustomer;
import com.ppproperti.simarco.responses.ResponseCustomerList;
import com.ppproperti.simarco.responses.ResponseCustomerListPagination;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface CustomerService {
    @FormUrlEncoded
    @POST("api/customers")
    Call<ResponseCustomer> addCustomers(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @PUT("api/customers/{id}")
    Call<ResponseCustomer> updateCustomer(@Path("id") int id, @FieldMap Map<String, String> map);

    @GET("api/customers")
    Call<ResponseCustomerList> customers(@QueryMap Map<String, String> map);

    @GET("api/customers")
    Call<ResponseCustomerListPagination> customers(@QueryMap Map<String, String> map, @Query("limit") int limit);

    @GET("api/customers/{id}")
    Call<ResponseCustomer> customer(@Path("id") int id, @QueryMap Map<String, String> map);

    @GET("api/customers/check")
    Call<ResponseCustomer> check(@QueryMap Map<String, String> map);
}
