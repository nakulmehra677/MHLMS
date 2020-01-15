package com.mudrahome.mhlms.fragments

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.mudrahome.mhlms.R
import com.mudrahome.mhlms.databinding.FragmentUploadLeadBinding
import com.mudrahome.mhlms.enums.UploadLeadEnum
import com.mudrahome.mhlms.firebase.Firestore
import com.mudrahome.mhlms.interfaces.FirestoreInterfaces
import com.mudrahome.mhlms.managers.Alarm
import com.mudrahome.mhlms.managers.LeadManager
import com.mudrahome.mhlms.managers.ProfileManager
import com.mudrahome.mhlms.model.LeadDetails
import com.mudrahome.mhlms.model.UserDetails
import com.mudrahome.mhlms.model.UserList
import com.mudrahome.mhlms.sharedPreferences.ProfileSP
import kotlinx.android.synthetic.main.user_list_item.*
import java.util.*


private const val ARG_PARAM1 = "leadDetails"

class UploadLeadFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentUploadLeadBinding

    private var currentLeadDetails: LeadDetails? = null

    private var propertyTypeAdapter: ArrayAdapter<CharSequence>? = null
    private var loanTypeAdapter: ArrayAdapter<CharSequence>? = null
    private var locationAdapter: ArrayAdapter<CharSequence>? = null
    private var assignedToAdapter: ArrayAdapter<CharSequence>? = null
    private var forwardToAdapter: ArrayAdapter<CharSequence>? = null

    private var assigneeContact: String? = null
    private var personList: List<UserDetails>? = null
    private var mYear = 0
    private var mMonth = 0
    private var mDay = 0
    private var mHour = 0
    private var mMinute = 0
    private var alarmYear = 0
    private var alarmMonth = 0
    private var alarmDay = 0
    private var alarmHour = 0
    private var alarmMinute = 0

    private lateinit var firestore: Firestore
    private lateinit var leadManager: LeadManager
    private lateinit var profileManager: ProfileManager

    private lateinit var user: UserDetails
    private var userType: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            currentLeadDetails = it.getSerializable(ARG_PARAM1) as LeadDetails?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_upload_lead, container, false
        )

        showProgressDialog("Loading..", context)

        profileManager = ProfileManager()
        firestore = Firestore(context!!)

        firestore.getUsers(object : FirestoreInterfaces.OnGetUserDetails {
            override fun onSuccess(userDetails: UserDetails) {
                user = userDetails
                if (user.userType!!.contains("Telecaller") && user.userType!!.contains("Assigner")) {
                    userType = R.string.telecaller_and_teleassigner
                } else {
                    userType = R.string.telecaller
                }
                if (currentLeadDetails != null)
                    leadManager = LeadManager(userType!!, currentLeadDetails)
                else {
                    leadManager = LeadManager(userType!!)
                }

                if (progress!!.isShowing) dismissProgressDialog()
                initLayout()
            }

            override fun fail() {
                if (progress!!.isShowing) dismissProgressDialog()
                onDestroy()
            }
        }, profileManager.getuId())

        return binding.root
    }


    private fun initLayout() {

        if (currentLeadDetails != null) {
            binding.name.setText(currentLeadDetails!!.name)
            binding.contactNumber.setText(currentLeadDetails!!.contactNumber)
            binding.loanAmount.setText(currentLeadDetails!!.loanAmount)

            if (currentLeadDetails!!.employment == "Salaried") {
                binding.salaried.isChecked = true
                binding.selfEmployed.isChecked = false
                binding.employmentNotProvided.isChecked = false

            } else if (currentLeadDetails!!.employment == "Self Employed") {
                binding.salaried.isChecked = false
                binding.selfEmployed.isChecked = true
                binding.employmentNotProvided.isChecked = false

                binding.selfEmployementLayout.visibility = View.VISIBLE

                if (currentLeadDetails!!.employmentType == "Partnership Firm") {
                    binding.partnershipFirm.isChecked = true
                    binding.privateLimitedCompany.isChecked = false
                    binding.proprietorshipFirm.isChecked = false
                    binding.selfEmploymentNotProvided.isChecked = false
                } else if (currentLeadDetails!!.employmentType == "Private Limited Company") {
                    binding.partnershipFirm.isChecked = false
                    binding.privateLimitedCompany.isChecked = true
                    binding.proprietorshipFirm.isChecked = false
                    binding.selfEmploymentNotProvided.isChecked = false
                } else if (currentLeadDetails!!.employmentType == "Proprietorship firm") {
                    binding.partnershipFirm.isChecked = false
                    binding.privateLimitedCompany.isChecked = false
                    binding.proprietorshipFirm.isChecked = true
                    binding.selfEmploymentNotProvided.isChecked = false
                } else {
                    binding.partnershipFirm.isChecked = false
                    binding.privateLimitedCompany.isChecked = false
                    binding.proprietorshipFirm.isChecked = false
                    binding.selfEmploymentNotProvided.isChecked = true
                }
            } else {
                binding.salaried.isChecked = false
                binding.selfEmployed.isChecked = false
                binding.employmentNotProvided.isChecked = true
            }
        }

        onRadioButtonClicked()
        onButtonClicked()

        initializeLoanTypeSpinner()
        initializeLocationSpinner()
    }

    private fun initializeLoanTypeSpinner() {
        // Loan type Spinner
        loanTypeAdapter = ArrayAdapter.createFromResource(
            context!!,
            R.array.loan_type, android.R.layout.simple_spinner_item
        )
        loanTypeAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.loanType?.adapter = loanTypeAdapter
        binding.loanType?.onItemSelectedListener = this
    }

    private fun initializeLocationSpinner() {
        // Location Spinner

        locationAdapter = ArrayAdapter.createFromResource(
            context!!,
            R.array.location, android.R.layout.simple_spinner_item
        )

        locationAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.location?.adapter = locationAdapter
        binding.location?.onItemSelectedListener = this
    }

    private fun initializePropertyTypeSpinner() {
        // Property type Spinner
        propertyTypeAdapter = if (leadManager.getLoanType() == "Home Loan") {
            ArrayAdapter.createFromResource(
                context!!,
                R.array.property, android.R.layout.simple_spinner_item
            )
        } else {
            ArrayAdapter.createFromResource(
                context!!,
                R.array.property_type, android.R.layout.simple_spinner_item
            )
        }

        propertyTypeAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.propertyType?.adapter = propertyTypeAdapter
        binding.propertyType?.onItemSelectedListener = this
    }

    private fun initializeAssignToSpinner(userName: List<String>) {
        // AssignedTo Spinner
        assignedToAdapter = ArrayAdapter(
            context!!,
            android.R.layout.simple_spinner_item, userName
        )

        assignedToAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.assignTo?.adapter = assignedToAdapter
        binding.assignTo?.onItemSelectedListener = this

        binding.assignTo?.isEnabled = true
        binding.assignTo?.isClickable = true
    }

    private fun initializeForwardToSpinner(userName: List<String>) {
        // ForwardTo Spinner
        forwardToAdapter = ArrayAdapter(
            context!!,
            android.R.layout.simple_spinner_item, userName
        )

        forwardToAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.forwardTo?.adapter = forwardToAdapter
        binding.forwardTo?.onItemSelectedListener = this

        binding.forwardTo?.isEnabled = true
        binding.forwardTo?.isClickable = true
    }

    private fun onRadioButtonClicked() {

        binding.employmentRadioGroup.setOnCheckedChangeListener { radioGroup, i ->

            var employment: String? = null

            if (i == binding.salaried.id) {
                employment = binding.salaried.text.toString()
                binding.selfEmployementLayout.visibility = View.GONE
            } else if (i == binding.selfEmployed.id) {
                employment = binding.selfEmployed.text.toString()
                binding.selfEmployementLayout.visibility = View.VISIBLE
            } else {
                employment = binding.employmentNotProvided.text.toString()
                binding.selfEmployementLayout.visibility = View.GONE
            }

            leadManager.setEmployment(employment)

        }

        binding.selfEmploymentTypeRadioGroup.setOnCheckedChangeListener { _, i ->

            var employmentType: String? = null

            if (i == binding.partnershipFirm.id) {
                employmentType = binding.partnershipFirm.text.toString()
            } else if (i == binding.privateLimitedCompany.id) {
                employmentType = binding.privateLimitedCompany.text.toString()
            } else if (i == binding.proprietorshipFirm.id) {
                employmentType = binding.proprietorshipFirm.text.toString()
            } else {
                employmentType = binding.selfEmploymentNotProvided.text.toString()
            }
            leadManager.setEmploymentType(employmentType)
        }

        binding.callLaterRadioGroup.setOnCheckedChangeListener { _, i ->

            var callLater: String? = null

            if (i == binding.callLaterNo.id) {
                callLater = binding.callLaterNo.text.toString()

                if (leadManager.location != "Not Provided") {
                    if (isNetworkConnected()) {
                        if (user.userType!!.contains("Telecaller") && !user.userType!!.contains("Assigner")) {
                            getAssignerListByLocation()
                        } else {
                            getSalesmanListByLocation()
                        }
                    } else {
                        showToastMessage(R.string.no_internet)
                        initializeLocationSpinner()
                    }
                }

            } else {
                callLater = binding.callLaterYes.text.toString()

                binding.forwardToLayout.visibility = View.GONE
                binding.assignToLayout.visibility = View.GONE
            }
            leadManager.setCallLater(callLater)
        }
    }

    override fun onItemSelected(
        parent: AdapterView<*>,
        view: View,
        position: Int,
        id: Long
    ) {
        when (parent.id) {
            R.id.loan_type -> {
                val loanType = parent.getItemAtPosition(position).toString()
                leadManager.loanType = loanType

                if (loanType == "Home Loan" || loanType == "Loan Against Property") {
                    initializePropertyTypeSpinner()
                    binding.propertyTypeLayout?.visibility = View.VISIBLE
                } else {
                    binding.propertyTypeLayout?.visibility = View.GONE
                }
            }

            R.id.property_type -> {
                val propertyType = parent.getItemAtPosition(position).toString()
                leadManager.setPropertyType(propertyType)
            }

            R.id.location -> {
                val location = parent.getItemAtPosition(position).toString()
                leadManager.location = location

                if (location != "Not Provided" && leadManager.status != "Incomplete Lead") {
                    if (isNetworkConnected()) {
                        if (user.userType!!.contains("Telecaller") && !user.userType!!.contains("Assigner")) {
                            getAssignerListByLocation()
                        } else {
                            getSalesmanListByLocation()
                        }
                    } else {
                        showToastMessage(R.string.no_internet)
                        initializeLocationSpinner()
                    }
                }
            }

            R.id.assign_to -> {
                val name = parent.getItemAtPosition(position).toString()
                for (user in this.personList!!) {
                    if (user.userName == name) {
                        leadManager.setAssignee(name, user.uId)
                        assigneeContact = user.contactNumber
                    }
                }
            }

            R.id.forward_to -> {
                val name = parent.getItemAtPosition(position).toString()
                for (user in this.personList!!) {
                    if (user.userName == name) {
                        leadManager.setForwarder(name, user.uId)
                    }
                }
            }
            else -> {
            }
        }
    }

    private fun onButtonClicked() {
        val c = Calendar.getInstance()

        binding.uploadDetails.setOnClickListener {
            if (isNetworkConnected()) {

                leadManager.customerName = binding.name.text.toString()
                leadManager.customerContact = binding.contactNumber.text.toString()
                leadManager.setLoanAmount(binding.loanAmount.text.toString())
                leadManager.setRemarks(binding.remarks.text.toString())

                leadManager.setLeadReminderDate(binding.date.text.toString())
                leadManager.setLeadReminderTime(binding.time.text.toString())

                leadManager.setAlarmDetails(
                    binding.date.text.toString(),
                    binding.time.text.toString()
                )

                val enum = leadManager.verifyLead()

                if (enum == UploadLeadEnum.UPLOAD_LEAD) {
                    uploadDetails()
                } else if (enum == UploadLeadEnum.FILL_NAME) {
                    showToastMessage(R.string.cannot_leave_name)
                } else if (enum == UploadLeadEnum.FILL_CONTACT) {
                    showToastMessage(R.string.cannot_leave_contact)
                } else if (enum == UploadLeadEnum.FILL_CALLER_REMARKS) {
                    showToastMessage(R.string.cannot_leave_remarks)
                } else if (enum == UploadLeadEnum.FILL_LOAN_AMOUNT) {
                    showToastMessage(R.string.cannot_leave_loan_amount)
                } else if (enum == UploadLeadEnum.SET_EMPLOYMENT) {
                    showToastMessage(R.string.set_employment)
                } else if (enum == UploadLeadEnum.SET_EMPLOYMENT_TYPE) {
                    showToastMessage(R.string.set_employment_type)
                } else if (enum == UploadLeadEnum.SET_LOAN_TYPE) {
                    showToastMessage(R.string.set_loan_type)
                } else if (enum == UploadLeadEnum.SET_PROPERTY_TYPE) {
                    showToastMessage(R.string.set_property_type)
                } else if (enum == UploadLeadEnum.SET_LOCATION) {
                    showToastMessage(R.string.set_location)
                } else if (enum == UploadLeadEnum.SET_ASSIGNEE) {
                    showToastMessage(R.string.set_assignee)
                } else if (enum == UploadLeadEnum.SET_REMINDER) {
                    showToastMessage(R.string.set_reminder)
                } else if (enum == UploadLeadEnum.SET_ALARM) {
                    showToastMessage(R.string.set_alarm)
                }
            } else
                showToastMessage(R.string.no_internet)
        }

        binding.datePicker.setOnClickListener {
            // Get Current Date
            mYear = c.get(Calendar.YEAR)
            mMonth = c.get(Calendar.MONTH)
            mDay = c.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                context!!,
                { _, year, monthOfYear, dayOfMonth ->

                    alarmDay = dayOfMonth
                    alarmMonth = monthOfYear
                    alarmYear = year

                    binding.date.text =
                        dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year

                }, mYear, mMonth, mDay
            )

            datePickerDialog.datePicker.minDate = c.timeInMillis
            datePickerDialog.show()
        }

        binding.timePicker.setOnClickListener {
            // Get Current TimeModel
            mHour = c.get(Calendar.HOUR_OF_DAY)
            mMinute = c.get(Calendar.MINUTE)

            // Launch TimeModel Picker Dialog
            val timePickerDialog = TimePickerDialog(
                context!!,
                { _, hourOfDay, minute ->

                    alarmHour = hourOfDay
                    alarmMinute = minute

                    binding.time?.text = "$hourOfDay:$minute"
                }, mHour, mMinute, false
            )
            timePickerDialog.show()
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    private fun getSalesmanListByLocation() {
        showProgressDialog("Loading..", context!!)

        firestore.fetchUsersByUserType(
            object : FirestoreInterfaces.OnFetchUsersList {
                override fun onFail() {
                    if (progress!!.isShowing) dismissProgressDialog()
                    showToastMessage(R.string.error_occur)
                }

                override fun onListFetched(userList: UserList?) {
                    if (progress!!.isShowing) dismissProgressDialog()
                    personList = userList?.userList
                    if (userList?.userList?.size!! > 0) {
                        val salesNameList = ArrayList<String>()
                        for (user in userList.userList) {
                            salesNameList.add(user.userName!!)
                        }
                        initializeAssignToSpinner(salesNameList)
                        binding.assignTo.isEnabled = true
                        binding.assignToLayout.visibility = View.VISIBLE

                    } else {
                        binding.assignTo.isEnabled = false
                        leadManager.setAssignee("Not Assigned", null)
                    }

                    leadManager.setForwarder("Not Assigned", null)
                    binding.forwardTo.isEnabled = false
                    binding.forwardToLayout.visibility = View.GONE
                }
            }, leadManager.location, getString(R.string.salesman)
        )
    }

    private fun getAssignerListByLocation() {
        showProgressDialog("Loading.", context!!)

        firestore.fetchUsersByUserType(
            object : FirestoreInterfaces.OnFetchUsersList {
                override fun onFail() {
                    showToastMessage(R.string.error_occur)
                    if (progress!!.isShowing) dismissProgressDialog()
                }

                override fun onListFetched(userList: UserList?) {
                    personList = userList?.userList
                    if (progress!!.isShowing) dismissProgressDialog()
                    if (userList?.userList?.size!! > 0) {
                        val forwarderNameList = ArrayList<String>()
                        for (user in userList.userList) {
                            forwarderNameList.add(user.userName!!)
                        }

                        binding.forwardToLayout.visibility = View.VISIBLE
                        binding.forwardTo.isEnabled = true

                        binding.assignToLayout.visibility = View.GONE
                        binding.assignTo.isEnabled = false

                        initializeForwardToSpinner(forwarderNameList)

                        leadManager.setAssignee("Not Assigned", null)

                    } else {
                        binding.forwardTo.isEnabled = false
                        leadManager.setForwarder("Not Assigned", null)
                        getSalesmanListByLocation()
                    }
                }
            }, leadManager.location, getString(R.string.teleassigner)
        )
    }

    private fun uploadDetails() {
        val alertDialog = AlertDialog.Builder(context!!)
            .setMessage("Are you sure you want to upload the details?")
            .setPositiveButton("Yes") { _, _ ->

                setAlarm()
                leadManager.setAssigner(user.userName, user.uId)
                leadManager.addCallerRemarks()
                leadManager.setWorkTime()

                showProgressDialog("Uploading...", context!!)
                if (currentLeadDetails == null) {
                    firestore.uploadCustomerDetails(
                        onUploadCustomerDetails(),
                        leadManager.getLead()
                    )
                } else {
                    firestore.updateLeadDetails(object : FirestoreInterfaces.OnUpdateLead {
                        override fun onFailer() {
                            if (progress!!.isShowing) {
                                progress?.dismiss()
                            }
                            showToastMessage(R.string.update_fail)
                            onDestroy()
                        }

                        override fun onLeadUpdated() {
                            if (progress!!.isShowing) {
                                progress?.dismiss()
                            }
                            showToastMessage(R.string.updated)
                        }

                    }, leadManager.lead)
                }
            }

            .setNegativeButton("No", null)
            .setCancelable(false)
            .show()

        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun setAlarm() {
        val alarm = Alarm(context!!)

        if (binding.date.text.toString() != "DD/MM/YYYY" && binding.time.text.toString() != "hh:mm"
        ) {
            val c = Calendar.getInstance()

            c.set(Calendar.DAY_OF_MONTH, alarmDay)
            c.set(Calendar.MONTH, alarmMonth)
            c.set(Calendar.YEAR, alarmYear)
            c.set(Calendar.HOUR_OF_DAY, alarmHour)
            c.set(Calendar.MINUTE, alarmMinute)
            c.set(Calendar.SECOND, 0)

            alarm.startAlarm(c, leadManager.customerName)
        }
    }

    private fun onUploadCustomerDetails(): FirestoreInterfaces.OnUploadCustomerDetails {
        return object : FirestoreInterfaces.OnUploadCustomerDetails {
            override fun onDataUploaded() {
                progress?.dismiss()
                onDestroy()

                showToastMessage(R.string.data_uploaded)
                if ((user.userType!!.contains("Telecaller") && user.userType!!.contains("Assigner")) ||
                    leadManager.forwarderUId == null
                ) {
                    if (leadManager.status != "Incomplete Lead")
                        startSMSIntent()
                }
            }

            override fun failedToUpload() {
                progress!!.dismiss()
                showToastMessage(R.string.failed_to_upload)
            }
        }
    }

    private fun startSMSIntent() {
        val preference = ProfileSP(context!!)
        val currentUserNumber = preference.contactNumber

        val currentUserFirstName =
            leadManager.assignerName.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
        val assigneeUserFirstName =
            leadManager.assigneeName.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()

        val uri = Uri.parse("smsto:${leadManager.customerContact}")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra(
            "sms_body", "Thanks for connecting mudrahome.com. You were speaking with " +
                    currentUserFirstName[0] + " " + currentUserNumber + ". " + assigneeUserFirstName[0] + " " +
                    assigneeContact + " will connect you for further processing your loan."
        )
        startActivity(intent)
    }

    protected var progress: ProgressDialog? = null

    private fun showProgressDialog(
        message: String?,
        context: Context?
    ) {
        progress = ProgressDialog(context)
        progress?.setMessage(message)
        progress?.setCancelable(false)
        progress?.setCanceledOnTouchOutside(false)
        progress?.show()
    }

    private fun dismissProgressDialog() {
        if (progress!!.isShowing()) progress?.dismiss()
    }

    private fun showToastMessage(message: Int) {
        Toast.makeText(context!!, message, Toast.LENGTH_SHORT).show()
    }

    private fun isNetworkConnected(): Boolean {
        val cm =
            context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null
    }
}