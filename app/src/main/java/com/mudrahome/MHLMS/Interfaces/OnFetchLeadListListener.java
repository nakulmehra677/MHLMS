package com.mudrahome.MHLMS.Interfaces;

import com.mudrahome.MHLMS.Models.LeadDetails;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public interface OnFetchLeadListListener {
    void onLeadAdded(List<LeadDetails> l, DocumentSnapshot lastVisible);

    void onLeadChanged(LeadDetails l);

    void onFailer();
}
