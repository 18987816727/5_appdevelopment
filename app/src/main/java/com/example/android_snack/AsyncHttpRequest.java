package com.example.android_snack;

import okhttp3.*;
import java.io.IOException;

public class AsyncHttpRequest {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public void sendPostRequest(String requestBody, String url,Callback callback) {
        RequestBody body = RequestBody.create(JSON, requestBody);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(callback);
    }
}