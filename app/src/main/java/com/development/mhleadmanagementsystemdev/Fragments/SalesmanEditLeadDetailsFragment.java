package com.development.mhleadmanagementsystemdev.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.development.mhleadmanagementsystemdev.R;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class SalesmanEditLeadDetailsFragment extends AppCompatDialogFragment {
    private String strStatus;
    private OnSalesmanSubmitClickListener listener;
    private ArrayAdapter<CharSequence> statusAdapter;
    private Spinner statusSpinner;

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

        statusSpinner = v.findViewById(R.id.status);

        statusAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.status, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                strStatus = parent.getItemAtPosition(position).toString();
                Log.i("STRSTATUS", strStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builder.setView(v)
                .setTitle("Edit details")
                .setPositiveButton("Make changes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!strStatus.equals("None"))
                            listener.onSubmitClicked(strStatus);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setCancelable(false);

        return builder.create();
    }

    public interface OnSalesmanSubmitClickListener {
        void onSubmitClicked(String dialogStatus);
    }
}