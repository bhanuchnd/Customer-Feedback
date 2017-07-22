package com.feedback.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by shridhar on 2/7/17.
 */

public class SharedPrefUtils {

    public static void updateSyncStatus(Context context,boolean status) {
        SharedPreferences syncStatus = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = syncStatus.edit();
        editor.putBoolean("SyncStatus",status);
        editor.apply();
    }
    public static boolean getSyncStatus(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("SyncStatus",Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("syncstatus",false);
    }
}
