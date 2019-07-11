package com.development.mhleadmanagementsystemdev.Interfaces;

import com.development.mhleadmanagementsystemdev.Models.UserDetails;

public interface OnFetchUserDetailsByUId {
    void onSuccess(UserDetails userDetails);
    void fail();
}
