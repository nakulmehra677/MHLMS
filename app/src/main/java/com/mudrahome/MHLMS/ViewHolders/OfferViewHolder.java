package com.mudrahome.MHLMS.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mudrahome.MHLMS.R;

public class OfferViewHolder extends RecyclerView.ViewHolder {
    public TextView offerTitle, offerDescription;
//    public ItemClickListener itemClickListener;

//    public void setItemClickListener(ItemClickListener itemClickListener) {
//        this.itemClickListener = itemClickListener;
//    }

    public OfferViewHolder(View itemView) {
        super(itemView);
        offerTitle = itemView.findViewById(R.id.offer_name);
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
