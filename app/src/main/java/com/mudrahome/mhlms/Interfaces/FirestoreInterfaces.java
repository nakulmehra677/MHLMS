package com.mudrahome.mhlms.Interfaces;

import com.google.firebase.firestore.DocumentSnapshot;
import com.mudrahome.mhlms.models.LeadDetails;
import com.mudrahome.mhlms.models.OfferDetails;
import com.mudrahome.mhlms.models.UserDetails;
import com.mudrahome.mhlms.models.UserList;

import java.util.ArrayList;
import java.util.List;

public class FirestoreInterfaces {

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

    public interface OnLeadDetails{
        void onSucces(LeadDetails leadDetails);
    }
}