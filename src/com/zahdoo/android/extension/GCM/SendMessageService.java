package com.zahdoo.android.extension.GCM;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
 
public class SendMessageService extends IntentService{
	private ConnectionDetector cd;
    
    private String shareID;
    private String byEmail;
    private String byName;
    private String toEmail;
    private String noteID;
    private String noteTitle;
    private String dbName;
    private String notificationType;
    private String senderRegID;
    
    //private Context context;
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
	}
    
    public SendMessageService()
    {
    	super("SendMessageService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
    	cd = new ConnectionDetector(getApplicationContext());
    	 
        // Check if Internet present
    	try {
    		 if (!cd.isConnectingToInternet()) {
    	            // Internet Connection is not present
//    	        	Log.d(CommonUtilities.TAG, "Internet Connection Error... " );
    	        	
    	        	GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION");
    	        	stopSelf();
    	            return;
    	        }
    	        
		} catch (Exception e) {
			Log.d(CommonUtilities.TAG, "2 Internet Connection Error... -  "  + e.toString());
		}
       
        try {
			Bundle extras = intent.getExtras();
			 
			shareID 		= extras.getString("shareID");
			byEmail 		= extras.getString("byEmail");
			byName 			= extras.getString("byName");
			toEmail 			= extras.getString("toEmail"); /// Group ID
			noteID 				= extras.getString("noteID");
			noteTitle 			= extras.getString("noteTitle");
			dbName 				= extras.getString("dbName");
			senderRegID  		= extras.getString("senderRegID");//Sender's GCM Registration ID
			notificationType 	= extras.getString("notificationType");//NOTE OR MESSAGE  // Group File
			
			//context = getApplicationContext();
			
			if( notificationType.contentEquals("CONTACT_REQUEST"))
            {
				sendContactRequest();
            }
			else if( notificationType.contentEquals("ACCEPT_REQUEST") || notificationType.contentEquals("DECLINE_REQUEST"))
            {
				acceptDeclineContactRequest();
            }
			else if( notificationType.contentEquals("SEND_INVITATION"))
            {
				sendInvitationToNonCadieUser();
            }
			else if( notificationType.contentEquals("BUDDY_SUGGESTION"))
            {
				getBuddySuggestion(shareID);//Param is userID
            }
			else if( notificationType.contentEquals("PROFILE_INFO_CHANGED_PIC_FALSE_NAME_FALSE"))
            {
				updateProfileInfoOfUser(false,false);
            }
			else if( notificationType.contentEquals("PROFILE_INFO_CHANGED_PIC_FALSE_NAME_TRUE"))
            {
				updateProfileInfoOfUser(false,true);
            }
			else if( notificationType.contentEquals("PROFILE_INFO_CHANGED_PIC_TRUE_NAME_FALSE"))
            {
				updateProfileInfoOfUser(true,false);
            }
			else if( notificationType.contentEquals("PROFILE_INFO_CHANGED_PIC_TRUE_NAME_TRUE"))
            {
				updateProfileInfoOfUser(true,true);
            }
			else if( notificationType.contentEquals("GET_USER_PROFILE_DATA"))
            {
				getUserProfileDataOnLogin();
            }
			else if( notificationType.contentEquals("PICTURE") || notificationType.contentEquals("VIDEO")
					|| notificationType.contentEquals("VOICE") || notificationType.contentEquals("FILE")
					||  notificationType.contentEquals("EXPORTED_PDF") )
            {
				String msg = "";
				//@PARAMS  ServerFileId, sendermailID,SenderName, ReceiverMailId, senderuserID, Title(At present blank),fileName,filetype,fileSize,senderRegID,toName
		    	msg = ServerUtilities.sendMessage(shareID,byEmail,byName,toEmail,noteID,noteTitle,dbName,notificationType,extras.getString("fileSize") , senderRegID,extras.getString("toName"));
		    	GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "MESSAGE_SENT^" + msg);
	        	stopSelf();
            }
			else if( notificationType.contentEquals("NOTE") || notificationType.contentEquals("MESSAGE"))
			{
				if(shareID.trim().length() > 0 && toEmail.trim().length() > 0)
				{
					String msg = "";
			    	msg = ServerUtilities.sendMessage(shareID,byEmail,byName,toEmail,noteID,noteTitle,dbName,notificationType,"" , senderRegID, "");
			    	GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "MESSAGE_SENT^" + msg);
		        	stopSelf();
				}
				else
				{
					Log.d(CommonUtilities.TAG , "Message Empty");
					//Toast.makeText(GCMInitFunction.cGCM.getActivity(), "Registration Error!", Toast.LENGTH_LONG).show();
					
					GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "ERROR");
					stopSelf();
				}
			}
			else if( notificationType.contentEquals("APP_UPDATE_REGISTRATION_CHECK") )
			{
				checkAppVersion(byEmail,senderRegID);
			}
			
		} catch (Exception e) {
			Log.d(CommonUtilities.TAG , "Message Exception  - " + e.toString());
			//GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "ERROR " + e.toString());
			stopSelf();
		}
    }
    
    /////////////////////////
    private void sendContactRequest()
    {
		String msg = "";
		try
		{
			Log.d(CommonUtilities.TAG, "1 CONTACT REQUEST " );
		    msg = ServerUtilities.sendContactRequest(noteID, byEmail, byName , toEmail ,noteTitle ,dbName ,senderRegID);
		    Log.d(CommonUtilities.TAG, "2 CONTACT REQUEST " );
		}
		catch (Exception ex)
		{
			Log.d(CommonUtilities.TAG, "3 CONTACT REQUEST EXCEPTION: " + ex.toString());
		    msg = ex.toString();
		}
		
		String[] vStrings = msg.split("\\^");
    	
    	if(msg.contentEquals(""))
			GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
		else if(vStrings[0].contentEquals("CONTACT_REQUEST_SENT")){
    		GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "CONTACT_REQUEST_SENT^" + vStrings[1] + "^" + vStrings[2] );
    	}
    	else
    	{
    		GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "CONTACT_REQUEST_NOT_SENT" );
    	}
    		
    	stopSelf();
    }
    
    //////////////////////////
    
    private void acceptDeclineContactRequest()
    {
    	String msg = "";
		try
		{
			Log.d(CommonUtilities.TAG, "1 ACCEPT DECLINE CONTACT REQUEST " );
		    msg = ServerUtilities.acceptDeclineContactRequest(noteID, byEmail, byName , toEmail ,noteTitle , dbName , notificationType ,senderRegID);
		    Log.d(CommonUtilities.TAG, "2 ACCEPT DECLINE CONTACT REQUEST " );
		}
		catch (Exception ex)
		{
			Log.d(CommonUtilities.TAG, "3 ACCEPT DECLINE CONTACT REQUEST EXCEPTION: " + ex.toString());
		    msg = ex.toString();
		}
		
    	Log.i(CommonUtilities.TAG, "SERVER RESPONSE MSG - " + msg);
    	
    	String[] vStrings = msg.split("\\^");
    	
    	if(msg.contentEquals(""))
			GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
		else if(vStrings[0].contentEquals("REQUEST_ACCEPT_RESPONSE_SENT"))
	     {
    		/* echo "REQUEST_ACCEPT_RESPONSE_SENT^".$reqSenderUserID."^".$reqMsgID."^".$reqSenderName."^".
			$reqSenderPhoneNo."^".$reqSenderProfileMessage."^".$reqSenderDOB."^".$reqSenderLocation."^".$reqSenderProfilePicName;
*/
    		startFriendsProfilePicDownload(toEmail);
    		GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", msg);
    		stopSelf();
    	}
    	else if(msg.contentEquals("REQUEST_ACCEPT_RESPONSE_ERROR"))//
    	{
    		GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "REQUEST_ACCEPT_RESPONSE_ERROR" );
    		stopSelf();
    	}
    	else if(msg.contentEquals("REQUEST_DECLINED_RESPONSE_SENT"))//
    	{
    		GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "REQUEST_DECLINED_RESPONSE_SENT" );
    		stopSelf();
    	}
    	else //
    	{
    		GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "REQUEST_DECLINED_RESPONSE_ERROR" );
    		stopSelf();
    	}
    }
    
    private void  startFriendsProfilePicDownload(String friendEmailID )
    {
    	URL url = null;
    	HttpURLConnection con = null;

        try {
        	String[] strProfilePicName = friendEmailID.split("\\@");
        	String urlpath = "http://54.88.103.38/gcm_server_php/CadieUserProfilePic/"+ strProfilePicName[0] + ".jpg";
        	
            url = new URL(urlpath.toString());
            con = (HttpURLConnection)url.openConnection();
            
            String PATH = "/data/data/air.com.zahdoo.cadie.debug/com.zahdoo.cadie.debug/Local Store/thumbnails";
            //String PATH = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/thumbnails";
            File file = new File(PATH); 
            
            if (!file.exists()) 
                file.mkdirs();
            
            if (!file.exists()) 
            	file.getParentFile().mkdirs();
           
            File outputFile = new File(file,strProfilePicName[0] + ".jpg");  
            FileOutputStream fos = new FileOutputStream(outputFile);

            con.connect();
            int status = con.getResponseCode();
            Log.d(CommonUtilities.TAG , "11 File Status  " + status);
            BufferedInputStream bis = new BufferedInputStream(con.getInputStream());
            Log.d(CommonUtilities.TAG , "22 InputStream - " + bis.toString() );
            
            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = bis.read(buffer)) != -1) {
                fos.write(buffer, 0, len1); // Write In FileOutputStream.
            }
            
            //Log.d(CommonUtilities.TAG , "File downloaded to device - " + profilePicFileName);
            fos.flush();
            fos.close();
            bis.close();
        } 
	    catch (Exception exc) {
	
	    	Log.d(CommonUtilities.TAG , " File Download Exception  - " + exc.toString() );
	    }
        finally{
        	if (con != null)
            	con.disconnect();
        }
    }
    
    //////////
    
    /**
     * Send a mail invitation to non Cadie users
     */
    private void sendInvitationToNonCadieUser()
    {
    	String msg = "";
		try
		{
			Log.d(CommonUtilities.TAG, "1 SEND INVITATION  " );
		    msg = ServerUtilities.sendMailInvitation(noteID ,byEmail, byName , toEmail , dbName , noteTitle ,senderRegID);
		    Log.d(CommonUtilities.TAG, "2 SEND INVITATION  " );
		}
		catch (Exception ex)
		{
			Log.d(CommonUtilities.TAG, "3 SEND INVITATION  EXCEPTION: " + ex.toString());
		    msg = ex.toString();
		}
		
		Log.i(CommonUtilities.TAG, "SERVER RESPONSE MSG - " + msg);
    	String[] vStrings = msg.split("\\^");
    	
    	if(msg.contentEquals(""))
			GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
		else if(vStrings[0].contentEquals("INVITATION_SENT_SUCCESSFULLY"))
    	{
    		GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "INVITATION_SENT_SUCCESSFULLY^" + vStrings[1] + "^" + vStrings[2] );
    	}
    	else 
    	{
    		GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "INVITATION_ERROR" );
    	}
    		
    	stopSelf();
    }
    
    
    
 //////////
    
    /**
     * Update the changed profile info in the server and notify his/her friends 
     */
    private void updateProfileInfoOfUser(Boolean isProfilePicChanged, Boolean isProfileNameChanged)
    {
    	String msg = "";
		try
		{
			Log.d(CommonUtilities.TAG, "1 UPDATE PROFILE INFO  " );
			
			//Params are name, emailid, status_msg, date_of_birth, phone_no, location, profile_pic_name, isProfilePicChanged ,isProfileNameChanged
		    msg = ServerUtilities.updateProfileInfo(shareID ,byEmail, byName , toEmail , noteID , noteTitle ,dbName, isProfilePicChanged,isProfileNameChanged , senderRegID);
		    Log.d(CommonUtilities.TAG, "2 UPDATE PROFILE INFO " );
		}
		catch (Exception ex)
		{
			Log.d(CommonUtilities.TAG, "3 UPDATE PROFILE INFO EXCEPTION: " + ex.toString());
		    msg = ex.toString();
		}
		
		Log.i(CommonUtilities.TAG, "SERVER RESPONSE MSG - " + msg);
    	
    	if(msg.contentEquals(""))
			GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
		else if(msg.contentEquals("PROFILE_UPDATE_SUCCESSFUL"))
       	{
    		GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "PROFILE_UPDATE_SUCCESSFUL" );
    	}
    	else //PROFILE_UPDATE_ERROR
    	{
    		GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "PROFILE_UPDATE_ERROR" );
    	}
    		
    	stopSelf();
    }
    
    
//////////
    
	private void getUserProfileDataOnLogin()
	{
		String msg = "";
		try
		{
			Log.d(CommonUtilities.TAG, "1 UPDATE PROFILE INFO  " );
			
			//Params are emailid, 
		    msg = ServerUtilities.getUserProfileDataFromServer(byEmail);
		    Log.d(CommonUtilities.TAG, "2 UPDATE PROFILE INFO " );
		}
		catch (Exception ex)
		{
			Log.d(CommonUtilities.TAG, "3 UPDATE PROFILE INFO EXCEPTION: " + ex.toString());
		    msg = ex.toString();
		}
		
		Log.i(CommonUtilities.TAG, "SERVER RESPONSE MSG - " + msg);
		
		String[] vStrings = msg.split("\\^@");
		
		if(msg.contentEquals(""))
			GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
		else if(vStrings[0].contentEquals("GET_PROFILE_INFO_DATA"))
   		{
			GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", msg );
		}
		else 
		{
			GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "ERROR" );
		}
			
		stopSelf();
	}
	
	///////////
    
    /**
     * Query the server to get mutual friend suggestions 
     */
    private void getBuddySuggestion(String userID)
    {
    	String msg = "";
		try
		{
			Log.d(CommonUtilities.TAG, "1 BUDDY SUGGESTION  " );
			
		    msg = ServerUtilities.getBuddySuggestion( userID);
		    Log.d(CommonUtilities.TAG, "2 BUDDY SUGGESTION " );
		}
		catch (Exception ex)
		{
			Log.d(CommonUtilities.TAG, "3 BUDDY SUGGESTION EXCEPTION: " + ex.toString());
		    msg = ex.toString();
		    GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "ERROR" );
		}
		finally
		{
			Log.i(CommonUtilities.TAG, "SERVER RESPONSE MSG - " + msg);
	    	
	    	if(msg.contentEquals("NO_BUDDY_SUGGESTION_FOUND"))
	    	{
	    		GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "NO_BUDDY_SUGGESTION_FOUND" );
	    	}
	    	else if(msg.contentEquals("ERROR^CADIE")) 
	    	{
	    		GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "ERROR" );
	    	}
	    	else
	    	{
	    		GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", msg );
	    	}
			stopSelf();
		}
    }
    
    /////////////////////////////////////////////////
    
    private void checkAppVersion(String emailId, String oldRegId)
    {
    	try {
    		String newAppVersion = getAppVersion(getApplicationContext());
			SQLiteDatabase cDataBase;
  	 
			//String cDB_PATH = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadieMainDB.sqlite";
			String cDB_PATH = "/data/data/air.com.zahdoo.cadie.debug/com.zahdoo.cadie.debug/Local Store/cadieMainDB.sqlite";

			cDataBase = SQLiteDatabase.openDatabase(cDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
			Cursor c;
			
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
					
					if(!appVersion.contentEquals(newAppVersion))
					{
						if(!emailId.contentEquals(""))//Only when user is logged in to Cadie account
							goForReRegistration(emailId, oldRegId , cDataBase ,newAppVersion);
						else
						{
							ContentValues values = new ContentValues();                          
			        		values.put("Value", newAppVersion);
			    			String where1 = "Key = ?";  
			    			final String[] whereArgs1 = { "AppVersion" };
			    			cDataBase.update("Config", values, where1, whereArgs1);
			    			
			    			Log.i(CommonUtilities.TAG, "Config table AppVersion updated " );
			    			values.clear();
			    			
							try {
				    			GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "OK" );
							} catch (Exception e2) {
								// TODO: handle exception
							}
						}
					}
					else
					{
						ContentValues values = new ContentValues();                          
		        		values.put("Value", newAppVersion);
		    			String where1 = "Key = ?";  
		    			final String[] whereArgs1 = { "AppVersion" };
		    			cDataBase.update("Config", values, where1, whereArgs1);
		    			
		    			Log.i(CommonUtilities.TAG, "Config table AppVersion updated " );
		    			values.clear();
						
						try {
			    			GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "OK" );
						} catch (Exception e2) {
							// TODO: handle exception
						}
					}
				}
				else
				{
					Log.d(CommonUtilities.TAG, "AppVersion is not there in Config table. So insert " );
					
					ContentValues values = new ContentValues();
					values.put("UserID",  1 );
					values.put("Key", "AppVersion");
					values.put("Value", "0");
					
					cDataBase.insert("Config", null, values);
					values.clear();
					
					Log.d(CommonUtilities.TAG, "AppVersion inserted into Config table " );
					
					try {
		    			GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "OK" );
					} catch (Exception e2) {
						// TODO: handle exception
					}
				}
			}
			
		} catch (Exception e) {
			Log.d(CommonUtilities.TAG, "checkAppVersion Exception -  " + e.toString() );
			e.printStackTrace();
			
			try {
				GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "ERROR" );
			} catch (Exception e2) {
				// TODO: handle exception
			}
		}
    	finally
    	{
    		stopSelf();
    	}
    }
    
    ///////////////////////////////////////////////////////////////
    
    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private String getAppVersion(Context context)
    {
		try
		{
		    PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		    Log.d(CommonUtilities.TAG, "Cadie App Package Name : " + context.getPackageName());
			   
		    Log.d(CommonUtilities.TAG, "Cadie App Version is : " + packageInfo.versionCode);
		    return packageInfo.versionCode + "";
		}
		catch (NameNotFoundException e)
		{
		    // should never happen
		    //throw new RuntimeException("Could not get package name: " + e);
			Log.d(CommonUtilities.TAG, "Could not get package name: " + e.toString());
			return 0 + "";
		}
    }
    
    private void goForReRegistration (String emailId, String oldRegId , SQLiteDatabase cDataBase , String newAppVersion)
    {
    	GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
    	
    	String newRegId = "";
    	
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
        		values.put("Value", newAppVersion);
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
    			
    			
    			try {
    				GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", newRegId );
    			} catch (Exception e2) {
    				// TODO: handle exception
    			}
    		}
    		else
    		{
    			try {
    				GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "ERROR" );
    			} catch (Exception e2) {
    				// TODO: handle exception
    			}
    		}
    	}
    	else
    	{
    		ContentValues values = new ContentValues();                          
    		values.put("Value", newAppVersion);
			String where1 = "Key = ?";  
			final String[] whereArgs1 = { "AppVersion" };
			cDataBase.update("Config", values, where1, whereArgs1);
			
			Log.i(CommonUtilities.TAG, "Config table AppVersion updated " );
			values.clear();
			
    		Log.i(CommonUtilities.TAG, "New and old RegId are same or one of them is blank"  );
    		Log.i(CommonUtilities.TAG, "New RegId - "  + newRegId);
    		Log.i(CommonUtilities.TAG, "Old RegId - "  + oldRegId );
    		
    		
    		try {
    			GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "OK" );
			} catch (Exception e2) {
				// TODO: handle exception
			}
    	}
    }
    
    
    /////////////////////////////
    
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