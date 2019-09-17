package com.mudrahome.MHLMS.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.mudrahome.MHLMS.Firebase.Firestore;
import com.mudrahome.MHLMS.Interfaces.FirestoreInterfaces;
import com.mudrahome.MHLMS.Managers.PermissionManager;
import com.mudrahome.MHLMS.Models.TimeModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mudrahome.MHLMS.Managers.TimeManager;
import com.mudrahome.MHLMS.Models.LeadDetails;
import com.mudrahome.MHLMS.Models.UserDetails;
import com.mudrahome.MHLMS.Models.UserList;
import com.mudrahome.MHLMS.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
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
    private TextView status;
    private TextView assignedOn;
    private TextView assignedAt;
    private TextView customerRemarks;
    private TextView workDate;
    private TextView workTime;
    private TextView bankNames;
    private TextView assignerContact;
    private TextView assigneeContact;

    private Button button;
    private LinearLayout assignedToLayout, assignerLayout, callerRemarksLayout, sallerRemarksLayout, employmentTypeLayout;
    private LinearLayout salesmanRemarksHeadingLayout;
    private LinearLayout customerRemarksLayout;
    private ProgressDialog progress;
    private Context context;

    private LeadDetails leadDetails;
    private com.mudrahome.MHLMS.Firebase.Firestore firestore;
    private BroadcastReceiver br;
    private String userType;

    private String customerNotInterested = "Customer Not Interested";
    private String documentPicked = "Document Picked";
    private String customerFollowUp = "Customer follow Up";
    private String customerNotContactable = "Customer Not Contactable";
    private String customerInterestedButDocumentPending = "Customer Interested but Document Pending";
    private String notDoable = "Not Doable";
    private String documentPickedFileLoggedIn = "Document Picked and File Logged in";




    public LeadDetailsFragment(LeadDetails leadDetails, Context context, String userType) {
        this.leadDetails = leadDetails;
        this.context = context;
        this.userType = userType;
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialog;
        try {
            Field behaviorField = bottomSheetDialog.getClass().getDeclaredField("behavior");
            behaviorField.setAccessible(true);
            final BottomSheetBehavior behavior = (BottomSheetBehavior) behaviorField.get(bottomSheetDialog);
            behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            });
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d("State", "onCreateDialog");

        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setCancelable(false);

        View view = View.inflate(getContext(), R.layout.fragment_lead_details, null);

//        br = new CallStatus();
        firestore = new com.mudrahome.MHLMS.Firebase.Firestore();

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
        customerRemarks = view.findViewById(R.id.customer_remarks);
        status = view.findViewById(R.id.status);
        workDate = view.findViewById(R.id.date);
        workTime = view.findViewById(R.id.time);
        assignedOn = view.findViewById(R.id.assign_date);
        assignedAt = view.findViewById(R.id.assign_time);
        bankNames = view.findViewById(R.id.bank_names);
        assignerContact = view.findViewById(R.id.assigner_contact_number);
        assigneeContact = view.findViewById(R.id.assignee_contact_number);
        callerRemarksLayout = view.findViewById(R.id.caller_remarks_layout);
        sallerRemarksLayout = view.findViewById(R.id.sales_remarks_layout);
        customerRemarksLayout = view.findViewById(R.id.customer_remarks_layout);

        salesmanRemarksHeadingLayout = view.findViewById(R.id.salesman_remarks_heading_layout);

        employmentTypeLayout = view.findViewById(R.id.employment_type_layout);
        assignedToLayout = view.findViewById(R.id.assigned_to_layout);
        assignerLayout = view.findViewById(R.id.assigner_layout);
        button = view.findViewById(R.id.edit_lead_details);

        setLayoutFields();

        if (leadDetails.getAssignerUId() == null) {
            assignerContact.setClickable(false);
            assignerContact.setCompoundDrawables(null, null, null, null);
            assignerContact.setText("Not available");
        } else {

            try{
                firestore.getUsers(new FirestoreInterfaces.OnGetUserDetails() {
                    @Override
                    public void onSuccess(UserDetails userDetails) {

                            try {
                                assignerContact.setText(userDetails.getContactNumber());
                            }catch (Exception e){
                                assignerContact.setText("Not available");
                            }

                    }

                    @Override
                    public void fail() {

                    }
                },leadDetails.getAssignerUId());
            }catch (Exception e){
                assignerContact.setText("Not available");
            }


            assignerContact.setOnClickListener(view13 -> {

                PermissionManager permission = new PermissionManager(getContext());

                if (permission.checkCallPhone()) {
//                    if (permission.checkReadPhoneState()) {
//                        if (permission.checkRecordAudio()) {
                    callCustomer(leadDetails.getAssignerContact());
//                        } else
//                            permission.requestRecordAudio();
//                    } else
//                        permission.requestReadPhoneState();
                } else
                    permission.requestCallPhone();
            });

        }

        if (leadDetails.getAssignedToUId() == null) {
            /*assigneeCallbutton.setVisibility(View.GONE);*/

            assigneeContact.setClickable(false);
            assigneeContact.setCompoundDrawables(null, null, null, null);
            assigneeContact.setText("Not available");
        } else {

            try {
                firestore.getUsers(new FirestoreInterfaces.OnGetUserDetails() {
                    @Override
                    public void onSuccess(UserDetails userDetails) {

                        try {
                            assigneeContact.setText(userDetails.getContactNumber());
                        }catch (Exception e){
                            assigneeContact.setText("Not available");
                            Log.d("AssigneeContact", " error " + e.getMessage());
                        }



                    }

                    @Override
                    public void fail() {

                    }
                },leadDetails.getAssignedToUId());
            }catch (Exception e){
                assigneeContact.setText("Not available");
            }




            assigneeContact.setOnClickListener(view12 -> {

                PermissionManager permission = new PermissionManager(getContext());

                if (permission.checkCallPhone()) {
//                    if (permission.checkReadPhoneState()) {
//                        if (permission.checkRecordAudio()) {
                    callCustomer(leadDetails.getAssigneeContact());
//                        } else
//                            permission.requestRecordAudio();
//                    } else
//                        permission.requestReadPhoneState();
                } else
                    permission.requestCallPhone();
            });

        }

        number.setOnClickListener(view1 -> {

            PermissionManager permission = new PermissionManager(getContext());

            if (permission.checkCallPhone()) {
//                    if (permission.checkReadPhoneState()) {
//                        if (permission.checkRecordAudio()) {
                callCustomer(leadDetails.getContactNumber());
//                        } else
//                            permission.requestRecordAudio();
//                    } else
//                        permission.requestReadPhoneState();
            } else
                permission.requestCallPhone();
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConnectivityManager cm = (ConnectivityManager) getActivity()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

                if (cm.getActiveNetworkInfo() != null) {
                    if (userType.equals(getString(R.string.telecaller))) {
                        progress = new ProgressDialog(context);
                        progress.setMessage("Loading...");
                        progress.setCancelable(false);
                        progress.setCanceledOnTouchOutside(false);
                        progress.show();

                        firestore.fetchUsersByUserType(
                                onFetchSalesPersonList(),
                                leadDetails.getLocation(),
                                getString(R.string.salesman));
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
        if (userType.equals(getString(R.string.telecaller)))
            assignerLayout.setVisibility(View.GONE);
        else if (userType.contains(getString(R.string.salesman)))
            assignedToLayout.setVisibility(View.GONE);
        else
            button.setVisibility(View.GONE);

        if (leadDetails.getEmploymentType().equals("None") || leadDetails.getEmploymentType().isEmpty()) {
            employmentTypeLayout.setVisibility(View.GONE);
        } else {
            employmentType.setText(leadDetails.getEmploymentType());
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2);

        sallerRemarksLayout.removeAllViewsInLayout();
        if (leadDetails.getSalesmanReason() == null  || leadDetails.getSalesmanReason().get(0).equals("None")) {
            salesmanRemarksHeadingLayout.setVisibility(View.GONE);
            sallerRemarksLayout.setVisibility(View.GONE);
        } else {

            View view2 = new View(getContext());
            view2.setLayoutParams(layoutParams);
            view2.setBackgroundColor(getResources().getColor(R.color.colorGray));
            sallerRemarksLayout.addView(view2);

            for (int i = leadDetails.getSalesmanReason().size() - 1; i >= 0; i--) {

                if(!leadDetails.getSalesmanReason().get(i).isEmpty()){
                    TextView textView = new TextView(getContext());
                    textView.setTextColor(getResources().getColor(R.color.coloBlack));
                    textView.setText(leadDetails.getSalesmanReason().get(i));
                    sallerRemarksLayout.addView(textView);

                    View view3 = new View(getContext());
                    view3.setLayoutParams(layoutParams);
                    view3.setBackgroundColor(getResources().getColor(R.color.colorGray));
                    sallerRemarksLayout.addView(view3);
                }

            }
        }

        if (leadDetails.getSalesmanRemarks().equals("None") || leadDetails.getSalesmanRemarks().isEmpty()) {
            customerRemarksLayout.setVisibility(View.GONE);
        } else {
            customerRemarks.setText(leadDetails.getSalesmanRemarks());
        }
        name.setText(leadDetails.getName());
        loan.setText(leadDetails.getLoanAmount());
        number.setText(leadDetails.getContactNumber());
        employment.setText(leadDetails.getEmployment());
        loanType.setText(leadDetails.getLoanType());
        propertyType.setText(leadDetails.getPropertyType());
        location.setText(leadDetails.getLocation());
        assignedTo.setText(leadDetails.getAssignedTo());
        assigner.setText(leadDetails.getAssigner());
        status.setText(leadDetails.getStatus());
        workDate.setText(leadDetails.getDate());
        workTime.setText(leadDetails.getTime());
        assignedOn.setText(leadDetails.getAssignDate());
        assignedAt.setText(leadDetails.getAssignTime());



        callerRemarksLayout.removeAllViewsInLayout();



        StringBuilder csvBuilder = new StringBuilder();
        for (String bank : leadDetails.getBanks()) {
            csvBuilder.append(bank);
            csvBuilder.append(", ");
        }
        String bankList = csvBuilder.toString();
        bankNames.setText(bankList);

        View view = new View(getContext());
        view.setLayoutParams(layoutParams);
        view.setBackgroundColor(getResources().getColor(R.color.colorGray));
        callerRemarksLayout.addView(view);

        for (int i = leadDetails.getTelecallerRemarks().size() - 1; i >= 0; i--) {

            if(!leadDetails.getTelecallerRemarks().get(i).isEmpty()){
                TextView textView = new TextView(getContext());
                textView.setTextColor(getResources().getColor(R.color.coloBlack));
                textView.setText(leadDetails.getTelecallerRemarks().get(i));
                callerRemarksLayout.addView(textView);

                View view1 = new View(getContext());
                view1.setLayoutParams(layoutParams);
                view1.setBackgroundColor(getResources().getColor(R.color.colorGray));
                callerRemarksLayout.addView(view1);
            }

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//        IntentFilter filter = new IntentFilter("android.intent.action.PHONE_STATE");
//        context.registerReceiver(br, filter);
//        Log.d("State", "onStart");
    }

    private FirestoreInterfaces.OnFetchUsersList onFetchSalesPersonList() {
        return userList -> {
            progress.dismiss();
            if (userList.getUserList().size() > 0)
                openTelecallerFragment(userList.getUserList());
            else
                Toast.makeText(context, "No Salesmen are present for " +
                        leadDetails.getLocation() + ".", Toast.LENGTH_SHORT).show();
        };
    }

    private void openTelecallerFragment(final List<UserDetails> userList) {
        if (userList.size() != 0) {
            TelecallerEditLeadFragment.newInstance(
                    leadDetails, userList, dialogLeadDetails -> {
                        progress = new ProgressDialog(context);
                        progress.setMessage("Loading..");
                        progress.setCancelable(false);
                        progress.setCanceledOnTouchOutside(false);
                        progress.show();

                        firestore.updateLeadDetails(onUpdateLead(), dialogLeadDetails);
                    }).show(getFragmentManager(), "promo");
        }
    }

    private void openSalesmanFragment() {
        SalesmanEditLeadFragment.newInstance(leadDetails, (dialogSalesmanRemarks, dialogSalesmanReason, banks) -> {

            TimeManager timeManager = new TimeManager();
            TimeModel timeModel = timeManager.getTime();

            leadDetails.setDate(timeModel.getDate());
            leadDetails.setTime(timeModel.getTime());
            leadDetails.setTimeStamp(timeModel.getTimeStamp());
            leadDetails.setBanks(banks);

            ArrayList<String> salesmanReson = leadDetails.getSalesmanReason();
            salesmanReson.add(dialogSalesmanReason);

            leadDetails.setSalesmanRemarks(dialogSalesmanRemarks);
            leadDetails.setSalesmanReason(salesmanReson);

            if (dialogSalesmanRemarks.equals(customerNotInterested))
                leadDetails.setStatus(getString(R.string.inactive));
            else if (dialogSalesmanRemarks.equals(documentPicked))
                leadDetails.setStatus(getString(R.string.work_in_progress));
            else if (dialogSalesmanRemarks.equals(documentPickedFileLoggedIn))
                leadDetails.setStatus(getString(R.string.closed));
            else if (dialogSalesmanRemarks.equals(customerFollowUp))
                leadDetails.setStatus(getString(R.string.follow_up));
            else if (dialogSalesmanRemarks.equals(customerNotContactable))
                leadDetails.setStatus(getString(R.string.inactive));
            else if (dialogSalesmanRemarks.equals(customerInterestedButDocumentPending))
                leadDetails.setStatus(getString(R.string.work_in_progress));
            else if (dialogSalesmanRemarks.equals(notDoable))
                leadDetails.setStatus(getString(R.string.not_doable));
            else
                leadDetails.setStatus(getString(R.string.active));

            progress = new ProgressDialog(context);
            progress.setMessage("Loading..");
            progress.setCancelable(false);
            progress.setCanceledOnTouchOutside(false);
            progress.show();

            firestore.updateLeadDetails(onUpdateLead(), leadDetails);
        }).show(getFragmentManager(), "promo");
    }

    private FirestoreInterfaces.OnUpdateLead onUpdateLead() {
        return new FirestoreInterfaces.OnUpdateLead() {
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

    private void callCustomer(String number) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        startActivity(callIntent);
    }

//    @Override
//    public void onDestroy() {
//        Log.d("State", "onDestroy");
//        context.unregisterReceiver(br);
//        super.onDestroy();
//    }
}