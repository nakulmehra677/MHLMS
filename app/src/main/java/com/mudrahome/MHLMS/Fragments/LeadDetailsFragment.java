package com.mudrahome.MHLMS.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.mudrahome.MHLMS.Broadcastreceivers.CallStatus;
import com.mudrahome.MHLMS.Managers.PermissionManager;
import com.mudrahome.MHLMS.Managers.RecordingManager;
import com.mudrahome.MHLMS.Models.TimeModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mudrahome.MHLMS.Firebase.Firestore;
import com.mudrahome.MHLMS.Interfaces.OnFetchUsersListListener;
import com.mudrahome.MHLMS.Interfaces.OnUpdateLeadListener;
import com.mudrahome.MHLMS.Managers.TimeManager;
import com.mudrahome.MHLMS.Models.LeadDetails;
import com.mudrahome.MHLMS.Models.UserDetails;
import com.mudrahome.MHLMS.Models.UserList;
import com.mudrahome.MHLMS.R;

import java.util.List;

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
    private BroadcastReceiver br;

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
        IntentFilter filter = new IntentFilter("android.intent.action.PHONE_STATE");
        context.registerReceiver(br, filter);


        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.fragment_lead_details, null);

        sharedPreferences = this.getActivity().getSharedPreferences(
                getString(R.string.SH_user_details), Activity.MODE_PRIVATE);
        currentUserType = sharedPreferences.getString(getString(R.string.SH_user_type), "Salesman");

        br = new CallStatus();
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

                PermissionManager permission = new PermissionManager(getContext());

                if (permission.checkCallPhone()) {
                    if (permission.checkReadPhoneState()) {
                        if (permission.checkRecordAudio()) {
                            callCustomer();
                        } else
                            permission.requestRecordAudio();
                    } else
                        permission.requestReadPhoneState();
                } else
                    permission.requestCallPhone();
            }
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
            TelecallerEditLeadFragment.newInstance(
                    leadDetails, userList, new TelecallerEditLeadFragment.OnSubmitClickListener() {
                        @Override
                        public void onSubmitClicked(LeadDetails dialogLeadDetails) {
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

    private void callCustomer() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + leadDetails.getContactNumber()));
        startActivity(callIntent);
        RecordingManager manager = new RecordingManager(context);
        manager.startRecording();

        try {
            for (int i = 0; i < 20; i++) {
                Log.d("calll", " " + i);

                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        manager.stopRecording();
        manager.uploadRecording();
    }

    @Override
    public void onDestroy() {
        Log.d("onnnnn", "destroy");
        context.unregisterReceiver(br);
        super.onDestroy();
    }
}