package com.development.mhleadmanagementsystemdev.Models;

public class TeleCallerDetails {
    private String uId, userName, mail, location;

    public TeleCallerDetails() {
    }

    public TeleCallerDetails(String uId, String userName, String mail, String location) {
        this.uId = uId;
        this.userName = userName;
        this.mail = mail;
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getuId() {
        return uId;
    }

    public String getUserName() {
        return userName;
    }

    public String getMail() {
        return mail;
    }
}
