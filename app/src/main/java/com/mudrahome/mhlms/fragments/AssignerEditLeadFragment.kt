package com.mudrahome.mhlms.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.mudrahome.mhlms.R
import com.mudrahome.mhlms.databinding.FragmentAssignerEditLeadDetailsBinding
import com.mudrahome.mhlms.interfaces.OnSubmitClickListener
import com.mudrahome.mhlms.managers.Alarm
import com.mudrahome.mhlms.managers.UpdateLead
import com.mudrahome.mhlms.model.LeadDetails
import com.mudrahome.mhlms.model.UserDetails
import java.util.*
import kotlin.collections.ArrayList

class AssignerEditLeadFragment(
    private val leadDetails: LeadDetails,
    private val salesPersonList: List<UserDetails>?,
    private val listener: OnSubmitClickListener
) : AppCompatDialogFragment() {

    private lateinit var binding: FragmentAssignerEditLeadDetailsBinding

    private var strAssignedTo: String? = null
    private var assignedToAdapter: ArrayAdapter<CharSequence>? = null

    private var strCustomerRemarks: String? = null
    private var customerRemarksAdapter: ArrayAdapter<CharSequence>? = null

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

    @SuppressLint("RestrictedApi")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(activity)
        val view: View =
            inflater.inflate(R.layout.fragment_assigner_edit_lead_details_, null, false)

        binding = FragmentAssignerEditLeadDetailsBinding.bind(view)
        val builder = AlertDialog.Builder(activity!!)

        binding.customerName.setText(leadDetails.name)
        binding.loanAmount.setText(leadDetails.loanAmount)
        binding.contactNumber.setText(leadDetails.contactNumber)

        if (leadDetails.salesmanRemarks == null) {
            binding.reminderLayout.visibility = View.GONE
            binding.date.text = "DD/MM/YYYY"
            binding.time.text = "hh:mm"
        } else if (leadDetails.salesmanRemarks == "Customer Interested but Document Pending" || leadDetails.salesmanRemarks == "Customer follow Up") {
            binding.reminderLayout.visibility = View.VISIBLE
        } else {
            binding.reminderLayout.visibility = View.GONE
            binding.date.text = "DD/MM/YYYY"
            binding.time.text = "hh:mm"
        }

        val salesPersonNameList = ArrayList<CharSequence>()
        Log.d("usserrr", salesPersonList!!.size.toString())

        for (user in salesPersonList) {
            salesPersonNameList.add(user.userName!!)
        }

        // AssignedTo Spinner
        assignedToAdapter = ArrayAdapter(
            context!!,
            android.R.layout.simple_spinner_item, salesPersonNameList
        )
        assignedToAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.assignTo.adapter = assignedToAdapter
        binding.assignTo.setSelection(salesPersonNameList.indexOf(leadDetails.assignedTo!!))
        binding.assignTo.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View,
                position: Int, id: Long
            ) {
                strAssignedTo = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Customer Remarks Spinner
        customerRemarksAdapter = ArrayAdapter.createFromResource(
            context!!,
            R.array.remarks, android.R.layout.simple_spinner_item
        )
        customerRemarksAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.customerRemarks.adapter = customerRemarksAdapter
        binding.customerRemarks.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View,
                position: Int, id: Long
            ) {
                strCustomerRemarks = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.datePicker.setOnClickListener {
            // Get Current Date
            val c = Calendar.getInstance()
            mYear = c[Calendar.YEAR]
            mMonth = c[Calendar.MONTH]
            mDay = c[Calendar.DAY_OF_MONTH]
            val datePickerDialog = DatePickerDialog(
                context!!,
                OnDateSetListener { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    alarmDay = dayOfMonth
                    alarmMonth = monthOfYear
                    alarmYear = year
                    binding.date.text = dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                }, mYear, mMonth, mDay
            )
            datePickerDialog.datePicker.minDate = c.timeInMillis
            datePickerDialog.show()
        }
        binding.timePicker.setOnClickListener {
            // Get Current TimeModel

            val c = Calendar.getInstance()
            mHour = c[Calendar.HOUR_OF_DAY]
            mMinute = c[Calendar.MINUTE]
            // Launch TimeModel Picker Dialog
            val timePickerDialog = TimePickerDialog(
                context,
                OnTimeSetListener { _: TimePicker?, hourOfDay: Int, minute: Int ->
                    alarmHour = hourOfDay
                    alarmMinute = minute
                    binding.time.text = "$hourOfDay:$minute"
                }, mHour, mMinute, false
            )
            timePickerDialog.show()
        }

        builder.setView(view)
            .setTitle("Edit details")
            .setPositiveButton(
                "Make changes"
            ) { _: DialogInterface?, _: Int ->
                val strName = binding.customerName.text.toString()
                val strLoanAmount = binding.loanAmount.text.toString()
                val strNumber = binding.contactNumber.text.toString()
                val strReason = leadDetails.forwarderRemarks
                strReason.add(binding.assignerRemarks.text.toString() + "@@" + System.currentTimeMillis())

                if (strName.isNotEmpty() &&
                    strLoanAmount.isNotEmpty() &&
                    strNumber.isNotEmpty()
                ) {
                    val updateLead = UpdateLead(leadDetails)
                    updateLead.updateByAssigner(strName, strLoanAmount, strNumber, strReason)
                    val alarm = Alarm(context)
                    if (leadDetails.salesmanRemarks != null) {
                        if (leadDetails.salesmanRemarks == "Customer Interested but Document Pending" || leadDetails.salesmanRemarks == "Customer follow Up") {

                            binding.reminderLayout.visibility = View.VISIBLE
//                            binding.bankLayout.setVisibility(View.GONE)
                            if (binding.date.text.toString() != "DD/MM/YYYY" &&
                                binding.time.text.toString() != "hh:mm"
                            ) {
                                val c = Calendar.getInstance()
                                c[Calendar.DAY_OF_MONTH] = alarmDay
                                c[Calendar.MONTH] = alarmMonth
                                c[Calendar.YEAR] = alarmYear
                                c[Calendar.HOUR_OF_DAY] = alarmHour
                                c[Calendar.MINUTE] = alarmMinute
                                c[Calendar.SECOND] = 0
                                alarm.startAlarm(c, leadDetails.name)
                            }
                        } else {
                            binding.reminderLayout.visibility = View.GONE
//                            binding.bankLayout.setVisibility(View.VISIBLE)
                            alarm.cancelAlarm(leadDetails.name)
                        }
                    }
                    updateLead.time()
                    listener.onSubmitClicked(updateLead.leadDetails)
                }
            }
            .setNegativeButton(
                "Cancel"
            ) { _: DialogInterface?, _: Int -> }.setCancelable(false)
        return builder.create()
    }
}