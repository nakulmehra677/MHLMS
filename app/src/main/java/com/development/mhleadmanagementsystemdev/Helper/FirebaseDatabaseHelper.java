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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
                "salesmanReason", updateLead.getSalesmanReason())

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

    public void getUserDetails(final OnFetchUserDetailsListener listener, String uId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("userList");

        myRef.orderByChild("uId").equalTo(uId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UserDetails userDetails = snapshot.getValue(UserDetails.class);
                            listener.onSuccess(userDetails);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void getLeadList(final OnFetchLeadListListener listener,
                            String assign, String userName, DocumentSnapshot lastLead,
                            String locationFilter, String assignerFilter,
                            String assigneeFilter, String statusFilter) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query query;

        if (lastLead == null)
            query = db.collection("leadList")
                    .orderBy("date", Query.Direction.DESCENDING)
                    .limit(6);
        else
            query = db.collection("leadList")
                    .orderBy("date", Query.Direction.DESCENDING)
                    .startAfter(lastLead)
                    .limit(6);

        if (!locationFilter.equals("All"))
            query = query.whereEqualTo("location", locationFilter);
        if (!assignerFilter.equals("All"))
            query = query.whereEqualTo("location", assignerFilter);
        if (!assigneeFilter.equals("All"))
            query = query.whereEqualTo("location", assigneeFilter);
        if (!statusFilter.equals("All"))
            query = query.whereEqualTo("location", statusFilter);

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

    public void getUsersByUId(final OnFetchUserDetailsByUId onFetchUserDetailsByUId, String uId) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference dRef = db.collection("userList").document(uId);

        dRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                UserDetails userDetails = documentSnapshot.toObject(UserDetails.class);
                Log.i("Fetching detials", userDetails.getUserName());
                onFetchUserDetailsByUId.onSuccess(userDetails);
            }
        });
    }

    public void setCurrentDeviceToken(String deviceToken, String key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("userList");

        myRef.child(key).child("deviceToken").setValue(deviceToken);
    }
}