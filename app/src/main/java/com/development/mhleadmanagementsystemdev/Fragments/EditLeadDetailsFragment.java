package com.development.mhleadmanagementsystemdev.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.development.mhleadmanagementsystemdev.Activities.FeedCustomerDetailsActivity;
import com.development.mhleadmanagementsystemdev.R;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class EditLeadDetailsFragment extends AppCompatDialogFragment {
    private String strAssignedTo, strStatus;
    private OnSubmitClickListener listener;
    private ArrayAdapter<CharSequence> statusAdapter, assignedToAdapter;
    private Spinner assignedToSpinner, statusSpinner;
    List arrayList = new ArrayList();

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
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_edit_lead_details_, null);

        assignedToSpinner = v.findViewById(R.id.assign_to);
        statusSpinner = v.findViewById(R.id.status);

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

        statusAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.status, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);
        statusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                strStatus = parent.getItemAtPosition(position).toString();
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
                        if (!strAssignedTo.equals("None") && !strStatus.equals("None"))
                            listener.onSubmitClicked(strAssignedTo, strStatus);
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
        void onSubmitClicked(String dialogAssignedTo, String dialogStatus);
    }
}