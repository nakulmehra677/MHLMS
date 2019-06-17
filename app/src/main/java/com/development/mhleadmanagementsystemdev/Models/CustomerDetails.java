package com.development.mhleadmanagementsystemdev.Models;

public class CustomerDetails {
    String name, contactNumber, propertyType, employement, loanType, location, loanAmount, remarks, date, assignedTo, status;

    public String getName() {
        return name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public String getEmployement() {
        return employement;
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

    public String getRemarks() {
        return remarks;
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
        this.employement = employement;
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

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public CustomerDetails(String name, String contactNumber, String propertyType,
                           String employement, String loanType, String location,
                           String loanAmount, String remarks, String date, String assignedTo, String status) {
        this.name = name;
        this.contactNumber = contactNumber;
        this.propertyType = propertyType;
        this.employement = employement;
        this.loanType = loanType;
        this.location = location;
        this.loanAmount = loanAmount;
        this.remarks = remarks;
        this.date = date;
        this.assignedTo = assignedTo;
        this.status = status;
    }
}
