package com.development.mhleadmanagementsystemdev.Models;

public class UserDetails {
    private String uId, password, userName, mail, userType;

    public UserDetails() {
    }

    public UserDetails(String uId, String password, String userName, String mail, String userType) {
        this.uId = uId;
        this.password = password;
        this.userName = userName;
        this.mail = mail;
        this.userType = userType;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getuId() {
        return uId;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }

    public String getMail() {
        return mail;
    }

    public String getUserType() {
        return userType;
    }
}
