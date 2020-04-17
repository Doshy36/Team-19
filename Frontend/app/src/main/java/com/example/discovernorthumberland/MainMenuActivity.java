package com.example.discovernorthumberland;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class MainMenuActivity extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener{

    private GoogleMap mMap;
    private MapView mapView;
    private LocationManager locationManager;
    private ConstraintLayout progressBarConstraintLayout;
    private ProgressBar progressBar;
    private TextView errorTextView;
    private Button retryButton;

    private LocationListener locationListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.activity_main_menu, container, false);

        mapView = rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        ImageView heritageButton = rootView.findViewById(R.id.heritageButtonImage);
        heritageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newActivityIntent = new Intent(getActivity(), TopicPageActivity.class);
                newActivityIntent.putExtra("topicId", "Heritage");
                startActivity(newActivityIntent);
            }
        });
        ImageView cultureButton = rootView.findViewById(R.id.cultureButtonImage);
        cultureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newActivityIntent = new Intent(getActivity(), TopicPageActivity.class);
                newActivityIntent.putExtra("topicId", "Culture");
                startActivity(newActivityIntent);
            }
        });
        ImageView cuisineButton = rootView.findViewById(R.id.cuisineButtonImage);
        cuisineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newActivityIntent = new Intent(getActivity(), TopicPageActivity.class);
                newActivityIntent.putExtra("topicId", "Cuisine");
                startActivity(newActivityIntent);

            }
        });
        ImageView sportButton = rootView.findViewById(R.id.sportsButtonImage);
        sportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newActivityIntent = new Intent(getActivity(), TopicPageActivity.class);
                newActivityIntent.putExtra("topicId", "Sports");
                startActivity(newActivityIntent);
            }
        });

        progressBarConstraintLayout = rootView.findViewById(R.id.progressBarConstraintLayout);
        progressBar = rootView.findViewById(R.id.mainPageLoadingProgressBar);
        errorTextView = rootView.findViewById(R.id.errorTextView);
        retryButton = rootView.findViewById(R.id.retryButton);



        return rootView;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.style_json));
            if (!success) {
                Log.e("JSON File Catch", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("JASON File Catch", "Can't find style. Error: ", e);
        }


        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://jwhitehead.uk/places";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("message");
                            Log.i("Bap",jsonArray.toString());
                            for(int i = 0; i < jsonArray.length();i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                JSONArray topicArray = jsonObject.getJSONArray("categories");

                                // Logging all the data retrieved from the database for debugging purposes delete when done yes yes 1 2
                                Log.i("BAP TOPIC MAIN", topicArray.toString());
                                for(int k =0;k<topicArray.length(); k++){
                                    Log.i("topic :" + k,topicArray.getString(k));
                                }
                                Log.i("Response :" + i,jsonObject.getString("placeId"));
                                Log.i("Response :" + i,jsonObject.getString("name"));
                                Log.i("Response :" + i,jsonObject.getString("description"));
                                Log.i("Response :" + i,jsonObject.getString("locationData"));
                                Log.i("Response :" + i,jsonObject.getString("imageUrl"));

                                String[] locationDataArray = jsonObject.getString("locationData").split(",");
                                LatLng locationLatLng = new LatLng(Double.parseDouble(locationDataArray[0]),Double.parseDouble(locationDataArray[1]));
                                Marker locationMarker = mMap.addMarker(new MarkerOptions().position(locationLatLng).title(jsonObject.getString("name")));
                                locationMarker.setTag(jsonObject.getString("placeId"));

                            }
                            progressBarConstraintLayout.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getActivity(),"ERROR CONNECTION TO SERVER FAILURE",Toast.LENGTH_LONG).show();

                        progressBar.setVisibility(View.GONE);
                        errorTextView.setVisibility(View.VISIBLE);
                        retryButton.setVisibility(View.VISIBLE);
                        Log.i("RESPONSE",error.toString());
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);


        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null) {
            LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 16));
        } else {
           // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(greysMonument, 16));
        }
        mMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMyLocationEnabled(true);
        mMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }



    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent newActivityIntent = new Intent(getActivity(), LocationInformation.class);
        newActivityIntent.putExtra("placeId", Objects.requireNonNull(marker.getTag()).toString());
        startActivity(newActivityIntent);
    }



}

