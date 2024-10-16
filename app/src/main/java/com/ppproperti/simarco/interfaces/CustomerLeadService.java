package com.ppproperti.simarco.interfaces;

import com.ppproperti.simarco.responses.ResponseCustomerLead;
import com.ppproperti.simarco.responses.ResponseCustomerLeadList;
import com.ppproperti.simarco.responses.ResponseCustomerLeadListPagination;

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

public interface CustomerLeadService {
    @FormUrlEncoded
    @POST("api/customerleads")
    Call<ResponseCustomerLead> addCustomerLeads(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @PUT("api/customerleads/{id}")
    Call<ResponseCustomerLead> updateCustomerLead(@Path("id") int id, @FieldMap Map<String, String> map);

    @GET("api/customerleads")
    Call<ResponseCustomerLeadList> get(@QueryMap Map<String, String> map);

    @GET("api/customerleads")
    Call<ResponseCustomerLeadListPagination> get(@QueryMap Map<String, String> map, @Query("limit") int limit);

    @GET("api/customerleads/{id}")
    Call<ResponseCustomerLead> show(@Path("id") int id, @QueryMap Map<String, String> map);

    @GET("api/customerleads/check")
    Call<ResponseCustomerLead> check(@QueryMap Map<String, String> map);
}