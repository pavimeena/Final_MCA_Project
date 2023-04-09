package com.example.venuerecommendation.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.venuerecommendation.R;

public class AddTagActivity extends AppCompatActivity {
    String gname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tag);

        Bundle extras = getIntent().getExtras();
        gname = extras.getString("lat");
        Log.e("test",gname);
    }
}
