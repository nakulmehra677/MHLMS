package com.mudrahome.mhlms.model;

public class LeadFilter {
    private String location = "All";
    private String assigner = "All";
    private String assignee = "All";
    private String status = "All";
    private String loanType = "All";
//
//    public LeadFilter(String location, String assigner, String assignee, String loanType, String status) {
//        this.location = location;
//        this.assigner = assigner;
//        this.assignee = assignee;
//        this.status = status;
//        this.loanType = loanType;
//    }

    public String getLocation() {
        return location;
    }

    public String getAssigner() {
        return assigner;
    }

    public String getAssignee() {
        return assignee;
    }

    public String getStatus() {
        return status;
    }

    public String getLoanType() {
        return loanType;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setAssigner(String assigner) {
        this.assigner = assigner;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }
}
