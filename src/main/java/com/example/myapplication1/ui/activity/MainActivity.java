package com.example.myapplication1.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.myapplication1.R;
import com.example.myapplication1.utils.ProgressDialogHelper;


import java.util.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

public class MainActivity extends AppCompatActivity implements AMapLocationListener {

    private static final String TAG = "DataClock";

    private ProgressDialogHelper progressDialogHelper;

    //NOTE : 加入签到时间设置，签到成功后要弹出提示框，提示签到时间地点
    private static final int MY_PERMISSIONS_REQUEST_CALL_LOCATION = 1;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private SharedPreferences sharedPreferences;
    private Button signInButton ;
    private Button signOutButton;
    private Button recordButton ;

    private static final String PREFS_NAME = "SignPrefs";
    private static final String KEY_SIGNED_IN = "signedIn";
    private static final String KEY_SIGNED_OUT = "signedOut" ;
    private static final String KEY_LAST_LATITUDE = "lastLatitude";
    private static final String KEY_LAST_LONGITUDE = "lastLongitude";
    private static final String KEY_SIGNIN_TIME = "signedInTime";
    private static final String KEY_SIGNOUT_TIME= "signedOutTime";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInButton = findViewById(R.id.buttonOn);
        signOutButton = findViewById(R.id.buttonOut);
        recordButton = findViewById(R.id.selfRecord);
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);

        progressDialogHelper = new ProgressDialogHelper(this);

        signInButton.setOnClickListener(view -> {
            checkIfAlreadySignedIn();
            Log.d("MainActivity", "成功点击了签到按钮");
            requestLocationPermission();


        });
        signOutButton.setOnClickListener(view -> {
            checkIfAlreadySignedOut();
            Log.d("MainActivity", "成功点击了签到按钮");

        });
        recordButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SelfRecordActivity.class);
            startActivity(intent);
        });


        AMapLocationClient.updatePrivacyShow(this, true, true);
        AMapLocationClient.updatePrivacyAgree(this, true);
    }
    private void checkIfAlreadySignedIn() {

        int userId = sharedPreferences.getInt("user_id", -1);  // 获取当前登录用户的 user_id
        if (userId == -1) {
            Log.e("MainActivity", "用户ID无效");
            return;
        }

        String signedInKey = "KEY_SIGNED_IN_" + userId;
        String lastLatitudeKey = "KEY_LAST_LATITUDE_" + userId;
        String lastLongitudeKey = "KEY_LAST_LONGITUDE_" + userId;
        String signedInTimeKey = "KEY_SIGNIN_TIME_" + userId;

        boolean signedIn = sharedPreferences.getBoolean(signedInKey, false);
        Log.d("DataClock", String.valueOf(signedIn));
        if (signedIn) {
            double lastLat = Double.longBitsToDouble(sharedPreferences.getLong(lastLatitudeKey, 0));
            double lastLon = Double.longBitsToDouble(sharedPreferences.getLong(lastLongitudeKey, 0));
            String timeStr = sharedPreferences.getString(signedInTimeKey, "00:00:00");
            showAlreadySignedInDialog(lastLat, lastLon, timeStr);
        }
    }
    private void checkIfAlreadySignedOut() {
        int userId = sharedPreferences.getInt("user_id", -1);
        boolean signedOut = sharedPreferences.getBoolean("KEY_SIGNED_OUT_"+userId, false);
        if(signedOut){
            String timeStrOut = sharedPreferences.getString("KEY_SIGNOUT_TIME_"+userId,"24:00:00");
            showAlreadySignedOutDialog(timeStrOut);
        }
        else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateOut = new Date();
            String formattedDateOut = formatter.format(dateOut);
            String[] dateTimeParts = formattedDateOut.split(" ");
            String datePartOut = dateTimeParts[0];  // 日期部分，例如 "2024-09-09"
            String timePartOut = dateTimeParts[1];  // 时间部分，例如 "05:49:30"
            sharedPreferences.edit().putString("KEY_SIGNOUT_TIME_"+userId,timePartOut).apply();
            clockOut(timePartOut,datePartOut);
        }
    }
    private void showAlreadySignedInDialog(double lastLat, double lastLon,String timeStr) {
        String message = "上次签到的位置是：\n纬度: " + lastLat + "\n经度: " + lastLon + "\n时间是"+timeStr+"\n是否重新签到？";
        int userId = sharedPreferences.getInt("user_id", -1);  // 获取当前登录用户的 user_id
        progressDialogHelper.showConfirmationDialog(
                "您已经签到过了",
                message,
                "重新签到",
                "取消",
                (dialog, which) -> {
                    // 把 KEY_SIGNED_IN 设置为 false ，下次签到不会触发 Dialog
                    sharedPreferences.edit().putBoolean("KEY_SIGNED_IN_" + userId, false).apply();
                },
                (dialog, which) -> dialog.dismiss()
        );

    }
    private void showAlreadySignedOutDialog(String timeStrOut){
        String message =  "时间是"+timeStrOut+"\n是否重新签退？";
        int userId = sharedPreferences.getInt("user_id", -1);  // 获
        progressDialogHelper.showConfirmationDialog(
                "您已经签退过了",
                message,
                "重新签退",
                "取消",
                (dialog, which) -> {
                    // 把 KEY_SIGNED_OUT 设置为 false ，下次签到不会触发 Dialog
                    sharedPreferences.edit().putBoolean("KEY_SIGNED_OUT_"+userId, false).apply();

                },
                (dialog, which) -> dialog.dismiss()
        );

    }
    private void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_CALL_LOCATION);
            } else {
                showLocation();
            }
        } else {
            showLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showLocation();
            } else {
                Toast.makeText(this, "权限已拒绝, 不能定位", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showLocation() {
        try {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            mlocationClient.setLocationListener(this);
            //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setInterval(10000);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            //启动定位
            mlocationClient.startLocation();
        } catch (Exception e) {

        }
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                double latitude = amapLocation.getLatitude();
                double longitude = amapLocation.getLongitude();

                int userId = sharedPreferences.getInt("user_id", -1);  // 获取当前登录用户的 user_id
                // 只有正确签到了才会触发 Text
                boolean signedIn = sharedPreferences.getBoolean("KEY_SIGNED_IN_" + userId, false);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                String formattedDate = formatter.format(date);
                String[] dateTimeParts = formattedDate.split(" ");
                String datePart = dateTimeParts[0];  // 日期部分，例如 "2024-09-09"
                String timePart = dateTimeParts[1];  // 时间部分，例如 "05:49:30"
                sharedPreferences.edit().putString(KEY_SIGNIN_TIME,timePart).apply();
                if (!signedIn) {
                    progressDialogHelper.showProgressDialog("正在签到...");
                    clockIn(latitude,longitude,datePart,timePart);

                }

                Log.d("Location", "Lat: " + latitude + ", Lon: " + longitude);

                sharedPreferences.edit()
                        .putBoolean("KEY_SIGNED_IN_" + userId, true)
                        .putLong("KEY_LAST_LATITUDE_"+userId, Double.doubleToRawLongBits(latitude))
                        .putLong("KEY_LAST_LONGITUDE_"+userId, Double.doubleToRawLongBits(longitude))
                        .apply();

                if (mlocationClient != null) {
                    mlocationClient.stopLocation();  // 停止定位
                }
            } else {
                Log.e("AmapError", "location Error, ErrCode:" + amapLocation.getErrorCode() + ", errInfo:" + amapLocation.getErrorInfo());
            }
        }
    }

    private void clockIn( double locationX, double locationY,String datePart,String timePart){



        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = sharedPreferences.getInt("user_id", -1);  // 如果不存在，返回 -1


        Log.d("Date", datePart);
        Log.d("Time", timePart);



        Map<String , String> params = new HashMap<>();
        if (userId == -1) {
            Log.e(TAG, "User ID is not available");
            return;  // 处理错误
        }
        Log.d(TAG, String.valueOf(userId));
        params.put("user_id",String.valueOf(userId));
        params.put("clock_in_time",timePart);
        params.put("locationX",String.valueOf(locationX));
        params.put("locationY",String.valueOf(locationY));
        params.put("date",datePart);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/")  // 替换为你的后端地址
                .addConverterFactory(GsonConverterFactory.create())  // 添加 Gson 转换器
                .build();
        ApiService apiService = retrofit.create(ApiService.class);


        Call<ApiResponse> call = apiService.clockIn(userId,timePart,locationX,locationY ,datePart);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse != null && apiResponse.getCode() == 200) {


                        progressDialogHelper.updateProgressDialogMessage("签到成功！\n时间: " + timePart + "\n纬度: " + locationX + "\n经度: " + locationY);
                        progressDialogHelper.showProgressDialogButton();
                        Log.d(TAG, "数据库签到成功");

                        // 登录成功的逻辑
                    } else {
                        Log.d(TAG, "签到失败: " + (apiResponse != null ? apiResponse.getMessage() : "未知错误"));
                    }
                } else {
                    Log.d(TAG, "签到失败，服务器错误");
                }
            }


        @Override
        public void onFailure(Call<ApiResponse> call, Throwable t) {
            // 网络或其他错误
            Log.d(TAG, "失败签到: " + t.getMessage());
        }
    });
}

private void clockOut(String timePart, String datePart){
    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
    int userId = sharedPreferences.getInt("user_id", -1);  // 如果不存在，返回 -1


    Map<String , String> params = new HashMap<>();
    if (userId == -1) {
        Log.e(TAG, "User ID is not available");
        return;  // 处理错误
    }
    Log.d(TAG, String.valueOf(userId));
    params.put("user_id",String.valueOf(userId));
    params.put("clock_in_time",timePart);
    Log.d(TAG,timePart);
    params.put("date",datePart);
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")  // 替换为你的后端地址
            .addConverterFactory(GsonConverterFactory.create())  // 添加 Gson 转换器
            .build();
    ApiService apiService = retrofit.create(ApiService.class);


    Call<ApiResponse> call = apiService.clockOut(userId,timePart ,datePart);

    call.enqueue(new Callback<ApiResponse>() {
        @Override
        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
            if (response.isSuccessful()) {
                ApiResponse apiResponse = response.body();
                if (apiResponse != null && apiResponse.getCode() == 200) {


                    progressDialogHelper.updateProgressDialogMessage("签退成功！\n时间: " + timePart );
                    progressDialogHelper.showProgressDialogButton();
                    Log.d(TAG, "数据库签退成功");
                    sharedPreferences.edit().putBoolean("KEY_SIGNED_OUT_"+userId,true).apply();
                    Log.d(TAG, String.valueOf(sharedPreferences.getBoolean("KEY_SIGNED_OUT_"+userId, false)));
                    // 登录成功的逻辑
                } else {
                    Log.d(TAG, "签退失败: " + (apiResponse != null ? apiResponse.getMessage() : "未知错误"));
                }
            } else {
                Log.d(TAG, "签退失败，服务器错误");
            }
        }
        @Override
        public void onFailure(Call<ApiResponse> call, Throwable t) {
            // 网络或其他错误
            Log.d(TAG, "失败签退: " + t.getMessage());
        }
    });


    }




































    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mlocationClient != null) {
            mlocationClient.onDestroy();
        }
    }
}
