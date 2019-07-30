package com.development.mhleadmanagementsystemdev.Fragments;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.development.mhleadmanagementsystemdev.Models.LeadDetails;
import com.development.mhleadmanagementsystemdev.R;
import com.development.mhleadmanagementsystemdev.Services.AlertReceiver;

import java.util.Calendar;


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
                } else {
                    reminderLayout.setVisibility(View.GONE);

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

                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
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

                                    startAlarm(c);
                                }
                            } else {
                                cancelAlarm();
                            }
                            listener.onSubmitClicked(strRemarks, salesmanReason.getText().toString());
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

    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlertReceiver.class);
        intent.putExtra("name", leadDetails.getName());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(), leadDetails.getName().hashCode(), intent, 0);

        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(), leadDetails.getName().hashCode(), intent, 0);

        alarmManager.cancel(pendingIntent);
    }

    public interface OnSalesmanSubmitClickListener {
        void onSubmitClicked(String dialogStatus, String dialogSalesmanReason);
    }
}