package com.mudrahome.mhlms.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.mudrahome.mhlms.ExtraViews;
import com.mudrahome.mhlms.R;
import com.mudrahome.mhlms.interfaces.FirestoreInterfaces;
import com.mudrahome.mhlms.model.OfferDetails;
import com.mudrahome.mhlms.sharedPreferences.UserDataSharedPreference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NotificationActivity extends BaseActivity {

    private com.mudrahome.mhlms.firebase.Firestore firestore;
    private List<Map<String, String>> data;
    private SimpleAdapter adapter;
    private String currentUserType;
    private String currentUserName;
    private List<String> list = new ArrayList<>();
    private ExtraViews extraViews;
    private ListView listView;
    private List<OfferDetails> offerDetails;
    private UserDataSharedPreference preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        listView = findViewById(R.id.listView);

        firestore = new com.mudrahome.mhlms.firebase.Firestore();
        extraViews = new ExtraViews();

        preference = new UserDataSharedPreference(this);

        currentUserName = preference.getUserName();
        currentUserType = preference.getUserType();

        extraViews.startProgressDialog("Loading...", this);

        String userType;
        if (currentUserType.equals(getString(R.string.admin))) {
            userType = getString(R.string.admin);
        } else if (currentUserType.equals(getString(R.string.telecaller))) {
            userType = getString(R.string.telecaller);
        } else {
            userType = getString(R.string.salesman);
        }

        firestore.getOffers(new FirestoreInterfaces.FetchOffer() {
            @Override
            public void onSuccess(List<OfferDetails> details) {

                offerDetails = new ArrayList<>(details);
                data = new ArrayList<>();

                for (OfferDetails offerDetails : details) {
                    list.add(offerDetails.getDescription());
                    Map<String, String> item = new HashMap<String, String>(2);
                    item.put("title", offerDetails.getTitle());
                    item.put("subtitle", offerDetails.getDescription());
                    data.add(item);
                }

                adapter = new SimpleAdapter(NotificationActivity.this,
                        data,
                        android.R.layout.simple_list_item_2,
                        new String[]{"title", "subtitle"},
                        new int[]{android.R.id.text1,
                                android.R.id.text2});

                listView.setAdapter(adapter);
                extraViews.dismissProgressDialog();
            }

            @Override
            public void onFail() {
                extraViews.dismissProgressDialog();
                Toast.makeText(NotificationActivity.this, "An error occured.", Toast.LENGTH_SHORT).show();
            }
        }, currentUserName, userType, false);

        if (currentUserType.contains(getString(R.string.admin))) {
            listView.setOnItemClickListener((adapterView, view, i, l) -> {

                AlertDialog.Builder adb = new AlertDialog.Builder(NotificationActivity.this);
                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to delete this ad?");

                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (isNetworkConnected()) {
                            firestore.removeAd(new FirestoreInterfaces.OnRemoveAd() {
                                @Override
                                public void onSuccess() {
                                    data.remove(i);
                                    adapter.notifyDataSetChanged();
                                    showToastMessage(R.string.deleted);
                                }

                                @Override
                                public void onFail() {
                                    showToastMessage(R.string.error_occur);
                                }
                            }, offerDetails.get(i));
                        } else
                            showToastMessage(R.string.no_internet);
                    }
                });
                adb.show();
            });
        }
    }
}