package com.mudrahome.MHLMS.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.mudrahome.MHLMS.ExtraViews;
import com.mudrahome.MHLMS.Interfaces.Firestore;
import com.mudrahome.MHLMS.Models.OfferDetails;
import com.mudrahome.MHLMS.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NotificationActivity extends BaseActivity {

    private com.mudrahome.MHLMS.Firebase.Firestore firestore;
    private List<Map<String, String>> data;
    private SimpleAdapter adapter;
    private SharedPreferences sharedPreferences;
    private Set<String> currentUserType;
    private String currentUserName;
    private List<String> list = new ArrayList<>();
    private ExtraViews extraViews;
    private ListView listView;
    private List<OfferDetails> offerDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        listView = findViewById(R.id.listView);

        firestore = new com.mudrahome.MHLMS.Firebase.Firestore();
        extraViews = new ExtraViews();

        sharedPreferences = getSharedPreferences(getString(R.string.SH_user_details), Activity.MODE_PRIVATE);

        currentUserType = sharedPreferences.getStringSet(getString(R.string.SH_user_type), Collections.singleton("Salesman"));
        currentUserName = sharedPreferences.getString(getString(R.string.SH_user_name), "");

        extraViews.startProgressDialog("Loading...", this);

        firestore.getOffers(new Firestore.FetchOffer() {
            @Override
            public void onSuccess(List<OfferDetails> details) {

                offerDetails = new ArrayList<>(details);
                data = new ArrayList<Map<String, String>>();

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
        }, currentUserName, currentUserType, false);

        if (currentUserType.equals(getString(R.string.admin))) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                    AlertDialog.Builder adb = new AlertDialog.Builder(NotificationActivity.this);
                    adb.setTitle("Delete?");
                    adb.setMessage("Are you sure you want to delete this ad?");

                    adb.setNegativeButton("Cancel", null);
                    adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (isNetworkConnected()) {
                                firestore.removeAd(new Firestore.OnRemoveAd() {
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
                }
            });
        }
    }
}