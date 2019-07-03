package com.development.mhleadmanagementsystemdev.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.development.mhleadmanagementsystemdev.Interfaces.ItemClickListener;
import com.development.mhleadmanagementsystemdev.R;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;

public class LeadListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView name, contact, propertyType, employment, employementType, loanType,
            location, loanAmount, telecallerRemarks, salesmanRemarks, salesmanReason, assignedTo, status, date, optionMenu;
    public TextView tassign;
    public LinearLayout tpropertyType, salesmanRemarksLayout, salesmanReasonLayout, employementTypeLayout;
    public ItemClickListener itemClickListener;
    public ExpandableLinearLayout expandableLinearLayout;
    public LinearLayout button;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public LeadListViewHolder(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.customer_name);
        contact = itemView.findViewById(R.id.customer_contact);
        propertyType = itemView.findViewById(R.id.customer_property_type);
        employment = itemView.findViewById(R.id.employment);
        employementType = itemView.findViewById(R.id.employment_type);
        loanType = itemView.findViewById(R.id.customer_loan_type);
        location = itemView.findViewById(R.id.customer_location);
        loanAmount = itemView.findViewById(R.id.customer_loan_amount);
        telecallerRemarks = itemView.findViewById(R.id.telecaller_remarks);
        salesmanRemarks = itemView.findViewById(R.id.salesman_remarks);
        salesmanReason = itemView.findViewById(R.id.salesman_reason);
        assignedTo = itemView.findViewById(R.id.assigned_to);
        status = itemView.findViewById(R.id.status);
        date = itemView.findViewById(R.id.date);

        tassign = itemView.findViewById(R.id.tassign);
        tpropertyType = itemView.findViewById(R.id.tpropertyType);
        salesmanRemarksLayout = itemView.findViewById(R.id.salesman_remarks_layout);
        salesmanReasonLayout = itemView.findViewById(R.id.salesman_reason_layout);
        employementTypeLayout = itemView.findViewById(R.id.employement_type_layout);


        optionMenu = itemView.findViewById(R.id.menu_option);

        expandableLinearLayout = itemView.findViewById(R.id.expandable_layout);
        button = itemView.findViewById(R.id.contact_layout);

        itemView.setOnClickListener(this);

        /*itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onClick(view, getAdapterPosition());
            }
        });*/
    }

    public void setName(String string) {
        name.setText(string);
    }

    public void setContact(String string) {
        contact.setText(string);
    }

    @Override
    public void onClick(View v) {
        expandableLinearLayout.toggle();
    }
}
