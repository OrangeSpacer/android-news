package com.example.newsapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity{
    private TabLayout tabLayout;
    private ViewPager viewPager;
        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        List<String> tabTitles = new ArrayList<>();
        tabTitles.add("политика");
        tabTitles.add("технологии");
        tabTitles.add("общество");
        tabTitles.add("инциденты");

        for (int i = 0; i < tabTitles.size(); i++) {
            tabLayout.addTab(tabLayout.newTab().setText(tabTitles.get(i)));
        }

            Adapter adapter = new Adapter(getSupportFragmentManager(), tabTitles);
            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);
    }

}