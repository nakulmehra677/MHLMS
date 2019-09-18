package com.mudrahome.MHLMS.Models;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class LeadDetails {
    private String name = "None";
    private String contactNumber = "None";
    private String assignerContact = "None";
    private String assigneeContact = "None";
    private String loanAmount = "None";
    private String loanType = "None";
    private String propertyType = "None";
    private String employment = "None";
    private String employmentType = "None";
    private String location = "None";
    private String salesmanRemarks = "None";
    private String date = "None";
    private String assignedTo = "None";
    private String status = "None";
    private String key = "None";
    private String assigner = "None";
    private String assignedToUId = "None";
    private String assignerUId = "None";
    private String time = "None";
    private String assignDate = "None";
    private String assignTime = "None";

    private ArrayList<String> telecallerRemarks = new ArrayList<>(Collections.singletonList("None"));
    private ArrayList<String> salesmanReason = new ArrayList<>(Collections.singletonList("None"));
    private long timeStamp;

    public String getAssignerContact() {
        return assignerContact;
    }

    public void setAssignerContact(String assignerContact) {
        this.assignerContact = assignerContact;
    }

    public String getAssigneeContact() {
        return assigneeContact;
    }

    public void setAssigneeContact(String assigneeContact) {
        this.assigneeContact = assigneeContact;
    }

    List<String> banks = new ArrayList<>();

    public List<String> getBanks() {
        return banks;
    }

    public void setBanks(List<String> banks) {
        this.banks = banks;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getName() {
        return name;
    }

    public String getAssignDate() {
        return assignDate;
    }

    public void setAssignDate(String assignDate) {
        this.assignDate = assignDate;
    }

    public String getAssignTime() {
        return assignTime;
    }

    public void setAssignTime(String assignTime) {
        this.assignTime = assignTime;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getKey() {
        return key;
    }

    public String getSalesmanRemarks() {
        return salesmanRemarks;
    }

    public void setSalesmanRemarks(String salesmanRemarks) {
        this.salesmanRemarks = salesmanRemarks;
    }

    public ArrayList<String> getTelecallerRemarks() {
        return telecallerRemarks;
    }

    public void setTelecallerRemarks(ArrayList<String> telecallerRemarks) {
        this.telecallerRemarks = telecallerRemarks;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setEmployment(String employment) {
        this.employment = employment;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public String getAssigner() {
        return assigner;
    }

    public void setAssigner(String assigner) {
        this.assigner = assigner;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public String getEmployment() {
        return employment;
    }

    public String getLoanType() {
        return loanType;
    }

    public String getLocation() {
        return location;
    }

    public String getLoanAmount() {
        return loanAmount;
    }

    public String getDate() {
        return date;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public void setEmployement(String employement) {
        this.employment = employment;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setLoanAmount(String loanAmount) {
        this.loanAmount = loanAmount;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getStatus() {
        return status;
    }


    public void setStatus(String status) {
        this.status = status;
    }

    public LeadDetails() {
    }

    public String getAssignedToUId() {
        return assignedToUId;
    }

    public void setAssignedToUId(String assignedToUId) {
        this.assignedToUId = assignedToUId;
    }

    public String getAssignerUId() {
        return assignerUId;
    }

    public void setAssignerUId(String assignerUId) {
        this.assignerUId = assignerUId;
    }

    public ArrayList<String> getSalesmanReason() {
        return salesmanReason;
    }

    public void setSalesmanReason(ArrayList<String> salesmanReason) {
        this.salesmanReason = salesmanReason;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public LeadDetails(String name, String contactNumber, String assignerContact,
                       String assigneeContact, String loanAmount, String employment,
                       String employmentType, String loanType, String propertyType, String location,
                       ArrayList<String> telecallerRemarks, String date, String assignedTo, String status,
                       String assigner, String key, String salesmanRemarks, String assignedToUId,
                       String assignerUId, ArrayList<String> salesmanReason, String time, String assignDate,
                       String assignTime, long timeStamp) {
        if (name != null)
            this.name = name;
        if (contactNumber != null)
            this.contactNumber = contactNumber;
        if (assignerContact != null)
            this.assignerContact = assignerContact;
        if (assigneeContact != null)
            this.assigneeContact = assigneeContact;
        if (propertyType != null)
            this.propertyType = propertyType;
        if (employment != null)
            this.employment = employment;
        if (loanType != null)
            this.loanType = loanType;
        if (location != null)
            this.location = location;
        if (loanAmount != null)
            this.loanAmount = loanAmount;
        if (date != null)
            this.date = date;
        if (assignedTo != null)
            this.assignedTo = assignedTo;
        if (status != null)
            this.status = status;
        if (key != null)
            this.key = key;
        if (employmentType != null)
            this.employmentType = employmentType;
        if (assigner != null)
            this.assigner = assigner;
        if (telecallerRemarks != null)
            this.telecallerRemarks = telecallerRemarks;
        if (salesmanRemarks != null)
            this.salesmanRemarks = salesmanRemarks;
        if (salesmanReason != null)
            this.salesmanReason = salesmanReason;
        if (assignedToUId != null)
            this.assignedToUId = assignedToUId;
        if (assignerUId != null)
            this.assignerUId = assignerUId;
        if (time != null)
            this.time = time;
        if (assignDate != null)
            this.assignDate = assignDate;
        if (assignTime != null)
            this.assignTime = assignTime;
        this.timeStamp = timeStamp;
    }
}
