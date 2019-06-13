package com.development.mhleadmanagementsystemdev.Helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.development.mhleadmanagementsystemdev.Interfaces.CountNoOfNodesInDatabaseListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnUploadCustomerDetailsListener;
import com.development.mhleadmanagementsystemdev.Models.CustomerDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDatabaseHelper {
    Context context;
    private long nodes = 0;

    public FirebaseDatabaseHelper(Context context) {
        this.context = context;
    }

    public void countNoOfNodesInDatabase(final CountNoOfNodesInDatabaseListener ofNodesInDatabaseListener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        Log.i("No of Nodes", "Fetching nodes...");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("No of Nodes", String.valueOf(dataSnapshot.getChildrenCount()));
                nodes = dataSnapshot.getChildrenCount();
                ofNodesInDatabaseListener.onFetched(nodes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("No of Nodes", "Cancelled");
                ofNodesInDatabaseListener.failedToFetch();
            }
        });
    }

    public void uploadCustomerdetails(OnUploadCustomerDetailsListener onUploadCustomerdetails,
                                      CustomerDetails customerDetails, long nodes) {

        Log.i("No of Nodes", "Uploading");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(String.valueOf(nodes));

        myRef.setValue(customerDetails);

        onUploadCustomerdetails.onDataUploaded();
    }
}
