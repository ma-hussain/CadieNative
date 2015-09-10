package com.zahdoo.android.extension.GCM;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.zahdoo.android.extension.alarm.Global;
 
public class RegisterService extends IntentService
{
	private ConnectionDetector cd;
    private String name;
    private String email;
    private String password;
    private String action;
    private String regIDFromUser;//returned from device not gcm server i.e.  
    private String serverUserID;
    private String verificationCode;
    private String macAddress;
    //private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    //private static final String PROPERTY_APP_VERSION = "appVersion";
    
    private GoogleCloudMessaging gcm;
    private String regid;
    private String newRegid;
    private String msgShareID;
    private String msgTime;
    
    //private Context context;
    //private AtomicInteger msgId = new AtomicInteger();
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
	}
    
    public RegisterService()
    {
    	super("RegisterService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        try {
			gcm = GoogleCloudMessaging.getInstance(this);
			//context = getApplicationContext();
	        cd = new ConnectionDetector(getApplicationContext());
	 
	        // Check if Internet present
	        if (!cd.isConnectingToInternet()) {
	            // Internet Connection is not present
//	           Toast.makeText(RegisterActivity.this,
//	                    "Internet Connection Error...\n" +
//	                    "Please connect to working Internet connection", Toast.LENGTH_LONG).show();
	           
	           	RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION");
		    	stopSelf();
	            return;
	        }
        
			// Getting name, email from intent
			//Intent i = getIntent();
			name = intent.getStringExtra("name");
			email = intent.getStringExtra("email");  
			password = intent.getStringExtra("password"); 
			action = intent.getStringExtra("action");
			
			try 
			{
				if( action.contentEquals("APP_LAUNCH_INFO"))
	            {
					addLaunchInfo();
	            }
				else if( action.contentEquals("FIRST_VERIFICATION"))
	            {
					firstVerification();
	            }
				else if( action.contentEquals("CHECK_CADIE_USER"))
	            {
					checkCadieUser();
	            }
				else if( action.contentEquals("USER_SIGN_IN"))
	            {
	        		// Check device for Play Services APK. If check succeeds, proceed with
					// GCM registration.
					if (checkPlayServices())
					{
						macAddress = intent.getStringExtra("regID"); 
						checkSignInCredentials();
					}
					else
					{
					    Log.i(CommonUtilities.TAG, "No valid Google Play Services APK found.");
					    RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_GOOGLE_PLAY_SERVICES");
				    	stopSelf();
				    	return;
					}
	            }
				else if( action.contentEquals("VERIFY_CODE"))
	            {
					regIDFromUser = intent.getStringExtra("regID"); 
					serverUserID = intent.getStringExtra("serverUserID"); 
					macAddress = intent.getStringExtra("macAddress");
	        		verifyCode();
	            }
				else if( action.contentEquals("VERIFY_HAVING_CODE"))
	            {
					verificationCode = intent.getStringExtra("verificationCode"); 
					macAddress = intent.getStringExtra("macAddress");
	        		verifyHavingCode();
	            }
				else if( action.contentEquals("SEND_RESET_CODE"))
	            {
	        		sendResetCode();
	            }
				else if( action.contentEquals("VERIFY_RESET_CODE"))
	            {
	        		verifyResetCode();
	            }
				else if( action.contentEquals("SAVE_NEW_PASSWORD"))
	            {
					// Check device for Play Services APK. If check succeeds, proceed with
					// GCM registration.
					if (checkPlayServices())
					{
						macAddress = intent.getStringExtra("regID"); 
						saveNewPassword();
					}
					else
					{
					    Log.i(CommonUtilities.TAG, "No valid Google Play Services APK found.");
					    RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_GOOGLE_PLAY_SERVICES");
				    	stopSelf();
				    	return;
					}
	            }
				else if( action.contentEquals("REGISTER_NEW") || action.contentEquals("REGISTER_OLD"))
	            {
					// Check device for Play Services APK. If check succeeds, proceed with
					// GCM registration.
					if (checkPlayServices())
					{
					    if (TextUtils.isEmpty(regid))
					    {
					    	registerInBackground();
					    }
					}
					else
					{
					    Log.i(CommonUtilities.TAG, "No valid Google Play Services APK found.");
					    
					    RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_GOOGLE_PLAY_SERVICES");
				    	stopSelf();
				    	return;
					}
	            }
				else if( action.contentEquals("DROPBOX_LINK_DETAILS"))
	            {
	        		saveDropboxDetails();
	            }
				else if( action.contentEquals("DOWNLOAD_USER_DATA"))
	            {
					//name -> UserID
					//email -> EmailID
					//password -> EmailID
					

					String msg = "";
					try
					{
						Log.d(CommonUtilities.TAG, "2 SUPPORT_CONTACT " );
					    msg = ServerUtilities.setUpContactWithSupport(name, email);//userId,emailid
					    Log.d(CommonUtilities.TAG, "3 SUPPORT_CONTACT MSG>> " + msg );
					    String[] vStrings = msg.split("\\^");
				    	
				    	if(vStrings[0].contentEquals("SUPPORT_CONTACT_DONE"))
				    	{
				    		msgShareID = vStrings[1];
				    		msgTime = vStrings[2];
				    		
				    		Log.d(CommonUtilities.TAG, "4 SUPPORT_CONTACT_DONE " );
				    	}
				    	else if(vStrings[0].contentEquals("SUPPORT_CONTACT_ALREADY_DONE"))
				    	{
				    		msgShareID = "";
				    		Log.d(CommonUtilities.TAG, "5 SUPPORT_CONTACT_ALREADY_DONE " );
				    	}
				    	else//SUPPORT_CONTACT_ERROR
				    	{
				    		msgShareID = "";
				    		Log.d(CommonUtilities.TAG, "0 SUPPORT_CONTACT_ERROR " );
				    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR" );
				    	}
					}
					catch (Exception ex)
					{
						Log.d(CommonUtilities.TAG, "4 USER REQUEST DATA EXCEPTION: " + ex.toString());
					    msg = ex.toString();
					}
					
					getUsersContactsFromServer();
	            }
				else if( action.contentEquals("DOWNLOAD_REQUEST_DATA"))
	            {
					
					
					
					String msg = "";
					try
					{
						msg = ServerUtilities.setUpContactWithSupport(name, email);//userId,emailid
					    
						String[] vStrings = msg.split("\\^");
				    	
				    	if(vStrings[0].contentEquals("SUPPORT_CONTACT_DONE"))
				    	{
				    		msgShareID = vStrings[1];
				    		msgTime = vStrings[2];
				    		
				    		Log.d(CommonUtilities.TAG, "4 SUPPORT_CONTACT_DONE " );
				    	}
				    	else if(vStrings[0].contentEquals("SUPPORT_CONTACT_ALREADY_DONE"))
				    	{
				    		msgShareID = "";
				    		Log.d(CommonUtilities.TAG, "5 SUPPORT_CONTACT_ALREADY_DONE " );
				    	}
				    	else//SUPPORT_CONTACT_ERROR
				    	{
				    		msgShareID = "";
				    		Log.d(CommonUtilities.TAG, "0 SUPPORT_CONTACT_ERROR " );
				    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR" );
				    	}
					}
					catch (Exception ex)
					{
						Log.d(CommonUtilities.TAG, "4 USER REQUEST DATA EXCEPTION: " + ex.toString());
					    msg = ex.toString();
					}
					
					
					/////
					
					getUsersRequestsFromServer();
	            }
				else if( action.contentEquals("GET_SERVER_FILE_NAME"))
	            {
					//name -> ServerFileID
					//email -> ServerFileID
					//password -> ServerFileID
					getServerFileNameFromID();
	            }
				else if( action.contentEquals("UNFRIEND_CONTACT"))
	            {
					unfriendUser(intent.getStringExtra("regID"), intent.getStringExtra("serverUserID"));//Param is oApp.gcmRegisteredUserID and senderDeviceRegID
	            }
				else if( action.contentEquals("SEARCH_USER_BY_NAME"))
	            {
					searchUserByNameEmail(name,false);//Param is search text
	            }
	            else if( action.contentEquals("SEARCH_USER_BY_EMAIL"))
	            {
	            	searchUserByNameEmail(name,true);//Param is search text
	            }
				else if( action.contentEquals("UNREGISTER"))
	            {
					unregister();
	            }
			} catch (Exception e) {
				// TODO: handle exception
			}
			 
			
		} catch (Exception e) {
			//Toast.makeText(getApplicationContext(), "On error " + e.toString() , Toast.LENGTH_LONG).show();
			RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR " + e.getMessage() );
			stopSelf();
		}
    }      
 
    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground()
    {
    	try {
    		new AsyncTask<Void, Void, String>()
    		{
    		    @Override
    		    protected String doInBackground(Void... params)
    		    {
    				String msg = "";
    				try
    				{
    					//Log.d(CommonUtilities.TAG, "1" );
    				    if (gcm == null)
    				    {
    				    	gcm = GoogleCloudMessaging.getInstance(RegisterService.this);
    				    }
    				    
    				    regid = gcm.register(CommonUtilities.SENDER_ID);
    				    
    				    Log.d(CommonUtilities.TAG, "2" );
    				    msg = "Device registered, registration ID=" + regid;
    		
    				    // You should send the registration ID to your server over
    				    // HTTP, so it can use GCM/HTTP or CCS to send messages to your app.
    				    sendRegistrationIdToBackend();
    				}
    				catch (Exception ex)
    				{
    					Log.d(CommonUtilities.TAG, "3" +ex.toString() );
    					RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR " + ex.toString() );
    					stopSelf();
    				    msg = "Error :" + ex.toString();
    				}
    				return msg;
    		    }
    	
    		    @Override
    		    protected void onPostExecute(String msg)
    		    {
    		    	Log.i(CommonUtilities.TAG, "AFTER REGISTER GCM");
    		    }
    		}.execute(null, null, null);
		} catch (Exception e) {
			Log.d(CommonUtilities.TAG, " 5 " + e.toString());
		}
		
    }
    
    /**
     * Gets the current registration ID for application on GCM service, if there
     * is one.
     * <p>
     * If result is empty, the app needs to register.
     * 
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
//    private String getRegistrationId(Context context)
//    {
//		final SharedPreferences prefs = getGcmPreferences(context);
//		String registrationId = prefs.getString(CommonUtilities.PREFS_PROPERTY_REG_ID, "");
//		
//		if (registrationId == null || registrationId.equals(""))
//		{
//		    Log.i(CommonUtilities.TAG, "Registration not found.");
//		    return "";
//		}
//		// Check if app was updated; if so, it must clear the registration ID
//		// since the existing regID is not guaranteed to work with the new
//		// app version.
//		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
//		int currentVersion = getAppVersion(context);
//		
//		if (registeredVersion != currentVersion)
//		{
//		    Log.i(CommonUtilities.TAG, "App version changed.");
//		    return "";
//		}
//		return registrationId;
//    }
    
    /**
     * Stores the registration ID and the app versionCode in the application's
     * {@code SharedPreferences}.
     * 
     * @param context
     *            application's context.
     * @param regId
     *            registration ID
     */
//    private void storeRegistrationId(Context context, String regId)
//    {
//		final SharedPreferences prefs = getGcmPreferences(context);
//		int appVersion = getAppVersion(context);
//		Log.i(CommonUtilities.TAG, "Saving regId on app version " + appVersion);
//		SharedPreferences.Editor editor = prefs.edit();
//		editor.putString(CommonUtilities.PREFS_PROPERTY_REG_ID, regId);
//		editor.putInt(PROPERTY_APP_VERSION, appVersion);
//		editor.commit();
//    }
    
    
    /**
     * @return Application's version code from the {@code PackageManager}.
     */
//    private String getAppVersion(Context context)
//    {
//		try
//		{
//		    PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
//		    Log.d(CommonUtilities.TAG, "Cadie App Package Name : " + context.getPackageName());
//			   
//		    Log.d(CommonUtilities.TAG, "Cadie App Version is : " + packageInfo.versionCode);
//		    return packageInfo.versionCode + "";
//		}
//		catch (NameNotFoundException e)
//		{
//		    // should never happen
//		    //throw new RuntimeException("Could not get package name: " + e);
//			Log.d(CommonUtilities.TAG, "Could not get package name: " + e.toString());
//			return 0 + "";
//		}
//    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
//    private SharedPreferences getGcmPreferences(Context context)
//    {
//		// This sample app persists the registration ID in shared preferences,
//		// but how you store the regID in your app is up to you.
//		return getSharedPreferences(CommonUtilities.PREFS_NAME, Context.MODE_PRIVATE);
//    }
    
    
    /**
     * Sends the registration ID to the 3rd party server via an upstream 
     * GCM message. Ideally this would be done via HTTP to guarantee success or failure 
     * immediately, but it would require an HTTP endpoint.
     */
    private void sendRegistrationIdToBackend()
    {
		Log.d(CommonUtilities.TAG, "1 REGISTER USERID: " );
		//String name = ((EditText)findViewById(R.id.name_edittext)).getText().toString();
//		new AsyncTask<String, Void, String>()
//		{
//		    @Override
//		    protected String doInBackground(String... params)
//		    {
				String msg = "";
				try
				{
//				    Bundle data = new Bundle();
//				    data.putString("name", params[0]);
//				    data.putString("action", "com.zahdoo.mycadie.REGISTER");
//				    String id = Integer.toString(msgId.incrementAndGet());
//				    Log.d(CommonUtilities.TAG, "2 REGISTER USERID: " + regid);
//				    gcm.send(CommonUtilities.SENDER_ID + "@gcm.googleapis.com", id, CommonUtilities.GCM_TIME_TO_LIVE, data);
				    
					if(action.contentEquals("REGISTER_NEW"))
						msg = ServerUtilities.register( name, email, regid , password , "false" );
					else //|| action.contentEquals("REGISTER_OLD")
						msg = ServerUtilities.register( name, email, regid , password , "true");
	                   
				    Log.d(CommonUtilities.TAG, "3 REGISTER USERID: " );
				}
				catch (Exception ex)
				{
					Log.d(CommonUtilities.TAG, "4 REGISTER EXCEPTION: " + ex.toString());
				    msg = ex.toString();
				}
//				return msg;
//		    }
//	
//		    @Override
//		    protected void onPostExecute(String msg)
//		    {
		    	Log.i(CommonUtilities.TAG, "AFTER REGISTER SERVER");
		    	Log.i(CommonUtilities.TAG, "SERVER RESPONSE MSG - " );
		    	
		    	String[] vStrings = msg.split("\\^");
		    	
		    	if(msg.contentEquals(""))
				    RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
				else if(vStrings[0].contentEquals("CODE SENT"))
		    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "VERIFICATION_CODE_SENT^" + regid  + "^" + vStrings[1]);
		    	else if(msg.contentEquals("Sent registration"))
		    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "RegID^" + regid);
		    	else if(msg.contains("SERVICE_NOT_AVAILABLE"))
		    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
		    	else
		    	{
		    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR " + msg);
		    		//Toast.makeText(context, "Error - " + msg, Toast.LENGTH_SHORT).show();
		    	}
		    		
		    	stopSelf();
//		    }
//		}.execute(name);
    }
    
    /**
     * Check the device to make sure it has the Google Play Services APK. If it
     * doesn't, display a dialog that allows users to download the APK from the
     * Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices()
    {
    	Boolean a = false;
    	try {
    		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    		if (resultCode != ConnectionResult.SUCCESS)
    		{
    		    if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
    		    {
//    		    	GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST)
//    				.show();
    			
    				Log.i(CommonUtilities.TAG, "Google Play Services " );
    				
    		    }
    		    else
    		    {
    				Log.i(CommonUtilities.TAG, "This device is not supported.");
    				stopSelf();
    		    }
    		    
    		    
    		    a = false;
    		}
    		a = true;
		} catch (Exception e) {
            // stop executing code by return
		}
    	finally{
    		return a ;
    	}
		
    }
    
    
    /**
     * Send an upstream GCM message to the 3rd party server to remove this 
     * device's registration ID, and contact the GCM server to do the same.
     */
    private void unregister()
    {
		Log.d(CommonUtilities.TAG, "UNREGISTER USERID: " );
//		new AsyncTask<Void, Void, String>()
//		{
//		    @Override
//		    protected String doInBackground(Void... params)
//		    {
				//String msg = "";
				try
				{
//				    Bundle data = new Bundle();
//				    data.putString("action", "com.zahdoo.mycadie.UNREGISTER");
//				    String id = Integer.toString(msgId.incrementAndGet());
//				    gcm.send(CommonUtilities.SENDER_ID + "@gcm.googleapis.com", id, CommonUtilities.GCM_TIME_TO_LIVE, data);
//				    msg = "Sent unregistration";
				    gcm.unregister();
				}
				catch (IOException ex)
				{
					Log.i(CommonUtilities.TAG, "DEVICE UNREGISTRATION ERROR");
				    //msg = "Error :" + ex.toString();
				}
				//return msg;
//		    }
//	
//		    @Override
//		    protected void onPostExecute(String msg)
//		    {
		    	Log.i(CommonUtilities.TAG, "DEVICE UNREGISTERED");
//		    }
//		}.execute();
    }
    
    /**
     * Removes the registration ID from the application's 
     * {@code SharedPreferences}.
     * @param context 
     * 		the application context
     */
//    private void removeRegistrationId(Context context)
//    {
//		final SharedPreferences prefs = getGcmPreferences(context);
//		int appVersion = getAppVersion(context);
//		Log.i(CommonUtilities.TAG, "Removig regId on app version " + appVersion);
//		SharedPreferences.Editor editor = prefs.edit();
//		editor.remove(CommonUtilities.PREFS_PROPERTY_REG_ID);
//		editor.commit();
//		regid = null;
//    }
    
    
    /////////////////////////////////
    /**
     * Store the device's  MAC Address and device info in server whenever app is launched
     * <p>
     * Stores it in the cadie_launch_info table in server
     */
    private void addLaunchInfo()
    {
    	try {
//    		new AsyncTask<Void, Void, String>()
//    		{
//    		    @Override
//    		    protected String doInBackground(Void... params)
//    		    {
    				//String msg = "";
    				try
    				{
    				    sendLaunchInfo();
    				}
    				catch (Exception ex)
    				{
    					Log.d(CommonUtilities.TAG, "3" + ex.toString() );
    					RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR " + ex.toString() );
    					stopSelf();
    				    //msg = "Error :" + ex.toString();
    				}
//    				return msg;
//    		    }
//    	
//    		    @Override
//    		    protected void onPostExecute(String msg)
//    		    {
    		    	Log.i(CommonUtilities.TAG, "AFTER APP LAUNCH INFO");
//    		    }
//    		}.execute(null, null, null);
		} catch (Exception e) {
			Log.d(CommonUtilities.TAG, " APP LAUNCH INFO Error - " + e.toString());
		}
		
    }
    
    /**
     * Sends the device's MAC address and device info to the 3rd party server .
     * Ideally this would be done via HTTP to guarantee success or failure 
     * immediately, but it would require an HTTP endpoint.
     */
    private void sendLaunchInfo()
    {
		Log.d(CommonUtilities.TAG, "1 APP LAUNCH INFO " );
		
//		new AsyncTask<String, Void, String>()
//		{
//		    @Override
//		    protected String doInBackground(String... params)
//		    {
				String msg = "";
				try
				{
					Log.d(CommonUtilities.TAG, "2 APP LAUNCH INFO " );
				    msg = ServerUtilities.postLaunchInfo(name, email,password);// context, MAC address ,deviceInfo and userID(blank if not signed in)
				    Log.d(CommonUtilities.TAG, "3 APP LAUNCH INFO " );
				}
				catch (Exception ex)
				{
					Log.d(CommonUtilities.TAG, "4 APP LAUNCH INFO EXCEPTION: " + ex.toString());
				    msg = ex.toString();
				}
//				return msg;
//		    }
//	
//		    @Override
//		    protected void onPostExecute(String msg)
//		    {
		    	Log.i(CommonUtilities.TAG, "AFTER APP LAUNCH INFO");
		    	Log.i(CommonUtilities.TAG, "SERVER RESPONSE MSG - ");
		    	
		    	stopSelf();
//		    }
//		}.execute(name);
    }
    
    /////////////////////////////////
    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void firstVerification()
    {
    	try {
//    		new AsyncTask<Void, Void, String>()
//    		{
//    		    @Override
//    		    protected String doInBackground(Void... params)
//    		    {
    				//String msg = "";
    				try
    				{
    				    // You should send the registration ID to your server over
    				    // HTTP, so it can use GCM/HTTP or CCS to send messages to your app.
    				    checkUserAvailable();
    		
    				}
    				catch (Exception ex)
    				{
    					Log.d(CommonUtilities.TAG, "3" +ex.toString() );
    					RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR " + ex.toString() );
    					stopSelf();
    				    //msg = "Error :" + ex.toString();
    				}
//    				return msg;
//    		    }
//    	
//    		    @Override
//    		    protected void onPostExecute(String msg)
//    		    {
    		    	Log.i(CommonUtilities.TAG, "AFTER FIRST VERIFICATION");
//    		    }
//    		}.execute(null, null, null);
		} catch (Exception e) {
			Log.d(CommonUtilities.TAG, " FirstVerification Error - " + e.toString());
		}
		
    }
    
    private void checkUserAvailable()
    {
		Log.d(CommonUtilities.TAG, "1 FIRST VERIFICATION " );
//		new AsyncTask<String, Void, String>()
//		{
//		    @Override
//		    protected String doInBackground(String... params)
//		    {
				String msg = "";
				try
				{
					Log.d(CommonUtilities.TAG, "2 FIRST VERIFICATION " );
				    msg = ServerUtilities.checkUserAvailable( email);
				    Log.d(CommonUtilities.TAG, "3 FIRST VERIFICATION " );
				}
				catch (Exception ex)
				{
					Log.d(CommonUtilities.TAG, "4 FIRST VERIFICATION EXCEPTION: " + ex.toString());
				    msg = ex.toString();
				}
//				return msg;
//		    }
//	
//		    @Override
//		    protected void onPostExecute(String msg)
//		    {
		    	Log.i(CommonUtilities.TAG, "AFTER REGISTER SERVER");
		    	Log.i(CommonUtilities.TAG, "SERVER RESPONSE MSG - " );
		    	
		    	String[] vStrings = msg.split("\\^");
		    	
		    	try {
					if(msg.contentEquals(""))
					    RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
					else if(vStrings[0].contentEquals("AVAILABLE"))
						RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "EMAIL_ID_AVAILABLE^"+vStrings[1] );
					else if(msg.contains("SERVICE_NOT_AVAILABLE"))
					    RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
					else//USER_ALREADY_EXISTS
						RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "USER_ALREADY_EXISTS^"+vStrings[1] );
				} catch (Exception e) {
					Log.i(CommonUtilities.TAG, "AFTER REGISTER SERVER Exception = " + e.toString());
				}
		    		
		    	stopSelf();
//		    }
//		}.execute(name);
    }
    
    /////////////////////////////////
    private void checkCadieUser()
    {
    	try {
//    		new AsyncTask<Void, Void, String>()
//    		{
//    		    @Override
//    		    protected String doInBackground(Void... params)
//    		    {
    				//String msg = "";
    				try
    				{
    				    checkContactCadieUsers();
    				}
    				catch (Exception ex)
    				{
    					Log.d(CommonUtilities.TAG, "3" +ex.toString() );
    					RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR " + ex.toString() );
    					stopSelf();
    					
    				  //  msg = "Error :" + ex.toString();
    				}
//    				return msg;
//    		    }
//    	
//    		    @Override
//    		    protected void onPostExecute(String msg)
//    		    {
    		    	Log.i(CommonUtilities.TAG, "AFTER CHECK CADIE USER");
//    		    }
//    		}.execute(null, null, null);
		} catch (Exception e) {
			Log.d(CommonUtilities.TAG, " CHECK CADIE USER Error - " + e.toString());
		}
    }
    
    private void checkContactCadieUsers()
    {
		Log.d(CommonUtilities.TAG, "1 CHECK CADIE USER " );
		
//		new AsyncTask<String, Void, String>()
//		{
//		    @Override
//		    protected String doInBackground(String... params)
//		    {
				String msg = "";
				try
				{
					Log.d(CommonUtilities.TAG, "2 CHECK CADIE USER " );
				    msg = ServerUtilities.checkUserFromContacts( email);
				    Log.d(CommonUtilities.TAG, "3 CHECK CADIE USER " );
				}
				catch (Exception ex)
				{
					Log.d(CommonUtilities.TAG, "4 CHECK CADIE USER: " + ex.toString());
				    msg = ex.toString();
				}
//				return msg;
//		    }
//	
//		    @Override
//		    protected void onPostExecute(String msg)
//		    {
		    	Log.i(CommonUtilities.TAG, "AFTER CHECK CADIE USER");
		    	Log.i(CommonUtilities.TAG, "SERVER RESPONSE MSG - " );
		    	
		    	RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "USER_CHECK_DONE;" + msg );
		    		
		    	stopSelf();
//		    }
//		}.execute(name);
    }    
    
    //////////////////////////////////////////////////////////
    
    private void verifyCode()
    {
    	try {
//    		new AsyncTask<Void, Void, String>()
//    		{
//    		    @Override
//    		    protected String doInBackground(Void... params)
//    		    {
    				//String msg = "";
    				try
    				{
    				    verifyAuthenticationCode();
    		
    				}
    				catch (Exception ex)
    				{
    					Log.d(CommonUtilities.TAG, "3" +ex.toString() );
    					RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR " + ex.toString() );
    					stopSelf();
    				   // msg = "Error :" + ex.toString();
    				}
//    				return msg;
//    		    }
//    	
//    		    @Override
//    		    protected void onPostExecute(String msg)
//    		    {
    		    	Log.i(CommonUtilities.TAG, "AFTER CODE VERIFICATION");
//    		    }
//    		}.execute(null, null, null);
		} catch (Exception e) {
			Log.d(CommonUtilities.TAG, " FirstVerification Error - " + e.toString());
		}
		
    }
    
    private void verifyAuthenticationCode()
    {
		Log.d(CommonUtilities.TAG, "1 AUTHENTICATION " );
//		new AsyncTask<String, Void, String>()
//		{
//		    @Override
//		    protected String doInBackground(String... params)
//		    {
				String msg = "";
				try
				{
					Log.d(CommonUtilities.TAG, "2 AUTHENTICATION " );
				    msg = ServerUtilities.verifyEmailAuthentication( name, email,regIDFromUser,serverUserID,macAddress);//code,emailid,macAddress
				    Log.d(CommonUtilities.TAG, "3 AUTHENTICATION" );
				}
				catch (Exception ex)
				{
					Log.d(CommonUtilities.TAG, "4 AUTHENTICATION EXCEPTION: " + ex.toString());
				    msg = ex.toString();
				}
//				return msg;
//		    }
//	
//		    @Override
//		    protected void onPostExecute(String msg)
//		    {
		    	Log.i(CommonUtilities.TAG, "AFTER REGISTER SERVER");
		    	Log.i(CommonUtilities.TAG, "SERVER RESPONSE MSG - " );
		    	
		    	try{
		    	if(msg.contentEquals(""))
				    RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
				else if(msg.contentEquals("VALID_USER"))
		    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "VALID_USER_CONFIRMED" );
		    	else if(msg.contains("SERVICE_NOT_AVAILABLE"))
		    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
		    	else
		    	{
		    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "CODE_NOT_CONFIRMED" );
		    	}
		    	} catch (Exception e) {
					Log.i(CommonUtilities.TAG, "AFTER REGISTER SERVER Exception = " + e.toString());
				}
		    		
		    	stopSelf();
//		    }
//		}.execute(name);
    }
    
    ////////////////////////////////////////////////////////////
    
    private void checkSignInCredentials()
    {
    	try {
//    		new AsyncTask<Void, Void, String>()
//    		{
//    		    @Override
//    		    protected String doInBackground(Void... params)
//    		    {
    				//String msg = "";
    				try
    				{
    				    checkUserCredentials();
    				}
    				catch (Exception ex)
    				{
    					Log.d(CommonUtilities.TAG, "3" +ex.toString() );
    					RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR " + ex.toString() );
    					stopSelf();
    				    //msg = "Error :" + ex.toString();
    				}
//    				return msg;
//    		    }
//    	
//    		    @Override
//    		    protected void onPostExecute(String msg)
//    		    {
    		    	Log.i(CommonUtilities.TAG, "AFTER SIGN IN VERIFICATION");
//    		    }
//    		}.execute(null, null, null);
		} catch (Exception e) {
			Log.d(CommonUtilities.TAG, " Sign In Check Error - " + e.toString());
		}
    }
    
    /**
     * Sends the registration ID to the 3rd party server via an upstream 
     * GCM message. Ideally this would be done via HTTP to guarantee success or failure 
     * immediately, but it would require an HTTP endpoint.
     */
    private void checkUserCredentials()
    {
		Log.d(CommonUtilities.TAG, "1 SIGN IN Credentials " );
//		new AsyncTask<String, Void, String>()
//		{
//		    @Override
//		    protected String doInBackground(String... params)
//		    {
				String msg = "";
				try
				{
					newRegid = gcm.register(CommonUtilities.SENDER_ID);
				    Log.d(CommonUtilities.TAG, "SIGN IN Device registered, registration ID="  );
					Log.d(CommonUtilities.TAG, "2 SIGN IN Credentials " );
				    msg = ServerUtilities.checkLoginCredentials( email ,password , newRegid,macAddress);//code,emailid
				    Log.d(CommonUtilities.TAG, "3 SIGN IN Credentials " );
				}
				catch (Exception ex)
				{
					Log.d(CommonUtilities.TAG, "4 SIGN IN Credentials EXCEPTION: " + ex.toString());
				    msg = ex.toString();
				}
//				return msg;
//		    }
//	
//		    @Override
//		    protected void onPostExecute(String msg)
//		    {
		    	Log.i(CommonUtilities.TAG, "AFTER SIGN IN Credentials");
		    	Log.i(CommonUtilities.TAG, "SERVER RESPONSE MSG - " );
		    	
		    	String[] vStrings = msg.split("\\^");
		    	
		    	try{
			    	if(msg.contentEquals(""))
					    RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
					else if(vStrings[0].contentEquals("SIGN_IN_OK"))
			    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", 
			    				"SIGN_IN_OK^" + vStrings[1] +  "^"  + vStrings[2] + "^" + vStrings[3] + "^" + newRegid + "^" + vStrings[4] );
			    	else if(msg.contains("USER_NOT_REGISTERED"))
			    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED","USER_NOT_REGISTERED");
			    	else if(msg.contains("SERVICE_NOT_AVAILABLE"))
			    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
			    	else
			    	{
			    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "SIGN_IN_NOT_OK" );
			    	}
		    	} catch (Exception e) {
					Log.i(CommonUtilities.TAG, "AFTER SIGN IN Credentials = " + e.toString());
				}
		    		
		    	stopSelf();
//		    }
//		}.execute(name);
    }
    
    ////////////////////////////////////// 
    private void sendResetCode()
    {
    	try {
//    		new AsyncTask<Void, Void, String>()
//    		{
//    		    @Override
//    		    protected String doInBackground(Void... params)
//    		    {
    				//String msg = "";
    				try
    				{
    					emailResetCode();
    				}
    				catch (Exception ex)
    				{
    					Log.d(CommonUtilities.TAG, "3" +ex.toString() );
    					RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR " + ex.toString() );
    					stopSelf();
    				   // msg = "Error :" + ex.toString();
    				}
//    				return msg;
//    		    }
//    	
//    		    @Override
//    		    protected void onPostExecute(String msg)
//    		    {
    		    	Log.i(CommonUtilities.TAG, "AFTER SIGN IN VERIFICATION");
//    		    }
//    		}.execute(null, null, null);
		} catch (Exception e) {
			Log.d(CommonUtilities.TAG, " Sign In Check Error - " + e.toString());
		}
    }
    
    /**
     * Sends the registration ID to the 3rd party server via an upstream 
     * GCM message. Ideally this would be done via HTTP to guarantee success or failure 
     * immediately, but it would require an HTTP endpoint.
     */
    private void emailResetCode()
    {
		Log.d(CommonUtilities.TAG, "1 SIGN IN Credentials " );
		//String name = ((EditText)findViewById(R.id.name_edittext)).getText().toString();
//		new AsyncTask<String, Void, String>()
//		{
//		    @Override
//		    protected String doInBackground(String... params)
//		    {
				String msg = "";
				try
				{
					Log.d(CommonUtilities.TAG, "2 SIGN IN Credentials " );
				    msg = ServerUtilities.mailResetCode( email, email, action );//code,emailid
				    Log.d(CommonUtilities.TAG, "3 SIGN IN Credentials " );
				}
				catch (Exception ex)
				{
					Log.d(CommonUtilities.TAG, "4 SIGN IN Credentials EXCEPTION: " + ex.toString());
				    msg = ex.toString();
				}
//				return msg;
//		    }
//	
//		    @Override
//		    protected void onPostExecute(String msg)
//		    {
		    	Log.i(CommonUtilities.TAG, "AFTER SIGN IN Credentials");
		    	Log.i(CommonUtilities.TAG, "SERVER RESPONSE MSG - " );
		    	
		    	try{
		    		if(msg.contentEquals(""))
		    			RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
					else if(msg.contentEquals("RESET_CODE_SENT"))
			    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED","RESET_CODE_SENT");
			    	else if(msg.contentEquals("USER_NOT_REGISTERED"))
			    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED","USER_NOT_REGISTERED");
			    	else if(msg.contains("SERVICE_NOT_AVAILABLE"))
			    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
			    	else
			    	{
			    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "RESET_CODE_ERROR" );
			    	}
			    } catch (Exception e) {
					Log.i(CommonUtilities.TAG, "AFTER SIGN IN Credentials = " + e.toString());
				}
		    		
		    	stopSelf();
//		    }
//		}.execute(name);
    }
    
    ////////////////////////////

    private void verifyResetCode()
    {
    	try {
//    		new AsyncTask<Void, Void, String>()
//    		{
//    		    @Override
//    		    protected String doInBackground(Void... params)
//    		    {
    				//String msg = "";
    				try
    				{
    					matchResetCode();
    				}
    				catch (Exception ex)
    				{
    					Log.d(CommonUtilities.TAG, "3" +ex.toString() );
    					RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR " + ex.toString() );
    					stopSelf();
    				   // msg = "Error :" + ex.toString();
    				}
//    				return msg;
//    		    }
//    	
//    		    @Override
//    		    protected void onPostExecute(String msg)
//    		    {
    		    	Log.i(CommonUtilities.TAG, "AFTER SIGN IN VERIFICATION");
//    		    }
//    		}.execute(null, null, null);
		} catch (Exception e) {
			Log.d(CommonUtilities.TAG, " Sign In Check Error - " + e.toString());
		}
    }
    
    private void matchResetCode()
    {
		Log.d(CommonUtilities.TAG, "1 SIGN IN Credentials " );

//		new AsyncTask<String, Void, String>()
//		{
//		    @Override
//		    protected String doInBackground(String... params)
//		    {
				String msg = "";
				try
				{
					Log.d(CommonUtilities.TAG, "2 SIGN IN Credentials " );
				    msg = ServerUtilities.mailResetCode( email, name ,action);//code,emailid
				    Log.d(CommonUtilities.TAG, "3 SIGN IN Credentials " );
				}
				catch (Exception ex)
				{
					Log.d(CommonUtilities.TAG, "4 SIGN IN Credentials EXCEPTION: " + ex.toString());
				    msg = ex.toString();
				}
//				return msg;
//		    }
//	
//		    @Override
//		    protected void onPostExecute(String msg)
//		    {
		    	Log.i(CommonUtilities.TAG, "AFTER SIGN IN Credentials");
		    	Log.i(CommonUtilities.TAG, "SERVER RESPONSE MSG - " );
		    	
		    	try{
			    	if(msg.contentEquals(""))
					    RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
					else if(msg.contentEquals("RESET_CODE_VERIFIED"))
			    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED","RESET_CODE_VERIFIED");
			    	else if(msg.contains("SERVICE_NOT_AVAILABLE"))
			    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
			    	else
			    	{
			    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "RESET_CODE_INCORRECT" );
			    	}
			    } catch (Exception e) {
					Log.i(CommonUtilities.TAG, "AFTER SIGN IN Credentials = " + e.toString());
				}
		    		
		    	stopSelf();
//		    }
//		}.execute(name);
    }
    
    /////////
    
    private void saveNewPassword()
    {
    	try {
//    		new AsyncTask<Void, Void, String>()
//    		{
//    		    @Override
//    		    protected String doInBackground(Void... params)
//    		    {
    				//String msg = "";
    				try
    				{
    					storeNewPassword();
    				}
    				catch (Exception ex)
    				{
    					Log.d(CommonUtilities.TAG, "3" +ex.toString() );
    					RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR " + ex.toString() );
    					stopSelf();
    				  //  msg = "Error :" + ex.toString();
    				}
//    				return msg;
//    		    }
//    	
//    		    @Override
//    		    protected void onPostExecute(String msg)
//    		    {
    		    	Log.i(CommonUtilities.TAG, "AFTER SIGN IN VERIFICATION");
//    		    }
//    		}.execute(null, null, null);
		} catch (Exception e) {
			Log.d(CommonUtilities.TAG, " Sign In Check Error - " + e.toString());
		}
    }
    
    private void storeNewPassword()
    {
		Log.d(CommonUtilities.TAG, "1 SAVE_NEW_PASSWORD " );
//		new AsyncTask<String, Void, String>()
//		{
//		    @Override
//		    protected String doInBackground(String... params)
//		    {
				String msg = "";
				try
				{
					newRegid = gcm.register(CommonUtilities.SENDER_ID);
					Log.d(CommonUtilities.TAG, "2 SAVE_NEW_PASSWORD " );
				    msg = ServerUtilities.saveNewPassword( email, password , newRegid , macAddress );//code,emailid
				    Log.d(CommonUtilities.TAG, "3 SAVE_NEW_PASSWORD " );
				}
				catch (Exception ex)
				{
					Log.d(CommonUtilities.TAG, "4 SAVE_NEW_PASSWORD EXCEPTION: " + ex.toString());
				    msg = ex.toString();
				}
//				return msg;
//		    }
//	
//		    @Override
//		    protected void onPostExecute(String msg)
//		    {
		    	Log.i(CommonUtilities.TAG, "AFTER SAVE_NEW_PASSWORD");
		    	Log.i(CommonUtilities.TAG, "SERVER RESPONSE MSG - " + msg);
		    	
		    	String[] vStrings = msg.split("\\^");
		    	
		    	try{
			    	if(msg.contentEquals(""))
					    RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
					else if(vStrings[0].contentEquals("PASSWORD_RESET_SUCCESSFUL"))
			    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", 
			    				"PASSWORD_RESET_SUCCESSFUL^" + vStrings[1] +  "^"  + vStrings[2] + "^" + vStrings[3] + "^" + newRegid + "^" + vStrings[4]);
			    	else if(msg.contains("SERVICE_NOT_AVAILABLE"))
			    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
			    	else
			    	{
			    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "PASSWORD_RESET_ERROR" );
			    	}
			    		
			    } catch (Exception e) {
			    	Log.i(CommonUtilities.TAG, "AFTER SIGN IN Credentials = " + e.toString());
			    }
		    	stopSelf();
//		    }
//		}.execute(name);
    }
    
    //////////////////////////////////////
    
    private void saveDropboxDetails()
    {
    	try {
//    		new AsyncTask<Void, Void, String>()
//    		{
//    		    @Override
//    		    protected String doInBackground(Void... params)
//    		    {
    				//String msg = "";
    				try
    				{
    					getDropboxInfo();
    				}
    				catch (Exception ex)
    				{
    					Log.d(CommonUtilities.TAG, "3" +ex.toString() );
    					RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR " + ex.toString() );
    					stopSelf();
    				    //msg = "Error :" + ex.toString();
    				}
//    				return msg;
//    		    }
//    	
//    		    @Override
//    		    protected void onPostExecute(String msg)
//    		    {
    		    	Log.i(CommonUtilities.TAG, "AFTER SIGN IN VERIFICATION");
//    		    }
//    		}.execute(null, null, null);
		} catch (Exception e) {
			Log.d(CommonUtilities.TAG, " Sign In Check Error - " + e.toString());
		}
    }
    
    private void getDropboxInfo()
    {
		Log.d(CommonUtilities.TAG, "1 SIGN IN Credentials " );
//		new AsyncTask<String, Void, String>()
//		{
//		    @Override
//		    protected String doInBackground(String... params)
//		    {
				String msg = "";
				try
				{
					Log.d(CommonUtilities.TAG, "2 SIGN IN Credentials " );
				    msg = ServerUtilities.saveDropboxKeys( name, email, password);//code,emailid
				    Log.d(CommonUtilities.TAG, "3 SIGN IN Credentials " );
				}
				catch (Exception ex)
				{
					Log.d(CommonUtilities.TAG, "4 SIGN IN Credentials EXCEPTION: " + ex.toString());
				    msg = ex.toString();
				}
//				return msg;
//		    }
//	
//		    @Override
//		    protected void onPostExecute(String msg)
//		    {
		    	Log.i(CommonUtilities.TAG, "AFTER SIGN IN Credentials");
		    	Log.i(CommonUtilities.TAG, "SERVER RESPONSE MSG - " );
		    	
		    	String[] vStrings = msg.split("\\^");
		    	
		    	try{
			    	if(msg.contentEquals(""))
					    RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
					else if(vStrings[0].contentEquals("DROPBOX_DETAILS_UPDATED"))
			    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "DROPBOX_DETAILS_UPDATED");
			    	else if(msg.contains("SERVICE_NOT_AVAILABLE"))
			    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
			    	else
			    	{
			    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "DROPBOX_UPDATE_ERROR" );
			    	}
			    } catch (Exception e) {
			    	Log.i(CommonUtilities.TAG, "AFTER SIGN IN Credentials = " + e.toString());
			    }
		    		
		    	stopSelf();
//		    }
//		}.execute(name);
    }
    
    /////////////////////////////////////
    
    private void getServerFileNameFromID()
    {
		Log.d(CommonUtilities.TAG, "1 GET SERVER FILE NAME " );
//		new AsyncTask<String, Void, String>()
//		{
//		    @Override
//		    protected String doInBackground(String... params)
//		    {
				String msg = "";
				try
				{
					Log.d(CommonUtilities.TAG, "2 GET SERVER FILE NAME " );
				    msg = ServerUtilities.getFileNameFromID(name);//serverfileId
				    Log.d(CommonUtilities.TAG, "3 GET SERVER FILE NAME " );
				}
				catch (Exception ex)
				{
					Log.d(CommonUtilities.TAG, "4 GET SERVER FILE NAME EXCEPTION: " + ex.toString());
				    msg = ex.toString();
				}
//				return msg;
//		    }
//	
//		    @Override
//		    protected void onPostExecute(String msg)
//		    {
		    	Log.i(CommonUtilities.TAG, "AFTER GET SERVER FILE NAME");
		    	Log.i(CommonUtilities.TAG, "SERVER RESPONSE MSG - " );
		    	
		    	String[] vStrings = msg.split("\\^");
		    	
		    	try{
			    	if(msg.contentEquals(""))
					    RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
					else if(vStrings[0].contentEquals("SUCCESSFUL"))
			    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "GOT_FILE_NAME^" + vStrings[1]);
			    	else if(msg.contains("SERVICE_NOT_AVAILABLE"))
			    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
			    	else
			    	{
			    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR" );
			    	}
		    	} catch (Exception e) {
			    	Log.i(CommonUtilities.TAG, "AFTER SIGN IN Credentials = " + e.toString());
			    }
		    	stopSelf();
//		    }
//		}.execute(name);
    }
    
 /////////////////////////////////////
    
    private void searchUserByNameEmail( String srchName ,final Boolean isEmailSrch)
    {
		Log.d(CommonUtilities.TAG, "1 SEARCH USER " );
//		new AsyncTask<String, Void, String>()
//		{
//		    @Override
//		    protected String doInBackground(String... params)
//		    {
				String msg = "";
				try
				{
					Log.d(CommonUtilities.TAG, "2 SEARCH USER " );
				    msg = ServerUtilities.searchUserByName(name,isEmailSrch);//name/email,isEmailSearch(true/false)
				}
				catch (Exception ex)
				{
					Log.d(CommonUtilities.TAG, "4 SEARCH USER: " + ex.toString());
				    msg = ex.toString();
				}
//				return msg;
//		    }
//	
//		    @Override
//		    protected void onPostExecute(String msg)
//		    {
		    	Log.i(CommonUtilities.TAG, "AFTER SEARCH USER");
		    	Log.i(CommonUtilities.TAG, "SERVER RESPONSE MSG - ");
		    	
		    	try{
			    	if(msg.contentEquals("NO_MATCHING_RESULTS_FOUND"))
			    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_MATCHING_RESULTS_FOUND");
			    	else if(msg.contains("SERVICE_NOT_AVAILABLE"))
			    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
			    	else if(msg.contentEquals("ERROR"))
			    	{
			    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR" );
			    	}
			    	else //CORRECT RESULT
			    	{
			    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", msg );
			    	}
		    	} catch (Exception e) {
			    	Log.i(CommonUtilities.TAG, "AFTER SIGN IN Credentials = " + e.toString());
			    }
		    	stopSelf();
//		    }
//		}.execute(name);
    }
    
 /////////////////////////////////////
    
    private void unfriendUser(final String deleterMailID , final String senderRegID)
    {
		Log.d(CommonUtilities.TAG, "1 UNFRIEND USER " );
//		new AsyncTask<String, Void, String>()
//		{
//		    @Override
//		    protected String doInBackground(String... params)
//		    {
				String msg = "";
				try
				{
					Log.d(CommonUtilities.TAG, "2 UNFRIEND USER " );
				    msg = ServerUtilities.deleteUserFriendFromServer(name,email,password,deleterMailID ,senderRegID);//userId,name,email,deleterMailId,senderRegId
				}
				catch (Exception ex)
				{
					Log.d(CommonUtilities.TAG, "4 UNFRIEND USER: " + ex.toString());
				    msg = ex.toString();
				}
//				return msg;
//		    }
//	
//		    @Override
//		    protected void onPostExecute(String msg)
//		    {
		    	Log.i(CommonUtilities.TAG, "AFTER UNFRIEND USER");
		    	Log.i(CommonUtilities.TAG, "SERVER RESPONSE MSG - " );
		    	
		    	try{
			    	if(msg.contentEquals(""))
					    RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
					else if(msg.contentEquals("UNFRIEND_SUCCESSFUL"))
			    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "UNFRIEND_SUCCESSFUL");
			    	else if(msg.contains("SERVICE_NOT_AVAILABLE"))
			    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
			    	else
			    	{
			    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR" );
			    	}
		    	} catch (Exception e) {
			    	Log.i(CommonUtilities.TAG, "AFTER SIGN IN Credentials = " + e.toString());
			    }
		    	stopSelf();
//		    }
//		}.execute(name);
    }
    
    ////////////////////////////////////////
    
    private void getUsersContactsFromServer()
    {
		Log.d(CommonUtilities.TAG, "1 USER SERVER DATA " );
//		new AsyncTask<String, Void, String>()
//		{
//		    @Override
//		    protected String doInBackground(String... params)
//		    {
				String msg = "";
				try
				{
					Log.d(CommonUtilities.TAG, "2 USER SERVER DATA " );
				    msg = ServerUtilities.getUserServerData(name, email,"CONTACTS");//code,emailid
				    Log.d(CommonUtilities.TAG, "3 USER SERVER DATA " );
				    String[] vStrings = msg.split("\\^");
			    	
			    	if(vStrings[0].contentEquals("INSERT_CADIE_CONTACTS"))
			    	{
			    		Log.i(CommonUtilities.TAG, "1 Gson Parsing - " + vStrings[1]);
			    		msg = doGsonParsing(vStrings[1]);
			    	}
				}
				catch (Exception ex)
				{
					Log.d(CommonUtilities.TAG, "4 USER SERVER DATA EXCEPTION: " + ex.toString());
				    msg = ex.toString();
				}
//				return msg;
//		    }
//	
//		    @Override
//		    protected void onPostExecute(String msg)
//		    {
		    	Log.i(CommonUtilities.TAG, "AFTER USER SERVER DATA");
		    	Log.i(CommonUtilities.TAG, "SERVER RESPONSE MSG - " );
		    	String[] vStrings = msg.split("\\^");
		    	
		    	if(vStrings[0].contentEquals("INSERT_CADIE_CONTACTS_COMPLETED"))
		    	{
		    		Log.i(CommonUtilities.TAG, "1 Gson Parsing - " + vStrings[1]);
//		    		doGsonParsing(vStrings[1]);
		    		getUsersRequestsFromServer();
		    	}
		    	else if(vStrings[0].contentEquals("NO_CONTACTS_FOUND"))
		    	{
		    		getUsersRequestsFromServer();
		    	}
		    	else//ERROR
		    	{
		    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR" );
		    		stopSelf();
		    	}
//		    }
//		}.execute(name);
		
		Log.d(CommonUtilities.TAG, "1 USER SERVER DATA FINISH" );
    }
    
    /////////////////
    private SQLiteDatabase aDataBase;
	
	//private String aDB_PATH = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadieAlertDB.sqlite";
	private String aDB_PATH = "/data/data/air.com.zahdoo.cadie.debug/com.zahdoo.cadie.debug/Local Store/cadieAlertDB.sqlite";
	
    private String doGsonParsing(String gsonStr)
    {
    	Log.i(CommonUtilities.TAG, "2 Gson Parsing  " );
    	
    	try {
            GsonBuilder gson = new GsonBuilder();
            Gson g = gson.create();    
            
            Log.i(CommonUtilities.TAG, "3 Gson Parsing  " );
        	D d = g.fromJson(gsonStr, D.class);
        	Log.i(CommonUtilities.TAG, "4 Gson Parsing  " );
        	
        	aDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
             
        	//Log.i(CommonUtilities.TAG, "6 Gson Parsing - d =  " + d.toString() );
        	//Log.i(CommonUtilities.TAG, "7 Gson Parsing - d.results =  " + d.CadieContacts.toString() );
        	//Log.i(CommonUtilities.TAG, "8 Gson Parsing - d.results.size =  " + d.CadieContacts.size());
        	
			for (int i = 0; i < d.CadieContacts.size(); i++) {
				Result arr = d.CadieContacts.get(i);
				//Insert new Result
				if(arr != null)
					InsertContactsData(arr);
			}
			Log.i(CommonUtilities.TAG, "6 Gson Parsing  " );
		} 
    	catch (Exception e)
    	{
			Log.i(CommonUtilities.TAG, "ERROR Gson Parsing Exception - " + e.toString() );
		}
    	finally{
    		
    		
    		if(aDataBase.isOpen())
    			aDataBase.close();
    		
    		return "INSERT_CADIE_CONTACTS_COMPLETED^CADIE";
    		//getUsersRequestsFromServer();
    		//finish();
		}
    }
    
	private void InsertContactsData(Result ar)
	{
    	try{
    		ContentValues values = new ContentValues();
    		
    	    // Log.i(CommonUtilities.TAG, "1 ar.addedOn - " + ar.addedOn );
    	     
    		values.put("FriendsUserID", ar.friendsUserID);
    		values.put("EmailID", ar.emailID);
    		values.put("Name", ar.name);
    		values.put("Status", 1);
    		values.put("AddedOn", ar.addedOn);
    		
    		values.put("PhoneNo", ar.phoneNo);
    		values.put("ProfileMessage", ar.profileMessage);
    		values.put("DateOfBirth", ar.dateOfBirth);
    		values.put("Location", ar.location);
    		values.put("ProfilePicName", ar.profilePicName);
    		values.put("LastActivityOn", ar.addedOn);
    		values.put("isActive", 1);
    		
    		aDataBase.insert("CADIEContacts", null, values);
    		
    		values.clear();
		}
    	catch (Exception e)
    	{
    		Log.i(CommonUtilities.TAG, "1 ERROR Gson PARSING - " + e.toString() );
		}
	}
    
    class D {
    	
        List<Result> CadieContacts;
    }
    
    class Result {
        @SerializedName("FriendsUserID")
        public String friendsUserID;

        @SerializedName("EmailID")
        public String emailID;
        
        @SerializedName("Name")
        public String name;
        
        @SerializedName("PhoneNo")
        public String phoneNo;
        
        @SerializedName("AddedOn")
        public String addedOn;

        @SerializedName("ProfileMessage")
        public String profileMessage;
        
        @SerializedName("DateOfBirth")
        public String dateOfBirth;
        
        @SerializedName("Location")
        public String location;
        
        @SerializedName("ProfilePicName")
        public String profilePicName;
        
    }
    
    ///////////////////////////////////////////////////////////////////////////
    
    private void getUsersRequestsFromServer()
    {
		Log.d(CommonUtilities.TAG, "1 USER REQUEST DATA " );
//		new AsyncTask<String, Void, String>()
//		{
//		    @Override
//		    protected String doInBackground(String... params)
//		    {
				String msg = "";
				try
				{
					Log.d(CommonUtilities.TAG, "2 USER REQUEST DATA " );
				    msg = ServerUtilities.getUserServerData(name, email,"REQUESTS");//code,emailid
				    Log.d(CommonUtilities.TAG, "3 USER REQUEST DATA " );
				    String[] vStrings = msg.split("\\^");
			    	
			    	if(vStrings[0].contentEquals("INSERT_CADIE_REQUESTS"))
			    	{
			    		msg = doRequestsGsonParsing(vStrings[1]);
			    	}
//			    	else if(vStrings[0].contentEquals("NO_REQUESTS_FOUND"))
//			    	{
//			    		Log.i(CommonUtilities.TAG, "No Gson REQUEST FOUND  ");
//			    		getUsersInvitationsFromServer();
//			    		
//			    	}
//			    	else//ERROR
//			    	{
//			    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR" );
//			    	}
				}
				catch (Exception ex)
				{
					Log.d(CommonUtilities.TAG, "4 USER REQUEST DATA EXCEPTION: " + ex.toString());
				    msg = ex.toString();
				}
//				return msg;
//		    }
//	
//		    @Override
//		    protected void onPostExecute(String msg)
//		    {
		    	Log.i(CommonUtilities.TAG, "AFTER USER REQUEST DATA");
		    	Log.i(CommonUtilities.TAG, "REQUEST RESPONSE MSG - " );
		    	
		    	String[] vStrings = msg.split("\\^");
		    	
		    	if(vStrings[0].contentEquals("INSERT_CADIE_REQUESTS_COMPLETED"))
		    	{
		    		//if(action.contentEquals("DOWNLOAD_USER_DATA"))
		    			getUsersChatShareInfoFromServer();
//		    		else
//		    		{
//		    			RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "SET_UP_SHARE_INFO");
//		    			finish();
//		    		}
		    		//Log.i(CommonUtilities.TAG, "1 Gson REQUEST Parsing - " + vStrings[1]);
		    		//doRequestsGsonParsing(vStrings[1]);
		    	}
		    	else if(vStrings[0].contentEquals("NO_REQUESTS_FOUND"))
		    	{
		    		Log.i(CommonUtilities.TAG, "No Gson REQUEST FOUND  ");
		    	
		    		//if(action.contentEquals("DOWNLOAD_USER_DATA"))
		    			getUsersChatShareInfoFromServer();
//		    		else
//		    		{
//		    			RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "SET_UP_SHARE_INFO");
//		    			finish();
//		    		}
		    		
		    	}
		    	else//ERROR
		    	{
		    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR" );
		    		stopSelf();
		    	}
		    		
		    	//finish();
//		    }
//		}.execute(name);
		
		Log.d(CommonUtilities.TAG, "1 USER SERVER DATA FINISH" );
    }
    
    
    private String doRequestsGsonParsing(String gsonStr)
    {
    	
    	Log.i(CommonUtilities.TAG, "2 Gson REQUEST Parsing  " );
    	
    	try {
            GsonBuilder gson = new GsonBuilder();
            Gson g = gson.create();    
            
            Log.i(CommonUtilities.TAG, "3 Gson REQUEST Parsing  " );
            
        	D1 d1 = g.fromJson(gsonStr, D1.class);
        	
        	Log.i(CommonUtilities.TAG, "4 Gson REQUEST Parsing  " );
        	
        	aDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
             
        //	Log.i(CommonUtilities.TAG, "6 Gson REQUEST Parsing - d1 =  " + d1.toString() );
        //	Log.i(CommonUtilities.TAG, "7 Gson REQUEST Parsing - d1.CadieRequests =  " + d1.CadieRequests.toString() );
        //	Log.i(CommonUtilities.TAG, "8 Gson REQUEST Parsing - d1.CadieRequests.size =  " + d1.CadieRequests.size());
        	
			for (int i = 0; i < d1.CadieRequests.size(); i++) {
				Request arr = d1.CadieRequests.get(i);
				//Insert new Result
				if(arr != null)
					InsertRequestsData(arr);
			}
			Log.i(CommonUtilities.TAG, "6 Gson REQUEST Parsing  " );
		} 
    	catch (Exception e)
    	{
			Log.i(CommonUtilities.TAG, "ERROR Gson REQUEST Parsing Exception - " + e.toString() );
		}
    	finally{
    		
    		if(aDataBase.isOpen())
    			aDataBase.close();
    		
    		return "INSERT_CADIE_REQUESTS_COMPLETED^CADIE";
    		//getUsersInvitationsFromServer();
    		//finish();
		}
    }
    
    private void InsertRequestsData(Request ar)
	{
    	try{
    		Log.i(CommonUtilities.TAG, "InsertRequestsData  "  );
    		ContentValues values = new ContentValues();
    		values.put("FromEmailID", ar.fromEmailID);
    		values.put("FromUserName", ar.fromUserName);
    		values.put("RequestType", ar.requestType);
    		values.put("InviteRequestType", ar.inviteRequestType);
    		values.put("Message", ar.message);
    		values.put("Status", 0);
    		values.put("ToEmailID", ar.toEmailID);
    		values.put("ToUserName", ar.toUserName);
    		values.put("ServerRequestID", Integer.parseInt(ar.serverRequestID));
    		values.put("OnDateTime", ar.onDateTime);
    		
    		aDataBase.insert("CADIEInvitesRequests", null, values);
    		
    		values.clear();
    		//c = null;
		}
    	catch (Exception e)
    	{
    		Log.i(CommonUtilities.TAG, "1 ERROR Gson PARSING - " + e.toString() );
		}
	}
    
    class D1 {
    	
        List<Request> CadieRequests;
    }
    
    class Request {
        @SerializedName("ServerRequestID")
        public String serverRequestID;

        @SerializedName("FromEmailID")
        public String fromEmailID;
        
        @SerializedName("FromUserName")
        public String fromUserName;
        
        @SerializedName("ToEmailID")
        public String toEmailID;
        
        @SerializedName("ToUserName")
        public String toUserName;

        @SerializedName("RequestType")
        public String requestType;
        
        @SerializedName("InviteRequestType")
        public String inviteRequestType;
        
        @SerializedName("Message")
        public String message;
        
        @SerializedName("Status")
        public String status;
        
        @SerializedName("OnDateTime")
        public String onDateTime;
    }
    
    ///////////////////////////////////////////////////////////////////////////
    
    private void getUsersChatShareInfoFromServer()
    {
		Log.d(CommonUtilities.TAG, "1 USER CHAT_SHARE_INFO DATA " );
//		new AsyncTask<String, Void, String>()
//		{
//		    @Override
//		    protected String doInBackground(String... params)
//		    {
				String msg = "";
				try
				{
					Log.d(CommonUtilities.TAG, "2 USER CHAT_SHARE_INFO DATA " );
				    msg = ServerUtilities.getUserServerData(name, email,"CHAT_SHARE_INFO");//code,emailid
				    Log.d(CommonUtilities.TAG, "3 USER CHAT_SHARE_INFO DATA " );
				    String[] vStrings = msg.split("\\^");
			    	
			    	if(vStrings[0].contentEquals("INSERT_CADIE_CHAT_SHARE_INFO"))
			    	{
			    		Log.i(CommonUtilities.TAG, "1 Gson CHAT_SHARE_INFO Parsing - " + vStrings[1]);
			    		msg = doChatShareInfoGsonParsing(vStrings[1]);
			    	}
				}catch (Exception ex){
					Log.d(CommonUtilities.TAG, "4 USER CHAT_SHARE_INFO DATA EXCEPTION: " + ex.toString());
				    msg = ex.toString();
				}
//				return msg;
//		    }
//	
//		    @Override
//		    protected void onPostExecute(String msg)
//		    {
		    	Log.i(CommonUtilities.TAG, "AFTER USER CHAT_SHARE_INFO DATA");
		    	Log.i(CommonUtilities.TAG, "CHAT_SHARE_INFO RESPONSE MSG - " );
		    	
		    	String[] vStrings = msg.split("\\^");
			    	
		    	try{
			    	if(msg.contentEquals(""))
					    RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
					else if(vStrings[0].contentEquals("INSERT_CADIE_CHAT_SHARE_INFO_COMPLETED"))
			    	{
						///
						if (! msgShareID.contentEquals(""))
							InsertSupportContact();
						
						///
			    		
						RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "SET_UP_SHARE_INFO");
			    	}
			    	else if(vStrings[0].contentEquals("NO_CHAT_SHARE_INFO_FOUND"))
			    	{
			    		Log.i(CommonUtilities.TAG, "No Gson CHAT_SHARE_INFO FOUND  ");
			    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "SET_UP_SHARE_INFO");
			    	}
			    	else if(msg.contains("SERVICE_NOT_AVAILABLE"))
			    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
			    	else//ERROR
			    	{
			    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR" );
			    	}
		    	}catch (Exception ex){
					Log.d(CommonUtilities.TAG, "AFTER USER CHAT_SHARE_INFO DATA EXCEPTION: " + ex.toString());
				}
		    	stopSelf();
//		    }
//		}.execute(name);
		
    }
    
    private String doChatShareInfoGsonParsing(String gsonStr)
    {
    	Log.i(CommonUtilities.TAG, "2 Gson CHAT_SHARE_INFO Parsing  " );
    	
    	try {
            GsonBuilder gson = new GsonBuilder();
            Gson g = gson.create();    
            Log.i(CommonUtilities.TAG, "3 Gson CHAT_SHARE_INFO Parsing  " );
        	D3 d3 = g.fromJson(gsonStr, D3.class);
        	Log.i(CommonUtilities.TAG, "4 Gson CHAT_SHARE_INFO Parsing  " );
        	
        	aDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
             
        	//Log.i(CommonUtilities.TAG, "6 Gson CHAT_SHARE_INFO Parsing - d3 =  " + d3.toString() );
        	//Log.i(CommonUtilities.TAG, "7 Gson CHAT_SHARE_INFO Parsing - d3.CadieShareInfo =  " + d3.CadieShareInfo.toString() );
        	//Log.i(CommonUtilities.TAG, "8 Gson CHAT_SHARE_INFO Parsing - d3.CadieShareInfo.size =  " + d3.CadieShareInfo.size());
        	
			for (int i = 0; i < d3.CadieShareInfo.size(); i++) {
				ShareInfo arr = d3.CadieShareInfo.get(i);
				//Insert new Result
				if(arr != null)
					InsertShareInfoData(arr);
			}
			Log.i(CommonUtilities.TAG, "6 Gson CHAT_SHARE_INFO Parsing  " );
		} 
    	catch (Exception e)
    	{
			Log.i(CommonUtilities.TAG, "ERROR Gson CHAT_SHARE_INFO Parsing Exception - " + e.toString() );
		}
    	finally{

    		if(aDataBase.isOpen())
    			aDataBase.close();
    		
    		return"INSERT_CADIE_CHAT_SHARE_INFO_COMPLETED";
    		//finish();
		}
    }
    
    private void InsertShareInfoData(ShareInfo ar)
	{
    	try{
    		Log.i(CommonUtilities.TAG, "InsertShareInfoData  "  );
    		ContentValues values = new ContentValues();
    		
    		values.put("ShareHistoryID", UUID.randomUUID().toString());
    		values.put("NotificationType", ar.notificationType);
    		values.put("ShareID", ar.shareID);
    		values.put("ByEmailID", ar.byEmailID);
    		
    		values.put("ByName", ar.byName);
    		values.put("ToEmailID", ar.toEmailID);
    		
    		if(ar.shareType.contentEquals("SHARED"))
    		{
    			values.put("FriendsEmailID", ar.toEmailID);
    			values.put("FriendsName", ar.toName);
    		}
    		else
    		{
    			values.put("FriendsEmailID", ar.byEmailID);
    			values.put("FriendsName", ar.byName);
    		}
    		
    		values.put("ShareType", ar.shareType);
    		
    		if(ar.notificationType.contentEquals("NOTE"))
    			values.put("NoteID", ar.noteID);
    		else
    			values.put("NoteID", UUID.randomUUID().toString());
    		
    		values.put("NoteTitle", ar.noteTitle);
    		values.put("DbName", ar.dbName);
    		values.put("isViewed", true);
    		values.put("isNoteViewed", false);
    		
    		values.put("isSynced", 0);
    		values.put("isDeleted", 0);
    		
    		values.put("SharedOn", ar.sharedOn);

    		values.put("isNoteDeleted", false);
    		values.put("isContactUnfriended", false);
    		values.put("FileName", ar.fileName);
    		values.put("FileSize", ar.fileSize);
    		
    		aDataBase.insert("ShareHistory", null, values);
    		
    		values.clear();
    		//c = null;
		}
    	catch (Exception e)
    	{
    		Log.i(CommonUtilities.TAG, "1 ERROR Gson PARSING - " + e.toString() );
		}
	}
    
    
    
    class D3 {
    	
        List<ShareInfo> CadieShareInfo;
    }
    
    
    
    class ShareInfo {
        @SerializedName("ShareID")
        public String shareID;

        @SerializedName("ByEmailID")
        public String byEmailID;
        
        @SerializedName("ByName")
        public String byName;
        
        @SerializedName("ToName")
        public String toName;
        
        @SerializedName("ToEmailID")
        public String toEmailID;
        
        @SerializedName("ShareType")
        public String shareType;
        
        @SerializedName("NoteID")
        public String noteID;
        
        @SerializedName("NoteTitle")
        public String noteTitle;
        
        @SerializedName("DbName")
        public String dbName;
        
        @SerializedName("NotificationType")
        public String notificationType;
        
        @SerializedName("SharedOn")
        public String sharedOn;
        
        @SerializedName("FileName")
        public String fileName;
        
        @SerializedName("FileSize")
        public int fileSize;
    }

    //////////////////////////////////////////////////////////
    
    private void verifyHavingCode()
    {
    	try {
//    		new AsyncTask<Void, Void, String>()
//    		{
//    		    @Override
//    		    protected String doInBackground(Void... params)
//    		    {
    				//String msg = "";
    				try
    				{
    				    checkUserAvailableForHavingCode();
    				}
    				catch (Exception ex)
    				{
    					Log.d(CommonUtilities.TAG, "3" +ex.toString() );
    					RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR " + ex.toString() );
    					stopSelf();
    				   // msg = "Error :" + ex.toString();
    				}
//    				return msg;
//    		    }
//    	
//    		    @Override
//    		    protected void onPostExecute(String msg)
//    		    {
    		    	Log.i(CommonUtilities.TAG, "AFTER HAVING CODE VERIFICATION");
//    		    }
//    		}.execute(null, null, null);
		} catch (Exception e) {
			Log.d(CommonUtilities.TAG, " FirstVerification Error - " + e.getMessage());
		}
		
    }
    
    ////////////////////////////////////////////////////////////
    
    private void checkUserAvailableForHavingCode()
    {
		Log.d(CommonUtilities.TAG, "1 FIRST VERIFICATION " );
//		new AsyncTask<String, Void, String>()
//		{
//		    @Override
//		    protected String doInBackground(String... params)
//		    {
				String msg = "";
				try
				{
					Log.d(CommonUtilities.TAG, "2 FIRST VERIFICATION " );
				    
				    msg = ServerUtilities.checkUserAvailable( email);
	                   
				    Log.d(CommonUtilities.TAG, "3 FIRST VERIFICATION " );
				}
				catch (Exception ex)
				{
					Log.d(CommonUtilities.TAG, "4 FIRST VERIFICATION EXCEPTION: " + ex.toString());
				    msg = ex.toString();
				}
//				return msg;
//		    }
//	
//		    @Override
//		    protected void onPostExecute(String msg)
//		    {
		    	Log.i(CommonUtilities.TAG, "AFTER REGISTER SERVER");
		    	Log.i(CommonUtilities.TAG, "SERVER RESPONSE MSG - " );
		    	
		    	String[] vStrings = msg.split("\\^");
		    	
		    	if(vStrings[0].contentEquals("AVAILABLE"))
		    	{
		    		//RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "EMAIL_ID_AVAILABLE^"+vStrings[1] );
		    		sendRegistrationIdToBackendForHavingCode();
		    	}
		    	else if(msg.contains("SERVICE_NOT_AVAILABLE"))
		    	{
		    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
		    		stopSelf();
		    	}
		    	else//USER_ALREADY_EXISTS
		    	{
		    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "USER_ALREADY_EXISTS^"+vStrings[1] );
		    		stopSelf();
		    	}
//		    }
//		}.execute(name);
    }
    
    //////////////////
    
    private void sendRegistrationIdToBackendForHavingCode()
    {
		Log.d(CommonUtilities.TAG, "1 REGISTER USERID: " );
//		new AsyncTask<String, Void, String>()
//		{
//		    @Override
//		    protected String doInBackground(String... params)
//		    {
				String msg = "";
				try
				{
					 if (gcm == null)
 				    {
 				    	gcm = GoogleCloudMessaging.getInstance(RegisterService.this);
 				    }
 				    
 				    regid = gcm.register(CommonUtilities.SENDER_ID);
 				    
					msg = ServerUtilities.register( name, email, regid , password , "havingTrue");
	                   
				    Log.d(CommonUtilities.TAG, "3 REGISTER USERID: ");
				}
				catch (Exception ex)
				{
					Log.d(CommonUtilities.TAG, "4 REGISTER EXCEPTION: " + ex.toString());
				    msg = ex.toString();
				}
//				return msg;
//		    }
//	
//		    @Override
//		    protected void onPostExecute(String msg)
//		    {
		    	Log.i(CommonUtilities.TAG, "AFTER REGISTER SERVER");
		    	Log.i(CommonUtilities.TAG, "SERVER RESPONSE MSG - " );
		    	
		    	String[] vStrings = msg.split("\\^");
		    	
		    	if(vStrings[0].contentEquals("CODE SENT"))
		    	{
		    		//RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "VERIFICATION_CODE_SENT^" + regid  + "^" + vStrings[1]);
		    		serverUserID =  vStrings[1];
		    		verifyAuthenticationCodeForHavingCode();
		    	}
		    	else if(msg.contentEquals("Sent registration"))
		    	{
		    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "RegID^" + regid);
		    		stopSelf();
		    	}
		    	else if(msg.contains("SERVICE_NOT_AVAILABLE"))
		    	{
		    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
		    		stopSelf();
		    	}
		    	else
		    	{
		    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR " + msg);
		    		//Toast.makeText(context, "Error - " + msg, Toast.LENGTH_SHORT).show();
		    		stopSelf();
		    	}
//		    }
//		}.execute(name);
    }
    /////////////////////////////////////////////////////////////////
    
    private void verifyAuthenticationCodeForHavingCode()
    {
		Log.d(CommonUtilities.TAG, "1 AUTHENTICATION " );
//		new AsyncTask<String, Void, String>()
//		{
//		    @Override
//		    protected String doInBackground(String... params)
//		    {
				String msg = "";
				try
				{
					Log.d(CommonUtilities.TAG, "2 AUTHENTICATION " );
				    msg = ServerUtilities.verifyEmailAuthentication( verificationCode, email,regid,serverUserID,macAddress);//code,emailid,macAddress
				    Log.d(CommonUtilities.TAG, "3 AUTHENTICATION" );
				}
				catch (Exception ex)
				{
					Log.d(CommonUtilities.TAG, "4 AUTHENTICATION EXCEPTION: " + ex.toString());
				    msg = ex.toString();
				}
//				return msg;
//		    }
//	
//		    @Override
//		    protected void onPostExecute(String msg)
//		    {
		    	Log.i(CommonUtilities.TAG, "AFTER REGISTER SERVER");
		    	Log.i(CommonUtilities.TAG, "SERVER RESPONSE MSG - " );
		    	
		    	if(msg.contentEquals("VALID_USER"))
		    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "VALID_USER_CONFIRMED_HAVING_CODE^" + serverUserID  + "^" + regid);
		    	else if(msg.contains("SERVICE_NOT_AVAILABLE"))
		    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
		    	else
		    	{
		    		RegisterFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "CODE_NOT_CONFIRMED" );
		    	}
		    	stopSelf();
//		    }
//		}.execute(name);
    }
    
    
    
    
    
    ///
    /**
     * Insert contact request info in CADIEContacts table
     */
    
    private void InsertSupportContact()
	{
    	try{
    		aDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    		Log.d(CommonUtilities.TAG, "Inside Insert Contact Support  " ); 
    		
			
			ContentValues values = new ContentValues();
    		values.put("FriendsUserID", "35");
    		values.put("EmailID", "support@mycadie.com");
    		values.put("Name", "Cadie Support");
    		values.put("Status", 1);
    		//values.put("AddedOn", getDateTime());
    		values.put("AddedOn", msgTime);
    		
    		values.put("PhoneNo", "");
    		values.put("ProfileMessage", "");
    		values.put("DateOfBirth", "");
    		values.put("Location", "");
    		values.put("ProfilePicName", "support.jpg");
    		
    		values.put("isActive", 1);
    		//values.put("LastActivityOn", getDateTime());
    		values.put("LastActivityOn", msgTime);
    		
    		aDataBase.insert("CADIEContacts", null, values);
    		values.clear();
    		
    		//cDataBase = SQLiteDatabase.openDatabase(cDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
//
//    		values = new ContentValues();
//    		values.put("ShareHistoryID", UUID.randomUUID().toString());
//    		values.put("NotificationType", "CONTACT_ACCEPT_MESSAGE");
//    		values.put("ShareID", msgShareID);
//    		values.put("ByEmailID", "support@mycadie.com");
//    		
//    		values.put("ByName", "Cadie Support");
//    		values.put("ToEmailID", email);
//    		values.put("FriendsEmailID", "support@mycadie.com");
//    		values.put("FriendsName", "Cadie Support");
//
//    		values.put("ShareType", "RECEIVED");
//    		
//    		values.put("NoteID", UUID.randomUUID().toString());
//    		values.put("NoteTitle", "Cadie Buddy Request Accepted");
//    		values.put("DbName", "");
//    		
//    		values.put("isViewed", false);
//    		
//    		values.put("isNoteViewed", false);
//    		
//    		values.put("isSynced", 0);
//    		values.put("isDeleted", 0);
//    		values.put("isNoteDeleted", false);
//    		values.put("isContactUnfriended", false);
//    		values.put("FileName", "");
//    		values.put("FileSize", 0);
//    		//values.put("SharedOn", getDateTime());
//    		values.put("SharedOn", msgTime);
//    		
//    		aDataBase.insert("ShareHistory", null, values);
    		
//    		values.clear();
    		
		}
    	catch (Exception e)
    	{
    		Log.d(CommonUtilities.TAG,"ContactRequests Insert Error"); 
		}
    	finally{
    		Log.d(CommonUtilities.TAG,"ContactRequests inserted.."); 
    		
    		if(aDataBase.isOpen())
    			aDataBase.close();
    	}
	}
}