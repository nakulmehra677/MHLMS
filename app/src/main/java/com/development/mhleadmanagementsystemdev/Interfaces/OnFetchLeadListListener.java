package com.development.mhleadmanagementsystemdev.Interfaces;

import com.development.mhleadmanagementsystemdev.Models.LeadDetails;

import java.util.List;

public interface OnFetchLeadListListener {
    void onSuccess(List<LeadDetails> list);

    void onFailer();
}
