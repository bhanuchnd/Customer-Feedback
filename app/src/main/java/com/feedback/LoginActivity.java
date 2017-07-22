package com.feedback;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.feedback.db_helper.DbController;
import com.feedback.model.Feedback;
import com.feedback.model.Rating;
import com.feedback.model.Rating_;
import com.feedback.model.Staff;
import com.feedback.model.Staff_;
import com.feedback.model.Success;
import com.feedback.restCall.APIClient;
import com.feedback.restCall.APIInterface;
import com.feedback.utils.SharedPrefUtils;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import customfonts.MyEditText;
import customfonts.MyRegularText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private MyEditText mEmailView;
    private MyEditText mPasswordView;
    private DbController controller;
    protected APIInterface service;
    MyRegularText submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Intent extra = getIntent();
        LoginActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mEmailView = (MyEditText) findViewById(R.id.email);
        service = APIClient.getClient().create(APIInterface.class);
        SharedPreferences oneTimeSync = PreferenceManager.getDefaultSharedPreferences(this);
        controller = new DbController(this);
        if (!oneTimeSync.getBoolean("firsttime", false)) {
            syncData();
            SharedPreferences.Editor editor = oneTimeSync.edit();
            editor.putBoolean("firsttime", true);
            editor.commit();
        }
        Boolean isSyncClicked = extra.getBooleanExtra("submit", false);
        if (isSyncClicked) {
            hideSoftKeyboard();
            sendFeedback();
            controller.dropTables();
            syncData();
        }
        mPasswordView = (MyEditText) findViewById(R.id.password);
        submit = (MyRegularText) findViewById(R.id.email_sign_in_button);
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();
                String email = mEmailView.getText().toString();
                String password = mPasswordView.getText().toString();
                if (!email.isEmpty() || !password.isEmpty()) {
                    if (email.equalsIgnoreCase("admin") && password.equalsIgnoreCase("admin")) {
                        Intent in = new Intent(LoginActivity.this, SyncActivity.class);
                        startActivity(in);
                        finish();
                    } else if (controller.isAuthenticated(email, password)) {
                        Intent in = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(in);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Please Enter Valid Credentials", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Please Enter Username and Password", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */


    private void syncData() {
        Log.d("string", "syncing");
        Call<Rating> getRating = service.getRating("rating");
        getRating.enqueue(new Callback<Rating>() {

            @Override
            public void onResponse(Call<Rating> call, Response<Rating> response) {
                insertIntoRating(response);
                Log.d("string", response.body().toString());
            }

            @Override
            public void onFailure(Call<Rating> call, Throwable t) {

            }
        });
        Call<Staff> getStaff = service.getStaff("staff");
        getStaff.enqueue(new Callback<Staff>() {
            @Override
            public void onResponse(Call<Staff> call, Response<Staff> response) {
                insertIntoStaff(response);
                Log.d("string", "syncing");
            }

            @Override
            public void onFailure(Call<Staff> call, Throwable t) {

            }
        });


    }

    public void sendFeedback() {
        List<Feedback> feedback = controller.getFeedBack();
        if (!feedback.isEmpty()) {
            Call<Success> sendFeedback = service.sendFeedback(feedback);
            sendFeedback.enqueue(new Callback<Success>() {
                @Override
                public void onResponse(Call<Success> call, Response<Success> response) {
                    if (response.isSuccessful()) {
                        controller.dropTable("FEEDBACK");
                        SharedPrefUtils.updateSyncStatus(LoginActivity.this, true);
                        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Succesfully Updated in Server")
                                .setConfirmText("Ok")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {

                                    }
                                })
                                .show();

                    } else
                        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText("Error While Inserting into the Server!")
                                .show();
                }

                @Override
                public void onFailure(Call<Success> call, Throwable t) {
                    Log.d("Failure", t.toString());
                    Log.d("Sucess/Fail", call + "");
                }
            });
        }
    }


    private void insertIntoRating(Response<Rating> response) {
        Rating ratings = response.body();
        List<Rating_> rating_list = ratings.getRating();
        controller.insertRatings(rating_list);
    }

    private void insertIntoStaff(Response<Staff> response) {
        Staff emp = response.body();
        List<Staff_> staff_list = emp.getStaff();
        controller.insertStaff(staff_list);
    }
}

