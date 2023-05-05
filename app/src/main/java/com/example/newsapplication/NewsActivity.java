package com.example.newsapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
        tabTitles.add("politics");
        tabTitles.add("technology");
        tabTitles.add("community");
        tabTitles.add("incidents");

        for (int i = 0; i < tabTitles.size(); i++) {
            tabLayout.addTab(tabLayout.newTab().setText(tabTitles.get(i)));
        }

            Adapter adapter = new Adapter(getSupportFragmentManager(), tabTitles);
            viewPager.setAdapter(adapter);
            tabLayout.setupWithViewPager(viewPager);
    }

    public static class TabFragment extends Fragment {
        private ListView listView;
        private List<String> titles = new ArrayList<>();
        private List<String> descriptions = new ArrayList<>();

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                String description = "test";

                // Создаем новый Intent для запуска активности с описанием новости
//                Intent intent = new Intent(getActivity(), DescriptionActivity.class);
//                intent.putExtra("description", description);
//                startActivity(intent);
            });
        }

        public static TabFragment newInstance(int position) {
            TabFragment fragment = new TabFragment();
            Bundle args = new Bundle();
            args.putInt("position", position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_tab, container, false);
            listView = view.findViewById(R.id.listView);
            return view;
        }

        @Override
        public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            int position = getArguments().getInt("position");
            String reqQuery = "";
            switch (position){
                case 0:
                    reqQuery = "politics";
                    break;
                case 1:
                    reqQuery = "technology";
                    break;
                case 2:
                    reqQuery = "community";
                    break;
                case 3:
                    reqQuery = "incidents";
                    break;
            }
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://news.rambler.ru/rss/" +  reqQuery + "/")
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }
                    String xml = response.body().string();
                    try{
                        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                        factory.setNamespaceAware(true);
                        XmlPullParser xpp = factory.newPullParser();
                        xpp.setInput(new StringReader(xml));

                        int eventType = xpp.getEventType();
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            if (eventType == XmlPullParser.START_TAG && xpp.getName().equals("title")) {
                                eventType = xpp.next();
                                String title = xpp.getText();
                                titles.add(title);
                            }
                            eventType = xpp.next();
                        }
                        getActivity().runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, titles);
                            listView.setAdapter(adapter);
                        });

                    } catch (XmlPullParserException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }
}