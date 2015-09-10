package com.zahdoo.android.extension.GCM;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class AppUpdateIntentService extends IntentService
{
	private SQLiteDatabase cDataBase;
	 
    //private String cDB_PATH = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadieMainDB.sqlite";
    private String cDB_PATH = "/data/data/air.com.zahdoo.cadie.debug/com.zahdoo.cadie.debug/Local Store/cadieMainDB.sqlite";

    public AppUpdateIntentService()
    {
    	super("AppUpdateIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
    	Log.d(CommonUtilities.TAG, "onHandleIntent of  AppUpdateIntentService " );
    	getAppVersion(getApplicationContext());
    	
//		Bundle extras = intent.getExtras();
//	
//		if (!extras.isEmpty()) // has effect of unparcelling Bundle
//		{
//			Log.d(CommonUtilities.TAG, "Extras not empty  " );
//		}
		
		try{
    		cDataBase = SQLiteDatabase.openDatabase(cDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    		Cursor c;
			
			c = cDataBase.rawQuery("SELECT Key , Value FROM Config WHERE Key IN ('GCMRegisteredEmailID','GCMRegistrationID') GROUP BY Key" , null);
			
			Log.d(CommonUtilities.TAG, "Cursor value -  "  + c); 
			if(c != null ) 
			{
				Log.d(CommonUtilities.TAG, "Cursor value is not null  " ); 
				if(c.moveToFirst())
				{
					String keyName = "";
					String emailId = "";
					String oldRegId   = "";
					String newRegId   = "";
					
					do {
						keyName = c.getString(c.getColumnIndex("Key"));
						
						if(keyName.contentEquals("GCMRegisteredEmailID"))
							emailId = c.getString(c.getColumnIndex("Value"));
						else if(keyName.contentEquals("GCMRegistrationID")) 
							oldRegId   = c.getString(c.getColumnIndex("Value"));
						 
			        }while (c.moveToNext());
					
					Log.d(CommonUtilities.TAG, "Cursor value moveToFirst  " ); 
					Log.d(CommonUtilities.TAG, "Config Value emailId - " + emailId);
					
					if(!emailId.contentEquals(""))
					{
						Log.d(CommonUtilities.TAG, "User is registered to Cadie Account");
						
						//Check config table if AppVersion is already there or not 
						c = cDataBase.rawQuery("SELECT * FROM Config WHERE Key = 'AppVersion' LIMIT 1" , null);
						
						if(c != null ) 
						{
							ConnectionDetector cd;
							
							Log.d(CommonUtilities.TAG, "Cursor value is not null  " ); 
							if(c.moveToFirst())
							{
								String appVersion = c.getString(c.getColumnIndex("Value"));
								Log.d(CommonUtilities.TAG, "Config Value AppVersion - " + appVersion);
								Log.d(CommonUtilities.TAG, "AppVersion is already there in Config table. So Update " ); 
								
						        cd = new ConnectionDetector(getApplicationContext());
						 
						        // Check if Internet present
						        if (cd.isConnectingToInternet()) {
						            // Internet Connection is present
						            //Re-register GCM and update the new registration id in the server
						        	
						        	goForReRegistration(emailId,oldRegId,newRegId);
						        }
							}
							else//When AppVersion key is not in Config
							{
								Log.d(CommonUtilities.TAG, "AppVersion is not there in Config table. So insert " );
								
								ContentValues values = new ContentValues();
					    		values.put("UserID",  1 );
					    		values.put("Key", "AppVersion");
					    		values.put("Value", "0");
					    		values.put("LastModifiedDateTime", getDateTime());
					    		
					    		cDataBase.insert("Config", null, values);
					    		values.clear();
					    		
					    		Log.d(CommonUtilities.TAG, "AppVersion inserted into Config table " );
					    		
					    		cd = new ConnectionDetector(getApplicationContext());
								 
						        // Check if Internet present
						        if (cd.isConnectingToInternet()) {
						            // Internet Connection is present
						           //Re-register GCM and update the new registration id in the server
						        	
						        	goForReRegistration(emailId,oldRegId,newRegId);
						        }
							}
						}
					}
					else
					{
						//User is not registered yet
						//Check config table if AppVersion is already there or not 
						c = cDataBase.rawQuery("SELECT * FROM Config WHERE Key = 'AppVersion' LIMIT 1" , null);
						
						if(c != null ) 
						{
							Log.d(CommonUtilities.TAG, "Cursor value is not null  " ); 
							if(!c.moveToFirst())
							{
								Log.d(CommonUtilities.TAG, "AppVersion is not there in Config table. So insert " );
								
								ContentValues values = new ContentValues();
					    		values.put("UserID",  1 );
					    		values.put("Key", "AppVersion");
					    		values.put("Value", "0");
					    		values.put("LastModifiedDateTime", getDateTime());
					    		
					    		cDataBase.insert("Config", null, values);
					    		values.clear();
					    		
					    		Log.d(CommonUtilities.TAG, "AppVersion inserted into Config table " );
							}
						}
					}
				}
				else
				{
					//User is not registered yet
					//Check config table if AppVersion is already there or not 
					c = cDataBase.rawQuery("SELECT * FROM Config WHERE Key = 'AppVersion' LIMIT 1" , null);
					
					if(c != null ) 
					{
						Log.d(CommonUtilities.TAG, "Cursor value is not null  " ); 
						if(!c.moveToFirst())
						{
							Log.d(CommonUtilities.TAG, "AppVersion is not there in Config table. So insert " );
							
							ContentValues values = new ContentValues();
				    		values.put("UserID",  1 );
				    		values.put("Key", "AppVersion");
				    		values.put("Value", getAppVersion(getApplicationContext()));
				    		values.put("LastModifiedDateTime", getDateTime());
				    		
				    		cDataBase.insert("Config", null, values);
				    		values.clear();
				    		
				    		Log.d(CommonUtilities.TAG, "AppVersion inserted into Config table " );
						}
					}
				}
			}
			c.close();
		}
    	catch (Exception e)
    	{
    		Log.d(CommonUtilities.TAG,"Get Config Java Code Error - " + e.toString()); 
		}
    	finally{
    		if(cDataBase.isOpen())
    			cDataBase.close();
    	}
    }
    
    private int getAppVersion(Context context)
    {
 		try
 		{
 		    PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
 		    Log.d(CommonUtilities.TAG, "Cadie App Package Name : " + context.getPackageName());
 		    Log.d(CommonUtilities.TAG, "Cadie App Version is : " + packageInfo.versionCode);
 		    return packageInfo.versionCode;
 		}
 		catch (NameNotFoundException e)
 		{
 		    // should never happen
 		    //throw new RuntimeException("Could not get package name: " + e);
 			Log.d(CommonUtilities.TAG, "Could not get package name: " + e.toString());
 			return 0;
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
    
    private void goForReRegistration (String emailId, String oldRegId, String newRegId)
    {
    	GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
    	
//    	try {
//			gcm.unregister();
//			Log.i(CommonUtilities.TAG, "DEVICE UNREGISTERED on App update");
			
			try {
				newRegId = gcm.register(CommonUtilities.SENDER_ID);
				Log.i(CommonUtilities.TAG, "DEVICE RE-REGISTERED on App update");
			} catch (Exception e) {
				Log.i(CommonUtilities.TAG, "DEVICE RE-REGISTRATION ERROR on App update - " + e.toString());
				e.printStackTrace();
			}
			
//		} catch (Exception e) {
//			Log.i(CommonUtilities.TAG, "DEVICE UNREGISTRATION ERROR on App update - " + e.toString());
//			e.printStackTrace();
//		}
    	
    	
    	if(!oldRegId.contentEquals(newRegId) && !oldRegId.contentEquals("") && !newRegId.contentEquals(""))
    	{
    		String resMsg = updateRegIDInServer(oldRegId,newRegId,emailId);
    		
    		Log.i(CommonUtilities.TAG, "Server Response - " + resMsg );
    		
    		if(resMsg.contentEquals("SUCCESSFUL"))
    		{
    			ContentValues values = new ContentValues();                          
        		values.put("Value", getAppVersion(getApplicationContext()));
        		values.put("LastModifiedDateTime", getDateTime());
    			String where1 = "Key = ?";  
    			final String[] whereArgs1 = { "AppVersion" };
    			cDataBase.update("Config", values, where1, whereArgs1);
    			
    			Log.i(CommonUtilities.TAG, "Config table AppVersion updated " );
    			values.clear();
    			
    			values = new ContentValues();                          
        		values.put("Value", newRegId);
    			String where2 = "Key = ?";  
    			final String[] whereArgs2 = { "GCMRegistrationID" };
    			cDataBase.update("Config", values, where2, whereArgs2);
    			
    			Log.i(CommonUtilities.TAG, "Config table GCMRegistrationID updated " );
    			values.clear();
    		}
    	}
    	else
    	{
    		ContentValues values = new ContentValues();                          
    		values.put("Value", getAppVersion(getApplicationContext()));
			String where1 = "Key = ?";  
			final String[] whereArgs1 = { "AppVersion" };
			cDataBase.update("Config", values, where1, whereArgs1);
			
			Log.i(CommonUtilities.TAG, "Config table AppVersion updated " );
			values.clear();
			
    		Log.i(CommonUtilities.TAG, "New and old RegId are same or one of them is blank"  );
    		Log.i(CommonUtilities.TAG, "New RegId - "  + newRegId);
    		Log.i(CommonUtilities.TAG, "Old RegId - "  + oldRegId );
    	}
    }
    
    private String updateRegIDInServer(String oldRegId,String newRegId,String emailId)
    {
    	String msg = "";
		try
		{
			msg = ServerUtilities.reRegisterOnAppUpdate( oldRegId, newRegId , emailId );
               
		}
		catch (Exception ex)
		{
			Log.d(CommonUtilities.TAG, "4 REGISTER EXCEPTION: " + ex.toString());
		    msg = ex.toString();
		}
		
		return msg;
    }
}
