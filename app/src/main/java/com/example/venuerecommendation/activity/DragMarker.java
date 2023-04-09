package com.example.venuerecommendation.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;

import com.example.venuerecommendation.R;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class DragMarker extends FragmentActivity implements OnMapReadyCallback {

    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private GoogleMap.OnCameraIdleListener onCameraIdleListener;
    private TextView resutText;
    private ImageView imageView;
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT < 22)
            setStatusBarTranslucent(false);
        else
            setStatusBarTranslucent(true);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_marker);
        resutText = (TextView) findViewById(R.id.dragg_result);
        imageView = findViewById(R.id.marker);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        configureCameraIdle();


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),OwnLocationTag.class);
                startActivity(intent);
                finish();

            }
        });

    }


    private void configureCameraIdle() {
        onCameraIdleListener = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                latLng = mMap.getCameraPosition().target;
                Geocoder geocoder = new Geocoder(DragMarker.this);

                try {
                    List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if (addressList != null && addressList.size() > 0) {
                        String locality = addressList.get(0).getAddressLine(0);
                        String country = addressList.get(0).getCountryName();
                        if (!locality.isEmpty() && !country.isEmpty())
                            resutText.setText(latLng.latitude + "  " + latLng.longitude);
                        SharedPreferences sharedPreferences = getSharedPreferences("prefe",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("lat", String.valueOf(latLng.latitude));
                        editor.putString("lng", String.valueOf(latLng.longitude));
                        editor.apply();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(onCameraIdleListener);
        CameraUpdate center=
                CameraUpdateFactory.newLatLng(new LatLng(13.0827,
                        80.2707));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

        mMap.moveCamera(center);
        mMap.animateCamera(zoom);

    }

    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

}
