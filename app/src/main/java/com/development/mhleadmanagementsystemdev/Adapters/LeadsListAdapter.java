package com.development.mhleadmanagementsystemdev.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.development.mhleadmanagementsystemdev.Activities.LeadsListActivity;
import com.development.mhleadmanagementsystemdev.Interfaces.ItemClickListener;
import com.development.mhleadmanagementsystemdev.Models.CustomerDetails;
import com.development.mhleadmanagementsystemdev.R;
import com.github.aakira.expandablelayout.ExpandableLinearLayout;

import java.util.List;

public class LeadsListAdapter extends RecyclerView.Adapter<LeadsListAdapter.LeadListViewHolder> {
    private List<CustomerDetails> chapter;
    private Context context;
    private boolean showMenuItem;


    public class LeadListViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout root;
        public TextView name, contact, propertyType, employment, loanType,
                location, loanAmount, remarks, assignedTo, status, date, optionMenu;
        public ItemClickListener itemClickListener;
        public ExpandableLinearLayout expandableLinearLayout;
        public LinearLayout button;

        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public LeadListViewHolder(View itemView, boolean showMenuItem) {
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

            optionMenu = itemView.findViewById(R.id.telecaller_menu_option);
            expandableLinearLayout = itemView.findViewById(R.id.expandable_layout);
            button = itemView.findViewById(R.id.list_root);

            if (showMenuItem)
                optionMenu.setVisibility(View.VISIBLE);

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

    public LeadsListAdapter(List<CustomerDetails> chapter, Context context, boolean showMenuItem) {
        this.chapter = chapter;
        this.context = context;
        this.showMenuItem = showMenuItem;
    }

    @Override
    public void onBindViewHolder(@NonNull final LeadListViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        CustomerDetails model = chapter.get(position);

        holder.name.setText(model.getName());
        holder.contact.setText(model.getContactNumber());
        holder.propertyType.setText(model.getPropertyType());
        holder.employment.setText(model.getEmployment());
        holder.loanType.setText(model.getLoanType());
        holder.location.setText(model.getLocation());
        holder.loanAmount.setText(model.getLoanAmount());
        holder.remarks.setText(model.getRemarks());
        holder.assignedTo.setText(model.getAssignedTo());
        holder.status.setText(model.getStatus());
        holder.date.setText(model.getDate());

        holder.expandableLinearLayout.setInRecyclerView(true);
        holder.expandableLinearLayout.setExpanded(false);

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.expandableLinearLayout.toggle();
            }
        });

        holder.optionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.optionMenu);
                popupMenu.inflate(R.menu.lead_list_item_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.edit_details:
                                //showProgressDialog("Loading..", context);

                                //updateLead = model;
                                //firebaseDatabaseHelper.fetchSalesPersons(onFetchSalesPersonListListener(), model.getLocation());
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @NonNull
    @Override
    public LeadListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lead_list_item, parent, false);
        return new LeadListViewHolder(view, this.showMenuItem);
    }

    @Override
    public int getItemCount() {
        return chapter.size();
    }
}
