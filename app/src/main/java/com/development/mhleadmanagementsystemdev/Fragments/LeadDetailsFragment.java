package com.development.mhleadmanagementsystemdev.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.development.mhleadmanagementsystemdev.Helper.FirebaseDatabaseHelper;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchSalesPersonListListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnUpdateLeadListener;
import com.development.mhleadmanagementsystemdev.Models.LeadDetails;
import com.development.mhleadmanagementsystemdev.Models.UserDetails;
import com.development.mhleadmanagementsystemdev.R;
import com.development.mhleadmanagementsystemdev.StringClass;

import java.util.ArrayList;
import java.util.List;

import static com.development.mhleadmanagementsystemdev.Activities.BaseActivity.salesmanUser;
import static com.development.mhleadmanagementsystemdev.Activities.BaseActivity.sharedPreferenceUserDetails;
import static com.development.mhleadmanagementsystemdev.Activities.BaseActivity.sharedPreferenceUserType;
import static com.development.mhleadmanagementsystemdev.Activities.BaseActivity.telecallerUser;

@SuppressLint("ValidFragment")
public class LeadDetailsFragment extends BottomSheetDialogFragment {

    private BottomSheetBehavior mBehavior;
    private TextView name, loan, number, employment, employmentType, loanType, propertyType, location,
            assignedTo, assigner, callerRemarks, salesmanRemarks, status, assignedOn, assignedAt;

    private Button button;
    private SharedPreferences sharedPreferences;
    private LinearLayout assignedToLayout, assignerLayout;
    private ProgressDialog progress;
    private Context context;

    private LeadDetails leadDetails;
    private String currentUserType;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private List<UserDetails> userDetailsList;

    private String customerNotInterested = "Customer Not Interested";
    private String documentPicked = "Document Picked";
    private String customerFollowUp = "Customer follow Up";
    private String customerNotContactable = "Customer Not Contactable";
    private String customerInterestedButDocumentPending = "Customer Interested but Document Pending";

    public LeadDetailsFragment(LeadDetails leadDetails, Context context) {
        this.leadDetails = leadDetails;
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.fragment_lead_details, null);

        sharedPreferences = this.getActivity().getSharedPreferences(sharedPreferenceUserDetails, Activity.MODE_PRIVATE);
        currentUserType = sharedPreferences.getString(sharedPreferenceUserType, "Salesman");

        firebaseDatabaseHelper = new FirebaseDatabaseHelper();

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

        assignedToLayout = view.findViewById(R.id.assigned_to_layout);
        assignerLayout = view.findViewById(R.id.assigner_layout);
        button = view.findViewById(R.id.edit_lead_details);

        setLayoutFields();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager cm = (ConnectivityManager) getActivity()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

                if (cm.getActiveNetworkInfo() != null) {
                    if (currentUserType.equals(telecallerUser)) {
                        progress = new ProgressDialog(context);
                        progress.setMessage("Loading..");
                        progress.setCancelable(false);
                        progress.setCanceledOnTouchOutside(false);
                        progress.show();

                        firebaseDatabaseHelper.fetchSalesPersonsByLocation(
                                onFetchSalesPersonListListener(), leadDetails.getLocation());
                    } else {
                        openSalesmanFragment();
                    }
                } else
                    Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show();
            }
        });

        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        return dialog;
    }

    private void setLayoutFields() {
        if (currentUserType.equals(telecallerUser))
            assignerLayout.setVisibility(View.GONE);
        else if (currentUserType.equals(salesmanUser))
            assignedToLayout.setVisibility(View.GONE);
        else
            button.setVisibility(View.GONE);

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
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private OnFetchSalesPersonListListener onFetchSalesPersonListListener() {
        return new OnFetchSalesPersonListListener() {
            @Override
            public void onListFetched(final List userDetailList, List userName) {
                progress.dismiss();
                if (userName.size() > 0)
                    openTelecallerFragment(userDetailList, userName);
                else
                    Toast.makeText(context, "No Salesmen are present for " +
                            leadDetails.getLocation() + ".", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void openTelecallerFragment(final List arrayList, List userName) {
        EditLeadDetailsFragment.newInstance(userName, new EditLeadDetailsFragment.OnSubmitClickListener() {
            @Override
            public void onSubmitClicked(String dialogAssignedTo) {
                leadDetails.setAssignedTo(dialogAssignedTo);

                userDetailsList = new ArrayList<>();
                userDetailsList = arrayList;

                String strAssignedToUId = null;
                for (UserDetails userDetails : userDetailsList) {
                    if (userDetails.getUserName().equals(dialogAssignedTo)) {
                        strAssignedToUId = userDetails.getuId();
                    }
                }
                leadDetails.setAssignedToUId(strAssignedToUId);
                firebaseDatabaseHelper.updateLeadDetails(onUpdateLeadListener(), leadDetails);
            }
        }).show(getFragmentManager(), "promo");
    }

    private void openSalesmanFragment() {
        SalesmanEditLeadDetailsFragment.newInstance(new SalesmanEditLeadDetailsFragment.OnSalesmanSubmitClickListener() {
            @Override
            public void onSubmitClicked(String dialogSalesmanRemarks, String dialogSalesmanReason) {

                leadDetails.setSalesmanRemarks(dialogSalesmanRemarks);
                leadDetails.setSalesmanReason(dialogSalesmanReason);

                if (dialogSalesmanRemarks.equals(customerNotInterested))
                    leadDetails.setStatus("Inactive");
                else if (dialogSalesmanRemarks.equals(documentPicked))
                    leadDetails.setStatus("Closed");
                else if (dialogSalesmanRemarks.equals(customerFollowUp))
                    leadDetails.setStatus("Follow Up");
                else if (dialogSalesmanRemarks.equals(customerNotContactable))
                    leadDetails.setStatus("Inactive");
                else if (dialogSalesmanRemarks.equals(customerInterestedButDocumentPending))
                    leadDetails.setStatus("Work in Progress");
                else
                    leadDetails.setStatus("Active");

                progress = new ProgressDialog(context);
                progress.setMessage("Loading..");
                progress.setCancelable(false);
                progress.setCanceledOnTouchOutside(false);
                progress.show();

                firebaseDatabaseHelper.updateLeadDetails(onUpdateLeadListener(), leadDetails);
            }
        }).show(getFragmentManager(), "promo");
    }

    private OnUpdateLeadListener onUpdateLeadListener() {
        return new OnUpdateLeadListener() {
            @Override
            public void onLeadUpdated() {
                Toast.makeText(context, R.string.lead_update, Toast.LENGTH_SHORT).show();
                setLayoutFields();
                progress.dismiss();
            }
        };
    }
}