package com.example.discovernorthumberland;

import android.accounts.AccountManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }

    public void onBackButtonOnClick(View view) {
        this.finish();
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
                    String userID = "";
                    Log.d("ACCESS TOKEN", accessToken);

                    MainActivity.logUserIn(accessToken,userID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", error.toString());
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
                    String userId = "";
                    Log.d("ACCESS TOKEN", accessToken);

                    MainActivity.logUserIn(accessToken,userId);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", error.toString());
            }
        });

        queue.add(jsonObjectRequest);


    }
}

