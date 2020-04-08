package com.example.discovernorthumberland;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

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
import org.w3c.dom.Text;

public class LocationInformation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_information);
        final String placeId = getIntent().getStringExtra("placeId");

        final TextView titleTextView = findViewById(R.id.locationTitleTextView);
        final TextView mainBodyTextView = findViewById(R.id.mainBodyText);
        final ImageView locationImageView = findViewById(R.id.imageView);


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
                            Picasso.get().load(jsonObject.getString("imageUrl")).into(locationImageView);
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
