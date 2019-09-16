package com.mudrahome.MHLMS.Managers;

import com.google.firebase.firestore.DocumentSnapshot;
import com.mudrahome.MHLMS.Firebase.Firestore;
import com.mudrahome.MHLMS.Interfaces.FirestoreInterfaces;
import com.mudrahome.MHLMS.Interfaces.ManagerInterfaces;
import com.mudrahome.MHLMS.Models.LeadDetails;
import com.mudrahome.MHLMS.Models.LeadFilter;

import java.util.List;

public class LeadManager {
    private Firestore firestore;
    private DocumentSnapshot snapshot;

    public LeadManager() {
        firestore = new Firestore();
    }

    public void getLeadLists(final ManagerInterfaces.GetLeadListListener listener, String assign,
                             String userName, LeadFilter leadFilter) {

        firestore.downloadLeadList(
                new FirestoreInterfaces.OnFetchLeadList() {
                    @Override
                    public void onLeadAdded(List<LeadDetails> l, DocumentSnapshot lastVisible) {
                        for (int i = 0; i < l.size(); i++) {
                            LeadDetails details = checkLead(l.get(i));
                            l.set(i, details);
                        }
                        LeadDetails lastLead = null;
                        if (l.size() > 0)
                            lastLead = l.get(l.size() - 1);
                        listener.onLeadAdded(l, null);
                    }

                    @Override
                    public void onLeadChanged(LeadDetails l) {

                    }

                    @Override
                    public void onFailer() {

                    }
                }, assign, userName, snapshot, leadFilter);
    }

    private LeadDetails checkLead(LeadDetails leadDetails) {
        return null;
    }
}
