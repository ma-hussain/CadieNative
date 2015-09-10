package com.zahdoo.android.extension.GCM;

import static com.zahdoo.android.extension.GCM.CommonUtilities.SERVER_SEND_MSG_URL;
import static com.zahdoo.android.extension.GCM.CommonUtilities.SERVER_URL;
import static com.zahdoo.android.extension.GCM.CommonUtilities.TAG;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import android.content.Context;
import android.util.Log;
 
 
public final class ServerUtilities 
{
    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
    private static int status = 0;
    private static String response = "";
    
    static String sendMessage( String shareID, String byEmail, String byName, String toEmail, String noteID, String noteTitle, String dbName,
    		String notificationType,String fileSize , String senderRegID , String toName) 
    {
        Log.i(TAG, "Sending msg to other user (email = " + toEmail + ")");
        String serverUrl = SERVER_SEND_MSG_URL;
        Map<String, String> params = new HashMap<String, String>();
        
        if(notificationType.contentEquals("MESSAGE"))
        {
            params.put("byEmail", byEmail);
            params.put("byName", byName);
            params.put("toEmail", toEmail);
            params.put("toName", dbName);
            params.put("byUserID", noteID);
            params.put("messageText", noteTitle);
            params.put("notificationType", notificationType);
        }
        else if(notificationType.contentEquals("PICTURE") || notificationType.contentEquals("VIDEO")
        		|| notificationType.contentEquals("VOICE") || notificationType.contentEquals("FILE"))
        {
        	//@PARAMS  ServerFileId, sendermailID,SenderName, ReceiverMailId, senderuserID, Title(At present blank),fileName,filetype , fileSize
        	params.put("serverFileID", shareID);
            params.put("byEmail", byEmail);
            params.put("byName", byName);
            params.put("toEmail", toEmail);
            params.put("byUserID", noteID);
            params.put("messageText", noteTitle);//noteTitle
            params.put("fileName", dbName);
            params.put("notificationType", notificationType);
            params.put("fileSize", fileSize + "");
            params.put("toName", toName);
        }
        else if( notificationType.contentEquals("EXPORTED_PDF"))
        {
        	//@PARAMS  ServerFileId, sendermailID(s),SenderName, ReceiverMailId, senderuserID, Title(At present blank),fileName,filetype , fileSize
        	params.put("serverFileID", shareID);
            params.put("byEmail", byEmail);
            params.put("byName", byName);
            params.put("toEmail", toEmail);
            params.put("byUserID", noteID);
            params.put("messageText", noteTitle);//noteTitle
            params.put("fileName", dbName);
            params.put("notificationType", notificationType);
            params.put("fileSize", fileSize + "");
            params.put("toName", toName);
        }
        else{
        	params.put("toName", shareID);
            params.put("byEmail", byEmail);
            params.put("byName", byName);
            params.put("toEmail", toEmail);
            params.put("noteID", noteID);
            params.put("noteTitle", noteTitle);
            params.put("dbName", dbName);
            params.put("notificationType", notificationType);
            
        }
        
        params.put("senderDeviceRegID", senderRegID);
        
        String resp = "ERROR";
        
        
        Log.d(CommonUtilities.TAG, "DbName - " + dbName);
         
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d(TAG, "Attempt #" + i + " to send");
            try {
                resp = post(serverUrl, params);
                break;
            } 
            catch (IOException e) 
            {
                Log.e(TAG, "Failed to register on attempt " + i + ":" + e.toString());
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                    Thread.sleep(backoff);
                }
                catch (InterruptedException e1) 
                {
                    Log.d(TAG, "Thread interrupted: abort remaining retries!");
                    Thread.currentThread().interrupt();
                }
                // increase backoff exponentially
                backoff *= 2;
            }
        }
        
        return resp;
    }
    
    /**
     * Register this account/device pair within the server.
     *
     */
    static String register(String name, String email, final String regId , String password , String isOldRegistration)  
    {
        String serverUrl = SERVER_URL;
        String resp = "ERROR";
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("regId", regId);
        params.put("name", name);
        params.put("email", email);
        params.put("password", password);
        params.put("isOldRegistration", isOldRegistration);
         
        Log.d(CommonUtilities.TAG, "1 IN SERVER Utilities : " );
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        // Once GCM returns a registration id, we need to register on our server
        // As the server might be down, we will retry it a couple
        // times.
        for (int i = 1; i <= MAX_ATTEMPTS; i++) 
        {
            try {
                //Toast.makeText(context, "Connecting to Server to register this device.", Toast.LENGTH_SHORT).show();
            	Log.d(TAG, "Connecting to Server to register this device.");
                
                resp = post(serverUrl, params);
                //GCMRegistrar.setRegisteredOnServer(context, true);
                
                //Toast.makeText(context, "Device registered on Server.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Device registered on Server.");
                break;
            } catch (IOException e) {
                // Here we are simplifying and retrying on any error; in a real
                // application, it should retry only on unrecoverable errors
                // (like HTTP error code 503).

            	
            	Log.d(CommonUtilities.TAG, "5 IN SERVER Utilities CAtch Exception  : " + e.toString() );
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    // Activity finished before we complete - exit.
                	Log.d(CommonUtilities.TAG, "6 IN SERVER Utilities CAtch Exception  : " + e.toString() );
                    Thread.currentThread().interrupt();
                    Log.d(CommonUtilities.TAG, "7 IN SERVER Utilities CAtch Exception  : " + e.toString() );
                }
                // increase backoff exponentially
                backoff *= 2;
            }
        }
        
        //Toast.makeText(context, "Could not register device on Server.", Toast.LENGTH_SHORT).show();
        //Log.d(TAG, "Could not register device on Server.");
        return resp;
    }
 
    /**
     * Unregister this account/device pair within the server.
     */
//    static void unregister(final Context context, final String regId) {
//        Log.i(TAG, "unregistering device (regId = " + regId + ")");
//        String serverUrl = SERVER_URL + "/unregister";
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("regId", regId);
//        try {
//            post(serverUrl, params);
//           // GCMRegistrar.setRegisteredOnServer(context, false);
//            
//            //Toast.makeText(context, "Device successfully removed on Cadie Server", Toast.LENGTH_SHORT).show();
//            Log.d(TAG, "Device successfully removed on Cadie Server");
//            
//        } catch (IOException e) {
//            // At this point the device is unregistered from GCM, but still
//            // registered in the server.
//            // We could try to unregister again, but it is not necessary:
//            // if the server tries to send a message to the device, it will get
//            // a "NotRegistered" error message and should unregister the device.
//        	//Toast.makeText(context, "Could not remove Device on Cadie Server", Toast.LENGTH_SHORT).show();
//        	Log.d(TAG, "Could not remove Device on Cadie Server");
//        }
//    }
 
    /**
     * Issue a POST request to the server.
     *
     * @param endpoint POST address.
     * @param params request parameters.
     *
     * @throws IOException propagated from POST.
     */
    private static String post(String endpoint, Map<String, String> params)
            throws IOException 
    {   
        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        
        
        Log.d(CommonUtilities.TAG, "2 IN SERVER Utilities Post : " );
        
        
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        
        
        Log.d(CommonUtilities.TAG, "3 IN SERVER Utilities Post : " );
        
        String body = bodyBuilder.toString();
        Log.v(TAG, "Posting '" + body + "' to " + url);
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        
        try {
            Log.e("URL", "> " + url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            
            Log.d(CommonUtilities.TAG, "4 IN SERVER Utilities Post : " );
            // post the request
            OutputStream out = conn.getOutputStream();
            Log.d(CommonUtilities.TAG, "A1 IN SERVER Utilities Post : " );
            out.write(bytes);
            Log.d(CommonUtilities.TAG, "A2 IN SERVER Utilities Post : " );
            out.close();
            Log.d(CommonUtilities.TAG, "A3 IN SERVER Utilities Post : " );
            // handle the response
            status = conn.getResponseCode();
            
            Log.d(CommonUtilities.TAG, "5 IN SERVER Utilities Post - Response Code  : " + status );
            if (status != 200) {
              throw new IOException("Post failed with error code " + status);
            }
            else{
            	// Get the response from Server PHP file 
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = "";
                
                response = "";
                
                while ((line = rd.readLine()) != null) 
                {
                	response += line;
                }
                
                Log.e("CADIE Post Response ", "> " + response); //CODE SENT || REG ERROR
               
            }
        } 
        catch (Exception e) {
        	Log.d(CommonUtilities.TAG, "Server Post Exception: " + e.toString());
        	response = "ERROR";
        }
        finally {
        	Log.d(CommonUtilities.TAG, "onPostServer:");
            if (conn != null) {
                conn.disconnect();
            }
            return response;
        }
      }
    
    //////////////////////////////
    
    /**
     * Register this account/device pair within the server.
     *
     */
    static String checkUserAvailable( String email) 
    {
        String serverUrl = "http://54.88.103.38/gcm_server_php/gcm_user_availability.php";
        String resp = "ERROR";
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);
         
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

        for (int i = 1; i <= MAX_ATTEMPTS; i++) 
        {
            try {
                resp = post(serverUrl, params);
                Log.d(TAG, "Check User Available Response - " + resp);
                break;
            } catch (IOException e) {
            	
            	Log.d(CommonUtilities.TAG, "5 IN SERVER Utilities CAtch Exception  : " + e.toString() );
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                	Log.d(CommonUtilities.TAG, "6 IN SERVER Utilities CAtch Exception  : " + e.toString() );
                    Thread.currentThread().interrupt();
                    Log.d(CommonUtilities.TAG, "7 IN SERVER Utilities CAtch Exception  : " + e.toString() );
                }
                backoff *= 2;
            }
        }
        
        return resp;
    }
    
 //////////////////////////////
    
    /**
     * Register this account/device pair within the server.
     *
     */
    static String postLaunchInfo( String macAddress, String devicesInfo, String userID) 
    {
        String serverUrl = "http://54.88.103.38/gcm_server_php/GCM_App_Launch_Info.php";
        String resp = "ERROR";
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("macAddress", macAddress);
        params.put("deviceInfo", devicesInfo);
        params.put("userID", userID);
         
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

        for (int i = 1; i <= MAX_ATTEMPTS; i++) 
        {
            try {
                
                resp = post(serverUrl, params);
                Log.d(TAG, "Check User Available Response - " + resp);
                break;
            } catch (IOException e) {
            	
            	Log.d(CommonUtilities.TAG, "5 IN SERVER Utilities CAtch Exception  : " + e.toString() );
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                	Log.d(CommonUtilities.TAG, "6 IN SERVER Utilities CAtch Exception  : " + e.toString() );
                    Thread.currentThread().interrupt();
                    Log.d(CommonUtilities.TAG, "7 IN SERVER Utilities CAtch Exception  : " + e.toString() );
                }
                // increase backoff exponentially
                backoff *= 2;
            }
        }
        
        return resp;
    }
    
    //////////////////////////////
    
    static String checkUserFromContacts( String email) 
    {
    	String serverUrl = "http://54.88.103.38/gcm_server_php/gcm_user_check.php";
        String resp = "ERROR";
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);
         
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

        for (int i = 1; i <= MAX_ATTEMPTS; i++) 
        {
            try {
                resp = post(serverUrl, params);
                Log.d(TAG, "Check User Available Response - " + resp);
                break;
            } catch (IOException e) {
            	
            	Log.d(CommonUtilities.TAG, "5 IN SERVER Utilities CAtch Exception  : " + e.toString() );
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                	Log.d(CommonUtilities.TAG, "6 IN SERVER Utilities CAtch Exception  : " + e.toString() );
                    Thread.currentThread().interrupt();
                    Log.d(CommonUtilities.TAG, "7 IN SERVER Utilities CAtch Exception  : " + e.toString() );
                }
                backoff *= 2;
            }
        }

        return resp;
    }
    
 //////////////////////////////

    static String verifyEmailAuthentication( String code, String email, String regIdFromUser , String serverUserID , String macAddress ) 
    {
        String serverUrl = "http://54.88.103.38/gcm_server_php/gcm_user_verification2.php";
        String resp = "ERROR";
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("code", code);
        params.put("email", email);
        params.put("regIdFromUser", regIdFromUser);
        params.put("serverUserID", serverUserID);
        params.put("macAddress", macAddress);
        
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        
        for (int i = 1; i <= MAX_ATTEMPTS; i++) 
        {
            try {
                resp = post(serverUrl, params);
                Log.d(TAG, "Check Code Authentication Response - " + resp);
                break;
            } catch (IOException e) {
            	
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                	Log.d(CommonUtilities.TAG, "6 IN SERVER Utilities CAtch Exception  : " + e.toString() );
                    Thread.currentThread().interrupt();
                    Log.d(CommonUtilities.TAG, "7 IN SERVER Utilities CAtch Exception  : " + e.toString() );
                }
                backoff *= 2;
            }
        }
        
        return resp;
    }
    
 //////////////////////////////
    
    static String checkLoginCredentials( String email , String password , String newRegid , String macAddress) 
    {
        String serverUrl = "http://54.88.103.38/gcm_server_php/GCM_login_authentication.php";
        String resp = "ERROR";
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("password", password);
        params.put("newRegid", newRegid);
        params.put("macAddress", macAddress);
         
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

        for (int i = 1; i <= MAX_ATTEMPTS; i++) 
        {
            try {
                resp = post(serverUrl, params);
                Log.d(TAG, "Check Code Authentication Response - " + resp);
                break;
            } catch (IOException e) {
            	Log.d(CommonUtilities.TAG, "5 IN SERVER Utilities CAtch Exception  : " + e.toString() );
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                	Log.d(CommonUtilities.TAG, "6 IN SERVER Utilities CAtch Exception  : " + e.toString() );
                    Thread.currentThread().interrupt();
                    Log.d(CommonUtilities.TAG, "7 IN SERVER Utilities CAtch Exception  : " + e.toString() );
                }
                backoff *= 2;
            }
        }
        
        return resp;
    }
    
    //	//////////////////////////////////////
    
    static String insertS3FileMap( String serverFileName) 
    {
        String serverUrl = "http://54.88.103.38/gcm_server_php/GCM_S3_File_Map.php";
        String resp = "ERROR";
        
        Map<String, String> params = new HashMap<String, String>();
        params.put("fileName", serverFileName);
         
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

        for (int i = 1; i <= MAX_ATTEMPTS; i++) 
        {
            try {
                resp = post(serverUrl, params);
                Log.d(TAG, "Check Code Authentication Response - " + resp);
                break;
            } catch (IOException e) {
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                	Log.d(CommonUtilities.TAG, "6 IN SERVER Utilities CAtch Exception  : " + e.toString() );
                    Thread.currentThread().interrupt();
                    Log.d(CommonUtilities.TAG, "7 IN SERVER Utilities CAtch Exception  : " + e.toString() );
                }
                backoff *= 2;
            }
        }

        return resp;
    }
    
    ////////////////////////////////////////
    
    static String mailResetCode( String email ,  String code , String action_type) 
    {
    	String serverUrl = "http://54.88.103.38/gcm_server_php/GCM_sendResetCode.php";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	if(action_type.contentEquals("SEND_RESET_CODE")){
    		
    		params.put("email", email);
    		params.put("action", action_type);
    	}
    	else{
    		params.put("email", email);
    		params.put("code", code);
    		params.put("action", action_type);
    	}
        
        String resp = "ERROR";
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

        for (int i = 1; i <= MAX_ATTEMPTS; i++) 
        {
            try {
                resp = post(serverUrl, params);
                Log.d(TAG, "Mail Reset Code Response - " + resp);
                break;
            } catch (IOException e) {
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                	Log.d(CommonUtilities.TAG, "6 Mail Reset Code Exception  : " + e.toString() );
                    Thread.currentThread().interrupt();
                    Log.d(CommonUtilities.TAG, "7 Mail Reset Code Exception  : " + e.toString() );
                }
                backoff *= 2;
            }
        }
        
        return resp;
    }
    
    ///////////////////////////////////////
    static String saveNewPassword( String email ,  String password , String newRegid , String macAddress) 
    {
    	String serverUrl = "http://54.88.103.38/gcm_server_php/GCM_saveNewPassword.php";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.put("email", email);
    	params.put("password", password);
    	params.put("newRegid", newRegid);
    	params.put("macAddress", macAddress);
        
        String resp = "ERROR";
      
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

        for (int i = 1; i <= MAX_ATTEMPTS; i++) 
        {
            try {
                resp = post(serverUrl, params);
                Log.d(TAG, "Mail Reset Code Response - " + resp);

                break;
            } catch (IOException e) {
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                	Log.d(CommonUtilities.TAG, "6 Mail Reset Code Exception  : " + e.toString() );
                    Thread.currentThread().interrupt();
                    Log.d(CommonUtilities.TAG, "7 Mail Reset Code Exception  : " + e.toString() );
                }
                backoff *= 2;
            }
        }

        return resp;
    }
    
    //////
    
    static String saveDropboxKeys(String userId , String emailId, String accReqTokens) 
    {
    	String serverUrl = "http://54.88.103.38/gcm_server_php/GCM_saveDropboxInfo.php";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.put("userId", userId);
    	params.put("emailId", emailId);
    	
    	String[] keyArr = accReqTokens.split("\\;");
		
    	params.put("reqKey", keyArr[0]);
    	params.put("reqSecret", keyArr[1]);
    	params.put("accKey", keyArr[2]);
    	params.put("accSecret", keyArr[3]);
        
        String resp = "ERROR";

        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

        for (int i = 1; i <= MAX_ATTEMPTS; i++) 
        {
            try {
                resp = post(serverUrl, params);
                Log.d(TAG, "Dropbox Info Response - " + resp);
                break;
            } catch (IOException e) {
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                	Log.d(CommonUtilities.TAG, "6 Dropbox Info Exception  : " + e.toString() );
                    Thread.currentThread().interrupt();
                    Log.d(CommonUtilities.TAG, "7 Dropbox Info Exception  : " + e.toString() );
                }
                backoff *= 2;
            }
        }
        
        return resp;
    }
    
 //////
    static String searchUserByName( String srchName , Boolean isEmailSrch) 
    {
    	String serverUrl = "http://54.88.103.38/gcm_server_php/GCM_searchUserByNameOrEmail.php";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.put("srchNameOrEmail", srchName);
    	
    	if(isEmailSrch)
    		params.put("srchType", "EMAIL_SEARCH");
    	else
    		params.put("srchType", "NAME_SEARCH");
        
        String resp = "ERROR";
        
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

        for (int i = 1; i <= MAX_ATTEMPTS; i++) 
        {
            try {
                resp = post(serverUrl, params);
                Log.d(TAG, "Get Server Name Response - " + resp);

                break;
            } catch (IOException e) {
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                	Log.d(CommonUtilities.TAG, "Get Server Name Exception  : " + e.toString() );
                    Thread.currentThread().interrupt();
                    Log.d(CommonUtilities.TAG, "Get Server Name  : " + e.toString() );
                }
                backoff *= 2;
            }
        }
        
        return resp;
    }
    
//////
    
    static String getFileNameFromID( String serverFileID) 
    {
    	String serverUrl = "http://54.88.103.38/gcm_server_php/GCM_getServerFileName.php";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.put("serverFileID", serverFileID);
        
        String resp = "ERROR";
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        
        for (int i = 1; i <= MAX_ATTEMPTS; i++) 
        {
            try {
                resp = post(serverUrl, params);
                Log.d(TAG, "Get Server Name Response - " + resp);
                break;
            } catch (IOException e) {
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                	Log.d(CommonUtilities.TAG, "Get Server Name Exception  : " + e.toString() );
                    Thread.currentThread().interrupt();
                    Log.d(CommonUtilities.TAG, "Get Server Name  : " + e.toString() );
                }
                backoff *= 2;
            }
        }
        
        return resp;
    }
    
//////
    
    static String deleteUserFriendFromServer(String deleterUserID, String deleterUserName, String deleterEmailID , String unfriendEmailID , String senderRegID) 
    {
    	String serverUrl = "http://54.88.103.38/gcm_server_php/GCM_unfriendUserFromContacts_SELF.php";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.put("deleterUserID", deleterUserID);
    	params.put("deleterUserName", deleterUserName);
    	params.put("deleterEmailID", deleterEmailID);
    	params.put("unfriendEmailID", unfriendEmailID);
    	params.put("senderDeviceRegID", senderRegID);
        
        String resp = "ERROR";
        
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        
        for (int i = 1; i <= MAX_ATTEMPTS; i++) 
        {
            try {
                resp = post(serverUrl, params);
                Log.d(TAG, "Get Unfriend User Response - " + resp);
                break;
            } catch (IOException e) {
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                	Log.d(CommonUtilities.TAG, "Get Unfriend User Exception  : " + e.toString() );
                    Thread.currentThread().interrupt();
                    Log.d(CommonUtilities.TAG, "Get Unfriend User  : " + e.toString() );
                }
                backoff *= 2;
            }
        }
        return resp;
    }
    
 //////
    
    static String sendContactRequest( String userId , String byEmail , String byName, String toEmail ,String reqMessage ,String toUserName ,String senderRegID) 
    {
    	String serverUrl = "http://54.88.103.38/gcm_server_php/GCM_sendContactRequest_SELF.php";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.put("userId", userId);
    	params.put("byEmail", byEmail);
    	params.put("byName", byName);
    	params.put("toEmail", toEmail);
    	params.put("reqMessage", reqMessage);
    	params.put("toUserName", toUserName);
    	params.put("senderDeviceRegID", senderRegID);
        
        String resp = "ERROR";
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

        for (int i = 1; i <= MAX_ATTEMPTS; i++) 
        {
            try {
                resp = post(serverUrl, params);
                Log.d(TAG, "Contact Request Response - " + resp);
                break;
            } catch (IOException e) {
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                	Log.d(CommonUtilities.TAG, "6 Contact Request Exception  : " + e.toString() );
                    Thread.currentThread().interrupt();
                    Log.d(CommonUtilities.TAG, "7 Contact Request Exception  : " + e.toString() );
                }
                backoff *= 2;
            }
        }
        
        return resp;
    }
    
    ///
    // reqAccepterUserId, reqAccepterMailId , reqAccepterName, reqSenderMailId , serverReqId, reqSenderUsername

    static String acceptDeclineContactRequest(String reqAccepterUserId , String reqAccepterMailId , String reqAccepterName, 
    		String reqSenderMailId, String serverReqId ,String reqSenderUsername , String responseType ,String senderRegID) 
    {
    	String serverUrl = "http://54.88.103.38/gcm_server_php/GCM_acceptContactRequest_SELF.php";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.put("serverReqId", serverReqId);
    	params.put("reqAccepterName", reqAccepterName);
    	params.put("reqAccepterUserId", reqAccepterUserId);
    	params.put("reqAccepterMailId", reqAccepterMailId);
    	params.put("reqSenderMailId", reqSenderMailId);
    	params.put("responseType", responseType);
    	params.put("senderDeviceRegID", senderRegID);
        
        String resp = "ERROR";
        
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

        for (int i = 1; i <= MAX_ATTEMPTS; i++) 
        {
            try {
                resp = post(serverUrl, params);
                Log.d(TAG, "Contact Request Response - " + resp);
                break;
            } catch (IOException e) {
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    // Activity finished before we complete - exit.
                	Log.d(CommonUtilities.TAG, "6 Contact Request Exception  : " + e.toString() );
                    Thread.currentThread().interrupt();
                    Log.d(CommonUtilities.TAG, "7 Contact Request Exception  : " + e.toString() );
                }
                backoff *= 2;
            }
        }

        return resp;
    }
    
    //////////////////////////
  // invitedByUserID, invitedByEmail, invitedByName, invitationToEmail, invitationToName,  invitationMessage ,notificationType
    
    /**
     * Send invitation mail
     *
     */
    static String sendMailInvitation( String invitedByUserID , String invitedByEmail , String invitedByName , String invitationToEmail, 
    		String invitationToName, String invitationMessage , String senderRegID) 
    {
    	String serverUrl = "http://54.88.103.38/gcm_server_php/GCM_sendMailInvitation_SELF.php";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.put("invitedByUserID", 	invitedByUserID);
    	params.put("invitedByEmail", 	invitedByEmail);
    	params.put("invitedByName", 	invitedByName);
    	params.put("invitationToEmail", invitationToEmail);
    	params.put("invitationToName", 	invitationToName);
    	params.put("invitationMessage",	invitationMessage);
    	params.put("senderDeviceRegID", senderRegID); 
        
        String resp = "ERROR";
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

        for (int i = 1; i <= MAX_ATTEMPTS; i++) 
        {
            try {
                resp = post(serverUrl, params);
                Log.d(TAG, "Send Invitation Response - " + resp);
                break;
            } catch (IOException e) {
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                	Log.d(CommonUtilities.TAG, "6 Send Invitation Exception  : " + e.toString() );
                    Thread.currentThread().interrupt();
                    Log.d(CommonUtilities.TAG, "7 Send Invitation Exception  : " + e.toString() );
                }
                backoff *= 2;
            }
        }
        
        return resp;
    }
    
    //////////////////////////
       //Params are  name, emailid, status_msg, date_of_birth, phone_no, location_name, profile_pic_name
      
      static String updateProfileInfo( String name , String emailid , String status_msg , String date_of_birth, 
      		String phone_no, String location_name , String profile_pic_name , Boolean isProfilePicChanged , Boolean isProfileNameChanged , String senderRegID) 
      {
      	String serverUrl = "http://54.88.103.38/gcm_server_php/GCM_updateProfileInfo_SELF.php";
      	Map<String, String> params = new HashMap<String, String>();
      	
      	params.put("userName", 			name);
      	params.put("userEmailId", 		emailid);
      	params.put("userStatusMsg", 	status_msg);
      	params.put("userDateOfBirth", 	date_of_birth);
      	params.put("userPhoneNo", 		phone_no);
      	params.put("userLocationName",	location_name);
      	params.put("userProfilePicName",profile_pic_name);
      	params.put("senderDeviceRegID", senderRegID);
      	
      	if(isProfilePicChanged)
      		params.put("isUserProfilePicChanged","YES");
      	else
      		params.put("isUserProfilePicChanged","NO");
      	
      	if(isProfileNameChanged)
      		params.put("isUserProfileNameChanged","YES");
      	else
      		params.put("isUserProfileNameChanged","NO");
      		
          String resp = "ERROR";
          long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

          for (int i = 1; i <= MAX_ATTEMPTS; i++) 
          {
              try {
                  resp = post(serverUrl, params);
                  Log.d(TAG, "Send Invitation Response - " + resp);
                  break;
              } catch (IOException e) {
                  if (i == MAX_ATTEMPTS) {
                      break;
                  }
                  try {
                      Thread.sleep(backoff);
                  } catch (InterruptedException e1) {
                  	Log.d(CommonUtilities.TAG, "6 Send Invitation Exception  : " + e.toString() );
                      Thread.currentThread().interrupt();
                      Log.d(CommonUtilities.TAG, "7 Send Invitation Exception  : " + e.toString() );
                  }
                  backoff *= 2;
              }
          }
          
          return resp;
      }
      
      //////////////////////////
      //Params are emailid
     
     static String getUserProfileDataFromServer( String emailid  ) 
     {
     	String serverUrl = "http://54.88.103.38/gcm_server_php/GCM_getUserProfileInfo.php";
     	Map<String, String> params = new HashMap<String, String>();
     	
     	params.put("userEmailId", 		emailid);
         
         String resp = "ERROR";
         long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

         for (int i = 1; i <= MAX_ATTEMPTS; i++) 
         {
             try {
                 resp = post(serverUrl, params);
                 Log.d(TAG, "Send Invitation Response - " + resp);
                 break;
             } catch (IOException e) {
                 if (i == MAX_ATTEMPTS) {
                     break;
                 }
                 try {
                     Thread.sleep(backoff);
                 } catch (InterruptedException e1) {
                 	Log.d(CommonUtilities.TAG, "6 Send Invitation Exception  : " + e.toString() );
                     Thread.currentThread().interrupt();
                     Log.d(CommonUtilities.TAG, "7 Send Invitation Exception  : " + e.toString() );
                 }
                 backoff *= 2;
             }
         }
         
         return resp;
     }
      
    //////////////////////////
    // reqAccepterUserId, reqAccepterMailId , reqAccepterName, reqSenderMailId , serverReqId, reqSenderUsername
    
    static String getUserServerData(String userCadieID , String userEmailID, String dataTableType) 
    {
    	String serverUrl = "http://54.88.103.38/gcm_server_php/GCM_getUserContactsData2.php";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.put("userID", userCadieID);
    	params.put("emailID", userEmailID);
    	params.put("dataTableType", dataTableType);
        
        String resp = "ERROR";
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

        for (int i = 1; i <= MAX_ATTEMPTS; i++) 
        {
            try {
                resp = post(serverUrl, params);
                Log.d(TAG, "Contact Request Response - " + resp);
                break;
            } catch (IOException e) {
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                	Log.d(CommonUtilities.TAG, "6 Contact Request Exception  : " + e.toString() );
                    Thread.currentThread().interrupt();
                    Log.d(CommonUtilities.TAG, "7 Contact Request Exception  : " + e.toString() );
                }
                backoff *= 2;
            }
        }
        
        return resp;
    }
    
    static String setUpContactWithSupport(String userCadieID , String userEmailID) 
    {
    	String serverUrl = "http://54.88.103.38/gcm_server_php/GCM_setUpContactWithSupport.php";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.put("userID", userCadieID);
    	params.put("emailID", userEmailID);
        
        String resp = "ERROR";
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

        for (int i = 1; i <= MAX_ATTEMPTS; i++) 
        {
            try {
                resp = post(serverUrl, params);
                Log.d(TAG, "Contact Request Response - " + resp);
                break;
            } catch (IOException e) {
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                	Log.d(CommonUtilities.TAG, "6 Contact Request Exception  : " + e.toString() );
                    Thread.currentThread().interrupt();
                    Log.d(CommonUtilities.TAG, "7 Contact Request Exception  : " + e.toString() );
                }
                backoff *= 2;
            }
        }
        
        return resp;
    }
    
    ////////////////////////////////////////////////
    //Params are  userid
   
   static String getBuddySuggestion( String userId  ) 
   {
   		String serverUrl = "http://54.88.103.38/gcm_server_php/GCM_getBuddySuggestion.php";
   		Map<String, String> params = new HashMap<String, String>();
   		params.put("userId", userId );
       
       String resp = "ERROR";
       long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

       for (int i = 1; i <= MAX_ATTEMPTS; i++) 
       {
           try {
               resp = post(serverUrl, params);
               Log.d(TAG, "Send Invitation Response - " + resp);
               break;
           } catch (IOException e) {
               if (i == MAX_ATTEMPTS) {
                   break;
               }
               try {
                   Thread.sleep(backoff);
               } catch (InterruptedException e1) {
               	Log.d(CommonUtilities.TAG, "6 Send Invitation Exception  : " + e.toString() );
                   Thread.currentThread().interrupt();
                   Log.d(CommonUtilities.TAG, "7 Send Invitation Exception  : " + e.toString() );
               }
               backoff *= 2;
           }
       }
       
       return resp;
   }
   
   /////////////////////////////////////////////////////////////////////
    
   static String reRegisterOnAppUpdate( String oldRegId,String newRegId,String emailId ) 
   {
   		String serverUrl = "http://54.88.103.38/gcm_server_php/GCM_reRegisterOnUpdate.php";
   		Map<String, String> params = new HashMap<String, String>();
   	
   		params.put("oldRegId", oldRegId);
    	params.put("newRegId", newRegId);
    	params.put("emailId", emailId);
        
       String resp = "ERROR";
       long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

       for (int i = 1; i <= MAX_ATTEMPTS; i++) 
       {
           try {
               resp = post(serverUrl, params);
               Log.d(TAG, "reRegisterOnAppUpdate Response - " + resp);
               break;
           } catch (IOException e) {
               if (i == MAX_ATTEMPTS) {
                   break;
               }
               try {
                   Thread.sleep(backoff);
               } catch (InterruptedException e1) {
               	Log.d(CommonUtilities.TAG, "6 reRegisterOnAppUpdate Exception  : " + e.toString() );
                   Thread.currentThread().interrupt();
                   Log.d(CommonUtilities.TAG, "7 reRegisterOnAppUpdate Exception  : " + e.toString() );
               }
               backoff *= 2;
           }
       }
       
       return resp;
   }
   
}