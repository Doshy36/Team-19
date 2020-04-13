package com.example.discovernorthumberland;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class TopicPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_page);

        String topic = getIntent().getStringExtra("topicId");


        TextView topicTitleTextView = findViewById(R.id.topicTitle);
        topicTitleTextView.setText(topic);


        if(topic.equals("Culture")){
            topic = "cultural";
        } else if (topic.equals("Sports")) {
            topic = "sport";
        } else if (topic.equals("Heritage")) {
            topic = "historical";
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
                            Log.i("Bap",jsonArrayOfLocations.toString());
                            int locationCounter = 0;
                            for(int i = 0; i < jsonArrayOfLocations.length();i++){
                                final JSONObject jsonObjectOfLocation = jsonArrayOfLocations.getJSONObject(i);
                                //Take each of object which is a location and convert into individual JSONObject
                                //Log.i("Bap",jsonObject.toString());
                                JSONArray topicArray = jsonObjectOfLocation.getJSONArray("categories");
                                //Take array of categories
                                // Logging all the data retrieved from the database for debugging purposes
                                //Log.i("BAP TOPIC MAIN", topicArray.toString());
                                for(int k =0;k<topicArray.length(); k++){
                                    LinearLayout buttonLinearLayout = findViewById(R.id.locationByTopicButtonsLinearLayout);
                                    //if topic selected is in the array from the location object go through this
                                    if(topicArray.getString(k).equalsIgnoreCase(finalTopic)){

                                        locationCounter++;

                                        ConstraintLayout constraintLayout = new ConstraintLayout(getBaseContext());

                                        TextView locationTextView = new TextView(getBaseContext());
                                        locationTextView.setText(jsonObjectOfLocation.getString("name"));
                                        locationTextView.setId(View.generateViewId());
                                        locationTextView.setTextSize(36);
                                        locationTextView.setTypeface(Typeface.SERIF);
                                        locationTextView.setGravity(Gravity.CENTER);


                                        ImageView locationButton = new ImageView(getBaseContext());
                                        float factor = getBaseContext().getResources().getDisplayMetrics().density;
                                        locationButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, (int) (130*factor)));
                                        switch (locationCounter%4){
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
                                                try {
                                                    newActivityIntent.putExtra("placeId", jsonObjectOfLocation.getString("placeId"));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                startActivity(newActivityIntent);
                                            }
                                        });
                                        locationButton.setId(View.generateViewId());

                                        constraintLayout.addView(locationButton);
                                        constraintLayout.addView(locationTextView);

                                        ConstraintSet constraintSet = new ConstraintSet();
                                        constraintSet.clone(constraintLayout);

                                        constraintSet.connect(locationButton.getId(),ConstraintSet.TOP,ConstraintSet.PARENT_ID,ConstraintSet.TOP,0);
                                        constraintSet.connect(locationButton.getId(),ConstraintSet.LEFT,ConstraintSet.PARENT_ID,ConstraintSet.LEFT,0);
                                        constraintSet.connect(locationButton.getId(),ConstraintSet.RIGHT,ConstraintSet.PARENT_ID,ConstraintSet.RIGHT,0);
                                        constraintSet.connect(locationButton.getId(),ConstraintSet.BOTTOM,ConstraintSet.PARENT_ID,ConstraintSet.BOTTOM,0);

                                        constraintSet.connect(locationTextView.getId(),ConstraintSet.TOP,ConstraintSet.PARENT_ID,ConstraintSet.TOP,0);
                                        constraintSet.connect(locationTextView.getId(),ConstraintSet.LEFT,ConstraintSet.PARENT_ID,ConstraintSet.LEFT,0);
                                        constraintSet.connect(locationTextView.getId(),ConstraintSet.RIGHT,ConstraintSet.PARENT_ID,ConstraintSet.RIGHT,0);
                                        constraintSet.connect(locationTextView.getId(),ConstraintSet.BOTTOM,ConstraintSet.PARENT_ID,ConstraintSet.BOTTOM,0);

                                        constraintSet.applyTo(constraintLayout);

                                        buttonLinearLayout.addView(constraintLayout);



                                    }

                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"ERROR CONNECTION TO SERVER FAILURE",Toast.LENGTH_LONG).show();
                        Log.i("RESPONSE",error.toString());
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);

    }

    public void onBackButtonOnClick(View view) {
        this.finish();
    }
}
