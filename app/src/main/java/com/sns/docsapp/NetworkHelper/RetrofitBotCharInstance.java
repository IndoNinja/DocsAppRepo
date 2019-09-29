package com.sns.docsapp.NetworkHelper;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitBotCharInstance {
    private static Retrofit retrofit;
    private static final String BASE_URL = "http://www.personalityforge.com/api/";
    //private static final String BASE_URL = "http://192.168.0.101:5000/api/";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
