package com.mudrahome.MHLMS.Models;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserDetails {
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private String uId, userName, mail, key, deviceToken, contactNumber,workingLocation;
    private List<String> userType;
    private Map<String, Boolean> location;


    public UserDetails() {
    }

    public UserDetails(String uId, String userName, String mail, Map<String, Boolean> location,
                       List<String> userType, String key, String deviceToken,String workingLocation) {
        this.uId = uId;
        this.userName = userName;
        this.mail = mail;
        this.location = location;
        this.userType = userType;
        this.key = key;
        this.deviceToken = deviceToken;
        this.workingLocation = workingLocation;
    }

    public String getWorkingLocation() {
        return workingLocation;
    }

    public void setWorkingLocation(String workingLocation) {
        this.workingLocation = workingLocation;
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

    public List<String> getUserType() {
        return userType;
    }

    public void setUserType(List<String> userType) {
        this.userType = userType;
    }

    public Map<String, Boolean> getLocation() {
        return location;
    }

    public void setLocation(Map<String, Boolean> location) {
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
