package com.example.pleasestop.vkonkurse;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.example.pleasestop.vkonkurse.Modules.AppModule;
import com.example.pleasestop.vkonkurse.Modules.NetModule;
import com.example.pleasestop.vkonkurse.Modules.RepositoryModule;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

public class MyApp extends Application {

    public static final String CHANNEL_ID = "chanelID";
    private static NetComponent mNetComponent;
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
        mNetComponent = DaggerNetComponent.builder()
                .repositoryModule(new RepositoryModule())
                .appModule(new AppModule(this))
                .netModule(new NetModule(getString(R.string.server)))
                .build();
        if(context == null)
            context = getApplicationContext();
        createNotificationChannel();
        VKSdk.initialize(getApplicationContext());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Example Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    public static Context getContext() {
        return context;
    }

    public static NetComponent getNetComponent() {
        return mNetComponent;
    }
}
