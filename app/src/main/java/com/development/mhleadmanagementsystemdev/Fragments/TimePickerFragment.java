package com.development.mhleadmanagementsystemdev.Fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get Current TimeModel
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch TimeModel Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                getActivity(), (TimePickerDialog.OnTimeSetListener) getActivity(), mHour, mMinute, false);
        return timePickerDialog;
    }
}