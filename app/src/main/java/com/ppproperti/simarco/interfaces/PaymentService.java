package com.ppproperti.simarco.interfaces;

import com.ppproperti.simarco.responses.ResponsePaymentSchemaCalculation;
import com.ppproperti.simarco.responses.ResponsePaymentSchemaList;
import com.ppproperti.simarco.responses.ResponsePaymentTypeList;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface PaymentService {
    @GET("api/paymenttypes")
    Call<ResponsePaymentTypeList> listPaymentTypes(@QueryMap Map<String, String> params);

    @GET("api/paymentschemas")
    Call<ResponsePaymentSchemaList> listPaymentSchemas(@QueryMap Map<String, String> params);

    @GET("api/paymentschemas/{paymentschemas}/calculate")
    Call<ResponsePaymentSchemaCalculation> calculate(@Path("paymentschemas") int id, @QueryMap Map<String, String> params);
}
