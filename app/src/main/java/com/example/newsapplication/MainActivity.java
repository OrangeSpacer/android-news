package com.example.newsapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView errorView = findViewById(R.id.error);
        EditText loginText = findViewById(R.id.loginText);
        EditText passwordText = findViewById(R.id.passwordText);
        Button loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(view -> sendReq(errorView,loginText,passwordText));
    }

    private void sendReq(TextView errorBlock,EditText login,EditText password) {
        Log.i("LoginBtn", "Нажатие на кнопку авторизации");
        if(login.getText().toString().isEmpty() || password.getText().toString() == "") {
            errorBlock.setText("Все поля должны быть заполнены");
            errorBlock.setVisibility(View.VISIBLE);
            Log.e("LoginBtnEmptyValue", "Пустые поля");
        } else {
            errorBlock.setText("");
            errorBlock.setVisibility(View.INVISIBLE);
            OkHttpClient client = new OkHttpClient();

            String API_URL = "https://android-for-students.ru/coursework/login.php";

            RequestBody formBody = new FormBody.Builder()
                    .add("lgn",login.getText().toString())
                    .add("pwd", password.getText().toString())
                    .add("g", "RIBO-01-21")
                    .build();
            Request req = new Request.Builder()
                    .url(API_URL)
                    .post(formBody)
                    .build();

            client.newCall(req).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String res = response.body().string();

                    runOnUiThread(() -> {
                        try{
                            JSONObject jsonObject = new JSONObject(res);
                            int resultCode = jsonObject.getInt("result_code");
                            if(resultCode == 1){
                                errorBlock.setVisibility(View.INVISIBLE);
                                Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                                startActivity(intent);
                                Log.i("LoginBtnSuccess", "Успешный вход");
                            } else if (resultCode == -1){
                                System.out.println(jsonObject);
                                String error = jsonObject.getString("error");
                                errorBlock.setText(error);
                                errorBlock.setVisibility(View.VISIBLE);
                                Log.e("LoginBtnError", "ошибка на стороне сервера");
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            });
        }
    }
}