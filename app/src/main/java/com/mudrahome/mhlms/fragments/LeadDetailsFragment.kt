package com.mudrahome.mhlms.fragments

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mudrahome.mhlms.R
import com.mudrahome.mhlms.databinding.FragmentLeadDetailsBinding
import com.mudrahome.mhlms.firebase.Firestore
import com.mudrahome.mhlms.fragments.ViewAllRemarksFragment.newInstance
import com.mudrahome.mhlms.interfaces.FirestoreInterfaces
import com.mudrahome.mhlms.interfaces.OnSubmitClickListener
import com.mudrahome.mhlms.managers.PermissionManager
import com.mudrahome.mhlms.managers.ProfileManager
import com.mudrahome.mhlms.managers.TimeManager
import com.mudrahome.mhlms.model.LeadDetails
import com.mudrahome.mhlms.model.UserDetails
import com.mudrahome.mhlms.model.UserList
import java.util.*

@SuppressLint("ValidFragment")
class LeadDetailsFragment(
    private val leadDetails: LeadDetails,
    private val userType: String
) : BottomSheetDialogFragment() {

    private var progress: ProgressDialog? = null
    private var firestore: Firestore? = null
    private var manager: ProfileManager? = null

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

        manager = ProfileManager(context)
        firestore = Firestore()

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (userType.equals(context!!.getString(R.string.admin))) {
            binding!!.editLeadButton.visibility = View.GONE
        }

        binding!!.editLeadButton.setOnClickListener {
            setButtonAction()
        }

        setText()

        if (leadDetails.assignerUId != null) {
            firestore!!.getUsers(object : FirestoreInterfaces.OnGetUserDetails {
                override fun onSuccess(userDetails: UserDetails?) {

                    if (userDetails?.contactNumber == null) {
                        hideCallerContact()
                    } else {
                        binding!!.callerContactNumber!!.text = userDetails.contactNumber
                        binding!!.callerContactNumber.isClickable = true
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
                        binding!!.assigneeContactNumber.isClickable = true
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
                        binding!!.assignerContactNumber.isClickable = true

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
                callCustomer(binding!!.callerContactNumber.text.toString())
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
        } else {
            binding!!.contactNumber.setOnClickListener {
                val permission = PermissionManager(context)
                if (permission.checkCallPhone()) {
                    callCustomer(binding!!.contactNumber.text.toString())
                } else
                    permission.requestCallPhone()
            }
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
            binding!!.assignTime.text = leadDetails.assignTime

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

    private fun setButtonAction() {
        when {
            leadDetails.status == "Incomplete Lead" -> {
                val transaction =
                    (context as AppCompatActivity).supportFragmentManager.beginTransaction()
                val fragment: Fragment = UploadLeadFragment()

                val bundle = Bundle()
                bundle.putSerializable("leadDetails", leadDetails)
                fragment.arguments = bundle

                transaction.addToBackStack(null)
                transaction.replace(R.id.content_frame, fragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }

            manager!!.spUserType == context!!.getString(R.string.telecaller) && leadDetails.forwarderUId != null -> {
                openCallerFragment()
            }

            manager!!.spUserType == context!!.getString(R.string.telecaller_and_teleassigner) ||
                    manager!!.spUserType == context!!.getString(R.string.telecaller) -> {
                firestore!!.fetchUsersByUserType(
                    onFetchSalesPersonList(),
                    leadDetails.location!!,
                    context!!.getString(R.string.salesman)
                )
            }

            userType == context!!.getString(R.string.salesman) -> {
                openSalesmanFragment()
            }

            else -> {
                binding!!.editLeadButton.visibility = View.GONE
            }
        }
    }

    private fun onFetchSalesPersonList(): FirestoreInterfaces.OnFetchUsersList {
        return object : FirestoreInterfaces.OnFetchUsersList {
            override fun onFail() {
                progress?.dismiss()
            }

            override fun onListFetched(userList: UserList?) {
                progress?.dismiss()

                if (userList!!.userList.size > 0) {
                    openAssignerFragment(userList.userList)
                } else {
                    Toast.makeText(
                        context,
                        "No salesman present for this location.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    }

    private fun openCallerFragment() {
        CallerEditLeadFragment(
            leadDetails, OnSubmitClickListener { dialogLeadDetails ->
                progress = ProgressDialog(context)
                progress!!.setMessage("Loading..")
                progress!!.setCancelable(false)
                progress!!.setCanceledOnTouchOutside(false)
                progress!!.show()

                firestore!!.updateLeadDetails(onUpdateLead(), dialogLeadDetails)
            }
        ).show(fragmentManager!!, "promo")
    }

    private fun openAssignerFragment(userList: List<UserDetails>) {
        AssignerEditLeadFragment(
            leadDetails, userList, userType, OnSubmitClickListener { dialogLeadDetails ->
                progress = ProgressDialog(context)
                progress!!.setMessage("Loading..")
                progress!!.setCancelable(false)
                progress!!.setCanceledOnTouchOutside(false)
                progress!!.show()

                leadDetails.status = setStatus(leadDetails.salesmanRemarks!!)

                firestore!!.updateLeadDetails(onUpdateLead(), dialogLeadDetails)
            }
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
            leadDetails.status = setStatus(dialogSalesmanRemarks)

            progress = ProgressDialog(context)
            progress!!.setMessage("Loading..")
            progress!!.setCancelable(false)
            progress!!.setCanceledOnTouchOutside(false)
            progress!!.show()
            firestore!!.updateLeadDetails(onUpdateLead(), leadDetails)
        }.show(fragmentManager!!, "promo")
    }

    private fun setStatus(remarks: String): String {
        if (remarks == customerNotInterested)
            return getString(R.string.inactive)
        else if (remarks == documentPicked)
            return getString(R.string.work_in_progress)
        else if (remarks == documentPickedFileLoggedIn)
            return getString(R.string.closed)
        else if (remarks == customerFollowUp)
            return getString(R.string.follow_up)
        else if (remarks == customerNotContactable)
            return getString(R.string.inactive)
        else if (remarks == customerInterestedButDocumentPending)
            return getString(R.string.work_in_progress)
        else if (remarks == notDoable)
            return getString(R.string.not_doable)
        else
            return getString(R.string.active)
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