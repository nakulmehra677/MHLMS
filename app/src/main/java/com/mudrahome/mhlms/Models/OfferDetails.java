package com.mudrahome.mhlms.models;

import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.List;

public class OfferDetails {
    private String title, description, key;
    private List<String> userNames = new ArrayList<>();
    private Timestamp timestamp;

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public OfferDetails() {

    }

    public OfferDetails(String title, String description, List<String> userNames, Timestamp timestamp) {
        this.title = title;
        this.description = description;
        this.userNames = userNames;
        this.timestamp = timestamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getUserNames() {
        return userNames;
    }

    public void setUserNames(List<String> userNames) {
        this.userNames = userNames;
    }
}
