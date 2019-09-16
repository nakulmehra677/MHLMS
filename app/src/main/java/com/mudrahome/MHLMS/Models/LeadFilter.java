package com.mudrahome.MHLMS.Models;

public class LeadFilter {
    private String location, assigner, assignee, status, loanType;

    public LeadFilter(String location, String assigner, String assignee, String loanType, String status) {
        this.location = location;
        this.assigner = assigner;
        this.assignee = assignee;
        this.status = status;
        this.loanType = loanType;
    }

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
}
