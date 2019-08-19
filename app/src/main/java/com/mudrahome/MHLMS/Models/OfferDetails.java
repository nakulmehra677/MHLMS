package com.mudrahome.MHLMS.Models;

import java.util.ArrayList;

public class OfferDetails {
    private String title, description, key;
    ArrayList<CharSequence> userNames;

    public OfferDetails(String title, String description, ArrayList<CharSequence> userNames) {
        this.title = title;
        this.description = description;
        this.userNames = userNames;
    }

    public OfferDetails() {
        
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

    public ArrayList<CharSequence> getUserNames() {
        return userNames;
    }

    public void setUserNames(ArrayList<CharSequence> userNames) {
        this.userNames = userNames;
    }
}
