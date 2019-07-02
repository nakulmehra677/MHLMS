package com.development.mhleadmanagementsystemdev.Interfaces;

import com.development.mhleadmanagementsystemdev.Models.UserDetails;

import java.util.List;

public interface OnFetchSalesPersonListListener<T> {
    void onListFetched(List<UserDetails> arrayList, List userName);

}
