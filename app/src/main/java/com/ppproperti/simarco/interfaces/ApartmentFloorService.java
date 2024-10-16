package com.ppproperti.simarco.interfaces;

import com.ppproperti.simarco.responses.ResponseApartmentFloorList;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface ApartmentFloorService {
    @GET("api/apartmentfloors")
    Call<ResponseApartmentFloorList> index(@QueryMap Map<String, String> params);

    @GET("api/apartmentfloors/populate")
    Call<ResponseApartmentFloorList> populate(@QueryMap Map<String, String> params);
}