package com.mudrahome.MHLMS.Firebase;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import android.util.Log;

import com.mudrahome.MHLMS.Models.LeadDetails;
import com.mudrahome.MHLMS.Models.OfferDetails;
import com.mudrahome.MHLMS.Models.UserDetails;
import com.mudrahome.MHLMS.Models.UserList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mudrahome.MHLMS.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.firebase.ui.auth.AuthUI.TAG;

public class Firestore {
    Context context;
    private long nodes = 0;
    private boolean isAdmin = false;

    public Firestore() {

    }

    public Firestore(Context context) {
        this.context = context;
    }

    public void uploadCustomerDetails(final com.mudrahome.MHLMS.Interfaces.Firestore.OnUploadCustomerDetails listener,
                                      final LeadDetails leadDetails) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("leadList").document();

        leadDetails.setKey(dRef.getId());

        dRef.set(leadDetails)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        listener.onDataUploaded();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", "Error adding document", e);
                    }
                });
    }

    public void fetchUsersByUserType(
            final com.mudrahome.MHLMS.Interfaces.Firestore.OnFetchUsersList listener, String location, String userType) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query;

        if (location.equals("All") || location == null)
            query = db.collection("userList")
                    .whereArrayContains("userType", userType);
        else
            query = db.collection("userList")
                    .whereArrayContains("userType", userType)
                    .whereEqualTo("location", location);

        query = query.orderBy("userName", Query.Direction.ASCENDING);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                Log.d("HEREEE", "ffff");

                List<UserDetails> salesPersonList = new ArrayList<>();
                for (QueryDocumentSnapshot document : documentSnapshots) {
                    UserDetails l = document.toObject(UserDetails.class);
                    salesPersonList.add(l);
                }
                UserList userList = new UserList(salesPersonList);
                listener.onListFetched(userList);
            }
        });
    }

    public void updateLeadDetails(final com.mudrahome.MHLMS.Interfaces.Firestore.OnUpdateLead listener, LeadDetails updateLead) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("leadList").document(updateLead.getKey());

        dRef.update("name", updateLead.getName(),
                "loanAmount", updateLead.getLoanAmount(),
                "contactNumber", updateLead.getContactNumber(),
                "assignedTo", updateLead.getAssignedTo(),
                "assignedToUId", updateLead.getAssignedToUId(),
                "telecallerRemarks", updateLead.getTelecallerRemarks(),
                "salesmanRemarks", updateLead.getSalesmanRemarks(),
                "salesmanReason", updateLead.getSalesmanReason(),
                "status", updateLead.getStatus(),
                "date", updateLead.getDate(),
                "time", updateLead.getTime(),
                "timeStamp", updateLead.getTimeStamp(),
                "banks", updateLead.getBanks())

                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listener.onLeadUpdated();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onFailer();
            }
        });
    }

    public void getOffers(final com.mudrahome.MHLMS.Interfaces.Firestore.FetchOffer fetchOffer,
                          String name, String userType, boolean singleItem) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("offerList");

        if (!userType.equals("Admin"))
            query = query.whereArrayContains("userNames", name);

        query = query.orderBy("timestamp", Query.Direction.DESCENDING);

        if (singleItem)
            query = query.limit(1);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {

                List<OfferDetails> offerDetails = new ArrayList<>();
                for (QueryDocumentSnapshot document : documentSnapshots) {
                    OfferDetails l = document.toObject(OfferDetails.class);
                    offerDetails.add(l);
//                    Log.d("offerr", l.getTitle());
//                    Log.d("offerr", l.getDescription());
                }

                fetchOffer.onSuccess(offerDetails);
            }
        });
    }

    public void getLeadList(final com.mudrahome.MHLMS.Interfaces.Firestore.OnFetchLeadList listener,
                            String assign, String userName, DocumentSnapshot lastLead,
                            String locationFilter, String assignerFilter,
                            String assigneeFilter, String loanTypeFilter, String statusFilter) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query = db.collection("leadList");

        if (!locationFilter.equals("All"))
            query = query.whereEqualTo("location", locationFilter);
        if (!assignerFilter.equals("All"))
            query = query.whereEqualTo("assigner", assignerFilter);
        if (!assigneeFilter.equals("All"))
            query = query.whereEqualTo("assignedTo", assigneeFilter);
        if (!loanTypeFilter.equals("All"))
            query = query.whereEqualTo("loanType", loanTypeFilter);
        if (!statusFilter.equals("All"))
            query = query.whereEqualTo("status", statusFilter);

        if (lastLead == null) {
            if (!assign.equals("Admin"))
                query = query.whereEqualTo(assign, userName);

            query = query.orderBy("timeStamp", Query.Direction.DESCENDING)
                    .limit(20);
        } else {
            if (!assign.equals("Admin"))
                query = query.whereEqualTo(assign, userName);

            query = query.orderBy("timeStamp", Query.Direction.DESCENDING)
                    .startAfter(lastLead)
                    .limit(20);
        }

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {

                List<LeadDetails> leads = new ArrayList<>();
                for (QueryDocumentSnapshot document : documentSnapshots) {
                    LeadDetails l = document.toObject(LeadDetails.class);
                    leads.add(l);
                }

                DocumentSnapshot lastVisible = null;
                if (documentSnapshots.size() > 0)
                    lastVisible = documentSnapshots.getDocuments()
                            .get(documentSnapshots.size() - 1);

                listener.onLeadAdded(leads, lastVisible);
            }
        });
    }

    public void getUsers(final com.mudrahome.MHLMS.Interfaces.Firestore.OnGetUserDetails onGetUserDetails, String uId) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("userList").document(uId);

        dRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                UserDetails userDetails = documentSnapshot.toObject(UserDetails.class);
                onGetUserDetails.onSuccess(userDetails);
            }
        });
    }

    public void setCurrentDeviceToken(String deviceToken, String uId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("userList").document(uId);

        dRef.update("deviceToken", deviceToken);
    }

    public void startOffer(final com.mudrahome.MHLMS.Interfaces.Firestore.OnUploadOffer listener, OfferDetails details) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("offerList").document();

        details.setKey(dRef.getId());

        dRef.set(details).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                listener.onSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onFail();
                Log.e("TAG", "Error adding document", e);
            }
        });
    }

    public void removeAd(final com.mudrahome.MHLMS.Interfaces.Firestore.OnRemoveAd removeAd, OfferDetails details) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("offerList").document(details.getKey());

        dRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                removeAd.onSuccess();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                removeAd.onFail();
            }
        });
    }

    public void updateUserDetails(final com.mudrahome.MHLMS.Interfaces.Firestore.OnUpdateUser updateUser, UserDetails details) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("userList").document(details.getuId());

        dRef.update("contactNumber", details.getContactNumber())

                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        updateUser.onSuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                updateUser.onFail();
            }
        });
    }

    public void getBankList(final com.mudrahome.MHLMS.Interfaces.Firestore.OnFetchBankList list) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("bankList").document("banks");

        dRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot snapshot) {
                ArrayList arrList = new ArrayList<String>();
                arrList = (ArrayList) snapshot.get("bankName");

                list.onSuccess(arrList);
            }
        });
    }
}