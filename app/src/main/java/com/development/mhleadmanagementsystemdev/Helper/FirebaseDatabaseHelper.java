package com.development.mhleadmanagementsystemdev.Helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchLeadListListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchSalesPersonListListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchUserDetailsListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnUpdateLeadListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnUploadCustomerDetailsListener;
import com.development.mhleadmanagementsystemdev.Models.LeadDetails;
import com.development.mhleadmanagementsystemdev.Models.UserDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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

    public void uploadCustomerDetails(OnUploadCustomerDetailsListener onUploadCustomerdetails,
                                      LeadDetails leadDetails) {

        Log.i("No of Nodes", "Uploading");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("leadList").push();

        String key = myRef.getKey();
        leadDetails.setKey(key);
        myRef.setValue(leadDetails);
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

    public void getLeadList(final OnFetchLeadListListener listener, int i) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("leadList");
        Query query = databaseReference.orderByKey().startAt(String.valueOf(i)).limitToFirst(20);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    List<LeadDetails> list = new ArrayList<>();
                    for (DataSnapshot d : dataSnapshot.getChildren())
                        list.add(d.getValue(LeadDetails.class));

                    listener.onSuccess(list);
                }
                listener.onFailer();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("ERROR", "getChapterList(), onCancelled", new Exception(databaseError.getMessage()));
            }
        });
    }
}