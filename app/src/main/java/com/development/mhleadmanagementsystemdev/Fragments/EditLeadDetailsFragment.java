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

@SuppressLint("ValidFragment")
public class EditLeadDetailsFragment extends AppCompatDialogFragment {
    private String strAssignedTo, strStatus;
    private OnSubmitClickListener listener;
    private ArrayAdapter<CharSequence> statusAdapter, assignedToAdapter;
    private Spinner assignedToSpinner, statusSpinner;

    public EditLeadDetailsFragment(OnSubmitClickListener listener) {
        this.listener = listener;
    }

    public static EditLeadDetailsFragment newInstance(String assignedTo, String status, OnSubmitClickListener listener) {

        EditLeadDetailsFragment f = new EditLeadDetailsFragment(listener);

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("assigned_to", assignedTo);
        args.putString("status", status);
        f.setArguments(args);

        Log.i("Assignedto", assignedTo);
        Log.i("status", status);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        strAssignedTo = getArguments().getString("assigned_to");
        strStatus = getArguments().getString("status");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_edit_lead_details_, null);

        final EditText assignTo = v.findViewById(R.id.assign_to);
        final EditText status = v.findViewById(R.id.status);


        /*// AssignedTo Spinner
        assignedToAdapter = new ArrayAdapter<CharSequence>(
                getContext(),
                android.R.layout.simple_spinner_item, arrayList);
        assignedToAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        assignedToSpinner.setAdapter(assignedToAdapter);
        assignedToSpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) getContext());

        statusAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.status, android.R.layout.simple_spinner_item);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);
        statusSpinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) getContext());

*/

        builder.setView(v)
                .setTitle("Edit details")
                .setPositiveButton("Make changes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        strAssignedTo = assignTo.getText().toString();
                        strStatus = status.getText().toString();
                        if (!strStatus.isEmpty() && !strAssignedTo.isEmpty()) {
                            listener.onSubmitClicked(strAssignedTo, strStatus);
                        }
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