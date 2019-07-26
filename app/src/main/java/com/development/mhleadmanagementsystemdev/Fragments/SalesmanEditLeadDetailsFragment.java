package com.development.mhleadmanagementsystemdev.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
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

import com.development.mhleadmanagementsystemdev.R;

import java.util.Calendar;


@SuppressLint("ValidFragment")
public class SalesmanEditLeadDetailsFragment extends AppCompatDialogFragment {
    private String strRemarks;
    private OnSalesmanSubmitClickListener listener;
    private ArrayAdapter<CharSequence> remarksAdapter;
    private Spinner remarksSpinner;
    private EditText salesmanReason;
    private Button datePickerButton;
    private Button timePickerButton;
    private TextView dateTextView;
    private TextView timeTextView;

    private LinearLayout datePickerLayout, timePickerLayout;

    private int mYear, mMonth, mDay, mHour, mMinute;

    public SalesmanEditLeadDetailsFragment(OnSalesmanSubmitClickListener listener) {
        this.listener = listener;
    }

    public static SalesmanEditLeadDetailsFragment newInstance(OnSalesmanSubmitClickListener listener) {

        SalesmanEditLeadDetailsFragment f = new SalesmanEditLeadDetailsFragment(listener);
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
        datePickerLayout = v.findViewById(R.id.date_picker_layout);
        timePickerLayout = v.findViewById(R.id.time_picker_layout);

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
                    datePickerLayout.setVisibility(View.VISIBLE);
                    timePickerLayout.setVisibility(View.VISIBLE);
                } else {
                    datePickerLayout.setVisibility(View.GONE);
                    timePickerLayout.setVisibility(View.GONE);
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

                                dateTextView.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
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

                                timeTextView.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }
        });

        builder.setView(v)
                .

                        setTitle("Edit details")
                .

                        setPositiveButton("Make changes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!salesmanReason.getText().toString().isEmpty())
                                    listener.onSubmitClicked(strRemarks, salesmanReason.getText().toString());
                            }
                        })
                .

                        setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).

                setCancelable(false);

        return builder.create();
    }

    public interface OnSalesmanSubmitClickListener {
        void onSubmitClicked(String dialogStatus, String dialogSalesmanReason);
    }
}