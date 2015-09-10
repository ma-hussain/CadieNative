package com.zahdoo.android.extension.GCM;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent) {
    	Log.d(CommonUtilities.TAG, "GCM Broadcast Receiver onReceive method ");
        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmIntentService.class.getName());
        
        
//        Intent service = new Intent(context, GcmIntentService.class);
//        startWakefulService(context, service);
        
        
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }

}
