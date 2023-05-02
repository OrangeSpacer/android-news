package com.example.newsapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Xml;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsActivity extends AppCompatActivity {

    OkHttpClient client = new OkHttpClient();
    private String URL = "https://news.rambler.ru/rss/politics/";
    private TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        mTextView = findViewById(R.id.linear_layout);
        WebView webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);


        new GetNewsTask().execute("https://news.rambler.ru/rss/politics/");
    }

    
    private class GetNewsTask extends AsyncTask<String, Void, ArrayList<String>> {
        @Override
        protected ArrayList<String> doInBackground(String... urls) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url(urls[0])
                    .build();

            try {
                Response response = client.newCall(request).execute();
                InputStream inputStream = response.body().byteStream();
                return parseXML(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            } catch (XmlPullParserException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            if (result != null) {
                for (String s : result) {
                    mTextView.append(s + "\n\n");
                }
            } else {
                mTextView.setText("Ошибка загрузки данных");
            }
        }
    }

    private ArrayList<String> parseXML(InputStream inputStream) throws XmlPullParserException, IOException {
        ArrayList<String> titles = new ArrayList<>();

        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(inputStream, null);

        int eventType = parser.getEventType();
        String title = null;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            String name = parser.getName();

            if (eventType == XmlPullParser.START_TAG) {
                if (name.equalsIgnoreCase("title")) {
                    title = parser.nextText();
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                if (name.equalsIgnoreCase("item")) {
                    titles.add(title);
                    title = null;
                }
            }

            eventType = parser.next();
        }

        return titles;
    }
}