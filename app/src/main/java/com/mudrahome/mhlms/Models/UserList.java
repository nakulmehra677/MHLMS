package com.mudrahome.mhlms.models;


import java.util.ArrayList;
import java.util.List;

public class UserList {
    private List<UserDetails> userList = new ArrayList<>();

    public UserList(List<UserDetails> userList) {
        this.userList = userList;
    }

    public List<UserDetails> getUserList() {
        return userList;
    }

    public void setUserList(List<UserDetails> userList) {
        this.userList = userList;
    }
}
