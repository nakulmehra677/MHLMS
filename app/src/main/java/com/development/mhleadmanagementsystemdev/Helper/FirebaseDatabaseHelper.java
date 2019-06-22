package com.development.mhleadmanagementsystemdev.Helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchSalesPersonListListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnFetchUserTypeListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnUpdateLeadListener;
import com.development.mhleadmanagementsystemdev.Interfaces.OnUploadCustomerDetailsListener;
import com.development.mhleadmanagementsystemdev.Models.CustomerDetails;
import com.development.mhleadmanagementsystemdev.Models.UserDetails;
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
    private boolean isAdmin = false;

    public FirebaseDatabaseHelper(Context context) {
        this.context = context;
    }

    /*public void countNoOfNodes(final CountNoOfNodesInDatabaseListener ofNodesInDatabaseListener, String nodePath) {
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
    }*/

    public void uploadCustomerDetails(OnUploadCustomerDetailsListener onUploadCustomerdetails,
                                      CustomerDetails customerDetails) {

        Log.i("No of Nodes", "Uploading");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("leadList").push();

        String key = myRef.getKey();
        customerDetails.setKey(key);
        myRef.setValue(customerDetails);
        onUploadCustomerdetails.onDataUploaded();
    }

    public void fetchSalesPersons(final OnFetchSalesPersonListListener onFetchSalesPersonListListener, String location) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("userList");
        Log.i("Users", "Fetching Users...");

        final List<String> salesPersonList = new ArrayList<>();
        salesPersonList.add("None");

        myRef.orderByChild("location").equalTo(location)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UserDetails userDetails = snapshot.getValue(UserDetails.class);

                            if (userDetails.getUserType().equals("Salesman")) {
                                Log.i("Users", userDetails.getUserName());
                                salesPersonList.add(userDetails.getUserName());
                            }
                        }
                        onFetchSalesPersonListListener.onListFetched(salesPersonList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void getUserType(final OnFetchUserTypeListener listener, String uId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("userList");

        myRef.orderByChild("uId").equalTo(uId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UserDetails userDetails = snapshot.getValue(UserDetails.class);
                            listener.onSuccess(userDetails.getUserType());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    /*public void signUpAccount(final SignUpAccountListener onSignUpAccountListener, String strMail, String strPassword, final String strUserName) {
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
    }*/

    /*public void uploadNewUserDetails(OnUploadNewUserDetailsListener onUploadNewUserDetailsListener, UserDetails teleCallerDetails, long nodes) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("userList").child(String.valueOf(nodes));

        myRef.setValue(teleCallerDetails);

        onUploadNewUserDetailsListener.dataUploaded();
    }*/

    /*public void fetchUserList(final OnFetchUserListListener onFetchUserListListener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("userList");

        // My top posts by number of stars
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserDetails teleCallerDetails;
                List<String> list = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    teleCallerDetails = postSnapshot.getValue(UserDetails.class);
                    list.add(teleCallerDetails.getUserName());
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
    }*/

    /*public void checkAdmin(final OnCheckAdminListener onCheckAdminListener, final String mail) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("adminList");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                    if (mail.equals(postSnapshot.getValue(String.class))) {
                        isAdmin = true;
                        break;
                    }
                onCheckAdminListener.onSuccess(isAdmin);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException());
                onCheckAdminListener.onFailer();
            }
        });
    }*/

    public void updateLeadDetails(OnUpdateLeadListener onUpdateLeadListener, CustomerDetails updateLead) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("leadList").child(updateLead.getKey());
        myRef.setValue(updateLead);

        onUpdateLeadListener.onLeadUpdated();

    }
}