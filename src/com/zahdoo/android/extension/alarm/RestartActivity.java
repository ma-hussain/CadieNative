package com.zahdoo.android.extension.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

public class RestartActivity extends Activity {
	
	private NotificationManager mNotificationManager;
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Bundle extras = getIntent().getExtras(); 
        String notificationType =  extras.getString("NotificationType");
        Log.d("CADIE ALARM", "Notification Type - " + notificationType);
        mNotificationManager = (NotificationManager) getSystemService( RestartActivity.NOTIFICATION_SERVICE );
        
        if(notificationType.contains("CADIE_ALARM_SNOOZE"))
        {
        	mNotificationManager.cancel("SNOOZE_ALARM", 1);
        	Intent intent = new Intent(RestartActivity.this, SnoozeAlarm.class);//this.getClass());
			PendingIntent pendingIntent =
			    PendingIntent.getBroadcast(RestartActivity.this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			
			// Cancel the alarm!
			alarmManager.cancel(pendingIntent);
        	finish();
        }
        else
        {
        	try {
    			int close_notify_id = getIntent().getIntExtra( "notification_id", 0 );
    			mNotificationManager.cancel(close_notify_id );
    		} catch (Exception e) {
    		
    		}
            finally
            {
    	        if( !Global.bRun )
    	        {
    	        	Global.bRun = true;
    		        Intent i = new Intent( Intent.ACTION_MAIN );
    		        PackageManager manager = getPackageManager();
    		        i = manager.getLaunchIntentForPackage( "air.com.zahdoo.cadie" );
    		        i.addCategory( Intent.CATEGORY_LAUNCHER );
    		        startActivity( i );
    		        System.exit( 0 );
    	        }
    	        finish();
            }
        }
    }
}
