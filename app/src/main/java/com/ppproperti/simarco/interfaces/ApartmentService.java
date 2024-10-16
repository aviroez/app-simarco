package com.ppproperti.simarco.interfaces;

import com.ppproperti.simarco.responses.ResponseApartmentList;
import com.ppproperti.simarco.responses.ResponseApartmentTowerList;
import com.ppproperti.simarco.responses.ResponseApartmentUnitList;
import com.ppproperti.simarco.responses.ResponseApartmentUnitPrice;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface ApartmentService {
    @GET("api/apartments")
    Call<ResponseApartmentList> listApartments(@QueryMap Map<String, String> params);

    @GET("api/apartmenttowers")
    Call<ResponseApartmentTowerList> listApartmentTowers(@QueryMap Map<String, String> params);

    @GET("api/apartmentunits")
    Call<ResponseApartmentUnitList> listApartmentUnits(@QueryMap Map<String, String> params);

    @GET("api/apartmentunits/prices")
    Call<ResponseApartmentUnitPrice> getPrice(@QueryMap Map<String, String> params);
}
