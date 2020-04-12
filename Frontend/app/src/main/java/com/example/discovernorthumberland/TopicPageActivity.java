package com.example.discovernorthumberland;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class TopicPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_page);

        final String topic = getIntent().getStringExtra("topicId");

        TextView topicTitleTextView = findViewById(R.id.topicTitle);
        topicTitleTextView.setText(topic);
    }

    public void onBackButtonOnClick(View view) {
        this.finish();
    }
}
