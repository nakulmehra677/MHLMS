package com.mudrahome.MHLMS.Activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mudrahome.MHLMS.Interfaces.FirestoreInterfaces;
import com.mudrahome.MHLMS.Managers.Alarm;
import com.mudrahome.MHLMS.Managers.TimeManager;
import com.mudrahome.MHLMS.Models.LeadDetails;
import com.mudrahome.MHLMS.Models.TimeModel;
import com.mudrahome.MHLMS.Models.UserDetails;
import com.mudrahome.MHLMS.Models.UserList;
import com.mudrahome.MHLMS.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FeedCustomerDetailsActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    private EditText name;
    private EditText contactNumber;
    private EditText loanAmount;
    private EditText remarks;

    private TextView dateTextView;
    private TextView timeTextView;

    private Spinner propertyTypeSpinner;
    private Spinner loanTypeSpinner;
    private Spinner locationSpinner;
    private Spinner assignedToSpinner;

    ArrayAdapter<CharSequence> propertyTypeAdapter;
    ArrayAdapter<CharSequence> loanTypeAdapter;
    ArrayAdapter<CharSequence> locationAdapter;
    ArrayAdapter<CharSequence> assignedToAdapter;

    private int mYear, mMonth, mDay, mHour, mMinute;
    private int alarmYear, alarmMonth, alarmDay, alarmHour, alarmMinute;

    private String strName, strContactNumber, strLoanAmount,
            strEmployment = "None", strEmploymentType = "None",
            strLoanType, strPropertyType = "None", strLocation, strAssignTo = "None",
            strAssignToUId, strAssigneeContact, strAssignerContact;

    private ArrayList<String> strRemarks = new ArrayList<>();
    private String strDate, strTime;
    private ProgressDialog progress;
    private LinearLayout selfEmploymentLayout, propertyTypeLayout;
    private RadioGroup selfEmploymentTypeRadioGroup;

    private LeadDetails leadDetails;
    private com.mudrahome.MHLMS.Firebase.Firestore firestore;
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
        dateTextView = findViewById(R.id.date);
        timeTextView = findViewById(R.id.time);

        firestore = new com.mudrahome.MHLMS.Firebase.Firestore(this);
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
                    if (user.getUserName().equals(strAssignTo)) {
                        strAssignToUId = user.getuId();
                        strAssigneeContact = user.getContactNumber();
                    }
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
        Calendar c = Calendar.getInstance();
        switch (view.getId()) {
            case R.id.upload_details:
                if (isNetworkConnected()) {
                    getDetails();
                    if (checkEmpty()) {
                        uploadDetails();
                    } else
                        showToastMessage(R.string.fill_details_correctly);
                } else
                    showToastMessage(R.string.no_internet);
                break;

            case R.id.date_picker:
                // Get Current Date
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                alarmDay = dayOfMonth;
                                alarmMonth = monthOfYear;
                                alarmYear = year;

                                dateTextView.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);

                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog.show();
                break;

            case R.id.time_picker:
                // Get Current TimeModel
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch TimeModel Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                alarmHour = hourOfDay;
                                alarmMinute = minute;

                                timeTextView.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
                break;

            default:

        }
    }

    private void startSMSIntent() {
        String currentUserName = sharedPreferences.getString(getString(R.string.SH_user_name), "");
        String currentUserNumber = sharedPreferences.getString(getString(R.string.SH_user_number), "");

        String[] currentUserFirstName = currentUserName.split(" ");
        String[] assigneeUserFirstName = strAssignTo.split(" ");

        Uri uri = Uri.parse("smsto:" + strContactNumber);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra("sms_body", "Thanks for connecting mudrahome.com. You were speaking with " +
                currentUserFirstName[0] + " " + currentUserNumber + ". " + assigneeUserFirstName[0] + " " +
                strAssigneeContact + " will connect you for further processing your loan.");
        startActivity(intent);
    }


    private void getSalesmanListByLocation() {
        /*progress = new ProgressDialog(t.khis);
        progress.setMessage("Loading..");
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();*/

        firestore.fetchUsersByUserType(
                onFetchUsersListListener(), strLocation, getString(R.string.salesman));
    }

    private boolean checkEmpty() {
        if (strName.isEmpty() || strContactNumber.length() != 10 || strLoanAmount.isEmpty() ||
                strRemarks.size() == 0 || strAssignTo.equals("None") || strEmployment.equals("None")) {
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

                        TimeManager timeManager = new TimeManager();
                        TimeModel timeModel = timeManager.getTime();
                        makeObject(timeModel);

                        setAlarm();

                        progress = new ProgressDialog(FeedCustomerDetailsActivity.this);
                        progress.setMessage("Uploading..");
                        progress.setCancelable(false);
                        progress.setCanceledOnTouchOutside(false);
                        progress.show();

                        firestore.uploadCustomerDetails(onUploadCustomerDetails(), leadDetails);
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

        if(!remarks.getText().toString().isEmpty()){
            strRemarks.add(remarks.getText().toString().trim());
        }

    }

    private void makeObject(TimeModel timeModel) {
        String assigner = sharedPreferences.getString(getString(R.string.SH_user_name), "");
        String assignerUId = sharedPreferences.getString(getString(R.string.SH_user_uid), "");
        String assignerContact = sharedPreferences.getString(getString(R.string.SH_user_number), "");

        ArrayList<String> salesmanreson = new ArrayList<>();
        salesmanreson.add("None");
        leadDetails = new LeadDetails(strName, strContactNumber, assignerContact,
                strAssigneeContact, strLoanAmount, strEmployment, strEmploymentType, strLoanType,
                strPropertyType, strLocation, strRemarks, timeModel.getDate(), strAssignTo,
                "Active", assigner, "", "None", strAssignToUId,
                assignerUId, salesmanreson, timeModel.getTime(), timeModel.getDate(),
                timeModel.getTime(), timeModel.getTimeStamp());
    }

    private FirestoreInterfaces.OnUploadCustomerDetails onUploadCustomerDetails() {
        return new FirestoreInterfaces.OnUploadCustomerDetails() {
            @Override
            public void onDataUploaded() {
                progress.dismiss();
                finish();
                showToastMessage(R.string.data_uploaded);
                startSMSIntent();
            }

            @Override
            public void failedToUpload() {
                progress.dismiss();
                showToastMessage(R.string.failed_to_upload);
            }
        };
    }

    private FirestoreInterfaces.OnFetchUsersList onFetchUsersListListener() {
        return new FirestoreInterfaces.OnFetchUsersList() {
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

    private void setAlarm() {
        Alarm alarm = new Alarm(this);

        if (!dateTextView.getText().toString().equals("DD/MM/YYYY") &&
                !timeTextView.getText().toString().equals("hh:mm")) {

            Calendar c = Calendar.getInstance();

            c.set(Calendar.DAY_OF_MONTH, alarmDay);
            c.set(Calendar.MONTH, alarmMonth);
            c.set(Calendar.YEAR, alarmYear);
            c.set(Calendar.HOUR_OF_DAY, alarmHour);
            c.set(Calendar.MINUTE, alarmMinute);
            c.set(Calendar.SECOND, 0);

            alarm.startAlarm(c, leadDetails.getName());
        }
    }
}
