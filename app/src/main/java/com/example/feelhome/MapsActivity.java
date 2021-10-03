// TODO: save map result as cache

package com.example.feelhome;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

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

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_Code = 99;
    private double latitude, longitude;
    private int ProximityRadius = 6000;
    private String btn_pressed, country_name;
    private boolean searchForQuery = true;
    private Toast toast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkUserLocationPermission();
        }

        Intent intent = getIntent();
        country_name = intent.getStringExtra("country_name");
        btn_pressed = intent.getStringExtra("btn_pressed");
        Log.d("Shakil(Maps_Activity)", "Country_name = " + country_name + " Button_pressed = " + btn_pressed);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    public void onClick(View v) {
        String shops = "grocery_or_supermarket", restaurant = "restaurant";
        Object transferData[] = new Object[2];
        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();


        switch (v.getId()) {

            case R.id.all_shop_nearby:
                if (toast != null) {
                    toast.cancel();
                }

                searchForQuery = false;
                mMap.clear();
                onLocationChanged(lastLocation);
                String url = getUrl(latitude, longitude, shops);
                transferData[0] = mMap;
                transferData[1] = url;

                getNearbyPlaces.execute(transferData);
                toast = Toast.makeText(this, "Searching for Nearby Shops...", Toast.LENGTH_SHORT);
                toast.show();
                toast = Toast.makeText(this, "Showing Nearby Shops...", Toast.LENGTH_SHORT);
                toast.show();
                break;


            case R.id.all_restaurant_nearby:
                if (toast != null) {
                    toast.cancel();
                }

                searchForQuery = false;
                mMap.clear();
                onLocationChanged(lastLocation);
                url = getUrl(latitude, longitude, restaurant);
                transferData[0] = mMap;
                transferData[1] = url;

                getNearbyPlaces.execute(transferData);
                toast = Toast.makeText(this, "Searching for Nearby Restaurants...", Toast.LENGTH_SHORT);
                toast.show();
                toast = Toast.makeText(this, "Showing Nearby Restaurants...", Toast.LENGTH_SHORT);
                toast.show();
                break;
        }
    }

    public void searchQuery() {
        if (toast != null) {
            toast.cancel();
        }

        Object transferData[] = new Object[2];
        GetNearbyPlaces getNearbyPlaces = new GetNearbyPlaces();
        if (btn_pressed.equals("grocery_or_supermarket")) {
            btn_pressed = "grocery+store";
        }

        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/textsearch/json?query=");
        googleURL.append(country_name + "+" + btn_pressed);
        googleURL.append("&location=" + latitude + "," + longitude);
        googleURL.append("&radius=" + ProximityRadius);
        googleURL.append("&key=" + "AIzaSyAb6JrlTZjJuXZuYLsnZE2FwYw_0z4V2T8");

        Log.d("Shakil(check_button_pressed)", googleURL.toString());

        transferData[0] = mMap;
        transferData[1] = googleURL.toString();
        getNearbyPlaces.execute(transferData);

        toast = Toast.makeText(this, "Searching for desired query...", Toast.LENGTH_SHORT);
        toast.show();
        toast = Toast.makeText(this, "Showing Results...", Toast.LENGTH_SHORT);
        toast.show();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("Shakil(onMapReady)", "onMapReady");
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

    }


    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        Log.d("Shakil(getUrl)", "getUrl");
        StringBuilder googleURL = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googleURL.append("location=" + latitude + "," + longitude);
        googleURL.append("&radius=" + ProximityRadius);
        googleURL.append("&type=" + nearbyPlace);
        googleURL.append("&sensor=true");
        googleURL.append("&key=" + "AIzaSyAb6JrlTZjJuXZuYLsnZE2FwYw_0z4V2T8");

        Log.d("GoogleMapsActivity", "url = " + googleURL.toString());

        return googleURL.toString();
    }


    public boolean checkUserLocationPermission() {
        Log.d("Shakil(checkUserLocationPermission)", "checkUserLocationPermission");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            }
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d("Shakil(onRequestPermissionsResult)", "onRequestPermissionsResult");
        switch (requestCode) {
            case Request_User_Location_Code:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "Permission Denied...", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }


    protected synchronized void buildGoogleApiClient() {
        Log.d("Shakil(buildGoogleApiClient)", "buildGoogleApiClient");
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
    }

    //When map ready this function starts
    @Override
    public void onLocationChanged(Location location) {
        Log.d("Shakil(onLocationChanged)", "onLocationChanged");
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        lastLocation = location;

        if (currentUserLocationMarker != null) {
            currentUserLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("user Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        currentUserLocationMarker = mMap.addMarker(markerOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }

        if (searchForQuery == true) {
            searchQuery();
        }

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("Shakil(onConnected)", "onConnected");
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}