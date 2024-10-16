package com.ppproperti.simarco.interfaces;

import com.ppproperti.simarco.responses.ResponseApartmentUnit;
import com.ppproperti.simarco.responses.ResponseApartmentUnitList;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface ApartmentUnitService {
    @GET("api/apartmentunits")
    Call<ResponseApartmentUnitList> index(@QueryMap Map<String, String> params);

    @GET("api/apartmentunits/{id}")
    Call<ResponseApartmentUnit> get(@Path("id") int id, @QueryMap Map<String, String> params);

    @GET("api/apartmentunits/number")
    Call<ResponseApartmentUnitList> number(@QueryMap Map<String, String> params);
}