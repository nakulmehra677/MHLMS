package com.mudrahome.mhlms.activities

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import com.mudrahome.mhlms.R
import com.mudrahome.mhlms.firebase.Firestore
import com.mudrahome.mhlms.interfaces.FirestoreInterfaces
import com.mudrahome.mhlms.managers.Alarm
import com.mudrahome.mhlms.managers.TimeManager
import com.mudrahome.mhlms.model.LeadDetails
import com.mudrahome.mhlms.model.TimeModel
import com.mudrahome.mhlms.model.UserDetails
import com.mudrahome.mhlms.sharedPreferences.UserDataSharedPreference
import java.util.*


class FeedCustomerDetailsActivity : BaseActivity(), AdapterView.OnItemSelectedListener {

    private var name: EditText? = null
    private var contact: EditText? = null
    private var amount: EditText? = null
    private var remarks: EditText? = null

    private var propertyTypeAdapter: ArrayAdapter<CharSequence>? = null
    private var loanTypeAdapter: ArrayAdapter<CharSequence>? = null
    private var locationAdapter: ArrayAdapter<CharSequence>? = null
    private var assignedToAdapter: ArrayAdapter<CharSequence>? = null
    private var loanType: Spinner? = null
    private var location: Spinner? = null
    private var propertyType: Spinner? = null
    private var assignTo: Spinner? = null

    private var selfEmploymentLayout: LinearLayout? = null
    private var selfEmploymentTypeRadioGroup: RadioGroup? = null
    private var propertyTypeLayout: LinearLayout? = null

    private var date: TextView? = null
    private var time: TextView? = null


    private var strName = "None"
    private var strContact = "None"
    private var strAmount = "None"
    private var strEmployment = "None"
    private var strEmploymentType = "None"
    private var strLoanType = "None"
    private var strPropertyType = "None"
    private var strLocation = "None"
    private var strAssignTo = "None"
    private var strAssignToUId = "None"
    private var strAssigneeContact = "None"
    private var strAssignerContact = "None"

    private var salesPersonList: List<UserDetails>? = null
    private var strRemarks = ArrayList<String>()
    private var mYear: Int = 0
    private var mMonth: Int = 0
    private var mDay: Int = 0
    private var mHour: Int = 0
    private var mMinute: Int = 0
    private var alarmYear: Int = 0
    private var alarmMonth: Int = 0
    private var alarmDay: Int = 0
    private var alarmHour: Int = 0
    private var alarmMinute: Int = 0


    private var firestore: Firestore? = null
    private var leadDetails: LeadDetails? = null
    private var userType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_customer_details)

        name = findViewById(R.id.name)
        contact = findViewById(R.id.contact_number)
        amount = findViewById(R.id.loan_amount)
        loanType = findViewById(R.id.loan_type)
        remarks = findViewById(R.id.remarks)
        assignTo = findViewById(R.id.assign_to)
        location = findViewById(R.id.location)
        val locationLayout: LinearLayout = findViewById(R.id.location_layout)
        val assignToLayout: LinearLayout = findViewById(R.id.assign_to_layout)
        val remarksLayout: TextView = findViewById(R.id.remarks)
        propertyType = findViewById(R.id.property_type)
        propertyTypeLayout = findViewById(R.id.property_type_layout)
        date = findViewById(R.id.date)
        time = findViewById(R.id.time)

        selfEmploymentLayout = findViewById(R.id.self_employement_layout)
        selfEmploymentTypeRadioGroup = findViewById(R.id.self_employment_type_radio_group)

        firestore = Firestore(this)
        val preference = UserDataSharedPreference(this)

        userType = preference.userType


        if (userType.equals(getString(R.string.telecaller))) {
            initializeLocationSpinner()
        } else {
            locationLayout.visibility = View.GONE
            assignToLayout.visibility = View.GONE
            remarksLayout.visibility = View.GONE

            strLocation = preference.location.iterator().next()
            strAssignTo = "Not assigned yet"
            strAssignToUId = "Not assigned yet"
            strAssigneeContact = "Not assigned yet"
        }
        initializeLoanTypeSpinner()
    }

    private fun initializeLoanTypeSpinner() {
        // Loan type Spinner
        loanTypeAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.loan_type, android.R.layout.simple_spinner_item
        )
        loanTypeAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        loanType?.adapter = loanTypeAdapter
        loanType?.onItemSelectedListener = this
    }

    private fun initializeLocationSpinner() {
        // Location Spinner
        locationAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.location, android.R.layout.simple_spinner_item
        )
        locationAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        location?.adapter = locationAdapter
        location?.onItemSelectedListener = this
    }

    private fun initializePropertyTypeSpinner() {
        // Property type Spinner
        if (strLoanType == "Home Loan") {
            propertyTypeAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.property, android.R.layout.simple_spinner_item
            )
        } else {
            propertyTypeAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.property_type, android.R.layout.simple_spinner_item
            )
        }

        propertyTypeAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        propertyType?.adapter = propertyTypeAdapter
        propertyType?.onItemSelectedListener = this
    }

    fun onRadioButtonClicked(view: View) {
        val checked = (view as RadioButton).isChecked

        when (view.getId()) {
            R.id.salaried -> {
                if (checked) {
                    strEmployment = "Salaried"
                    selfEmploymentLayout?.visibility = View.GONE
                    selfEmploymentTypeRadioGroup?.clearCheck()
                    strEmploymentType = "None"
                }
            }

            R.id.self_employed -> if (checked) {
                strEmployment = "Self Employed"
                selfEmploymentLayout?.visibility = View.VISIBLE
            }

            R.id.partnership_firm -> {
                if (checked) {
                    strEmploymentType = "Partnership Firm"
                }
            }
            R.id.private_limited_company -> {
                if (checked) {
                    strEmploymentType = "Private Limited Company"
                }
            }
            R.id.proprietorship_firm -> if (checked) {
                strEmploymentType = "Proprietorship Firm"
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        when (parent.id) {
            R.id.loan_type -> {
                strLoanType = parent.getItemAtPosition(position).toString()
                if (strLoanType == "Home Loan" || strLoanType == "Loan Against Property") {
                    initializePropertyTypeSpinner()

                    propertyTypeLayout?.visibility = View.VISIBLE
                } else {
                    propertyTypeLayout?.visibility = View.GONE
                    strPropertyType = "None"
                }
            }

            R.id.property_type -> {
                strPropertyType = parent.getItemAtPosition(position).toString()
            }

            R.id.location -> {
                strLocation = parent.getItemAtPosition(position).toString()

                //assignedToSpinner.setEnabled(false);
                //assignedToSpinner.setClickable(false);

                if (isNetworkConnected) {
                    getSalesmanListByLocation()
                } else {
                    showToastMessage(R.string.no_internet)
                    initializeLocationSpinner()
                }
            }

            R.id.assign_to -> {
                strAssignTo = parent.getItemAtPosition(position).toString()
                for (user in this.salesPersonList!!) {
                    if (user.userName == strAssignTo) {
                        strAssignToUId = user.getuId()
                        strAssigneeContact = user.contactNumber
                    }
                }
            }
            else -> {
            }
        }
    }

    private fun getDetails() {

        strName = name?.text.toString().trim()
        strContact = contact?.text.toString().trim()
        strAmount = amount?.text.toString().trim()

        /*if (!remarks.getText().toString().isEmpty()) {
            strRemarks.add(remarks.getText().toString().trim());
        }*/

    }

    private fun checkEmpty(): Boolean {
        if (strName.isEmpty() || strContact.length != 10 || strAmount.isEmpty() ||
            strAssignTo == "None" || strEmployment == "None"
        ) {
            return false
        }
        if (strEmployment == "Self Employed") {
            if (strEmploymentType == "None")
                return false
        }
        return strAssignTo != "None"
    }

    fun onButtonClicked(view: View) {
        val c = Calendar.getInstance()
        when (view.id) {
            R.id.upload_details -> if (isNetworkConnected) {
                getDetails()
                if (checkEmpty()) {
                    uploadDetails()
                } else
                    showToastMessage(R.string.fill_details_correctly)
            } else
                showToastMessage(R.string.no_internet)

            R.id.date_picker -> {
                // Get Current Date
                mYear = c.get(Calendar.YEAR)
                mMonth = c.get(Calendar.MONTH)
                mDay = c.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(
                    this,
                    { view1, year, monthOfYear, dayOfMonth ->

                        alarmDay = dayOfMonth
                        alarmMonth = monthOfYear
                        alarmYear = year

                        date?.text = dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year

                    }, mYear, mMonth, mDay
                )

                datePickerDialog.datePicker.minDate = c.timeInMillis
                datePickerDialog.show()
            }

            R.id.time_picker -> {
                // Get Current TimeModel
                mHour = c.get(Calendar.HOUR_OF_DAY)
                mMinute = c.get(Calendar.MINUTE)

                // Launch TimeModel Picker Dialog
                val timePickerDialog = TimePickerDialog(
                    this,
                    { view12, hourOfDay, minute ->

                        alarmHour = hourOfDay
                        alarmMinute = minute

                        time?.text = "$hourOfDay:$minute"
                    }, mHour, mMinute, false
                )
                timePickerDialog.show()
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun getSalesmanListByLocation() {
        /*progress = new ProgressDialog(t.khis);
        progress.setMessage("Loading..");
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();*/

        firestore?.fetchUsersByUserType(
            onFetchUsersListListener(), strLocation, getString(R.string.salesman)
        )
    }

    private fun onFetchUsersListListener(): FirestoreInterfaces.OnFetchUsersList {
        return FirestoreInterfaces.OnFetchUsersList { userList ->
            salesPersonList = userList.userList

            if ((salesPersonList as MutableList<UserDetails>?)?.size !== 0) {
                val salesPersonNameList = ArrayList<String>()
                for (user in (salesPersonList as MutableList<UserDetails>?)!!) {
                    salesPersonNameList.add(user.userName)
                }

                // AssignedTo Spinner
                assignedToAdapter = ArrayAdapter<CharSequence>(
                    this@FeedCustomerDetailsActivity,
                    android.R.layout.simple_spinner_item, salesPersonNameList as List<CharSequence>
                )
                assignedToAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                assignTo?.adapter = assignedToAdapter
                assignTo?.onItemSelectedListener = this@FeedCustomerDetailsActivity

                assignTo?.isEnabled = true
                assignTo?.isClickable = true

                //progress.dismiss();
            } else {
                strAssignTo = "None"
                assignTo?.isEnabled = false
                assignTo?.isClickable = false
            }
        }
    }

    private fun uploadDetails() {
        val alertDialog = AlertDialog.Builder(this)
            .setMessage("Are you sure you want to upload the details?")
            .setPositiveButton("Yes") { dialog, which ->

                if (userType.equals(getString(R.string.telecaller)))
                    strRemarks.add(remarks?.text.toString().trim { it <= ' ' } + "@@" + System.currentTimeMillis())              // remarks with time steamp
                else
                    strRemarks.add("Not available")


                val timeManager = TimeManager()
                val timeModel = timeManager.time
                makeObject(timeModel)

                setAlarm()

                progress = ProgressDialog(this@FeedCustomerDetailsActivity)
                progress.setMessage("Uploading..")
                progress.setCancelable(false)
                progress.setCanceledOnTouchOutside(false)
                progress.show()

                firestore?.uploadCustomerDetails(onUploadCustomerDetails(), leadDetails)
            }

            // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton("No", null)
            .setCancelable(false)
            .show()

        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun makeObject(timeModel: TimeModel) {

        val preference = UserDataSharedPreference(this)

        val currentUserName = preference.userName
        val currentUserNumber = preference.contactNumber
        val currentUserUId = preference.userUid
        var status: String? = null
        var assignDate: String? = null
        var assignTime: String? = null
        var userName: String? = null

        if (userType.equals(getString(R.string.telecaller))) {
            status = "Active"
            assignDate = timeModel.date
            assignTime = timeModel.time
            userName = currentUserName
        } else {
            status = "Not assigned yet"
            assignDate = "Not assigned yet"
            assignTime = "Not assigned yet"
            userName = "Not assigned yet"
        }

        leadDetails = LeadDetails(
            strName, strContact, currentUserNumber,
            strAssigneeContact, strAmount, strEmployment, strEmploymentType, strLoanType,
            strPropertyType, strLocation, strRemarks, timeModel.date, strAssignTo,
            status, userName, strAssignToUId,
            currentUserUId, timeModel.time, assignDate,
            assignTime, timeModel.timeStamp
        )
    }

    private fun setAlarm() {
        val alarm = Alarm(this)

        if (!date?.text.toString().equals("DD/MM/YYYY") && !time?.text.toString().equals(
                "hh:mm"
            )
        ) {

            val c = Calendar.getInstance()

            c.set(Calendar.DAY_OF_MONTH, alarmDay)
            c.set(Calendar.MONTH, alarmMonth)
            c.set(Calendar.YEAR, alarmYear)
            c.set(Calendar.HOUR_OF_DAY, alarmHour)
            c.set(Calendar.MINUTE, alarmMinute)
            c.set(Calendar.SECOND, 0)

            alarm.startAlarm(c, leadDetails?.name)
        }
    }

    private fun onUploadCustomerDetails(): FirestoreInterfaces.OnUploadCustomerDetails {
        return object : FirestoreInterfaces.OnUploadCustomerDetails {
            override fun onDataUploaded() {
                progress.dismiss()
                finish()
                showToastMessage(R.string.data_uploaded)
                if (userType.equals(getString(R.string.telecaller)))
                    startSMSIntent()
            }

            override fun failedToUpload() {
                progress.dismiss()
                showToastMessage(R.string.failed_to_upload)
            }
        }
    }

    private fun startSMSIntent() {
        val preference = UserDataSharedPreference(this)

        val currentUserName = preference.userName
        val currentUserNumber = preference.contactNumber

        val currentUserFirstName =
            currentUserName.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val assigneeUserFirstName =
            strAssignTo.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val uri = Uri.parse("smsto:$strContact")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra(
            "sms_body", "Thanks for connecting mudrahome.com. You were speaking with " +
                    currentUserFirstName[0] + " " + currentUserNumber + ". " + assigneeUserFirstName[0] + " " +
                    strAssigneeContact + " will connect you for further processing your loan."
        )
        startActivity(intent)
    }
}