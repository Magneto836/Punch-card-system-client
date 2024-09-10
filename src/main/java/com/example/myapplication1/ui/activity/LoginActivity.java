package com.example.myapplication1.ui.activity;


import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.content.SharedPreferences;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication1.R;
import com.example.myapplication1.databinding.ActivityLoginBinding;
import com.example.myapplication1.utils.ProgressDialogHelper;

import java.util.HashMap;
import java.util.Map;

import api.ApiResponse;
import api.ApiService;
import api.UserApiResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoginActivity extends AppCompatActivity {

    private ProgressDialogHelper progressDialogHelper;
    private static final String TAG = "LoginActivity";
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialogHelper = new ProgressDialogHelper(this);

        // 获取输入框
        EditText usernameLayoutEditText = binding.tlUsername.getEditText();
        EditText passwordLayoutEditText = binding.tlPassword.getEditText();

        // 添加 TextWatcher 监听用户名输入变化
        if (usernameLayoutEditText != null) {
            usernameLayoutEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // 这里可以在输入框内容改变前进行操作（可选）
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // 当用户输入内容时，清除错误提示
                    if (s.length() > 0) {
                        binding.tlUsername.setError(null);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // 这里可以在输入框内容改变后进行操作（可选）
                }

            });
        }
        if (passwordLayoutEditText != null) {
            passwordLayoutEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // 这里可以在输入框内容改变前进行操作（可选）
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // 当用户输入内容时，清除错误提示
                    if (s.length() > 0) {
                        binding.tlPassword.setError(null);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // 这里可以在输入框内容改变后进行操作（可选）
                }
            });
        }
    }

    private void handleUserInput(String actionType) {
        Log.d(TAG, "点击了按钮，操作类型: " + actionType);
        String username = "", password = "";

        // 获取输入框内容
        EditText usernameLayoutEditText = binding.tlUsername.getEditText();
        EditText passwordLayoutEditText = binding.tlPassword.getEditText();

        if (usernameLayoutEditText != null) {
            if (usernameLayoutEditText.getText().length() == 0) {
                Log.d(TAG, "请输入用户名");
                binding.tlUsername.setError("请输入用户名");
                return;
            }
            username = usernameLayoutEditText.getText().toString();
            Log.d(TAG, "用户名为：" + username);
        }

        if (passwordLayoutEditText != null) {
            if (passwordLayoutEditText.getText().length() == 0) {
                binding.tlPassword.setError("请输入密码");
                Log.d(TAG, "请输入密码");
                return;
            }
            password = passwordLayoutEditText.getText().toString();
        }

        if (username.isEmpty() || password.isEmpty()) {
            Log.d(TAG, "空");
            return;
        }

        // 根据操作类型调用不同的方法
        if (actionType.equals("login")) {
            attemptLogin(username, password);
        } else if (actionType.equals("regist")) {
            attemptRegist(username, password);
        }
    }

    public void handleLoginBtn(View view) {
        handleUserInput("login");
    }

    public void handleRegistBtn(View view) {
        handleUserInput("regist");
    }

    private void attemptRequest(String username, String password, String message, Call<UserApiResponse> call) {
        progressDialogHelper.showProgressDialog(message);  // 显示进度框

        call.enqueue(new Callback<UserApiResponse>() {
            @Override
            public void onResponse(Call<UserApiResponse> call, Response<UserApiResponse> response) {


                if (response.isSuccessful()) {
                    UserApiResponse apiResponse = response.body();
                    if (apiResponse != null && apiResponse.getCode() == 200) {
                        Log.d(TAG, message + "成功");
                        int userId = apiResponse.getData().getUserId();
                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", username);
                        editor.putInt("user_id", userId);
                        Log.d(TAG, String.valueOf(userId));
                        editor.apply();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);

                    } else {
                        String errorMessage = (apiResponse != null) ? apiResponse.getMessage() : "未知错误";
                        progressDialogHelper.updateProgressDialogMessage("失败: " + errorMessage);  // 更新对话框消息
                        progressDialogHelper.showProgressDialogButton();  // 显示按钮，允许用户手动关闭
                        Log.d(TAG, message + "失败: " + (apiResponse != null ? apiResponse.getMessage() : "未知错误"));
                    }
                } else {
                    Log.d(TAG, message + "失败，服务器错误");
                }
                progressDialogHelper.showProgressDialogButton();  // 显示按钮，允许用户手动关闭
            }

            @Override
            public void onFailure(Call<UserApiResponse> call, Throwable t) {
                progressDialogHelper.updateProgressDialogMessage("失败：" + t.getMessage());
                Log.d(TAG, "消息为" + t.getMessage());
                progressDialogHelper.showProgressDialogButton();  // 显示按钮，允许用户手动关闭
            }
        });
    }

    private void attemptRegist(String username, String password) {
        // 构建请求体
        Map<String, String> params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("role", "employee");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // 调用通用的请求处理函数
        Call<UserApiResponse> call = apiService.addUser(username, password, "employee");
        attemptRequest(username, password, "正在注册……", call);
    }

    private void attemptLogin(String username, String password) {
        // 构建请求体
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // 调用通用的请求处理函数
        Call<UserApiResponse> call = apiService.login(username, password);
        attemptRequest(username, password, "正在登录……", call);
    }


}
