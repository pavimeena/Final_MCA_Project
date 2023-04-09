package com.example.venuerecommendation.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.venuerecommendation.R;
import com.example.venuerecommendation.model.User;
import com.example.venuerecommendation.sharedpref.SharedPrefManager;
import com.example.venuerecommendation.url.URLs;
import com.example.venuerecommendation.volley.VolleySingleton;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendSmsActivity extends AppCompatActivity {

    public EditText smdtext;
    public Button send;
    DatabaseReference latlng;
    List<String> latvalue;
    List<String> lngvalue;
    public String latitude;
    public String longitude;
    Button maps;
    String pnumber;
    int count;
    ArrayList<String> numbers;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);

        User user = SharedPrefManager.getInstance(this).getUser();
        pnumber = user.getPhone();

        numbers=getIntent().getExtras().getStringArrayList("list");

        count = numbers.size();



        smdtext =(EditText)findViewById(R.id.editText);
        send = (Button)findViewById(R.id.button2);
        maps = (Button)findViewById(R.id.button5);


        latvalue = new ArrayList<>();
        lngvalue = new ArrayList<>();

        handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                Count();
                handler.postDelayed(this, 1000);
            }
        };

        handler.postDelayed(r, 1000);


       send.setOnClickListener(new View.OnClickListener() {
         //  ArrayList<String> numbers=getIntent().getExtras().getStringArrayList("list");
           @Override
           public void onClick(View v) {

               try{
                   SmsManager smgr = SmsManager.getDefault();
                   for (String number : numbers)   // using foreach
                   {
                       smgr.sendTextMessage(number,null,"request@1@"+pnumber,null,null);
                   }

                   Toast.makeText(SendSmsActivity.this, "SMS Sent Successfully", Toast.LENGTH_SHORT).show();
               }
               catch (Exception e){
                   Toast.makeText(SendSmsActivity.this, "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
               }
           }


       });

        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void Count() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.COUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                     //   progressBar.setVisibility(View.GONE);
                        Log.e("test","tets1");

                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            Log.e("test",response);

                            //if no error in response
                            if (!obj.getBoolean("error")) {
                                //Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                JSONObject userJson = obj.getJSONObject("user");
                                int cou = Integer.parseInt(userJson.getString("count"));
                                if(count==cou){
                                    handler.removeCallbacksAndMessages(null);
                                    Intent intent = new Intent(getApplicationContext(),SelectTagActivity.class);
                                    intent.putStringArrayListExtra("list",numbers);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("phone", pnumber);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);

    }

}
