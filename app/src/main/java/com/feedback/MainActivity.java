package com.feedback;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.feedback.Adapter.ListAdapter;
import com.feedback.db_helper.DbController;
import com.feedback.model.Feeback;
import com.feedback.model.Feedback;
import com.feedback.model.Staff_;
import com.feedback.model.Success;
import com.feedback.restCall.APIClient;
import com.feedback.restCall.APIInterface;
import com.feedback.sync.FeedbackSync;
import com.feedback.utils.SharedPrefUtils;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import customfonts.MyEditText;
import customfonts.MyRegularText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    MyEditText customerName, customerPh;
    RadioGroup userSelect;
    public DbController controller;
    Spinner spinner;
    int id;
    ListView multipleStaff;
    List<String> staffData = new ArrayList<>();
    ListAdapter adapter;
    ArrayList<String> staffId = new ArrayList<>();
    List<Staff_> staffDetails = new ArrayList<>();
    public APIInterface service;
    MyRegularText nextButton;
    String name, phno;
    String nameTag = "name";
    boolean multiple = false;
    String phoneTag = "phone";
    String staffIds = "staffId";
    private FeedbackSync feedbackSync;
    LinearLayout ll__checkBoxes;
    CheckBox[] checkBoxes;
    Typeface tf;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.rate_app:
                Toast.makeText(MainActivity.this,"Under Process",Toast.LENGTH_SHORT);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        return true;
    }
    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent extra = getIntent();
        MainActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        controller = new DbController(this);
        Boolean submit = extra.getBooleanExtra("submit", false);
        spinner = (Spinner) findViewById(R.id.spinner_staff);
        nextButton = (MyRegularText) findViewById(R.id.next_btn);
        multipleStaff = (ListView) findViewById(R.id.multiple_staff);
        service = APIClient.getClient().create(APIInterface.class);
        feedbackSync = new FeedbackSync();
        feedbackSync.setAlarm(MainActivity.this);
        setStaffName();
        customerName = (MyEditText) findViewById(R.id.cust_name);
        customerPh = (MyEditText) findViewById(R.id.cust_phno);
        ll__checkBoxes = (LinearLayout) findViewById(R.id.ll_checkboxes);
        userSelect = (RadioGroup) findViewById(R.id.select_users);
        if (submit) {
            sendFeedback();
        }
        tf = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/Lato-Light.ttf");

        final ArrayAdapter<String> staffNames = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, staffData);
        staffNames.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(staffNames);
        spinner.setOnItemSelectedListener(itemSelected);
        listView();
        ll__checkBoxes.setVisibility(View.GONE);
        userSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                setCustDetails();
                hideSoftKeyboard();
                switch (checkedId) {
                    case R.id.single:
                        if (customerName.getText().toString().isEmpty() || customerPh.getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity.this, "Please Enter name / phone number", Toast.LENGTH_SHORT).show();
                        } else {
                            multiple = false;
                            spinner.setVisibility(View.VISIBLE);
                            nextButton.setVisibility(View.VISIBLE);
//                            multipleStaff.setVisibility(View.GONE);
                            ll__checkBoxes.setVisibility(View.GONE);
//
                        }
                        break;
                    case R.id.multiple:
                        if (customerName.getText().toString().isEmpty() || customerPh.getText().toString().isEmpty()) {
                            userSelect.clearCheck();
                            Toast.makeText(MainActivity.this, "Please Enter name / phone number", Toast.LENGTH_SHORT).show();
                        } else {
                            multiple = true;
                            spinner.setVisibility(View.GONE);
//                            multipleStaff.setVisibility(View.VISIBLE);
                            ll__checkBoxes.setVisibility(View.VISIBLE);

//
                        }
                }
            }
        });
        ll__checkBoxes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i=0;i<staffDetails.size();i++) {
                    if(checkBoxes[i].isChecked()) {
                        staffId.add(staffDetails.get(i).getId());
                    }
                }
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (multiple) {
                    multipleSelected();
                } else
                    singleSelected();
            }
        });


    }
    public void listView() {
        int size = staffDetails.size();
        checkBoxes = new CheckBox[size];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        for(int i=0; i<size;i++) {
            checkBoxes[i] = new CheckBox(this);
            checkBoxes[i].setText(staffDetails.get(i).getName());
            checkBoxes[i].setTypeface(tf);
            ll__checkBoxes.addView(checkBoxes[i],layoutParams);
        }
        for (int i=0;i<staffDetails.size();i++) {
            if(checkBoxes[i].isChecked()) {
                staffId.add(staffDetails.get(i).getId());
            }
        }
    }

    private AdapterView.OnItemSelectedListener itemSelected = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            MainActivity.this.id = position + 1;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    public void multipleSelected() {
        for (int i=0;i<staffDetails.size();i++) {
            if(checkBoxes[i].isChecked()) {
                staffId.add(staffDetails.get(i).getId());
            }
        }
        Intent multiple = new Intent(MainActivity.this, MultipleUser.class);
        multiple.putExtra(nameTag, name);
        multiple.putExtra(phoneTag, phno);
        Toast.makeText(MainActivity.this,"Please select atleast 2 employees",Toast.LENGTH_SHORT);
        if (staffId.size() > 1) {
            multiple.putStringArrayListExtra(staffIds, staffId);
            multiple.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(multiple);
        } else {
            Toast.makeText(MainActivity.this,"Please select atleast 2 employees",Toast.LENGTH_SHORT);
        }
    }

    private void singleSelected() {
        if (userSelect.getCheckedRadioButtonId() == R.id.single) {
            Intent single = new Intent(MainActivity.this, SingleUser.class);
            single.putExtra(nameTag, name);
            single.putExtra(phoneTag, phno);
            single.putExtra(staffIds, id);
            single.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(single);
        }
    }

    private void setCustDetails() {
        name = customerName.getText().toString();
        phno = customerPh.getText().toString();
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
                        SharedPrefUtils.updateSyncStatus(MainActivity.this, true);
                        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Succesfully Updated in Server")
                                .setConfirmText("Ok")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {

                                    }
                                })
                                .show();

                    } else
                        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
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

//    private void getFeedBack() {
//        Call<Feeback> getFeedback = service.getFeedback("feedback");
//        getFeedback.enqueue(new Callback<Feeback>() {
//            @Override
//            public void onResponse(Call<Feeback> call, Response<Feeback> response) {
//                insertIntoFeedback(response);
//            }
//
//            @Override
//            public void onFailure(Call<Feeback> call, Throwable t) {
//
//            }
//        });
//    }

    public void setStaffName() {

        List<Staff_> staff = controller.getStaff();
        staffDetails = staff;
        for (Staff_ data : staff) {
            staffData.add(data.getName());
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void insertIntoFeedback(Response<Feeback> response) {
        Feeback feedback = response.body();
        List<Feedback> feebackList = feedback.getFeedback();
        for (Feedback feedback1 : feebackList) {

            controller.insertFeedBack(feedback1);
        }
    }


}
