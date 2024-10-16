package com.ppproperti.simarco.interfaces;

import com.ppproperti.simarco.responses.ResponseEventActivityLog;
import com.ppproperti.simarco.responses.ResponseOrder;
import com.ppproperti.simarco.responses.ResponseUser;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ImageService {
    @Multipart
    @POST("api/images/{order_id}/order")
    Call<ResponseOrder> order(@Path("order_id") int orderId,
                              @Part List<MultipartBody.Part> file,
                              @Part("file[]") List<RequestBody> name,
                              @Query("code[]") ArrayList<String> code);

    @Multipart
    @POST("api/images/{event_activity_log_id}/eventactivitylog")
    Call<ResponseEventActivityLog> eventactivitylog(@Path("event_activity_log_id") int eventActivityLogId,
                                                    @Part List<MultipartBody.Part> file,
                                                    @Part("images[]") List<RequestBody> name,
                                                    @Query("code[]") ArrayList<String> code);
    @Multipart
    @POST("api/images/{user_id}/user")
    Call<ResponseUser> user(@Path("user_id") int userId,
                             @Part List<MultipartBody.Part> file,
                             @Part("file[]") List<RequestBody> name,
                             @Query("code[]") ArrayList<String> code);
}
