package com.mudrahome.mhlms.firebase;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.mudrahome.mhlms.interfaces.OnPasswordChange;
import com.mudrahome.mhlms.interfaces.OnUserLogin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.mudrahome.mhlms.managers.ProfileManager;

public class Authentication {
    private Context context;


    public Authentication(Context context) {
        this.context = context;
    }

    public void loginUser(final OnUserLogin onUserLogin, String mail, String password) {

        final FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener((AppCompatActivity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            onUserLogin.onSuccess(mAuth.getUid());
                        } else {
                            onUserLogin.onFailer();
                        }
                    }
                });
    }

    public void UpdatePassword(String oldpass, final String newpass , String emailid, final OnPasswordChange onPasswordChange){
       final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final Firestore firestore = new Firestore(context);
        final ProfileManager profileManager = new ProfileManager();

        Log.d("Tag", "document Reference :  " + profileManager.getuId());


        AuthCredential credential = EmailAuthProvider
                .getCredential(emailid, oldpass);

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newpass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        /*Toast.makeText(context, "Password Updated", Toast.LENGTH_SHORT).show();*/
                                        firestore.setPassword(newpass,profileManager.getuId());
                                        onPasswordChange.onSucess("Password Updated");
                                    } else {
                                        onPasswordChange.onSucess("Try again");
                                        Toast.makeText(context, "Try again", Toast.LENGTH_SHORT).show();


                                    }
                                }
                            });
                        } else {
                                onPasswordChange.onSucess("Wrong Password");
                            /*Toast.makeText(context, "Wrong Password", Toast.LENGTH_SHORT).show();*/

                           /* Log.d(TAG, "Error auth failed")*/
                        }
                    }
                });


    }
}
