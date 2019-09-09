package com.mudrahome.MHLMS.Fragments;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mudrahome.MHLMS.Firebase.Authentication;
import com.mudrahome.MHLMS.Models.UserDetails;
import com.mudrahome.MHLMS.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePasswordFragment extends AppCompatDialogFragment {

    OnPasswordChangedClicked onPasswordChangedClicked;

    public ChangePasswordFragment(OnPasswordChangedClicked passwordChangedClicked) {
        // Required empty public constructor
        this.onPasswordChangedClicked = passwordChangedClicked;
    }

    public static ChangePasswordFragment newInstance(OnPasswordChangedClicked passwordChangedClicked){

        ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment(passwordChangedClicked);
        return changePasswordFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Log.d("Dialog ", "onCreateDialog: onOptionsItemSelected" + " dialog show");


        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_change_password,null);
        alertDialog.setView(view);




        final EditText currentPasswordEdittext = view.findViewById(R.id.currentPasswordfragemt);
        final EditText newPasswordEditText = view.findViewById(R.id.newPasswordfragment);
        final EditText confirmPasswordEditText = view.findViewById(R.id.confirmPasswordfragment);
        Button updatepassword = view.findViewById(R.id.updatepassword);
        TextView canceldialoftextview = view.findViewById(R.id.canceldialoftextview);


        updatepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String currentPassword = currentPasswordEdittext.getText().toString();
                final String newPassword = newPasswordEditText.getText().toString();
                final String confirmPassword = confirmPasswordEditText.getText().toString();

                if(currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()){

                    Toast.makeText(getContext(), "Please Fill all Fields" + currentPassword , Toast.LENGTH_SHORT).show();
                }else if(!newPassword.matches(confirmPassword)){
                    Toast.makeText(getContext(), "Confirm Password doesn't matched" ,  Toast.LENGTH_SHORT).show();
                }else {
                    onPasswordChangedClicked.onPasswordChange(currentPassword,newPassword);
                }
            }
        });

        canceldialoftextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });









        return /*super.onCreateDialog(savedInstanceState)*/alertDialog.create();
    }

    public interface OnPasswordChangedClicked{
        void onPasswordChange(String oldPassword,String newPassword);
    }

}
