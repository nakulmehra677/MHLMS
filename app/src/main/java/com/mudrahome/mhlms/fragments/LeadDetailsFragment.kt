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
    private val br: BroadcastReceiver? = null

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
        setLayoutVisibility()
        setText()
        Log.d("assignerContact", "User assigner Uid  "+leadDetails.assignerUId  )

        if (leadDetails.assignerUId == "Not available" || leadDetails.assignerUId == null) {
            hideAssignerContact()
        } else {
            firestore!!.getUsers(object : FirestoreInterfaces.OnGetUserDetails {
                override fun onSuccess(userDetails: UserDetails?) {


                    if (userDetails!!.contactNumber!!.toString() == "Not available" ||
                        userDetails.contactNumber == null
                    ) {
                        Log.d("assignerContact", "User Details  "+userDetails!!.contactNumber  )
                        hideAssignerContact()
                    } else {
                        binding!!.assignerContactNumber!!.text = userDetails.contactNumber

                    }
                }

                override fun fail() {
                    hideAssignerContact()
                }

            }, leadDetails.assignerUId!!)

            binding!!.assignerContactNumber.setOnClickListener { _ ->
                val permission = PermissionManager(context)
                if (permission.checkCallPhone()) {
                    callCustomer(binding!!.assignerContactNumber.text.toString())
                } else
                    permission.requestCallPhone()
            }
        }

        if (leadDetails.assignedToUId == "Not available" || leadDetails.assignedToUId == null) {
            hideAssigneeContact()
        } else {
            firestore!!.getUsers(object : FirestoreInterfaces.OnGetUserDetails {
                override fun onSuccess(userDetails: UserDetails?) {
                    if (userDetails!!.contactNumber == "Not available" ||
                        userDetails.contactNumber == null
                    ) {
                        hideAssigneeContact()
                    } else {
                        binding!!.assigneeContactNumber!!.text = userDetails.contactNumber
                    }
                }

                override fun fail() {
                    hideAssigneeContact()
                }

            }, leadDetails.assignedToUId!!)

            binding!!.assigneeContactNumber.setOnClickListener { _ ->
                val permission = PermissionManager(context)
                if (permission.checkCallPhone()) {
                    callCustomer(binding!!.assigneeContactNumber.text.toString())
                } else
                    permission.requestCallPhone()
            }
        }

        binding!!.contactNumber.setOnClickListener { _ ->
            val permission = PermissionManager(context)

            if (permission.checkCallPhone()) {
                callCustomer(leadDetails.contactNumber)
            } else
                permission.requestCallPhone()
        }

        binding!!.editLeadDetails.setOnClickListener { _ ->
            val cm = activity!!
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (cm.activeNetworkInfo != null) {
                if (userType == getString(R.string.telecaller)
                    || userType == getString(R.string.teleassigner)
                ) {
                    progress = ProgressDialog(context)
                    progress!!.setMessage("Loading...")
                    progress!!.setCancelable(false)
                    progress!!.setCanceledOnTouchOutside(false)
                    progress!!.show()

                    firestore!!.fetchUsersByUserType(
                        onFetchSalesPersonList(),
                        leadDetails.location!!,
                        getString(R.string.salesman)
                    )
                } else if (userType == getString(R.string.salesman)) {
                    openSalesmanFragment()
                }
            } else
                Toast.makeText(context, R.string.no_internet, Toast.LENGTH_SHORT).show()
        }
    }

    private fun hideAssignerContact() {
        binding!!.assignerContactNumber.isClickable = false
        binding!!.assignerContactNumber.setCompoundDrawables(null, null, null, null)
        binding!!.assignerContactNumber.text = "Not available"
        Log.d("assignerContact", binding!!.assignerContactNumber.text.toString())
    }

    private fun hideAssigneeContact() {
        binding!!.assigneeContactNumber.isClickable = false
        binding!!.assigneeContactNumber.setCompoundDrawables(null, null, null, null)
        binding!!.assigneeContactNumber.text = "Not available"
    }

    private fun setLayoutVisibility() {
        if (userType == getString(R.string.telecaller) || userType == getString(R.string.teleassigner)) {
            binding!!.assignerLayout.visibility = View.GONE
            binding!!.assignerContactLayout.visibility = View.GONE
        } else if (userType == getString(R.string.salesman)) {
            binding!!.assignedToLayout.visibility = View.GONE
            binding!!.assigneeContactLayout.visibility = View.GONE
        } else if (userType == getString(R.string.admin) || userType == getString(R.string.business_associate)) {
            binding!!.editLeadDetails.visibility = View.GONE
        }

        if (leadDetails.employment == "Salaried") {
            binding!!.employmentTypeLayout.visibility = View.GONE
        }

        if (leadDetails.salesmanRemarks == null || leadDetails.salesmanRemarks == "Not available") {
            binding!!.customerRemarksLayout!!.visibility = View.GONE
        }

        if(leadDetails.assignerUId == null && leadDetails.assigner == null){
            binding!!.assignerLinearLayout!!.visibility = View.GONE
            binding!!.assigneeLinearLayout!!.visibility = View.GONE
            binding!!.leadStatusLinearLayout!!.visibility = View.GONE
        }
    }

    private fun setText() {
        if (!leadDetails.salesmanReason.isNullOrEmpty()) {
            if (leadDetails.salesmanReason.size != 1) {
                var temp = 1
                var remark =
                    getLatestRemark(leadDetails.salesmanReason[leadDetails.salesmanReason.size - temp])
                while (remark == null) {
                    temp++
                    remark =
                        getLatestRemark(leadDetails.salesmanReason[leadDetails.salesmanReason.size - temp])
                }
                binding!!.latestsalesmanRemark.text = Html.fromHtml(remark)
                binding!!.viewallSalesmanRemark.setOnClickListener { view ->
                    newInstance(leadDetails.salesmanReason, "Salesman's").show(
                        childFragmentManager,
                        "ShowRemarks"
                    )
                }
            } else {
                if (leadDetails.salesmanReason.isNullOrEmpty()) {
                    latestsalesmanRemark!!.visibility = View.GONE
                    viewallSalesmanRemark!!.text = "Not available"
                } else if (!leadDetails.salesmanReason[0].matches("Not available".toRegex())) {
                    latestsalesmanRemark!!.text =
                        Html.fromHtml(getLatestRemark(leadDetails.salesmanReason[0]))
                    viewallSalesmanRemark!!.text = ""
                } else {
                    latestsalesmanRemark!!.visibility = View.GONE
                    viewallSalesmanRemark!!.text = "Not available"
                }

                viewallSalesmanRemark!!.setTextColor(resources.getColor(R.color.coloBlack))
            }
        }

        if (!leadDetails.telecallerRemarks.isNullOrEmpty()) {
            if (leadDetails.telecallerRemarks.size > 1) {
                var remark =
                    getLatestRemark(leadDetails.telecallerRemarks[leadDetails.telecallerRemarks.size - 1])
                var temp = 1
                while (remark == null) {
                    temp++
                    remark =
                        getLatestRemark(leadDetails.telecallerRemarks[leadDetails.telecallerRemarks.size - temp])
                }

                latestCallerRemark!!.text = Html.fromHtml(remark)
                viewallCallerRemark!!.setOnClickListener { view ->
                    newInstance(leadDetails.telecallerRemarks, "Caller's").show(
                        childFragmentManager,
                        "ShowRemarks"
                    )
                }
            } else {
                if (leadDetails.telecallerRemarks.isNullOrEmpty()) {
                    viewallCallerRemark!!.text = "Not available"
                    latestCallerRemark!!.visibility = View.GONE
                } else if (!leadDetails.telecallerRemarks[0].equals("Not available")) {
                    latestCallerRemark!!.text =
                        Html.fromHtml(getLatestRemark(leadDetails.telecallerRemarks[0]))
                    viewallCallerRemark!!.text = ""
                } else {
                    viewallCallerRemark!!.text = "Not available"
                    latestCallerRemark!!.visibility = View.GONE
                }
                viewallCallerRemark!!.setTextColor(resources.getColor(R.color.coloBlack))
            }
        }

        binding!!.customerName.text = leadDetails.name
        binding!!.loanAmount.text = leadDetails.loanAmount
        binding!!.contactNumber.text = leadDetails.contactNumber
        binding!!.employment.text = leadDetails.employment
        binding!!.employmentType.text = leadDetails.employmentType
        binding!!.loanType.text = leadDetails.loanType
        binding!!.propertyType.text = leadDetails.propertyType
        binding!!.location.text = leadDetails.location
        binding!!.assigner!!.text = leadDetails.assigner
        binding!!.customerRemarks.text = leadDetails.salesmanRemarks
        binding!!.date.text = Date(leadDetails.timeStamp).toString()


        if (leadDetails.contactNumber == "Not available" ||
            leadDetails.contactNumber == null
        ) {
            binding!!.contactNumber.isClickable = false
            binding!!.contactNumber.setCompoundDrawables(null, null, null, null)
        }

        if (leadDetails.assignedTo.isNullOrEmpty()) {
            binding!!.assignTo.text = "Not assigned yet"
            binding!!.status.text = "Not assigned yet"
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
    }

    private fun getLatestRemark(remark: String): String? {
        var r: String? = null

        if (remark.contains("@@")) {
            val remarkWithTime =
                remark.split("@@".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            @SuppressLint("SimpleDateFormat") val sdf = SimpleDateFormat("MMM dd,yyyy hh:mm a")
            val resultdate = Date(java.lang.Long.parseLong(remarkWithTime[1]))

            if (!remarkWithTime[0].isEmpty())
                r =
                    "<font color=\"#196587\">" + sdf.format(resultdate) + "</font>" + "<br>" + remarkWithTime[0]
        } else {
            if (!remark.isEmpty())
                r = remark
        }

        return r
    }

//    override fun onStart() {
//        super.onStart()
//        mBehavior!!.state = BottomSheetBehavior.STATE_EXPANDED
//    }

    private fun onFetchSalesPersonList(): FirestoreInterfaces.OnFetchUsersList {
        return FirestoreInterfaces.OnFetchUsersList { userList ->
            progress!!.dismiss()
            if (userList!!.userList.size > 0)
                openTelecallerFragment(userList.userList)
            else {
                Toast.makeText(
                    context, "No Salesmen are present for " +
                            leadDetails.location + ".", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun openTelecallerFragment(userList: List<UserDetails>) {
        if (userList.size != 0) {
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
                setLayoutVisibility()
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