package com.development.mhleadmanagementsystemdev.Interfaces;

import com.development.mhleadmanagementsystemdev.Models.UserDetails;

public interface OnGetUserDetails {
    void onSuccess(UserDetails userDetails);
    void fail();
}
