package com.mudrahome.MHLMS.Fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.Timestamp;
import com.mudrahome.MHLMS.ExtraViews;
import com.mudrahome.MHLMS.Interfaces.Firestore;
import com.mudrahome.MHLMS.Models.OfferDetails;
import com.mudrahome.MHLMS.Models.UserDetails;
import com.mudrahome.MHLMS.Models.UserList;
import com.mudrahome.MHLMS.R;

import java.util.ArrayList;
import java.util.List;

import static com.mudrahome.MHLMS.Fragments.FeedOfferDetailsFragment.strOfferDescription;
import static com.mudrahome.MHLMS.Fragments.FeedOfferDetailsFragment.strOfferTitle;

public class SelectUserForOfferFragment extends Fragment implements View.OnClickListener {

    private ViewGroup v;
    private Button delhiButton;
    private Button indoreButton;
    private Button jaipurButton;
    private Button gwaliorButton;
    private Button ahmedabadButton;
    private Button telecallerButton;
    private Button salesmanButton;

    private boolean delhiButtonActive = true;
    private boolean indoreButtonActive = false;
    private boolean jaipurButtonActive = false;
    private boolean gwaliorButtonActive = false;
    private boolean ahmedabadButtonActive = false;
    private boolean telecallerButtonActive = true;
    private boolean salesmanButtonActive = false;

    private ScrollView delhiTelecallerScrollView;
    private ScrollView indoreTelecallerScrollView;
    private ScrollView jaipurTelecallerScrollView;
    private ScrollView gwaliorTelecallerScrollView;
    private ScrollView ahmedabadTelecallerScrollView;
    private ScrollView delhiSalesmanScrollView;
    private ScrollView indoreSalesmanScrollView;
    private ScrollView jaipurSalesmanScrollView;
    private ScrollView gwaliorSalesmanScrollView;
    private ScrollView ahmedabadSalesmanScrollView;

    private LinearLayout delhiTelecallerLayout;
    private LinearLayout indoreTelecallerLayout;
    private LinearLayout jaipurTelecallerLayout;
    private LinearLayout gwaliorTelecallerLayout;
    private LinearLayout ahmedabadTelecallerLayout;
    private LinearLayout delhiSalesmanLayout;
    private LinearLayout indoreSalesmanLayout;
    private LinearLayout jaipurSalesmanLayout;
    private LinearLayout gwaliorSalesmanLayout;
    private LinearLayout ahmedabadSalesmanLayout;

    private LinearLayout selectUsersLayout;
    private Switch allCallerSwitch;

    private Button startOffer;
    private Button editOfferDetails;

    private List<String> userNames = new ArrayList<>();
    private List<String> allCallers = new ArrayList<>();
    private OfferDetails details;
    private com.mudrahome.MHLMS.Firebase.Firestore firestore;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = (ViewGroup) inflater.inflate(
                R.layout.fragment_select_user_for_offer, container, false);

        delhiButton = v.findViewById(R.id.delhi_button);
        indoreButton = v.findViewById(R.id.indore_button);
        jaipurButton = v.findViewById(R.id.jaipur_button);
        gwaliorButton = v.findViewById(R.id.gwalior_button);
        ahmedabadButton = v.findViewById(R.id.ahmedabad_button);
        telecallerButton = v.findViewById(R.id.telecaller_button);
        salesmanButton = v.findViewById(R.id.salesman_button);

        delhiButton.setOnClickListener(this);
        indoreButton.setOnClickListener(this);
        jaipurButton.setOnClickListener(this);
        gwaliorButton.setOnClickListener(this);
        ahmedabadButton.setOnClickListener(this);
        telecallerButton.setOnClickListener(this);
        salesmanButton.setOnClickListener(this);

        delhiTelecallerScrollView = v.findViewById(R.id.delhi_telecaller_scroll_view);
        indoreTelecallerScrollView = v.findViewById(R.id.indore_telecaller_scroll_view);
        jaipurTelecallerScrollView = v.findViewById(R.id.jaipur_telecaller_scroll_view);
        gwaliorTelecallerScrollView = v.findViewById(R.id.gwalior_telecaller_scroll_view);
        ahmedabadTelecallerScrollView = v.findViewById(R.id.ahmedabad_telecaller_scroll_view);
        delhiSalesmanScrollView = v.findViewById(R.id.delhi_salesmen_scroll_view);
        indoreSalesmanScrollView = v.findViewById(R.id.indore_salesman_scroll_view);
        jaipurSalesmanScrollView = v.findViewById(R.id.jaipur_salesman_scroll_view);
        gwaliorSalesmanScrollView = v.findViewById(R.id.gwalior_salesman_scroll_view);
        ahmedabadSalesmanScrollView = v.findViewById(R.id.ahmedabad_salesman_scroll_view);

        delhiTelecallerLayout = v.findViewById(R.id.delhi_telecaller_layout);
        indoreTelecallerLayout = v.findViewById(R.id.indore_telecaller_layout);
        jaipurTelecallerLayout = v.findViewById(R.id.jaipur_telecaller_layout);
        gwaliorTelecallerLayout = v.findViewById(R.id.gwalior_telecaller_layout);
        ahmedabadTelecallerLayout = v.findViewById(R.id.ahmedabad_telecaller_layout);
        delhiSalesmanLayout = v.findViewById(R.id.delhi_salesman_layout);
        indoreSalesmanLayout = v.findViewById(R.id.indore_salesman_layout);
        jaipurSalesmanLayout = v.findViewById(R.id.jaipur_salesman_layout);
        gwaliorSalesmanLayout = v.findViewById(R.id.gwalior_salesman_layout);
        ahmedabadSalesmanLayout = v.findViewById(R.id.ahmedabad_salesman_layout);

        selectUsersLayout = v.findViewById(R.id.select_user_layout);
        allCallerSwitch = v.findViewById(R.id.switch1);

        startOffer = v.findViewById(R.id.start_offer_button);
        editOfferDetails = v.findViewById(R.id.edit_offer_details_button);

        firestore = new com.mudrahome.MHLMS.Firebase.Firestore();

        showDelhiTelecallerScrollView();

        editOfferDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View pager = getActivity().findViewById(R.id.pager);
                ((ViewPager) pager).setCurrentItem(0);
            }
        });

        allCallerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    selectUsersLayout.setVisibility(View.INVISIBLE);
                    if (allCallers.size() == 0) {
                        extraViews.startProgressDialog("Loading...", getContext());
                        firestore.fetchUsersByUserType(new Firestore.OnFetchUsersList() {
                            @Override
                            public void onListFetched(UserList userList) {
                                for (UserDetails details : userList.getUserList()) {
                                    allCallers.add((details.getUserName()));
                                }
                                extraViews.dismissProgressDialog();
                            }
                        }, "All", getString(R.string.telecaller));
                    }
                } else {
                    selectUsersLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        startOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userNames.size() != 0 || allCallers.size() != 0) {
                    if (allCallerSwitch.isEnabled())
                        details = new OfferDetails(strOfferTitle,
                                strOfferDescription, allCallers, Timestamp.now());
                    else
                        details = new OfferDetails(strOfferTitle,
                                strOfferDescription, userNames, Timestamp.now());


                    if (isNetworkConnected()) {
                        final ExtraViews extraViews = new ExtraViews();
                        extraViews.startProgressDialog("Loading...", getContext());
                        firestore.startOffer(new Firestore.OnUploadOffer() {
                            @Override
                            public void onSuccess() {
                                extraViews.dismissProgressDialog();
                                Toast.makeText(getContext(), getString(R.string.uploaded), Toast.LENGTH_SHORT).show();
                                getActivity().finish();
                            }

                            @Override
                            public void onFail() {
                                Toast.makeText(getContext(), getString(R.string.failed_to_upload), Toast.LENGTH_SHORT).show();
                            }
                        }, details);
                    } else {
                        Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(getContext(), "Select Users", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void onClick(View v) {
        if (isNetworkConnected()) {

            switch (v.getId()) {
                case R.id.telecaller_button:
                    if (salesmanButtonActive) {

                        if (delhiButtonActive) {
                            showDelhiTelecallerScrollView();
                            hideDelhiSalesmanScrollView();
                        } else if (indoreButtonActive) {
                            showIndoreTelecallerScrollView();
                            hideIndoreSalesmanScrollView();
                        } else if (jaipurButtonActive) {
                            showJaipurTelecallerScrollView();
                            hideJaipurSalesmanScrollView();
                        } else if (gwaliorButtonActive) {
                            showGwaliorTelecallerScrollView();
                            hideGwaliorSalesmanScrollView();
                        } else {
                            showAhmedabadTelecallerScrollView();
                            hideAhmedabadSalesmanScrollView();
                        }
                    }
                    setTelecallerButtonWhite();
                    setSalesmanButtonGray();
                    break;

                case R.id.salesman_button:
                    if (telecallerButtonActive) {

                        if (delhiButtonActive) {
                            showDelhiSalesmanScrollView();
                            hideDelhiTelecallerScrollView();
                        } else if (indoreButtonActive) {
                            hideIndoreTelecallerScrollView();
                            showIndoreSalesmanScrollView();
                        } else if (jaipurButtonActive) {
                            hideJaipurTelecallerScrollView();
                            showJaipurSalesmanScrollView();
                        } else if (gwaliorButtonActive) {
                            hideGwaliorTelecallerScrollView();
                            showGwaliorSalesmanScrollView();
                        } else {
                            showAhmedabadSalesmanScrollView();
                            hideAhmedabadTelecallerScrollView();
                        }
                        setTelecallerButtonGray();
                        setSalesmanButtonWhite();
                    }
                    break;

                case R.id.delhi_button:
                    if (indoreButtonActive) {
                        if (telecallerButtonActive) {
                            hideIndoreTelecallerScrollView();
                            showDelhiTelecallerScrollView();
                        } else {
                            hideIndoreSalesmanScrollView();
                            showDelhiSalesmanScrollView();
                        }
                        setDelhiButtonWhite();
                        setIndoreButtonGray();
                    } else if (jaipurButtonActive) {
                        if (telecallerButtonActive) {
                            hideJaipurTelecallerScrollView();
                            showDelhiTelecallerScrollView();
                        } else {
                            hideJaipurSalesmanScrollView();
                            showDelhiSalesmanScrollView();
                        }
                        setDelhiButtonWhite();
                        setJaipurButtonGray();
                    } else if (gwaliorButtonActive) {
                        if (telecallerButtonActive) {
                            hideGwaliorTelecallerScrollView();
                            showDelhiTelecallerScrollView();
                        } else {
                            hideGwaliorSalesmanScrollView();
                            showDelhiSalesmanScrollView();
                        }
                        setDelhiButtonWhite();
                        setGwaliorButtonGray();
                    } else if (ahmedabadButtonActive) {
                        if (telecallerButtonActive) {
                            hideAhmedabadTelecallerScrollView();
                            showDelhiTelecallerScrollView();
                        } else {
                            hideAhmedabadSalesmanScrollView();
                            showDelhiSalesmanScrollView();
                        }
                        setDelhiButtonWhite();
                        setAhmedabadButtonGray();
                    }
                    break;

                case R.id.indore_button:
                    if (delhiButtonActive) {
                        if (telecallerButtonActive) {
                            hideDelhiTelecallerScrollView();
                            showIndoreTelecallerScrollView();
                        } else {
                            hideDelhiSalesmanScrollView();
                            showIndoreSalesmanScrollView();
                        }
                        setIndoreButtonWhite();
                        setDelhiButtonGray();
                    } else if (jaipurButtonActive) {
                        if (telecallerButtonActive) {
                            hideJaipurTelecallerScrollView();
                            showIndoreTelecallerScrollView();
                        } else {
                            hideJaipurSalesmanScrollView();
                            showIndoreSalesmanScrollView();
                        }
                        setIndoreButtonWhite();
                        setJaipurButtonGray();
                    } else if (gwaliorButtonActive) {
                        if (telecallerButtonActive) {
                            hideGwaliorTelecallerScrollView();
                            showIndoreTelecallerScrollView();
                        } else {
                            hideGwaliorSalesmanScrollView();
                            showIndoreSalesmanScrollView();
                        }
                        setIndoreButtonWhite();
                        setGwaliorButtonGray();
                    } else if (ahmedabadButtonActive) {
                        if (telecallerButtonActive) {
                            hideAhmedabadTelecallerScrollView();
                            showIndoreTelecallerScrollView();
                        } else {
                            hideAhmedabadSalesmanScrollView();
                            showIndoreSalesmanScrollView();
                        }
                        setIndoreButtonWhite();
                        setAhmedabadButtonGray();
                    }
                    break;

                case R.id.jaipur_button:
                    if (indoreButtonActive) {
                        if (telecallerButtonActive) {
                            hideIndoreTelecallerScrollView();
                            showJaipurTelecallerScrollView();
                        } else {
                            hideIndoreSalesmanScrollView();
                            showJaipurSalesmanScrollView();
                        }
                        setJaipurButtonWhite();
                        setIndoreButtonGray();
                    } else if (delhiButtonActive) {
                        if (telecallerButtonActive) {
                            hideDelhiTelecallerScrollView();
                            showJaipurTelecallerScrollView();
                        } else {
                            hideDelhiSalesmanScrollView();
                            showJaipurSalesmanScrollView();
                        }
                        setJaipurButtonWhite();
                        setDelhiButtonGray();
                    } else if (gwaliorButtonActive) {
                        if (telecallerButtonActive) {
                            hideGwaliorTelecallerScrollView();
                            showJaipurTelecallerScrollView();
                        } else {
                            hideGwaliorSalesmanScrollView();
                            showJaipurSalesmanScrollView();
                        }
                        setJaipurButtonWhite();
                        setGwaliorButtonGray();
                    } else if (ahmedabadButtonActive) {
                        if (telecallerButtonActive) {
                            hideAhmedabadTelecallerScrollView();
                            showJaipurTelecallerScrollView();
                        } else {
                            hideAhmedabadSalesmanScrollView();
                            showJaipurSalesmanScrollView();
                        }
                        setJaipurButtonWhite();
                        setAhmedabadButtonGray();
                    }
                    break;

                case R.id.gwalior_button:
                    if (indoreButtonActive) {
                        if (telecallerButtonActive) {
                            hideIndoreTelecallerScrollView();
                            showGwaliorTelecallerScrollView();
                        } else {
                            hideIndoreSalesmanScrollView();
                            showGwaliorSalesmanScrollView();
                        }
                        setGwaliorButtonWhite();
                        setIndoreButtonGray();
                    } else if (jaipurButtonActive) {
                        if (telecallerButtonActive) {
                            hideJaipurTelecallerScrollView();
                            showGwaliorTelecallerScrollView();
                        } else {
                            hideJaipurSalesmanScrollView();
                            showGwaliorSalesmanScrollView();
                        }
                        setGwaliorButtonWhite();
                        setJaipurButtonGray();
                    } else if (delhiButtonActive) {
                        if (telecallerButtonActive) {
                            hideDelhiTelecallerScrollView();
                            showGwaliorTelecallerScrollView();
                        } else {
                            showGwaliorSalesmanScrollView();
                            hideDelhiSalesmanScrollView();
                        }
                        setGwaliorButtonWhite();
                        setDelhiButtonGray();
                    } else if (ahmedabadButtonActive) {
                        if (telecallerButtonActive) {
                            hideAhmedabadTelecallerScrollView();
                            showGwaliorTelecallerScrollView();
                        } else {
                            hideAhmedabadSalesmanScrollView();
                            showGwaliorSalesmanScrollView();
                        }
                        setGwaliorButtonWhite();
                        setAhmedabadButtonGray();
                    }
                    break;

                case R.id.ahmedabad_button:
                    if (indoreButtonActive) {
                        if (telecallerButtonActive) {
                            hideIndoreTelecallerScrollView();
                            showAhmedabadTelecallerScrollView();
                        } else {
                            hideIndoreSalesmanScrollView();
                            showAhmedabadSalesmanScrollView();
                        }
                        setAhmedabadButtonWhite();
                        setIndoreButtonGray();
                    } else if (jaipurButtonActive) {
                        if (telecallerButtonActive) {
                            hideJaipurTelecallerScrollView();
                            showAhmedabadTelecallerScrollView();
                        } else {
                            hideJaipurSalesmanScrollView();
                            showAhmedabadSalesmanScrollView();
                        }
                        setAhmedabadButtonWhite();
                        setJaipurButtonGray();
                    } else if (gwaliorButtonActive) {
                        if (telecallerButtonActive) {
                            hideGwaliorTelecallerScrollView();
                            showAhmedabadTelecallerScrollView();
                        } else {
                            hideGwaliorSalesmanScrollView();
                            showAhmedabadSalesmanScrollView();
                        }
                        setAhmedabadButtonWhite();
                        setGwaliorButtonGray();
                    } else if (delhiButtonActive) {
                        if (telecallerButtonActive) {
                            showAhmedabadTelecallerScrollView();
                            hideDelhiTelecallerScrollView();
                        } else {
                            showAhmedabadSalesmanScrollView();
                            hideDelhiSalesmanScrollView();
                        }
                        setAhmedabadButtonWhite();
                        setDelhiButtonGray();
                    }
                    break;
            }
        } else
            Toast.makeText(getContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
    }

    private void setDelhiButtonWhite() {
        delhiButton.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        delhiButtonActive = true;
    }

    private void setJaipurButtonWhite() {
        jaipurButton.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        jaipurButtonActive = true;
    }

    private void setIndoreButtonWhite() {
        indoreButton.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        indoreButtonActive = true;
    }

    private void setGwaliorButtonWhite() {
        gwaliorButton.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        gwaliorButtonActive = true;
    }

    private void setAhmedabadButtonWhite() {
        ahmedabadButton.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        ahmedabadButtonActive = true;
    }

    private void setSalesmanButtonWhite() {
        salesmanButton.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        salesmanButtonActive = true;
    }

    private void setTelecallerButtonWhite() {
        telecallerButton.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        telecallerButtonActive = true;
    }

    private void setDelhiButtonGray() {
        delhiButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
        delhiButtonActive = false;
    }

    private void setIndoreButtonGray() {
        indoreButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
        indoreButtonActive = false;
    }

    private void setJaipurButtonGray() {
        jaipurButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
        jaipurButtonActive = false;
    }

    private void setGwaliorButtonGray() {
        gwaliorButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
        gwaliorButtonActive = false;
    }

    private void setAhmedabadButtonGray() {
        ahmedabadButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
        ahmedabadButtonActive = false;
    }

    private void setTelecallerButtonGray() {
        telecallerButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
        telecallerButtonActive = false;
    }

    private void setSalesmanButtonGray() {
        salesmanButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
        salesmanButtonActive = false;
    }

    ExtraViews extraViews = new ExtraViews();

    private CheckBox addAllCheckBox(String text) {
        CheckBox checkBox = new CheckBox(getContext());
        checkBox.setPadding(24, 24, 24, 24);
        checkBox.setText(text);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = ((View) view.getParent()).getId();
                LinearLayout linearLayout = v.findViewById(id);

                if (((CheckBox) view).getText().equals("All")) {

                    if (((CheckBox) view).isChecked()) {
                        for (int i = 1; i < linearLayout.getChildCount(); i++) {
                            View childView = linearLayout.getChildAt(i);
                            if (!((CheckBox) childView).isChecked()) {
                                ((CheckBox) childView).setChecked(true);
                                userNames.add((String) ((CheckBox) childView).getText());
                            }
                        }
                    } else {
                        for (int i = 1; i < linearLayout.getChildCount(); i++) {
                            View childView = linearLayout.getChildAt(i);
                            ((CheckBox) childView).setChecked(false);
                            userNames.remove(((CheckBox) childView).getText());
                        }
                    }
                } else {
                    boolean flag = false;
                    if (((CheckBox) view).isChecked()) {
                        for (int i = 1; i < linearLayout.getChildCount(); i++) {
                            View childView = linearLayout.getChildAt(i);
                            if (!((CheckBox) childView).isChecked()) {
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            View childView = linearLayout.getChildAt(0);
                            ((CheckBox) childView).setChecked(true);
                        }
                        userNames.add((String) ((CheckBox) view).getText());
                    } else {
                        View childView = linearLayout.getChildAt(0);
                        if (((CheckBox) childView).isChecked()) {
                            ((CheckBox) childView).setChecked(false);
                        }
                        userNames.remove(((CheckBox) view).getText());
                    }
                }
                Log.d("UserNames", String.valueOf(userNames));
            }
        });
        return checkBox;
    }

    private void showDelhiTelecallerScrollView() {
        if (delhiTelecallerLayout.getChildCount() == 0) {
            extraViews.startProgressDialog("Loading...", getContext());

            firestore.fetchUsersByUserType(new Firestore.OnFetchUsersList() {
                @Override
                public void onListFetched(UserList userList) {

                    if (userList.getUserList().size() != 0) {
                        CheckBox allCheckBox = addAllCheckBox("All");
                        delhiTelecallerLayout.addView(allCheckBox);

                        for (UserDetails user : userList.getUserList()) {
                            CheckBox checkBox = addAllCheckBox(user.getUserName());
                            delhiTelecallerLayout.addView(checkBox);
                        }
                    }
                    extraViews.dismissProgressDialog();
                    delhiTelecallerScrollView.setVisibility(View.VISIBLE);
                }
            }, getString(R.string.delhi), getString(R.string.telecaller));

        } else
            delhiTelecallerScrollView.setVisibility(View.VISIBLE);
    }

    private void showIndoreTelecallerScrollView() {
        if (indoreTelecallerLayout.getChildCount() == 0) {
            extraViews.startProgressDialog("Loading...", getContext());

            firestore.fetchUsersByUserType(new Firestore.OnFetchUsersList() {
                @Override
                public void onListFetched(UserList userList) {

                    if (userList.getUserList().size() != 0) {
                        CheckBox allCheckBox = addAllCheckBox("All");
                        indoreTelecallerLayout.addView(allCheckBox);

                        for (UserDetails user : userList.getUserList()) {
                            CheckBox checkBox = addAllCheckBox(user.getUserName());
                            indoreTelecallerLayout.addView(checkBox);
                        }
                    }
                    extraViews.dismissProgressDialog();
                    indoreTelecallerScrollView.setVisibility(View.VISIBLE);
                }
            }, getString(R.string.indore), getString(R.string.telecaller));

        } else
            indoreTelecallerScrollView.setVisibility(View.VISIBLE);
    }

    private void showJaipurTelecallerScrollView() {
        if (jaipurTelecallerLayout.getChildCount() == 0) {
            extraViews.startProgressDialog("Loading...", getContext());

            firestore.fetchUsersByUserType(new Firestore.OnFetchUsersList() {
                @Override
                public void onListFetched(UserList userList) {

                    if (userList.getUserList().size() != 0) {
                        CheckBox allCheckBox = addAllCheckBox("All");
                        jaipurTelecallerLayout.addView(allCheckBox);

                        for (UserDetails user : userList.getUserList()) {
                            CheckBox checkBox = addAllCheckBox(user.getUserName());
                            jaipurTelecallerLayout.addView(checkBox);
                        }
                    }
                    extraViews.dismissProgressDialog();
                    jaipurTelecallerScrollView.setVisibility(View.VISIBLE);
                }
            }, getString(R.string.jaipur), getString(R.string.telecaller));

        } else
            jaipurTelecallerScrollView.setVisibility(View.VISIBLE);
    }

    private void showGwaliorTelecallerScrollView() {
        if (gwaliorTelecallerLayout.getChildCount() == 0) {
            extraViews.startProgressDialog("Loading...", getContext());

            firestore.fetchUsersByUserType(new Firestore.OnFetchUsersList() {
                @Override
                public void onListFetched(UserList userList) {

                    if (userList.getUserList().size() != 0) {
                        CheckBox allCheckBox = addAllCheckBox("All");
                        gwaliorTelecallerLayout.addView(allCheckBox);

                        for (UserDetails user : userList.getUserList()) {
                            CheckBox checkBox = addAllCheckBox(user.getUserName());
                            gwaliorTelecallerLayout.addView(checkBox);
                        }
                    }
                    extraViews.dismissProgressDialog();
                    gwaliorTelecallerScrollView.setVisibility(View.VISIBLE);
                }
            }, getString(R.string.gwalior), getString(R.string.telecaller));

        } else
            gwaliorTelecallerScrollView.setVisibility(View.VISIBLE);
    }

    private void showAhmedabadTelecallerScrollView() {
        if (ahmedabadTelecallerLayout.getChildCount() == 0) {
            extraViews.startProgressDialog("Loading...", getContext());

            firestore.fetchUsersByUserType(new Firestore.OnFetchUsersList() {
                @Override
                public void onListFetched(UserList userList) {

                    if (userList.getUserList().size() != 0) {
                        CheckBox allCheckBox = addAllCheckBox("All");
                        ahmedabadTelecallerLayout.addView(allCheckBox);

                        for (UserDetails user : userList.getUserList()) {
                            CheckBox checkBox = addAllCheckBox(user.getUserName());
                            ahmedabadTelecallerLayout.addView(checkBox);
                        }
                    }
                    extraViews.dismissProgressDialog();
                    ahmedabadTelecallerScrollView.setVisibility(View.VISIBLE);
                }
            }, getString(R.string.ahmedabad), getString(R.string.telecaller));

        } else
            ahmedabadTelecallerScrollView.setVisibility(View.VISIBLE);
    }

    private void showDelhiSalesmanScrollView() {
        if (delhiSalesmanLayout.getChildCount() == 0) {
            extraViews.startProgressDialog("Loading...", getContext());

            firestore.fetchUsersByUserType(new Firestore.OnFetchUsersList() {
                @Override
                public void onListFetched(UserList userList) {

                    if (userList.getUserList().size() != 0) {
                        CheckBox allCheckBox = addAllCheckBox("All");
                        delhiSalesmanLayout.addView(allCheckBox);

                        for (UserDetails user : userList.getUserList()) {
                            CheckBox checkBox = addAllCheckBox(user.getUserName());
                            delhiSalesmanLayout.addView(checkBox);
                        }
                    }
                    extraViews.dismissProgressDialog();
                    delhiSalesmanScrollView.setVisibility(View.VISIBLE);
                }
            }, getString(R.string.delhi), getString(R.string.salesman));

        } else
            delhiSalesmanScrollView.setVisibility(View.VISIBLE);
    }

    private void showIndoreSalesmanScrollView() {
        if (indoreSalesmanLayout.getChildCount() == 0) {
            extraViews.startProgressDialog("Loading...", getContext());

            firestore.fetchUsersByUserType(new Firestore.OnFetchUsersList() {
                @Override
                public void onListFetched(UserList userList) {

                    if (userList.getUserList().size() != 0) {
                        CheckBox allCheckBox = addAllCheckBox("All");
                        indoreSalesmanLayout.addView(allCheckBox);

                        for (UserDetails user : userList.getUserList()) {
                            CheckBox checkBox = addAllCheckBox(user.getUserName());
                            indoreSalesmanLayout.addView(checkBox);
                        }
                    }
                    extraViews.dismissProgressDialog();
                    indoreSalesmanScrollView.setVisibility(View.VISIBLE);
                }
            }, getString(R.string.indore), getString(R.string.salesman));

        } else
            indoreSalesmanScrollView.setVisibility(View.VISIBLE);
    }

    private void showJaipurSalesmanScrollView() {
        if (jaipurSalesmanLayout.getChildCount() == 0) {
            extraViews.startProgressDialog("Loading...", getContext());

            firestore.fetchUsersByUserType(new Firestore.OnFetchUsersList() {
                @Override
                public void onListFetched(UserList userList) {

                    if (userList.getUserList().size() != 0) {
                        CheckBox allCheckBox = addAllCheckBox("All");
                        jaipurSalesmanLayout.addView(allCheckBox);

                        for (UserDetails user : userList.getUserList()) {
                            CheckBox checkBox = addAllCheckBox(user.getUserName());
                            jaipurSalesmanLayout.addView(checkBox);
                        }
                    }
                    extraViews.dismissProgressDialog();
                    jaipurSalesmanScrollView.setVisibility(View.VISIBLE);
                }
            }, getString(R.string.jaipur), getString(R.string.salesman));

        } else
            jaipurSalesmanScrollView.setVisibility(View.VISIBLE);
    }

    private void showGwaliorSalesmanScrollView() {
        if (gwaliorSalesmanLayout.getChildCount() == 0) {
            extraViews.startProgressDialog("Loading...", getContext());

            firestore.fetchUsersByUserType(new Firestore.OnFetchUsersList() {
                @Override
                public void onListFetched(UserList userList) {

                    if (userList.getUserList().size() != 0) {
                        CheckBox allCheckBox = addAllCheckBox("All");
                        gwaliorSalesmanLayout.addView(allCheckBox);

                        for (UserDetails user : userList.getUserList()) {
                            CheckBox checkBox = addAllCheckBox(user.getUserName());
                            gwaliorSalesmanLayout.addView(checkBox);
                        }
                    }
                    extraViews.dismissProgressDialog();
                    gwaliorSalesmanScrollView.setVisibility(View.VISIBLE);
                }
            }, getString(R.string.gwalior), getString(R.string.salesman));

        } else
            gwaliorSalesmanScrollView.setVisibility(View.VISIBLE);
    }

    private void showAhmedabadSalesmanScrollView() {
        if (ahmedabadSalesmanLayout.getChildCount() == 0) {
            extraViews.startProgressDialog("Loading...", getContext());

            firestore.fetchUsersByUserType(new Firestore.OnFetchUsersList() {
                @Override
                public void onListFetched(UserList userList) {

                    if (userList.getUserList().size() != 0) {
                        CheckBox allCheckBox = addAllCheckBox("All");
                        ahmedabadSalesmanLayout.addView(allCheckBox);

                        for (UserDetails user : userList.getUserList()) {
                            CheckBox checkBox = addAllCheckBox(user.getUserName());
                            ahmedabadSalesmanLayout.addView(checkBox);
                        }
                    }
                    extraViews.dismissProgressDialog();
                    ahmedabadSalesmanScrollView.setVisibility(View.VISIBLE);
                }
            }, getString(R.string.ahmedabad), getString(R.string.salesman));

        } else
            ahmedabadSalesmanScrollView.setVisibility(View.VISIBLE);
    }

    private void hideDelhiTelecallerScrollView() {
        delhiTelecallerScrollView.setVisibility(View.GONE);
    }

    private void hideIndoreTelecallerScrollView() {
        indoreTelecallerScrollView.setVisibility(View.GONE);
    }

    private void hideJaipurTelecallerScrollView() {
        jaipurTelecallerScrollView.setVisibility(View.GONE);
    }

    private void hideGwaliorTelecallerScrollView() {
        gwaliorTelecallerScrollView.setVisibility(View.GONE);
    }

    private void hideAhmedabadTelecallerScrollView() {
        ahmedabadTelecallerScrollView.setVisibility(View.GONE);
    }

    private void hideDelhiSalesmanScrollView() {
        delhiSalesmanScrollView.setVisibility(View.GONE);
    }

    private void hideIndoreSalesmanScrollView() {
        indoreSalesmanScrollView.setVisibility(View.GONE);
    }

    private void hideJaipurSalesmanScrollView() {
        jaipurSalesmanScrollView.setVisibility(View.GONE);
    }

    private void hideGwaliorSalesmanScrollView() {
        gwaliorSalesmanScrollView.setVisibility(View.GONE);
    }

    private void hideAhmedabadSalesmanScrollView() {
        ahmedabadSalesmanScrollView.setVisibility(View.GONE);
    }
}