package com.example.discovernorthumberland;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LocationInformation extends AppCompatActivity implements TextToSpeech.OnInitListener{
    ToggleButton toggle;
    TextView title, mainBody;
    TextToSpeech textToSpeech;
    private ProgressBar progressBar;
    private String placeId;
    private int userRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_information);
        placeId = getIntent().getStringExtra("placeId");

        final TextView titleTextView = findViewById(R.id.locationTitleTextView);
        final TextView mainBodyTextView = findViewById(R.id.mainBodyText);
        final ImageView locationImageView = findViewById(R.id.imageView);

        final LinearLayout mainBodyLinearLayout = findViewById(R.id.mainBodyLinearLayout);
        mainBodyLinearLayout.setVisibility(View.GONE);
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
                            Picasso.get().load(jsonObject.getString("imageUrl")).into(locationImageView, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    if (progressBar != null) {
                                        progressBar.setVisibility(View.GONE);
                                        mainBodyLinearLayout.setVisibility(View.VISIBLE);
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
        String url2 = "https://jwhitehead.uk/ratings/" + placeId;
        JsonObjectRequest jsonObjectRatingRequest = new JsonObjectRequest
                (Request.Method.GET, url2, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("Rating BAP", response.toString());
                            JSONArray responseJSONArray = response.getJSONArray("message");
                            Log.i("Rating BAP2", responseJSONArray.toString());
                            JSONObject responseJSONObject = responseJSONArray.getJSONObject(0);
                            Log.i("Rating of location", responseJSONObject.getString("SUM(rating)"));
                            //float rating = Float.parseFloat(responseJSONObject.getString("SUM(rating)"));
                            TextView ratingTextView = findViewById(R.id.previewRatingAverageTextView);
                            if (!responseJSONObject.getString("SUM(rating)").equalsIgnoreCase("null")) {
                                ratingTextView.setText(responseJSONObject.getString("SUM(rating)"));
                                float ratingFloat = Float.parseFloat(responseJSONObject.getString("SUM(rating)"));
                                Log.i("Rating of location as Float", Float.toString(ratingFloat));
                                final ImageView starImageButton1 = findViewById(R.id.previewStarRatingImageView1);
                                final ImageView starImageButton2 = findViewById(R.id.previewStarRatingImageView2);
                                final ImageView starImageButton3 = findViewById(R.id.previewStarRatingImageView3);
                                final ImageView starImageButton4 = findViewById(R.id.previewStarRatingImageView4);
                                final ImageView starImageButton5 = findViewById(R.id.previewStarRatingImageView5);
                                if (ratingFloat > 0 && ratingFloat < 0.75) {
                                    starImageButton1.setImageDrawable(getDrawable(R.drawable.ic_star_half_gold_24dp));
                                    starImageButton2.setImageDrawable(getDrawable(R.drawable.ic_star_border_grey_24dp));
                                    starImageButton3.setImageDrawable(getDrawable(R.drawable.ic_star_border_grey_24dp));
                                    starImageButton4.setImageDrawable(getDrawable(R.drawable.ic_star_border_grey_24dp));
                                    starImageButton5.setImageDrawable(getDrawable(R.drawable.ic_star_border_grey_24dp));
                                } else if (ratingFloat > 0.75 && ratingFloat < 1.25) {
                                    starImageButton1.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                                    starImageButton2.setImageDrawable(getDrawable(R.drawable.ic_star_border_grey_24dp));
                                    starImageButton3.setImageDrawable(getDrawable(R.drawable.ic_star_border_grey_24dp));
                                    starImageButton4.setImageDrawable(getDrawable(R.drawable.ic_star_border_grey_24dp));
                                    starImageButton5.setImageDrawable(getDrawable(R.drawable.ic_star_border_grey_24dp));
                                } else if (ratingFloat > 1.25 && ratingFloat < 1.75) {
                                    starImageButton1.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                                    starImageButton2.setImageDrawable(getDrawable(R.drawable.ic_star_half_gold_24dp));
                                    starImageButton3.setImageDrawable(getDrawable(R.drawable.ic_star_border_grey_24dp));
                                    starImageButton4.setImageDrawable(getDrawable(R.drawable.ic_star_border_grey_24dp));
                                    starImageButton5.setImageDrawable(getDrawable(R.drawable.ic_star_border_grey_24dp));
                                } else if (ratingFloat > 1.75 && ratingFloat < 2.25) {
                                    starImageButton1.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                                    starImageButton2.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                                    starImageButton3.setImageDrawable(getDrawable(R.drawable.ic_star_border_grey_24dp));
                                    starImageButton4.setImageDrawable(getDrawable(R.drawable.ic_star_border_grey_24dp));
                                    starImageButton5.setImageDrawable(getDrawable(R.drawable.ic_star_border_grey_24dp));
                                } else if (ratingFloat > 2.25 && ratingFloat < 2.75) {
                                    starImageButton1.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                                    starImageButton2.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                                    starImageButton3.setImageDrawable(getDrawable(R.drawable.ic_star_half_gold_24dp));
                                    starImageButton4.setImageDrawable(getDrawable(R.drawable.ic_star_border_grey_24dp));
                                    starImageButton5.setImageDrawable(getDrawable(R.drawable.ic_star_border_grey_24dp));
                                } else if (ratingFloat > 2.75 && ratingFloat < 3.25) {
                                    starImageButton1.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                                    starImageButton2.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                                    starImageButton3.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                                    starImageButton4.setImageDrawable(getDrawable(R.drawable.ic_star_border_grey_24dp));
                                    starImageButton5.setImageDrawable(getDrawable(R.drawable.ic_star_border_grey_24dp));
                                } else if (ratingFloat > 3.25 && ratingFloat < 3.75) {
                                    starImageButton1.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                                    starImageButton2.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                                    starImageButton3.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                                    starImageButton4.setImageDrawable(getDrawable(R.drawable.ic_star_half_gold_24dp));
                                    starImageButton5.setImageDrawable(getDrawable(R.drawable.ic_star_border_grey_24dp));
                                } else if (ratingFloat > 3.75 && ratingFloat < 4.25) {
                                    starImageButton1.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                                    starImageButton2.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                                    starImageButton3.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                                    starImageButton4.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                                    starImageButton5.setImageDrawable(getDrawable(R.drawable.ic_star_border_grey_24dp));
                                } else if (ratingFloat > 4.25 && ratingFloat < 4.75) {
                                    starImageButton1.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                                    starImageButton2.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                                    starImageButton3.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                                    starImageButton4.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                                    starImageButton5.setImageDrawable(getDrawable(R.drawable.ic_star_half_gold_24dp));
                                } else if (ratingFloat > 4.75) {
                                    starImageButton1.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                                    starImageButton2.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                                    starImageButton3.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                                    starImageButton4.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                                    starImageButton5.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                                }
                            } else {
                                ratingTextView.setText("0.0");
                            }

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
        queue.add(jsonObjectRatingRequest);


        // --- Text to Speech ---
        title = (TextView) findViewById(R.id.locationTitleTextView);
        mainBody = (TextView) findViewById(R.id.mainBodyText);
        toggle = (ToggleButton) findViewById(R.id.textToSpeechToggle);

        textToSpeech = new TextToSpeech(this, this);
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggle.isChecked()){
                    textToSpeak();
                } else {
                    silence();
                }
            }
        });
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("error", "This Language is not supported");
            }
        } else {
            Log.e("error", "Failed to Initialize");
        }
    }

    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    private void textToSpeak() {
        String text = title.getText().toString() + "." + mainBody.getText().toString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }


    public void bookmarkTextViewOnClick(final View view) {
        if (MainActivity.getUserLoggedIn()) {


            final RequestQueue queue = Volley.newRequestQueue(getBaseContext());

            final boolean[] locationIsBookmarked = {false};

            String url = "https://jwhitehead.uk/bookmarks/" + MainActivity.getUserID();

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONArray jsonArray = response.getJSONArray("message");
                                Log.i("Bookmark Check Bap", jsonArray.toString());
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    Log.i("Bookmark Check Bap :" + i, jsonObject.toString());
                                    if (jsonObject.getString("placeId").equalsIgnoreCase(placeId)) {
                                        locationIsBookmarked[0] = true;
                                    }
                                }
                                if (locationIsBookmarked[0]) {
                                    runBookmarkPopupWindowDelete(view);
                                } else {
                                    runBookmarkPopupWindow(view);
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
            Toast.makeText(LocationInformation.this, "Not Logged in please log in before rating the location", Toast.LENGTH_LONG).show();
        }
    }

    public void runBookmarkPopupWindow(View view) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_window_bookmark, null);

        final PopupWindow popupWindow = new PopupWindow(popupView, 1000, 450, true);
        popupWindow.setElevation(20);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        final Button cancelButton = popupView.findViewById(R.id.bookmarkCancelButton);
        final Button bookmarkSubmitButton = popupView.findViewById(R.id.bookmarkSubmitButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        bookmarkSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestQueue queue = Volley.newRequestQueue(getBaseContext());
                // Instantiate the RequestQueue.
                String url = "https://jwhitehead.uk/bookmarks/add";

                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", MainActivity.getUserID());
                params.put("placeId", placeId);
                JSONObject parameters = new JSONObject(params);

                // Initialize a new JsonArrayRequest instance
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("Bookmark creation.Response", response.toString());
                            boolean ratingResponseBoolean = response.getBoolean("success");
                            Log.d("Bookmark Response boolean", Boolean.toString(ratingResponseBoolean));
                            if (ratingResponseBoolean) {
                                Toast.makeText(LocationInformation.this, "Successfully Bookmarked " + title.getText(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LocationInformation.this, "Failure rating user may have already Bookmarked this location", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Rating.Error.Response", error.toString());
                    }
                });

                queue.add(jsonObjectRequest);
                popupWindow.dismiss();
            }
        });

    }

    public void runBookmarkPopupWindowDelete(View view) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_window_bookmark_delete, null);

        final PopupWindow popupWindow = new PopupWindow(popupView, 1000, 450, true);
        popupWindow.setElevation(20);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        final Button cancelButton = popupView.findViewById(R.id.bookmarkCancelButton);
        final Button bookmarkDeleteButton = popupView.findViewById(R.id.deleteBookmarkButton);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        bookmarkDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestQueue queue = Volley.newRequestQueue(getBaseContext());
                String url = "https://jwhitehead.uk/bookmarks/delete";

                Map<String, String> params = new HashMap<String, String>();
                params.put("userId", MainActivity.getUserID());
                params.put("placeId", placeId);
                JSONObject parameters = new JSONObject(params);

                // Initialize a new JsonArrayRequest instance
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, parameters, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("Delete book mark Response", response.toString());
                            boolean ratingResponseBoolean = response.getBoolean("success");
                            Log.d("Delete Bookmark response boolean", Boolean.toString(ratingResponseBoolean));
                            if (ratingResponseBoolean) {
                                Toast.makeText(LocationInformation.this, "Successfully deleted bookmark " + title.getText(), Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(LocationInformation.this, "Failure", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LocationInformation.this, "Failure", Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("Rating.Error.Response", error.toString());
                    }
                });

                queue.add(jsonObjectRequest);
                popupWindow.dismiss();
            }
        });
    }



    private void silence() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak("", TextToSpeech.QUEUE_FLUSH, null, null);
        }
        else {
            textToSpeech.speak("", TextToSpeech.QUEUE_FLUSH, null);
        }
    }


    public void ratingTextViewOnClick(View view) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.popup_window_rating, null);

        final PopupWindow popupWindow = new PopupWindow(popupView, 1000, 450, true);
        popupWindow.setElevation(20);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        final ImageView starImageButton1 = popupView.findViewById(R.id.starImageView1);
        final ImageView starImageButton2 = popupView.findViewById(R.id.starImageView2);
        final ImageView starImageButton3 = popupView.findViewById(R.id.starImageView3);
        final ImageView starImageButton4 = popupView.findViewById(R.id.starImageView4);
        final ImageView starImageButton5 = popupView.findViewById(R.id.starImageView5);
        final Button cancelButton = popupView.findViewById(R.id.cancelButton);
        final Button submitButton = popupView.findViewById(R.id.submitButton);


        starImageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRating = 1;
                starImageButton1.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                starImageButton2.setImageDrawable(getDrawable(R.drawable.ic_star_border_grey_24dp));
                starImageButton3.setImageDrawable(getDrawable(R.drawable.ic_star_border_grey_24dp));
                starImageButton4.setImageDrawable(getDrawable(R.drawable.ic_star_border_grey_24dp));
                starImageButton5.setImageDrawable(getDrawable(R.drawable.ic_star_border_grey_24dp));

            }
        });

        starImageButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRating = 2;
                starImageButton1.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                starImageButton2.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                starImageButton3.setImageDrawable(getDrawable(R.drawable.ic_star_border_grey_24dp));
                starImageButton4.setImageDrawable(getDrawable(R.drawable.ic_star_border_grey_24dp));
                starImageButton5.setImageDrawable(getDrawable(R.drawable.ic_star_border_grey_24dp));
            }
        });

        starImageButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRating = 3;
                starImageButton1.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                starImageButton2.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                starImageButton3.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                starImageButton4.setImageDrawable(getDrawable(R.drawable.ic_star_border_grey_24dp));
                starImageButton5.setImageDrawable(getDrawable(R.drawable.ic_star_border_grey_24dp));
            }
        });

        starImageButton4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRating = 4;
                starImageButton1.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                starImageButton2.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                starImageButton3.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                starImageButton4.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                starImageButton5.setImageDrawable(getDrawable(R.drawable.ic_star_border_grey_24dp));
            }
        });

        starImageButton5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userRating = 5;
                starImageButton1.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                starImageButton2.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                starImageButton3.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                starImageButton4.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                starImageButton5.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.getUserLoggedIn()) {
                    // Instantiate the RequestQueue.
                    RequestQueue queue = Volley.newRequestQueue(getBaseContext());
                    String url = "https://jwhitehead.uk/ratings/set";

                    Map<String, String> params = new HashMap<String, String>();
                    params.put("userId", MainActivity.getUserID());
                    params.put("placeId", placeId);
                    params.put("rating", Integer.toString(userRating));
                    JSONObject parameters = new JSONObject(params);

                    // Initialize a new JsonArrayRequest instance
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.d("Rating Response", response.toString());
                                boolean ratingResponseBoolean = response.getBoolean("success");
                                Log.d("Rating Response boolean", Boolean.toString(ratingResponseBoolean));
                                if (ratingResponseBoolean) {
                                    Toast.makeText(LocationInformation.this, "Successfully rated " + title.getText() + " :" + Integer.toString(userRating), Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(LocationInformation.this, "Failure rating user may have already rated this location", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("Rating.Error.Response", error.toString());
                        }
                    });

                    queue.add(jsonObjectRequest);
                    popupWindow.dismiss();

                } else {
                    Toast.makeText(LocationInformation.this, "Not Logged in please log in before rating the location", Toast.LENGTH_LONG).show();
                    popupWindow.dismiss();
                }
            }
        });
    }

    public void onBackButtonOnClick(View view) {
        this.finish();
    }
}

