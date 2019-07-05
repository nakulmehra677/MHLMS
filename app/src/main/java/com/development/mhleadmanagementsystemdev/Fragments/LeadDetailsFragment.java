package com.development.mhleadmanagementsystemdev.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.development.mhleadmanagementsystemdev.Models.LeadDetails;
import com.development.mhleadmanagementsystemdev.R;

@SuppressLint("ValidFragment")
public class LeadDetailsFragment extends BottomSheetDialogFragment {

    private BottomSheetBehavior mBehavior;
    private TextView name, loan, number, employment, employmentType, loanType, propertyType, location,
            assignedTo, assigner, callerRemarks, salesmanRemarks, status, assignedOn, assignedAt;

    private FloatingActionButton button;

    private LeadDetails leadDetails;

    public LeadDetailsFragment(LeadDetails leadDetails) {
        this.leadDetails = leadDetails;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.fragment_lead_details, null);

        name = view.findViewById(R.id.customer_name);
        loan = view.findViewById(R.id.loan_amount);
        number = view.findViewById(R.id.contact_number);
        employment = view.findViewById(R.id.employment);
        employmentType = view.findViewById(R.id.employment_type);
        loanType = view.findViewById(R.id.loan_type);
        propertyType = view.findViewById(R.id.property_type);
        location = view.findViewById(R.id.location);
        assignedTo = view.findViewById(R.id.assign_to);
        assigner = view.findViewById(R.id.assigner);
        callerRemarks = view.findViewById(R.id.caller_remarks);
        salesmanRemarks = view.findViewById(R.id.salesman_remarks);
        status = view.findViewById(R.id.status);
        assignedOn = view.findViewById(R.id.date);
        assignedAt = view.findViewById(R.id.time);

        button = view.findViewById(R.id.fab);

        name.setText(leadDetails.getName());
        loan.setText(leadDetails.getLoanAmount());
        number.setText(leadDetails.getContactNumber());
        employment.setText(leadDetails.getEmployment());
        employmentType.setText(leadDetails.getEmploymentType());
        loanType.setText(leadDetails.getLoanType());
        propertyType.setText(leadDetails.getPropertyType());
        location.setText(leadDetails.getLocation());
        assignedTo.setText(leadDetails.getAssignedTo());
        assigner.setText(leadDetails.getAssigner());
        callerRemarks.setText(leadDetails.getTelecallerRemarks());
        salesmanRemarks.setText(leadDetails.getSalesmanReason());
        status.setText(leadDetails.getStatus());
        assignedOn.setText(leadDetails.getDate());
        assignedAt.setText(leadDetails.getTime());

        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
}
