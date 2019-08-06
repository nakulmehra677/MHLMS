package com.mudrahome.MHLMS.Interfaces;

public interface OnAppUpdateListener {
    void checkUpdate(boolean updateAvailable);

    void Failed();
}
