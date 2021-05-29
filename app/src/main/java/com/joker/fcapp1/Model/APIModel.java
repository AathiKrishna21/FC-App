package com.joker.fcapp1.Model;

import com.joker.fcapp1.Remote.APIService;
import com.joker.fcapp1.Remote.RetrofitClient;

public class APIModel {
    private static final String BASE_URL = "https://fcm.googleapis.com/";
    public static APIService getFCMService(){
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
