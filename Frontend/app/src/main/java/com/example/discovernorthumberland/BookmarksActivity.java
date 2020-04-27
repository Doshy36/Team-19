package com.example.discovernorthumberland;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;

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

    private final ArrayList<String> PLACE_ID_ARRAY_LIST = new ArrayList<>();
    private final ArrayList<Place> SORTED_ARRAY_OF_LOCATIONS = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        //Check if user is logged in before sending request
        if (MainActivity.getUserLoggedIn()) {
            RequestQueue queue = Volley.newRequestQueue(this);
            final String URL = "https://jwhitehead.uk/bookmarks";
            //Send JSON Request to server to get a list of PlaceId's relating to the UserID logged in
            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                //responseLength used to check once the array of place Ids is completed
                                int responseLength = 0;
                                //Take array of JSON OBJECT OF ALL LOCATIONS
                                JSONArray jsonArrayOfLocations = response.getJSONArray("message");
                                Log.i("Bookmark Main Message", jsonArrayOfLocations.toString());
                                //Loop through arrayList
                                for (int i = 0; i < jsonArrayOfLocations.length(); i++) {
                                    JSONObject jsonObjectOfBookmark = jsonArrayOfLocations.getJSONObject(i); //Create JSONObject for each JSONObject in JSONArray retrieved from Server
                                    PLACE_ID_ARRAY_LIST.add(jsonObjectOfBookmark.getString("placeId")); // Add placeId String to Array List
                                    responseLength++;
                                }
                                if (responseLength == jsonArrayOfLocations.length()) {
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
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    //Pass through Authorization through the HTTP header
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + MainActivity.getAccessToken());
                    Log.i("Header toString", headers.toString());
                    return headers;
                }
            };
            // Add the request to the RequestQueue.
            queue.add(jsonObjectRequest);
        } else {
            //Create new Constraint Layout for Button
            ConstraintLayout parentConstraintLayout = findViewById(R.id.bookmarkParentConstraintLayout);
            parentConstraintLayout.setId(View.generateViewId());

            ConstraintLayout constraintLayout = findViewById(R.id.errorUserNotLoggedInConstraintLayout);

            //Create Text View notifying user that they are not logged in and cannot view bookmarks
            TextView notLoggedInErrorTextView = new TextView(getBaseContext());
            String text = "Not Logged In Please Log in before viewing bookmarks.";
            notLoggedInErrorTextView.setText(text);
            notLoggedInErrorTextView.setTextSize(30);
            notLoggedInErrorTextView.setGravity(Gravity.CENTER);
            notLoggedInErrorTextView.setId(View.generateViewId());

            constraintLayout.addView(notLoggedInErrorTextView);

            //Set Constraint to properly present views to user
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);

            constraintSet.connect(notLoggedInErrorTextView.getId(), ConstraintSet.TOP, constraintLayout.getId(), ConstraintSet.TOP, 0);
            constraintSet.connect(notLoggedInErrorTextView.getId(), ConstraintSet.LEFT, constraintLayout.getId(), ConstraintSet.LEFT, 0);
            constraintSet.connect(notLoggedInErrorTextView.getId(), ConstraintSet.RIGHT, constraintLayout.getId(), ConstraintSet.RIGHT, 0);
            constraintSet.connect(notLoggedInErrorTextView.getId(), ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM, 0);

            constraintSet.applyTo(constraintLayout);

            //reload Constraint Layout
            parentConstraintLayout.removeView(constraintLayout);
            parentConstraintLayout.addView(constraintLayout);

        }
    }

    private void createListOfBookmarks() {
        final int[] LOCATION_COUNTER = {0};
        //For each in Place Id bookmark array List retrieve Place data from Server
        for (int i = 0; i < PLACE_ID_ARRAY_LIST.size(); i++) {
            Log.w("Bookmark :" + i, PLACE_ID_ARRAY_LIST.get(i));
            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://jwhitehead.uk/place/" + PLACE_ID_ARRAY_LIST.get(i);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.i("Bookmark response", response.toString());
                                JSONObject bookmarkLocationJsonObject = response.getJSONObject("message");
                                LOCATION_COUNTER[0]++;
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
                                if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions((Activity) getBaseContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                                    return;
                                }
                                LatLng userLatLng;
                                if(locationManager != null) {
                                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                    if (location != null) {
                                        userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    } else {
                                        userLatLng = null;
                                    }
                                }
                                else {
                                    userLatLng= null;
                                }
                                Place place = new Place(bookmarkLocationJsonObject.getString("placeId"), bookmarkLocationJsonObject.getString("name"), bookmarkLocationJsonObject.getString("description"), bookmarkLocationJsonObject.getString("locationData"), imageUrlArray, categoriesArray, userLatLng);
                                SORTED_ARRAY_OF_LOCATIONS.add(place);
                                if (LOCATION_COUNTER[0] == PLACE_ID_ARRAY_LIST.size()) {
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

            // Add the request to the RequestQueue.
            queue.add(jsonObjectRequest);
        }
    }

    private void drawBookmarkButtons() {
        //Used to give each bookmark a different button background
        int locationCounter = 0;
        Collections.sort(SORTED_ARRAY_OF_LOCATIONS);

        //Creates a button for each bookmark
        for (int i = 0; i < SORTED_ARRAY_OF_LOCATIONS.size(); i++) {
            final Place PLACE = SORTED_ARRAY_OF_LOCATIONS.get(i);

            locationCounter++;

            //Create new Linear and Constraint layouts for bookmarks
            LinearLayout buttonLinearLayout = findViewById(R.id.bookmarksButtonsLinearLayout);
            ConstraintLayout constraintLayout = new ConstraintLayout(getBaseContext());

            //Create Text Views presenting the name of the location and distance from user
            TextView locationTextView = new TextView(getBaseContext());
            TextView locationDistanceFromUserTextView = new TextView(getBaseContext());

            //Calculating distance from the user
            float[] distanceFromUser = PLACE.getDISTANCE_FROM_USER();
            int distanceFromUserInt = Math.round(distanceFromUser[0]);
            String locationDistanceFromUserTextViewString;
            if(distanceFromUserInt>1000){
                distanceFromUserInt = distanceFromUserInt/1000;
                String distanceFromUserString = Integer.toString(distanceFromUserInt);
                locationDistanceFromUserTextViewString = distanceFromUserString + "km away";
            }else {
                String distanceFromUserString = Integer.toString(distanceFromUserInt);
                if (distanceFromUserString.equalsIgnoreCase("0")) {
                    locationDistanceFromUserTextViewString = "";
                } else {
                    locationDistanceFromUserTextViewString = distanceFromUserString + "m away";
                }
            }

            //Setting name text in the Text View for the location
            locationTextView.setText(PLACE.getLocationName());
            locationTextView.setId(View.generateViewId());
            if (PLACE.getLocationName().length() > 30) {
                locationTextView.setTextSize(26);
            } else if (PLACE.getLocationName().length() > 22) {
                locationTextView.setTextSize(30);
            } else if (PLACE.getLocationName().length() > 15) {
                locationTextView.setTextSize(33);
            } else {
                locationTextView.setTextSize(36);
            }
            locationTextView.setTypeface(Typeface.SERIF);
            locationTextView.setGravity(Gravity.CENTER);

            //Setting distance text in Text View
            locationDistanceFromUserTextView.setText(locationDistanceFromUserTextViewString);
            locationDistanceFromUserTextView.setId(View.generateViewId());
            locationDistanceFromUserTextView.setTextSize(12);
            locationDistanceFromUserTextView.setTypeface(Typeface.SERIF);
            locationDistanceFromUserTextView.setGravity(Gravity.CENTER);

            //Creating background button for the location Text Views
            ImageView locationButton = new ImageView(getBaseContext());
            float factor = getBaseContext().getResources().getDisplayMetrics().density;
            locationButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (int) (130 * factor)));

            //Setting background button image for each bookmark, ensuring each image is different
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

            //Setting bookmark's onClick to open the location information for that location
            TypedValue outValue = new TypedValue();
            getBaseContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            locationButton.setBackgroundResource(outValue.resourceId);
            locationButton.setClickable(true);
            locationButton.setFocusable(true);
            locationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent newActivityIntent = new Intent(getBaseContext(), LocationInformation.class);
                    newActivityIntent.putExtra("placeId", PLACE.getPlaceId());
                    startActivity(newActivityIntent);
                }
            });
            locationButton.setId(View.generateViewId());

            //Add bookmark button to the Constraint layout
            constraintLayout.addView(locationButton);
            constraintLayout.addView(locationTextView);
            constraintLayout.addView(locationDistanceFromUserTextView);

            //Set Constraint to properly present views to user
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

            //Add complete Constraint Layout for current bookmark onto Linear Layout
            buttonLinearLayout.addView(constraintLayout);

        }
    }


    public void onBackButtonOnClick(View view) {
        this.finish();
    }
}
