package com.mudrahome.mhlms.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.mudrahome.mhlms.R;
import com.mudrahome.mhlms.managers.Alarm;
import com.mudrahome.mhlms.managers.UpdateLead;
import com.mudrahome.mhlms.model.LeadDetails;
import com.mudrahome.mhlms.model.UserDetails;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@SuppressLint("ValidFragment")
public class TelecallerEditLeadFragment extends AppCompatDialogFragment {
    private String strAssignedTo;
    private OnSubmitClickListener listener;
    private ArrayAdapter<CharSequence> assignedToAdapter;
    private Spinner assignedToSpinner;
    List<UserDetails> salesPersonList;

    private EditText customerName;
    private EditText loanAmount;
    private EditText contactNumber;
    private EditText telecallerReason;
    private Button datePickerButton;
    private Button timePickerButton;
    private TextView dateTextView;
    private TextView timeTextView;
    private TextView reasontextview;

    private LinearLayout reminderLayout;
    private LinearLayout nameLayout;
    private LinearLayout contactLayout;
    private LinearLayout amountLayout;
    private LinearLayout assignToLayout;
    private LinearLayout reasonLayout;

    private int mYear, mMonth, mDay, mHour, mMinute;
    private int alarmYear, alarmMonth, alarmDay, alarmHour, alarmMinute;


    private LeadDetails leadDetails;
    private Boolean reasonshow = false;
    private String userType;


    public TelecallerEditLeadFragment(LeadDetails leadDetails, List<UserDetails> salesPersonList,
                                      OnSubmitClickListener listener, String userType) {
        this.leadDetails = leadDetails;
        this.listener = listener;
        this.salesPersonList = salesPersonList;
        this.userType = userType;
    }

    public static TelecallerEditLeadFragment newInstance(
            LeadDetails leadDetails, List<UserDetails> salesPersonList,
            OnSubmitClickListener listener, String userType) {

        TelecallerEditLeadFragment f = new TelecallerEditLeadFragment(
                leadDetails, salesPersonList, listener, userType);
        return f;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_telecaller_edit_lead_details_, null);

        customerName = v.findViewById(R.id.customer_name);
        loanAmount = v.findViewById(R.id.loan_amount);
        contactNumber = v.findViewById(R.id.contact_number);
        assignedToSpinner = v.findViewById(R.id.assign_to);
        datePickerButton = v.findViewById(R.id.date_picker);
        timePickerButton = v.findViewById(R.id.time_picker);
        dateTextView = v.findViewById(R.id.date);
        timeTextView = v.findViewById(R.id.time);
        reminderLayout = v.findViewById(R.id.reminder_layout);
        nameLayout = v.findViewById(R.id.customer_name_layout);
        contactLayout = v.findViewById(R.id.contact_number_layout);
        amountLayout = v.findViewById(R.id.loan_amount_layout);
        assignToLayout = v.findViewById(R.id.linearLayout);
        reasonLayout = v.findViewById(R.id.reason_layout);

        telecallerReason = v.findViewById(R.id.telecaller_reason);

        reasontextview = v.findViewById(R.id.reasontextview);


//        if (userType.equals(getString(R.string.teleassigner))) {
//            nameLayout.setVisibility(View.GONE);
//            amountLayout.setVisibility(View.GONE);
//            contactLayout.setVisibility(View.GONE);
//        } else if (userType.equals(getString(R.string.business_associate))) {
//            assignToLayout.setVisibility(View.GONE);
//            reasonLayout.setVisibility(View.GONE);
//            reminderLayout.setVisibility(View.GONE);
//        }
        customerName.setText(leadDetails.getName());
        loanAmount.setText(leadDetails.getLoanAmount());
        contactNumber.setText(leadDetails.getContactNumber());

        String remarks = "";

        telecallerReason.setText(remarks);

        if (salesPersonList == null) {
            assignToLayout.setVisibility(View.GONE);
        }
        if (leadDetails.getSalesmanRemarks() == null) {
            reminderLayout.setVisibility(View.GONE);
            dateTextView.setText("DD/MM/YYYY");
            timeTextView.setText("hh:mm");
        } else if (
                leadDetails.getSalesmanRemarks().equals("Customer Interested but Document Pending") ||
                        leadDetails.getSalesmanRemarks().equals("Customer follow Up")) {
            reminderLayout.setVisibility(View.VISIBLE);
        } else {
            reminderLayout.setVisibility(View.GONE);

            dateTextView.setText("DD/MM/YYYY");
            timeTextView.setText("hh:mm");
        }

        List salesPersonNameList = new ArrayList<>();
        for (UserDetails user : salesPersonList) {
            salesPersonNameList.add(user.getUserName());
        }

        // AssignedTo Spinner
        assignedToAdapter = new ArrayAdapter<CharSequence>(
                getContext(),
                android.R.layout.simple_spinner_item, salesPersonNameList);
        assignedToAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assignedToSpinner.setAdapter(assignedToAdapter);

        assignedToSpinner.setSelection(salesPersonNameList.indexOf(leadDetails.getAssignedTo()));

        assignedToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                strAssignedTo = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        datePickerButton.setOnClickListener(v1 -> {
            // Get Current Date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view, year, monthOfYear, dayOfMonth) -> {

                        alarmDay = dayOfMonth;
                        alarmMonth = monthOfYear;
                        alarmYear = year;

                        dateTextView.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                    }, mYear, mMonth, mDay);

            datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
            datePickerDialog.show();
        });

        timePickerButton.setOnClickListener(v12 -> {

            // Get Current TimeModel
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);

            // Launch TimeModel Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    (view, hourOfDay, minute) -> {

                        alarmHour = hourOfDay;
                        alarmMinute = minute;

                        timeTextView.setText(hourOfDay + ":" + minute);
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        });

        builder.setView(v)
                .setTitle("Edit details")
                .setPositiveButton("Make changes", (dialog, which) -> {
                    String strName = customerName.getText().toString();
                    String strLoanAmount = loanAmount.getText().toString();
                    String strNumber = contactNumber.getText().toString();


                    ArrayList<String> strReason = leadDetails.getTelecallerRemarks();
                    if (!userType.equals(getString(R.string.business_associate)))
                        strReason.add(telecallerReason.getText().toString() + "@@" + System.currentTimeMillis());         // Save remark with time steamp


                    if (!strName.isEmpty() &&
                            !strLoanAmount.isEmpty() &&
                            !strNumber.isEmpty()) {

                        UpdateLead updateLead = new UpdateLead(leadDetails);

                        updateLead.taleCaller(strName, strLoanAmount, strNumber, strReason);

                        Alarm alarm = new Alarm(getContext());

                        if (leadDetails.getSalesmanRemarks() != null) {
                            if (leadDetails.getSalesmanRemarks().equals("Customer Interested but Document Pending") ||
                                    leadDetails.getSalesmanRemarks().equals("Customer follow Up")) {

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
                            } else {
                                alarm.cancelAlarm(leadDetails.getName());
                            }
                        }

                        updateLead.time();

                        for (UserDetails userDetails : salesPersonList) {
                            if (userDetails.getUserName().equals(strAssignedTo)) {
                                updateLead.assignedToDetails(strAssignedTo, userDetails.getUId());
                            }
                        }

                        listener.onSubmitClicked(updateLead.getLeadDetails());
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {

                }).setCancelable(false);

        return builder.create();
    }

    public interface OnSubmitClickListener {
        void onSubmitClicked(LeadDetails leadDetails);
    }
}