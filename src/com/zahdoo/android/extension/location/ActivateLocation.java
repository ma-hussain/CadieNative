package com.zahdoo.android.extension.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.zahdoo.android.extension.alarm.Global;

/**
 * This is an example of implement an {@link BroadcastReceiver} for an alarm that
 * should occur once.
 */
public class ActivateLocation extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
		Log.d("CADIE", "In Activate Location  " );
		try {
			Intent cName = new Intent(context,LocationCheckService.class);
			context.startService(cName);
		}
	    catch (Exception e) 
    	{
	    	Log.d("CADIE", "Exception Activate Location - " + e.toString());
			Toast.makeText( context, " \n Error TEST 1 \nbRun " + Global.bRun, Toast.LENGTH_LONG).show();
		}
    }
}