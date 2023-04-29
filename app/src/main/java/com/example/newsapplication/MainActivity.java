package com.example.newsapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String API_URL = "https://android-for-studentsru/coursework/login.php";
    private OkHttpClient client = new OkHttpClient();

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
                Log.i("string_app_tag", "login");
                System.out.println("Click");
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("lgn", "Student60029")
                        .addFormDataPart("pwd", "k7embX")
                        .addFormDataPart("g", "RIBO-01-21")
                        .build();

                // Создаем запрос
                Request request = new Request.Builder()
                        .url(API_URL)
                        .post(requestBody)
                        .build();

                // Отправляем запрос асинхронно
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        System.out.println(e);
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        System.out.println("Click");
                        Intent actIntent = new Intent(getApplicationContext(), MainActivity2.class);
                        startActivity(actIntent);
                    }
                });
            }
        });
    }
}