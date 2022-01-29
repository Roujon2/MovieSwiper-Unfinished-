package com.example.mk2_electricbungaloo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JsonRequest.getInstance(this);
        setContentView(R.layout.activity_main);


        TextView textView = findViewById(R.id.resultTextView);

        String result = Movie.testMethod(550);
        textView.setText(result);
        Log.d("Main","response: "+ result);

    }
}