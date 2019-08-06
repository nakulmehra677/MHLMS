package com.mudrahome.MHLMS.Interfaces;

import com.mudrahome.MHLMS.Models.UserList;

public interface OnFetchUsersListListener<T> {
    void onListFetched(UserList userList);

}
