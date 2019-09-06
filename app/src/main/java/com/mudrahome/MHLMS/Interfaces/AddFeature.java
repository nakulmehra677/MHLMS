package com.mudrahome.MHLMS.Interfaces;

import android.content.Context;

public interface AddFeature {
    void startProgressDialog(String message, Context context);
    void dismissProgressDialog();
    void showToast(int message, Context context);
}
