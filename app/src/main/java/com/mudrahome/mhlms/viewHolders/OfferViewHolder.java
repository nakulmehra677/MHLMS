package com.mudrahome.mhlms.viewHolders;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mudrahome.mhlms.interfaces.ItemClickListener;
import com.mudrahome.mhlms.R;

public class OfferViewHolder extends RecyclerView.ViewHolder {
    public TextView offerTitle, offerDescription;
    public LinearLayout layout;
    public ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public OfferViewHolder(View itemView) {
        super(itemView);
        layout = itemView.findViewById(R.id.offer_layout);
        offerTitle = itemView.findViewById(R.id.offer_title);
        offerDescription = itemView.findViewById(R.id.offer_description);
    }

    public TextView getOfferTitle() {
        return offerTitle;
    }

    public void setOfferTitle(TextView offerTitle) {
        this.offerTitle = offerTitle;
    }

    public TextView getOfferDescription() {
        return offerDescription;
    }

    public void setOfferDescription(TextView offerDescription) {
        this.offerDescription = offerDescription;
    }
}
