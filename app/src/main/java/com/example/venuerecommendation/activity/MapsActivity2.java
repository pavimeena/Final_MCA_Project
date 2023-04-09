package com.example.venuerecommendation.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.venuerecommendation.R;
import com.example.venuerecommendation.model.Common;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
//import com.manjurulhoque.mynearbyplaces.activity.DetailList;
import com.example.venuerecommendation.model.MyPlaces;
import com.example.venuerecommendation.model.Results;
import com.example.venuerecommendation.remotes.GoogleApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private double latitude, longitude;
    private Location mLastLocation;
    private Marker mMarker;
    private LocationRequest mLocationRequest;

    GoogleApiService mGoogleApiService;
    private static final int LOCATION_CODE = 1000;
    public Button button;
    private Spinner spinner;
    double lat;
    double lng;
    String placeName;
    String vicinity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        button = findViewById(R.id.button7);
        spinner = findViewById(R.id.spinner);
        //buildGoogleApiClient();
        //Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Init service
        mGoogleApiService = Common.getGoogleApiService();

        // Request runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String spinn = spinner.getSelectedItem().toString();

                SharedPreferences sp = getSharedPreferences("pre",MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("placetype", spinn);
                editor.apply();

               if(spinn.equalsIgnoreCase("Select Place Type")){
                   Toast.makeText(getApplicationContext(),"Plese Select A Place Type",Toast.LENGTH_SHORT).show();
               }
               else {
                   nearbyPlaces(spinn);
               }
            }
        });

       /* BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_hospital:
                        nearbyPlaces("hospital");
                        break;
                    case R.id.action_market:
                        nearbyPlaces("market");
                        break;
                    case R.id.action_restaurant:
                        nearbyPlaces("restaurant");
                        break;
                    default:
                        break;
                }
                return false;
            }
        });*/
        FindLocation();
    }

    public void FindLocation() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        boolean network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location location;

        if (network_enabled) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                mLastLocation = location;
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                Toast.makeText(
                        getApplicationContext(),
                        String.valueOf(location.getLatitude()) + "\n" + String.valueOf(location.getLongitude()), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        if (mGoogleApiClient != null)
            mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void nearbyPlaces(final String placeType) {
        mMap.clear();

        String url = getUrl(latitude, longitude, placeType);

        mGoogleApiService.getNearByPlaces(url)
                .enqueue(new Callback<MyPlaces>() {
                    @Override
                    public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {
                        if (response.isSuccessful()) {
                            for (int i = 0; i < response.body().getResults().size(); i++) {
                                MarkerOptions markerOptions = new MarkerOptions();
                                Results googlePlace = response.body().getResults().get(i);
                                lat = Double.parseDouble(googlePlace.getGeometry().getLocation().getLat());
                                lng = Double.parseDouble(googlePlace.getGeometry().getLocation().getLng());
                                placeName = googlePlace.getName();
                                vicinity = googlePlace.getVicinity();

                                LatLng latLng = new LatLng(lat, lng);
                               // markerOptions.position(latLng);
                                //markerOptions.title(placeName);

                                //markerOptions.isVisible();

                                //mMap.addMarker(markerOptions);
                                Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(placeName));
                                marker.showInfoWindow();


                              /*  mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                    @Override
                                    public boolean onMarkerClick(Marker marker) {
                                        Intent intent = new Intent(getApplicationContext(), DetailList.class);
                                        intent.putExtra("placename",placeName);
                                        intent.putExtra("lat",lat);
                                        intent.putExtra("lng",lng);
                                        intent.putExtra("address",vicinity);

                                        startActivity(intent);
                                       // finish();
                                        return false;
                                    }
                                });*/

                                // move camera
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));



                            }
                        }
                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(final Marker marker) {

                                /*Intent intent = new Intent(getApplicationContext(), DetailList.class);
                                intent.putExtra("placename",marker.getTitle());
                                intent.putExtra("lat",marker.getPosition().latitude);
                                intent.putExtra("lng",marker.getPosition().longitude);
                                startActivity(intent);*/
                                final Dialog dialog = new Dialog(MapsActivity2.this);
                                dialog.setContentView(R.layout.custom);
                                Button dialogButton = (Button) dialog.findViewById(R.id.buttonOk);
                                TextView textView = (TextView)dialog.findViewById(R.id.textView17);
                                textView.setText(marker.getTitle());
                                dialogButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(getApplicationContext(), DetailList.class);
                                        intent.putExtra("placename",marker.getTitle());
                                        intent.putExtra("lat",marker.getPosition().latitude);
                                        intent.putExtra("lng",marker.getPosition().longitude);
                                        startActivity(intent);
                                    }
                                });
                                dialog.show();

                                return false;
                            }
                        });
                      /*  mMap.setonMa(new GoogleMap.OnMapLongClickListener() {
                            @Override
                            public void onMapLongClick(LatLng latLng) {

                                Intent intent = new Intent(getApplicationContext(), DetailList.class);
                                intent.putExtra("placename",placeName);
                                intent.putExtra("lat",lat);
                                intent.putExtra("lng",lng);
                                intent.putExtra("address",vicinity);
                                startActivity(intent);
                            }
                        });*/
                      /*  mMap.addCircle(new CircleOptions()
                                .center(new LatLng(latitude, longitude))
                                .radius(2000)
                                .strokeColor(Color.RED)
                                .fillColor(Color.TRANSPARENT));*/
                    }

                    @Override
                    public void onFailure(Call<MyPlaces> call, Throwable t) {

                    }
                });
    }

    private String getUrl(double latitude, double longitude, String placeType) {
        StringBuilder builder = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        builder.append("location=" + latitude + "," + longitude);
        builder.append("&radius=2000");
        builder.append("&type="+placeType);
        builder.append("&key=" + getResources().getString(R.string.locationnearby));
        Log.d("APIURL", builder.toString());
        return builder.toString();
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, LOCATION_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, LOCATION_CODE);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        onLocationChanged(mLastLocation);

//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mMarker != null) {
            mMarker.remove();
        }

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("Your position")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        mMarker = mMap.addMarker(options);

        // move camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }
}
