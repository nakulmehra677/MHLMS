package com.development.mhleadmanagementsystemdev.Interfaces;

import com.development.mhleadmanagementsystemdev.Models.UserList;

public interface OnFetchUsersListListener<T> {
    void onListFetched(UserList userList);

}
