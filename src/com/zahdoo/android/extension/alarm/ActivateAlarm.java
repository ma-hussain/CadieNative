package com.zahdoo.android.extension.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * This is an example of implement an {@link BroadcastReceiver} for an alarm that
 * should occur once.
 */
public class ActivateAlarm extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
    	if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) 
    	{

    	}
    	else //if(Global.bRun)
    	{
    		Intent cName = new Intent(context,AlarmActivity.class);
	    	
    		Log.d("CADIE", "In Activate Alarm  " );
    		try {
	    		WakeLocker.acquire(context); 
				cName.addCategory(Intent.CATEGORY_LAUNCHER);
				cName.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(cName);
    		}
		    catch (Exception e) 
	    	{
		    	Log.d("CADIE", "Exception Activate Alarm - " + e.toString());
				Toast.makeText( context, " \n Error TEST 1 \nbRun " + Global.bRun, Toast.LENGTH_LONG).show();
			}
    	}
    }
}