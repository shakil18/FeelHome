package com.example.feelhome;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetNearbyPlaces extends AsyncTask<Object, String, String> {
    private String googleplaceData, url;
    private GoogleMap mMap;

    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googleplaceData = downloadUrl.ReadTheURL(url);
            Log.d("Shakil(Checking_Map_Limit)", "url = " + googleplaceData);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return googleplaceData;
    }


    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String, String>> nearByPlacesList = null;
        DataParser dataParser = new DataParser();
        nearByPlacesList = dataParser.parse(s);

        // TODO: Storing/Caching need to be done here. all information contained by nearByPlacesList
        DisplayNearbyPlaces(nearByPlacesList);
        Log.d("Shakil(DisplayNearbyPlaces)", "nearByPlacesList = " + nearByPlacesList);

    }


    private void DisplayNearbyPlaces(List<HashMap<String, String>> nearByPlacesList) {
        for (int i = 0; i < nearByPlacesList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();

            HashMap<String, String> googleNearbyPlace = nearByPlacesList.get(i);
            String nameOfPlace = googleNearbyPlace.get("place_name");
            String vicinity = googleNearbyPlace.get("vicinity");
            String rating = googleNearbyPlace.get("rating");
            String formatted_address = googleNearbyPlace.get("formatted_address");

            double lat = Double.parseDouble(googleNearbyPlace.get("lat"));
            double lng = Double.parseDouble(googleNearbyPlace.get("lng"));


            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(nameOfPlace + " : " + vicinity + formatted_address);
            markerOptions.snippet("Rating: " + rating);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            mMap.addMarker(markerOptions);
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
        }
    }
}