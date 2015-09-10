package com.zahdoo.android.extension.GCM;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.util.Log;

import com.zahdoo.android.extension.alarm.Global;

public class RetrieveNoteService extends IntentService {
	
	private NotificationManager mNotificationManager;
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
	}
    
    public RetrieveNoteService()
    {
    	super("RetrieveNoteService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
       // Bundle extras = getIntent().getExtras(); 
        int close_notify_id = intent.getIntExtra( "notification_id", 0 );
        String notificationType =  intent.getStringExtra("NotificationType");
        Log.d(CommonUtilities.TAG, "Notification Type - " + notificationType);
        
        try {
        	
			mNotificationManager = (NotificationManager) getSystemService( RetrieveNoteService.NOTIFICATION_SERVICE );
			Log.d(CommonUtilities.TAG, "Notification shareId - " + close_notify_id);
			mNotificationManager.cancel(close_notify_id );
		} catch (Exception e) {
		
		}
        finally{
        	
	        if( !Global.bRun )
	        {
	        	Global.bRun = true;
	        	
		        Intent i = new Intent( Intent.ACTION_MAIN );
		        PackageManager manager = getPackageManager();
		        i = manager.getLaunchIntentForPackage( "air.com.zahdoo.cadie.debug" );
		        //i = manager.getLaunchIntentForPackage( "air.com.zahdoo.cadie" );
		        i.addCategory( Intent.CATEGORY_LAUNCHER );
		        startActivity( i );
		        System.exit( 0 );
	        }
	        else{
	        	if(notificationType.contentEquals("CONTACT_REQUEST"))
	        		GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", "CONTACT_REQUEST");
	        	else if(notificationType.contentEquals("CONTACT_REQUEST_ACCEPTED"))
	        	{
	        		String acceptedByName =  intent.getStringExtra("AcceptedByName");
	        		String acceptedByEmailId =  intent.getStringExtra("AcceptedByEmailId");
	        		
        			GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", "CONTACT_REQUEST_ACCEPTED^" + acceptedByEmailId + "^"  + acceptedByName);
	        	}
	        	else if(notificationType.contentEquals("CONTACT_REQUEST_DECLINED"))
        			;//GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", "CONTACT_REQUEST_DECLINED");
	        	else if(notificationType.contentEquals("MESSAGE") || notificationType.contentEquals("PICTURE") ||
	        			notificationType.contentEquals("VIDEO") || notificationType.contentEquals("VOICE") || 
	        			notificationType.contentEquals("FILE"))
	        	{
	        		String byEmail =  intent.getStringExtra("ByEmail");
	        		String byName =  intent.getStringExtra("ByName");
        			GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", "MESSAGE^" + byEmail + "^" + byName);
	        	}
	        	else if(notificationType.contentEquals("CONTACT_UNFRIEND"))
	        	{
	        		String byEmail =  intent.getStringExtra("UnfriendFromEmail");
	        		String byName =  intent.getStringExtra("UnfriendFromName");
        			GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", "CONTACT_UNFRIEND^" + byEmail + "^" + byName);
	        	}
	        	else if(notificationType.contentEquals("LOCATION_REACHED"))
	        	{
	        		String locID =  intent.getStringExtra("LocationID");
        			GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", "LOCATION_REACHED^" + locID );
	        	}
		        else if(notificationType.contentEquals("GROUP_CREATED") || 
		        		notificationType.contentEquals("GROUP_MESSAGE") ||
		        		notificationType.contentEquals("GROUP_FILE_MESSAGE") ||
		        		notificationType.contentEquals("EXIT_GROUP") )
	        	{
		        	String ServerGroupID = intent.getStringExtra("ServerGroupID");
		        	GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", "GROUP_MESSAGE_TAP^" + ServerGroupID);
	        	}
		        else{
	        		
	        		String shrdID 			=  intent.getStringExtra("ShareID");
	        	    String noteID 			=  intent.getStringExtra("NoteID");
	        	    Log.d(CommonUtilities.TAG, "Notification Intent Data ID - " + shrdID);
	        		GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", shrdID + "^" + noteID);
	        	}
	        	
	        	Intent i = new Intent( Intent.ACTION_MAIN );
		        PackageManager manager = getPackageManager();
		        i = manager.getLaunchIntentForPackage( "air.com.zahdoo.cadie.debug" );
		        //i = manager.getLaunchIntentForPackage( "air.com.zahdoo.cadie" );
		        i.addCategory( Intent.CATEGORY_LAUNCHER );
		        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		        startActivity( i );
	        }
	        
	        stopSelf();
        }
    }
}
