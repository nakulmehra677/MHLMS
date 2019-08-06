package com.mudrahome.MHLMS.Interfaces;

import com.mudrahome.MHLMS.Models.UserDetails;

public interface OnGetUserDetails {
    void onSuccess(UserDetails userDetails);
    void fail();
}
