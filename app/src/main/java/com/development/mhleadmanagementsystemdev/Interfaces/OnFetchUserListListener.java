package com.development.mhleadmanagementsystemdev.Interfaces;

import java.util.List;

public interface OnFetchUserListListener {
    void onUserListFetched(List<String> list);

    void onFailed();
}
