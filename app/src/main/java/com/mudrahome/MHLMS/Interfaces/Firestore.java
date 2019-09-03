package com.mudrahome.MHLMS.Interfaces;

import com.google.firebase.firestore.DocumentSnapshot;
import com.mudrahome.MHLMS.Models.LeadDetails;
import com.mudrahome.MHLMS.Models.OfferDetails;
import com.mudrahome.MHLMS.Models.UserDetails;
import com.mudrahome.MHLMS.Models.UserList;

import java.util.ArrayList;
import java.util.List;

public class Firestore {

    public interface FetchOffer {
        void onSuccess(List<OfferDetails> details);

        void onFail();
    }

    public interface OnFetchBankList {
        void onSuccess(ArrayList list);

        void onFail();
    }

    public interface OnFetchLeadList {
        void onLeadAdded(List<LeadDetails> l, DocumentSnapshot lastVisible);

        void onLeadChanged(LeadDetails l);

        void onFailer();
    }

    public interface OnGetUserDetails {
        void onSuccess(UserDetails userDetails);

        void fail();
    }

    public interface OnFetchUsersList {
        void onListFetched(UserList userList);

    }

    public interface OnRemoveAd {
        void onSuccess();
        void onFail();
    }

    public interface OnUploadOffer {
        void onSuccess();
        void onFail();
    }

    public interface OnUpdateUser {
        void onSuccess();
        void onFail();
    }

    public interface OnUploadCustomerDetails {
        void onDataUploaded();
        void failedToUpload();
    }

    public interface OnUpdateLead {
        void onLeadUpdated();
        void onFailer();
    }
}