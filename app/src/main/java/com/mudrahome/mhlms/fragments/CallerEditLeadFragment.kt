package com.mudrahome.mhlms.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.mudrahome.mhlms.R
import com.mudrahome.mhlms.databinding.FragmentCallerEditLeadDetailsBinding
import com.mudrahome.mhlms.interfaces.OnSubmitClickListener
import com.mudrahome.mhlms.managers.Alarm
import com.mudrahome.mhlms.managers.UpdateLead
import com.mudrahome.mhlms.model.LeadDetails
import java.util.*


@SuppressLint("ValidFragment")
class CallerEditLeadFragment(
    private val leadDetails: LeadDetails,
    private val listener: OnSubmitClickListener
) : AppCompatDialogFragment() {

    private lateinit var binding: FragmentCallerEditLeadDetailsBinding

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
        val view: View = inflater.inflate(R.layout.fragment_caller_edit_lead_details_, null, false)

        binding = FragmentCallerEditLeadDetailsBinding.bind(view)
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
                val strReason = leadDetails.telecallerRemarks
                strReason.add(binding.callerRemakrs.text.toString() + "@@" + System.currentTimeMillis())

                if (strName.isNotEmpty() &&
                    strLoanAmount.isNotEmpty() &&
                    strNumber.isNotEmpty()
                ) {
                    val updateLead = UpdateLead(leadDetails)
                    updateLead.updateByCaller(strName, strLoanAmount, strNumber, strReason)
                    val alarm = Alarm(context)
                    if (leadDetails.salesmanRemarks != null) {
                        if (leadDetails.salesmanRemarks == "Customer Interested but Document Pending" || leadDetails.salesmanRemarks == "Customer follow Up") {
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