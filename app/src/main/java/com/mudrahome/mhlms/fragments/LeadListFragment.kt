package com.mudrahome.mhlms.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.mudrahome.mhlms.ExtraViews
import com.mudrahome.mhlms.R
import com.mudrahome.mhlms.activities.FilterActivity
import com.mudrahome.mhlms.activities.StartOfferActivity
import com.mudrahome.mhlms.adapters.LeadsItemAdapter
import com.mudrahome.mhlms.databinding.FragmentLeadListBinding
import com.mudrahome.mhlms.firebase.Firestore
import com.mudrahome.mhlms.interfaces.FirestoreInterfaces
import com.mudrahome.mhlms.managers.ProfileManager
import com.mudrahome.mhlms.model.LeadDetails
import com.mudrahome.mhlms.model.LeadFilter
import java.util.*

class LeadListFragment(private val userType: Int) : Fragment(), View.OnClickListener {

    private var binding: FragmentLeadListBinding? = null
    private var linearLayoutManager: LinearLayoutManager? = null

    private var firestore: Firestore? = null
    private var manager: ProfileManager? = null


    private val leadDetailsList = ArrayList<Any>()
    private var leadFilter: LeadFilter? = null
    private var adapter: LeadsItemAdapter? = null
    private var isSrolling: Boolean = false
    private var isLastItemFetched: Boolean = false
    private var bottomVisibleItem: DocumentSnapshot? = null

    private var extraViews: ExtraViews? = null

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
        firestore = Firestore(context!!)
        manager = ProfileManager()

        leadFilter = LeadFilter()

        linearLayoutManager = LinearLayoutManager(context)

        binding!!.fab.setOnClickListener(this)
        binding!!.fabFilter.setOnClickListener(this)

        adapter = LeadsItemAdapter(leadDetailsList, context!!, getString(userType))
        binding!!.recyclerView!!.adapter = adapter
        setLayoutByUser()

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

            fetchLeads()
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
        fetchLeads()
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
                if (context != null)
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
            R.string.telecaller -> leadFilter?.assigner = manager!!.getuId()
            R.string.salesman -> leadFilter?.assignee = manager!!.getuId()
            R.string.business_associate -> leadFilter?.businessAssociateUId = manager!!.getuId()
            R.string.teleassigner -> leadFilter?.forwarder = manager!!.getuId()
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
                    else {
                        val transaction =
                            (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                        val fragment: Fragment = UploadLeadFragment()

                        val bundle = Bundle()
                        bundle.putInt("userType", userType)
                        fragment.arguments = bundle

                        transaction.addToBackStack(null)
                        transaction.replace(R.id.frame_layout, fragment)
                        transaction.addToBackStack(null)
                        transaction.commit()
                    }
                }
            }
        } else {
            extraViews!!.showToast(R.string.no_internet, context)
        }
    }
}