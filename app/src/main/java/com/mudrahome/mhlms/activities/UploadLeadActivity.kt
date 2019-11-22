package com.mudrahome.mhlms.activities

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.mudrahome.mhlms.R
import com.mudrahome.mhlms.databinding.ActivityUploadLeadBinding
import com.mudrahome.mhlms.firebase.Firestore
import com.mudrahome.mhlms.interfaces.FirestoreInterfaces
import com.mudrahome.mhlms.managers.Alarm
import com.mudrahome.mhlms.managers.TimeManager
import com.mudrahome.mhlms.model.LeadDetails
import com.mudrahome.mhlms.model.UserDetails
import com.mudrahome.mhlms.model.UserList
import com.mudrahome.mhlms.sharedPreferences.UserDataSharedPreference
import java.util.*


class UploadLeadActivity : BaseActivity(), AdapterView.OnItemSelectedListener {

    private var propertyTypeAdapter: ArrayAdapter<CharSequence>? = null
    private var loanTypeAdapter: ArrayAdapter<CharSequence>? = null
    private var locationAdapter: ArrayAdapter<CharSequence>? = null
    private var assignedToAdapter: ArrayAdapter<CharSequence>? = null

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

    private var binding: ActivityUploadLeadBinding? = null
    private var firestore: Firestore? = null
    private var leadDetails: LeadDetails? = null
    private var userType: String? = null

    private var AUTOCOMPLETE_REQUEST_CODE = 1
    private var country = "IN"


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_upload_lead)

        firestore = Firestore(this)
        val preference = UserDataSharedPreference(this)

        userType = preference.userType
        leadDetails = LeadDetails()
        initializeLoanTypeSpinner()
        initializeLocationSpinner()

        if (userType.equals(getString(R.string.telecaller)) ||
            userType.equals(getString(R.string.telecaller_and_teleassigner))
        ) {


        } else if (userType.equals(getString(R.string.business_associate))) {
            binding!!.assignToText.text = "Send to"
            binding!!.remarks.visibility = View.GONE
            binding!!.reminderLayout.visibility = View.GONE


        } else {
            finish()
        }


    }

    private fun initializeLoanTypeSpinner() {
        // Loan type Spinner
        loanTypeAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.loan_type, android.R.layout.simple_spinner_item
        )
        loanTypeAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.loanType?.adapter = loanTypeAdapter
        binding!!.loanType?.onItemSelectedListener = this
    }

    private fun initializeLocationSpinner() {
        // Location Spinner
        if(userType.equals(getString(R.string.business_associate))){

            locationAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.locationOthers, android.R.layout.simple_spinner_item
            )
        }else{
            locationAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.location, android.R.layout.simple_spinner_item
            )
        }


        locationAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.location?.adapter = locationAdapter
        binding!!.location?.onItemSelectedListener = this
    }

    private fun initializePropertyTypeSpinner() {
        // Property type Spinner
        if (leadDetails?.loanType == "Home Loan") {
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
        binding!!.propertyType?.adapter = propertyTypeAdapter
        binding!!.propertyType?.onItemSelectedListener = this
    }

    private fun initializeAssignToSpinner(userName: List<String>) {
        // AssignedTo Spinner
        assignedToAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item, userName
        )

        assignedToAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding!!.assignTo?.adapter = assignedToAdapter
        binding!!.assignTo?.onItemSelectedListener = this@UploadLeadActivity

        binding!!.assignTo?.isEnabled = true
        binding!!.assignTo?.isClickable = true
    }

    /*private fun initializePlaceClient(){

        if (!com.google.android.libraries.places.api.Places.isInitialized()) {
            com.google.android.libraries.places.api.Places.initialize(
                applicationContext,
                getString(R.string.google_key_api)
            )
        }
    }*/

    fun onRadioButtonClicked(view: View) {
        val checked = (view as RadioButton).isChecked

        when (view.getId()) {
            R.id.salaried -> {
                if (checked) {
                    leadDetails?.employment = "Salaried"
                    binding!!.selfEmployementLayout?.visibility = View.GONE
                    binding!!.selfEmploymentTypeRadioGroup?.clearCheck()
                    leadDetails?.employmentType = null
                }
            }

            R.id.self_employed -> if (checked) {
                leadDetails?.employment = "Self Employed"
                binding!!.selfEmployementLayout?.visibility = View.VISIBLE
            }

            R.id.partnership_firm -> {
                if (checked) {
                    leadDetails?.employmentType = "Partnership Firm"
                }
            }
            R.id.private_limited_company -> {
                if (checked) {
                    leadDetails?.employmentType = "Private Limited Company"
                }
            }
            R.id.proprietorship_firm -> if (checked) {
                leadDetails?.employmentType = "Proprietorship Firm"
            }
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        when (parent.id) {
            R.id.loan_type -> {
                leadDetails?.loanType = parent.getItemAtPosition(position).toString()
                if (leadDetails?.loanType == "Home Loan" || leadDetails?.loanType == "Loan Against Property") {
                    initializePropertyTypeSpinner()

                    binding!!.propertyTypeLayout?.visibility = View.VISIBLE
                } else {
                    binding!!.propertyTypeLayout?.visibility = View.GONE
                    leadDetails?.propertyType = null
                }
            }

            R.id.property_type -> {
                leadDetails?.propertyType = parent.getItemAtPosition(position).toString()
            }

            R.id.location -> {
                leadDetails?.location = parent.getItemAtPosition(position).toString()

                if (isNetworkConnected) {
                    if (userType.equals(getString(R.string.business_associate))) {
                        checkforOthersLocation()
                    } else {
                        getSalesmanListByLocation()
                    }
                } else {
                    showToastMessage(R.string.no_internet)
                    initializeLocationSpinner()
                }
            }

            R.id.assign_to -> {
                val name = parent.getItemAtPosition(position).toString()
                Log.d("sdv", name)
                for (user in this.personList!!) {
                    if (user.userName == name) {
                        if (userType.equals(getString(R.string.telecaller_and_teleassigner)) ||
                            userType.equals(getString(R.string.telecaller))
                        ) {
                            leadDetails?.assignedToUId = user.getuId()
                            assigneeContact = user.contactNumber
                            leadDetails?.assignedTo = name
                        } else {
                            leadDetails?.assignerUId = user.getuId()
                            leadDetails?.assigner = name
                        }
                    }
                }
            }
            else -> {
            }
        }
    }

    private fun checkforOthersLocation() {
        if (leadDetails?.location.equals("Others")) {
            binding!!.teleassignerOtherLocation.visibility = View.VISIBLE
            binding!!.assignToLayout.visibility = View.GONE
            leadDetails!!.assigner = null
            leadDetails!!.assignerUId = null
            /*initializePlaceClient()*/
        } else {
            binding!!.teleassignerOtherLocation.visibility = View.GONE
            binding!!.assignToLayout.visibility = View.VISIBLE
            getCallersListByLocation()
        }
    }

    private fun setDetails() {
        val timeModel = TimeManager()

        leadDetails?.name = binding!!.name.text.toString().trim()
        leadDetails?.contactNumber = binding!!.contactNumber.text.toString().trim()
        leadDetails?.loanAmount = binding!!.loanAmount.text.toString().trim()
        leadDetails?.timeStamp = timeModel.timeStamp
        if (binding!!.teleassignerOtherLocation.visibility == View.VISIBLE)
            leadDetails?.location = binding!!.teleassignerOtherLocation.text.toString()

        val preference = UserDataSharedPreference(this)

        if (userType.equals(getString(R.string.telecaller)) ||
            userType.equals(getString(R.string.telecaller_and_teleassigner))
        ) {
            leadDetails?.status = "Active"
            leadDetails?.assignDate = timeModel.strDate
            leadDetails?.assignTime = timeModel.strTime
            leadDetails?.assigner = preference.userName
            leadDetails?.assignerUId = preference.userUid
            leadDetails?.businessAssociateUploader = false
            leadDetails!!.telecallerRemarks.clear()
            if (!binding!!.remarks.text.toString().trim().isEmpty()) {
                leadDetails?.telecallerRemarks?.add(
                    binding!!.remarks.text.toString().trim { it <= ' ' } +
                            "@@" + System.currentTimeMillis())
            }
        } else {
            leadDetails?.businessAssociateUid = preference.userUid
            leadDetails?.businessAssociateUploader = true
            leadDetails?.assigner = "Dipa Meena"
            leadDetails?.assignerUId = "hwn8jj23f8VGmnesvAkhDZsORl52"
        }
    }

    private fun checkEmpty(): Boolean {
        if (leadDetails?.name!!.isEmpty() ||
            leadDetails?.contactNumber?.length != 10 ||
            leadDetails?.loanAmount!!.isEmpty() ||
            leadDetails?.employment == null
        ) {
            return false
        }
        if (leadDetails?.employment == "Self Employed") {
            if (leadDetails?.employmentType == null) {
                return false
            }
        }
        if (userType.equals(getString(R.string.business_associate))) {
            if (binding!!.teleassignerOtherLocation.visibility == View.VISIBLE) {
                if (binding!!.teleassignerOtherLocation.text.toString().isNullOrEmpty()) {
                    return false
                }
            } else {
                if (leadDetails?.assigner == null) {
                    return false
                }
            }
        } else {
            if (leadDetails?.assignedTo == null ||
                leadDetails!!.telecallerRemarks.size == 0
            ) {
                return false
            }
        }
        return true
    }

    @SuppressLint("SetTextI18n")
    fun onButtonClicked(view: View) {
        val c = Calendar.getInstance()
        when (view.id) {
            R.id.upload_details ->
                if (isNetworkConnected) {
                    setDetails()
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
                    { _, year, monthOfYear, dayOfMonth ->

                        alarmDay = dayOfMonth
                        alarmMonth = monthOfYear
                        alarmYear = year

                        binding!!.date.text =
                            dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year

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

                        binding!!.time?.text = "$hourOfDay:$minute"
                    }, mHour, mMinute, false
                )
                timePickerDialog.show()
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    private fun getCallersListByLocation() {
        firestore!!.fetchUsersByUserType(
            object : FirestoreInterfaces.OnFetchUsersList {
                override fun onFail() {
                    showToastMessage(R.string.error_occur)
                }

                override fun onListFetched(userList: UserList?) {
                    personList = userList?.userList

                    if (userList?.userList?.size!! > 0) {
                        val callerNameList = ArrayList<String>()
                        for (user in userList.userList) {
                            callerNameList.add(user.userName)
                        }
                        initializeAssignToSpinner(callerNameList)
                    } else {
                        binding!!.assignTo.isEnabled = false
                        leadDetails?.assigner = null
                        leadDetails?.assignerUId = null
                    }
                }


            }, leadDetails?.location!!, getString(R.string.teleassigner)
        )
    }

    private fun getSalesmanListByLocation() {
        /*progress = new ProgressDialog(t.khis);
        progress.setMessage("Loading..");
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();*/

        firestore?.fetchUsersByUserType(
            object : FirestoreInterfaces.OnFetchUsersList {
                override fun onFail() {
                    showToastMessage(R.string.error_occur)
                }

                override fun onListFetched(userList: UserList?) {
                    personList = userList?.userList
                    Log.d("UserList",personList.toString())
                    if (userList?.userList?.size!! > 0) {
                        val salesNameList = ArrayList<String>()
                        for (user in userList.userList) {
                            salesNameList.add(user.userName)
                        }
                        initializeAssignToSpinner(salesNameList)

                    } else {
                        binding!!.assignTo.isEnabled = false
                        leadDetails?.assignedToUId = null
                        leadDetails?.assignedTo = null
                    }
                }
            }, leadDetails?.location!!, getString(R.string.salesman)
        )
    }

    private fun uploadDetails() {
        val alertDialog = AlertDialog.Builder(this)
            .setMessage("Are you sure you want to upload the details?")
            .setPositiveButton("Yes") { dialog, which ->

                setAlarm()

                progress = ProgressDialog(this@UploadLeadActivity)
                progress.setMessage("Uploading..")
                progress.setCancelable(false)
                progress.setCanceledOnTouchOutside(false)
                progress.show()

                firestore!!.uploadCustomerDetails(onUploadCustomerDetails(), leadDetails!!)
            }

            // A null listener allows the button to dismiss the dialog and take no further action.
            .setNegativeButton("No", null)
            .setCancelable(false)
            .show()

        alertDialog.setCanceledOnTouchOutside(false)
    }

    private fun setAlarm() {
        val alarm = Alarm(this)

        if (!binding!!.date.text.toString().equals("DD/MM/YYYY") && !binding!!.time.text.toString().equals(
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
                if (userType.equals(getString(R.string.telecaller_and_teleassigner)))
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
        val currentUserNumber = preference.contactNumber

        val currentUserFirstName =
            leadDetails!!.assigner!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()
        val assigneeUserFirstName =
            leadDetails!!.assignedTo!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
                .toTypedArray()

        val uri = Uri.parse("smsto:${leadDetails?.contactNumber}")
        val intent = Intent(Intent.ACTION_SENDTO, uri)
        intent.putExtra(
            "sms_body", "Thanks for connecting mudrahome.com. You were speaking with " +
                    currentUserFirstName[0] + " " + currentUserNumber + ". " + assigneeUserFirstName[0] + " " +
                    assigneeContact + " will connect you for further processing your loan."
        )
        startActivity(intent)
    }

    /*fun getLocation() {
        val fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
            .setCountry(country)
            .build(this@UploadLeadActivity)

        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
    }*/

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val place = Autocomplete.getPlaceFromIntent(data)
                    Toast.makeText(this,place.name,Toast.LENGTH_LONG).show()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }*/
}