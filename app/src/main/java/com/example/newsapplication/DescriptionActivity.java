package com.example.newsapplication;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DescriptionActivity extends AppCompatActivity {
    private TextView textView;
    private TextView titleText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        String description = getIntent().getStringExtra("description");
        String title = getIntent().getStringExtra("title");

        textView = findViewById(R.id.descriptionTextView);
        titleText = findViewById(R.id.titleTextView);
        titleText.setText(title);
        textView.setText(description);
    }
}