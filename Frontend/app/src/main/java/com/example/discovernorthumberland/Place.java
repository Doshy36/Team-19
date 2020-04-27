package com.example.discovernorthumberland;


import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.Arrays;

public class Place implements Comparable<Place>, ClusterItem {


    private final LatLng M_POSITION;

    private final float[] DISTANCE_FROM_USER = new float[3];

    private final String PLACE_ID;
    private final String LOCATION_NAME;
    private final String DESCRIPTION;
    private final String[] IMAGE_URL_STRING_ARRAY;
    private final String[] CATEGORIES;

    Place(String placeId, String locationName, String description, String latLngString, String[] imageUrlStringArray, String[] categories, LatLng userLocation) {
        this.PLACE_ID = placeId;
        this.LOCATION_NAME = locationName;
        this.DESCRIPTION = description;
        this.IMAGE_URL_STRING_ARRAY = imageUrlStringArray;
        this.CATEGORIES = categories;
        String[] latLngStringArray = latLngString.split(",");
        M_POSITION = new LatLng(Double.parseDouble(latLngStringArray[0]), Double.parseDouble(latLngStringArray[1]));
        if (userLocation != null) {
            android.location.Location.distanceBetween(userLocation.latitude, userLocation.longitude, M_POSITION.latitude, M_POSITION.longitude, DISTANCE_FROM_USER);
        }
    }


    String getPlaceId() {
        return PLACE_ID;
    }

    String getLocationName() {
        return LOCATION_NAME;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public String[] getIMAGE_URL_STRING_ARRAY() {
        return IMAGE_URL_STRING_ARRAY;
    }

    String[] getCATEGORIES() {
        return CATEGORIES;
    }

    float[] getDISTANCE_FROM_USER() {
        return DISTANCE_FROM_USER;
    }

    @Override
    public int compareTo(Place location) {
        float[] location2Array = location.getDISTANCE_FROM_USER();
        return Integer.compare(Math.round(DISTANCE_FROM_USER[0]), Math.round(location2Array[0]));
    }



    @Override
    public String toString() {
        return PLACE_ID + " : " + LOCATION_NAME + "\n" + DESCRIPTION + "\n" + M_POSITION.latitude + ", " + M_POSITION.longitude + "\n" + DISTANCE_FROM_USER[0] + "m away from User" + "\n" + "Categories :" + Arrays.toString(CATEGORIES) + "\n" + Arrays.toString(IMAGE_URL_STRING_ARRAY);
    }

    @Override
    public LatLng getPosition() {
        return M_POSITION;
    }

    @Override
    public String getTitle() {
        return LOCATION_NAME;
    }

    @Override
    public String getSnippet() {
        return null;
    }

}
