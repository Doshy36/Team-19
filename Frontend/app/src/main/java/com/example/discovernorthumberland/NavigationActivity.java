package com.example.discovernorthumberland;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class NavigationActivity extends FragmentActivity implements OnMapReadyCallback {

    private String placeId;
    private LatLng latLng;
    private GoogleMap mMap;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        placeId = bundle.getString(placeId);
        double lat = bundle.getDouble("lat");
        double lng = bundle.getDouble("lng");
        latLng = new LatLng(lat, lng);

        mapView = findViewById(R.id.mapViewNav);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getBaseContext(), R.raw.style_json));
            if (!success) {
                Log.e("JSON File Catch", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("JASON File Catch", "Can't find style. Error: ", e);
        }

        LocationManager locationManager = (LocationManager) getBaseContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getBaseContext(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        assert locationManager != null;
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setMyLocationEnabled(true);

        // Add a marker in location and move the camera
        mMap.addMarker(new MarkerOptions().position(latLng).title(placeId));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

        if (lastKnownLocation != null) {
            LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(userLocation);
            builder.include(latLng);
            LatLngBounds bounds = builder.build();
            int padding = 150;
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cu);

            String apiKey = getResources().getString(R.string.google_maps_key);
            String directionsURL = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=" + userLocation.latitude + "," + userLocation.longitude +
                    "&destination=" + latLng.latitude + "," + latLng.longitude +
                    "&key=" + apiKey;
            Log.i("Directions URL", directionsURL);
            RequestQueue queue = Volley.newRequestQueue(getBaseContext());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, directionsURL, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.i("Directions Response", response.toString());
                                String responseStatus = response.getString("status");
                                Log.i("Directions Status", responseStatus);
                                if (responseStatus.equalsIgnoreCase("OK")) {
                                    JSONArray routesJSONArray = response.getJSONArray("routes");
                                    Log.i("Directions routes", routesJSONArray.toString());
                                    JSONObject routeJSONObject = routesJSONArray.getJSONObject(0);
                                    Log.i("Directions route 1", routeJSONObject.toString());
                                    JSONArray legsJSONArray = routeJSONObject.getJSONArray("legs");
                                    JSONObject legJSONObject = legsJSONArray.getJSONObject(0);
                                    JSONObject distanceJSONObject = legJSONObject.getJSONObject("distance");
                                    JSONObject durationJSONObject = legJSONObject.getJSONObject("duration");
                                    String distance = distanceJSONObject.getString("text");
                                    Log.i("Directions distance:", distance);
                                    String duration = durationJSONObject.getString("text");
                                    Log.i("Directions duration:", duration);

                                    JSONObject overviewPolyLineJSONObject = routeJSONObject.getJSONObject("overview_polyline");
                                    String overviewPolylineEncodedPolyline = overviewPolyLineJSONObject.getString("points");
                                    Log.i("Overview Polyline Encoded", overviewPolylineEncodedPolyline);

                                    List<LatLng> pointsList = PolyUtil.decode(overviewPolylineEncodedPolyline);
                                    Log.i("Points List", pointsList.toString());
                                    PolylineOptions polylineOptions = new PolylineOptions();
                                    for (int i = 0; i < pointsList.size(); i++) {
                                        polylineOptions.add(pointsList.get(i));
                                    }
                                    mMap.addPolyline(polylineOptions);

                                    TextView nameTextView = findViewById(R.id.locationNameTextView);
                                    TextView etaTextView = findViewById(R.id.etaTextView);
                                    TextView distanceTextView = findViewById(R.id.distanceTextView);

                                    nameTextView.setText(getIntent().getStringExtra("name"));
                                    final String ETA_STRING = "ETA :" + duration;
                                    final String DISTANCE_STRING = "Distance :" + distance;
                                    etaTextView.setText(ETA_STRING);
                                    distanceTextView.setText(DISTANCE_STRING);


                                } else {
                                    Toast.makeText(getBaseContext(), "ERROR", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getBaseContext(), "ERROR CONNECTION TO SERVER FAILURE", Toast.LENGTH_LONG).show();
                        }
                    });

            // Add the request to the RequestQueue.
            queue.add(jsonObjectRequest);

        } else {
            //TODO HANDLE NO LOCATION DATA FOR NAVIGATION
        }


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

}

