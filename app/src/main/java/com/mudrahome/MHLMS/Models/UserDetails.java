package com.mudrahome.MHLMS.Models;

import java.util.List;
import java.util.Set;

public class UserDetails {
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private String uId, userName, mail, location, key, deviceToken, contactNumber;
    private Set<String> userType;

    public UserDetails() {
    }

    public UserDetails(String uId, String userName, String mail, String location,
                       Set<String> userType, String key, String deviceToken) {
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

    public Set<String> getUserType() {
        return userType;
    }

    public void setUserType(Set<String> userType) {
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
