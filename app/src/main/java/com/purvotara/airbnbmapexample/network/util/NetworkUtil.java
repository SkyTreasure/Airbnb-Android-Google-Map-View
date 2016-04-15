package com.purvotara.airbnbmapexample.network.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.volley.NetworkError;

/**
 * Created by skytreasure on 30/12/15.</br>
 * <p/>
 * Network Utility methods goes in this class
 */
public class NetworkUtil {
    /**
     * Gives internet connection status - true if internet is connected
     *
     * @param context The context of the calling object
     * @return boolean isInternetConnected
     */
    public static boolean isInternetConnected(Context context) {
        final ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if there's any network problem
     *
     * @param error
     * @return
     */
    public static boolean isNetworkProblem(Object error) {
        return (error instanceof NetworkError);
    }
}
