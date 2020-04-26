package com.example.discovernorthumberland;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.Objects;

public class SearchActivity extends AppCompatActivity {

    private SearchView searchView;
    private LocationManager locationManager;
    private ArrayList<Place> placeArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getArray();
    }


    public void getArray() {
        placeArrayList = new ArrayList<>();
        final int[] LISTCOUNTER = {0};
        RequestQueue queue = Volley.newRequestQueue(SearchActivity.this);
        String url = "https://jwhitehead.uk/places";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("message");
                            Log.i("Bap", jsonArray.toString());
                            for (int i = 0; i < jsonArray.length(); i++) {
                                LISTCOUNTER[0]++;
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                JSONArray topicArray = jsonObject.getJSONArray("categories");

                                Log.i("Response :" + i, jsonObject.getString("placeId"));
                                Log.i("Response :" + i, jsonObject.getString("name"));
                                Log.i("Response :" + i, jsonObject.getString("description"));
                                Log.i("Response :" + i, jsonObject.getString("locationData"));
                                Log.i("Response :" + i, jsonObject.getString("imageUrl"));
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
                                placeArrayList.add(place);
                            }
                            if (LISTCOUNTER[0] == jsonArray.length()) {
                                setUpSearch();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(SearchActivity.this, "ERROR CONNECTION TO SERVER FAILURE", Toast.LENGTH_LONG).show();
                        Log.i("RESPONSE", error.toString());
                    }
                });
        queue.add(jsonObjectRequest);
    }


    public void setUpSearch(){

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setIconified(false);

        ListView searchListView = findViewById(R.id.searchListView);
        Collections.sort(placeArrayList);

        final HashMap<String,String> placeHashMap = new HashMap<String,String>();
        ArrayList<String> arrayListOfLocationNames = new ArrayList<>();
        for(int i =0;i<placeArrayList.size();i++){
            placeHashMap.put(placeArrayList.get(i).getLocationName(),placeArrayList.get(i).getPlaceId());
            arrayListOfLocationNames.add(placeArrayList.get(i).getLocationName());
        }
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                arrayListOfLocationNames
        );

        searchListView.setAdapter(arrayAdapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                arrayAdapter.getFilter().filter(s);
                return false;
            }
        });

        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = (String) adapterView.getItemAtPosition(i);
                Intent newActivityIntent = new Intent(getApplicationContext(), LocationInformation.class);
                newActivityIntent.putExtra("placeId", placeHashMap.get(selectedItem));
                startActivity(newActivityIntent);
            }
        });

    }



    public void searchViewOnClick(View view){
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setIconified(false);

    }


    public void onBackButtonOnClick(View view) {
        this.finish();
    }

}
