package com.development.mhleadmanagementsystemdev.Interfaces;

import com.development.mhleadmanagementsystemdev.Models.LeadDetails;

import java.util.List;

public interface OnFetchLeadListListener {
    void onLeadAdded(List<LeadDetails> l);

    void onLeadChanged(LeadDetails l);

    void onFailer();
}
