package com.mudrahome.mhlms.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import com.mudrahome.mhlms.R;
import com.mudrahome.mhlms.interfaces.FirestoreInterfaces;
import com.mudrahome.mhlms.model.UserDetails;
import com.mudrahome.mhlms.model.UserList;
import com.mudrahome.mhlms.sharedPreferences.UserDataSharedPreference;


public class FilterActivity extends BaseActivity {

    private String strLocation;
    private String strAssigner;
    private String strAssignee;
    private String strStatus;
    private String strLoanType;

    private com.mudrahome.mhlms.firebase.Firestore firestore;
    private UserDataSharedPreference preference;

    private RadioGroup locationRadioGroup;
    private RadioGroup assignerRadioGroup;
    private RadioGroup assigneeRadioGroup;
    private RadioGroup loanTypeRadioGroup;
    private RadioGroup statusRadioGroup;

    private ScrollView locationScrollView;
    private ScrollView assignerScrollView;
    private ScrollView assigneeScrollView;
    private ScrollView loanTypeScrollView;
    private ScrollView statusScrollView;

    private Button locationButton;
    private Button assignerButton;
    private Button assigneeButton;
    private Button loanTypeButton;
    private Button statusButton;

    private boolean assignerLocationChanged;
    private boolean assigneeLocationChanged;

    private int userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        Intent intent = getIntent();
        userType = intent.getIntExtra("userType", R.string.salesman);

        locationButton = findViewById(R.id.location_button);
        assignerButton = findViewById(R.id.assigner_button);
        assigneeButton = findViewById(R.id.assignee_button);
        loanTypeButton = findViewById(R.id.loan_type_button);
        statusButton = findViewById(R.id.status_button);

        locationScrollView = findViewById(R.id.location_scroll_view);
        assignerScrollView = findViewById(R.id.assigner_scroll_view);
        assigneeScrollView = findViewById(R.id.assignee_scroll_view);
        loanTypeScrollView = findViewById(R.id.loan_type_scroll_view);
        statusScrollView = findViewById(R.id.status_scroll_view);

        locationRadioGroup = findViewById(R.id.location_radio_group);
        assignerRadioGroup = findViewById(R.id.assigner_radio_group);
        assigneeRadioGroup = findViewById(R.id.assignee_radio_group);
        loanTypeRadioGroup = findViewById(R.id.loan_type_radio_group);
        statusRadioGroup = findViewById(R.id.status_radio_group);

        if (userType == R.string.telecaller) {
            assignerButton.setVisibility(View.GONE);

        } else if (userType == R.string.salesman) {
            locationButton.setVisibility(View.GONE);
            assigneeButton.setVisibility(View.GONE);

        } else if (userType == R.string.teleassigner) {
            locationButton.setVisibility(View.GONE);
            assignerButton.setVisibility(View.GONE);
        }

        firestore = new com.mudrahome.mhlms.firebase.Firestore();
        initAllValues();
        showLocationList();

        locationRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton rb = findViewById(i);
            strLocation = rb.getText().toString();

            Log.d("Radio_Button", "" + strLocation);

            assigneeLocationChanged = true;
            assignerLocationChanged = true;
        });

        assignerRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton rb = findViewById(i);
            strAssigner = rb.getText().toString();

            Log.d("Radio_Button", "" + strAssigner);
        });

        assigneeRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton rb = findViewById(i);
            strAssignee = rb.getText().toString();

            Log.d("Radio_Button", "" + strAssignee);
        });

        loanTypeRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton rb = findViewById(i);
            strLoanType = rb.getText().toString();

            Log.d("Radio_Button", "" + strLoanType);
        });

        statusRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton rb = findViewById(i);
            strStatus = rb.getText().toString();

            Log.d("Radio_Button", "" + strStatus);
        });
    }

    public void buttonClicked(View v) {
        switch (v.getId()) {
            case R.id.location_button:
                showLocationList();
                highlightLocationButton();
                break;

            case R.id.assigner_button:
                setAssignerRadioGroup();
                highlightAssignerButton();
                break;

            case R.id.assignee_button:
                setAssigneeRadioGroup();
                highlightAssigneeButton();
                break;

            case R.id.loan_type_button:
                showLoanTypeList();
                highlightLoanTypeButton();
                break;

            case R.id.status_button:
                showStatusList();
                highlightStatusButton();
                break;

            case R.id.clear_filter_button:
                initAllValues();
                break;

            case R.id.filter_button:
                Intent intent = new Intent();
                intent.putExtra("assigner_filter", strAssigner);
                intent.putExtra("assignee_filter", strAssignee);
                intent.putExtra("location_filter", strLocation);
                intent.putExtra("loan_type_filter", strLoanType);
                intent.putExtra("status_filter", strStatus);

                setResult(201, intent);
                finish();
                break;
        }
    }

    private void initAllValues() {
        strLocation = "All";
        strAssigner = "All";
        strAssignee = "All";
        strStatus = "All";
        strLoanType = "All";

        assignerLocationChanged = true;
        assigneeLocationChanged = true;

        setLocationRadioGroup();
        setLoanTypeRadioGroup();
        setStatusRadioGroup();

        if (userType == R.string.salesman) {
            setAssignerRadioGroup();
            highlightAssignerButton();
        } else {
            showLocationList();
            highlightLocationButton();
        }
    }

    private void setLocationRadioGroup() {
        locationRadioGroup.removeAllViews();

        String[] locationArray = getResources().getStringArray(R.array.location_filter);
        for (String locationItem : locationArray) {
            RadioButton rb = new RadioButton(this);
            rb.setPadding(24, 24, 24, 24);
            rb.setText(locationItem);
            locationRadioGroup.addView(rb);
        }
        ((RadioButton) locationRadioGroup.getChildAt(0)).setChecked(true);
    }

    private void setAssignerRadioGroup() {
        if (assignerLocationChanged) {
            showProgressDialog("Loading...", this);
            assignerRadioGroup.removeAllViews();

            firestore.fetchUsersByUserType(new FirestoreInterfaces.OnFetchUsersList() {
                @Override
                public void onListFetched(UserList userList) {
                    RadioButton rb = new RadioButton(FilterActivity.this);
                    rb.setPadding(24, 24, 24, 24);
                    rb.setText("All");
                    assignerRadioGroup.addView(rb);

                    if (userList.getUserList().size() != 0) {
                        for (UserDetails user : userList.getUserList()) {
                            RadioButton rb2 = new RadioButton(FilterActivity.this);
                            rb2.setPadding(24, 24, 24, 24);
                            rb2.setText(user.getUserName());
                            assignerRadioGroup.addView(rb2);
                        }
                    }
                    ((RadioButton) assignerRadioGroup.getChildAt(0)).setChecked(true);

                    assignerLocationChanged = false;
                    showAssignerList();
                    dismissProgressDialog();
                }

                @Override
                public void onFail() {
                    dismissProgressDialog();
                }
            }, strLocation, getString(R.string.telecaller));
        } else
            showAssignerList();
    }

    private void setAssigneeRadioGroup() {
        if (assigneeLocationChanged) {
            showProgressDialog("Loading...", this);
            assigneeRadioGroup.removeAllViews();

            firestore.fetchUsersByUserType(new FirestoreInterfaces.OnFetchUsersList() {
                @Override
                public void onListFetched(UserList userList) {
                    RadioButton rb = new RadioButton(FilterActivity.this);
                    rb.setPadding(24, 24, 24, 24);
                    rb.setText("All");
                    assigneeRadioGroup.addView(rb);

                    if (userList.getUserList().size() != 0) {
                        for (UserDetails user : userList.getUserList()) {
                            RadioButton rb2 = new RadioButton(FilterActivity.this);
                            rb2.setPadding(24, 24, 24, 24);
                            rb2.setText(user.getUserName());
                            assigneeRadioGroup.addView(rb2);
                        }
                    }
                    ((RadioButton) assigneeRadioGroup.getChildAt(0)).setChecked(true);

                    showAssigneeList();
                    assigneeLocationChanged = false;
                    dismissProgressDialog();
                }

                @Override
                public void onFail() {
                    dismissProgressDialog();
                }
            }, strLocation, getString(R.string.salesman));
        } else
            showAssigneeList();
    }

    private void setLoanTypeRadioGroup() {
        loanTypeRadioGroup.removeAllViews();

        String[] locationArray = getResources().getStringArray(R.array.loan_type_filter);
        for (String locationItem : locationArray) {
            RadioButton rb = new RadioButton(this);
            rb.setPadding(24, 24, 24, 24);
            rb.setText(locationItem);
            loanTypeRadioGroup.addView(rb);
        }
        ((RadioButton) loanTypeRadioGroup.getChildAt(0)).setChecked(true);
    }

    private void setStatusRadioGroup() {
        statusRadioGroup.removeAllViews();

        String[] locationArray = getResources().getStringArray(R.array.status_filter);
        for (String locationItem : locationArray) {
            RadioButton rb = new RadioButton(this);
            rb.setPadding(24, 24, 24, 24);
            rb.setText(locationItem);
            statusRadioGroup.addView(rb);
        }
        ((RadioButton) statusRadioGroup.getChildAt(0)).setChecked(true);
    }

    private void showLocationList() {
        showLocationScrollView();
        hideAssignerScrollView();
        hideAssigneeScrollView();
        hideLoanTypeScrollView();
        hideStatusScrollView();
    }

    private void showAssignerList() {
        hideLocationScrollView();
        showAssignerScrollView();
        hideAssigneeScrollView();
        hideLoanTypeScrollView();
        hideStatusScrollView();
    }

    private void showAssigneeList() {
        hideLocationScrollView();
        hideAssignerScrollView();
        showAssigneeScrollView();
        hideLoanTypeScrollView();
        hideStatusScrollView();
    }

    private void showLoanTypeList() {
        hideLocationScrollView();
        hideAssignerScrollView();
        hideAssigneeScrollView();
        showLoanTypeScrollView();
        hideStatusScrollView();
    }

    private void showStatusList() {
        hideLocationScrollView();
        hideAssignerScrollView();
        hideAssigneeScrollView();
        hideLoanTypeScrollView();
        showStatusScrollView();
    }

    private void highlightLocationButton() {
        makeLocationButtonWhite();
        makeAssignerButtonGray();
        makeAssigneeButtonGray();
        makeLoanTypeButtonGray();
        makeStatusButtonGray();
    }

    private void highlightAssignerButton() {
        makeLocationButtonGray();
        makeAssignerButtonWhite();
        makeAssigneeButtonGray();
        makeLoanTypeButtonGray();
        makeStatusButtonGray();
    }

    private void highlightAssigneeButton() {
        makeLocationButtonGray();
        makeAssignerButtonGray();
        makeAssigneeButtonWhite();
        makeLoanTypeButtonGray();
        makeStatusButtonGray();
    }

    private void highlightLoanTypeButton() {
        makeLocationButtonGray();
        makeAssignerButtonGray();
        makeAssigneeButtonGray();
        makeLoanTypeButtonWhite();
        makeStatusButtonGray();
    }

    private void highlightStatusButton() {
        makeLocationButtonGray();
        makeAssignerButtonGray();
        makeAssigneeButtonGray();
        makeLoanTypeButtonGray();
        makeStatusButtonWhite();
    }

    private void makeLocationButtonWhite() {
        locationButton.setBackgroundColor(getResources().getColor(R.color.colorWhite));
    }

    private void makeAssignerButtonWhite() {
        assignerButton.setBackgroundColor(getResources().getColor(R.color.colorWhite));
    }

    private void makeAssigneeButtonWhite() {
        assigneeButton.setBackgroundColor(getResources().getColor(R.color.colorWhite));
    }

    private void makeLoanTypeButtonWhite() {
        loanTypeButton.setBackgroundColor(getResources().getColor(R.color.colorWhite));
    }

    private void makeStatusButtonWhite() {
        statusButton.setBackgroundColor(getResources().getColor(R.color.colorWhite));
    }

    private void makeLocationButtonGray() {
        locationButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
    }

    private void makeAssignerButtonGray() {
        assignerButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
    }

    private void makeAssigneeButtonGray() {
        assigneeButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
    }

    private void makeLoanTypeButtonGray() {
        loanTypeButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
    }

    private void makeStatusButtonGray() {
        statusButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
    }


    private void showLocationScrollView() {
        locationScrollView.setVisibility(View.VISIBLE);
    }

    private void showAssignerScrollView() {
        assignerScrollView.setVisibility(View.VISIBLE);
    }

    private void showAssigneeScrollView() {
        assigneeScrollView.setVisibility(View.VISIBLE);
    }

    private void showLoanTypeScrollView() {
        loanTypeScrollView.setVisibility(View.VISIBLE);
    }

    private void showStatusScrollView() {
        statusScrollView.setVisibility(View.VISIBLE);
    }

    private void hideLocationScrollView() {
        locationScrollView.setVisibility(View.GONE);
    }

    private void hideAssignerScrollView() {
        assignerScrollView.setVisibility(View.GONE);
    }

    private void hideAssigneeScrollView() {
        assigneeScrollView.setVisibility(View.GONE);
    }

    private void hideLoanTypeScrollView() {
        loanTypeScrollView.setVisibility(View.GONE);
    }

    private void hideStatusScrollView() {
        statusScrollView.setVisibility(View.GONE);
    }
}