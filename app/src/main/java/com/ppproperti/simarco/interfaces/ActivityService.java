package com.ppproperti.simarco.interfaces;

import com.ppproperti.simarco.responses.ResponseActivityList;
import com.ppproperti.simarco.responses.ResponseEvent;
import com.ppproperti.simarco.responses.ResponseEventActivity;
import com.ppproperti.simarco.responses.ResponseEventActivityList;
import com.ppproperti.simarco.responses.ResponseEventActivityLog;
import com.ppproperti.simarco.responses.ResponseEventActivityLogList;
import com.ppproperti.simarco.responses.ResponseEventList;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface ActivityService {

    @FormUrlEncoded
    @POST("api/events")
    Call<ResponseEvent> addEvent(@FieldMap Map<String, String> params);

    @GET("api/events")
    Call<ResponseEventList> listEvent(@QueryMap Map<String, String> params);

    @GET("api/activities")
    Call<ResponseActivityList> listActivity(@QueryMap Map<String, String> params);

    @GET("api/eventactivities")
    Call<ResponseEventActivityList> listEventActivity(@QueryMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/eventactivities")
    Call<ResponseEventActivity> addEventActivity(@FieldMap Map<String, String> params);

    @GET("api/eventactivitylogs")
    Call<ResponseEventActivityLogList> listEventActivityLog(@QueryMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/eventactivitylogs")
    Call<ResponseEventActivityLog> addEventActivityLog(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("api/eventactivitylogs/{id}")
    Call<ResponseEventActivityLog> updateEventActivityLog(@Path("id") int id, @FieldMap Map<String, String> params);
}
