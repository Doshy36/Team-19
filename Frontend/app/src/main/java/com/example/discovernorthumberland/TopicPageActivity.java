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
import android.widget.ProgressBar;
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

public class TopicPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_page);

        //Retrieves topic chosen into a String variable
        String topic = getIntent().getStringExtra("topicId");

        TextView topicTitleTextView = findViewById(R.id.topicTitle);
        //Sets text on top of the screen to show the current topic chosen
        topicTitleTextView.setText(topic);

        assert topic != null;
        switch (topic) {
            case "Culture":
                topic = "cultural";
                break;
            case "Sports":
                topic = "sport";
                break;
            case "Heritage":
                topic = "historical";
                break;
        }

        //Sets variable to be final for later use
        final String FINAL_TOPIC = topic;

        // Instantiate the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://jwhitehead.uk/places";
        // Initialise a new JsonObjectRequest instance
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Take array of JSON OBJECT OF ALL LOCATIONS
                            JSONArray jsonArrayOfLocations = response.getJSONArray("message");
                            Log.i("Bap", jsonArrayOfLocations.toString());
                            //ArrayList stores locations
                            ArrayList<Place> sortedArrayOfLocations = new ArrayList<>();

                            //Loop through arrayList
                            for (int i = 0; i < jsonArrayOfLocations.length(); i++) {
                                //Create JSONObject for each JSONObject in JSONArray retrieved from Server
                                JSONObject jsonObjectOfLocation = jsonArrayOfLocations.getJSONObject(i);

                                //String Array of each image url from server
                                String[] imageUrlArray = jsonObjectOfLocation.getString("imageUrl").split(",");

                                //Create ArrayList of categories of location retrieved from server & transfer to String
                                ArrayList<String> categoriesArrayList = new ArrayList<>();
                                JSONArray jsonTopicArray = jsonObjectOfLocation.getJSONArray("categories");
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
                                Place place = new Place(jsonObjectOfLocation.getString("placeId"), jsonObjectOfLocation.getString("name"), jsonObjectOfLocation.getString("description"), jsonObjectOfLocation.getString("locationData"), imageUrlArray, categoriesArray, userLatLng);
                                for (String s : place.getCATEGORIES()) {
                                    //If place is in the category which the user has selected add to array
                                    if (s.equalsIgnoreCase(FINAL_TOPIC)) {
                                        sortedArrayOfLocations.add(place);
                                    }
                                }
                                //Sorts array of all locations in ascending order
                                Collections.sort(sortedArrayOfLocations);
                            }

                            //Counter to keep track of how many locations are under this topic
                            int locationCounter = 0;
                            for (int i = 0; i < sortedArrayOfLocations.size(); i++) {
                                final Place PLACE = sortedArrayOfLocations.get(i);

                                locationCounter++;

                                LinearLayout buttonLinearLayout = findViewById(R.id.locationByTopicButtonsLinearLayout);

                                //Create new Constraint layouts for topic buttons
                                ConstraintLayout constraintLayout = new ConstraintLayout(getBaseContext());

                                //Sets Text Views presenting the name of the location and distance from the user
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
                                if(PLACE.getLocationName().length()>30) {
                                    locationTextView.setTextSize(26);
                                }else if(PLACE.getLocationName().length()>22){
                                    locationTextView.setTextSize(30);
                                }else if(PLACE.getLocationName().length()>15){
                                    locationTextView.setTextSize(33);
                                }else {
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

                                //Setting background button image for each location, ensuring each image is different
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

                                //Setting location's onClick to open the location information for that location
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

                                //Adds each location button to the Constraint layout
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

                                constraintSet.connect(locationDistanceFromUserTextView.getId(),ConstraintSet.TOP, locationTextView.getId(),ConstraintSet.BOTTOM,0);
                                constraintSet.connect(locationDistanceFromUserTextView.getId(),ConstraintSet.LEFT, locationButton.getId(),ConstraintSet.LEFT,0);
                                constraintSet.connect(locationDistanceFromUserTextView.getId(),ConstraintSet.RIGHT, locationButton.getId(),ConstraintSet.RIGHT,0);

                                constraintSet.applyTo(constraintLayout);

                                //Add complete Constraint Layout for current bookmark onto Linear Layout
                                buttonLinearLayout.addView(constraintLayout);
                                ProgressBar progressBar = findViewById(R.id.progressBarTopicPage);
                                progressBar.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                        , new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ProgressBar progressBar = findViewById(R.id.progressBarTopicPage);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "ERROR CONNECTION TO SERVER FAILURE", Toast.LENGTH_LONG).show();
                        Log.i("RESPONSE", error.toString());
                    }
                });
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);

    }

    public void onBackButtonOnClick(View view) {
        this.finish();
    }
}
