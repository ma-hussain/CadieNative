package com.zahdoo.android.extension.location;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

/**
 * This is an example of implement an {@link BroadcastReceiver} for an alarm that
 * should occur once.
 */
public class LocationReceiverOnBoot extends BroadcastReceiver
{
	// Restart service every 10 minutes
	private static final long REPEAT_TIME = 1000 * 60 * 10;

    private SQLiteDatabase cDataBase;
    private static String cDB_PATH = "/data/data/air.com.zahdoo.cadie.debug/com.zahdoo.cadie.debug/Local Store/cadieMainDB.sqlite";
    //private static String cDB_PATH = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadieMainDB.sqlite";
	
	
    @Override
    public void onReceive(Context context, Intent intent)
    {
         try 
         {	
        	Log.e("CADIE LOCATION", "On LocationReceiverOnBoot" );
        	
        	AlarmManager service = (AlarmManager) context
    				.getSystemService(Context.ALARM_SERVICE);
        	
    		Intent i = new Intent(context, ActivateLocation.class);
    		Log.e("Cadie Location Service ", "LocationReceiverOnBoot");
    		PendingIntent pending = PendingIntent.getBroadcast(context, 0, i,
    				PendingIntent.FLAG_CANCEL_CURRENT);
    		
        	cDataBase = SQLiteDatabase.openDatabase(cDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        	Cursor c;
			c = cDataBase.rawQuery("SELECT * " +
					"FROM EventReminder " +
					"WHERE UContextID IS NOT NULL AND UContextID != '' AND isDeleted = 0 ", null);
			
        	if(c != null ) {
		        if(c.moveToFirst()){
		        	Log.e("CADIE LOCATION", "On LocationReceiverOnBoot 51" );
		        	
		        	Calendar cal = Calendar.getInstance();
		    		// Start 30 seconds after boot completed
		    		cal.add(Calendar.SECOND, 60);
		    		//
		    		// Fetch every 30 seconds
		    		// InexactRepeating allows Android to optimize the energy consumption
		    		service.setInexactRepeating(AlarmManager.RTC_WAKEUP,
		    				cal.getTimeInMillis(), REPEAT_TIME, pending);
		        }
		        else
		        {
		        	Log.e("CADIE LOCATION", "On LocationReceiverOnBoot 66" );
		        	service.cancel(pending);
		        }
		    }
        	else
        	{
        		Log.e("CADIE LOCATION", "On LocationReceiverOnBoot 72" );
        		service.cancel(pending);
        	}
        	
        	c.close();
        	
        	if(cDataBase.isOpen())
        		cDataBase.close();
        	
		} catch (Exception e) {
			Log.e("CADIE LOCATION", "LocationReceiverOnBoot Exception - " + e.toString());
			Toast.makeText(context, "Error 12  " + e.toString() , Toast.LENGTH_SHORT).show();
		}
	}
}
