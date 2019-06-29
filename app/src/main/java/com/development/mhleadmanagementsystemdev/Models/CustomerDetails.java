package com.development.mhleadmanagementsystemdev.Models;

public class CustomerDetails {
    String name, contactNumber, loanAmount, loanType, propertyType, employment,
            employmentType, location, salesmanRemarks, date, assignedTo, status, key,
            assigner, telecallerRemarks;

    public String getName() {
        return name;
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

    public String getTelecallerRemarks() {
        return telecallerRemarks;
    }

    public void setTelecallerRemarks(String telecallerRemarks) {
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

    public CustomerDetails() {
    }

    public CustomerDetails(String name, String contactNumber, String loanAmount, String employment,
                           String employmentType, String loanType, String propertyType,
                           String location, String telecallerRemarks,
                           String date, String assignedTo, String status, String assigner,
                           String key, String salesmanRemarks) {
        this.name = name;
        this.contactNumber = contactNumber;
        this.propertyType = propertyType;
        this.employment = employment;
        this.loanType = loanType;
        this.location = location;
        this.loanAmount = loanAmount;
        this.date = date;
        this.assignedTo = assignedTo;
        this.status = status;
        this.key = key;
        this.employmentType = employmentType;
        this.assigner = assigner;
        this.telecallerRemarks = telecallerRemarks;
        this.salesmanRemarks = salesmanRemarks;
    }
}
