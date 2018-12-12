package com.example.pleasestop.vkonkurse;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.crashlytics.android.Crashlytics;


public class BootCompletedIntentReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            Intent serviceIntent = new Intent(context, MyForeGroundService.class);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, MyForeGroundService.class));
            } else {
                context.startService(new Intent(context, MyForeGroundService.class));
            }

        }
    }
}
