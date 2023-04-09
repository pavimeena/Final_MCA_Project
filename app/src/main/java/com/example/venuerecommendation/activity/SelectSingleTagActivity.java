package com.example.venuerecommendation.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SelectSingleTagActivity extends AppCompatActivity{
    String lat,lng;
    String phone;
    TextView tlat,tlng;
    public static final String SHARED_PREF_NAME = "mysharedpref";
    public static final String KEY_LAT = "keylat";
    public static final String KEY_LNG = "keylng";
    Button button;
    ArrayList<String> number;
    private JSONArray result;
    ArrayList<String> students;
    Spinner sptype,sctype,senvi,ssuitable,ssession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_tag);
        button = findViewById(R.id.button8);

        sptype = findViewById(R.id.spinner10);
        sctype = findViewById(R.id.spinner2);
        senvi = findViewById(R.id.spinner7);
        ssuitable = findViewById(R.id.spinner8);
        ssession = findViewById(R.id.spinner9);


        User user = SharedPrefManager.getInstance(this).getUser();
        phone = user.getPhone();

       // number = getIntent().getExtras().getStringArrayList("list");
        fetch();
        /*getData();*/

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sptype.getSelectedItem().toString().equalsIgnoreCase("Select Place Type")){
                    Toast.makeText(getApplicationContext(),"Select Place Type",Toast.LENGTH_SHORT).show();
                }
                else if(sctype.getSelectedItem().toString().equalsIgnoreCase("Select Category")){
                    Toast.makeText(getApplicationContext(),"Select Category",Toast.LENGTH_SHORT).show();
                }
                else if(senvi.getSelectedItem().toString().equalsIgnoreCase("Select Environment")){
                    Toast.makeText(getApplicationContext(),"Select Environment Type",Toast.LENGTH_SHORT).show();
                }
                else if(ssuitable.getSelectedItem().toString().equalsIgnoreCase("Suitable For")){
                    Toast.makeText(getApplicationContext(),"Select Suitable For",Toast.LENGTH_SHORT).show();
                }
                else if(ssession.getSelectedItem().toString().equalsIgnoreCase("Select Session")){
                    Toast.makeText(getApplicationContext(),"Select Session",Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), SingleMapsActivity3.class);
                   // intent.putStringArrayListExtra("list", number);
                    intent.putExtra("placetype",sptype.getSelectedItem().toString());
                    intent.putExtra("category",sctype.getSelectedItem().toString());
                    intent.putExtra("environment",senvi.getSelectedItem().toString());
                    intent.putExtra("suitable",ssuitable.getSelectedItem().toString());
                    intent.putExtra("session",ssession.getSelectedItem().toString());
                    startActivity(intent);
                }
            }
        });

    }
    public void fetch(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.CENTER_POINT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // progressBar.setVisibility(View.GONE);
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                JSONObject userJson = obj.getJSONObject("user");

                                lat = userJson.getString("lat");
                                Log.e("test", lat);
                                lng = userJson.getString("lng");
                                Log.e("test1", lng);
                                /*tlat.setText(lat);
                                tlng.setText(lng);*/
                                SharedPreferences sp = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();

                                editor.putString(KEY_LAT, lat);
                                editor.putString(KEY_LNG,lng);
                                editor.apply();



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
                params.put("phone",phone);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

   /* private void getData(){
        StringRequest stringRequest = new StringRequest(URLs.SELECT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject j = null;
                        try {
                            j = new JSONObject(response);
                            result = j.getJSONArray("result");
                            Log.e("test", String.valueOf(result));
                            getStudents(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void getStudents(JSONArray j){
        for(int i=0;i<j.length();i++){
            try {
                JSONObject json = j.getJSONObject(i);
                Log.e("test",json.getString("name"));
                students.add(json.getString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        spinner.setAdapter(new ArrayAdapter<String>(SelectTagActivity.this, android.R.layout.simple_spinner_dropdown_item, students));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(),"test",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }*/
}
