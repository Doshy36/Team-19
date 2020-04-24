package com.example.discovernorthumberland;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BookmarksActivity extends AppCompatActivity {

    final ArrayList<String> placeIdArrayList = new ArrayList<>();
    final ArrayList<Place> sortedArrayOfLocations = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        if (MainActivity.getUserLoggedIn()) {

            final RequestQueue queue = Volley.newRequestQueue(this);
            final String url = "https://jwhitehead.uk/bookmarks/" + MainActivity.getUserID();
            final int[] responseLength = {0};

            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                //Take array of JSON OBJECT OF ALL LOCATIONS
                                JSONArray jsonArrayOfLocations = response.getJSONArray("message");
                                Log.i("Bookmark Main Message", jsonArrayOfLocations.toString());
                                //ArrayList stores locations
                                final ArrayList<Place> sortedArrayOfLocations = new ArrayList<>();
                                //Loop through arrayList
                                for (int i = 0; i < jsonArrayOfLocations.length(); i++) {
                                    final JSONObject jsonObjectOfBookmark = jsonArrayOfLocations.getJSONObject(i); //Create JSONObject for each JSONObject in JSONArray retrieved from Server
                                    placeIdArrayList.add(jsonObjectOfBookmark.getString("placeId"));
                                    responseLength[0]++;
                                }
                                if (responseLength[0] == jsonArrayOfLocations.length()) {
                                    createListOfBookmarks();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                            , new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "ERROR CONNECTION TO SERVER FAILURE", Toast.LENGTH_LONG).show();
                            Log.i("RESPONSE", error.toString());
                        }
                    });
            // Add the request to the RequestQueue.
            queue.add(jsonObjectRequest);
        } else {
            ConstraintLayout parentConstraintLayout = findViewById(R.id.bookmarkParentConstraintLayout);
            parentConstraintLayout.setId(View.generateViewId());

            ConstraintLayout constraintLayout = findViewById(R.id.errorUserNotLoggedInConstraintLayout);

            TextView notLoggedInErrorTextView = new TextView(getBaseContext());
            String text = "Not Logged In Please Log in before viewing bookmarks.";
            notLoggedInErrorTextView.setText(text);
            notLoggedInErrorTextView.setTextSize(30);
            notLoggedInErrorTextView.setGravity(Gravity.CENTER);
            notLoggedInErrorTextView.setId(View.generateViewId());

            constraintLayout.addView(notLoggedInErrorTextView);


            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);

            constraintSet.connect(notLoggedInErrorTextView.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP, 0);
            constraintSet.connect(notLoggedInErrorTextView.getId(), ConstraintSet.LEFT, constraintLayout.getId(), ConstraintSet.LEFT, 0);
            constraintSet.connect(notLoggedInErrorTextView.getId(), ConstraintSet.RIGHT, constraintLayout.getId(), ConstraintSet.RIGHT, 0);
            constraintSet.connect(notLoggedInErrorTextView.getId(), ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM, 0);

            constraintSet.applyTo(constraintLayout);
            parentConstraintLayout.removeView(constraintLayout);
            parentConstraintLayout.addView(constraintLayout);

        }
    }

    public void createListOfBookmarks() {
        final int[] locationCounter = {0};
        for (int i = 0; i < placeIdArrayList.size(); i++) {
            Log.w("Bookmark :" + i, placeIdArrayList.get(i));
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://jwhitehead.uk/bookmarks";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.i("Bookmark response", response.toString());
                                JSONObject bookmarkLocationJsonObject = response.getJSONObject("message");
                                Log.i("Bookmark response.Message", bookmarkLocationJsonObject.toString());

                                locationCounter[0]++;

                                String[] imageUrlArray = bookmarkLocationJsonObject.getString("imageUrl").split(","); //String Array of each image url from server

                                //Create ArrayList of categories of location retrieved from server & transfer to String
                                ArrayList<String> categoriesArrayList = new ArrayList<>();
                                JSONArray jsonTopicArray = bookmarkLocationJsonObject.getJSONArray("categories");
                                for (int k = 0; k < jsonTopicArray.length(); k++) {
                                    categoriesArrayList.add(jsonTopicArray.getString(k));
                                }
                                String[] categoriesArray = categoriesArrayList.toArray(new String[0]);

                                //Taker users location to send to Place Constructor
                                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                                @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                Place place = new Place(bookmarkLocationJsonObject.getString("placeId"), bookmarkLocationJsonObject.getString("name"), bookmarkLocationJsonObject.getString("description"), bookmarkLocationJsonObject.getString("locationData"), imageUrlArray, categoriesArray, userLatLng);
                                sortedArrayOfLocations.add(place);
                                if (locationCounter[0] == placeIdArrayList.size()) {
                                    drawBookmarkButtons();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("RESPONSE", error.toString());
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + MainActivity.getAccessToken());
                    headers.put("userId", MainActivity.getUserID());
                    Log.i("Header toString",headers.toString());
                    return headers;
                }
            };
            /*
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://jwhitehead.uk/place/" + placeIdArrayList.get(i);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.i("Bookmark response", response.toString());
                                JSONObject bookmarkLocationJsonObject = response.getJSONObject("message");
                                Log.i("Bookmark response.Message", bookmarkLocationJsonObject.toString());

                                locationCounter[0]++;

                                String[] imageUrlArray = bookmarkLocationJsonObject.getString("imageUrl").split(","); //String Array of each image url from server

                                //Create ArrayList of categories of location retrieved from server & transfer to String
                                ArrayList<String> categoriesArrayList = new ArrayList<>();
                                JSONArray jsonTopicArray = bookmarkLocationJsonObject.getJSONArray("categories");
                                for (int k = 0; k < jsonTopicArray.length(); k++) {
                                    categoriesArrayList.add(jsonTopicArray.getString(k));
                                }
                                String[] categoriesArray = categoriesArrayList.toArray(new String[0]);

                                //Taker users location to send to Place Constructor
                                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                                @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                Place place = new Place(bookmarkLocationJsonObject.getString("placeId"), bookmarkLocationJsonObject.getString("name"), bookmarkLocationJsonObject.getString("description"), bookmarkLocationJsonObject.getString("locationData"), imageUrlArray, categoriesArray, userLatLng);
                                sortedArrayOfLocations.add(place);
                                if (locationCounter[0] == placeIdArrayList.size()) {
                                    drawBookmarkButtons();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("RESPONSE", error.toString());
                        }
                    });

             */
            // Add the request to the RequestQueue.
            queue.add(jsonObjectRequest);
        }
    }

    public void drawBookmarkButtons() {

        int locationCounter = 0;
        Collections.sort(sortedArrayOfLocations);
        for (int i = 0; i < sortedArrayOfLocations.size(); i++) {
            Log.i("Bookmark Sorted :" + i, sortedArrayOfLocations.get(i).toString());
            final Place place = sortedArrayOfLocations.get(i);

            locationCounter++;

            LinearLayout buttonLinearLayout = findViewById(R.id.bookmarksButtonsLinearLayout);

            ConstraintLayout constraintLayout = new ConstraintLayout(getBaseContext());

            TextView locationTextView = new TextView(getBaseContext());
            TextView locationDistanceFromUserTextView = new TextView(getBaseContext());

            float[] distanceFromUser = place.getDistanceFromUser();
            String distanceFromUserString = Integer.toString(Math.round(distanceFromUser[0]));
            String locationDistanceFromUserTextViewString = distanceFromUserString + "m away";

            locationTextView.setText(place.getLocationName());
            locationTextView.setId(View.generateViewId());
            if (place.getLocationName().length() > 30) {
                locationTextView.setTextSize(26);
            } else if (place.getLocationName().length() > 22) {
                locationTextView.setTextSize(30);
            } else if (place.getLocationName().length() > 15) {
                locationTextView.setTextSize(33);
            } else {
                locationTextView.setTextSize(36);
            }
            locationTextView.setTypeface(Typeface.SERIF);
            locationTextView.setGravity(Gravity.CENTER);

            locationDistanceFromUserTextView.setText(locationDistanceFromUserTextViewString);
            locationDistanceFromUserTextView.setId(View.generateViewId());
            locationDistanceFromUserTextView.setTextSize(12);
            locationDistanceFromUserTextView.setTypeface(Typeface.SERIF);
            locationDistanceFromUserTextView.setGravity(Gravity.CENTER);


            ImageView locationButton = new ImageView(getBaseContext());
            float factor = getBaseContext().getResources().getDisplayMetrics().density;
            locationButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (int) (130 * factor)));
            switch (locationCounter % 4) {
                case 0:
                    locationButton.setImageDrawable(getDrawable(R.drawable.ic_buttonimagevector1));
                    break;
                case 1:
                    locationButton.setImageDrawable(getDrawable(R.drawable.ic_buttonimagevector2));
                    break;
                case 2:
                    locationButton.setImageDrawable(getDrawable(R.drawable.ic_buttonimagevector3));
                    break;
                case 3:
                    locationButton.setImageDrawable(getDrawable(R.drawable.ic_buttonimagevector4));
                    break;
            }

            TypedValue outValue = new TypedValue();
            getBaseContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            locationButton.setBackgroundResource(outValue.resourceId);
            locationButton.setClickable(true);
            locationButton.setFocusable(true);
            locationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent newActivityIntent = new Intent(getBaseContext(), LocationInformation.class);
                    newActivityIntent.putExtra("placeId", place.getPlaceId());
                    startActivity(newActivityIntent);
                }
            });
            locationButton.setId(View.generateViewId());

            constraintLayout.addView(locationButton);
            constraintLayout.addView(locationTextView);
            constraintLayout.addView(locationDistanceFromUserTextView);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);

            constraintSet.connect(locationButton.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            constraintSet.connect(locationButton.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
            constraintSet.connect(locationButton.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
            constraintSet.connect(locationButton.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);

            constraintSet.connect(locationTextView.getId(), ConstraintSet.TOP, locationButton.getId(), ConstraintSet.TOP, 0);
            constraintSet.connect(locationTextView.getId(), ConstraintSet.LEFT, locationButton.getId(), ConstraintSet.LEFT, 0);
            constraintSet.connect(locationTextView.getId(), ConstraintSet.RIGHT, locationButton.getId(), ConstraintSet.RIGHT, 0);
            constraintSet.connect(locationTextView.getId(), ConstraintSet.BOTTOM, locationButton.getId(), ConstraintSet.BOTTOM, 0);

            constraintSet.connect(locationDistanceFromUserTextView.getId(), ConstraintSet.TOP, locationTextView.getId(), ConstraintSet.BOTTOM, 0);
            constraintSet.connect(locationDistanceFromUserTextView.getId(), ConstraintSet.LEFT, locationButton.getId(), ConstraintSet.LEFT, 0);
            constraintSet.connect(locationDistanceFromUserTextView.getId(), ConstraintSet.RIGHT, locationButton.getId(), ConstraintSet.RIGHT, 0);

            constraintSet.applyTo(constraintLayout);

            buttonLinearLayout.addView(constraintLayout);

        }
    }


    public void onBackButtonOnClick(View view) {
        this.finish();
    }
}
