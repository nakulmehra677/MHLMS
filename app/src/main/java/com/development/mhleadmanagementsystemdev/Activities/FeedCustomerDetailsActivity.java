package com.development.mhleadmanagementsystemdev.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.development.mhleadmanagementsystemdev.Helper.FirebaseDatabaseHelper;
import com.development.mhleadmanagementsystemdev.Interfaces.CountNoOfNodesInDatabaseListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchSalesPersonListListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnUploadCustomerDetailsListener;
import com.development.mhleadmanagementsystemdev.Models.CustomerDetails;
import com.development.mhleadmanagementsystemdev.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FeedCustomerDetailsActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    private EditText name, contactNumber, loanAmount, remarks;
    private Spinner propertyTypeSpinner, loanTypeSpinner, locationSpinner, remarksSpinner, assignedToSpinner, statusSpinner;
    ArrayAdapter<CharSequence> propertyTypeAdapter;
    ArrayAdapter<CharSequence> loanTypeAdapter;
    ArrayAdapter<CharSequence> locationAdapter;
    ArrayAdapter<CharSequence> remarksAdapter;
    ArrayAdapter<String> assignedToAdapter;
    ArrayAdapter<CharSequence> statusAdapter;
    private String strEmployment = null, strName, strContactNumber, strLoanAmount,
            strRemarks, strPropertyType, strLoanType, strLocation, strAssignTo, strStatus;
    private String date;
    private ProgressDialog progress;

    private CustomerDetails customerDetails;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_customer_details);

        name = findViewById(R.id.name);
        contactNumber = findViewById(R.id.contact_number);
        propertyTypeSpinner = findViewById(R.id.property_type);
        loanTypeSpinner = findViewById(R.id.loan_type);
        locationSpinner = findViewById(R.id.location);
        loanAmount = findViewById(R.id.loan_amount);
        remarksSpinner = findViewById(R.id.remarks);
        assignedToSpinner = findViewById(R.id.assign_to);
        statusSpinner = findViewById(R.id.status);

        firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);

        // Property type Spinner
        propertyTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.property_type, android.R.layout.simple_spinner_item);
        propertyTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        propertyTypeSpinner.setAdapter(propertyTypeAdapter);
        propertyTypeSpinner.setOnItemSelectedListener(this);

        // Loan type Spinner
        loanTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.loan_type, android.R.layout.simple_spinner_item);
        loanTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        loanTypeSpinner.setAdapter(loanTypeAdapter);
        loanTypeSpinner.setOnItemSelectedListener(this);

        // Location Spinner
        locationAdapter = ArrayAdapter.createFromResource(this,
                R.array.location, android.R.layout.simple_spinner_item);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(locationAdapter);
        locationSpinner.setOnItemSelectedListener(this);

        // Remarks Spinner
        remarksAdapter = ArrayAdapter.createFromResource(this,
                R.array.remarks, android.R.layout.simple_spinner_item);
        remarksAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        remarksSpinner.setAdapter(remarksAdapter);
        remarksSpinner.setOnItemSelectedListener(this);

        // Status Spinner
        statusAdapter = ArrayAdapter.createFromResource(this,
                R.array.status, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);
        statusSpinner.setOnItemSelectedListener(this);

        firebaseDatabaseHelper.listAllUsers(onFetchSalesPersonListListener());
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.salaried:
                if (checked) {
                    strEmployment = "Salaried";
                    break;
                }
            case R.id.self_employed:
                if (checked) {
                    strEmployment = "Self Employed";
                    break;
                }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.property_type:
                strPropertyType = parent.getItemAtPosition(position).toString();
                break;

            case R.id.loan_type:
                strLoanType = parent.getItemAtPosition(position).toString();
                break;

            case R.id.location:
                strLocation = parent.getItemAtPosition(position).toString();
                break;

            case R.id.assign_to:
                strAssignTo = parent.getItemAtPosition(position).toString();
                break;

            case R.id.remarks:
                strRemarks = parent.getItemAtPosition(position).toString();
                break;

            case R.id.status:
                strStatus = parent.getItemAtPosition(position).toString();
                break;

            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void onUploadDetailsButtonClicked(View view) {
        if (isNetworkConnected()) {
            getDetails();

            if (!strName.isEmpty() && !strContactNumber.isEmpty() && !strLoanAmount.isEmpty() &&
                    !strRemarks.equals("None") && !strPropertyType.equals("None") &&
                    !strLoanType.equals("None") && !strLocation.equals("None") &&
                    !strAssignTo.equals("None") && !strStatus.equals("None") && strEmployment != null) {

                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setMessage("Are you sure you want to upload the details?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i("Fields filled", "All the fields are filled");

                                getDate();
                                makeObject();

                                progress = new ProgressDialog(FeedCustomerDetailsActivity.this);
                                progress.setMessage("Uploading..");
                                progress.setCancelable(false);
                                progress.setCanceledOnTouchOutside(false);
                                progress.show();

                                firebaseDatabaseHelper.countNoOfNodes(onCountNoOfNodesInDatabase(), "leadsList");
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("No", null)
                        .setCancelable(false)
                        .show();

                alertDialog.setCanceledOnTouchOutside(false);

            } else {
                showToastMessage(R.string.fill_all_fields);
                Log.i("Fields filled", "All the fields are not filled");
            }
        } else {
            showToastMessage(R.string.no_internet);
        }
    }

    private void getDetails() {
        strName = name.getText().toString();
        strContactNumber = contactNumber.getText().toString();
        strLoanAmount = loanAmount.getText().toString();
        strRemarks = remarks.getText().toString();
    }

    private void getDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        date = df.format(c);
    }

    private void makeObject() {
        customerDetails = new CustomerDetails(strName, strContactNumber, strPropertyType,
                strEmployment, strLoanType, strLocation, strLoanAmount, strRemarks, date, strAssignTo, strStatus);
    }

    private OnUploadCustomerDetailsListener onUploadCustomerdetails() {
        return new OnUploadCustomerDetailsListener() {
            @Override
            public void onDataUploaded() {
                Log.i("No of Nodes", "Uploaded");
                progress.dismiss();
                showToastMessage(R.string.data_uploaded);
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }

            @Override
            public void failedToUpload() {
                progress.dismiss();
                showToastMessage(R.string.failed_to_upload);
            }
        };
    }

    private CountNoOfNodesInDatabaseListener onCountNoOfNodesInDatabase() {
        return new CountNoOfNodesInDatabaseListener() {

            @Override
            public void onFetched(long nodes) {
                Log.i("No of Nodes", "About to upload details");
                firebaseDatabaseHelper.uploadCustomerDetails(onUploadCustomerdetails(), customerDetails, nodes);
            }

            @Override
            public void failedToFetch() {
                progress.dismiss();
                showToastMessage(R.string.failed_to_upload);
            }
        };
    }

    private OnFetchSalesPersonListListener onFetchSalesPersonListListener() {
        return new OnFetchSalesPersonListListener() {

            @Override
            public void onListFetched(List arrayList) {
                Log.i("userList", String.valueOf(arrayList));

                // AssignedTo Spinner
                assignedToAdapter = new ArrayAdapter<String>(FeedCustomerDetailsActivity.this,
                        android.R.layout.simple_list_item_1, arrayList);
                assignedToAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                assignedToSpinner.setAdapter(assignedToAdapter);

                assignedToSpinner.setOnItemSelectedListener(FeedCustomerDetailsActivity.this);
            }
        };
    }
}
