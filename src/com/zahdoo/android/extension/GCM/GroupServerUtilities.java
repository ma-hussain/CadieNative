package com.zahdoo.android.extension.GCM;

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

import android.util.Log;
 
 
public final class GroupServerUtilities 
{
    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
    private static int status = 0;
    private static String response = "";
 
    
 
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
        
        while (iterator.hasNext()) 
        {
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
            
            Log.d(CommonUtilities.TAG, "93 IN Group Utilities Post - Response Code  : " + status );
            
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
    
    
    
    //	=================================================================
    
    static String removeUserFromGroup(String actionType , String userCadieID , String userEmailID,  String userName, String userRegId, String serverGrpID, String grpName) 
    {
    	String serverUrl = "http://54.88.103.38/gcm_server_php/GCM_Remove_User_From_Group_SELF.php";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.put("actionType", actionType);
    	params.put("userID", userCadieID);
    	params.put("emailID", userEmailID);
    	params.put("userName", userName);
    	params.put("userRegId", userRegId);
    	params.put("serverGroupId", serverGrpID);
    	params.put("groupName", grpName);
    	
    	Log.d(TAG, "Contact Request - " + userCadieID + " >> " + userEmailID  + " >> " +serverGrpID + " >> " + grpName);
    	
        
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
                	Log.d(CommonUtilities.TAG, "6 GCM_Group Data Download Request Exception  : " + e.toString() );
                    Thread.currentThread().interrupt();
                    Log.d(CommonUtilities.TAG, "7 GCM_Group Data Download Request Exception  : " + e.toString() );
                }
                backoff *= 2;
            }
        }
        
        return resp;
    }
    /////////////////////////////////
   
    
    static String createNewGroup( String grpTitle, String grpProfilePic, String byUserID, String byEmail, 
    		String byName, String byRegID, String friendsID, String friendsEmail, String friendsName) 
    {
    	String serverUrl = "http://54.88.103.38/gcm_server_php/GCM_Group_Create_New.php";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.put("grpTitle"		, grpTitle);
    	params.put("grpProfilePic"	, grpProfilePic);
    	params.put("grpType"		, "GROUP");
    	params.put("userID"			, byUserID);
    	params.put("byEmail"		, byEmail);
    	params.put("byName"			, byName);
    	params.put("byRegID"		, byRegID);
    	
    	params.put("friendsID"		, friendsID);
    	params.put("friendsEmail"	, friendsEmail);
    	params.put("friendsName"	, friendsName);
        
        String resp = "ERROR";
        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);

        for (int i = 1; i <= MAX_ATTEMPTS; i++) 
        {
            try {
                resp = post(serverUrl, params);
                Log.d(TAG, "Create New Group Response - " + resp);
                break;
            } 
            catch (IOException e) 
            {
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                
                try {
                    Thread.sleep(backoff);
                } 
                catch (InterruptedException e1) 
                {
                	Log.d(CommonUtilities.TAG, "E 01 Create New Group Exception  : " + e.toString() );
                    Thread.currentThread().interrupt();
                    Log.d(CommonUtilities.TAG, "E 02 Create New Group Exception  : " + e.toString() );
                }
                
                backoff *= 2;
            }
        }
        
        return resp;
    }
    
    
      
    //////////////////////////
    // reqAccepterUserId, reqAccepterMailId , reqAccepterName, reqSenderMailId , serverReqId, reqSenderUsername

    //=================================================================
    
    static String getUserServerData(String userCadieID , String userEmailID, String dataTableType, String SGID) 
    {
    	String serverUrl = "http://54.88.103.38/gcm_server_php/GCM_Group_Data_Download.php";
    	Map<String, String> params = new HashMap<String, String>();
    	
    	params.put("userID", userCadieID);
    	params.put("emailID", userEmailID);
    	params.put("dataTableType", dataTableType);
    	params.put("SGID", SGID);
    	
    	Log.d(TAG, "Contact Request - " + userCadieID + " >> " + userEmailID  + " >> " +dataTableType + " >> " + SGID);
    	
        
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
                	Log.d(CommonUtilities.TAG, "6 GCM_Group Data Download Request Exception  : " + e.toString() );
                    Thread.currentThread().interrupt();
                    Log.d(CommonUtilities.TAG, "7 GCM_Group Data Download Request Exception  : " + e.toString() );
                }
                backoff *= 2;
            }
        }
        
        return resp;
    }
    
    //===================================================
    
//    
//static String getGroupChatDataFromServer( String emailid  ) 
//{
//	String serverUrl = "http://54.88.103.38/gcm_server_php/GCM_GroupChatDataDownload.php";
//	Map<String, String> params = new HashMap<String, String>();
//	
//	params.put("userEmailId", 		emailid);
//	
//	String resp = "ERROR";
//	long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
//	
//	for (int i = 1; i <= MAX_ATTEMPTS; i++) 
//	{
//		try {
//			resp = post(serverUrl, params);
//			Log.d(TAG, "Send Invitation Response - " + resp);
//			break;
//		} catch (IOException e) {
//			if (i == MAX_ATTEMPTS) {
//				break;
//			}
//			try 
//			{
//				Thread.sleep(backoff);
//			} 
//			catch (InterruptedException e1) 
//			{
//				Log.d(CommonUtilities.TAG, "6 Send Invitation Exception  : " + e.toString() );
//				Thread.currentThread().interrupt();
//				Log.d(CommonUtilities.TAG, "7 Send Invitation Exception  : " + e.toString() );
//			}
//			backoff *= 2;
//		}
//	}
//	
//	return resp;
//}

static String sendMessage(String serverGroupID, String GMID, String grpTitle, String byUserID, String byEmail, String byName, String byRegID,
			String message, String messageType, String shareType, 
			String noteID, String noteTitle, String dbName, String fileName, String filePath, String fileSize)
{
  Log.i(TAG, "Sending msg to other user (serverGroupID = " + serverGroupID + ")");
  String serverUrl = "http://54.88.103.38/gcm_server_php/GCM_Group_send_cadie_msg_SELF.php";
  Map<String, String> params = new HashMap<String, String>();
  
  if(messageType.contentEquals("GROUP_MESSAGE"))
  {
	  params.put("serverGroupID", serverGroupID);
	  params.put("GMID", GMID);
	  params.put("grpTitle", grpTitle);
	  params.put("byUserID", byUserID);
      params.put("byEmail", byEmail);
      params.put("byName", byName);
      params.put("byRegID", byRegID);
      params.put("message", message);
      params.put("messageText", message);
      params.put("messageType", messageType); //GROUP_MESSAGE
      params.put("shareType", shareType); //TEXT / IMAGE / VIDEO
      params.put("noteID", noteID);
      params.put("noteTitle", noteTitle);
      params.put("dbName", dbName);
      params.put("fileName", fileName);
      params.put("filePath", filePath);
      params.put("fileSize", fileSize);
  }
  
  String resp = "ERROR";
  
   
  long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
  
  for (int i = 1; i <= MAX_ATTEMPTS; i++) {
      Log.e(TAG, "Attempt #" + i + " to send the message to group");
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


static String sendGroupFiles(String serverGroupID, String GMID, String grpTitle, 
							String byUserID, String byEmail, String byName, String byRegID,
							String serverFileID, String messageType, String shareType, 
							String noteID, String noteTitle, String dbName, 
							String fileTitle, String fileName, String filePath, String fileSize )
{
  Log.i(TAG, "Sending msg to other user (serverGroupID = " + serverGroupID + ")");
  String serverUrl = "http://54.88.103.38/gcm_server_php/GCM_Group_send_file_msg_SELF.php";
  Map<String, String> params = new HashMap<String, String>();
  
  //if(messageType.contentEquals("GROUP_MESSAGE"))
  //{
	  params.put("serverGroupID", serverGroupID);
	  params.put("GMID"			, GMID);
	  params.put("grpTitle"		, grpTitle);
	  params.put("byUserID"		, byUserID);
      params.put("byEmail"		, byEmail);
      params.put("byName"		, byName);
      params.put("byRegID"		, byRegID);
      params.put("messageType"	, messageType); //GROUP_MESSAGE
      params.put("shareType"	, shareType); //TEXT / IMAGE / VIDEO
      params.put("noteID"		, noteID);
      params.put("noteTitle"	, noteTitle);
      params.put("dbName"		, dbName);
      
      params.put("fileTitle"	, fileTitle);
      params.put("fileName"		, fileName);
      params.put("filePath"		, filePath);
      params.put("fileSize"		, fileSize + "");
      
      params.put("serverFileID", serverFileID);
 // }
  
  
  
//  else if(notificationType.contentEquals("PICTURE") || notificationType.contentEquals("VIDEO")
//          || notificationType.contentEquals("VOICE") || notificationType.contentEquals("FILE"))
//  {
//   //@PARAMS  ServerFileId, sendermailID,SenderName, ReceiverMailId, senderuserID, Title(At present blank),fileName,filetype , fileSize
//   params.put("serverFileID", shareID);
//      params.put("byEmail", byEmail);
//      params.put("byName", byName);
//      params.put("toEmail", toEmail);
//      params.put("byUserID", noteID);
//      params.put("messageText", noteTitle);
//      params.put("fileName", dbName);
//      params.put("notificationType", notificationType);
//      params.put("fileSize", fileSize + "");
//      params.put("toName", toName);
//  }
//  else if( notificationType.contentEquals("EXPORTED_PDF"))
//  {
//   	//@PARAMS  ServerFileId, sendermailID(s),SenderName, ReceiverMailId, senderuserID, Title(At present blank),fileName,filetype , fileSize
//   	params.put("serverFileID", shareID);
//      params.put("byEmail", byEmail);
//      params.put("byName", byName);
//      params.put("toEmail", toEmail);
//      params.put("byUserID", noteID);
//      params.put("messageText", noteTitle);//noteTitle
//      params.put("fileName", dbName);
//      params.put("notificationType", notificationType);
//      params.put("fileSize", fileSize + "");
//      params.put("toName", toName);
//  }
//  else{
//  	params.put("toName", shareID);
//      params.put("byEmail", byEmail);
//      params.put("byName", byName);
//      params.put("toEmail", toEmail);
//      params.put("noteID", noteID);
//      params.put("noteTitle", noteTitle);
//      params.put("dbName", dbName);
//      params.put("notificationType", notificationType);
//      
//  }
  
  //params.put("senderDeviceRegID", senderRegID);
  
  String resp = "ERROR";
  
  
  //Log.d(CommonUtilities.TAG, "DbName - " + dbName);
   
  long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
  
  for (int i = 1; i <= MAX_ATTEMPTS; i++) {
      Log.e(TAG, "Attempt #" + i + " to send the message to group");
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
   


}