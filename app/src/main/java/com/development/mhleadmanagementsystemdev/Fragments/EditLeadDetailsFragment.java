package com.development.mhleadmanagementsystemdev.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.development.mhleadmanagementsystemdev.R;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class EditLeadDetailsFragment extends AppCompatDialogFragment {
    private String strAssignedTo;
    private OnSubmitClickListener listener;
    private ArrayAdapter<CharSequence> assignedToAdapter;
    private Spinner assignedToSpinner;
    List arrayList = new ArrayList();
    private EditText telecallerReason;


    public EditLeadDetailsFragment(List arrayList, OnSubmitClickListener listener) {
        this.listener = listener;
        this.arrayList = arrayList;
    }

    public static EditLeadDetailsFragment newInstance(List arrayList, OnSubmitClickListener listener) {

        EditLeadDetailsFragment f = new EditLeadDetailsFragment(arrayList, listener);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_telecaller_edit_lead_details_, null);

        assignedToSpinner = v.findViewById(R.id.assign_to);
        telecallerReason = v.findViewById(R.id.telecaller_reason);

        // AssignedTo Spinner
        assignedToAdapter = new ArrayAdapter<CharSequence>(
                getContext(),
                android.R.layout.simple_spinner_item, arrayList);
        assignedToAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assignedToSpinner.setAdapter(assignedToAdapter);
        assignedToSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                strAssignedTo = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builder.setView(v)
                .setTitle("Edit details")
                .setPositiveButton("Make changes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!telecallerReason.getText().toString().isEmpty())
                            listener.onSubmitClicked(strAssignedTo, telecallerReason.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setCancelable(false);

        return builder.create();
    }

    public interface OnSubmitClickListener {
        void onSubmitClicked(String dialogAssignedTo, String telecallerReason);
    }
}