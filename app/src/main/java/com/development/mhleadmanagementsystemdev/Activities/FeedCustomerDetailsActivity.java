package com.development.mhleadmanagementsystemdev.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.development.mhleadmanagementsystemdev.Firebase.Firestore;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchUsersListListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnUploadCustomerDetailsListener;
import com.development.mhleadmanagementsystemdev.Managers.TimeManager;
import com.development.mhleadmanagementsystemdev.Models.LeadDetails;
import com.development.mhleadmanagementsystemdev.Models.UserDetails;
import com.development.mhleadmanagementsystemdev.Models.UserList;
import com.development.mhleadmanagementsystemdev.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedCustomerDetailsActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    private EditText name, contactNumber, loanAmount, remarks;
    private Spinner propertyTypeSpinner, loanTypeSpinner, locationSpinner, assignedToSpinner;
    ArrayAdapter<CharSequence> propertyTypeAdapter;
    ArrayAdapter<CharSequence> loanTypeAdapter;
    ArrayAdapter<CharSequence> locationAdapter;
    ArrayAdapter<CharSequence> assignedToAdapter;

    private String strName, strContactNumber, strLoanAmount, strRemarks,
            strEmployment = "None", strEmploymentType = "None",
            strLoanType, strPropertyType = "None", strLocation, strAssignTo = "None", strAssignToUId;

    private String strDate, strTime;
    private ProgressDialog progress;
    private LinearLayout selfEmploymentLayout, propertyTypeLayout;
    private RadioGroup selfEmploymentTypeRadioGroup;

    private LeadDetails leadDetails;
    private Firestore firestore;
    private SharedPreferences sharedPreferences;

    private List<UserDetails> salesPersonList;


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
        remarks = findViewById(R.id.remarks);
        assignedToSpinner = findViewById(R.id.assign_to);
        selfEmploymentLayout = findViewById(R.id.self_employement_layout);
        propertyTypeLayout = findViewById(R.id.property_type_layout);
        selfEmploymentTypeRadioGroup = findViewById(R.id.self_employment_type_radio_group);

        firestore = new Firestore(this);
        sharedPreferences = getSharedPreferences(
                getString(R.string.SH_user_details), AppCompatActivity.MODE_PRIVATE);

        initializeLoanTypeSpinner();
        initializeLocationSpinner();
    }

    private void initializeLoanTypeSpinner() {
        // Loan type Spinner
        loanTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.loan_type, android.R.layout.simple_spinner_item);
        loanTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        loanTypeSpinner.setAdapter(loanTypeAdapter);
        loanTypeSpinner.setOnItemSelectedListener(this);
    }

    private void initializeLocationSpinner() {
        // Location Spinner
        locationAdapter = ArrayAdapter.createFromResource(this,
                R.array.location, android.R.layout.simple_spinner_item);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(locationAdapter);
        locationSpinner.setOnItemSelectedListener(this);
    }

    private void initializePropertyTypeSpinner() {
        // Property type Spinner
        if (strLoanType.equals("Home Loan"))
            propertyTypeAdapter = ArrayAdapter.createFromResource(this,
                    R.array.property, android.R.layout.simple_spinner_item);
        else
            propertyTypeAdapter = ArrayAdapter.createFromResource(this,
                    R.array.property_type, android.R.layout.simple_spinner_item);

        propertyTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        propertyTypeSpinner.setAdapter(propertyTypeAdapter);
        propertyTypeSpinner.setOnItemSelectedListener(this);
    }

    public void onEmployementRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.salaried:
                if (checked) {
                    strEmployment = "Salaried";
                    selfEmploymentLayout.setVisibility(View.GONE);
                    selfEmploymentTypeRadioGroup.clearCheck();
                    strEmploymentType = "None";
                    break;
                }
            case R.id.self_employed:
                if (checked) {
                    strEmployment = "Self Employed";
                    selfEmploymentLayout.setVisibility(View.VISIBLE);
                    break;
                }
        }
    }

    public void onEmployementTypeRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.partnership_firm:
                if (checked) {
                    strEmploymentType = "Partnership Firm";
                    break;
                }
            case R.id.private_limited_company:
                if (checked) {
                    strEmploymentType = "Private Limited Company";
                    break;
                }
            case R.id.proprietorship_firm:
                if (checked) {
                    strEmploymentType = "Proprietorship Firm";
                    break;
                }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.loan_type:
                Log.i("TAGG", "Loan type called" + parent.getItemAtPosition(position).toString());
                strLoanType = parent.getItemAtPosition(position).toString();
                if (strLoanType.equals("Home Loan") || strLoanType.equals("Loan Against Property")) {
                    initializePropertyTypeSpinner();

                    propertyTypeLayout.setVisibility(View.VISIBLE);
                } else {
                    propertyTypeLayout.setVisibility(View.GONE);
                    strPropertyType = "None";
                }
                break;

            case R.id.property_type:
                Log.i("TAGG", "Property type called" + parent.getItemAtPosition(position).toString());
                strPropertyType = parent.getItemAtPosition(position).toString();
                break;

            case R.id.location:
                Log.i("TAGG", "Location called" + parent.getItemAtPosition(position).toString());
                strLocation = parent.getItemAtPosition(position).toString();

                //assignedToSpinner.setEnabled(false);
                //assignedToSpinner.setClickable(false);

                if (isNetworkConnected()) {
                    getSalesmanListByLocation();
                } else {
                    showToastMessage(R.string.no_internet);
                    initializeLocationSpinner();
                }
                break;

            case R.id.assign_to:
                Log.i("TAGG", "Assignto called" + parent.getItemAtPosition(position).toString());
                strAssignTo = parent.getItemAtPosition(position).toString();
                for (UserDetails user : salesPersonList) {
                    if (user.getUserName().equals(strAssignTo))
                        strAssignToUId = user.getuId();
                }
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
            if (checkEmpty()) {
                uploadDetails();
            } else
                showToastMessage(R.string.fill_details_correctly);
        } else
            showToastMessage(R.string.no_internet);
    }

    private void getSalesmanListByLocation() {
        /*progress = new ProgressDialog(this);
        progress.setMessage("Loading..");
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();*/

        firestore.fetchSalesPersons(
                onFetchUsersListListener(), strLocation);
    }

    private boolean checkEmpty() {
        if (strName.isEmpty() || strContactNumber.length() != 10 || strLoanAmount.isEmpty() ||
                strRemarks.isEmpty() || strAssignTo.equals("None") || strEmployment.equals("None")) {
            return false;
        }
        if (strEmployment.equals("Self Employed")) {
            if (strEmploymentType.equals("None"))
                return false;
        }
        if (strAssignTo.equals("None"))
            return false;
        return true;
    }

    private void uploadDetails() {
        androidx.appcompat.app.AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to upload the details?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("Fields filled", "All the fields are filled");

                        HashMap<String, String> time = new HashMap<>();
                        TimeManager timeManager = new TimeManager();
                        time = timeManager.getTime();
                        makeObject(time);

                        progress = new ProgressDialog(FeedCustomerDetailsActivity.this);
                        progress.setMessage("Uploading..");
                        progress.setCancelable(false);
                        progress.setCanceledOnTouchOutside(false);
                        progress.show();

                        firestore.uploadCustomerDetails(onUploadCustomerdetails(), leadDetails);
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("No", null)
                .setCancelable(false)
                .show();

        alertDialog.setCanceledOnTouchOutside(false);
    }

    private void getDetails() {

        strName = name.getText().toString().trim();
        strContactNumber = contactNumber.getText().toString().trim();
        strLoanAmount = loanAmount.getText().toString().trim();
        strRemarks = remarks.getText().toString().trim();
    }

    private void makeObject(Map<String, String> time) {
        String assigner = sharedPreferences.getString(getString(R.string.SH_user_name), "");
        String assignerUId = sharedPreferences.getString(getString(R.string.SH_user_uid), "");

        leadDetails = new LeadDetails(strName, strContactNumber, strLoanAmount, strEmployment,
                strEmploymentType, strLoanType, strPropertyType, strLocation, strRemarks, "None",
                strAssignTo, "Active", assigner, "", "None",
                strAssignToUId, assignerUId, "None", "None", time.get("date"), time.get("time"));
    }

    private OnUploadCustomerDetailsListener onUploadCustomerdetails() {
        return new OnUploadCustomerDetailsListener() {
            @Override
            public void onDataUploaded() {
                Log.i("No of Nodes", "Uploaded");
                progress.dismiss();
                finish();
                /*showToastMessage(R.string.data_uploaded);
                Intent intent = getIntent();
                finish();
                startActivity(intent);*/
            }

            @Override
            public void failedToUpload() {
                progress.dismiss();
                showToastMessage(R.string.failed_to_upload);
            }
        };
    }

    private OnFetchUsersListListener onFetchUsersListListener() {
        return new OnFetchUsersListListener() {
            @Override
            public void onListFetched(UserList userList) {

                salesPersonList = userList.getUserList();

                if (salesPersonList.size() != 0) {
                    List salesPersonNameList = new ArrayList<>();
                    for (UserDetails user : salesPersonList) {
                        salesPersonNameList.add(user.getUserName());
                    }

                    // AssignedTo Spinner
                    assignedToAdapter = new ArrayAdapter<CharSequence>(
                            FeedCustomerDetailsActivity.this,
                            android.R.layout.simple_spinner_item, salesPersonNameList);
                    assignedToAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    assignedToSpinner.setAdapter(assignedToAdapter);
                    assignedToSpinner.setOnItemSelectedListener(FeedCustomerDetailsActivity.this);

                    assignedToSpinner.setEnabled(true);
                    assignedToSpinner.setClickable(true);

                    //progress.dismiss();
                } else {
                    strAssignTo = "None";
                    assignedToSpinner.setEnabled(false);
                    assignedToSpinner.setClickable(false);
                }
            }
        };
    }
}
