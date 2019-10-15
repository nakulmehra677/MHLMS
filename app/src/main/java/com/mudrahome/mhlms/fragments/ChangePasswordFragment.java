package com.mudrahome.mhlms.fragments;


import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mudrahome.mhlms.R;

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


        updatepassword.setOnClickListener(view1 -> {

            final String currentPassword = currentPasswordEdittext.getText().toString();
            final String newPassword = newPasswordEditText.getText().toString();
            final String confirmPassword = confirmPasswordEditText.getText().toString();

            if(currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()){

                Toast.makeText(getContext(), "Please Fill all Fields" + currentPassword , Toast.LENGTH_SHORT).show();
            }else if(newPassword.matches(confirmPassword)){

                if(newPassword.length() >= 6){
                    onPasswordChangedClicked.onPasswordChange(currentPassword,newPassword);
                }else {
                    Toast.makeText(getContext(), "You have to enter atleast 6 digit password", Toast.LENGTH_SHORT).show();
                }


            }else {
                Toast.makeText(getContext(), "Confirm Password doesn't matched" ,  Toast.LENGTH_SHORT).show();
            }
        });

        canceldialoftextview.setOnClickListener(view12 -> dismiss());

        return /*super.onCreateDialog(savedInstanceState)*/alertDialog.create();
    }

    public interface OnPasswordChangedClicked{
        void onPasswordChange(String oldPassword,String newPassword);
    }

}
