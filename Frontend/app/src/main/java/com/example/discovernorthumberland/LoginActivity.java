package com.example.discovernorthumberland;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
Class for the Login page and relating methods.
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (MainActivity.getUserLoggedIn()) {
            setContentView(R.layout.activity_loggedin);
        } else {
            setContentView(R.layout.activity_login);
        }
    }

    public void onBackButtonOnClick(View view) {
        this.finish();
    }

    public void logOutButtonOnClick(View view) {
        MainActivity.logOut();
        Intent intent = new Intent();
        intent.putExtra("Log Status", "LoggedOut");
        setResult(RESULT_OK, intent);
        finish();

    }

    public void onOkButtonOnClick(View view){
        Intent intent = new Intent();
        intent.putExtra("Log Status", "LoggedIn");
        setResult(RESULT_OK, intent);
        finish();
    }

    public void registerButtonOnClick(View view) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://jwhitehead.uk/auth/register";

        Map<String, String> params = new HashMap<String, String>();
        EditText emailEditText = findViewById(R.id.emailEnter);
        EditText passwordEditText = findViewById(R.id.passwordEnter);
        params.put("email", emailEditText.getText().toString());
        params.put("password", passwordEditText.getText().toString());
        JSONObject parameters = new JSONObject(params);

        // Initialize a new JsonArrayRequest instance
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response", response.toString());
                try {
                    String accessToken = response.getString("accessToken");
                    String userId = response.getString("userId");
                    Log.d("ACCESS TOKEN", accessToken);
                    Log.d("USER ID", userId);

                    MainActivity.logUserIn(accessToken, userId);
                    setContentView(R.layout.activity_loggedin);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", error.toString());
                Toast.makeText(getBaseContext(), "Error, Please try again", Toast.LENGTH_LONG).show();
            }
        });
        queue.add(jsonObjectRequest);
    }

    public void loginButtonOnClick(View view) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://jwhitehead.uk/auth/login";

        Map<String, String> params = new HashMap<String, String>();
        EditText emailEditText = findViewById(R.id.emailEnter);
        EditText passwordEditText = findViewById(R.id.passwordEnter);
        params.put("email", emailEditText.getText().toString());
        params.put("password", passwordEditText.getText().toString());
        JSONObject parameters = new JSONObject(params);

        // Initialize a new JsonArrayRequest instance
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("Response", response.toString());
                try {
                    String accessToken = response.getString("accessToken");
                    String userId = response.getString("userId");
                    Log.d("ACCESS TOKEN", accessToken);
                    Log.d("USER ID", userId);
                    MainActivity.logUserIn(accessToken, userId);
                    setContentView(R.layout.activity_loggedin);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error.
                Log.d("Error.Response", error.toString());
                if (error.toString().equalsIgnoreCase("com.android.volley.AuthFailureError")) {
                    Toast.makeText(getBaseContext(), "Incorrect email or password, Please try again", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(), "Connection Error, Please try again", Toast.LENGTH_LONG).show();
                }
            }
        });
        queue.add(jsonObjectRequest);
    }
}

