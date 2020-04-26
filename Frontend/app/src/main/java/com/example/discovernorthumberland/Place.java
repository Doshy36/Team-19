package com.example.discovernorthumberland;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;
import java.util.Arrays;

/*
Class for creating and defining Places/Locations.
 */
public class Place implements Comparable<Place>, ClusterItem {

    private LatLng mPosition;
    private float[] distanceFromUser = new float[3];
    private String placeId;
    private String locationName;
    private String description;
    private String[] imageUrlStringArray;
    private String[] categories;

    // Setting up Place object.
    public Place(String placeId, String locationName, String description, String latLngString, String[] imageUrlStringArray, String[] categories, LatLng userLocation) {
        this.placeId = placeId;
        this.locationName = locationName;
        this.description = description;
        this.imageUrlStringArray = imageUrlStringArray;
        this.categories = categories;
        String[] latLngStringArray = latLngString.split(",");
        mPosition = new LatLng(Double.parseDouble(latLngStringArray[0]), Double.parseDouble(latLngStringArray[1]));
        if (userLocation != null) {
            android.location.Location.distanceBetween(userLocation.latitude, userLocation.longitude, mPosition.latitude, mPosition.longitude, distanceFromUser);
        }
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

    public float[] getDistanceFromUser() {
        return distanceFromUser;
    }

    @Override
    public int compareTo(Place location) {
        float[] location2Array = location.getDistanceFromUser();
        return Integer.compare(Math.round(distanceFromUser[0]), Math.round(location2Array[0]));
    }

    @Override
    public String toString() {
        return placeId + " : " + locationName + "\n" + description + "\n" + mPosition.latitude + ", " + mPosition.longitude + "\n" + distanceFromUser[0] + "m away from User" + "\n" + "Categories :" + Arrays.toString(categories) + "\n" + Arrays.toString(imageUrlStringArray);
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return locationName;
    }

    @Override
    public String getSnippet() {
        return null;
    }
}
