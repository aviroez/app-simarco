package com.ppproperti.simarco.interfaces;
import com.ppproperti.simarco.responses.ResponseSetting;
import com.ppproperti.simarco.responses.ResponseSettingList;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface SettingService {

    @GET("api/settings")
    Call<ResponseSettingList> get(@QueryMap Map<String, String> map);

    @GET("api/settings/{id}")
    Call<ResponseSetting> get(@Path("id") int id, @QueryMap Map<String, String> map);
}