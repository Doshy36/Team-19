package com.example.discovernorthumberland;

import android.Manifest;
import android.annotation.SuppressLint;
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
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class MainMenuActivity extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    private MapView mapView;
    private LocationManager locationManager;
    private ConstraintLayout progressBarConstraintLayout;
    private ProgressBar progressBar;
    private TextView errorTextView;
    private Button retryButton;
    private ClusterManager<Place> mClusterManager;

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

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
        View rootView = inflater.inflate(R.layout.activity_main_menu, container, false);

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
                            int counter = 0;
                            ArrayList<Place> placeArrayList = new ArrayList<>();
                            Log.i("Bap", jsonArray.toString());


                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                counter++;
                                String[] imageUrlArray = jsonObject.getString("imageUrl").split(","); //String Array of each image url from server
                                //Create ArrayList of categories of location retrieved from server & transfer to String

                                ArrayList<String> categoriesArrayList = new ArrayList<>();
                                JSONArray jsonTopicArray = jsonObject.getJSONArray("categories");
                                for (int k = 0; k < jsonTopicArray.length(); k++) {
                                    categoriesArrayList.add(jsonTopicArray.getString(k));
                                }
                                String[] categoriesArray = categoriesArrayList.toArray(new String[0]);

                                //Taker users location to send to Place Constructor
                                locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
                                @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());

                                Place place = new Place(jsonObject.getString("placeId"), jsonObject.getString("name"), jsonObject.getString("description"), jsonObject.getString("locationData"), imageUrlArray, categoriesArray, userLatLng);
                                Log.i("place." + place.getLocationName(), place.toString());

                                placeArrayList.add(place);


                                /*
                                Marker locationMarker = mMap.addMarker(new MarkerOptions().position(place.getPosition()).title(place.getLocationName()));
                                locationMarker.setTag(place.getPlaceId());
                                markerArrayList.add(locationMarker);

                                 */


                            }
                            if (counter == jsonArray.length()) {
                                setUpClusterer(placeArrayList);
                            }
                            progressBarConstraintLayout.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getActivity(), "ERROR CONNECTION TO SERVER FAILURE", Toast.LENGTH_LONG).show();

                        progressBar.setVisibility(View.GONE);
                        errorTextView.setVisibility(View.VISIBLE);
                        retryButton.setVisibility(View.VISIBLE);
                        Log.i("RESPONSE", error.toString());
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


    public void setUpClusterer(ArrayList<Place> placeArrayList) {

        mClusterManager = new ClusterManager<Place>(requireActivity(), mMap);
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.addItems(placeArrayList);
        mClusterManager.setOnClusterItemInfoWindowClickListener(new ClusterManager.OnClusterItemInfoWindowClickListener<Place>() {
            @Override
            public void onClusterItemInfoWindowClick(Place item) {
                Intent newActivityIntent = new Intent(getActivity(), LocationInformation.class);
                newActivityIntent.putExtra("placeId", item.getPlaceId());
                startActivity(newActivityIntent);
            }
        });

    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent newActivityIntent = new Intent(getActivity(), LocationInformation.class);
        newActivityIntent.putExtra("placeId", Objects.requireNonNull(marker.getTag()).toString());
        startActivity(newActivityIntent);
    }


}

