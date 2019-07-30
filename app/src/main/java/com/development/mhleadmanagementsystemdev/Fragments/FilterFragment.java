package com.development.mhleadmanagementsystemdev.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.development.mhleadmanagementsystemdev.R;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class FilterFragment extends AppCompatDialogFragment {
    private String strAssignedTo, strStatus;
    private OnSubmitClickListener listener;
    private List<String> arrayList = new ArrayList<>();
    private ListView list;

    public FilterFragment(List arrayList, OnSubmitClickListener listener) {
        this.listener = listener;
        this.arrayList = arrayList;
    }

    public static FilterFragment newInstance(List arrayList, OnSubmitClickListener listener) {

        FilterFragment f = new FilterFragment(arrayList, listener);
        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_show_sales_persons, null);

        list = v.findViewById(R.id.sales_person_list);


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