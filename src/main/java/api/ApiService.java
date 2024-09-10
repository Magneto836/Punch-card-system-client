package api;  // 根据你创建的包名

import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;

import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {
    @FormUrlEncoded
    @POST("/login")
    Call<UserApiResponse> login(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("/regist")
    Call<UserApiResponse> addUser(@Field("username") String username, @Field("password") String password,@Field("role") String role);

    @FormUrlEncoded
    @POST("/ClockIn")
    Call<ApiResponse> clockIn(
            @Field("user_id") int userId,
            @Field("clock_in_time") String clockInTime,
            @Field("locationX") double locationX,
            @Field("locationY") double locationY,
            @Field("date") String date
    );
    @FormUrlEncoded
    @POST("/ClockOut")
    Call<ApiResponse> clockOut(
            @Field("user_id") int userId,
            @Field("clock_out_time") String clockInTime,
            @Field("date") String date

    );

    @GET("/GetRecord/{userId}")
    Call<RecordApiResponse> getUserRecords(@Path("userId") int userId);






}
