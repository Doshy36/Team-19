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
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

        String topic = getIntent().getStringExtra("topicId");


        TextView topicTitleTextView = findViewById(R.id.topicTitle);
        topicTitleTextView.setText(topic);


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
        final String finalTopic = topic;

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://jwhitehead.uk/places";

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
                                JSONObject jsonObjectOfLocation = jsonArrayOfLocations.getJSONObject(i); //Create JSONObject for each JSONObject in JSONArray retrieved from Server

                                String[] imageUrlArray = jsonObjectOfLocation.getString("imageUrl").split(","); //String Array of each image url from server

                                //Create ArrayList of categories of location retrieved from server & transfer to String
                                ArrayList<String> categoriesArrayList = new ArrayList<>();
                                JSONArray jsonTopicArray = jsonObjectOfLocation.getJSONArray("categories");
                                for (int k = 0; k < jsonTopicArray.length(); k++) {
                                    categoriesArrayList.add(jsonTopicArray.getString(k));
                                }
                                String[] categoriesArray = categoriesArrayList.toArray(new String[0]);

                                //Taker users location to send to Place Constructor
                                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                                @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                Place place = new Place(jsonObjectOfLocation.getString("placeId"), jsonObjectOfLocation.getString("name"), jsonObjectOfLocation.getString("description"), jsonObjectOfLocation.getString("locationData"), imageUrlArray, categoriesArray, userLatLng);
                                for (String s : place.getCategories()) {
                                    //If place is in the category which the user has selected add to array
                                    if (s.equalsIgnoreCase(finalTopic)) {
                                        sortedArrayOfLocations.add(place);
                                    }
                                }

                                Collections.sort(sortedArrayOfLocations);
                                for (Place p : sortedArrayOfLocations) {
                                    Log.i("PLACE", place.toString());
                                }
                            }

                            int locationCounter = 0;
                            for (int i = 0; i < sortedArrayOfLocations.size(); i++) {
                                final Place place = sortedArrayOfLocations.get(i);

                                locationCounter++;

                                LinearLayout buttonLinearLayout = findViewById(R.id.locationByTopicButtonsLinearLayout);

                                ConstraintLayout constraintLayout = new ConstraintLayout(getBaseContext());

                                TextView locationTextView = new TextView(getBaseContext());
                                TextView locationDistanceFromUserTextView = new TextView(getBaseContext());

                                float[] distanceFromUser = place.getDistanceFromUser();
                                String distanceFromUserString = Integer.toString(Math.round(distanceFromUser[0]));
                                String locationDistanceFromUserTextViewString = distanceFromUserString + "m away";

                                locationTextView.setText(place.getLocationName());
                                locationTextView.setId(View.generateViewId());
                                if(place.getLocationName().length()>30) {
                                    locationTextView.setTextSize(26);
                                }else if(place.getLocationName().length()>22){
                                    locationTextView.setTextSize(30);
                                }else if(place.getLocationName().length()>15){
                                    locationTextView.setTextSize(33);
                                }else {
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

                                constraintSet.connect(locationDistanceFromUserTextView.getId(),ConstraintSet.TOP, locationTextView.getId(),ConstraintSet.BOTTOM,0);
                                constraintSet.connect(locationDistanceFromUserTextView.getId(),ConstraintSet.LEFT, locationButton.getId(),ConstraintSet.LEFT,0);
                                constraintSet.connect(locationDistanceFromUserTextView.getId(),ConstraintSet.RIGHT, locationButton.getId(),ConstraintSet.RIGHT,0);

                                constraintSet.applyTo(constraintLayout);

                                buttonLinearLayout.addView(constraintLayout);


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

    }

    public void onBackButtonOnClick(View view) {
        this.finish();
    }
}
