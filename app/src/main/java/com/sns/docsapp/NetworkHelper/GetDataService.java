package com.sns.docsapp.NetworkHelper;

import com.sns.docsapp.Model.ResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetDataService {
    @GET("chat/")
    Call<ResponseModel> getBotResponse(@Query("apiKey") String apiKey, @Query("message") String message, @Query("chatBotID") String chatBotID, @Query("externalD") String externalD);
}
