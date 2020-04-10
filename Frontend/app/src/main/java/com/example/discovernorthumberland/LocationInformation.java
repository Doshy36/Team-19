package com.example.discovernorthumberland;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationInformation extends AppCompatActivity {

    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_information);
        final String placeId = getIntent().getStringExtra("placeId");

        final TextView titleTextView = findViewById(R.id.locationTitleTextView);
        titleTextView.setVisibility(View.GONE);
        final TextView mainBodyTextView = findViewById(R.id.mainBodyText);
        mainBodyTextView.setVisibility(View.GONE);
        final ImageView locationImageView = findViewById(R.id.imageView);
        locationImageView.setVisibility(View.GONE);

        progressBar = findViewById(R.id.progressBar);


        RequestQueue queue = Volley.newRequestQueue(this);


        String url = "https://jwhitehead.uk/place/" + placeId;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("BAP", response.toString());
                            JSONObject jsonObject = response.getJSONObject("message");
                            Log.i("BAP2", jsonObject.toString());
                            titleTextView.setText(jsonObject.getString("name"));
                            mainBodyTextView.setText(jsonObject.getString("description"));
                            Picasso.get().load(jsonObject.getString("imageUrl")).into(locationImageView,new com.squareup.picasso.Callback(){
                                @Override
                                public void onSuccess() {
                                    if (progressBar != null) {
                                        progressBar.setVisibility(View.GONE);
                                        titleTextView.setVisibility(View.VISIBLE);
                                        mainBodyTextView.setVisibility(View.VISIBLE);
                                        locationImageView.setVisibility(View.VISIBLE);
                                    }
                                }

                                @Override
                                public void onError(Exception e) {

                                }

                            });

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
