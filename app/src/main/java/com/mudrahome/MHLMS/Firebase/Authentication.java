package com.mudrahome.MHLMS.Firebase;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseUser;
import com.mudrahome.MHLMS.Interfaces.OnUserLogin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Authentication {
    private Context context;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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

    public void UpdatePassword(String oldpass, final String newpass , String emailid, final ProgressDialog progress, final AlertDialog alertDialog){


        progress.setMessage("Please wait...");
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        AuthCredential credential = EmailAuthProvider
                .getCredential(emailid, oldpass);

// Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newpass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, "Password Updated", Toast.LENGTH_SHORT).show();
                                        progress.dismiss();
                                        alertDialog.dismiss();
                                        /*Log.d(TAG, "Password updated");*/
                                    } else {
                                        Toast.makeText(context, "Something want wrong", Toast.LENGTH_SHORT).show();
                                        /*Log.d(TAG, "Error password not updated")*/
                                    }
                                }
                            });
                        } else {
                           /* Log.d(TAG, "Error auth failed")*/
                        }
                    }
                });


    }
}
