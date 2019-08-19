package com.mudrahome.MHLMS.Interfaces;

import com.mudrahome.MHLMS.Models.OfferDetails;

import java.util.List;

public interface FetchOffer {
    void onSuccess(List<OfferDetails> details);
    void onFail();
}
