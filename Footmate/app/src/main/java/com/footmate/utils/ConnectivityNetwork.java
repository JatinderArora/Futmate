package com.footmate.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by sahil on 03-02-2016.
 */
public class ConnectivityNetwork {
    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

}
