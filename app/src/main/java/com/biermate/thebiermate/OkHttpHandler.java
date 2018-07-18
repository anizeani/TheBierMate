package com.biermate.thebiermate;
import android.os.AsyncTask;
import android.util.Log;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpHandler extends AsyncTask<String,Void,String> {

    OkHttpClient client = new OkHttpClient(); // make object of OkHttpClient

    // override onPreExecute() if necessary

    @Override
    protected String doInBackground(String... params) {

        //clean up
        String json = "{\n" +
                "\"grant_type\":\"client_credentials\",\n" +
                "\"client_id\": \""  + params[1] + "\",\n" +
                "\"client_secret\": \"" + params[2] + "\"\n" +
                "}";

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        Request request = new Request.Builder().url(params[0]).post(requestBody).build();

        try {
            Response response = client.newCall(request).execute();
            return response.body().string(); // execution happens here in background
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String string) {
        super.onPostExecute(string);
        // DO something with result inside the variable string.....
    }
}

