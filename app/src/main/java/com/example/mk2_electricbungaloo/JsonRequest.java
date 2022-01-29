package com.example.mk2_electricbungaloo;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JsonRequest {

    private static final String TAG = "JsonRequest";
    private static JsonRequest instance = null;

    // For Volley API
    public RequestQueue requestQueue;

    private JsonRequest(Context context){
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized JsonRequest getInstance(Context context){
        if (null == instance){
            instance = new JsonRequest(context);
        }
        return instance;
    }

    //this is so you don't need to pass context each time
    public static synchronized JsonRequest getInstance()
    {
        if (null == instance)
        {
            throw new IllegalStateException(JsonRequest.class.getSimpleName() +
                    " is not initialized, call getInstance(...) first");
        }
        return instance;
    }

    public void returnRequest(String url, final CustomListener<String> listener){

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {
                        Log.d(TAG + ": ", "somePostRequest Response : " + response.toString());
                        if(!"null".equals(response.toString()))
                            listener.getResult(response.toString());
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        if (null != error.networkResponse)
                        {
                            Log.d(TAG + ": ", "Error Response code: " + error.networkResponse.statusCode);
                            listener.getResult("Error 69");
                        }
                    }
                });

        requestQueue.add(request);

    }
}