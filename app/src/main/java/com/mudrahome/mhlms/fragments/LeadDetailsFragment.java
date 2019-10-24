package com.mudrahome.mhlms.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mudrahome.mhlms.R;
import com.mudrahome.mhlms.activities.LeadListActivity;
import com.mudrahome.mhlms.firebase.Firestore;
import com.mudrahome.mhlms.interfaces.FirestoreInterfaces;
import com.mudrahome.mhlms.managers.PermissionManager;
import com.mudrahome.mhlms.managers.TimeManager;
import com.mudrahome.mhlms.model.LeadDetails;
import com.mudrahome.mhlms.model.TimeModel;
import com.mudrahome.mhlms.model.UserDetails;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mudrahome.mhlms.fragments.ViewAllRemarksFragment.newInstance;

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
    private TextView viewallCallerRemark;
    private TextView viewallSalesmanRemark;
    private TextView latestsalesmanRemark;
    private TextView latestCallerRemark;

    private Button button;
    private LinearLayout assignedToLayout, assignerLayout, callerRemarksLayout, sallerRemarksLayout, employmentTypeLayout;
    private LinearLayout salesmanRemarksHeadingLayout;
    private LinearLayout customerRemarksLayout;
    private ProgressDialog progress;
    private Context context;

    private LeadDetails leadDetails;
    private Firestore firestore;
    private BroadcastReceiver br;
    private String userType;

    private String customerNotInterested = "Customer Not Interested";
    private String documentPicked = "Document Picked";
    private String customerFollowUp = "Customer follow Up";
    private String customerNotContactable = "Customer Not Contactable";
    private String customerInterestedButDocumentPending = "Customer Interested but Document Pending";
    private String notDoable = "Not Doable";
    private String documentPickedFileLoggedIn = "Document Picked and File Logged in";

    private Boolean isEdit = false;


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
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setCancelable(false);

        View view = View.inflate(getContext(), R.layout.fragment_lead_details, null);

//        br = new CallStatus();
        firestore = new Firestore();

        viewallCallerRemark = view.findViewById(R.id.viewallCallerRemark);
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
        /*callerRemarksLayout = view.findViewById(R.id.caller_remarks_layout);*/
        /*sallerRemarksLayout = view.findViewById(R.id.sales_remarks_layout);*/
        customerRemarksLayout = view.findViewById(R.id.customer_remarks_layout);
        viewallSalesmanRemark = view.findViewById(R.id.viewallSalesmanRemark);
        latestCallerRemark = view.findViewById(R.id.latestCallerRemark);
        latestsalesmanRemark = view.findViewById(R.id.latestsalesmanRemark);

        salesmanRemarksHeadingLayout = view.findViewById(R.id.salesman_remarks_heading_layout);

        employmentTypeLayout = view.findViewById(R.id.employment_type_layout);
        assignedToLayout = view.findViewById(R.id.assigned_to_layout);
        assignerLayout = view.findViewById(R.id.assigner_layout);
        button = view.findViewById(R.id.edit_lead_details);

        setLayoutFields();

        dialog.setOnKeyListener((dialogInterface, i, keyEvent) -> {

            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                if (isEdit) {
                    Intent intent = new Intent(getContext(), LeadListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    if (getActivity() != null)
                        getActivity().finish();
                    /*dialog.dismiss();*/
                    return true;
                } else {
                    return false;
                }

            } else {
                return false;
            }


        });

        if (leadDetails.getAssignerUId().equals("Not available")) {
            hideAssignerContact();
        } else {
            firestore.getUsers(new FirestoreInterfaces.OnGetUserDetails() {
                @Override
                public void onSuccess(UserDetails userDetails) {
                    if (!userDetails.getContactNumber().equals("Not available")) {
                        assignerContact.setText(userDetails.getContactNumber());
//                        leadDetails.setAssignerContact(userDetails.getContactNumber());
                    } else {
                        hideAssignerContact();
                    }
                }

                @Override
                public void fail() {
                    hideAssignerContact();
                }
            }, leadDetails.getAssignerUId());

            assignerContact.setOnClickListener(view13 -> {
                PermissionManager permission = new PermissionManager(getContext());
                if (permission.checkCallPhone()) {
//                    if (permission.checkReadPhoneState()) {
//                        if (permission.checkRecordAudio()) {
//                    callCustomer(leadDetails.getAssignerContact());
//                        } else
//                            permission.requestRecordAudio();
//                    } else
//                        permission.requestReadPhoneState();
                } else
                    permission.requestCallPhone();
            });
        }

        if (leadDetails.getAssignedToUId().equals("Not available")) {
            hideAssigneeContact();
        } else {
            firestore.getUsers(new FirestoreInterfaces.OnGetUserDetails() {
                @Override
                public void onSuccess(UserDetails userDetails) {
                    if (!userDetails.getContactNumber().equals("Not available")) {
                        assigneeContact.setText(userDetails.getContactNumber());
//                        leadDetails.setAssigneeContact(userDetails.getContactNumber());
                    } else {
                        hideAssigneeContact();
                    }
                }

                @Override
                public void fail() {
                    hideAssigneeContact();
                }
            }, leadDetails.getAssignedToUId());

            assigneeContact.setOnClickListener(view12 -> {

                PermissionManager permission = new PermissionManager(getContext());

                if (permission.checkCallPhone()) {
//                    if (permission.checkReadPhoneState()) {
//                        if (permission.checkRecordAudio()) {
//                    callCustomer(leadDetails.getAssigneeContact());
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

        button.setOnClickListener(view14 ->
        {
            ConnectivityManager cm = (ConnectivityManager) getActivity()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            if (cm.getActiveNetworkInfo() != null) {
                if (userType.equals(getString(R.string.telecaller))
                        || userType.equals(getString(R.string.business_associate))
                        || userType.equals(getString(R.string.teleassigner))) {
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
        });

        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        return dialog;
    }

    private void hideAssignerContact() {
        assignerContact.setText("Not available");
        assignerContact.setClickable(false);
        assignerContact.setCompoundDrawables(null, null, null, null);
    }

    private void hideAssigneeContact() {
        assigneeContact.setClickable(false);
        assigneeContact.setCompoundDrawables(null, null, null, null);
        assigneeContact.setText("Not available");
    }

    private void setLayoutFields() {
        if (userType.equals(getString(R.string.telecaller)) ||
                userType.equals(getString(R.string.teleassigner)))
            assignerLayout.setVisibility(View.GONE);

        else if (userType.equals(getString(R.string.salesman)) ||
                userType.equals(getString(R.string.business_associate)))
            assignedToLayout.setVisibility(View.GONE);

        else
            button.setVisibility(View.GONE);

        if (leadDetails.getEmploymentType().equals("None") || leadDetails.getEmploymentType().isEmpty()) {
            employmentTypeLayout.setVisibility(View.GONE);
        } else {
            employmentType.setText(leadDetails.getEmploymentType());
        }

        if (leadDetails.getSalesmanReason() != null) {
            if (leadDetails.getSalesmanReason().size() != 1) {
                int temp = 1;
                String remark = getLatestRemark(leadDetails.getSalesmanReason().get(leadDetails.getSalesmanReason().size() - temp));
                while (remark == null) {
                    temp++;
                    remark = getLatestRemark(leadDetails.getSalesmanReason().get(leadDetails.getSalesmanReason().size() - temp));
                }
                latestsalesmanRemark.setText(Html.fromHtml(remark));
                viewallSalesmanRemark.setOnClickListener(view -> newInstance(leadDetails.getSalesmanReason(), "Salesman's").show(getChildFragmentManager(), "ShowRemarks"));
            } else {
                if (!leadDetails.getSalesmanReason().get(0).matches("Not available")) {
                    latestsalesmanRemark.setText(Html.fromHtml(getLatestRemark(leadDetails.getSalesmanReason().get(0))));
                    viewallSalesmanRemark.setText("");
                } else {
                    latestsalesmanRemark.setVisibility(View.GONE);
                    viewallSalesmanRemark.setText("Not available");
                }

                viewallSalesmanRemark.setTextColor(getResources().getColor(R.color.coloBlack));
            }


        }

        if (leadDetails.getTelecallerRemarks() != null) {
            if (leadDetails.getTelecallerRemarks().size() > 1) {
                String remark = getLatestRemark(leadDetails.getTelecallerRemarks().get(leadDetails.getTelecallerRemarks().size() - 1));
                int temp = 1;
                while (remark == null) {
                    temp++;
                    remark = getLatestRemark(leadDetails.getTelecallerRemarks().get(leadDetails.getTelecallerRemarks().size() - temp));
                }

                latestCallerRemark.setText(Html.fromHtml(remark));
                viewallCallerRemark.setOnClickListener(view -> newInstance(leadDetails.getTelecallerRemarks(), "Caller's").show(getChildFragmentManager(), "ShowRemarks"));
            } else {
                if (!leadDetails.getTelecallerRemarks().get(0).matches("Not available")) {
                    latestCallerRemark.setText(Html.fromHtml(getLatestRemark(leadDetails.getTelecallerRemarks().get(0))));
                    viewallCallerRemark.setText("");
                } else {
                    viewallCallerRemark.setText("Not available");
                    latestCallerRemark.setVisibility(View.GONE);
                }
                viewallCallerRemark.setTextColor(getResources().getColor(R.color.coloBlack));
            }
        }

        if (leadDetails.getSalesmanRemarks() == null || leadDetails.getSalesmanRemarks().equals("Not available")) {
            customerRemarksLayout.setVisibility(View.GONE);
        } else {
            customerRemarks.setText(leadDetails.getSalesmanRemarks());
        }
        name.setText(leadDetails.getName());
        loan.setText(leadDetails.getLoanAmount());
        number.setText(leadDetails.getContactNumber());
        Log.d("number", leadDetails.getContactNumber());
        if (leadDetails.getContactNumber().equals("Not available")) {
            number.setClickable(false);
            number.setCompoundDrawables(null, null, null, null);
        }
        employment.setText(leadDetails.getEmployment());
        loanType.setText(leadDetails.getLoanType());
        propertyType.setText(leadDetails.getPropertyType());
        location.setText(leadDetails.getLocation());
        assignedTo.setText(leadDetails.getAssignedTo());
        assigner.setText(leadDetails.getAssigner());
        status.setText(leadDetails.getStatus());
//        workDate.setText(leadDetails.getDate());
//        workTime.setText(leadDetails.getTime());
        assignedOn.setText(leadDetails.getAssignDate());
        assignedAt.setText(leadDetails.getAssignTime());

        StringBuilder csvBuilder = new StringBuilder();
        for (String bank : leadDetails.getBanks()) {
            csvBuilder.append(bank);
            csvBuilder.append(", ");
        }
        String bankList = csvBuilder.toString();
        bankNames.setText(bankList);
    }

    private String getLatestRemark(String remark) {
        String r = null;

        if (remark.contains("@@")) {

            String[] remarkWithTime = remark.split("@@");

            @SuppressLint("SimpleDateFormat") DateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
            Date resultdate = new Date(Long.parseLong(remarkWithTime[1]));

            if (!remarkWithTime[0].isEmpty())
                r = "<font color=\"#196587\">" + sdf.format(resultdate) + "</font>" + "<br>" + remarkWithTime[0];
        } else {
            if (!remark.isEmpty())
                r = remark;
        }

        return r;
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
                    }, userType).show(getFragmentManager(), "promo");
        }
    }


    private void openSalesmanFragment() {
        SalesmanEditLeadFragment.newInstance(leadDetails, (dialogSalesmanRemarks, dialogSalesmanReason, banks) -> {

            TimeManager timeManager = new TimeManager();

//            leadDetails.setDate(timeModel.getDate());
//            leadDetails.setTime(timeModel.getTime());
            leadDetails.setTimeStamp(timeManager.getTimeStamp());
            leadDetails.setBanks(banks);

            ArrayList<String> salesmanReson = leadDetails.getSalesmanReason();
            salesmanReson.add(dialogSalesmanReason + "@@" + System.currentTimeMillis());        // Set SalesmanReason with timesteamp

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
                isEdit = true;
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


}