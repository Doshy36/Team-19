package com.example.discovernorthumberland;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class BookmarksActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);

        if(MainActivity.getUserLoggedIn()){

        }else{
            ConstraintLayout parentConstraintLayout = findViewById(R.id.bookmarkParentConstraintLayout);
            parentConstraintLayout.setId(View.generateViewId());

            ConstraintLayout constraintLayout = findViewById(R.id.errorUserNotLoggedInConstraintLayout);

            TextView notLoggedInErrorTextView = new TextView(getBaseContext());
            String text = "Not Logged In Please Log in before viewing bookmarks.";
            notLoggedInErrorTextView.setText(text);
            notLoggedInErrorTextView.setTextSize(30);
            notLoggedInErrorTextView.setGravity(Gravity.CENTER);
            notLoggedInErrorTextView.setId(View.generateViewId());

            constraintLayout.addView(notLoggedInErrorTextView);


            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);

            constraintSet.connect(notLoggedInErrorTextView.getId(), ConstraintSet.TOP, constraintLayout.getId() ,ConstraintSet.TOP,0);
            constraintSet.connect(notLoggedInErrorTextView.getId(),ConstraintSet.LEFT, constraintLayout.getId(),ConstraintSet.LEFT,0);
            constraintSet.connect(notLoggedInErrorTextView.getId(),ConstraintSet.RIGHT, constraintLayout.getId(),ConstraintSet.RIGHT,0);
            constraintSet.connect(notLoggedInErrorTextView.getId(),ConstraintSet.BOTTOM, constraintLayout.getId(),ConstraintSet.BOTTOM,0);

            constraintSet.applyTo(constraintLayout);
            parentConstraintLayout.removeView(constraintLayout);
            parentConstraintLayout.addView(constraintLayout);

        }
    }


    public void onBackButtonOnClick(View view) {
        this.finish();
    }
}
