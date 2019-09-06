package com.mudrahome.MHLMS.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.mudrahome.MHLMS.Fragments.LeadDetailsFragment;
import com.mudrahome.MHLMS.Models.LeadDetails;
import com.mudrahome.MHLMS.Models.OfferDetails;
import com.mudrahome.MHLMS.R;
import com.mudrahome.MHLMS.ViewHolders.LeadListViewHolder;
import com.mudrahome.MHLMS.ViewHolders.OfferViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;


public class LeadsItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private List<Object> items;
    private Context context;
    private /*Set<String>*/String currentUserType;
    private List<LeadDetails> allLeads;

    private final int OFFER = 0, LEADS = 1;

    public LeadsItemAdapter(List<Object> leadDetails, Context context, /*Set<String>*/String currentUserType) {
        this.items = leadDetails;
        this.context = context;
        this.currentUserType = currentUserType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lead_list_item, parent, false);
//        return new LeadListViewHolder(view);

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case OFFER:
                View v2 = inflater.inflate(R.layout.offer_item, parent, false);
                viewHolder = new OfferViewHolder(v2);
                break;

            default:
                /*View v1 = inflater.inflate(R.layout.lead_list_item, parent, false);*/
                /**
                 * Change by Himanshu
                 */

                View v1 = inflater.inflate(R.layout.list_lead_item_version,parent,false);

                viewHolder = new LeadListViewHolder(v1);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int i) {

        switch (holder.getItemViewType()) {
            case OFFER:
                OfferViewHolder vh1 = (OfferViewHolder) holder;
                final OfferDetails offerDetails = (OfferDetails) items.get(i);

                if (offerDetails != null) {
                    Random rnd = new Random();
//                    int color = Color.argb(255,
//                            rnd.nextInt(256),
//                            rnd.nextInt(256),
//                            rnd.nextInt(256));
//
//                    vh1.layout.setBackgroundColor(color);

                    vh1.offerTitle.setText(offerDetails.getTitle());
                    vh1.offerDescription.setText(offerDetails.getDescription());

                }
                break;

            default:
                LeadListViewHolder vh2 = (LeadListViewHolder) holder;

                final LeadDetails model = (LeadDetails) items.get(i);

                vh2.status.setText(model.getStatus());

                if(model.getStatus().matches("Closed")){

                    vh2.status.setTextColor(Color.RED);

                }else {
                    vh2.status.setTextColor(context.getResources().getColor(R.color.colorPrimary));

                }

                vh2.name.setText(model.getName());
                vh2.loanAmount.setText(model.getLoanAmount());
                vh2.loanType.setText(model.getLoanType());
                vh2.location.setText(model.getLocation());


                vh2.date.setText(model.getDate());

                if (currentUserType.equals(context.getString(R.string.telecaller)))
                    vh2.assignedTo.setText(model.getAssignedTo());
                else if (currentUserType.equals(context.getString(R.string.salesman))) {
                    vh2.assignText.setText("Assginer");
                    vh2.assignedTo.setText(model.getAssigner());
                } else
                    vh2.assignLayout.setVisibility(View.GONE);

                vh2.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LeadDetailsFragment leadDetailsFragment = new LeadDetailsFragment(model, context);
                        leadDetailsFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "f");
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    private Filter exampleFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Object> filterList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filterList.addAll(items);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Object item : items) {
                    LeadDetails details = (LeadDetails) item;
                    if (details.getName().toLowerCase().contains(filterPattern) ||
                            details.getAssignedTo().toLowerCase().contains(filterPattern) ||
                            details.getAssigner().toLowerCase().contains(filterPattern) ||
                            details.getLocation().toLowerCase().contains(filterPattern) ||
                            details.getStatus().toLowerCase().contains(filterPattern)) {
                        filterList.add(details);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filterList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            items.clear();
            if (results.values != null)
                items.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof OfferDetails) {
            return OFFER;
        } else {
            return LEADS;
        }
    }
}