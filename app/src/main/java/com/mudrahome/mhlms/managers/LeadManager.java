package com.mudrahome.mhlms.managers;

import android.icu.lang.UProperty;
import android.util.Log;

import com.mudrahome.mhlms.R;
import com.mudrahome.mhlms.enums.UploadLeadEnum;
import com.mudrahome.mhlms.model.LeadDetails;

import java.util.ArrayList;

public class LeadManager {
    private LeadDetails leadDetails;
    private int userType;
    private String INCOMPLETE_LEAD = "Incomplete Lead";
    private String NOT_PROVIDED = "Not Provided";
    private String remarks = "";

    private String date = "DD/MM/YYYY", time = "hh:mm";

    public LeadManager(int userT̥ype) {
        this.userType = userType;

        leadDetails = new LeadDetails();
        leadDetails.setStatus(INCOMPLETE_LEAD);
        leadDetails.setEmployment(NOT_PROVIDED);
        leadDetails.setEmploymentType(NOT_PROVIDED);
    }

    public LeadManager(int userType, LeadDetails currentLeadDetails) {
        this(userType);
        if (currentLeadDetails != null) {
            leadDetails = currentLeadDetails;
        }
    }

    public Enum verifyLead() {
        if (!leadDetails.getStatus().equals(INCOMPLETE_LEAD)) {
            if (!verifyName()) {
                return UploadLeadEnum.FILL_NAME;
            }

            if (!verifyLoanAmount()) {
                return UploadLeadEnum.FILL_LOAN_AMOUNT;
            }

            if (!verifyEmployment()) {
                return UploadLeadEnum.SET_EMPLOYMENT;
            }

            if (!verifyEmploymentType()) {
                return UploadLeadEnum.SET_EMPLOYMENT_TYPE;
            }

            if (!verifyLoanType()) {
                return UploadLeadEnum.SET_LOAN_TYPE;
            }

            if (!verifyPropertyType()) {
                return UploadLeadEnum.SET_PROPERTY_TYPE;
            }

            if (!verifyLocation()) {
                return UploadLeadEnum.SET_LOCATION;
            }

            if (userType == R.string.telecaller_and_teleassigner) {
                if (!verifyAssignee()) {
                    return UploadLeadEnum.SET_ASSIGNEE;
                }
            } else {
                if (!verifyAssigner()) {
                    if (!verifyAssignee()) {
                        return UploadLeadEnum.SET_ASSIGNEE;
                    }
                }
            }
        } else {
            if (!verifyAlarm()) {
                return UploadLeadEnum.SET_ALARM;
            }
        }

        if (!verifyContact()) {
            return UploadLeadEnum.FILL_CONTACT;
        }

        if (!verifyRemarks()) {
            return UploadLeadEnum.FILL_CALLER_REMARKS;
        }

        return UploadLeadEnum.UPLOAD_LEAD;
    }

    public void setCallLater(String callLater) {
        if (callLater.equals("Yes")) {
            leadDetails.setStatus(INCOMPLETE_LEAD);
            setAssignee("Not Assigned", null);
            setForwarder("Not Assigned", null);
        } else {
            if (userType == R.string.telecaller_and_teleassigner || leadDetails.getForwarderUId() == null) {
//                if (!leadDetails.getStatus().equals("Incomplete Lead"))
                leadDetails.setStatus("Active");
                setAssignTime();
            } else {
//                if (!leadDetails.getStatus().equals("Incomplete Lead"))
                leadDetails.setStatus("Decision Pending");
            }
        }
    }

    private boolean verifyContact() {
        return leadDetails.getContactNumber() != null &&
                !leadDetails.getContactNumber().isEmpty() &&
                leadDetails.getContactNumber().length() == 10;
    }

    private boolean verifyName() {
        return !leadDetails.getName().isEmpty();
    }

    private boolean verifyLoanAmount() {
        return !leadDetails.getLoanAmount().isEmpty();
    }

    private boolean verifyEmployment() {
        return !leadDetails.getEmployment().equals(NOT_PROVIDED);
    }

    private boolean verifyEmploymentType() {
        if (leadDetails.getEmployment().equals("Self Employed"))
            return !leadDetails.getEmploymentType().equals(NOT_PROVIDED);
        return true;
    }

    private boolean verifyLoanType() {
        return !leadDetails.getLoanType().equals(NOT_PROVIDED);
    }

    private boolean verifyPropertyType() {
        if (leadDetails.getLoanType().equals("Home Loan") || leadDetails.getLoanType().equals("Loan Against Property"))
            return !leadDetails.getPropertyType().equals(NOT_PROVIDED);
        else
            return true;
    }

    private boolean verifyLocation() {
        return !leadDetails.getLocation().equals(NOT_PROVIDED);
    }

    private boolean verifyRemarks() {
        return remarks.length() != 0;
    }

    private boolean verifyAlarm() {
        return !date.equals("DD/MM/YYYY") && !time.equals("hh:mm");
    }

    private boolean verifyAssignee() {
        return leadDetails.getAssignedToUId() != null;
    }

    private boolean verifyAssigner() {
        return leadDetails.getForwarderUId() != null;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public LeadDetails getLead() {
        return leadDetails;
    }

    public void setCustomerName(String name) {
        leadDetails.setName(name);
    }

    public String getCustomerName() {
        return leadDetails.getName();
    }


    public void setCustomerContact(String contact) {
        leadDetails.setContactNumber(contact);
    }

    public String getCustomerContact() {
        return leadDetails.getContactNumber();
    }

    public void setLoanAmount(String loanAmount) {
        leadDetails.setLoanAmount(loanAmount);
    }

    public void setRemarks(String remark) {
        if (!remark.isEmpty()) {
            remarks = remark;
        }
    }

    public void addCallerRemarks() {
        ArrayList<String> remarkss = leadDetails.getTelecallerRemarks();
        remarkss.add(remarks);
        leadDetails.setTelecallerRemarks(remarkss);

    }

    public void setEmployment(String employment) {
        leadDetails.setEmployment(employment);
        if (employment.equals(NOT_PROVIDED) || employment.equals("Salaried")) {
            setEmploymentType(NOT_PROVIDED);
        }
    }

    public void setEmploymentType(String employmentType) {
        leadDetails.setEmploymentType(employmentType);
    }

    public void setLoanType(String loanType) {
        leadDetails.setLoanType(loanType);

        if (!loanType.equals("Home Loan") && !loanType.equals("Loan Against Property")) {
            leadDetails.setPropertyType(NOT_PROVIDED);
        }
    }

    public String getLoanType() {
        return leadDetails.getLoanType();
    }

    public void setPropertyType(String propertyType) {
        leadDetails.setPropertyType(propertyType);
    }

    public void setLocation(String location) {
        leadDetails.setLocation(location);

        if (location.equals(NOT_PROVIDED)) {
            setForwarder("Not Assigned", null);
            setAssignee("Not Assigned", null);
        }
    }

    public String getLocation() {
        return leadDetails.getLocation();
    }

    public void setForwarder(String name, String uId) {
        leadDetails.setForwarderName(name);
        leadDetails.setForwarderUId(uId);
    }

    public String getForwarderUId() {
        return leadDetails.getForwarderUId();
    }

    public void setAssignee(String name, String uId) {
        leadDetails.setAssignedTo(name);
        leadDetails.setAssignedToUId(uId);
    }

    public String getAssigneeName() {
        return leadDetails.getAssignedTo();
    }

    public String getAssignerName() {
        return leadDetails.getAssigner();
    }

    public String getAssigneeUId() {
        return leadDetails.getAssignedToUId();
    }

    public void setAssigner(String name, String uId) {
        leadDetails.setAssigner(name);
        leadDetails.setAssignerUId(uId);
    }

    public String getStatus() {
        return leadDetails.getStatus();
    }

    public void setLeadReminderDate(String date) {
        this.date = date;
    }

    public void setLeadReminderTime(String time) {
        this.time = time;
    }

    public void setAssignTime() {
        TimeManager manager = new TimeManager();

        if (getAssigneeUId() != null) {
            leadDetails.setAssignDate(manager.getStrDate());
            leadDetails.setAssignTime(manager.getStrTime());
        }
    }

    public void setWorkTime() {
        TimeManager manager = new TimeManager();
        leadDetails.setTimeStamp(manager.getTimeStamp());
    }

    public void setAlarmDetails(String date, String time) {
        this.date = date;
        this.time = time;
    }
}
