package com.zahdoo.android.extension.GCM;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AppUpdateBroadcastReceiver extends BroadcastReceiver
{
	
    @Override
    public void onReceive(Context context, Intent intent) 
    {
    	Log.d(CommonUtilities.TAG, "App Update Broadcast Receiver onReceive method ");
    	
    	try {
			// use this to start and trigger a service
			Intent i= new Intent(context, AppUpdateIntentService.class);
			// potentially add data to the intent
			//i.putExtra("KEY1", "Value to be used by the service");
			context.startService(i);
		}
    	catch (Exception e) 
    	{
    		Log.d(CommonUtilities.TAG, "App Update Broadcast Receiver Exception - " + e.toString());
			e.printStackTrace();
		} 
    }
}
