package com.development.mhleadmanagementsystemdev.Interfaces;

public interface CountNoOfNodesInDatabaseListener {
    void onFetched(long nodes);
    void failedToFetch();
}
