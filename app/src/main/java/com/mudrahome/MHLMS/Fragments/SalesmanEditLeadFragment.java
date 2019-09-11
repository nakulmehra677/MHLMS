package com.mudrahome.MHLMS.Fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.mudrahome.MHLMS.Interfaces.FirestoreInterfaces;
import com.mudrahome.MHLMS.Managers.Alarm;
import com.mudrahome.MHLMS.Models.LeadDetails;
import com.mudrahome.MHLMS.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


@SuppressLint("ValidFragment")
public class SalesmanEditLeadFragment extends AppCompatDialogFragment {
    private String strRemarks;
    private OnSalesmanSubmitClickListener listener;
    private ArrayAdapter<CharSequence> remarksAdapter;
    private Spinner remarksSpinner;
    private EditText salesmanReason;
    private Button datePickerButton;
    private Button timePickerButton;
    private TextView dateTextView;
    private TextView timeTextView;

    private LinearLayout reminderLayout;
    private LinearLayout bankLayout;
    private LinearLayout bankListLayout;

    private ProgressBar bankProgress;

    private int mYear, mMonth, mDay, mHour, mMinute;
    private int alarmYear, alarmMonth, alarmDay, alarmHour, alarmMinute;

    private LeadDetails leadDetails;

    public SalesmanEditLeadFragment(LeadDetails leadDetails, OnSalesmanSubmitClickListener listener) {
        this.leadDetails = leadDetails;
        this.listener = listener;
    }

    public static SalesmanEditLeadFragment newInstance(LeadDetails leadDetails,
                                                       OnSalesmanSubmitClickListener listener) {

        SalesmanEditLeadFragment f = new SalesmanEditLeadFragment(leadDetails, listener);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_salesman_edit_lead_details, null);

        remarksSpinner = v.findViewById(R.id.salesman_remarks);
        salesmanReason = v.findViewById(R.id.salesman_reason);
        datePickerButton = v.findViewById(R.id.date_picker);
        timePickerButton = v.findViewById(R.id.time_picker);
        dateTextView = v.findViewById(R.id.date);
        timeTextView = v.findViewById(R.id.time);
        reminderLayout = v.findViewById(R.id.reminder_layout);
        bankLayout = v.findViewById(R.id.linearLayout2);
        bankListLayout = v.findViewById(R.id.bank_layout);
        bankProgress = v.findViewById(R.id.bank_progress);

        remarksSpinner.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        remarksAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.remarks, android.R.layout.simple_spinner_item);
        remarksAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        remarksSpinner.setAdapter(remarksAdapter);

        remarksSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                strRemarks = parent.getItemAtPosition(position).toString();

                if (strRemarks.equals("Customer Interested but Document Pending") ||
                        strRemarks.equals("Customer follow Up")) {
                    reminderLayout.setVisibility(View.VISIBLE);
                    bankLayout.setVisibility(View.GONE);

                } else if (strRemarks.equals("Document Picked and File Logged in")) {
                    getBankList();
                    bankLayout.setVisibility(View.VISIBLE);
                    reminderLayout.setVisibility(View.GONE);

                    dateTextView.setText("DD/MM/YYYY");
                    timeTextView.setText("hh:mm");

                } else {
                    reminderLayout.setVisibility(View.GONE);
                    bankLayout.setVisibility(View.GONE);

                    dateTextView.setText("DD/MM/YYYY");
                    timeTextView.setText("hh:mm");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
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
            }
        });

        timePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get Current TimeModel
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch TimeModel Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
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
            }
        });

        builder.setView(v)
                .setTitle("Edit details")
                .setPositiveButton("Make changes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!salesmanReason.getText().toString().isEmpty()) {

                            Alarm alarm = new Alarm(getContext());

                            if (strRemarks.equals("Customer Interested but Document Pending") ||
                                    strRemarks.equals("Customer follow Up")) {

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
                            listener.onSubmitClicked(strRemarks, salesmanReason.getText().toString(), banks);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(false);

        return builder.create();
    }

    private void getBankList() {
        com.mudrahome.MHLMS.Firebase.Firestore firestore = new com.mudrahome.MHLMS.Firebase.Firestore();
        firestore.getBankList(new FirestoreInterfaces.OnFetchBankList() {
            @Override
            public void onSuccess(ArrayList list) {
                if (list.size() != 0) {
                    for (int i = 0; i < list.size(); i++) {
                        CheckBox checkBox = addCheckBox((String) list.get(i));
                        bankListLayout.addView(checkBox);
                    }
                }
                bankProgress.setVisibility(View.GONE);
            }

            @Override
            public void onFail() {

            }
        });
    }

    private List<String> banks = new ArrayList<>();

    private CheckBox addCheckBox(String bank) {
        CheckBox checkBox = new CheckBox(getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkBox.setButtonTintList(getContext().getColorStateList(R.color.colorPrimaryDark));
        }

        checkBox.setPadding(24, 24, 24, 24);

        checkBox.setText(bank);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox) view).isChecked()) {
                    banks.add((String) ((CheckBox) view).getText());
                } else {
                    banks.remove((String) ((CheckBox) view).getText());
                }
            }
        });
        return checkBox;
    }

    public interface OnSalesmanSubmitClickListener {
        void onSubmitClicked(String dialogStatus, String dialogSalesmanReason, List<String> banks);
    }
}