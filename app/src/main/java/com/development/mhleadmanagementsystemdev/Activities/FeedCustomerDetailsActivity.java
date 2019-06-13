package com.development.mhleadmanagementsystemdev.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.development.mhleadmanagementsystemdev.Helper.FirebaseDatabaseHelper;
import com.development.mhleadmanagementsystemdev.Interfaces.CountNoOfNodesInDatabaseListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnUploadCustomerDetailsListener;
import com.development.mhleadmanagementsystemdev.Models.CustomerDetails;
import com.development.mhleadmanagementsystemdev.R;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FeedCustomerDetailsActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    private EditText name, contactNumber, loanAmount, remarks;
    private Spinner propertyTypeSpinner, loanTypeSpinner, locationSpinner;
    ArrayAdapter<CharSequence> propertyTypeAdapter, loanTypeAdapter, locationAdapter;
    private String strEmployment = null, strName, strContactNumber, strLoanAmount,
            strRemarks, strPropertyType, strLoanType, strLocation;
    private String date, mailId;
    private ProgressDialog progress;

    private CustomerDetails customerDetails;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;

    private FirebaseAuth mAuth;


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
        Button button = findViewById(R.id.list);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FeedCustomerDetailsActivity.this,LeadsListActivity.class));
            }
        });

        mAuth = FirebaseAuth.getInstance();

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

            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                if (isNetworkConnected()) {
                    mAuth.signOut();
                    Toast.makeText(FeedCustomerDetailsActivity.this, "Logged Out.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(FeedCustomerDetailsActivity.this, MainActivity.class));
                    finish();
                } else
                    Toast.makeText(FeedCustomerDetailsActivity.this, "No Internet Connection...", Toast.LENGTH_SHORT).show();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onUploadDetailsButtonClicked(View view) {
        if (isNetworkConnected()) {
            getDetails();

            if (!strName.isEmpty() && !strContactNumber.isEmpty() && !strLoanAmount.isEmpty() &&
                    !strRemarks.isEmpty() && !strPropertyType.equals("None") &&
                    !strLoanType.equals("None") && !strLocation.equals("None") && strEmployment != null) {

                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setMessage("Are you sure you want to upload the details?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i("Fields filled", "All the fields are filled");

                                getDate();
                                getMailId();
                                makeObject();

                                progress = new ProgressDialog(FeedCustomerDetailsActivity.this);
                                progress.setMessage("Uploading..");
                                progress.setCancelable(false);
                                progress.setCanceledOnTouchOutside(false);
                                progress.show();

                                firebaseDatabaseHelper = new FirebaseDatabaseHelper(FeedCustomerDetailsActivity.this);
                                firebaseDatabaseHelper.countNoOfNodesInDatabase(onCountNoOfNodesInDatabase());
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("No", null)
                        .setCancelable(false)
                        .show();

                alertDialog.setCanceledOnTouchOutside(false);

            } else {
                Toast.makeText(FeedCustomerDetailsActivity.this, "Fill all the fields.", Toast.LENGTH_SHORT).show();
                Log.i("Fields filled", "All the fields are not filled");
            }
        } else {
            Toast.makeText(FeedCustomerDetailsActivity.this, "No Internet Connection...", Toast.LENGTH_SHORT).show();
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

    private void getMailId() {
        mailId = mAuth.getCurrentUser().getDisplayName();
    }

    private void makeObject() {
        customerDetails = new CustomerDetails(strName, strContactNumber, strPropertyType,
                strEmployment, strLoanType, strLocation, strLoanAmount, strRemarks, date, mailId);
    }

    private OnUploadCustomerDetailsListener onUploadCustomerdetails() {
        return new OnUploadCustomerDetailsListener() {
            @Override
            public void onDataUploaded() {
                Log.i("No of Nodes", "Uploaded");
                progress.dismiss();
                Toast.makeText(FeedCustomerDetailsActivity.this, "Data Uploaded.", Toast.LENGTH_SHORT).show();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }

            @Override
            public void failedToUpload() {
                progress.dismiss();
                Toast.makeText(FeedCustomerDetailsActivity.this, "Failed to upload data.", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private CountNoOfNodesInDatabaseListener onCountNoOfNodesInDatabase() {
        return new CountNoOfNodesInDatabaseListener() {

            @Override
            public void onFetched(long nodes) {
                Log.i("No of Nodes", "About to upload details");
                firebaseDatabaseHelper.uploadCustomerdetails(onUploadCustomerdetails(), customerDetails, nodes);
            }

            @Override
            public void failedToFetch() {
                progress.dismiss();
                Toast.makeText(FeedCustomerDetailsActivity.this, "Failed to upload data.", Toast.LENGTH_SHORT).show();
            }
        };
    }
}
