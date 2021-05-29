package com.joker.fcapp1.Remote;

import com.joker.fcapp1.Model.Response;
import com.joker.fcapp1.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAV8qkCS4:APA91bEw0GZEdiZMvp6HjHFsmrZICnp0yh_BijvZhM1JgztVlf39kz3ttWASWOHARbB0di1xgXax27ZB22_kBAuLKx5kU4wBCddFL4xar8MIaHqan3GV7vBVisusCgHCyVy0-NTlcTXb"
            }

    )
    @POST("fcm/send")
    Call<Response> sendNotification (@Body Sender body);
}
