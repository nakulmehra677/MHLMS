package com.mudrahome.mhlms.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.mudrahome.mhlms.R;

public class EditPhoneFragment extends AppCompatDialogFragment {

    private String number;
    private OnSubmitClickListener listener;
    private EditText phone;
    private Button updateContact;
    private TextView cancelTextView;

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
        updateContact = v.findViewById(R.id.updateContact);
        cancelTextView = v.findViewById(R.id.cancelTextView);

        builder.setView(v);


        updateContact.setOnClickListener(view -> {

            if(phone.getText().toString().length() != 10){
                Toast.makeText(getContext(), "Invalid Number", Toast.LENGTH_SHORT).show();
            }else {
                listener.onSubmitClicked("+91" + phone.getText().toString());
                dismiss();
            }
        });

        cancelTextView.setOnClickListener(view ->
        {
            dismiss();
        });
        return builder.create();
    }

    public interface OnSubmitClickListener {
        void onSubmitClicked(String number);
    }
}
