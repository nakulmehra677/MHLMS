package com.mudrahome.MHLMS.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.mudrahome.MHLMS.ExtraViews;
import com.mudrahome.MHLMS.Firebase.Firestore;
import com.mudrahome.MHLMS.Interfaces.FetchOffer;
import com.mudrahome.MHLMS.Models.OfferDetails;
import com.mudrahome.MHLMS.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationActivity extends AppCompatActivity {

    private Firestore firestore;
    private String[] listItem;
    SimpleAdapter adapter;
    private SharedPreferences sharedPreferences;
    private String currentUserType;
    private String currentUserName;
    private List<String> list = new ArrayList<>();
    private ExtraViews extraViews;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        listView = findViewById(R.id.listView);

        firestore = new Firestore();
        extraViews = new ExtraViews();

        sharedPreferences = getSharedPreferences(getString(R.string.SH_user_details), Activity.MODE_PRIVATE);

        currentUserType = sharedPreferences.getString(getString(R.string.SH_user_type), "Salesman");
        currentUserName = sharedPreferences.getString(getString(R.string.SH_user_name), "");

        extraViews.startProgressDialog("Loading...", this);

        firestore.getOffers(new FetchOffer() {
            @Override
            public void onSuccess(List<OfferDetails> details) {

                List<Map<String, String>> data = new ArrayList<Map<String, String>>();

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
    }
}
