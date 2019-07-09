package com.development.mhleadmanagementsystemdev.Interfaces;

import com.development.mhleadmanagementsystemdev.Models.LeadDetails;

public interface OnFetchLeadListListener {
    void onLeadAdded(LeadDetails l);
    void onLeadChanged(LeadDetails l);
    void onFailer();
}
