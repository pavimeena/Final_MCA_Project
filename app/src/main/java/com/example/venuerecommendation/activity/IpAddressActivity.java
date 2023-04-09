package com.example.venuerecommendation.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.venuerecommendation.R;

import java.util.ArrayList;
import java.util.List;


public class IpAddressActivity extends AppCompatActivity {
    public EditText editText;
    public Button button;
    private static final int PERMISSION_REQUEST_CODE = 1;
    String[] permissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.SYSTEM_ALERT_WINDOW,

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip_address);

        checkPermissions();

        editText = findViewById(R.id.editText);
        button = findViewById(R.id.button9);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = editText.getText().toString().trim();
                if(ip.equalsIgnoreCase("")){
                    editText.setError("Enter Ip");
                    editText.requestFocus();
                }
                else {
                    IpAddress.Ip_Address =ip;
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }
            }
        });



    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // do something
            }
            return;
        }
    }
}
