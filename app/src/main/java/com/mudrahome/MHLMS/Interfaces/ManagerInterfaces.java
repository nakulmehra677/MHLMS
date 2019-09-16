package com.mudrahome.MHLMS.Interfaces;

import com.mudrahome.MHLMS.Models.LeadDetails;

import java.util.List;

public class ManagerInterfaces {
    public interface GetLeadListListener {
        void onSuccess(List<LeadDetails> leadDetailsList);

        void onFail();
    }
}
