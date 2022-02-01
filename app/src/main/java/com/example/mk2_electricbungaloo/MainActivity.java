package com.example.mk2_electricbungaloo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This creates the JsonRequest instance (idk why nor what for)
        JsonRequest.getInstance(this);

        context = getApplicationContext();

        setContentView(R.layout.activity_main);


        TextView textView = findViewById(R.id.resultTextView);

        String result = Movie.testMethod(634649);
        textView.setText(result);
        Log.d("Main","response: "+ result);


    }

    public static Context getContext(){
        return context;
    }
}