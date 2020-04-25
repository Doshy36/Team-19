package com.example.discovernorthumberland;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

import android.app.SearchManager;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SearchView searchView;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getArray();
    }

    public ArrayList<Place> getArray(){
        final ArrayList<Place> LISTOFPLACES = new ArrayList<>();
        final int[] LISTCOUNTER = {0};
        RequestQueue queue = Volley.newRequestQueue(SearchActivity.this);
        String url = "https://jwhitehead.uk/places";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("message");
                            Log.i("Bap",jsonArray.toString());
                            for(int i = 0; i < jsonArray.length();i++){
                                LISTCOUNTER[0]++;
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                JSONArray topicArray = jsonObject.getJSONArray("categories");

                                Log.i("Response :" + i,jsonObject.getString("placeId"));
                                Log.i("Response :" + i,jsonObject.getString("name"));
                                Log.i("Response :" + i,jsonObject.getString("description"));
                                Log.i("Response :" + i,jsonObject.getString("locationData"));
                                Log.i("Response :" + i,jsonObject.getString("imageUrl"));
                                String[] imageUrlArray = jsonObject.getString("imageUrl").split(","); //String Array of each image url from server
                                //Create ArrayList of categories of location retrieved from server & transfer to String
                                ArrayList<String> categoriesArrayList = new ArrayList<>();
                                JSONArray jsonTopicArray = jsonObject.getJSONArray("categories");
                                for (int k = 0; k < jsonTopicArray.length(); k++) {
                                    categoriesArrayList.add(jsonTopicArray.getString(k));
                                }
                                String[] categoriesArray = categoriesArrayList.toArray(new String[0]);
                                //Take users location to send to Place Constructor
                                locationManager = (LocationManager) Objects.requireNonNull(SearchActivity.this).getSystemService(Context.LOCATION_SERVICE);
                                @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                Place place = new Place(jsonObject.getString("placeId"), jsonObject.getString("name"), jsonObject.getString("description"), jsonObject.getString("locationData"), imageUrlArray, categoriesArray, userLatLng);
                                Log.i("Array List Test", place.toString());
                                LISTOFPLACES.add(place);
                            }
                            if(LISTCOUNTER[0] == jsonArray.length()){
                                ArrayList<String> placeList = new ArrayList<String>();
                                //dynamic list to store ALL location names into

                                for(int i = 0; i < jsonArray.length();i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    JSONArray topicArray = jsonObject.getJSONArray("categories");
                                    String[] imageUrlArray = jsonObject.getString("imageUrl").split(",");
                                    ArrayList<String> categoriesArrayList = new ArrayList<>();
                                    JSONArray jsonTopicArray = jsonObject.getJSONArray("categories");
                                    for (int k = 0; k < jsonTopicArray.length(); k++) {
                                        categoriesArrayList.add(jsonTopicArray.getString(k));
                                    }
                                    String[] categoriesArray = categoriesArrayList.toArray(new String[0]);
                                    //Take users location to send to Place Constructor
                                    locationManager = (LocationManager) Objects.requireNonNull(SearchActivity.this).getSystemService(Context.LOCATION_SERVICE);
                                    @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                    LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    Place place = new Place(jsonObject.getString("placeId"), jsonObject.getString("name"), jsonObject.getString("description"), jsonObject.getString("locationData"), imageUrlArray, categoriesArray, userLatLng);

                                    placeList.add(place.getLocationName());
                                    //adds all the names of locations to an array


                                }

                                recyclerView = findViewById(R.id.recyclerListOfLocations);
                                final RecyclerViewAdapter adapter = new RecyclerViewAdapter(SearchActivity.this, placeList, LISTOFPLACES);


                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));

                                searchView = findViewById(R.id.searchView);
                                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                    @Override
                                    public boolean onQueryTextSubmit(String query) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onQueryTextChange(String newText) {

                                        adapter.getFilter().filter(newText);
                                        return false;
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(SearchActivity.this,"ERROR CONNECTION TO SERVER FAILURE",Toast.LENGTH_LONG).show();
                        Log.i("RESPONSE",error.toString());
                    }
                });
        queue.add(jsonObjectRequest);
        return LISTOFPLACES;
    }

    public void onBackButtonOnClick(View view) {
        this.finish();
    }

}
