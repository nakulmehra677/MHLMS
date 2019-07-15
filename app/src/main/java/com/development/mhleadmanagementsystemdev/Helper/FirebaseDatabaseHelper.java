package com.development.mhleadmanagementsystemdev.Helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchDeviceTokenListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchLeadListListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchSalesPersonListListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchUserDetailsByUId;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchUserDetailsListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnSetCurrentDeviceTokenListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnUpdateLeadListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnUploadCustomerDetailsListener;
import com.development.mhleadmanagementsystemdev.Models.LeadDetails;
import com.development.mhleadmanagementsystemdev.Models.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseDatabaseHelper {
    Context context;
    private long nodes = 0;
    private boolean isAdmin = false;

    public FirebaseDatabaseHelper() {

    }

    public FirebaseDatabaseHelper(Context context) {
        this.context = context;
    }

    public void uploadCustomerDetails(OnUploadCustomerDetailsListener onUploadCustomerdetails,
                                      LeadDetails leadDetails) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("leadList")
                .add(leadDetails)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("TAG", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error adding document", e);
                    }
                });

        /*Log.i("No of Nodes", "Uploading");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("leadList").push();

        String key = myRef.getKey();
        leadDetails.setKey(key);
        myRef.setValue(leadDetails);*/
        onUploadCustomerdetails.onDataUploaded();
    }

    public void fetchSalesPersonsByLocation(
            final OnFetchSalesPersonListListener onFetchSalesPersonListListener, String location) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("userList");
        Log.i("Users", "Fetching Users...");

        final List<UserDetails> salesPersonList = new ArrayList<>();
        final List<String> salesPersonNameList = new ArrayList<>();

        myRef.orderByChild("location").equalTo(location)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UserDetails userDetails = snapshot.getValue(UserDetails.class);

                            if (userDetails.getUserType().equals("Salesman")) {
                                salesPersonList.add(userDetails);
                                salesPersonNameList.add(userDetails.getUserName());
                            }
                        }
                        onFetchSalesPersonListListener.onListFetched(salesPersonList, salesPersonNameList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void updateLeadDetails(OnUpdateLeadListener onUpdateLeadListener, LeadDetails updateLead) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("leadList").child(updateLead.getKey());
        myRef.setValue(updateLead);

        onUpdateLeadListener.onLeadUpdated();

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

    public void getLeadList(final OnFetchLeadListListener listener, String key, String value, long lastLead) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("leadList")
                .orderBy("date", Query.Direction.ASCENDING)
                .startAfter(lastLead)
                .limit(6)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<LeadDetails> leads = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                LeadDetails l = document.toObject(LeadDetails.class);
                                leads.add(l);

                            }
                            listener.onLeadAdded(leads);

                        } else {
                            Log.w("TAG", "Error getting documents.", task.getException());
                        }
                    }
                });

        /*DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("leadList");

        Query query;
        if (!key.equals("Admin"))
            query = databaseReference.orderByChild(key).equalTo(value);
        else
            query = databaseReference;
        // Query query = databaseReference.orderByKey().startAt(key).limitToLast(20);

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                LeadDetails leadDetails = dataSnapshot.getValue(LeadDetails.class);
                listener.onLeadAdded(leadDetails);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                LeadDetails leadDetails = dataSnapshot.getValue(LeadDetails.class);
                listener.onLeadChanged(leadDetails);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ERROR", "getChapterList(), onCancelled", new Exception(databaseError.getMessage()));
            }
        });*/
    }

    public void getUsersByUId(final OnFetchUserDetailsByUId onFetchUserDetailsByUId, String uId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("userList");

        myRef.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserDetails userDetails = dataSnapshot.getValue(UserDetails.class);
                onFetchUserDetailsByUId.onSuccess(userDetails);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onFetchUserDetailsByUId.fail();
            }
        });
    }

    public void setCurrentDeviceToken(String deviceToken, String key) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("userList");

        myRef.child(key).child("deviceToken").setValue(deviceToken);
    }

    public void makeNewNodeOfUserDetails(UserDetails userDetails) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("userList");

        myRef.child(userDetails.getuId()).setValue(userDetails);

        myRef.child(userDetails.getKey()).removeValue();
    }
}