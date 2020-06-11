package com.example.basicotplogin.Fragments;

import com.example.basicotplogin.Notifications.MyResponse;
import com.example.basicotplogin.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAvgtDwT4:APA91bHnhgoCUNRx7V9myXCqmC5Y8KM2-iypNIAfHds4h3ygbZsGKYRITQ_AKpZOMQ2BXDOkQH0ExSpDDCJo799lZLJn9YOo0VHU2qOa2QtmfhtTBlk1E_-2038v4LyxmHQ1m7hHa-OJ"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
