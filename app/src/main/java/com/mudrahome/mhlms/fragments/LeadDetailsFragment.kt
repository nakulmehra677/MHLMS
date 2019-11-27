package com.mudrahome.mhlms.fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mudrahome.mhlms.R
import com.mudrahome.mhlms.databinding.FragmentLeadDetailsBinding
import com.mudrahome.mhlms.firebase.Firestore
import com.mudrahome.mhlms.fragments.ViewAllRemarksFragment.newInstance
import com.mudrahome.mhlms.interfaces.FirestoreInterfaces
import com.mudrahome.mhlms.managers.PermissionManager
import com.mudrahome.mhlms.managers.TimeManager
import com.mudrahome.mhlms.model.LeadDetails
import com.mudrahome.mhlms.model.UserDetails
import com.mudrahome.mhlms.model.UserList
import kotlinx.android.synthetic.main.fragment_lead_details.*
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ValidFragment")
class LeadDetailsFragment(
    private val leadDetails: LeadDetails,
    private val userType: String
) : BottomSheetDialogFragment() {

    private var progress: ProgressDialog? = null
    private var firestore: Firestore? = null

    private val customerNotInterested = "Customer Not Interested"
    private val documentPicked = "Document Picked"
    private val customerFollowUp = "Customer follow Up"
    private val customerNotContactable = "Customer Not Contactable"
    private val customerInterestedButDocumentPending = "Customer Interested but Document Pending"
    private val notDoable = "Not Doable"
    private val documentPickedFileLoggedIn = "Document Picked and File Logged in"

    private var isEdit: Boolean? = false
    private var binding: FragmentLeadDetailsBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.fragment_lead_details, null, false
        )

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firestore = Firestore()

        setText()

        if (leadDetails.assignerUId != null) {
            firestore!!.getUsers(object : FirestoreInterfaces.OnGetUserDetails {
                override fun onSuccess(userDetails: UserDetails?) {

                    if (userDetails?.contactNumber == null) {
                        hideCallerContact()
                    } else {
                        binding!!.callerContactNumber!!.text = userDetails.contactNumber
                    }
                }

                override fun fail() {
                    hideCallerContact()
                }

            }, leadDetails.assignerUId!!)
        }

        if (leadDetails.assignedToUId != null) {
            firestore!!.getUsers(object : FirestoreInterfaces.OnGetUserDetails {
                override fun onSuccess(userDetails: UserDetails?) {

                    if (userDetails?.contactNumber == null) {
                        hideAssigneeContact()
                    } else {
                        binding!!.assigneeContactNumber!!.text = userDetails.contactNumber
                    }
                }

                override fun fail() {
                    hideAssigneeContact()
                }

            }, leadDetails.assignedToUId!!)
        }

        if (leadDetails.forwarderUId != null) {
            firestore!!.getUsers(object : FirestoreInterfaces.OnGetUserDetails {
                override fun onSuccess(userDetails: UserDetails?) {

                    if (userDetails?.contactNumber == null) {
                        hideAssignerContact()
                    } else {
                        binding!!.assignerContactNumber!!.text = userDetails.contactNumber
                    }
                }

                override fun fail() {
                    hideAssignerContact()
                }

            }, leadDetails.forwarderUId!!)
        }

        binding!!.callerContactNumber.setOnClickListener {
            val permission = PermissionManager(context)
            if (permission.checkCallPhone()) {
                callCustomer(binding!!.assignerContactNumber.text.toString())
            } else
                permission.requestCallPhone()
        }

        binding!!.assignerContactNumber.setOnClickListener {
            val permission = PermissionManager(context)
            if (permission.checkCallPhone()) {
                callCustomer(binding!!.assignerContactNumber.text.toString())
            } else
                permission.requestCallPhone()
        }

        binding!!.assigneeContactNumber.setOnClickListener {
            val permission = PermissionManager(context)
            if (permission.checkCallPhone()) {
                callCustomer(binding!!.assigneeContactNumber.text.toString())
            } else
                permission.requestCallPhone()
        }
    }

    private fun hideAssignerContact() {
        binding!!.assignerContactNumber.isClickable = false
        binding!!.assignerContactNumber.setCompoundDrawables(null, null, null, null)
        binding!!.assignerContactNumber.text = "Not available"
    }

    private fun hideAssigneeContact() {
        binding!!.assigneeContactNumber.isClickable = false
        binding!!.assigneeContactNumber.setCompoundDrawables(null, null, null, null)
        binding!!.assigneeContactNumber.text = "Not available"
    }

    private fun hideCallerContact() {
        binding!!.callerContactNumber.isClickable = false
        binding!!.callerContactNumber.setCompoundDrawables(null, null, null, null)
        binding!!.callerContactNumber.text = "Not available"
    }

    private fun setText() {
        binding!!.customerName.text = leadDetails.name
        binding!!.loanAmount.text = leadDetails.loanAmount
        binding!!.contactNumber.text = leadDetails.contactNumber
        binding!!.employment.text = leadDetails.employment
        binding!!.employmentType.text = leadDetails.employmentType
        binding!!.loanType.text = leadDetails.loanType
        binding!!.propertyType.text = leadDetails.propertyType
        binding!!.location.text = leadDetails.location
        binding!!.caller!!.text = leadDetails.assigner
        binding!!.assigner!!.text = leadDetails.forwarderName
        binding!!.customerRemarks.text = leadDetails.salesmanRemarks
        binding!!.date.text = Date(leadDetails.timeStamp).toString()
        binding!!.status.text = leadDetails.status


        if (leadDetails.contactNumber == null) {
            binding!!.contactNumber.isClickable = false
            binding!!.contactNumber.setCompoundDrawables(null, null, null, null)
        }

        if (leadDetails.assignedTo.isNullOrEmpty()) {
            binding!!.assignTo.text = "Not assigned yet"
            binding!!.assignDate.text = "Not assigned yet"
            binding!!.assignTime.text = "Not assigned yet"

            hideAssigneeContact()
        } else {
            binding!!.assignTo.text = leadDetails.assignedTo
            binding!!.status.text = leadDetails.status
            binding!!.assignDate.text = leadDetails.assignDate
        }

        if (leadDetails.banks.isNullOrEmpty()) {
            binding!!.bankLayout.visibility = View.GONE
        } else {
            val csvBuilder = StringBuilder()
            for (bank in leadDetails.banks) {
                csvBuilder.append(bank)
                csvBuilder.append(", ")
            }
            val bankList = csvBuilder.toString()
            binding!!.bankNames.text = bankList
            binding!!.bankLayout.visibility = View.VISIBLE
        }

        if (!leadDetails.salesmanReason.isNullOrEmpty()) {
            binding!!.assigneeRemarks.setOnClickListener { view ->
                newInstance(leadDetails.salesmanReason, "Salesman's").show(
                    childFragmentManager,
                    "ShowRemarks"
                )
            }
        }

        if (!leadDetails.telecallerRemarks.isNullOrEmpty()) {
            binding!!.callerRemarks!!.setOnClickListener { view ->
                newInstance(leadDetails.telecallerRemarks, "Caller's").show(
                    childFragmentManager,
                    "ShowRemarks"
                )
            }
        }

        if (!leadDetails.forwarderRemarks.isNullOrEmpty()) {
            binding!!.assignerRemarks!!.setOnClickListener { view ->
                newInstance(leadDetails.forwarderRemarks, "Caller's").show(
                    childFragmentManager,
                    "ShowRemarks"
                )
            }
        }

    }


//    override fun onStart() {
//        super.onStart()
//        mBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
//    }

    private fun onFetchSalesPersonList(): FirestoreInterfaces.OnFetchUsersList {
        return object : FirestoreInterfaces.OnFetchUsersList {
            override fun onFail() {
                progress?.dismiss()
                val userList = ArrayList<UserDetails>()
                openTelecallerFragment(userList)
            }

            override fun onListFetched(userList: UserList?) {
                progress?.dismiss()
                openTelecallerFragment(userList!!.userList)
            }

        }
    }

    private fun openTelecallerFragment(userList: List<UserDetails>) {
        TelecallerEditLeadFragment.newInstance(
            leadDetails, userList, { dialogLeadDetails ->
                progress = ProgressDialog(context)
                progress!!.setMessage("Loading..")
                progress!!.setCancelable(false)
                progress!!.setCanceledOnTouchOutside(false)
                progress!!.show()

                firestore!!.updateLeadDetails(onUpdateLead(), dialogLeadDetails)
            }, userType
        ).show(fragmentManager!!, "promo")
    }

    private fun openSalesmanFragment() {
        SalesmanEditLeadFragment.newInstance(leadDetails) { dialogSalesmanRemarks, dialogSalesmanReason, banks ->

            val timeManager = TimeManager()

            //            leadDetails.setDate(timeModel.getDate());
            //            leadDetails.setTime(timeModel.getTime());
            leadDetails.timeStamp = timeManager.timeStamp
            leadDetails.banks = banks

            val salesmanReson = leadDetails.salesmanReason
            salesmanReson.add(dialogSalesmanReason + "@@" + System.currentTimeMillis())        // Set SalesmanReason with timesteamp

            leadDetails.salesmanRemarks = dialogSalesmanRemarks
            leadDetails.salesmanReason = salesmanReson

            if (dialogSalesmanRemarks == customerNotInterested)
                leadDetails.status = getString(R.string.inactive)
            else if (dialogSalesmanRemarks == documentPicked)
                leadDetails.status = getString(R.string.work_in_progress)
            else if (dialogSalesmanRemarks == documentPickedFileLoggedIn)
                leadDetails.status = getString(R.string.closed)
            else if (dialogSalesmanRemarks == customerFollowUp)
                leadDetails.status = getString(R.string.follow_up)
            else if (dialogSalesmanRemarks == customerNotContactable)
                leadDetails.status = getString(R.string.inactive)
            else if (dialogSalesmanRemarks == customerInterestedButDocumentPending)
                leadDetails.status = getString(R.string.work_in_progress)
            else if (dialogSalesmanRemarks == notDoable)
                leadDetails.status = getString(R.string.not_doable)
            else
                leadDetails.status = getString(R.string.active)

            progress = ProgressDialog(context)
            progress!!.setMessage("Loading..")
            progress!!.setCancelable(false)
            progress!!.setCanceledOnTouchOutside(false)
            progress!!.show()
            firestore!!.updateLeadDetails(onUpdateLead(), leadDetails)
        }.show(fragmentManager!!, "promo")
    }

    private fun onUpdateLead(): FirestoreInterfaces.OnUpdateLead {
        return object : FirestoreInterfaces.OnUpdateLead {
            override fun onLeadUpdated() {
                Toast.makeText(context, R.string.lead_update, Toast.LENGTH_SHORT).show()
                isEdit = true
                setText()
                if (progress!!.isShowing)
                    progress!!.dismiss()
            }

            override fun onFailer() {
                Toast.makeText(context, R.string.lead_update_failed, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun callCustomer(number: String?) {
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:" + number!!)
        startActivity(callIntent)
    }
}