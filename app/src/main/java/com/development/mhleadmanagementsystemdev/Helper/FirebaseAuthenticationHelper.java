package com.development.mhleadmanagementsystemdev.Helper;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.development.mhleadmanagementsystemdev.Interfaces.OnUserLoginListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseAuthenticationHelper {
    private Context context;

    public FirebaseAuthenticationHelper(Context context) {
        this.context = context;
    }

    public void loginUser(final OnUserLoginListener onUserLoginListener, String mail, String password) {

        final FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            onUserLoginListener.onSuccess(mAuth.getUid());
                        } else {
                            onUserLoginListener.onFailer();
                        }
                    }
                });
    }
}
