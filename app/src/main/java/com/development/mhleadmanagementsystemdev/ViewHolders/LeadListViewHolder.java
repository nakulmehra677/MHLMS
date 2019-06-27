package com.development.mhleadmanagementsystemdev.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.development.mhleadmanagementsystemdev.Interfaces.ItemClickListener;
import com.development.mhleadmanagementsystemdev.R;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;

public class LeadListViewHolder extends RecyclerView.ViewHolder {
    public TextView name, contact, propertyType, employment, loanType,
            location, loanAmount, remarks, assignedTo, status, date, telecallerOptionMenu, salesmanOptionMenu;
    public ItemClickListener itemClickListener;
    public ExpandableLinearLayout expandableLinearLayout;
    public LinearLayout button;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public LeadListViewHolder(View itemView, String currentUserType) {
        super(itemView);
        name = itemView.findViewById(R.id.customer_name);
        contact = itemView.findViewById(R.id.customer_contact);
        propertyType = itemView.findViewById(R.id.customer_property);
        employment = itemView.findViewById(R.id.employment);
        loanType = itemView.findViewById(R.id.customer_loan_type);
        location = itemView.findViewById(R.id.customer_location);
        loanAmount = itemView.findViewById(R.id.customer_loan_amount);
        remarks = itemView.findViewById(R.id.customer_remarks);
        assignedTo = itemView.findViewById(R.id.assigned_to);
        status = itemView.findViewById(R.id.status);
        date = itemView.findViewById(R.id.date);

        telecallerOptionMenu = itemView.findViewById(R.id.menu_option);

        expandableLinearLayout = itemView.findViewById(R.id.expandable_layout);
        button = itemView.findViewById(R.id.list_root);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onClick(view, getAdapterPosition());
            }
        });
    }

    public void setName(String string) {
        name.setText(string);
    }

    public void setContact(String string) {
        contact.setText(string);
    }
}
