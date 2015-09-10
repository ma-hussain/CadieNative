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
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class AlarmActivity extends Activity {
	
	private String aTitle 			= "";
	private String aEventType 		= "";
	private String RemindTime 		= "";
	private String UntillDate		= "";
	private String SelectedWeekDays = "";
	private Boolean isAlarmAnswered = false;
	private String eID 				= "";
	private int FrequencyID 		= -5;
	private int FrequencyValue 		= -5;
	private int z = 0;
	private SQLiteDatabase aDataBase;
	private SQLiteDatabase zDataBase;
    private String aDB_PATH = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadieAlertDB.sqlite";
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
	}
    
    NotificationManager nMgr;
    
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        Log.d("CADIE", "In Alarm Activity " );
        
        nMgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
    	
        try {
		    try{
		    	getAlarmData();
		    }
		    catch(Exception e){
		    	Toast.makeText(AlarmActivity.this, z + " Alarm Error " + e.toString(), Toast.LENGTH_SHORT).show();
		    }
		    
		    int resId = 0;
    		
    		try {
    			resId = getResources().getIdentifier("icon", "drawable", "air.com.zahdoo.cadie");
    			Log.d("CADIE ICON", "2 Icon Integer Value - " + resId);
    		} catch (Exception e) {
    			Log.d("CADIE ICON", "2 Icon Integer Exception - " + e.toString());
    		}
		    
		    AlertDialog.Builder ad = new AlertDialog.Builder(this); 
		   	ad.setMessage(Character.toUpperCase(aTitle.charAt(0)) + aTitle.substring(1));
		   	
		   	ad.setIcon(resId);
			ad.setTitle(aEventType+ " Alert").setCancelable(false);
			ad.setPositiveButton("Snooze", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Toast.makeText(AlarmActivity.this, "Alarm Snoozed for 10 mins", Toast.LENGTH_LONG).show();
					
					nMgr.cancelAll();
					WakeLocker.release();
					isAlarmAnswered = true;
					dialog.cancel();
					
					///Set the alarm snooze for 10 mins 
					Intent intent = new Intent(AlarmActivity.this, SnoozeAlarm.class);//this.getClass());
					//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra("Title", Character.toUpperCase(aTitle.charAt(0)) + aTitle.substring(1));
					intent.putExtra("AlarmType", aEventType);
					
					PendingIntent pendingIntent =
					    PendingIntent.getActivity(AlarmActivity.this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		
					Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(System.currentTimeMillis());
					calendar.add(Calendar.SECOND, 600);
					
					AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
					
					Log.d("CADIE TIME", "System time - " + System.currentTimeMillis());
					Log.d("CADIE TIME", "Calendar time  - " + calendar.getTimeInMillis());
					
					// Schedule the alarm!
					alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
					
					//Show notification for the alarm snoozing
					int resId0 = 0;
		    		
		    		try {
		    			resId0 = getResources().getIdentifier("icon", "drawable", "air.com.zahdoo.cadie");
		    			Log.d("CADIE ICON", "2 Icon Integer Value - " + resId0);
						
						int icon =resId0;	//		dialog_warning;
						CharSequence tickerText = "Cadie - Alarm snoozed";  
						long when = System.currentTimeMillis();
						//Context context = getApplicationContext();
						CharSequence contentTitle = Character.toUpperCase(aTitle.charAt(0)) + aTitle.substring(1);
						SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
						Date date = new Date();
						
						Calendar cl = Calendar. getInstance();
					    cl.setTime(date);
					    cl.add(Calendar.MINUTE, 10);
					    date = cl.getTime();
						
						Log.d("CADIE ALARM", "The current time is - " + dateFormat.format(date));
						Toast.makeText(AlarmActivity.this,"The current time is - " + dateFormat.format(date), Toast.LENGTH_LONG).show();
						CharSequence contentText = "Alarm snoozing till " + date.getHours() + ":" +date.getMinutes()+ " .Touch to cancel.";
						
						//////////////////
						Intent notificationIntent = new Intent(AlarmActivity.this, RestartActivity.class);
						notificationIntent.putExtra("NotificationType", "CADIE_ALARM_SNOOZE");
						
						PendingIntent contentIntent = PendingIntent.getActivity(AlarmActivity.this,
						        0, notificationIntent,
						        PendingIntent.FLAG_CANCEL_CURRENT);
	
						NotificationManager mNotificationManager = (NotificationManager) AlarmActivity.this
						        .getSystemService(Context.NOTIFICATION_SERVICE);
	
						Uri alarmSound = RingtoneManager.getDefaultUri(AudioManager.STREAM_ALARM);
						if(alarmSound == null)
						{
							alarmSound = RingtoneManager.getDefaultUri(AudioManager.STREAM_RING);
							
							if(alarmSound == null)
								alarmSound = RingtoneManager.getDefaultUri(AudioManager.STREAM_NOTIFICATION);
						}
						
						////////////////////////
						Resources res = AlarmActivity.this.getResources();
						NotificationCompat.Builder builder = new NotificationCompat.Builder(AlarmActivity.this);
	
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
						if(Global.bRun)
							finish();
		    		}
					
				}
			});
			ad.setNeutralButton("Dismiss",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton){
					try{
						nMgr.cancelAll();
						WakeLocker.release();
						isAlarmAnswered = true;
						dialog.cancel();
						
						if(Global.bRun)
							finish();
					}catch(Exception e)
					{
						Log.d("CADIE", "Dialog exception - " + e.toString());
						Toast.makeText(AlarmActivity.this, "Dialog exception - " + e.toString(), Toast.LENGTH_LONG).show();
					}
				}
			});
			ad.show();
		    
		    displayNotification(Character.toUpperCase(aTitle.charAt(0)) + aTitle.substring(1),"CADIE " +aEventType);
		} 
        catch (Exception e) 
		{
			Toast.makeText(AlarmActivity.this, "A\n" + e.toString(), Toast.LENGTH_SHORT).show();
		}
        finally{
        	if(Global.bRun){
        		updateAlert();
    			
    			Global.AlertID   = 0;
    		  	Global.aTitle	 = "";
    			new CalculateAlarmActivity();
        	}
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		try {
			if(isAlarmAnswered )
			{
				Global.bRun = true;
		        Intent i = new Intent( Intent.ACTION_MAIN );
		        PackageManager manager = getPackageManager();
		        i = manager.getLaunchIntentForPackage( "air.com.zahdoo.cadie" );
		        i.addCategory( Intent.CATEGORY_LAUNCHER );
		        startActivity( i );
		        finish();
			}
		} catch (Exception e) {
			Toast.makeText(AlarmActivity.this, "On Resume Exception - " + e.toString(), Toast.LENGTH_LONG).show();
			Log.d("CADIE", "On Resume Exception - " + e.toString());
		}
	}
	
    private void getAlarmData(){
    	try {
			z = 100;
			String zDB_PATH = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadieMainDB.sqlite";//cadieDB.db";
			aDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
			z = 101;
			
			Cursor c;
			
			c = aDataBase.rawQuery("SELECT AlertID, " +
					"EventReminderID, " +
					"EventReminderTitle, " +
					"EventReminderType, " +
					"RemindAt, " +
					"RemindDate, " +
					"RemindTime, " +
					"UntillDate, " +
					"Status, " +
					"FrequencyID, " +
					"FrequencyValue, " +
					"SelectedWeekDays " +
					"FROM ADBAlerts " +
					"WHERE Status = 'Activated' " +
					"ORDER BY RemindAt Asc Limit 1", null);
			
			if(c != null ) {
			    if(c.moveToFirst()){
			        do {
			        	eID   				= c.getString(c.getColumnIndex("EventReminderID"));
			          	FrequencyID 		= Integer.parseInt(c.getString(c.getColumnIndex("FrequencyID")));
			          	FrequencyValue 		= Integer.parseInt(c.getString(c.getColumnIndex("FrequencyValue")));
			          	RemindTime 			= c.getString(c.getColumnIndex("RemindTime"));
			          	UntillDate 			= c.getString(c.getColumnIndex("UntillDate"));
			          	SelectedWeekDays 	= c.getString(c.getColumnIndex("SelectedWeekDays"));
			          	//RemindAt			= c.getString(c.getColumnIndex("RemindAt"));
						aTitle	 			= c.getString(c.getColumnIndex("EventReminderTitle"));
						//aStatus	 			= c.getString(c.getColumnIndex("Status"));
						aEventType 			= c.getString(c.getColumnIndex("EventReminderType"));
			        }while (c.moveToNext());
			    }
			}
			
			z = 102;
			zDataBase = SQLiteDatabase.openDatabase(zDB_PATH, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.OPEN_READWRITE );
			z = 103;
			c.close();
		} catch (Exception e) {
			Toast.makeText(AlarmActivity.this, "GET - " + z +"\n" + e.toString(), Toast.LENGTH_SHORT).show();
		}
    }
    
	private void updateAlert(){
		z = 0;
		Boolean isExpired 		= false;
		
		try {
			Calendar uDt = Calendar.getInstance();
			Calendar cM = Calendar.getInstance();
			
			if(FrequencyID == 0){
				z= 20;
				String where = "EventReminderID=?";  
				final String[] whereArgs = { eID };
				aDataBase.delete("ADBAlerts",  where, whereArgs);
				z= 21;
				
				String where1 = "UEventReminderID=?";  
				final String[] whereArgs1 = { eID };
				zDataBase.delete("Alerts",  where1, whereArgs1);
			}
			else{
				String[] arrUDt = UntillDate.split("\\/");
				String[] curTime =  RemindTime.split("\\:");
				uDt.set(Integer.parseInt(arrUDt[0]), Integer.parseInt(arrUDt[1]), Integer.parseInt(arrUDt[2]),Integer.parseInt(curTime[0]),Integer.parseInt(curTime[1]),Integer.parseInt(curTime[2]));
				uDt.add(Calendar.MONTH, -1);
				
				z = 1;
				int addDay = -100;
				
				if(uDt.compareTo(cM)==1){
					
					if(FrequencyID == 1){
						z = 2;
						cM.add(Calendar.DAY_OF_MONTH,1);
					}
					else if(FrequencyID == 2){
						z = 3;
						String[] alertDay = SelectedWeekDays.split("\\,");
						
						int i = 0;
				
						for (i=0; i<alertDay.length; i++){
							int temp = 0;
							
							temp = Integer.parseInt(alertDay[i]) - (cM.get(Calendar.DAY_OF_WEEK) - 1);
							
							if(addDay == -100)
								addDay = temp;
							else if((addDay <= 0) && (temp > 0)){
								addDay = temp;
								break;
							}
						}
						
						if (addDay < 0)
							addDay = (7 - (cM.get(Calendar.DAY_OF_WEEK) - 1)) + Integer.parseInt(alertDay[0]) ;
						else if(addDay == 0)
							addDay = 7;
						
						cM.add(Calendar.DAY_OF_MONTH,addDay);
					}
					else if(FrequencyID == 3){
						z = 4;
						cM.add(Calendar.MONTH, 1);
					}
					else if(FrequencyID == 4){
						z = 5;
						cM.add(Calendar.YEAR,1);
					}
					else if(FrequencyID == 5){
						z = 6;
						cM.add(Calendar.DAY_OF_MONTH,FrequencyValue);
					}
					else if(FrequencyID == 6){
						z = 7;
						cM.add(Calendar.DAY_OF_MONTH,(FrequencyValue * 7));
					}
					else if(FrequencyID == 7){
						z = 8;
						cM.add(Calendar.MONTH,FrequencyValue);
					}
					else if(FrequencyID == 8){
						z = 9;
						cM.add(Calendar.YEAR,FrequencyValue);
					}
					else if(FrequencyID == 9){
						z = 10;
						String[] alertDay = SelectedWeekDays.split("\\,");
						
						int i = 0;
				
						for (i=0; i<alertDay.length; i++){
							int temp = 0;
							temp = Integer.parseInt(alertDay[i]) - (cM.get(Calendar.DAY_OF_WEEK) - 1);
							
							if(addDay == -100)
								addDay = temp;
							else if((addDay <= 0) && (temp > 0)){
								addDay = temp;
								break;
							}
						}
						
						if (addDay < 0)
							addDay = (7 - (cM.get(Calendar.DAY_OF_WEEK) - 1)) + Integer.parseInt(alertDay[0]) ;
						else if(addDay == 0)
							addDay = 7;
						
						cM.add(Calendar.DAY_OF_MONTH,addDay + ((FrequencyValue-1) * 7));
					}
					else if(FrequencyID == 10){
						z = 11;
						cM.add(Calendar.DAY_OF_MONTH,2);
					}
					else if(FrequencyID == 11){
						z = 12;
						cM.add(Calendar.DAY_OF_MONTH,14);
					}
					else if(FrequencyID == 12){
						z = 13;
						cM.add(Calendar.MONTH,2);
					}
					else if(FrequencyID == 13){
						z = 14;
						cM.add(Calendar.YEAR,2);
					}
					else if(FrequencyID == 14){
						z = 15;
						String[] alertDay = SelectedWeekDays.split("\\,");
						
						int i = 0;
				
						for (i=0; i<alertDay.length; i++){
							int temp = 0;
							temp = Integer.parseInt(alertDay[i]) - (cM.get(Calendar.DAY_OF_WEEK) - 1);
							
							if(addDay == -100)
								addDay = temp;
							else if((addDay <= 0) && (temp > 0)){
								addDay = temp;
								break;
							}
						}
						
						if (addDay < 0)
							addDay = (7 - (cM.get(Calendar.DAY_OF_WEEK) - 1)) + Integer.parseInt(alertDay[0]) ;
						
						cM.add(Calendar.DAY_OF_MONTH,addDay + (2 * 7));
					}
				}
				else{
					isExpired = true;
				}
				
				if(isExpired)
				{
					String where = "EventReminderID=?";  
					final String[] whereArgs = { eID };
					aDataBase.delete("ADBAlerts",  where, whereArgs);
					
					String where1 = "UEventReminderID=?";  
					final String[] whereArgs1 = { eID };
					zDataBase.delete("Alerts",  where1, whereArgs1);
					isExpired = false;
				}
				else{
					z = 16;
					
					if(uDt.compareTo(cM)==1){
						
						z = 17;
						String aDate = cM.get(Calendar.YEAR)+"/"+ (cM.get(Calendar.MONTH)+1) +"/" + cM.get(Calendar.DAY_OF_MONTH) +" " + RemindTime;//cM.get(Calendar.HOUR) +":" +cM.get(Calendar.MINUTE) +":" +cM.get(Calendar.SECOND);
						z = 18;

						java.util.Date tmpDt = new java.util.Date(aDate);
		              	java.util.Date dt = tmpDt;
		              	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		              	
		              	z = 19;
//						int x = 0;
//						int y = 0;
						
						ContentValues dataToUpdate = new ContentValues();                          
						dataToUpdate.put("RemindAt", dt.getTime());
						dataToUpdate.put("Status", "Active");
						String where = "EventReminderID=?";  
						final String[] whereArgs = { eID };
						aDataBase.update("ADBAlerts", dataToUpdate, where, whereArgs);
						z = 10;
						
						ContentValues dataToUpdate1 = new ContentValues();                          
						dataToUpdate1.put("RemindAt", dateFormat.format(dt));
						String where1 = "UEventReminderID=?";  
						zDataBase.update("Alerts", dataToUpdate1, where1, whereArgs);
					}
					else{
						String where = "EventReminderID=?";  
						final String[] whereArgs = { eID };
						aDataBase.delete("ADBAlerts",  where, whereArgs);
						
						z=222;
						String where1 = "UEventReminderID=?";  
						final String[] whereArgs1 = { eID };
						zDataBase.delete("Alerts",  where1, whereArgs1);
						isExpired = false;
					}
				}
			}
			
			if(aDataBase.isOpen())
				aDataBase.close();
			
			if(zDataBase.isOpen())
				zDataBase.close();
			
		} catch (Exception e) {
			Toast.makeText(AlarmActivity.this, z + " Update Error \n" + e.toString(), Toast.LENGTH_LONG).show();
		}
	}
	
	protected void alertbox(String title, String mymessage)
    {
		new AlertDialog.Builder(AlarmActivity.this)
			.setMessage(mymessage).setTitle(title).setCancelable(false)
			.setNeutralButton("OK",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton){
					try{
						
						mNotificationManager.cancelAll();
						WakeLocker.release();
						finish();
					}catch(Exception e) {}
				}
			})
       .show();
	}
	
	NotificationManager mNotificationManager;
	
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
			
			////////////////////////
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
			
			Global.notificationID++;
			mNotificationManager.cancel("SNOOZE_ALARM", 1);
			mNotificationManager.notify("ALERT",Global.notificationID, builder.build());
			
			Log.d("CADIE", "Alarm Notification 1");
			Log.d("CADIE", "Alarm Notification 6");
			
			 if(!Global.bRun){
				 Log.d("CADIE", "Alarm Notification 7");
        		updateAlert();
    			
    			Global.AlertID   = 0;
    		  	Global.aTitle	 = "";
    			CalculateAlarm();
	         }

		} catch (Exception e) {
			Log.d("CADIE", "Alarm Notification Exception 8 - " + e.toString());
			Toast.makeText(AlarmActivity.this,"Exception - " + e.toString(), Toast.LENGTH_LONG).show();
		}
	}
	
	public void CalculateAlarm()
    { 
         try 
         {	
        	Boolean havingAlarm = false;
        	aDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        	Cursor c;
			c = aDataBase.rawQuery("SELECT AlertID, " +
					"EventReminderID, " +
					"EventReminderTitle, " +
					"EventReminderType, " +
					"RemindAt, " +
					"RemindDate, " +
					"RemindTime, " +
					"UntillDate, " +
					"Status, " +
					"FrequencyID, " +
					"FrequencyValue, " +
					"SelectedWeekDays " +
					"FROM ADBAlerts " +
					"ORDER BY RemindAt Asc Limit 1", null);
			
			AlarmManager am = (AlarmManager)AlarmActivity.this.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(AlarmActivity.this, ActivateAlarm.class);
        	PendingIntent sender = PendingIntent.getBroadcast(AlarmActivity.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			
			Global.aTitle = "1";
			Global.aStatus = "2";
			
        	if(c != null ) {
		        if(c.moveToFirst()){
		            do {
		            	havingAlarm 			= true;
		            	Global.AlertID   		= Integer.parseInt(c.getString(c.getColumnIndex("AlertID")));
		            	Global.EventReminderID	= c.getString(c.getColumnIndex("EventReminderID"));
		            	
		              	Global.FrequencyID 		= Integer.parseInt(c.getString(c.getColumnIndex("FrequencyID")));
		              	Global.FrequencyValue 	= Integer.parseInt(c.getString(c.getColumnIndex("FrequencyValue")));
		              	Global.aStatus		 	= c.getString(c.getColumnIndex("Status"));
		              	Global.EventReminderType= c.getString(c.getColumnIndex("EventReminderType"));
		              	Global.RemindDate 		= c.getString(c.getColumnIndex("RemindDate"));
		              	Global.RemindTime 		= c.getString(c.getColumnIndex("RemindTime"));
		              	Global.UntillDate 		= c.getString(c.getColumnIndex("UntillDate"));
		              	Global.SelectedWeekDays = c.getString(c.getColumnIndex("SelectedWeekDays"));
		              	
						Global.aTitle	 = c.getString(c.getColumnIndex("EventReminderTitle"));
		              	Long l = Long.parseLong(c.getString(c.getColumnIndex("RemindAt")));
		            	
		              	java.util.Date d = new java.util.Date();
		              	Calendar calendar1 = Calendar.getInstance();
		              	
		              	d = calendar1.getTime();
		              	d.getTime();
		              	
		              	if((l-d.getTime()) <= 10)
		              		Global.iInterval = (long) 10;
		              	else{
		              		Global.iInterval = l-d.getTime();
		              		Global.iInterval /=1000;
		              	}
		              	
		            }while (c.moveToNext());
		        }
		        
		        if(Global.aTitle.equals("1") && Global.aStatus.equals("2")){
		        	am.cancel(sender);
		        }
		        
		        ContentValues dataToUpdate = new ContentValues();                          
				dataToUpdate.put("Status", "Activated");
				String where = "EventReminderID=?" ; 
				final String[] whereArgs = { Global.EventReminderID };
				aDataBase.update("ADBAlerts", dataToUpdate, where, whereArgs );
		        aDataBase.close();
		        
		        if(havingAlarm){//Global.iInterval>0){

		        	String[] curTime =  Global.RemindTime.split("\\:");
					String AmPm = "AM";
					if(Integer.parseInt(curTime[0]) >= 12){
						curTime[0] = (Integer.parseInt(curTime[0]) - 12) + "";
						AmPm = "PM" ;
						if(Integer.parseInt(curTime[0]) == 0)
							curTime[0] = "12";
					}
					if(Integer.parseInt(curTime[1]) < 10 )
						curTime[1] = "0" + curTime[1];
					
					if(Integer.parseInt(curTime[2]) < 10 )
						curTime[2] = "0" + curTime[2];
					
					String RemndTme = curTime[0] + ":" + curTime[1] + ":" + curTime[2] + " " + AmPm;
					
					String[] rmndDt =  Global.RemindDate.split("\\/");
					if(Integer.parseInt(rmndDt[1]) < 10 )
						rmndDt[1] = "0" + rmndDt[1];
					
					if(Integer.parseInt(rmndDt[2]) < 10 )
						rmndDt[2] = "0" + rmndDt[2];
					
					String RemindDate = rmndDt[0] + "/" + rmndDt[1] + "/" + rmndDt[2];
					Global.EventReminderType = Global.EventReminderType.replace("s", "");
					
					if(Global.iInterval <= 40){
	              		Global.iInterval = (long) 40;
	              	}
					
			        Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(System.currentTimeMillis());
					calendar.add(Calendar.SECOND, Integer.parseInt(Global.iInterval+""));
					
					// Schedule the alarm!
					am.cancel(sender);
					am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
					
					if(Global.iInterval < 60){
						
					}
					else if(Global.iInterval < 3600){
						Long min = (Global.iInterval/60);
						Long sec = Global.iInterval - (min*60);
						
						Toast.makeText(AlarmActivity.this, "Next " + Global.EventReminderType +" \n'" + Global.aTitle.toUpperCase() + "'\nin " + min + " minutes "+ sec +" seconds\nat " + RemndTme + "    on " + RemindDate + "    -  CADIE", Toast.LENGTH_LONG).show();
						Toast.makeText(AlarmActivity.this, "Next " + Global.EventReminderType +" \n'" + Global.aTitle.toUpperCase() + "'\nin " + min + " minutes "+ sec +" seconds\nat " + RemndTme + "    on " + RemindDate + "    -  CADIE", Toast.LENGTH_LONG).show();
					}
					else if(Global.iInterval >= 3600 && Global.iInterval <= 86400){
						Long hrs = Global.iInterval / 3600;
						Long min = Global.iInterval - (hrs*3600); 
						
						if(min>=60){
							Long t = (min/60);
							Long sec = min - (t*60);
							
							Toast.makeText(AlarmActivity.this, "Next " + Global.EventReminderType +" \n'" + Global.aTitle.toUpperCase() + "'\nin " + hrs + " hour "+ t +" minutes "+ sec +" seconds\nat " + RemndTme + "    on " + RemindDate + "    -  CADIE", Toast.LENGTH_LONG).show();
							Toast.makeText(AlarmActivity.this, "Next " + Global.EventReminderType +" \n'" + Global.aTitle.toUpperCase() + "'\nin " + hrs + " hour "+ t +" minutes "+ sec +" seconds\nat " + RemndTme + "    on " + RemindDate + "    -  CADIE", Toast.LENGTH_LONG).show();
						}
						else{
							Toast.makeText(AlarmActivity.this, "Next " + Global.EventReminderType +" \n'" + Global.aTitle.toUpperCase() + "'\nin " + hrs + " hour "+ min +" minutes\nat " + RemndTme + "    on " + RemindDate + "    -  CADIE", Toast.LENGTH_LONG).show();
							Toast.makeText(AlarmActivity.this, "Next " + Global.EventReminderType +" \n'" + Global.aTitle.toUpperCase() + "'\nin " + hrs + " hour "+ min +" minutes\nat " + RemndTme + "    on " + RemindDate + "    -  CADIE", Toast.LENGTH_LONG).show();
						}
					}
					else if(Global.iInterval > 86400){
						Long days = Global.iInterval / 86400;
						int hrs = Integer.parseInt((Global.iInterval - (days*86400))/3600 + "");
						int min = Integer.parseInt((Global.iInterval - (days*86400) - (hrs*3600))/60 +"");
						
						if(min>=60){
							int t = (min/60);
							int sec = min - (t*60);
							
							Toast.makeText(AlarmActivity.this, "Next " + Global.EventReminderType +" \n'" + Global.aTitle.toUpperCase() + "'\nin " + days + " days " + hrs + " hour "+ t +" minutes "+ sec +" seconds\nat " + RemndTme + "    on " + RemindDate + "    -  CADIE", Toast.LENGTH_LONG).show();
							Toast.makeText(AlarmActivity.this, "Next " + Global.EventReminderType +" \n'" + Global.aTitle.toUpperCase() + "'\nin " + days + " days " + hrs + " hour "+ t +" minutes "+ sec +" seconds\nat " + RemndTme + "    on " + RemindDate + "    -  CADIE", Toast.LENGTH_LONG).show();
						}
						else
						{
							Toast.makeText(AlarmActivity.this, "Next " + Global.EventReminderType +" \n'" + Global.aTitle.toUpperCase() + "'\nin " + days + " days " + hrs + " hour "+ min +" minutes\nat " + RemndTme + "    on " + RemindDate + "    -  CADIE", Toast.LENGTH_LONG).show();
							Toast.makeText(AlarmActivity.this, "Next " + Global.EventReminderType +" \n'" + Global.aTitle.toUpperCase() + "'\nin " + days + " days " + hrs + " hour "+ min +" minutes\nat " + RemndTme + "    on " + RemindDate + "    -  CADIE", Toast.LENGTH_LONG).show();
						}
					}
        		}
		    }

        	c.close();
        	
        	if(aDataBase.isOpen())
        		aDataBase.close();
        	
		} catch (Exception e) {
			Toast.makeText(AlarmActivity.this, "Error 155  " + e.toString() , Toast.LENGTH_SHORT).show();
		}
	}
}
