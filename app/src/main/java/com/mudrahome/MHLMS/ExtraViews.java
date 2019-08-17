package com.mudrahome.MHLMS;

import android.app.ProgressDialog;
import android.content.Context;

import com.mudrahome.MHLMS.Interfaces.AddFeature;

public class ExtraViews implements AddFeature {
    public ProgressDialog progress;

    @Override
    public void startProgressDialog(String message, Context context) {
        progress = new ProgressDialog(context);
        progress.setMessage(message);
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

    @Override
    public void dismissProgressDialog() {
        if (progress.isShowing())
            progress.dismiss();
    }
}
