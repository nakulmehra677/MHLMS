package com.mudrahome.mhlms.managers;

import com.mudrahome.mhlms.model.LeadDetails;
import com.mudrahome.mhlms.model.TimeModel;

import java.util.ArrayList;

public class UpdateLead {
    private LeadDetails leadDetails;

    public LeadDetails getLeadDetails() {
        return leadDetails;
    }

    public void setLeadDetails(LeadDetails leadDetails) {
        this.leadDetails = leadDetails;
    }

    public UpdateLead(LeadDetails leadDetails) {
        this.leadDetails = leadDetails;
    }

    public void updateByCaller(String customerName, String loanAmount, String contactNumber, ArrayList<String> callerRemarks) {
        leadDetails.setName(customerName);
        leadDetails.setLoanAmount(loanAmount);
        leadDetails.setContactNumber(contactNumber);
        leadDetails.setTelecallerRemarks(callerRemarks);
    }

    public void updateByAssigner(String customerName, String loanAmount, String contactNumber, ArrayList<String> forwarderRemarks) {
        leadDetails.setName(customerName);
        leadDetails.setLoanAmount(loanAmount);
        leadDetails.setContactNumber(contactNumber);
        leadDetails.setForwarderRemarks(forwarderRemarks);
    }

    public void time() {
        TimeManager timeManager = new TimeManager();
        leadDetails.setTimeStamp(timeManager.getTimeStamp());
    }

    public void assignDate() {
        TimeManager timeManager = new TimeManager();

        leadDetails.setAssignDate(timeManager.getStrDate());
        leadDetails.setAssignTime(timeManager.getStrTime());
    }

    public void assignedToDetails(String assignedTo, String uid) {
        leadDetails.setAssignedTo(assignedTo);
        leadDetails.setAssignedToUId(uid);

        TimeManager timeManager = new TimeManager();
        leadDetails.setStatus("Active");
        leadDetails.setAssignDate(timeManager.getStrDate());
        leadDetails.setAssignTime(timeManager.getStrTime());
    }

    private TimeManager getTime() {
        return new TimeManager();
    }
}
