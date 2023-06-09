package com.example.newsapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TabFragment extends Fragment {
    private ListView listView;

    List<String> filteredTitles;
    List<String> filteredDescr;
    ArrayAdapter<String> adapter;
    private List<String> titles = new ArrayList<>();
    private List<String> descriptions = new ArrayList<>();

    private EditText textEdit;

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
        textEdit = view.findViewById(R.id.txtsearch);

        textEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    filteredDescr = null;
                    filteredTitles = null;
                    adapter = new ArrayAdapter<>(getActivity(), R.layout.listview_item,R.id.news, titles);
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    searchItem(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               if(filteredTitles != null && filteredDescr != null) {
                    System.out.println(filteredDescr);
                    Intent intent = new Intent(getActivity(), DescriptionActivity.class);
                    intent.putExtra("description", filteredDescr.get(position));
                    intent.putExtra("title", filteredTitles.get(position));
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), DescriptionActivity.class);
                    intent.putExtra("description", descriptions.get(position));
                    intent.putExtra("title", titles.get(position));
                    startActivity(intent);
                }
            }
        });
        return view;
    }


    @Override
    public void onViewCreated(@NotNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        int position = getArguments().getInt("position");
        String reqQuery = "";
        switch (position) {
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
                .url("https://news.rambler.ru/rss/" + reqQuery + "/")
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
                parser(descriptions,titles,xml);
                getActivity().runOnUiThread(() -> {
                    adapter = new ArrayAdapter<>(getActivity(), R.layout.listview_item,R.id.news, titles);
                    listView.setAdapter(adapter);
                });
            }
        });
    }

    public void searchItem(String textToSearch){
        filteredTitles = new ArrayList<>();
        filteredDescr = new ArrayList<>();
        for(int i = 0;i < titles.size();i++){
            if(titles.get(i).toLowerCase().contains(textToSearch.toLowerCase())){
                filteredTitles.add(titles.get(i));
                filteredDescr.add(descriptions.get(i));
           }
        }
        adapter = new ArrayAdapter<>(getActivity(), R.layout.listview_item,R.id.news, filteredTitles);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void parser(List<String> decr, List<String> titles, String file) {
        String xml = file;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        Document doc = null;
        try {
            doc = builder.parse(new InputSource(new StringReader(xml)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }

        NodeList itemList = doc.getElementsByTagName("item");
        titles.clear();
        decr.clear();

        for (int i = 0; i < itemList.getLength(); i++) {
            Element item = (Element) itemList.item(i);
            String title = item.getElementsByTagName("title").item(0).getTextContent();
            String description = item.getElementsByTagName("description").item(0).getTextContent();
            titles.add(title);
            decr.add(description);
        }
    }
}