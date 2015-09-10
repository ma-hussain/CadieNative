package com.zahdoo.android.extension.location;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import com.google.gdata.util.common.base.StringUtil;
import com.zahdoo.android.extension.GCM.CommonUtilities;
import com.zahdoo.android.extension.GCM.ConnectionDetector;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class LocationActivity extends Activity {
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
	}
	
	private double currentLatitude;
	private double currentLongitude;
	// GPSTracker class
	private GPSTracker gps;
	private ConnectionDetector cd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		try
        {
			Log.e("CADIE LOCATION", "1" );
			
			 cd = new ConnectionDetector(getApplicationContext());
			 
	        // Check if Internet present
	        if (!cd.isConnectingToInternet()) {
	        	GetLocationFunction.loc.dispatchStatusEventAsync("LOCATION_FOUND","NO_INTERNET_CONNECTION");
	        	finish();
	        	return;
	        }
	        
        	String locName = "";
        	 		
      	   try {
      		   Log.e("CADIE LOCATION", "2" );
      		// create class object
 		        gps = new GPSTracker(LocationActivity.this);
 		        Log.e("CADIE LOCATION", "3" );

 				// check if GPS enabled		
 		        if(gps.canGetLocation()){
 		        	Log.e("CADIE LOCATION", "4" );
 		        	currentLatitude = gps.getLatitude();
 		        	currentLongitude = gps.getLongitude();
 		        	Log.e("CADIE LOCATION", "5" );
 		        	Geocoder geocoder = new Geocoder(LocationActivity.this, Locale.getDefault());
 		        	Log.e("CADIE LOCATION", "6" );
 		        	
                String result = null;
                try {
                    List<Address> addressList = geocoder.getFromLocation(
                            currentLatitude, currentLongitude, 1);
                    Log.e("CADIE LOCATION", "7" );
                    if (addressList != null && addressList.size() > 0)
                    {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        
                        Log.e("CADIE LOCATION", "Address getMaxAddressLineIndex - " + address.getMaxAddressLineIndex() );
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        	
                        	Log.e("CADIE LOCATION", "Address Lines - " + address.getAddressLine(i) );
                        	
                        	if(i == address.getMaxAddressLineIndex() - 1 || i == address.getMaxAddressLineIndex() - 2)
                        		sb.append(address.getAddressLine(i)).append(" @ ");
                        	else
                        		sb.append(address.getAddressLine(i)).append(", ");
                        }
//  		                        sb.append(address.getLocality()).append("\n");
//  		                        sb.append(address.getPostalCode()).append("\n");
//  		                        sb.append(address.getCountryName());
                        result = sb.toString();
                        Log.e("CADIE LOCATION", "8" );
                        locName = result.substring(0, result.lastIndexOf(" @ "));
                        locName = locName.trim();
                        
                    }
	                } catch (IOException e) {
	                    Log.e("LOCATION ", "Unable connect to Geocoder", e);
	                }
	                
	                if(!locName.contentEquals(""))
	                {
	                	SQLiteDatabase cDataBase;
	            		
	            	    //String cDB_PATH = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadieMainDB.sqlite";
	            	    String cDB_PATH = "/data/data/air.com.zahdoo.cadie.debug/com.zahdoo.cadie.debug/Local Store/cadieMainDB.sqlite";

	            		cDataBase = SQLiteDatabase.openDatabase(cDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
	            		
	            		
	            		
	            		
	            		Cursor c = null;
	            		
	            		try{
	            			c = cDataBase.rawQuery("SELECT ULocationID,LocationName FROM Location WHERE LocationName = ?" , new String [] {locName});
	            			Log.d("CADIE Location Activity", "Cursor value -  "  + c); 
	            			if(c != null ) {
	            				if(c.moveToFirst())
	            				{
	            					Log.d("CADIE Location Activity", "Old Location Captured -  "  + locName); 
	            					GetLocationFunction.loc.dispatchStatusEventAsync("LOCATION_FOUND","LOCATION_ALREADY_REGISTERED^" + locName + "^" + c.getString(c.getColumnIndex("ULocationID")) );
	            				}
	            				else
	            				{
	            					Log.d("CADIE Location Activity", "New Location Captured -  "  + locName); 
	        	            		String locId = UUID.randomUUID().toString();
	        	            		
	        	            		ContentValues values = new ContentValues();
	        	            		values.put("ULocationID", locId);
	        	            		values.put("UserID", 1);
	        	            		values.put("LocationName", locName);
	        	            		values.put("DegLat", currentLatitude);
	        	            		values.put("DegLng", currentLongitude);
	        	            		values.put("RadLat", Math.toRadians(currentLatitude));
	        	            		values.put("RadLng", Math.toRadians(currentLongitude));
	        	            		values.put("SinRadLat", Math.sin(Math.toRadians(currentLatitude)));
	        	            		values.put("SinRadLng", Math.sin(Math.toRadians(currentLongitude)));
	        	            		values.put("CosRadLat", Math.cos(Math.toRadians(currentLatitude)));
	        	            		values.put("CosRadLng", Math.cos(Math.toRadians(currentLongitude)));
	        	            		values.put("isSynced", 0);
	        	            		values.put("isDeleted", 0);
	        	            		values.put("CreatedDateTime", getDateTime());
	        	            		values.put("LastModifiedDateTime", getDateTime());
	        	            		
	        	            		cDataBase.insert("Location", null, values);
	        	            		values.clear();
	        	            		
	        	            		
	        	            		values = new ContentValues();
	        	            		values.put("Value", locName);
	        	            		values.put("LastModifiedDateTime", getDateTime());
	        	            		
	        	            		
	        	        			String where1 = "Key = ?";  
	        	        			final String[] whereArgs1 = { "LastLocationOn" };
	        	        			int rowsAffected = cDataBase.update("Config", values, where1, whereArgs1);
	        	        			
	        	        			Log.d("CADIE Location Activity", "Update Config Rows Affected -  "  + rowsAffected);
	        	            		values.clear();
	        	            		
	        	            		GetLocationFunction.loc.dispatchStatusEventAsync("LOCATION_FOUND",locName + "^" + locId );
	            				}
	            			}
	            			
	            			cDataBase.close();
	            			
	            			if(c != null ) 
	                			c.close();
	            		}
	                	catch (Exception e)
	                	{
	                		Log.d("CADIE Location Activity","ShareHistory Insert Error - " + e.toString()); 
	            		}
	            		
	                }
	                else
	                {
	                	GetLocationFunction.loc.dispatchStatusEventAsync("LOCATION_FOUND",locName);
	                }
        		
                	finish();
 		        }
 		        else
 		        {
 		        	// can't get location
 		        	// GPS or Network is not enabled
 		        	// Ask user to enable GPS/network in settings
 		        	Log.e("CADIE LOCATION", "9" );
 		        	showSettingsAlert();
 		        	locName = "GPS_IS_OFF";
 		        	
 		        	GetLocationFunction.loc.dispatchStatusEventAsync("LOCATION_FOUND",locName);
 		        }
 		        
 				} 
          	    catch (Exception e) 
 				{
 					Log.e("CADIE LOCATION", "Can't get Address - " + e.toString());
 				}
              	   
              	 Log.e("CADIE LOCATION", "10" );
              	  
        } 
        catch (Exception e) 
        {
        	Log.d("CADIE Location" , e.toString() );
        	Toast.makeText(LocationActivity.this, "1 Location error\n" + e.toString() , Toast.LENGTH_LONG).show();
        }
//		finally{
//			Log.d("CADIE Location" , "onFinally call Finish Activity" );
//			finish();
//		}
    }
    
    
    /**
	 * Function to show settings alert dialog
	 * On pressing Settings button will lauch Settings Options
	 * */
	public void showSettingsAlert(){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(LocationActivity.this);
		
		
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
            	Log.d("CADIE Location" , "onCancel Finish Activity" );
            	LocationActivity.this.finish();
            }
        });
   	 
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
 
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
 
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
            	Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            	LocationActivity.this.startActivity(intent);
            	finish();
            }
        });
 
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            finish();
            }
        });
 
        // Showing Alert Message
        alertDialog.show();
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
