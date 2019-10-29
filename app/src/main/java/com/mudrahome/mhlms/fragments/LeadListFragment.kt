package com.mudrahome.mhlms.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.mudrahome.mhlms.ExtraViews
import com.mudrahome.mhlms.R
import com.mudrahome.mhlms.activities.FilterActivity
import com.mudrahome.mhlms.activities.StartOfferActivity
import com.mudrahome.mhlms.activities.UploadLeadActivity
import com.mudrahome.mhlms.adapters.LeadsItemAdapter
import com.mudrahome.mhlms.databinding.FragmentLeadListBinding
import com.mudrahome.mhlms.firebase.Firestore
import com.mudrahome.mhlms.interfaces.FirestoreInterfaces
import com.mudrahome.mhlms.model.LeadDetails
import com.mudrahome.mhlms.model.LeadFilter
import com.mudrahome.mhlms.model.OfferDetails
import com.mudrahome.mhlms.sharedPreferences.UserDataSharedPreference
import java.util.*

class LeadListFragment(private val userType: Int) : Fragment(), View.OnClickListener {

    private var binding: FragmentLeadListBinding? = null
    private var linearLayoutManager: LinearLayoutManager? = null

    private var firestore: Firestore? = null

    private val leadDetailsList = ArrayList<Any>()
    private var leadFilter: LeadFilter? = null
    private var adapter: LeadsItemAdapter? = null
    private var isSrolling: Boolean = false
    private var isLastItemFetched: Boolean = false
    private var bottomVisibleItem: DocumentSnapshot? = null

    private var extraViews: ExtraViews? = null
    private var preferences: UserDataSharedPreference? = null

    private val isNetworkConnected: Boolean
        get() {
            val cm = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return cm.activeNetworkInfo != null
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_lead_list, container, false)

        extraViews = ExtraViews()
        preferences = UserDataSharedPreference(context!!)
        firestore = Firestore(context!!)
        leadFilter = LeadFilter()

        linearLayoutManager = LinearLayoutManager(context)

        binding!!.fab.setOnClickListener(this)
        binding!!.fabFilter.setOnClickListener(this)

        adapter = LeadsItemAdapter(leadDetailsList, context!!, getString(userType))
        binding!!.recyclerView!!.adapter = adapter
        setLayoutByUser()

//        Handler().postDelayed({ firstPageProgressBar!!.visibility = View.GONE }, 5000)

        binding!!.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isSrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val firstVisibleItem = linearLayoutManager!!.findFirstVisibleItemPosition().toLong()
                val visibleItemCount = linearLayoutManager!!.childCount.toLong()
                val totalItemCount = linearLayoutManager!!.itemCount.toLong()

                if (!recyclerView.canScrollVertically(1)) {

                    if (isSrolling && firstVisibleItem + visibleItemCount == totalItemCount && !isLastItemFetched) {
                        isSrolling = false
                        binding!!.progressBar.visibility = View.VISIBLE
                        fetchLeads()
                    }
                }
            }
        })

        binding!!.swipeRefresh.setOnRefreshListener {
            leadDetailsList.clear()
            adapter!!.notifyDataSetChanged()
            isLastItemFetched = false
            bottomVisibleItem = null
            binding!!.firstPageProgressBar.visibility = View.VISIBLE

            getOffer()
        }

        binding!!.recyclerView.layoutManager = linearLayoutManager
        binding!!.recyclerView.setHasFixedSize(true)
        binding!!.recyclerView!!.setItemViewCacheSize(20)

        return binding!!.root
    }

    @SuppressLint("RestrictedApi")
    private fun setLayoutByUser() {
        when (userType) {
            R.string.telecaller -> {
                binding!!.fab.visibility = View.VISIBLE
                binding!!.fab.setImageResource(R.drawable.ic_add_white_24dp)

            }
            R.string.admin -> {
                binding!!.fab.visibility = View.VISIBLE
                binding!!.fab.setImageResource(R.drawable.megaphone)

            }
            R.string.business_associate -> binding!!.fab.visibility = View.VISIBLE
        }
        leadDetailsList.clear()
        getOffer()
    }

    private fun getOffer() {
        firestore!!.getOffers(
            object : FirestoreInterfaces.FetchOffer {
                override fun onSuccess(details: List<OfferDetails>) {
                    if (details.isNotEmpty()) {
                        leadDetailsList.addAll(details)
                        adapter!!.notifyDataSetChanged()
                    }
                    fetchLeads()
                }

                override fun onFail() {
                    fetchLeads()
                }
            },
            preferences!!.userName,
            getString(userType),
            true
        )
    }

    private fun fetchLeads() {
        setFilter()
        firestore!!.downloadLeadList(onFetchLeadList(), bottomVisibleItem, leadFilter!!)
    }

    private fun onFetchLeadList(): FirestoreInterfaces.OnFetchLeadList {
        return object : FirestoreInterfaces.OnFetchLeadList {
            override fun noLeads() {
                binding!!.swipeRefresh.isRefreshing = false
                binding!!.firstPageProgressBar.visibility = View.GONE
                binding!!.progressBar.visibility = View.GONE
                extraViews!!.showToast(R.string.no_leads, context)

            }

            override fun onLeadAdded(l: List<LeadDetails>, lastVisible: DocumentSnapshot) {
                if (l.size < 20)
                    isLastItemFetched = true

                bottomVisibleItem = lastVisible

                leadDetailsList.addAll(l)
                adapter!!.notifyDataSetChanged()

                if (binding!!.swipeRefresh.isRefreshing)
                    binding!!.swipeRefresh.isRefreshing = false

                binding!!.progressBar.visibility = View.GONE
                binding!!.firstPageProgressBar.visibility = View.GONE
            }

            override fun onFail() {
                extraViews!!.showToast(R.string.error_occur, context)

                //if (progress.isShowing())
                //  progress.dismiss();
                binding!!.swipeRefresh.isRefreshing = false
                binding!!.firstPageProgressBar.visibility = View.GONE
                binding!!.progressBar.visibility = View.GONE
            }
        }
    }


    private fun setFilter() {

        when (userType) {
            R.string.telecaller -> leadFilter?.assigner = preferences!!.userName
            R.string.salesman -> leadFilter?.assignee = preferences!!.userName
            R.string.business_associate -> leadFilter?.businessAssociateUId = preferences?.userUid
            R.string.teleassigner -> {
                leadFilter?.assigner = preferences!!.userName
                leadFilter?.businessAssociateUploader = true
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == 201) {
            leadFilter?.assigner = data!!.getStringExtra("assigner_filter")
            leadFilter?.assignee = data.getStringExtra("assignee_filter")
            leadFilter?.location = data.getStringExtra("location_filter")
            leadFilter?.loanType = data.getStringExtra("loan_type_filter")
            leadFilter?.status = data.getStringExtra("status_filter")


            leadDetailsList.clear()
            adapter!!.notifyDataSetChanged()
            isLastItemFetched = false
            bottomVisibleItem = null
            binding!!.firstPageProgressBar.visibility = View.VISIBLE

            fetchLeads()
        }
    }

    override fun onClick(view: View) {
        if (isNetworkConnected) {
            when (view.id) {
                R.id.fab_filter -> {
                    val intent = Intent(context, FilterActivity::class.java)
                    intent.putExtra("userType", userType)
                    startActivityForResult(intent, 201)
                }
                R.id.fab -> {
                    if (userType == R.string.admin)
                        startActivity(Intent(context, StartOfferActivity::class.java))
                    else
                        startActivity(Intent(context, UploadLeadActivity::class.java))
                }
            }
        } else {
            extraViews!!.showToast(R.string.no_internet, context)
        }
    }
}