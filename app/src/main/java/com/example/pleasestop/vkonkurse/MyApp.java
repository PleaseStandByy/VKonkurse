package com.example.pleasestop.vkonkurse;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

public class MyApp extends Application {

    private static Context context;

    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (newToken == null) {
                Intent intent = new Intent(MyApp.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        if(context == null)
            context = getApplicationContext();
        VKSdk.initialize(getApplicationContext());
    }



    public static Context getContext() {
        return context;
    }
}
