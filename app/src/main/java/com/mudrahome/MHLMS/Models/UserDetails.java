package com.mudrahome.MHLMS.Models;

public class UserDetails {
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private String uId, userName, mail, location, userType, key, deviceToken,contactNumber;

    public UserDetails() {
    }

    public UserDetails(String uId, String userName, String mail, String location,
                       String userType, String key, String deviceToken) {
        this.uId = uId;
        this.userName = userName;
        this.mail = mail;
        this.location = location;
        this.userType = userType;
        this.key = key;
        this.deviceToken = deviceToken;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
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
