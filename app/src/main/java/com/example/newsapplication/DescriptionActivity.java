package com.example.newsapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DescriptionActivity extends AppCompatActivity {
    private TextView textView;
    private TextView titleText;

    private Button shareBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        String description = getIntent().getStringExtra("description");
        String title = getIntent().getStringExtra("title");

        textView = findViewById(R.id.descriptionTextView);
        titleText = findViewById(R.id.titleTextView);
        shareBtn = findViewById(R.id.shareBtn);
        titleText.setText(title);
        textView.setText(description);

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textToShare = textView.getText().toString();
                Log.i("ShareBtn", "Делимся данными");
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
                startActivity(Intent.createChooser(shareIntent, "share"));
            }
        });
    }
}