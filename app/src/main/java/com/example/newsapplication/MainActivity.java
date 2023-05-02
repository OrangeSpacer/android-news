package com.example.newsapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {
    public interface LoginCallback {
        void onSuccess(JSONObject response) throws JSONException;
        void onFailure(Exception e);
    }

    public static JSONObject fetchToServer(String login, String password, LoginCallback callback) {
        OkHttpClient client = new OkHttpClient();
        String API_URL = "https://android-for-students.ru/coursework/login.php";

        RequestBody formBody = new FormBody.Builder()
                .add("lgn",login)
                .add("pwd", password)
                .add("g", "RIBO-01-21")
                .build();
        Request request = new Request.Builder()
                .url(API_URL)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                callback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String json = response.body().string();

                try {
                    JSONObject jsonObject = new JSONObject(json);
                    callback.onSuccess(jsonObject);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText loginText = findViewById(R.id.loginText);
        EditText passwordText = findViewById(R.id.passwordText);
        Button loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject res = fetchToServer(loginText.getText().toString(), passwordText.getText().toString(), new LoginCallback() {
                    @Override
                    public void onSuccess(JSONObject response) throws JSONException {
                        if(response.getInt("result_code") == -1) {
                            System.out.println(response);
                        } else {
                            Intent actIntent = new Intent(getApplicationContext(), NewsActivity.class);
                            startActivity(actIntent);
                        }
                    }

                    @Override
                    public void onFailure(Exception e) {
                        System.out.println(e);
                    }
                });
            }
        });
    }
}

//    Intent actIntent = new Intent(getApplicationContext(), NewsActivity.class);
//    startActivity(actIntent);