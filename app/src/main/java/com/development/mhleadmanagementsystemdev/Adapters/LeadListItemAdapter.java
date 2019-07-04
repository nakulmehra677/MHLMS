package com.development.mhleadmanagementsystemdev.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.development.mhleadmanagementsystemdev.Activities.LeadsListActivity;
import com.development.mhleadmanagementsystemdev.Models.LeadDetails;
import com.development.mhleadmanagementsystemdev.R;
import com.development.mhleadmanagementsystemdev.ViewHolders.LeadListViewHolder;

import java.util.List;

import static com.development.mhleadmanagementsystemdev.Activities.BaseActivity.telecallerUser;
import static com.development.mhleadmanagementsystemdev.Activities.LeadsListActivity.currentUserType;

public class LeadListItemAdapter extends RecyclerView.Adapter<LeadListViewHolder> {
    private List<LeadDetails> leads;

    public LeadListItemAdapter(List<LeadDetails> leadDetails) {
        this.leads = leadDetails;
    }

    @NonNull
    @Override
    public LeadListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lead_list_item, parent, false);
        return new LeadListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final LeadListViewHolder holder, int i) {
        LeadDetails model = leads.get(i);

        holder.name.setText(model.getName());
        holder.contact.setText(model.getContactNumber());
        holder.employment.setText(model.getEmployment());
        holder.loanType.setText(model.getLoanType());
        holder.location.setText(model.getLocation());
        holder.loanAmount.setText("\u20B9" + model.getLoanAmount());
        holder.telecallerRemarks.setText(model.getTelecallerRemarks());
        holder.status.setText(model.getStatus());
        holder.date.setText(model.getDate());

        if (model.getEmployment().equals("Self Employed"))
            holder.employementType.setText(model.getEmploymentType());
        else
            holder.employementTypeLayout.setVisibility(View.GONE);

        if (model.getSalesmanRemarks().equals("None")) {
            holder.salesmanRemarksLayout.setVisibility(View.GONE);
            holder.salesmanReasonLayout.setVisibility(View.GONE);
        } else {
            holder.salesmanRemarks.setText(model.getSalesmanRemarks());
            holder.salesmanReason.setText(model.getSalesmanReason());
        }

        if (model.getLoanType().equals("Home Loan") || model.getLoanType().equals("Loan Against Property")) {
            holder.propertyType.setText(model.getPropertyType());
        } else {
            holder.tpropertyType.setVisibility(View.GONE);
        }
        if (currentUserType.equals(telecallerUser))
            holder.assignedTo.setText(model.getAssignedTo());
        else {
            holder.tassign.setText("Assginer");
            holder.assignedTo.setText(model.getAssigner());
        }

        holder.expandableLinearLayout.setInRecyclerView(true);
        holder.expandableLinearLayout.setExpanded(false);

        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return leads.size();
    }
}