package com.mudrahome.MHLMS.ViewHolders;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mudrahome.MHLMS.Interfaces.ItemClickListener;
import com.mudrahome.MHLMS.R;

public class LeadListViewHolder extends RecyclerView.ViewHolder {
    public TextView name, contact, loanAmount, loanType, location, assignedTo, status, date, assignText;
    public ItemClickListener itemClickListener;
    public LinearLayout assignLayout;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public LeadListViewHolder(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.customer_name);
        loanAmount = itemView.findViewById(R.id.customer_loan_amount);
        loanType = itemView.findViewById(R.id.customer_loan_type);
        location = itemView.findViewById(R.id.customer_location);
        assignedTo = itemView.findViewById(R.id.assigned_to);
        status = itemView.findViewById(R.id.status);
        date = itemView.findViewById(R.id.date);
        assignLayout = itemView.findViewById(R.id.assign_layout);
        assignText = itemView.findViewById(R.id.assign_text);
    }

    public void setName(String string) {
        name.setText(string);
    }

    public void setContact(String string) {
        contact.setText(string);
    }
}
