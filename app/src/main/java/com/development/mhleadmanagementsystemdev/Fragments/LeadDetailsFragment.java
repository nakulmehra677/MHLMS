package com.development.mhleadmanagementsystemdev.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.development.mhleadmanagementsystemdev.Models.TimeModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.development.mhleadmanagementsystemdev.Firebase.Firestore;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchUsersListListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnUpdateLeadListener;
import com.development.mhleadmanagementsystemdev.Managers.TimeManager;
import com.development.mhleadmanagementsystemdev.Models.LeadDetails;
import com.development.mhleadmanagementsystemdev.Models.UserDetails;
import com.development.mhleadmanagementsystemdev.Models.UserList;
import com.development.mhleadmanagementsystemdev.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.Manifest.permission.CALL_PHONE;

@SuppressLint("ValidFragment")
public class LeadDetailsFragment extends BottomSheetDialogFragment {

    private BottomSheetBehavior mBehavior;
    private TextView name;
    private TextView loan;
    private TextView number;
    private TextView employment;
    private TextView employmentType;
    private TextView loanType;
    private TextView propertyType;
    private TextView location;
    private TextView assignedTo;
    private TextView assigner;
    private TextView callerRemarks;
    private TextView salesmanRemarks;
    private TextView status;
    private TextView assignedOn;
    private TextView assignedAt;
    private TextView customerRemarks;
    private TextView workDate;
    private TextView workTime;

    private Button button, callButton;
    private SharedPreferences sharedPreferences;
    private LinearLayout assignedToLayout, assignerLayout;
    private ProgressDialog progress;
    private Context context;

    private LeadDetails leadDetails;
    private String currentUserType;
    private Firestore firestore;

    private String customerNotInterested = "Customer Not Interested";
    private String documentPicked = "Document Picked";
    private String customerFollowUp = "Customer follow Up";
    private String customerNotContactable = "Customer Not Contactable";
    private String customerInterestedButDocumentPending = "Customer Interested but Document Pending";
    private String notDoable = "Not Doable";

    public LeadDetailsFragment(LeadDetails leadDetails, Context context) {
        this.leadDetails = leadDetails;
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.fragment_lead_details, null);

        sharedPreferences = this.getActivity().getSharedPreferences(
                getString(R.string.SH_user_details), Activity.MODE_PRIVATE);
        currentUserType = sharedPreferences.getString(getString(R.string.SH_user_type), "Salesman");

        firestore = new Firestore();

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
        customerRemarks = view.findViewById(R.id.customer_remarks);
        status = view.findViewById(R.id.status);
        workDate = view.findViewById(R.id.date);
        workTime = view.findViewById(R.id.time);
        assignedOn = view.findViewById(R.id.assign_date);
        assignedAt = view.findViewById(R.id.assign_time);

        assignedToLayout = view.findViewById(R.id.assigned_to_layout);
        assignerLayout = view.findViewById(R.id.assigner_layout);
        button = view.findViewById(R.id.edit_lead_details);
        callButton = view.findViewById(R.id.call_button);

        setLayoutFields();

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//
//                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.RECORD_AUDIO},
//                            0);
//
//                } else {
//                    Intent intent = new Intent(getContext(), RecordingService.class);
//                    getActivity().startService(intent);


                //CallRecord record = new CallRecord(getContext());
                Intent callIntent = new Intent(Intent.ACTION_CALL);

                callIntent.setData(Uri.parse("tel:" + leadDetails.getContactNumber()));
                if (ContextCompat.checkSelfPermission(getContext(), CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                    Log.d("call record", "started");
                    startActivity(callIntent);
                } else {
                    requestPermissions(new String[]{CALL_PHONE}, 1);
                }
            }
            //}
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager cm = (ConnectivityManager) getActivity()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

                if (cm.getActiveNetworkInfo() != null) {
                    if (currentUserType.equals(getString(R.string.telecaller))) {
                        progress = new ProgressDialog(context);
                        progress.setMessage("Loading..");
                        progress.setCancelable(false);
                        progress.setCanceledOnTouchOutside(false);
                        progress.show();

                        firestore.fetchSalesPersons(
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
        if (currentUserType.equals(getString(R.string.telecaller)))
            assignerLayout.setVisibility(View.GONE);
        else if (currentUserType.equals(getString(R.string.salesman)))
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
        customerRemarks.setText(leadDetails.getSalesmanRemarks());
        status.setText(leadDetails.getStatus());
        workDate.setText(leadDetails.getDate());
        workTime.setText(leadDetails.getTime());
        assignedOn.setText(leadDetails.getAssignDate());
        assignedAt.setText(leadDetails.getAssignTime());
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private OnFetchUsersListListener onFetchSalesPersonListListener() {
        return new OnFetchUsersListListener() {
            @Override
            public void onListFetched(UserList userList) {
                progress.dismiss();
                if (userList.getUserList().size() > 0)
                    openTelecallerFragment(userList.getUserList());
                else
                    Toast.makeText(context, "No Salesmen are present for " +
                            leadDetails.getLocation() + ".", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void openTelecallerFragment(final List<UserDetails> userList) {

        if (userList.size() != 0) {
            List<String> salesPersonNameList = new ArrayList<>();
            for (UserDetails user : userList) {
                salesPersonNameList.add(user.getUserName());
            }

            TelecallerEditLeadFragment.newInstance(
                    leadDetails, salesPersonNameList, new TelecallerEditLeadFragment.OnSubmitClickListener() {
                        @Override
                        public void onSubmitClicked(String dialogAssignedTo, LeadDetails dialogLeadDetails) {

                            leadDetails = dialogLeadDetails;
                            leadDetails.setAssignedTo(dialogAssignedTo);

                            TimeManager timeManager = new TimeManager();
                            TimeModel timeModel = timeManager.getTime();

                            leadDetails.setDate(timeModel.getDate());
                            leadDetails.setTime(timeModel.getTime());
                            leadDetails.setTimeStamp(timeModel.getTimeStamp());

                            Log.d("sssssssssssssssss", String.valueOf(timeModel.getTimeStamp()));

                            String strAssignedToUId = null;
                            for (UserDetails userDetails : userList) {
                                if (userDetails.getUserName().equals(dialogAssignedTo)) {
                                    strAssignedToUId = userDetails.getuId();
                                }
                            }
                            leadDetails.setAssignedToUId(strAssignedToUId);

                            progress = new ProgressDialog(context);
                            progress.setMessage("Loading..");
                            progress.setCancelable(false);
                            progress.setCanceledOnTouchOutside(false);
                            progress.show();

                            firestore.updateLeadDetails(onUpdateLeadListener(), dialogLeadDetails);
                        }
                    }).show(getFragmentManager(), "promo");
        }
    }

    private void openSalesmanFragment() {
        SalesmanEditLeadFragment.newInstance(leadDetails, new SalesmanEditLeadFragment.OnSalesmanSubmitClickListener() {
            @Override
            public void onSubmitClicked(String dialogSalesmanRemarks, String dialogSalesmanReason) {

                TimeManager timeManager = new TimeManager();
                TimeModel timeModel = timeManager.getTime();

                leadDetails.setDate(timeModel.getDate());
                leadDetails.setTime(timeModel.getTime());
                leadDetails.setTimeStamp(timeModel.getTimeStamp());

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
                else if (dialogSalesmanRemarks.equals(notDoable))
                    leadDetails.setStatus("Not Doable");
                else
                    leadDetails.setStatus("Active");

                progress = new ProgressDialog(context);
                progress.setMessage("Loading..");
                progress.setCancelable(false);
                progress.setCanceledOnTouchOutside(false);
                progress.show();

                firestore.updateLeadDetails(onUpdateLeadListener(), leadDetails);
            }
        }).show(getFragmentManager(), "promo");
    }

    private OnUpdateLeadListener onUpdateLeadListener() {
        return new OnUpdateLeadListener() {
            @Override
            public void onLeadUpdated() {
                Toast.makeText(context, R.string.lead_update, Toast.LENGTH_SHORT).show();
                setLayoutFields();
                if (progress.isShowing())
                    progress.dismiss();
            }

            @Override
            public void onFailer() {
                Toast.makeText(context, R.string.lead_update_failed, Toast.LENGTH_SHORT).show();
            }
        };
    }
}