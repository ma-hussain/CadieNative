package com.zahdoo.android.extension.location;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.zahdoo.android.extension.GCM.CommonUtilities;
import com.zahdoo.android.extension.GCM.GCMInitFunction;
import com.zahdoo.android.extension.GCM.RetrieveNoteService;
import com.zahdoo.android.extension.alarm.Global;
import com.zahdoo.android.extension.alarm.RestartActivity;
 
public class LocationCheckService extends IntentService
{
	// GPSTracker class
	GPSTracker gps;
	private double latitude  ;
	private double longitude ;
    
    private SQLiteDatabase cDataBase;
	
	//private String cDB_PATH = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadieMainDB.sqlite";
	private String cDB_PATH = "/data/data/air.com.zahdoo.cadie.debug/com.zahdoo.cadie.debug/Local Store/cadieMainDB.sqlite";

    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
	}
    
    public LocationCheckService()
    {
    	super("LocationCheckService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        try {
        	Log.e("CADIE LOCATION", "onHandleIntent LocationCheckService " );
        	gps = new GPSTracker(getApplicationContext());

			// check if GPS enabled		
	        if(gps.canGetLocation())
	        {
	        	Log.e("CADIE LOCATION", "onHandleIntent LocationCheckService GPS on " );
	        	
	        	/////////////////////
	        	cDataBase = SQLiteDatabase.openDatabase(cDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
	        	Cursor c;
				c = cDataBase.rawQuery("SELECT * " +
						"FROM EventReminder " +
						"WHERE UContextID IS NOT null AND UContextID != '' AND isDeleted = 0 ", null);
				
	        	if(c != null ) {
			        if(c.moveToFirst())
			        {
			        	latitude = gps.getLatitude();
			        	longitude = gps.getLongitude();
			        	
			        	Log.e("CADIE LOCATION", "LocationCheckService Lat - " + latitude);
			        	Log.e("CADIE LOCATION", "LocationCheckService Long - " + longitude);
			        	
			        	Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
		                String result = null;
		                try {
		                    List<Address> addressList = geocoder.getFromLocation(
		                            latitude, longitude, 1);
		                    if (addressList != null && addressList.size() > 0) {
		                        Address address = addressList.get(0);
		                        StringBuilder sb = new StringBuilder();
		                        Log.e("CADIE LOCATION", "Address getMaxAddressLineIndex - " + address.getMaxAddressLineIndex() );
		                        
		                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
		                        	if(i == address.getMaxAddressLineIndex() - 1 || i == address.getMaxAddressLineIndex() - 2)
		                        		sb.append(address.getAddressLine(i)).append(" @ ");
		                        	else
		                        		sb.append(address.getAddressLine(i)).append(", ");
		                        }
//		                        sb.append(address.getLocality()).append("\n");
//		                        sb.append(address.getPostalCode()).append("\n");
//		                        sb.append(address.getCountryName());
		                        result = sb.toString();
		                        result = result.substring(0, result.lastIndexOf(" @ "));
		                    }
		                } catch (IOException e) {
		                    Log.e("LOCATION ", "Unable connect to Geocoder", e);
		                }
		                
		                Log.e("CADIE LOCATION", "LocationCheckService Location Name - " + result);
		                ////////////////////////////////////////////Config -LastLocationOn
		                
		                
		                if(Global.bRun)
			        	{
			        		GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", "LATEST_LOCATION_NAME^" + result);
			        	}

						c = cDataBase.rawQuery("SELECT * FROM Config WHERE Key = 'LastLocationOn' LIMIT 1" , null);
						
						if(c != null ) 
						{
							if(c.moveToFirst())
							{
								String lastLocName =c.getString(c.getColumnIndex("Value"));
								if(result.contentEquals(lastLocName))
								{
									Log.e("LOCATION ", "Same as last location - " + lastLocName);
									
									NotificationManager mNotificationManager;
						        	mNotificationManager = (NotificationManager) getSystemService( RestartActivity.NOTIFICATION_SERVICE );
					             	mNotificationManager.cancel("LOCATION_REACHED", 1);
					        	
						        	cDataBase.close();
					    			
					    			if(c != null ) 
					    				c.close();
					    			
					    			return;
								}
								else
								{
									Log.e("LOCATION ", "New from last location - " + lastLocName);
									
									ContentValues values = new ContentValues();
				            		values.put("Value", result);
				            		values.put("LastModifiedDateTime", getDateTime());
				            		
				        			String where1 = "Key = ?";  
				        			final String[] whereArgs1 = { "LastLocationOn" };
				        			cDataBase.update("Config", values, where1, whereArgs1);
				            		values.clear();
								}
					        }
			        	}
						
		                
		                ///////////////////////////////////////////////
		                
		                double earthRadius = 6371.01;
		        		GeoLocation myLocation = GeoLocation.fromDegrees(latitude, longitude);
		        		double distance = 0.5;//km
		        		
		        		GeoLocation[] boundingCoordinates =	myLocation.boundingCoordinates(distance, earthRadius);
		        		
		        		boolean meridian180WithinDistance =
		        				boundingCoordinates[0].getLongitudeInRadians() > boundingCoordinates[1].getLongitudeInRadians();
		        				
//						cDataBase = SQLiteDatabase.openDatabase(cDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
			        	Cursor cLoc;
			        	
			        	cLoc = cDataBase.rawQuery("SELECT * FROM Location WHERE (RadLat >= ? AND RadLat <= ?) " +
		        				"AND (RadLng >= ? " +
		        				(meridian180WithinDistance ? "OR" : "AND") + " RadLng <= ?) ",
		        				new String [] {boundingCoordinates[0].getLatitudeInRadians() + "",boundingCoordinates[1].getLatitudeInRadians() + "",
		        				boundingCoordinates[0].getLongitudeInRadians() + "",boundingCoordinates[1].getLongitudeInRadians() + ""});
						
		        		Log.e("Cadie Location Service ", "boundingCoordinates[0].getLatitudeInRadians() - " + boundingCoordinates[0].getLatitudeInRadians());
		        		Log.e("Cadie Location Service ", "boundingCoordinates[0].getLongitudeInRadians() - " + boundingCoordinates[0].getLongitudeInRadians());
		        		Log.e("Cadie Location Service ", "boundingCoordinates[1].getLatitudeInRadians() - " + boundingCoordinates[1].getLatitudeInRadians());
		        		Log.e("Cadie Location Service ", "boundingCoordinates[1].getLongitudeInRadians() - " + boundingCoordinates[1].getLongitudeInRadians());
		        		
		        		Double sinRadLat;
		        		Double cosRadLat;
		        		Double radLng;
		        		String strLocMatched = "";
		        		int numLocs =0 ;
		        		
			        	if(cLoc != null ) {
					        if(cLoc.moveToFirst()){
					        	do {
					        		//"acos(sin(?) * sin(Lat) + cos(?) * cos(Lat) * cos(Lon - ?)) <= ?");
					        		sinRadLat   				= cLoc.getDouble(cLoc.getColumnIndex("SinRadLat"));
					        		cosRadLat   				= cLoc.getDouble(cLoc.getColumnIndex("CosRadLat"));
					        		radLng		   				= cLoc.getDouble(cLoc.getColumnIndex("RadLng"));
					        		
					        		Log.e("Cadie Location Service ", "radLat - " + cLoc.getDouble(cLoc.getColumnIndex("RadLat")));
					        		Log.e("Cadie Location Service ", "radLng - " + radLng);
					        		
					        		if(Math.acos(Math.sin(myLocation.getLatitudeInRadians()) * sinRadLat +
		    								Math.cos(myLocation.getLatitudeInRadians()) * cosRadLat * Math.cos(radLng - myLocation.getLongitudeInRadians())) <= (distance / earthRadius))
		    						{
					        			numLocs++;
					        			
					        			if(strLocMatched.contentEquals(""))
					        			{
					        				strLocMatched = cLoc.getString(cLoc.getColumnIndex("ULocationID"));
					        			}
					        			else
					        			{
					        				strLocMatched = strLocMatched + "^" + cLoc.getString(cLoc.getColumnIndex("ULocationID"));
					        			}
		    						}
					        		
						        }while (cLoc.moveToNext());
					        }
			        	}
			        	
			        	//cDataBase.close();
		    			
		    			if(cLoc != null ) 
		    				cLoc.close();
			        	
			        	Log.e("CADIE LOCATION", "LocationCheckService Number of locations matched - " + numLocs);
			        	
			        	if(!strLocMatched.contentEquals(""))
			        	{
			        		String strQuery =  "SELECT * " +
									"FROM EventReminder " +
									"WHERE UContextID IN (" ;
			        		
			        		for(int k = 0; k < numLocs; k++)
			        		{
			        			if(k == 0)
			        				strQuery += "?";
			        			else
			        				strQuery += ",?";
			        		}
									
									
							strQuery += ") AND isDeleted = 0 ";
							
							Log.e("CADIE LOCATION", "LocationCheckService strQuery - " + strQuery);
							
							String[] vLocs = strLocMatched.split("\\^");
			        				
							Log.e("CADIE LOCATION", "LocationCheckService strQuery - " + strQuery);
			        		c = cDataBase.rawQuery( strQuery, vLocs);
							
			        		Log.e("CADIE LOCATION", "LocationCheckService strQuery - " + strQuery);
			        		
			        		int numNotif = 0;
			        		
				        	if(c != null ) {
						        if(c.moveToFirst())
						        {
						        	if(Global.bRun)
						        	{
						        		GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", "LOCATION_REACHED^" + strLocMatched);
						        	}
						        	
						        	do{
						        		numNotif++;
						        		
						        		String locID		= c.getString(c.getColumnIndex("UContextID"));
						        		String locName		= c.getString(c.getColumnIndex("SelectedWeekDays"));
						        		String reminderTitle		= c.getString(c.getColumnIndex("EventReminderTitle"));
						              	
							        	NotificationManager mNotificationManager;
							        	mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
							    		
							    		Intent intent1 = new Intent(this, RetrieveNoteService.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							    		intent1.putExtra("NotificationType", "LOCATION_REACHED");
							    		intent1.putExtra("LocationID", strLocMatched);
							    		intent1.setData((Uri.parse("custom://LOCATION_REACHED")));
							    		
							    		PendingIntent contentIntent = PendingIntent.getService(this, 0, intent1,PendingIntent.FLAG_UPDATE_CURRENT);
							    		
							    		int resId0 = 0;
							    		
							    		try {
							    			resId0 = getResources().getIdentifier("icon", "drawable", getApplicationContext().getPackageName());
							    		} catch (Exception e) {
							    		}
							    		
							    		NotificationCompat.Builder mBuilder = 
							    			new NotificationCompat.Builder(this)
							    			.setSmallIcon(resId0)
							    			.setContentTitle("Cadie Reminder - " + reminderTitle)
							    			.setContentText(locName )
							    			.setTicker("Location Reminder - " + reminderTitle)
							    			.setAutoCancel(true)
							    			.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
							    			.setVibrate(new long[]{100, 100, 100, 100});
							    	
							    		mBuilder.setContentIntent(contentIntent);
							    		mNotificationManager.cancel("LOCATION_REACHED", numNotif);
							    		mNotificationManager.notify("LOCATION_REACHED",numNotif, mBuilder.build());
							    		
						        	 }while (c.moveToNext());
						        }
						        else
						        {
						        	NotificationManager mNotificationManager;
						        	mNotificationManager = (NotificationManager) getSystemService( RestartActivity.NOTIFICATION_SERVICE );
					             	mNotificationManager.cancel("LOCATION_REACHED", 1);
						        }
				        	}
				        	else
				        	{
				        		NotificationManager mNotificationManager;
					        	mNotificationManager = (NotificationManager) getSystemService( RestartActivity.NOTIFICATION_SERVICE );
				             	mNotificationManager.cancel("LOCATION_REACHED", 1);
				        	}
			        		
				        	Log.e("CADIE LOCATION", "LocationCheckService strQuery - " + strQuery);
				        	
			        	}
			        }
			        else
			        {
			        	NotificationManager mNotificationManager;
			        	mNotificationManager = (NotificationManager) getSystemService( RestartActivity.NOTIFICATION_SERVICE );
		             	mNotificationManager.cancel("LOCATION_REACHED", 1);
			        }
	        	}
	        	else
	        	{
	        		NotificationManager mNotificationManager;
		        	mNotificationManager = (NotificationManager) getSystemService( RestartActivity.NOTIFICATION_SERVICE );
	             	mNotificationManager.cancel("LOCATION_REACHED", 1);
	        	}
	        	
	        	if(cDataBase.isOpen())
	        		cDataBase.close();
    			
    			if(c != null ) 
    				c.close();
	        	//////////////////////
	        }
	        else
	        {
	        	if(Global.bRun)
	        	{
	        		GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", "GPS_STATUS^OFF");
	        	}
	        	Log.e("CADIE LOCATION", "LocationCheckService GPS is off" );
	        	// can't get location
	        	// GPS or Network is not enabled
	        	// Ask user to enable GPS/network in settings
	        	//gps.showSettingsAlert();
	        }
		} catch (Exception e) {
			Log.d("CADIE PROFILE PIC DOWNLOAD", "Exception - " + e.toString());
		}
        finally{
        	Log.d("CADIE PROFILE PIC DOWNLOAD", "Finally Stopself" );
        	stopSelf();
        }
    }
    
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        Date date = new Date();
        //Date fromDate = Calendar.getInstance().getTime();
        return dateFormat.format(date);
    }
    
}