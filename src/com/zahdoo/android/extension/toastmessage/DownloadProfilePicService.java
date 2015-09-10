package com.zahdoo.android.extension.toastmessage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.zahdoo.android.extension.GCM.ConnectionDetector;
 
public class DownloadProfilePicService extends IntentService
{
	private ConnectionDetector cd;
    
    private static String allFriendEmailIds;
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
	}
    
    public DownloadProfilePicService()
    {
    	super("DownloadProfilePicService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
    	cd = new ConnectionDetector(getApplicationContext());
    	Log.d("CADIE PROFILE PIC DOWNLOAD", "onHandleIntent " );
		
        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
        	//Toast.makeText(getApplicationContext(), "Internet Connection Error...\n" +
            //        "Please connect to working Internet connection", Toast.LENGTH_LONG).show();
        	
        	stopSelf();
            return;
        }
        
        try {
			Bundle extras = intent.getExtras();
			allFriendEmailIds 	= extras.getString("allEmailIds");
			Log.d("CADIE PROFILE PIC DOWNLOAD", "Friend Email IDs - " + allFriendEmailIds);
			
			String[] emailIDsArr = allFriendEmailIds.split("\\^");
			
			for(int i = 0; i < emailIDsArr.length ; i++)
			{
				startFriendsProfilePicDownload(emailIDsArr[i]);
				Log.d("CADIE PROFILE PIC DOWNLOAD", "Friend Email ID - " + emailIDsArr[i]);
			}
			
			UpdateConfig();
			
			//GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", "REFRESH_BUDDY_PIC");
			
		} catch (Exception e) {
			Log.d("CADIE PROFILE PIC DOWNLOAD", "Exception - " + e.toString());
		}
        finally{
        	stopSelf();
        }
    }
    
    private void  startFriendsProfilePicDownload(String friendEmailID)
    {
    	URL url = null;
    	HttpURLConnection con = null;

        try {
        	String[] strProfilePicName = friendEmailID.split("\\@");
        	
        	String urlpath = "http://54.88.103.38/gcm_server_php/CadieUserProfilePic/"+ strProfilePicName[0] + ".jpg";
        	
            url = new URL(urlpath.toString());
            con = (HttpURLConnection)url.openConnection();
            
            con.connect();
            
            int status = con.getResponseCode();

            Log.d("CADIE PROFILE PIC DOWNLOAD" , "11 File Status  " + status);
            
            if(status == 404)
            {
            	 Log.d("CADIE PROFILE PIC DOWNLOAD" , "File Not found - " + strProfilePicName[0] + ".jpg");
            }
            else
            {
            	
            	//String PATH = Environment.getExternalStorageDirectory() + "/Cadie";
                File file = new File("/data/data/air.com.zahdoo.cadie.debug/com.zahdoo.cadie.debug/Local Store/thumbnails");  
                //File file = new File("/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/thumbnails");  
                
                if (!file.exists()) 
                    file.mkdirs();
                
                if (!file.exists()) 
                	file.getParentFile().mkdirs();
               
                File outputFile = new File(file,strProfilePicName[0] + ".jpg");  
                FileOutputStream fos = new FileOutputStream(outputFile);

            	 BufferedInputStream bis = new BufferedInputStream(con.getInputStream());
                 
                 Log.d("CADIE PROFILE PIC DOWNLOAD" , "22 InputStream - " + bis.toString() );
                 
                 byte[] buffer = new byte[1024];
                 int len1 = 0;
                 while ((len1 = bis.read(buffer)) != -1) {
                     fos.write(buffer, 0, len1); // Write In FileOutputStream.
                 }
                 
                 Log.d("CADIE PROFILE PIC DOWNLOAD" , "File downloaded to device - " + strProfilePicName[0] + ".jpg");
                 
                 fos.flush();
                 fos.close();
                 bis.close();
            }
           
        } 
	    catch (Exception exc) {
	
	    	Log.d("CADIE PROFILE PIC DOWNLOAD" , " File Download Exception  - " + exc.toString() );
	    }
        finally{
        	if (con != null)
            	con.disconnect();
        }
    }
    
	/////
    
    private SQLiteDatabase cDataBase;
	
	//private String cDB_PATH = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadieMainDB.sqlite";
	private String cDB_PATH = "/data/data/air.com.zahdoo.cadie.debug/com.zahdoo.cadie.debug/Local Store/cadieMainDB.sqlite";

	private void UpdateConfig()
	{
  		try
  		{
  			cDataBase = SQLiteDatabase.openDatabase(cDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);

  			Log.d("CADIE PROFILE PIC DOWNLOAD", "Inside update ContactRequests  " ); 
  		
  			ContentValues dataToUpdate = new ContentValues();                          
			dataToUpdate.put("Value", "true");
			String where = "Key = ?";  
			final String[] whereArgs = { "isProfilePicsDownloaded" };
			cDataBase.update("Config", dataToUpdate, where, whereArgs);
			
			dataToUpdate.clear();
		}
	  	catch (Exception e)
	  	{
	  		Log.d("CADIE PROFILE PIC DOWNLOAD","ContactRequests Insert Error"); 
		}
	  	finally
	  	{
	  		Log.d("CADIE PROFILE PIC DOWNLOAD","ContactRequests updated.."); 
	  		
	  		if(cDataBase.isOpen())
	  			cDataBase.close();
	  	}
	}
}