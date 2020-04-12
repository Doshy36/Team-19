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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class LocationInformation extends AppCompatActivity implements TextToSpeech.OnInitListener{
    TextView listenBtn;
    TextView title, mainBody;
    TextToSpeech textToSpeech;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_information);
        final String placeId = getIntent().getStringExtra("placeId");

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

        // --- Text to Speech ---
        title = (TextView) findViewById(R.id.locationTitleTextView);
        mainBody = (TextView) findViewById(R.id.mainBodyText);
        listenBtn = (TextView) findViewById(R.id.textToSpeechTextView);

        textToSpeech = new TextToSpeech(this, this);
        listenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeak();
            }
        });
    }

// just so lyle can commit n push - ignore - delete if u see this lol

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
        String text = title.getText().toString() + mainBody.getText().toString();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
        else {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
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
                starImageButton1.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                starImageButton2.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                starImageButton3.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                starImageButton4.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
                starImageButton5.setImageDrawable(getDrawable(R.drawable.ic_star_gold_24dp));
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });
    }
}

