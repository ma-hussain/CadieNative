package com.zahdoo.android.extension.alarm;

import java.util.Calendar;

import com.zahdoo.android.extension.location.ActivateLocation;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

public class CalculateAlarmActivity  {
	
	// Restart service every 10 minutes
	private static final long REPEAT_TIME = 1000 * 60 * 10;

	private SQLiteDatabase cDataBase;
	private static String cDB_PATH = "/data/data/air.com.zahdoo.cadie.debug/com.zahdoo.cadie.debug/Local Store/cadieMainDB.sqlite";
	//private static String cDB_PATH = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadieMainDB.sqlite";
		
	private SQLiteDatabase aDataBase;
	
	private static String aDB_PATH = "/data/data/air.com.zahdoo.cadie.debug/com.zahdoo.cadie.debug/Local Store/cadieAlertDB.sqlite";
    //private static String aDB_PATH = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadieAlertDB.sqlite";
	
    public CalculateAlarmActivity()
    {
    	Global.bRun = true;
    	Context context = AlarmInitFunction.ac.getActivity();
		
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

			AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, ActivateAlarm.class);
        	PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			
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
		        
		        if(havingAlarm)
		        {
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
					
					if(Global.iInterval <= 10)
	              		Global.iInterval = (long) 10;

			        Calendar calendar = Calendar.getInstance();
					calendar.setTimeInMillis(System.currentTimeMillis());
					calendar.add(Calendar.SECOND, Integer.parseInt(Global.iInterval+""));
					
					// Schedule the alarm!
					am.cancel(sender);
					am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
					
					if(Global.iInterval < 60){
						Toast.makeText(context, "Next " + Global.EventReminderType +" \n'" + Global.aTitle.toUpperCase() + "'\nin "  + Global.iInterval + " seconds" + "\nat " + RemndTme + "    on " + RemindDate + "    -  CADIE", Toast.LENGTH_LONG).show();
						Toast.makeText(context, "Next " + Global.EventReminderType +" \n'" + Global.aTitle.toUpperCase() + "'\nin "  + Global.iInterval + " seconds" + "\nat " + RemndTme + "    on " + RemindDate + "    -  CADIE", Toast.LENGTH_LONG).show();
					}
					else if(Global.iInterval < 3600){
						Long min = (Global.iInterval/60);
						Long sec = Global.iInterval - (min*60);
						
						Toast.makeText(context, "Next " + Global.EventReminderType +" \n'" + Global.aTitle.toUpperCase() + "'\nin " + min + " minutes "+ sec +" seconds\nat " + RemndTme + "    on " + RemindDate + "    -  CADIE", Toast.LENGTH_LONG).show();
						Toast.makeText(context, "Next " + Global.EventReminderType +" \n'" + Global.aTitle.toUpperCase() + "'\nin " + min + " minutes "+ sec +" seconds\nat " + RemndTme + "    on " + RemindDate + "    -  CADIE", Toast.LENGTH_LONG).show();
					}
					else if(Global.iInterval >= 3600 && Global.iInterval <= 86400){
						Long hrs = Global.iInterval / 3600;
						Long min = Global.iInterval - (hrs*3600); 
						
						if(min>=60){
							Long t = (min/60);
							Long sec = min - (t*60);
							
							Toast.makeText(context, "Next " + Global.EventReminderType +" \n'" + Global.aTitle.toUpperCase() + "'\nin " + hrs + " hour "+ t +" minutes "+ sec +" seconds\nat " + RemndTme + "    on " + RemindDate + "    -  CADIE", Toast.LENGTH_LONG).show();
							Toast.makeText(context, "Next " + Global.EventReminderType +" \n'" + Global.aTitle.toUpperCase() + "'\nin " + hrs + " hour "+ t +" minutes "+ sec +" seconds\nat " + RemndTme + "    on " + RemindDate + "    -  CADIE", Toast.LENGTH_LONG).show();
						}
						else{
							Toast.makeText(context, "Next " + Global.EventReminderType +" \n'" + Global.aTitle.toUpperCase() + "'\nin " + hrs + " hour "+ min +" minutes\nat " + RemndTme + "    on " + RemindDate + "    -  CADIE", Toast.LENGTH_LONG).show();
							Toast.makeText(context, "Next " + Global.EventReminderType +" \n'" + Global.aTitle.toUpperCase() + "'\nin " + hrs + " hour "+ min +" minutes\nat " + RemndTme + "    on " + RemindDate + "    -  CADIE", Toast.LENGTH_LONG).show();
						}
					}
					else if(Global.iInterval > 86400){
						Long days = Global.iInterval / 86400;
						int hrs = Integer.parseInt((Global.iInterval - (days*86400))/3600 + "");
						int min = Integer.parseInt((Global.iInterval - (days*86400) - (hrs*3600))/60 +"");
						
						if(min>=60){
							int t = (min/60);
							int sec = min - (t*60);
							
							Toast.makeText(context, "Next " + Global.EventReminderType +" \n'" + Global.aTitle.toUpperCase() + "'\nin " + days + " days " + hrs + " hour "+ t +" minutes "+ sec +" seconds\nat " + RemndTme + "    on " + RemindDate + "    -  CADIE", Toast.LENGTH_LONG).show();
							Toast.makeText(context, "Next " + Global.EventReminderType +" \n'" + Global.aTitle.toUpperCase() + "'\nin " + days + " days " + hrs + " hour "+ t +" minutes "+ sec +" seconds\nat " + RemndTme + "    on " + RemindDate + "    -  CADIE", Toast.LENGTH_LONG).show();
						}
						else
						{
							Toast.makeText(context, "Next " + Global.EventReminderType +" \n'" + Global.aTitle.toUpperCase() + "'\nin " + days + " days " + hrs + " hour "+ min +" minutes\nat " + RemndTme + "    on " + RemindDate + "    -  CADIE", Toast.LENGTH_LONG).show();
							Toast.makeText(context, "Next " + Global.EventReminderType +" \n'" + Global.aTitle.toUpperCase() + "'\nin " + days + " days " + hrs + " hour "+ min +" minutes\nat " + RemndTme + "    on " + RemindDate + "    -  CADIE", Toast.LENGTH_LONG).show();
						}
					}
        		}
		    }
        	
        	
        	//////////////////////////////
        	
        	
        	Log.e("CADIE LOCATION", "On Location Refresh CalculateAlarmActivity" );
        	
        	AlarmManager service = (AlarmManager) context
    				.getSystemService(Context.ALARM_SERVICE);
        	
    		Intent i = new Intent(context, ActivateLocation.class);
    		Log.e("Cadie Location Service ", "CalculateAlarmActivity");
    		PendingIntent pending = PendingIntent.getBroadcast(context, 0, i,
    				PendingIntent.FLAG_CANCEL_CURRENT);
    		
        	cDataBase = SQLiteDatabase.openDatabase(cDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        	Cursor cLoc;
			cLoc = cDataBase.rawQuery("SELECT * " +
					"FROM EventReminder " +
					"WHERE UContextID IS NOT null AND UContextID != '' AND isDeleted = 0 ", null);
			
        	if(cLoc != null ) {
		        if(cLoc.moveToFirst()){
		        	Log.e("CADIE LOCATION", "On CalculateAlarmActivity 51" );
		        	
		        	Calendar cal = Calendar.getInstance();
		    		// Start 30 seconds after boot completed
		    		cal.add(Calendar.SECOND, 5);
		    		//
		    		// Fetch every 30 seconds
		    		// InexactRepeating allows Android to optimize the energy consumption
		    		service.setInexactRepeating(AlarmManager.RTC_WAKEUP,
		    				cal.getTimeInMillis(), REPEAT_TIME, pending);
		        }
		        else
		        {
		        	Log.e("CADIE LOCATION", "On CalculateAlarmActivity 66" );
		        	service.cancel(pending);
		        }
		    }
        	else
        	{
        		Log.e("CADIE LOCATION", "On LocationReceiverOnBoot 72" );
        		service.cancel(pending);
        	}
        	
        	
        	///////////////////////////////

        	c.close();
        	cLoc.close();
        	
        	if(aDataBase.isOpen())
        		aDataBase.close();
        	
        	if(cDataBase.isOpen())
        		cDataBase.close();
        	
		} catch (Exception e) {
			Toast.makeText(context, "Error 12  " + e.toString() , Toast.LENGTH_SHORT).show();
		}
	}
}
