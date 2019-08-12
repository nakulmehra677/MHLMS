package com.mudrahome.MHLMS.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mudrahome.MHLMS.Firebase.Firestore;
import com.mudrahome.MHLMS.Interfaces.OnFetchUsersListListener;
import com.mudrahome.MHLMS.Models.UserDetails;
import com.mudrahome.MHLMS.Models.UserList;
import com.mudrahome.MHLMS.R;

import java.util.ArrayList;
import java.util.List;

public class FilterActivity extends BaseActivity {

    private String strLocation = "All";
    private String strAssigner = "All";
    private String strAssignee = "All";
    private String strStatus = "All";
    private String strLoanType = "All";
    private String currentUserType;

    private Firestore firestore;
    private SharedPreferences sharedPreferences;

    private RadioGroup radioGroup;

    private Button locationButton;
    private Button assignerButton;
    private Button assigneeButton;
    private Button loanTypeButton;
    private Button statusButton;

    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        listView = findViewById(R.id.list_view);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                getResources().getStringArray(R.array.filter_button));

        listView.setAdapter(adapter);

        listView.setItemsCanFocus(true);
        listView.setSelection(0);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (position == 0) {
                    showLocationList();
                } else if (position == 1) {
                    showAssignerList();
                } else if (position == 2) {
                    showAssigneeList();
                } else if (position == 3) {
                    showLoanTypeList();
                } else {
                    showStatusList();
                }
            }
        });


        sharedPreferences = getSharedPreferences(
                getString(R.string.SH_user_details), AppCompatActivity.MODE_PRIVATE);
        currentUserType = sharedPreferences.getString(getString(R.string.SH_user_type), "Salesman");

        firestore = new Firestore();

        radioGroup = findViewById(R.id.radio_group);

        showLocationList();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                Log.d("Radio_Button", "" + i);
            }
        });
    }

    private void showLocationList() {

        radioGroup.removeAllViews();

        String[] locationArray = getResources().getStringArray(R.array.location_filter);
        for (String locationItem : locationArray) {
            RadioButton rb = new RadioButton(this);
            rb.setPadding(24, 24, 24, 24);
            rb.setText(locationItem);
            radioGroup.addView(rb);
        }
        ((RadioButton) radioGroup.getChildAt(0)).setChecked(true);
    }

    private void showAssignerList() {
        showProgressDialog("Loading...", this);
        firestore.fetchUsersByUserType(
                onFetchAssignerListListener(), "All", getString(R.string.telecaller));
    }

    private void showAssigneeList() {
        showProgressDialog("Loading...", this);
        firestore.fetchUsersByUserType(
                onFetchAssigneeListListener(), "All", getString(R.string.salesman));
    }

    private void showLoanTypeList() {
        radioGroup.removeAllViews();

        String[] locationArray = getResources().getStringArray(R.array.loan_type_filter);
        for (String locationItem : locationArray) {
            RadioButton rb = new RadioButton(this);
            rb.setPadding(24, 24, 24, 24);
            rb.setText(locationItem);
            radioGroup.addView(rb);
        }

        ((RadioButton) radioGroup.getChildAt(0)).setChecked(true);
    }

    private void showStatusList() {
        radioGroup.removeAllViews();

        String[] locationArray = getResources().getStringArray(R.array.status_filter);
        for (String locationItem : locationArray) {
            RadioButton rb = new RadioButton(this);
            rb.setPadding(24, 24, 24, 24);
            rb.setText(locationItem);
            radioGroup.addView(rb);
        }

        ((RadioButton) radioGroup.getChildAt(0)).setChecked(true);
    }


    private OnFetchUsersListListener onFetchAssignerListListener() {
        return new OnFetchUsersListListener() {
            @Override
            public void onListFetched(UserList userList) {
                radioGroup.removeAllViews();
                RadioButton rb = new RadioButton(FilterActivity.this);
                rb.setPadding(24, 24, 24, 24);
                rb.setText("All");
                radioGroup.addView(rb);

                if (userList.getUserList().size() != 0) {
                    for (UserDetails user : userList.getUserList()) {
                        RadioButton rb2 = new RadioButton(FilterActivity.this);
                        rb2.setPadding(24, 24, 24, 24);
                        rb2.setText(user.getUserName());
                        radioGroup.addView(rb2);
                    }
                }
                ((RadioButton) radioGroup.getChildAt(0)).setChecked(true);

                dismissProgressDialog();
            }
        };
    }

    private OnFetchUsersListListener onFetchAssigneeListListener() {
        return new OnFetchUsersListListener() {
            @Override
            public void onListFetched(UserList userList) {
                radioGroup.removeAllViews();
                RadioButton rb = new RadioButton(FilterActivity.this);
                rb.setPadding(24, 24, 24, 24);
                rb.setText("All");
                radioGroup.addView(rb);

                if (userList.getUserList().size() != 0) {
                    for (UserDetails user : userList.getUserList()) {
                        RadioButton rb2 = new RadioButton(FilterActivity.this);
                        rb2.setPadding(24, 24, 24, 24);
                        rb2.setText(user.getUserName());
                        radioGroup.addView(rb2);
                    }
                }
                ((RadioButton) radioGroup.getChildAt(0)).setChecked(true);

                dismissProgressDialog();
            }
        };
    }


//        assignerSpinner = findViewById(R.id.assigner_filter);
//        assigneeSpinner = findViewById(R.id.assignee_filter);
//        locationSpinner = findViewById(R.id.location_filter);
//        loanTypeSpinner = findViewById(R.id.loan_type_filter);
//        statusSpinner = findViewById(R.id.status_filter);
//
//        locationFilterLayout = findViewById(R.id.location_filter_layout);
//        assignerFilterLayout = findViewById(R.id.assigner_filter_layout);
//        assigneeFilterLayout = findViewById(R.id.assignee_filter_layout);
//
//        showProgressDialog("Loading...", this);
//
//        if (currentUserType.equals(getString(R.string.salesman))) {
//            locationFilterLayout.setVisibility(View.GONE);
//            assigneeFilterLayout.setVisibility(View.GONE);
//        } else if (currentUserType.equals(R.string.telecaller)) {
//            assignerFilterLayout.setVisibility(View.GONE);
//        }
//
//
//        initializeLocationSpinner();
//        initializeLoanTypeSpinner();
//        initializeStatusSpinner();
//
//        getAssignerList();
//
//        Button button = findViewById(R.id.filter_button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent();
//                intent.putExtra("assigner_filter", strAssigner);
//                intent.putExtra("assignee_filter", strAssignee);
//                intent.putExtra("location_filter", strLocation);
//                intent.putExtra("loan_type_filter", strLoanType);
//                intent.putExtra("status_filter", strStatus);
//
//                setResult(201, intent);
//                finish();
//            }
//        });
//    }
//
//
//
//
//
//    private void initializeLocationSpinner() {
//        // Location Spinner
//        locationAdapter = ArrayAdapter.createFromResource(this,
//                R.array.location_filter, android.R.layout.simple_spinner_item);
//        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        locationSpinner.setAdapter(locationAdapter);
//        locationSpinner.setOnItemSelectedListener(this);
//    }
//
//    private void initializeLoanTypeSpinner() {
//        // Loan Type Spinner
//        loanTypeAdapter = ArrayAdapter.createFromResource(this,
//                R.array.loan_type_filter, android.R.layout.simple_spinner_item);
//        loanTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        loanTypeSpinner.setAdapter(loanTypeAdapter);
//        loanTypeSpinner.setOnItemSelectedListener(this);
//    }
//
//    private void initializeStatusSpinner() {
//        // Status Spinner
//        statusAdapter = ArrayAdapter.createFromResource(this,
//                R.array.status_filter, android.R.layout.simple_spinner_item);
//        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        statusSpinner.setAdapter(statusAdapter);
//        statusSpinner.setOnItemSelectedListener(this);
//    }
//
//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        switch (parent.getId()) {
//
//            case R.id.location_filter:
//                strLocation = parent.getItemAtPosition(position).toString();
//                break;
//
//            case R.id.assigner_filter:
//                strAssigner = parent.getItemAtPosition(position).toString();
//                break;
//
//            case R.id.assignee_filter:
//                strAssignee = parent.getItemAtPosition(position).toString();
//                break;
//
//            case R.id.loan_type_filter:
//                strLoanType = parent.getItemAtPosition(position).toString();
//                break;
//
//            case R.id.status_filter:
//                strStatus = parent.getItemAtPosition(position).toString();
//                break;
//
//            default:
//                break;
//        }
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//
//    }
}