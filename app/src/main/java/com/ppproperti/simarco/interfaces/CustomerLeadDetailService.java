package com.ppproperti.simarco.interfaces;

import com.ppproperti.simarco.responses.ResponseCustomerLead;
import com.ppproperti.simarco.responses.ResponseCustomerLeadDetail;
import com.ppproperti.simarco.responses.ResponseCustomerLeadDetailList;
import com.ppproperti.simarco.responses.ResponseCustomerLeadList;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface CustomerLeadDetailService {
    @FormUrlEncoded
    @POST("api/customerleaddetails")
    Call<ResponseCustomerLeadDetail> add(@FieldMap Map<String, String> map);

    @FormUrlEncoded
    @PUT("api/customerleaddetails/{id}")
    Call<ResponseCustomerLeadDetail> update(@Path("id") int id, @FieldMap Map<String, String> map);

    @GET("api/customerleaddetails")
    Call<ResponseCustomerLeadDetailList> get(@QueryMap Map<String, String> map);

    @GET("api/customerleaddetails/{id}")
    Call<ResponseCustomerLeadDetail> show(@Path("id") int id, @QueryMap Map<String, String> map);
}
