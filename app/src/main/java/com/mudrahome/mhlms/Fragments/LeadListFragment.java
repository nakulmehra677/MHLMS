package com.mudrahome.mhlms.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.mudrahome.mhlms.Activities.FeedCustomerDetailsActivity;
import com.mudrahome.mhlms.Activities.FilterActivity;
import com.mudrahome.mhlms.Activities.StartOfferActivity;
import com.mudrahome.mhlms.Adapters.LeadsItemAdapter;
import com.mudrahome.mhlms.ExtraViews;
import com.mudrahome.mhlms.Firebase.Firestore;
import com.mudrahome.mhlms.Interfaces.FirestoreInterfaces;
import com.mudrahome.mhlms.models.LeadDetails;
import com.mudrahome.mhlms.models.LeadFilter;
import com.mudrahome.mhlms.models.OfferDetails;
import com.mudrahome.mhlms.R;
import com.mudrahome.mhlms.SharedPreferences.UserDataSharedPreference;

import java.util.ArrayList;
import java.util.List;

public class LeadListFragment extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FloatingActionButton fab;
    private SwipeRefreshLayout mySwipeRefreshLayout;
    private ProgressBar progressBar, firstPageProgressBar;

    private Firestore firestore;

    private List<Object> leadDetailsList = new ArrayList<>();
    LeadFilter leadFilter;
    private LeadsItemAdapter adapter;
    private boolean isSrolling;
    private boolean isLastItemFetched;
    private DocumentSnapshot bottomVisibleItem = null;

    private ExtraViews extraViews;
    private FloatingActionButton filter;
    private int userType;
    private UserDataSharedPreference preferences;

    public LeadListFragment(int userType) {
        this.userType = userType;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(
                R.layout.fragment_lead_list, container, false);
        extraViews = new ExtraViews();

        recyclerView = v.findViewById(R.id.recycler_view);
        fab = v.findViewById(R.id.fab);
        mySwipeRefreshLayout = v.findViewById(R.id.swiperefresh);
        progressBar = v.findViewById(R.id.progressBar);
        firstPageProgressBar = v.findViewById(R.id.first_page_progressBar);
        filter = v.findViewById(R.id.filter1);

        preferences = new UserDataSharedPreference(getContext());
        firestore = new Firestore(getContext());
        leadFilter = new LeadFilter();

        linearLayoutManager = new LinearLayoutManager(getContext());
        filter.setOnClickListener(this);

        adapter = new LeadsItemAdapter(leadDetailsList, getContext(), getString(userType));
        recyclerView.setAdapter(adapter);
        setLayoutByUser();

        new Handler().postDelayed(() -> firstPageProgressBar.setVisibility(View.GONE), 5000);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isSrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(1)) {

                    long firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                    long visibleItemCount = linearLayoutManager.getChildCount();
                    long totalItemCount = linearLayoutManager.getItemCount();

                    if (isSrolling && (firstVisibleItem + visibleItemCount == totalItemCount) && !isLastItemFetched) {
                        isSrolling = false;
                        progressBar.setVisibility(View.VISIBLE);
                        fetchLeads();
                    }
                }
            }
        });

        mySwipeRefreshLayout.setOnRefreshListener(
                () -> {
                    leadDetailsList.clear();
                    adapter.notifyDataSetChanged();
                    isLastItemFetched = false;
                    bottomVisibleItem = null;
                    firstPageProgressBar.setVisibility(View.VISIBLE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            firstPageProgressBar.setVisibility(View.GONE);
                        }
                    }, 5000);
                    //linearLayoutManager = new LinearLayoutManager(LeadListActivity.this);
                    getOffer();
                }
        );

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        return v;
    }

    @SuppressLint("RestrictedApi")
    private void setLayoutByUser() {
        if (userType == R.string.telecaller) {
            fab.setVisibility(View.VISIBLE);
            fab.setImageResource(R.drawable.ic_add_white_24dp);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getContext(), FeedCustomerDetailsActivity.class));
                }
            });
        } else if (userType == R.string.admin) {
            fab.setVisibility(View.VISIBLE);
            fab.setImageResource(R.drawable.megaphone);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isNetworkConnected()) {
                        startActivity(new Intent(getContext(), StartOfferActivity.class));
                    } else
                        extraViews.showToast(R.string.no_internet, getContext());
                }
            });
        }
        leadDetailsList.clear();
        getOffer();
    }

    private void getOffer() {

        firestore.getOffers(new FirestoreInterfaces.FetchOffer() {
                                @Override
                                public void onSuccess(List<OfferDetails> details) {
                                    if (details.size() > 0) {
                                        leadDetailsList.addAll(details);
                                        adapter.notifyDataSetChanged();
                                    }
                                    fetchLeads();
                                }

                                @Override
                                public void onFail() {
                                    fetchLeads();
                                }
                            },
                preferences.getUserName(),
                getString(userType),
                true);
    }

    private void fetchLeads() {
        String s;
        if (userType == R.string.telecaller)
            s = "assigner";
        else if (userType == R.string.salesman)
            s = "assignedTo";
        else
            s = "Admin";

        firestore.downloadLeadList(onFetchLeadList(),
                s, preferences.getUserName(), bottomVisibleItem,
                leadFilter);
    }

    private FirestoreInterfaces.OnFetchLeadList onFetchLeadList() {
        return new FirestoreInterfaces.OnFetchLeadList() {
            @Override
            public void onLeadAdded(List<LeadDetails> l, DocumentSnapshot lastVisible) {
                if (l.size() < 20)
                    isLastItemFetched = true;

                bottomVisibleItem = lastVisible;

                leadDetailsList.addAll(l);
                adapter.notifyDataSetChanged();

                if (mySwipeRefreshLayout.isRefreshing())
                    mySwipeRefreshLayout.setRefreshing(false);

                progressBar.setVisibility(View.GONE);
                firstPageProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailer() {
                extraViews.showToast(R.string.no_internet, getContext());

                //if (progress.isShowing())
                //  progress.dismiss();
                mySwipeRefreshLayout.setRefreshing(false);
                firstPageProgressBar.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2


        if (resultCode == 201) {
            leadFilter.setAssigner(data.getStringExtra("assigner_filter"));
            leadFilter.setAssignee(data.getStringExtra("assignee_filter"));
            leadFilter.setLocation(data.getStringExtra("location_filter"));
            leadFilter.setLoanType(data.getStringExtra("loan_type_filter"));
            leadFilter.setStatus(data.getStringExtra("status_filter"));

            leadDetailsList.clear();
            adapter.notifyDataSetChanged();
            isLastItemFetched = false;
            bottomVisibleItem = null;
            firstPageProgressBar.setVisibility(View.VISIBLE);

            fetchLeads();
        }
    }

    protected boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void onClick(View view) {
        if (isNetworkConnected()) {
            switch (view.getId()) {
                case R.id.filter1:
                    Intent intent = new Intent(getContext(), FilterActivity.class);
                    intent.putExtra("userType", userType);
                    startActivityForResult(intent, 201);
                    break;
            }
        }
    }
}