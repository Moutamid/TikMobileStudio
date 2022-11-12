package com.moutamid.tikmobilestudio;

import android.content.Context;

public class SharedPreferences {
    private static final String APP_PREFS_NAME = "TikMobileStudio";
    private static final String COIN_PREFS_NAME = "NAME";

    private android.content.SharedPreferences mPreference;
    private android.content.SharedPreferences.Editor mPrefEditor;
    private Context context;

    public SharedPreferences(Context context) {
        this.mPreference = context.getSharedPreferences(APP_PREFS_NAME, Context.MODE_PRIVATE);
        this.mPrefEditor = mPreference.edit();
        this.context = context;
    }

    public void saveName(String coin){
        mPrefEditor.putString(COIN_PREFS_NAME, coin);
        mPrefEditor.commit();
    }

    public String getName() {
        String c = mPreference.getString(COIN_PREFS_NAME, "");
        return c;
    }
}
