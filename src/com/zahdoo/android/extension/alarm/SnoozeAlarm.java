package com.zahdoo.android.extension.alarm;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.R;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class SnoozeAlarm extends Activity
{
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
	}
    
    NotificationManager nMgr;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        Log.d("CADIE", "In Alarm Activity " );
        nMgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
    	Log.d("CADIE", "Snoozed Alarm Ringing");
    	
    	Bundle extras = getIntent().getExtras(); 
        final String title =  extras.getString("Title");
        String alarmType =  extras.getString("AlarmType");
    	
    	int resId = 0;
 		
 		try {
 			resId = getResources().getIdentifier("icon", "drawable", "air.com.zahdoo.cadie");
 			Log.d("CADIE ICON", "2 Icon Integer Value - " + resId);
 		} catch (Exception e) {
 			Log.d("CADIE ICON", "2 Icon Integer Exception - " + e.toString());
 		}
	    
	    AlertDialog.Builder ad = new AlertDialog.Builder(this); 
	   	ad.setMessage(title);
	   	
		//ad.setIcon(R.drawable.ic_dialog_alert);
	   	ad.setIcon(resId);
		ad.setTitle(alarmType+ " Alert").setCancelable(false);
		ad.setPositiveButton("Snooze", new DialogInterface.OnClickListener() 
		{
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Toast.makeText(SnoozeAlarm.this, "Alarm Snoozed for 10 mins", Toast.LENGTH_LONG).show();
				nMgr.cancelAll();
				WakeLocker.release();
				dialog.cancel();
				
				///Set the alarm snooze for 10 mins 
				Intent intent = new Intent(SnoozeAlarm.this, SnoozeAlarm.class);//this.getClass());
				//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				PendingIntent pendingIntent =
				    PendingIntent.getBroadcast(SnoozeAlarm.this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(System.currentTimeMillis());
				calendar.add(Calendar.SECOND, 600);
				
				AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
				
				Log.d("CADIE TIME", "System time - " + System.currentTimeMillis());
				Log.d("CADIE TIME", "Calendar time  - " + calendar.getTimeInMillis());
				
				// Schedule the alarm!
				//alarmManager.cancel(pendingIntent);
				alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
				
				//Show notification for the alarm snoozing
				int resId0 = 0;
	    		
	    		try {
	    			resId0 = getResources().getIdentifier("icon", "drawable", "air.com.zahdoo.cadie");
	    			Log.d("CADIE ICON", "2 Icon Integer Value - " + resId0);
					
					int icon =resId0;	//		dialog_warning;
					CharSequence tickerText = "Cadie - Alarm snoozed";  
					long when = System.currentTimeMillis();
					  
					CharSequence contentTitle = title;
					SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
					Date date = new Date();
					
					Calendar cl = Calendar. getInstance();
				    cl.setTime(date);
				    cl.add(Calendar.MINUTE, 10);
				    date = cl.getTime();
					
					Log.d("CADIE ALARM", "The current time is - " + dateFormat.format(date));
					Toast.makeText(SnoozeAlarm.this,"The current time is - " + dateFormat.format(date), Toast.LENGTH_LONG).show();
					//System.out.println(dateFormat.format(date));
					CharSequence contentText = "Alarm snoozing till " + date.getHours() + ":" +date.getMinutes()+ " .Touch to cancel.";
					
					//////////////////
					Intent notificationIntent = new Intent(SnoozeAlarm.this, RestartActivity.class);
					notificationIntent.putExtra("NotificationType", "CADIE_ALARM_SNOOZE");
					
					PendingIntent contentIntent = PendingIntent.getActivity(SnoozeAlarm.this,
					        0, notificationIntent,
					        PendingIntent.FLAG_CANCEL_CURRENT);

					NotificationManager mNotificationManager = (NotificationManager) SnoozeAlarm.this
					        .getSystemService(Context.NOTIFICATION_SERVICE);

					Uri alarmSound = RingtoneManager.getDefaultUri(AudioManager.STREAM_ALARM);
					if(alarmSound == null)
					{
						alarmSound = RingtoneManager.getDefaultUri(AudioManager.STREAM_RING);
						
						if(alarmSound == null){
							alarmSound = RingtoneManager.getDefaultUri(AudioManager.STREAM_NOTIFICATION);
						}
					}
					
					////////////////////////
					Resources res = SnoozeAlarm.this.getResources();
					NotificationCompat.Builder builder = new NotificationCompat.Builder(SnoozeAlarm.this);

					builder.setContentIntent(contentIntent)
								.setSmallIcon(R.drawable.ic_lock_idle_alarm)
					            .setLargeIcon(BitmapFactory.decodeResource(res, icon))
					            .setTicker(tickerText)
					            .setWhen(when)
					            .setAutoCancel(true)
					            .setSound(alarmSound,AudioManager.STREAM_ALARM)
					            .setContentTitle(contentTitle)
					            .setContentText(contentText)
								.setVibrate(new long[]{100, 200});
					
					mNotificationManager.notify("SNOOZE_ALARM",1, builder.build());
					
	    		} catch (Exception e) {
	    			Log.d("CADIE ICON", "2 Icon Integer Exception - " + e.toString());
	    		}
	    		finally
	    		{
					//if(Global.bRun)
						finish();
	    		}
				
			}
		});
		ad.setNeutralButton("Dismiss",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton){
				try{
					nMgr.cancelAll();
					WakeLocker.release();
					dialog.cancel();
					
					//if(Global.bRun)
						finish();
					
					
				}catch(Exception e) {
					Log.d("CADIE", "Dialog exception - " + e.toString());
					Toast.makeText(SnoozeAlarm.this, "Dialog exception - " + e.toString(), Toast.LENGTH_LONG).show();
				}
			}
		});
		ad.show();
		displayNotification(title,"CADIE " +alarmType);
    }
	
	public void displayNotification(String notificationString, String appName){
		try {
			
			int resId0 = 0;
    		
    		try {
    			resId0 = getResources().getIdentifier("icon", "drawable", "air.com.zahdoo.cadie");
    			
    			Log.d("CADIE ICON", "2 Icon Integer Value - " + resId0);
    		} catch (Exception e) {
    			Log.d("CADIE ICON", "2 Icon Integer Exception - " + e.toString());
    		}
			
			int icon =resId0;	//		dialog_warning;
			CharSequence tickerText = appName;  
			long when = System.currentTimeMillis();
			//Context context = getApplicationContext();
			  
			CharSequence contentTitle = notificationString;
			CharSequence contentText = appName;
			
			//////////////////
			Intent notificationIntent = new Intent(this, RestartActivity.class);
			notificationIntent.putExtra("NotificationType", "CADIE_ALARM");
			PendingIntent contentIntent = PendingIntent.getActivity(this,
			        0, notificationIntent,
			        PendingIntent.FLAG_CANCEL_CURRENT);

			NotificationManager mNotificationManager = (NotificationManager) this
			        .getSystemService(Context.NOTIFICATION_SERVICE);
			
			Uri alarmSound = RingtoneManager.getDefaultUri(AudioManager.STREAM_ALARM);
			if(alarmSound == null)
			{
				alarmSound = RingtoneManager.getDefaultUri(AudioManager.STREAM_RING);
				
				if(alarmSound == null){
					alarmSound = RingtoneManager.getDefaultUri(AudioManager.STREAM_NOTIFICATION);
				}
			}
			
			//Resources res = this.getResources();
			NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

			builder.setContentIntent(contentIntent)
						.setSmallIcon(icon)
			            //.setLargeIcon(BitmapFactory.decodeResource(res, icon))
			            .setTicker(tickerText)
			            .setWhen(when)
			            .setAutoCancel(true)
			            .setSound(alarmSound,AudioManager.STREAM_ALARM)
			            .setContentTitle(contentTitle)
			            .setContentText(contentText)
						.setVibrate(new long[]{100, 300, 200, 300});
			
			//Notification n = builder.build();

			Global.notificationID++;
			mNotificationManager.cancel("SNOOZE_ALARM", 1);
			mNotificationManager.notify("ALERT",Global.notificationID, builder.build());
			
			Log.d("CADIE", "Snoozed Alarm Notification ");

		} catch (Exception e) {
			Log.d("CADIE", "SnoozeAlarm Notification Exception 8 - " + e.toString());
			Toast.makeText(SnoozeAlarm.this,"Exception - " + e.toString(), Toast.LENGTH_LONG).show();
		}
	}
}