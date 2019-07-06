package com.development.mhleadmanagementsystemdev.ViewHolders;

import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.development.mhleadmanagementsystemdev.Activities.LeadsListActivity;
import com.development.mhleadmanagementsystemdev.Fragments.LeadDetailsFragment;
import com.development.mhleadmanagementsystemdev.Interfaces.ItemClickListener;
import com.development.mhleadmanagementsystemdev.R;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;

//import static com.development.mhleadmanagementsystemdev.Activities.BaseActivity.telecallerUser;

public class LeadListViewHolder extends RecyclerView.ViewHolder {
    public TextView name, contact, loanAmount, assignedTo, status, date, assignText;
    public ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public LeadListViewHolder(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.customer_name);
        loanAmount = itemView.findViewById(R.id.customer_loan_amount);
        assignedTo = itemView.findViewById(R.id.assigned_to);
        status = itemView.findViewById(R.id.status);
        date = itemView.findViewById(R.id.date);

        assignText = itemView.findViewById(R.id.assign_text);
    }

    public void setName(String string) {
        name.setText(string);
    }

    public void setContact(String string) {
        contact.setText(string);
    }
}
