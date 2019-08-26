package com.mudrahome.MHLMS.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.mudrahome.MHLMS.R;

public class MobileFragment extends DialogFragment {
    private OnNumberClickListener listener;

    public static MobileFragment newInstance(OnNumberClickListener onNumberClickListener) {
        MobileFragment f = new MobileFragment(onNumberClickListener);
        return f;
    }

    public MobileFragment(OnNumberClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_mobile_number, null);

        final EditText mobileNumber = v.findViewById(R.id.mobile_number);

        builder.setView(v)
                .setTitle("Edit details")
                .setPositiveButton("Make changes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!mobileNumber.getText().toString().isEmpty()) {
                            listener.onSubmitClicked(mobileNumber.getText().toString());
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

    public interface OnNumberClickListener {
        void onSubmitClicked(String number);
    }
}
