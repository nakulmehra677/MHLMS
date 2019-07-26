package com.development.mhleadmanagementsystemdev.Helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchLeadListListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchUsersListListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchUserDetailsByUId;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchUserDetailsListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnUpdateLeadListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnUploadCustomerDetailsListener;
import com.development.mhleadmanagementsystemdev.Models.LeadDetails;
import com.development.mhleadmanagementsystemdev.Models.UserDetails;
import com.development.mhleadmanagementsystemdev.Models.UserList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabaseHelper {
    Context context;
    private long nodes = 0;
    private boolean isAdmin = false;

    public FirebaseDatabaseHelper() {

    }

    public FirebaseDatabaseHelper(Context context) {
        this.context = context;
    }

    public void uploadCustomerDetails(final OnUploadCustomerDetailsListener listener,
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
                        Log.w("TAG", "Error adding document", e);
                    }
                });
    }

    public void fetchSalesPersons(
            final OnFetchUsersListListener listener, String location) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query;

        if (location.equals("All") || location == null)
            query = db.collection("userList")
                    .whereEqualTo("userType", "Salesman");
        else
            query = db.collection("userList")
                    .whereEqualTo("userType", "Salesman")
                    .whereEqualTo("location", location);


        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {

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

    public void fetchTelecallers(
            final OnFetchUsersListListener listener, String location) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Query query;

        if (location.equals("All") || location == null)
            query = db.collection("userList")
                    .whereEqualTo("userType", "Telecaller");
        else
            query = db.collection("userList")
                    .whereEqualTo("userType", "Telecaller")
                    .whereEqualTo("location", location);


        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {

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

    public void updateLeadDetails(final OnUpdateLeadListener listener, LeadDetails updateLead) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("leadList").document(updateLead.getKey());

        dRef.update("assignedTo", updateLead.getAssignedTo(),
                "telecallerRemarks", updateLead.getTelecallerRemarks(),
                "salesmanRemarks", updateLead.getSalesmanRemarks(),
                "salesmanReason", updateLead.getSalesmanReason(),
                "status", updateLead.getStatus())

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

    public void getLeadList(final OnFetchLeadListListener listener,
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

            query = query.orderBy("date", Query.Direction.DESCENDING)
                    //.orderBy("time", Query.Direction.DESCENDING)
                    .limit(20);
        } else {
            if (!assign.equals("Admin"))
                query = query.whereEqualTo(assign, userName);

            query = query.orderBy("date", Query.Direction.DESCENDING)
                    //.orderBy("time", Query.Direction.DESCENDING)
                    .startAfter(lastLead)
                    .limit(20);
        }

        Log.i("database",assign);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                Log.i("database","rrrrrrrrr");

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

    public void getUsersByUId(final OnFetchUserDetailsByUId onFetchUserDetailsByUId, String uId) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("userList").document(uId);

        dRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserDetails userDetails = documentSnapshot.toObject(UserDetails.class);
                onFetchUserDetailsByUId.onSuccess(userDetails);
            }
        });
    }

    public void setCurrentDeviceToken(String deviceToken, String uId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("userList").document(uId);

        dRef.update("deviceToken", deviceToken);
    }
}