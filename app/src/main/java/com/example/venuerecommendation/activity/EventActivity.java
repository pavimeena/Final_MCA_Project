package com.example.venuerecommendation.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.venuerecommendation.R;
import com.example.venuerecommendation.adapter.RvAdapter;
import com.example.venuerecommendation.model.User;
import com.example.venuerecommendation.model.events;
import com.example.venuerecommendation.url.URLs;
import com.example.venuerecommendation.volley.VolleySingleton;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventActivity extends AppCompatActivity {

    ArrayList<events> Events;
    private RvAdapter rvAdapter;
    private RecyclerView recyclerView;
    String placename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        recyclerView = findViewById(R.id.recyclerView);

        Bundle bundle = getIntent().getExtras();
        placename = bundle.getString("placename");
        events();
    }
    public void events(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.EVENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       // progressBar.setVisibility(View.GONE);

                        try {
                            //converting response to json object
                            JSONObject obj = new JSONObject(response);
                            Log.e("test",response);

                            //if no error in response
                            if (!obj.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                //getting the user from the response
                              //  JSONObject userJson = obj.getJSONObject("user");
                                Events = new ArrayList<>();
                                JSONArray dataArray  = obj.getJSONArray("user");

                                for (int i = 0; i < dataArray.length(); i++) {

                                    events event = new events();
                                    JSONObject dataobj = dataArray.getJSONObject(i);
                                    event.setName(dataobj.getString("name"));
                                    event.setEventname(dataobj.getString("eventname"));
                                    event.setHostname(dataobj.getString("hostname"));
                                    event.setStime(dataobj.getString("stime"));
                                    event.setSdate(dataobj.getString("sdate"));
                                    event.setEtime(dataobj.getString("etime"));
                                    event.setCategory(dataobj.getString("category"));
                                    event.setSuitablefor(dataobj.getString("suitablefor"));
                                    event.setEnvironment(dataobj.getString("environment"));
                                    event.setSession(dataobj.getString("session"));
                                    event.setEventimage(dataobj.getString("eventimage"));
                                    Events.add(event);
                                }
                                setupRecycler();

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
                params.put("name", placename);

                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);

    }
    @SuppressLint("WrongConstant")
    private void setupRecycler(){

        rvAdapter = new RvAdapter(this,Events);
        //recyclerView.setAdapter(rvAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(rvAdapter);
        //recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

    }

}
