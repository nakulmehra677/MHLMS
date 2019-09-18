package com.mudrahome.mhlms.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.mudrahome.mhlms.R;

public class EditPhoneFragment extends AppCompatDialogFragment {

    private String number;
    private OnSubmitClickListener listener;
    private EditText phone;

    public EditPhoneFragment(String number, OnSubmitClickListener listener) {
        this.number = number;
        this.listener = listener;
    }

    public static EditPhoneFragment newInstance(String number, OnSubmitClickListener listener) {

        EditPhoneFragment f = new EditPhoneFragment(number, listener);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_mobile_number, null);

        phone = v.findViewById(R.id.mobile_number);

        builder.setView(v)
                .setTitle("Edit Number")
                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onSubmitClicked("+91" + phone.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setCancelable(false);

        return builder.create();
    }

    public interface OnSubmitClickListener {
        void onSubmitClicked(String number);
    }
}
