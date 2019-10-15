package com.mudrahome.mhlms.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UserDetails {
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    private String uId = "Not available";
    private String userName = "Not available";
    private String mail = "Not available";
    private String key = "Not available";
    private String deviceToken = "Not available";
    private String contactNumber = "Not available";
    private String workingLocation = "Not available";
    private List<String> userType = new ArrayList<>(Collections.singletonList("Not available"));
    private Map<String, Boolean> location;


    public UserDetails() {
    }

    public UserDetails(String uId, String userName, String mail, Map<String, Boolean> location,
                       List<String> userType, String key, String deviceToken, String workingLocation) {
        if (uId != null)
            this.uId = uId;
        if (userName != null)
            this.userName = userName;
        if (mail != null)
            this.mail = mail;
        if (location != null)
            this.location = location;
        if (userType != null)
            this.userType = userType;
        if (key != null)
            this.key = key;
        if (deviceToken != null)
            this.deviceToken = deviceToken;
        if (workingLocation != null)
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
