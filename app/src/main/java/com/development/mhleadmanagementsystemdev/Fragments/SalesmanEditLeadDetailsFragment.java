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
import android.widget.EditText;
import android.widget.Spinner;

import com.development.mhleadmanagementsystemdev.R;


@SuppressLint("ValidFragment")
public class SalesmanEditLeadDetailsFragment extends AppCompatDialogFragment {
    private String strRemarks;
    private OnSalesmanSubmitClickListener listener;
    private ArrayAdapter<CharSequence> remarksAdapter;
    private Spinner remarksSpinner;
    private EditText salesmanReason;

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

        remarksAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.remarks, android.R.layout.simple_spinner_item);
        remarksAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        remarksSpinner.setAdapter(remarksAdapter);
        remarksSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                strRemarks = parent.getItemAtPosition(position).toString();
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
                        if (!strRemarks.equals("None") && !salesmanReason.getText().toString().isEmpty())
                            listener.onSubmitClicked(strRemarks, salesmanReason.getText().toString());
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
        void onSubmitClicked(String dialogStatus, String dialogSalesmanReason);
    }
}