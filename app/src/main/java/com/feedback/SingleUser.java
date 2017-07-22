package com.feedback;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.feedback.db_helper.DbController;
import com.feedback.model.Feedback;
import com.feedback.model.Rating_;
import com.feedback.model.Staff_;
import com.feedback.restCall.APIClient;
import com.feedback.restCall.APIInterface;
import com.feedback.utils.SharedPrefUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import customfonts.MyEditText;
import customfonts.MyRadioButton;
import customfonts.MyTextView;

public class SingleUser extends AppCompatActivity {

    private RadioGroup personRating, serviceRating;
    MyEditText eTComments;
    MyTextView submit, cancel;
    DbController controller;
    TextView staffName;
    protected APIInterface service;
    Spinner spinnerStaff;
    int serviceScale, overallScale;
    List<String> staffData = new ArrayList<>();
    Feedback feedback = new Feedback();
    String staffId;
    List<Integer> ratingScale = new ArrayList<>();
    String name, phoneNo, comments;
    List<Rating_> ratings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_user);
        service = APIClient.getClient().create(APIInterface.class);
        Intent intent = getIntent();
        int id = (int) intent.getExtras().get("staffId");
        staffName = (TextView) findViewById(R.id.staff_text);
        controller = new DbController(this);
        personRating = (RadioGroup) findViewById(R.id.person_rating);
        serviceRating = (RadioGroup) findViewById(R.id.ov_service_rating);
        spinnerStaff = (Spinner) findViewById(R.id.spinner_staff);
        staffId = Integer.toString(id);
        String name = controller.getName(staffId);
        staffName.setText("How you'd like to give rating to " + name);
        ratings = controller.getRating();
        addRadioButtons(ratings);
        eTComments = (MyEditText) findViewById(R.id.suggestion);
        submit = (MyTextView) findViewById(R.id.submit);
        cancel = (MyTextView) findViewById(R.id.cancel);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickRadioButtons();
                setFeedback();
                hideSoftKeyboard();
                long success = controller.insertFeedBack(feedback);
                if (success != -1) {
                    SharedPrefUtils.updateSyncStatus(SingleUser.this,false);
                    new SweetAlertDialog(SingleUser.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Thanks for your Feedback")
                            .setContentText("Have a Nice day!")
                            .setConfirmText("Ok")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    Intent i = new Intent(SingleUser.this, MainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                                    sDialog.dismissWithAnimation();
                                    startActivity(i);
                                }
                            })
                            .show();

                } else {
                    new SweetAlertDialog(SingleUser.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Error While Inserting!")
                            .show();
                }


            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SingleUser.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });
    }
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
    private void clickRadioButtons() {
        overallScale = serviceRating.getCheckedRadioButtonId();
        serviceScale = personRating.getCheckedRadioButtonId();
    }
    private void setFeedback() {
        Intent extra = getIntent();
        name = extra.getStringExtra("name");
        phoneNo = extra.getStringExtra("phone");
        comments = eTComments.getText().toString();
        feedback.setCustomerName(name);
        feedback.setComments(comments);
        feedback.setPhone(phoneNo);
        feedback.setServiceBy(staffId);
        feedback.setOverallRating(overallScale);
        feedback.setServiceRating(serviceScale);
    }

    public void addRadioButtons(List<Rating_> data) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Lato-Light.ttf");
        for (Rating_ list : data) {
            ratingScale.add(list.getId());
            RadioButton radioButton1 = new RadioButton(this);
            radioButton1.setId(list.getId());
            radioButton1.setText(list.getRatingType());
            radioButton1.setTypeface(tf);
            RadioButton radioButton2 = new RadioButton(this);
            radioButton2.setId(list.getId());
            radioButton2.setText(list.getRatingType());
            radioButton2.setTypeface(tf);
            personRating.addView(radioButton2, layoutParams);
            serviceRating.addView(radioButton1, layoutParams);
        }

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(SingleUser.this,MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}
