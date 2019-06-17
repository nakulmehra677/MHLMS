package com.development.mhleadmanagementsystemdev.Helper;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ListView;

import com.development.mhleadmanagementsystemdev.Interfaces.CountNoOfNodesInDatabaseListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchSalesPersonListListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchUserListListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnUploadCustomerDetailsListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnUploadNewUserDetailsListener;
import com.development.mhleadmanagementsystemdev.Interfaces.SignUpAccountListener;
import com.development.mhleadmanagementsystemdev.Models.CustomerDetails;
import com.development.mhleadmanagementsystemdev.Models.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDatabaseHelper {
    Context context;
    private long nodes = 0;

    public FirebaseDatabaseHelper(Context context) {
        this.context = context;
    }

    public void countNoOfNodes(final CountNoOfNodesInDatabaseListener ofNodesInDatabaseListener, String nodePath) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(nodePath);
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

    public void uploadCustomerDetails(OnUploadCustomerDetailsListener onUploadCustomerdetails,
                                      CustomerDetails customerDetails, long nodes) {

        Log.i("No of Nodes", "Uploading");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("leadsList").child(String.valueOf(nodes));

        myRef.setValue(customerDetails);

        onUploadCustomerdetails.onDataUploaded();
    }

    public void listAllUsers(final OnFetchSalesPersonListListener onFetchSalesPersonListListener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        Log.i("Users", "Fetching Users...");

        final List<String> salesPersonList = new ArrayList<>();
        salesPersonList.add("None");

        myRef.child("salesPersons").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String personName = postSnapshot.getValue(String.class);
                    salesPersonList.add(personName);
                }
                onFetchSalesPersonListListener.onListFetched(salesPersonList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void signUpAccount(final SignUpAccountListener onSignUpAccountListener, String strMail, String strPassword, final String strUserName) {
        final FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(strMail, strPassword)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(strUserName).build();

                            user.updateProfile(profileUpdates);

                            onSignUpAccountListener.signUpSuccessful(mAuth.getCurrentUser().getUid());
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithEmail:failure", task.getException());
                            onSignUpAccountListener.signUpFailed();
                        }
                    }
                });
    }

    public void uploadNewUserDetails(OnUploadNewUserDetailsListener onUploadNewUserDetailsListener, UserDetails userDetails, long nodes) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("userList").child(String.valueOf(nodes));

        myRef.setValue(userDetails);

        onUploadNewUserDetailsListener.dataUploaded();
    }

    public void fetchUserList(final OnFetchUserListListener onFetchUserListListener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("userList");

        // My top posts by number of stars
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserDetails userDetails;
                List<String> list = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    userDetails = postSnapshot.getValue(UserDetails.class);
                    list.add(userDetails.getUserName());
                }
                onFetchUserListListener.onUserListFetched(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                onFetchUserListListener.onFailed();
            }
        });
    }

}