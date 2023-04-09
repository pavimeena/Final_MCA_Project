package com.example.venuerecommendation.activity;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.venuerecommendation.R;
import com.example.venuerecommendation.activity.ProfileActivity;
import com.example.venuerecommendation.model.User;
import com.example.venuerecommendation.sharedpref.SharedPrefManager;
import com.example.venuerecommendation.url.URLs;
import com.example.venuerecommendation.volley.VolleySingleton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    String mob;
    Double lat,lng;
    LatLng sydney;
    String pnumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        User user = SharedPrefManager.getInstance(this).getUser();
        pnumber = user.getPhone();
        fetch();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(lat!=null) {
            sydney = new LatLng(lat, lng);
        }
        else {
            sydney = new LatLng(37.0902, 95.7129);
        }
        mMap.addMarker(new MarkerOptions().position(sydney).title("Center Point"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public void fetch(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://10.0.0.16/VenueRecommender/loc.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       // progressBar.setVisibility(View.GONE);
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                JSONObject userJson = obj.getJSONObject("user");

                               lat = Double.valueOf(userJson.getString("lat"));
                               Log.e("test", String.valueOf(lat));
                               lng = Double.valueOf(userJson.getString("lng"));
                               Log.e("test1", String.valueOf(lng));

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
                params.put("phone",pnumber);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
    }


