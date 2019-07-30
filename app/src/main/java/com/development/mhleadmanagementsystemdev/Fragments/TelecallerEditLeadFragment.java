package com.development.mhleadmanagementsystemdev.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.development.mhleadmanagementsystemdev.Models.LeadDetails;
import com.development.mhleadmanagementsystemdev.R;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class TelecallerEditLeadFragment extends AppCompatDialogFragment {
    private String strAssignedTo;
    private OnSubmitClickListener listener;
    private ArrayAdapter<CharSequence> assignedToAdapter;
    private Spinner assignedToSpinner;
    List salesPersonList = new ArrayList();

    private EditText customerName;
    private EditText loanAmount;
    private EditText contactNumber;
    private EditText telecallerReason;

    private LeadDetails leadDetails;


    public TelecallerEditLeadFragment(LeadDetails leadDetails, List arrayList, OnSubmitClickListener listener) {
        this.leadDetails = leadDetails;
        this.listener = listener;
        this.salesPersonList = arrayList;
    }

    public static TelecallerEditLeadFragment newInstance(
            LeadDetails leadDetails, List arrayList, OnSubmitClickListener listener) {

        TelecallerEditLeadFragment f = new TelecallerEditLeadFragment(leadDetails, arrayList, listener);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_telecaller_edit_lead_details_, null);

        customerName = v.findViewById(R.id.customer_name);
        loanAmount = v.findViewById(R.id.loan_amount);
        contactNumber = v.findViewById(R.id.contact_number);
        assignedToSpinner = v.findViewById(R.id.assign_to);
        telecallerReason = v.findViewById(R.id.telecaller_reason);

        customerName.setText(leadDetails.getName());
        loanAmount.setText(leadDetails.getLoanAmount());
        contactNumber.setText(leadDetails.getContactNumber());
        telecallerReason.setText(leadDetails.getTelecallerRemarks());

        // AssignedTo Spinner
        assignedToAdapter = new ArrayAdapter<CharSequence>(
                getContext(),
                android.R.layout.simple_spinner_item, salesPersonList);
        assignedToAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assignedToSpinner.setAdapter(assignedToAdapter);

        assignedToSpinner.setSelection(salesPersonList.indexOf(leadDetails.getAssignedTo()));

        assignedToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                strAssignedTo = parent.getItemAtPosition(position).toString();
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
                        if (!telecallerReason.getText().toString().isEmpty()) {
                            leadDetails.setName(customerName.getText().toString());
                            leadDetails.setLoanAmount(loanAmount.getText().toString());
                            leadDetails.setContactNumber(contactNumber.getText().toString());
                            leadDetails.setTelecallerRemarks(telecallerReason.getText().toString());

                            listener.onSubmitClicked(strAssignedTo, leadDetails);
                        }
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
        void onSubmitClicked(String dialogAssignedTo, LeadDetails leadDetails);
    }
}