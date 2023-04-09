package com.example.venuerecommendation.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.venuerecommendation.R;
import com.example.venuerecommendation.adapter.RvAdapter;
import com.example.venuerecommendation.model.events;
import com.example.venuerecommendation.url.URLs;
import com.example.venuerecommendation.volley.VolleySingleton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.OnMapReadyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NearbyEventsActivity extends AppCompatActivity implements LocationListener {

    RecyclerView recyclerView;
    ArrayList<events> Events;
    private RvAdapter rvAdapter;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        recyclerView = findViewById(R.id.recyclerView);

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }

        getLocation();

    }
    void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        }
        catch(SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        // locationText.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());
        Log.e("test","Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());
        nearbyevents(location.getLatitude(),location.getLongitude());


    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(NearbyEventsActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    public void nearbyevents(final Double lati, final Double lngi){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.NEARBY_EVENT,
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

                                    double di = distance(lati,lngi,Double.valueOf(dataobj.getString("lat")),Double.valueOf(dataobj.getString("lng")),"K");

                                    if(di<2.0) {
                                        Log.e("test","hi");
                                        Log.e("12",dataobj.getString("name"));


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

                                    /*event.setLat(Double.valueOf(dataobj.getString("lat")));
                                    event.setLat(Double.valueOf(dataobj.getString("lat")));*/
                                        Events.add(event);
                                    }
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

    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        }

        return (dist);
    }
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
}
