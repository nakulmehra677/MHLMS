package com.development.mhleadmanagementsystemdev.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Spinner;

import com.development.mhleadmanagementsystemdev.Helper.FirebaseDatabaseHelper;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchUsersListListener;
import com.development.mhleadmanagementsystemdev.Models.UserDetails;
import com.development.mhleadmanagementsystemdev.Models.UserList;
import com.development.mhleadmanagementsystemdev.R;

import java.util.ArrayList;
import java.util.List;

public class FilterActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    private Spinner assignerSpinner, statusSpinner, locationSpinner, assigneeSpinner;
    ArrayAdapter<CharSequence> assigneeAdapter;
    ArrayAdapter<CharSequence> statusAdapter;
    ArrayAdapter<CharSequence> locationAdapter;
    ArrayAdapter<CharSequence> assignerAdapter;

    private String strLocation, strAssigner, strAssignee, strStatus;

    private FirebaseDatabaseHelper firebaseDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        assignerSpinner = findViewById(R.id.assigner_filter);
        assigneeSpinner = findViewById(R.id.assignee_filter);
        locationSpinner = findViewById(R.id.location_filter);
        statusSpinner = findViewById(R.id.status_filter);

        firebaseDatabaseHelper = new FirebaseDatabaseHelper();

        //firebaseDatabaseHelper.fetchUsersByLocation(onFetchUsersListListener(), )

        initializeLocationSpinner();
        initializeStatusSpinner();

        getAssignerList();
        getAssigneeList();

        Button button = findViewById(R.id.filter_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("assigner_filter", strAssigner);
                intent.putExtra("assignee_filter", strAssignee);
                intent.putExtra("location_filter", strLocation);
                intent.putExtra("status_filter", strStatus);

                setResult(101, intent);
                finish();
            }
        });

    }

    private void getAssignerList() {
        firebaseDatabaseHelper.fetchTelecallers(onFetchAssignerListListener(), "All");
    }

    private void getAssigneeList() {
        firebaseDatabaseHelper.fetchSalesPersons(onFetchAssigneeListListener(), "All");
    }


    private OnFetchUsersListListener onFetchAssignerListListener() {
        return new OnFetchUsersListListener() {
            @Override
            public void onListFetched(UserList userList) {
                if (userList.getUserList().size() != 0) {

                    List telecallerNameList = new ArrayList<>();
                    for (UserDetails user : userList.getUserList()) {
                        telecallerNameList.add(user.getUserName());
                    }
                    // AssignedTo Spinner
                    assignerAdapter = new ArrayAdapter<CharSequence>(
                            FilterActivity.this,
                            android.R.layout.simple_spinner_item, telecallerNameList);
                    assignerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    assignerSpinner.setAdapter(assignerAdapter);
                    assignerSpinner.setOnItemSelectedListener(FilterActivity.this);

                    //assignerSpinner.setEnabled(true);
                    //assignerSpinner.setClickable(true);

                    //progress.dismiss();
                } else {
                    strAssigner = "None";
                    //assignedToSpinner.setEnabled(false);
                    //assignedToSpinner.setClickable(false);
                }
            }
        };
    }

    private OnFetchUsersListListener onFetchAssigneeListListener() {
        return new OnFetchUsersListListener() {
            @Override
            public void onListFetched(UserList userList) {
                if (userList.getUserList().size() != 0) {

                    List salesmanNameList = new ArrayList<>();
                    for (UserDetails user : userList.getUserList()) {
                        salesmanNameList.add(user.getUserName());
                    }
                    // AssignedTo Spinner
                    assigneeAdapter = new ArrayAdapter<CharSequence>(
                            FilterActivity.this,
                            android.R.layout.simple_spinner_item, salesmanNameList);
                    assigneeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    assigneeSpinner.setAdapter(assigneeAdapter);
                    assigneeSpinner.setOnItemSelectedListener(FilterActivity.this);

                    //assignerSpinner.setEnabled(true);
                    //assignerSpinner.setClickable(true);

                    //progress.dismiss();
                } else {
                    strAssignee = "None";
                    //assignedToSpinner.setEnabled(false);
                    //assignedToSpinner.setClickable(false);
                }
            }
        };
    }

    private void initializeLocationSpinner() {
        // Location Spinner
        locationAdapter = ArrayAdapter.createFromResource(this,
                R.array.location_filter, android.R.layout.simple_spinner_item);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(locationAdapter);
        locationSpinner.setOnItemSelectedListener(this);
    }

    private void initializeStatusSpinner() {
        // Location Spinner
        statusAdapter = ArrayAdapter.createFromResource(this,
                R.array.status_filter, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);
        statusSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {

            case R.id.location_filter:
                Log.i("TAGG", "Location called" + parent.getItemAtPosition(position).toString());
                strLocation = parent.getItemAtPosition(position).toString();
                break;

            case R.id.assigner_filter:
                Log.i("TAGG", "Loan type called" + parent.getItemAtPosition(position).toString());
                strAssigner = parent.getItemAtPosition(position).toString();
                break;

            case R.id.assignee_filter:
                Log.i("TAGG", "Property type called" + parent.getItemAtPosition(position).toString());
                strAssignee = parent.getItemAtPosition(position).toString();
                break;

            case R.id.status_filter:
                Log.i("TAGG", "Assignto called" + parent.getItemAtPosition(position).toString());
                strStatus = parent.getItemAtPosition(position).toString();
                break;

            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}