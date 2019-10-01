package com.mudrahome.mhlms.fragments;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;

import com.mudrahome.mhlms.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewAllRemarksFragment extends AppCompatDialogFragment {

    ArrayList<String> remarksList;
    String userType;

    public ViewAllRemarksFragment(ArrayList<String> remarksList ,String salesmanOrCaller) {
        this.remarksList = remarksList;
        this.userType = salesmanOrCaller;
        // Required empty public constructor
    }

    static ViewAllRemarksFragment newInstance(ArrayList<String> remarksList,String salesmanOrCaller){
        return new ViewAllRemarksFragment(remarksList,salesmanOrCaller);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = getActivity().getLayoutInflater().inflate(R.layout.fragment_view_all_remarks, null);


        LinearLayout linearLayout = v.findViewById(R.id.showRemarksLinearLayout);
        TextView close = v.findViewById(R.id.closeRemarksView);
        TextView salesmanOrCaller = v.findViewById(R.id.salesmanOrCaller);

        salesmanOrCaller.setText(String.format("%s Remarks ", userType));
        linearLayout.removeAllViewsInLayout();
        linearLayout.removeAllViews();

        for(int i = remarksList.size() - 1; i >= 0; i--){

            if(!remarksList.get(i).isEmpty() && !remarksList.get(i).equals("None") && !remarksList.get(i).equals("Not available"))
            {
                TextView textView = new TextView(getContext());
                textView.setTextColor(getResources().getColor(R.color.coloBlack));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(60,0,60,20);
                textView.setTextSize(16);
                textView.setLayoutParams(layoutParams);

                LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,2);

                View view2 = new View(getContext());
                view2.setLayoutParams(l);
                view2.setBackgroundColor(getResources().getColor(R.color.colorGray));

                String remark = remarksList.get(i);

                if(remark.contains("@@")){
                    String[] remarkWithTime = remark.split("@@");

                    @SuppressLint("SimpleDateFormat") DateFormat sdf = new SimpleDateFormat("MMM dd,yyyy hh:mm a");
                    Date resultdate = new Date(Long.parseLong(remarkWithTime[1]));
                    String res = remarkWithTime[0];
                    if(!res.isEmpty()){
                        textView.setText(Html.fromHtml("<font color=\"#196587\">"+sdf.format(resultdate) + "</font>" + "<br>" + remarkWithTime[0]));
                        linearLayout.addView(textView);
                        linearLayout.addView(view2);
                    }

                }else {
                    textView.setText(remark);
                    linearLayout.addView(textView);
                    linearLayout.addView(view2);
                }

            }


        }


        builder.setCancelable(false);

        close.setOnClickListener(view -> dismiss());

        builder.setView(v);

        return builder.create();
    }


}
