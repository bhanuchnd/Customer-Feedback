package com.feedback.restCall;

import com.feedback.model.Feeback;
import com.feedback.model.Feedback;
import com.feedback.model.Rating;
import com.feedback.model.Rating_;
import com.feedback.model.Staff;
import com.feedback.model.Staff_;
import com.feedback.model.Success;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by shridhar on 24/5/17.
 */

public interface APIInterface {
    @FormUrlEncoded
    @POST("sync_tables.php")
    Call<Success> login(@Field("username") String username, @Field("password") String password);
    @FormUrlEncoded
    @POST("sync_tables.php")
    Call<Staff> getStaff(@Field("staff") String staff);
    @FormUrlEncoded
    @POST("sync_tables.php")
    Call<Rating> getRating(@Field("rating") String rating);
    @POST("feedback.php")
    Call<Success> sendFeedback(@Body List<Feedback> feedback);
    @POST("feedback.php")
      void sendFeedBack(@Body List<Feedback> feedbackList, Callback<Success> success);
    @FormUrlEncoded
    @POST("sync_tables.php")
    Call<Feeback> getFeedback(@Field("feedback") String feedback);

}
