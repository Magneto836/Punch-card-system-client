package com.example.myapplication1.ui.activity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.myapplication1.R;
import com.example.myapplication1.ui.adapter.RecordAdapter;

import java.util.ArrayList;
import java.util.List;

import api.ApiService;
import api.RecordApiResponse;
import api.RecordPojo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SelfRecordActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences ;
    private RecyclerView recyclerView;
    private RecordAdapter recordAdapter;

    private static final String TAG = "SelfRecord";
    private List<RecordPojo> recordList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);  // 确保这个布局文件存在


        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);

        if (userId == -1) {
            Log.e(TAG, "User ID is not available");
            return;  // 处理错误
        }

        fetchUserRecords(userId);  // 获取打卡记录

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


    }


    private void fetchUserRecords(int userId) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")  // 替换为你的后端地址
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        Call<RecordApiResponse> call = apiService.getUserRecords(userId);

        //progressDialogHelper.showProgressDialog("加载中...");

        call.enqueue(new Callback<RecordApiResponse>() {
            @Override
            public void onResponse(Call<RecordApiResponse> call, Response<RecordApiResponse> response) {
                // progressDialogHelper.dismissProgressDialog();
                Log.d(TAG,"马上发请求");
                if (response.isSuccessful()) {
                    RecordApiResponse recordApiResponse = response.body();
                    if (recordApiResponse != null && recordApiResponse.getCode() == 200) {
                        recordList.clear();
                        recordList.addAll(recordApiResponse.getData().getRecords());
                        recordAdapter = new RecordAdapter(recordList);  // 创建适配器
                        recyclerView.setAdapter(recordAdapter);  // 绑定适配器
                        Log.d(TAG, "成功");
                        Log.d(TAG, recordList.toString());
                        //recordAdapter.notifyDataSetChanged();  // 更新 RecyclerView

                    } else {
                        Log.d(TAG, "获取记录失败: " + recordApiResponse.getMessage());
                    }
                }
                else {
                    Log.d(TAG, "失败获取记录: " + response);
                }
            }

            @Override
            public void onFailure(Call<RecordApiResponse> call, Throwable t) {
                //progressDialogHelper.dismissProgressDialog();
                Log.e(TAG, "请求失败：" + t.getMessage());
            }
        });
    }





}
