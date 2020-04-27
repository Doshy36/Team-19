package com.example.discovernorthumberland;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/*
Implements methods for the information relating to Loctions including:
    - Text to speach for information.
    - Bookmarking the location.
    - Sharing the location Data.
    - Navigating to location.
*/
public class LocationInformation extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private ToggleButton toggle;
    private TextView title, mainBody;
    private TextToSpeech textToSpeech;
    private ProgressBar progressBar;
    private String placeId;
    private LatLng locationLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_information);

        placeId = getIntent().getStringExtra("placeId");

        final TextView titleTextView = findViewById(R.id.locationTitleTextView);
        final TextView mainBodyTextView = findViewById(R.id.mainBodyText);

        final LinearLayout mainBodyLinearLayout = findViewById(R.id.mainBodyLinearLayout);
        mainBodyLinearLayout.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressBar);

        // Retrieves information from DB to create a customised location information page.

        // Instantiate the RequestQueue
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

                            // Sets title and description for current location.
                            titleTextView.setText(jsonObject.getString("name"));
                            mainBodyTextView.setText(jsonObject.getString("description"));

                            // Retrieves location coordinates and places in an array.
                            String[] locationDataArray = jsonObject.getString("locationData").split(",");
                            locationLatLng = new LatLng(Double.parseDouble(locationDataArray[0]), Double.parseDouble(locationDataArray[1]));

                            // String Array of each image url from server.
                            String[] imageArray = jsonObject.getString("imageUrl").split(",");

                            // Going through each image in the imageArray.
                            for (int i = 0; i < imageArray.length; i++) {
                                //Creates Image View to hold the current image in the array.
                                ImageView imageView = new ImageView(getBaseContext());

                                // Create new Linear Layout to hold all the images later.
                                LinearLayout imageLinearLayout = findViewById(R.id.imageLinearLayout);
                                if (i != 0) {
                                    //If there is more than 1 image in the array for the current location, creates a View to add to the Linear Layout.
                                    View view = new View(getBaseContext());
                                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                                    layoutParams.setMargins(5, 0, 5, 0);
                                    view.setLayoutParams(layoutParams);
                                    imageLinearLayout.addView(view);

                                }
                                // Adds images to Linear Layout, ensuring it is properly presented to the user.
                                imageLinearLayout.addView(imageView);
                                imageView.setScaleType(ImageView.ScaleType.CENTER);
                                imageView.setAdjustViewBounds(true);
                                imageView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
                                Picasso.get().load(imageArray[i]).into(imageView, new com.squareup.picasso.Callback() {
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
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("RESPONSE", error.toString());
                    }
                });
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);

        String url2 = "https://jwhitehead.uk/ratings/" + placeId;

        // Initialise a new JsonObjectRequest instance.
        JsonObjectRequest jsonObjectRatingRequest = new JsonObjectRequest
                (Request.Method.GET, url2, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("Rating BAP", response.toString());
                            JSONArray responseJSONArray = response.getJSONArray("message");
                            Log.i("Rating BAP2", responseJSONArray.toString());
                            JSONObject responseJSONObject = responseJSONArray.getJSONObject(0);
                            Log.i("Rating of location", responseJSONObject.getString("AVG(rating)"));
                            TextView ratingTextView = findViewById(R.id.previewRatingAverageTextView);

                            // If there exists a rating for the location.
                            if (!responseJSONObject.getString("AVG(rating)").equalsIgnoreCase("null")) {
                                //Retrieves rating and displays it to the user
                                ratingTextView.setText(responseJSONObject.getString("AVG(rating)"));

                                // Stores the rating in a variable, used to show rating in star format.
                                float ratingFloat = Float.parseFloat(responseJSONObject.getString("AVG(rating)"));
                                Log.i("Rating of location as Float", Float.toString(ratingFloat));

                                //Creating the Image Views of all the stars for rating purposes.
                                final ImageView starImageButton1 = findViewById(R.id.previewStarRatingImageView1);
                                final ImageView starImageButton2 = findViewById(R.id.previewStarRatingImageView2);
                                final ImageView starImageButton3 = findViewById(R.id.previewStarRatingImageView3);
                                final ImageView starImageButton4 = findViewById(R.id.previewStarRatingImageView4);
                                final ImageView starImageButton5 = findViewById(R.id.previewStarRatingImageView5);

                                //Setting how the stars are presented in relation to the rating of the location.
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
                                //If not previously rated, show no stars and 0.0.
                                ratingTextView.setText("0.0");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }, new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("RESPONSE", error.toString());
                    }
                });
        // Add the request to the RequestQueue.
        queue.add(jsonObjectRatingRequest);



        // --- Text to Speech ---
        title = findViewById(R.id.locationTitleTextView);
        mainBody = findViewById(R.id.mainBodyText);
        toggle = findViewById(R.id.textToSpeechToggle);

        textToSpeech = new TextToSpeech(this, this);
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggle.isChecked()) {
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
        // Application to speak the title, a pause, and then the description.
        String text = title.getText().toString() + "." + mainBody.getText().toString();
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void silence() {
        //Application to stop speaking
        textToSpeech.speak("", TextToSpeech.QUEUE_FLUSH, null, null);
    }

    public void bookmarkTextViewOnClick(final View view) {
        // Ensuring user is logged in to add bookmarks.
        if (MainActivity.getUserLoggedIn()) {
            // Instantiate the RequestQueue.
            final RequestQueue queue = Volley.newRequestQueue(getBaseContext());

            final boolean[] locationIsBookmarked = {false};

            String url = "https://jwhitehead.uk/bookmarks";

            // Initialise a new JsonObjectRequest instance.
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
                    }, new ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Toast.makeText(getBaseContext(), "ERROR CONNECTION TO SERVER FAILURE", Toast.LENGTH_LONG).show();

                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + MainActivity.getAccessToken());
                    Log.i("Header toString", headers.toString());
                    return headers;
                }
            };

            // Add the request to the RequestQueue.
            queue.add(jsonObjectRequest);

        } else {
            Toast.makeText(LocationInformation.this, "Not Logged in please log in before trying to bookmark the location", Toast.LENGTH_LONG).show();
        }
    }

    public void runBookmarkPopupWindow(View view) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        @SuppressLint("InflateParams") View popupView = layoutInflater.inflate(R.layout.popup_window_bookmark, null);

        // Setting Popup Window properties.
        final PopupWindow popupWindow = new PopupWindow(popupView, 1000, 450, true);
        popupWindow.setElevation(20);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        final Button cancelButton = popupView.findViewById(R.id.bookmarkCancelButton);
        final Button bookmarkSubmitButton = popupView.findViewById(R.id.bookmarkSubmitButton);

        // Setting cancel button onClick action.
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        // Setting submit button onClick action.
        bookmarkSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getBaseContext());
                String url = "https://jwhitehead.uk/bookmarks/add";


                Map<String, String> params = new HashMap<>();
                params.put("userId", MainActivity.getUserID());
                params.put("placeId", placeId);
                JSONObject parameters = new JSONObject(params);

                // Initialise a new JsonObjectRequest instance.
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,parameters, new Response.Listener<JSONObject>() {
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
                }, new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error.
                        Log.d("Rating.Error.Response", error.toString());
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Bearer " + MainActivity.getAccessToken());
                        Log.i("Header toString", headers.toString());
                        return headers;
                    }
                };

                queue.add(jsonObjectRequest);
                popupWindow.dismiss();
            }
        });

    }

    public void runBookmarkPopupWindowDelete(View view) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        @SuppressLint("InflateParams") View popupView = layoutInflater.inflate(R.layout.popup_window_bookmark_delete, null);

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
                String url = "https://jwhitehead.uk/bookmarks/delete/" + placeId;


                // Initialise a new JsonObjectRequest instance.
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
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
                }, new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("BookmarkDelete.Error.Response", error.toString());
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Bearer " + MainActivity.getAccessToken());
                        Log.i("Header toString", headers.toString());
                        return headers;
                    }
                };


                queue.add(jsonObjectRequest);
                popupWindow.dismiss();
            }
        });
    }

    public void ratingTextViewOnClick(View view) {
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        @SuppressLint("InflateParams") View popupView = layoutInflater.inflate(R.layout.popup_window_rating, null);

        // Setting Popup Window properties.
        final PopupWindow popupWindow = new PopupWindow(popupView, 1000, 450, true);
        popupWindow.setElevation(20);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        final int[] USER_RATING = new int[1];
        final ImageView starImageButton1 = popupView.findViewById(R.id.starImageView1);
        final ImageView starImageButton2 = popupView.findViewById(R.id.starImageView2);
        final ImageView starImageButton3 = popupView.findViewById(R.id.starImageView3);
        final ImageView starImageButton4 = popupView.findViewById(R.id.starImageView4);
        final ImageView starImageButton5 = popupView.findViewById(R.id.starImageView5);
        final Button cancelButton = popupView.findViewById(R.id.cancelButton);
        final Button submitButton = popupView.findViewById(R.id.submitButton);

        // Sets rating depending on which star is clicked on.
        starImageButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                USER_RATING[0] = 1;
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
                USER_RATING[0] = 2;
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
                USER_RATING[0] = 3;
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
                USER_RATING[0] = 4;
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
                USER_RATING[0] = 5;
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

        // Setting submit button onClick action.
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MainActivity.getUserLoggedIn()) {
                    // Instantiate the RequestQueue.
                    RequestQueue queue = Volley.newRequestQueue(getBaseContext());
                    String url = "https://jwhitehead.uk/ratings/set";

                    Map<String, Object> params = new HashMap<>();
                    //params.put("userId", MainActivity.getUserID());
                    params.put("placeId", placeId);
                    params.put("rating", USER_RATING[0]);
                    JSONObject parameters = new JSONObject(params);

                    // Initialise a new JsonObjectRequest instance.
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, parameters, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.d("Rating Response", response.toString());
                                boolean ratingResponseBoolean = response.getBoolean("success");
                                Log.d("Rating Response boolean", Boolean.toString(ratingResponseBoolean));
                                if (ratingResponseBoolean) {
                                    Toast.makeText(LocationInformation.this, "Successfully rated " + title.getText() + " :" + USER_RATING[0], Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(LocationInformation.this, "Failure rating user may have already rated this location", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("Rating.Error.Response", error.toString());
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() {
                            HashMap<String, String> headers = new HashMap<>();
                            headers.put("Authorization", "Bearer " + MainActivity.getAccessToken());
                            Log.i("Header toString", headers.toString());
                            return headers;
                        }
                    };
                    queue.add(jsonObjectRequest);
                    popupWindow.dismiss();
                } else {
                    Toast.makeText(LocationInformation.this, "Not Logged in please log in before rating the location", Toast.LENGTH_LONG).show();
                    popupWindow.dismiss();
                }
            }
        });
    }

    // Navigates to location on map.
    public void onNavigationButtonClick(View view) {
        Intent newActivityIntent = new Intent(getBaseContext(), NavigationActivity.class);
        newActivityIntent.putExtra("placeId", placeId);
        newActivityIntent.putExtra("name", title.getText());
        newActivityIntent.putExtra("lat", locationLatLng.latitude);
        newActivityIntent.putExtra("lng", locationLatLng.longitude);
        startActivity(newActivityIntent);
    }

    public void onBackButtonOnClick(View view) {
        this.finish();
    }

    // Sending or Sharing data. Sends locations currently as text. May change in the future.
    public void onShareButtonClick(View view) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,title.getText()+"\n");
        sendIntent.putExtra(Intent.EXTRA_TEXT,mainBody.getText());
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }
}

