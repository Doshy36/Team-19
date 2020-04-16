package com.example.discovernorthumberland;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;

public class Place extends AppCompatActivity implements Comparable<Place> {

    private double latitude;
    private double longitude;
    String[] latLngStringArray;
    private float[] distanceFromUser = new float[3];
    private String placeId;
    private String locationName;
    private String description;
    private String[] imageUrlStringArray;
    private String[] categories;

    public Place(String placeId, String locationName, String description, String latLngString , String[] imageUrlStringArray, String[] categories,LatLng userLocation) {
        this.placeId = placeId;
        this.locationName = locationName;
        this.description = description;
        this.imageUrlStringArray = imageUrlStringArray;
        this.categories = categories;
        this.latLngStringArray = latLngString.split(",");
        latitude = Double.parseDouble(latLngStringArray[0]);
        longitude = Double.parseDouble(latLngStringArray[1]);
        setDistanceFromUser(userLocation);


    }


    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getPlaceId() {
        return placeId;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getDescription() {
        return description;
    }

    public String[] getImageUrlStringArray() {
        return imageUrlStringArray;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setDistanceFromUser(LatLng userLocation){
        android.location.Location.distanceBetween(userLocation.latitude,userLocation.longitude,this.latitude,this.longitude,distanceFromUser);
    }

    public float[] getDistanceFromUser() {
        return distanceFromUser;
    }

    @Override
    public int compareTo(Place location) {
        float[] location2Array = location.getDistanceFromUser();
        return Integer.compare(Math.round(distanceFromUser[0]),Math.round(location2Array[0]));
    }

    @Override
    public String toString() {
        return placeId + " : " + locationName + "\n" + description + "\n" + latitude + ", " + longitude + "\n"+ distanceFromUser[0] +"m away from User" + "\n" + "Categories :"+Arrays.toString(categories)+"\n"+ Arrays.toString(imageUrlStringArray);
    }
}
