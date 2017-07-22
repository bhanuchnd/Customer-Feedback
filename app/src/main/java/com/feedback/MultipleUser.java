package com.feedback;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.feedback.db_helper.DbController;
import com.feedback.model.Feedback;
import com.feedback.model.Rating_;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import customfonts.MyRadioButton;
import customfonts.MyRegularText;

public class MultipleUser extends AppCompatActivity {

    private RadioGroup serviceRating;
    EditText eTComments;
    MyRegularText submit, cancel;
    int serviceScale = 4, overallScale = 4;
    String name, phoneNo, comments;
    private RadioButton selected;
    ArrayList<String> staffIdList = new ArrayList<>();
    List<Integer> ratingScale = new ArrayList<>();
    String staffIds = "staffId";
    List<Rating_> ratings = new ArrayList<>();
    DbController controller;
    LinearLayout ll_PersonRating;
    RadioGroup[] radioGroup;
    HashMap<String, Integer> personRating = new HashMap<>();
    RadioButton radioButton1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_user);
        Intent extra = getIntent();
        ll_PersonRating = (LinearLayout) findViewById(R.id.ll_person_rating);
        serviceRating = (RadioGroup) findViewById(R.id.ov_service_rating);
        MultipleUser.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        eTComments = (EditText) findViewById(R.id.suggestion);
        submit = (MyRegularText) findViewById(R.id.submit);
        cancel = (MyRegularText) findViewById(R.id.cancel);
        name = extra.getStringExtra("name");
        phoneNo = extra.getStringExtra("phone");
        staffIdList = extra.getStringArrayListExtra(staffIds);
        Log.d(staffIds, staffIdList.get(0));
        controller = new DbController(getApplicationContext());
        ratings = controller.getRating();
        addRadioButtons(ratings);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickRadioButtons();
                hideSoftKeyboard();
                comments = eTComments.getText().toString();
                for (String staff : staffIdList) {
                    Feedback feedback = new Feedback();
                    feedback.setCustomerName(name);
                    feedback.setPhone(phoneNo);
                    feedback.setComments(comments);
                    feedback.setServiceBy(staff);
                    feedback.setServiceRating(personRating.get(staff));
                    feedback.setOverallRating(overallScale);
                    controller.insertFeedBack(feedback);
                }
                if (controller.isInsertedIntoFeedback()) {
                    new SweetAlertDialog(MultipleUser.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Thanks for your Feedback")
                            .setContentText("Have a Nice day!")
                            .setConfirmText("Ok")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    Intent i = new Intent(MultipleUser.this, MainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(i);
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .show();

                } else {
                    new SweetAlertDialog(MultipleUser.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Error While Inserting!")
                            .show();
                }


            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eTComments.setText("");
//                serviceRating.check();
            }
        });
    }

    private void clickRadioButtons() {
        for (int i = 0; i < staffIdList.size(); i++) {
            personRating.put(staffIdList.get(i), radioGroup[i].getCheckedRadioButtonId());
        }
        serviceScale = serviceRating.getCheckedRadioButtonId();
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
    public void addRadioButtons(List<Rating_> data) {
        int size = staffIdList.size();
        String name = "";
        Typeface tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Lato-Light.ttf");
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        radioGroup = new RadioGroup[size];
        MyRegularText[] textViews = new MyRegularText[size];
        for (int i = 0; i < radioGroup.length; i++) {
            radioGroup[i] = new RadioGroup(this);
            radioGroup[i].setId(i);
            textViews[i] = new MyRegularText(this);
            name = controller.getName(staffIdList.get(i));
            textViews[i].setText("How you'd like to give rating to " + name);
            for (Rating_ list : data) {
                ratingScale.add(list.getScale());
                RadioButton radioButton1 = new RadioButton(this);
                radioButton1.setId(list.getId());
                radioButton1.setText(list.getRatingType());
                radioButton1.setTypeface(tf);
                radioGroup[i].addView(radioButton1, layoutParams);
            }
            ll_PersonRating.addView(textViews[i], layoutParams);
            ll_PersonRating.addView(radioGroup[i]);

        }
        for (Rating_ list : data) {
            ratingScale.add(list.getScale());
            radioButton1 = new RadioButton(this);
            radioButton1.setId(list.getId());
            radioButton1.setText(list.getRatingType());
            radioButton1.setTypeface(tf);
            serviceRating.addView(radioButton1, layoutParams);
        }
    }

}
