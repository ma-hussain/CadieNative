package com.zahdoo.android.extension.GCM;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
 
public class GroupMessageService extends IntentService
{
	private ConnectionDetector cd;
    
	private String actionType;
	private String grpTitle;
	private String byUserID;
    private String byEmail;
    private String byName;
    private String byRegID;
    private String friendsID;
    private String friendsEmail;
    private String friendsName;
    private String grpProfilePic;
    
     
    private String message;
    private String messageType;
    private String shareType;
    private String txtToSend;
    private String serverGroupID;
    private String gmID;
    
    private String noteID;
    private String noteTitle;
    private String dbName;
    
    private String fileTitle;
    private String fileName;
    private String filePath;
    private String fileSize;
    private String serverFileID;
    
    //private ArrayList<String> arrSGID = new ArrayList<String>();
    
    private SQLiteDatabase aDataBase;
    //private String aDB_PATH = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadieAlertDB.sqlite";
    private String aDB_PATH = "/data/data/air.com.zahdoo.cadie.debug/com.zahdoo.cadie.debug/Local Store/cadieAlertDB.sqlite";
    
    //private Context context;
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
	}
    
    public GroupMessageService()
    {
    	super("GroupMessageService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
    	cd = new ConnectionDetector(getApplicationContext());
    	 
        // Check if Internet present
    	try {
   		 if (!cd.isConnectingToInternet()) {
   	            // Internet Connection is not present
   	        	GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION");
   	        	stopSelf();
   	            return;
   	        }
   	    } catch (Exception e) {
			Log.d(CommonUtilities.TAG, "2 Internet Connection Error... -  "  + e.toString());
		}
      
    	int itest = 0;
        try {
        	Log.d(CommonUtilities.TAG, "2 Inside Group Message Service ... " );
        	
			Bundle extras = intent.getExtras();
			itest  = 1;
		    actionType 			= extras.getString("actionType");
			grpTitle 			= extras.getString("grpTitle");
			grpProfilePic		= extras.getString("grpProfilePic");
			byUserID 			= extras.getString("byUserID");
			byEmail 			= extras.getString("byEmail");
			byName 				= extras.getString("byName");
			byRegID				= extras.getString("byRegID");
			friendsID 			= extras.getString("friendsID");
			friendsEmail 		= extras.getString("friendsEmail");
			friendsName			= extras.getString("friendsName");
			
			message				= extras.getString("message");
			messageType			= extras.getString("messageType");
			shareType			= extras.getString("shareType");
			noteID				= extras.getString("noteID");
			noteTitle			= extras.getString("noteTitle");
			dbName				= extras.getString("dbName");
			
			fileTitle 			= extras.getString("fileTitle");
			
			txtToSend			= extras.getString("txtToSend");
			serverGroupID		= extras.getString("serverGroupID");
			gmID				= extras.getString("gmID");
			
			fileName			= extras.getString("fileName");
			filePath			= extras.getString("filePath");
			fileSize			= extras.getString("fileSize");
			
			serverFileID 		= extras.getString("serverFileID");
			itest  = 2;
			//context = getApplicationContext();
			
			Log.e(CommonUtilities.TAG, "actionType -  " + actionType );
			
			itest = 3;
			
			if( actionType.contentEquals("SEND_GROUP_MESSAGE") )
			{
				if(txtToSend.trim().length() > 0 )
				{
					Log.e(CommonUtilities.TAG, "SENDIG Message to group ... " + serverGroupID + " gmID >> '" + gmID + "'" );
					Log.e(CommonUtilities.TAG, "SENDIG Message to group ... " + serverGroupID + " gmID >> '" + gmID + "'" );
					
					String msg = "";
					msg = GroupServerUtilities.sendMessage(serverGroupID, gmID, grpTitle, byUserID, byEmail, byName, byRegID, 
							txtToSend, messageType, shareType, noteID, noteTitle, dbName, fileName, filePath, fileSize);
					
					Log.e(CommonUtilities.TAG, "RESPONSE === ... " + msg );
					
					
					String[] vStrings = msg.split("\\^");
					
					if(msg.contentEquals(""))
						GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
					else if(vStrings[0].contentEquals("GROUP_MESSAGE_SENT"))
						GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "MESSAGE_SENT^" + vStrings[1] +"^"+ vStrings[2]);
					else if(vStrings[0].contentEquals("GROUP_FILE_MESSAGE_SENT"))
						GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "FILE_MESSAGE_SENT^" + vStrings[1] +"^"+ vStrings[2]);
					
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
			else if( actionType.contentEquals("CREATE_GROUP"))
            {
				createGroup();
            }
			else if( actionType.contentEquals("GET_GROUP_CHAT_USERS_DATA"))
            {
				getGroupUserData( byUserID, byEmail );
            }
			else if( actionType.contentEquals("GET_GROUP_CHAT_DATA"))
			{
				getGroupChatData( byUserID, byEmail );
			}
			else if( actionType.contentEquals("EXIT_GROUP") ){
				 removeUserFromGroup();
			}
			else if( messageType.contentEquals("GROUP_PICTURE") || messageType.contentEquals("GROUP_VIDEO")
				     || messageType.contentEquals("GROUP_VOICE") || messageType.contentEquals("GROUP_FILE")
				     ||  messageType.contentEquals("GROUP_EXPORTED_PDF") )
			{
				String msg = "";
				//@PARAMS  ServerFileId, sendermailID,SenderName, ReceiverMailId, senderuserID, Title(At present blank),fileName,filetype,fileSize,senderRegID,toName
			    msg = GroupServerUtilities.sendGroupFiles(serverGroupID, gmID, grpTitle, 
			    									byUserID, byEmail, byName, byRegID, 
			    									serverFileID, messageType, shareType,
			    									noteID, noteTitle, dbName, 
			    									fileTitle, fileName, filePath, fileSize);
			    
			    GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "MESSAGE_SENT^" + msg);
				stopSelf();
			}
			else if( actionType.contentEquals("SEND_GROUP_MESSAGE") )
			{
				if(txtToSend.trim().length() > 0 )
				{
					Log.e(CommonUtilities.TAG, "SENDIG Message to group ... " + serverGroupID + " gmID >> '" + gmID + "'" );
					Log.e(CommonUtilities.TAG, "SENDIG Message to group ... " + serverGroupID + " gmID >> '" + gmID + "'" );
					
					String msg = "";
					msg = GroupServerUtilities.sendMessage(serverGroupID, gmID, grpTitle, byUserID, byEmail, byName, byRegID, 
							txtToSend, messageType, shareType, noteID, noteTitle, dbName, fileName, filePath, fileSize);
					
					Log.e(CommonUtilities.TAG, "RESPONSE === ... " + msg );
					
					
					String[] vStrings = msg.split("\\^");
					
					if(msg.contentEquals(""))
						GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
					else if(vStrings[0].contentEquals("GROUP_MESSAGE_SENT"))
						GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "MESSAGE_SENT^" + vStrings[1] +"^"+ vStrings[2]);
					else if(vStrings[0].contentEquals("GROUP_FILE_MESSAGE_SENT"))
						GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "FILE_MESSAGE_SENT^" + vStrings[1] +"^"+ vStrings[2]);
					
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
		} catch (Exception e) {
			Log.d(CommonUtilities.TAG , "Message Exception  - " + e.toString() + "  " + itest);
			//SendMessageFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR " + e.getMessage());
			stopSelf();
		}
    }
    

	//////////////////////////////////////////////////////
	//////////////////////////////////////////////////////
	///
	///  CREATING NEW GROUP
	///
	/////////////////////////////////////////////////////
    
    /////////////////////////
    private void createGroup()
    {
    	String msg = "";
		try
		{
			Log.d(CommonUtilities.TAG, "1 Create New Group " );
		    msg = GroupServerUtilities.createNewGroup(grpTitle, grpProfilePic, byUserID, byEmail, byName, byRegID, friendsID, friendsEmail, friendsName);
		    Log.d(CommonUtilities.TAG, "2 Create New Group" );
		}
		catch (Exception ex)
		{
			Log.d(CommonUtilities.TAG, "3 Create New Group EXCEPTION: " + ex.toString());
		    msg = ex.toString();
		}
		
		String[] vStrings = msg.split("\\^");
    	
		if(msg.contentEquals(""))
			GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
		else if(vStrings[0].contentEquals("GROUP_CREATED"))
		{
    		GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", msg );
    	}
    	else
    	{
    		GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "GROUP_NOT_CREATED_ERROR" + msg );
    	}
    		
		stopSelf();
    }
    
    //////////////////////////////////////////////////////
	//////////////////////////////////////////////////////
	///
	///  DOWNLOADING GROUP DATA
	///
	//////////////////////////////////////////////////////
    
    private void getGroupChatData( String userID, String email)
    {
    	String msg = "";
 	 	try
		{
			//Log.d(CommonUtilities.TAG, "148 USER Group info DATA " );
			
		    msg = GroupServerUtilities.getUserServerData( userID, email, "GET_GROUP_CHAT_DATA", "");//code,emailid
		    
		   // Log.d(CommonUtilities.TAG, "152 USER Group info DATA "  + msg);
		    
		    String[] vStrings = msg.split("\\^");
	    	
	    	if(vStrings[0].contentEquals("INSERT_CADIE_GROUP_CHAT_INFO"))
	    	{
	    		//Log.i(CommonUtilities.TAG, "158 Gson Group info Parsing - " + vStrings[1]);
	    		
	    		msg = GroupChatInfo_GsonParsing( vStrings[1] );
	    		
	    		GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "INSERTED_CADIE_GROUP_CHAT_INFO" );
	    	}
	    	else if(vStrings[0].contentEquals("NO_GROUP_CHAT_INFO_FOUND"))
	    	{
	    		GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "NO_GROUP_CHAT_INFO_FOUND" );
	    	}
		}catch (Exception ex){
			Log.d(CommonUtilities.TAG, "165 USER Group info DATA EXCEPTION: " + ex.toString());
		    msg = ex.toString();
		    
		    GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "ERROR" );
		}
    		
 	   stopSelf();
    }
    
    private String GroupChatInfo_GsonParsing(String gsonStr)
    {
    	try {
            GsonBuilder gson = new GsonBuilder();
            Gson g = gson.create();    
        	
        	D grpList =  g.fromJson(gsonStr, D.class);
        	
        	aDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
             
        	for (int i = 0; i < grpList.CadieGroupInfo.size(); i++) 
        	{
        		GroupInfo arr = grpList.CadieGroupInfo.get(i);

        		if(arr != null)
					InsertGroupChatData(arr);
			}
		} 
    	catch (Exception e)
    	{
			Log.i(CommonUtilities.TAG, "ERROR Gson Group CHAT INFO Parsing Exception - " + e.toString() );
		}
    	finally
    	{
    		if(aDataBase.isOpen())
    			aDataBase.close();
    		
    		return "INSERT_CADIE_GROUP_CHAT_INFO_COMPLETED";
		}
    }
    
    private void InsertGroupChatData(GroupInfo gi)
   	{
       	try{
       		String CREATE_GROUP_MESSAGE = "CREATE TABLE IF NOT EXISTS GRP_"
			        + gi.SGID + " (GMID PRIMARY KEY NOT NULL, " +
			        "ServerGroupID  INTEGER, " +
			        "SentByName 	VARCHAR(100), " +
			        "SentByEmail 	VARCHAR(100), " +
			        
			        "Message 		TEXT," + 
			        "MessageType 	VARCHAR(10), " +
			        "ShareType 		VARCHAR(10), " +
			        "NoteID 		VARCHAR(100), " +
			        "NoteTitle     	TEXT, " +
			        "DbName       	VARCHAR(255), " +
			        
					"isViewed    	BOOLEAN, " +
			        "isSynced    	INTEGER, " +
			        "isDeleted    	BOOLEAN, " +
					"isNoteDeleted  BOOLEAN, " +
					"MsgStatus  	INTEGER, " +
			        
					"FileName   	VARCHAR(100), " +
					"FilePath   	VARCHAR(100), " +
					"FileSize   	NUMBER, " +
					"SharedOn     	VARCHAR(36));";
			aDataBase.execSQL(CREATE_GROUP_MESSAGE);
			
			if(aDataBase.isOpen())
				aDataBase.close();
			
			//Toast.makeText( getApplicationContext() , "AFTER", Toast.LENGTH_SHORT).show();
			
    		//////////////////////////////////////////////////////
    		
			aDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
			
			ContentValues values = new ContentValues();
    		values.put("GroupID", UUID.randomUUID().toString());
    		values.put("ServerGroupID", gi.SGID);
    		values.put("GroupTitle", gi.GroupTitle);
    		
    		values.put("GroupType", gi.GroupType);
    		values.put("ProfilePic", gi.ProfilePic);
    		values.put("CreatedByID", gi.CreatedByID);
    		values.put("CreatedByName", gi.CreatedByName);
    		
    		values.put("LastMessage", gi.LastMessage);
    		values.put("LastMessageType", gi.LastMessageType);
    		values.put("LastMessageOn", gi.LastMessageOn);
    		values.put("LastMessageBy", gi.LastMessageBy);
    		
    		values.put("CreatedOn", gi.CreatedOn);
    		values.put("isSynced", false);
    		values.put("ShareType", gi.ShareType);
    		values.put("NewMsgCount", 0);
    		
    		aDataBase.insert("BroadcastGroup", null, values);
    		values.clear();
			
       		//c = null;
   		}
       	catch (Exception e)
       	{
       		Log.i(CommonUtilities.TAG, "1 ERROR Gson PARSING - " + e.toString() );
   		}
   	}
    
    class D {
    	
        List<GroupInfo> CadieGroupInfo;
    }
    
    class GroupInfo {
        @SerializedName("SGID")
        public String SGID;

        @SerializedName("GROUP_TITLE")
        public String GroupTitle;
        
        @SerializedName("GROUP_TYPE")
        public String GroupType;
        
        @SerializedName("PROFILE_PIC")
        public String ProfilePic;
        
        @SerializedName("CREATED_BY_ID")
        public String CreatedByID;
        
        @SerializedName("CREATED_BY_NAME")
        public String CreatedByName;
        
        @SerializedName("LAST_MESSAGE")
        public String LastMessage;
        
        @SerializedName("LAST_MESSAGE_TYPE")
        public String LastMessageType;
        
        @SerializedName("LAST_MESSAGE_ON")
        public String LastMessageOn;
        
        @SerializedName("LAST_MESSAGE_BY")
        public String LastMessageBy;
        
        @SerializedName("CREATED_ON")
        public String CreatedOn;
        
        @SerializedName("SHARE_TYPE")
        public String ShareType;
    }

    
//////////
    

	//////////////////////////////////////////////////////
	//////////////////////////////////////////////////////
	///
	///  DOWNLOADING GROUP USERS
	///
	//////////////////////////////////////////////////////
    
private void getGroupUserData( String userID, String email)
{
	String msg = "";
	 	try
	{
		msg = GroupServerUtilities.getUserServerData( userID, email, "GET_GROUP_CHAT_USERS_DATA", "");//code,emailid
	    
	    String[] vStrings = msg.split("\\^");
    	
    	if(vStrings[0].contentEquals("INSERT_CADIE_GROUP_USER")) 
    	{
    		msg = GroupUserInfo_GsonParsing( vStrings[1] );
    		
    		//GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "INSERTED_CADIE_GROUP_USER" ); 
    	}
	}catch (Exception ex){
		Log.d(CommonUtilities.TAG, "374 USER Group USER info DATA EXCEPTION: " + ex.toString());
	    msg = ex.toString();
	    
	   // GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "ERROR" );
	}
		
	stopSelf();
}

private String GroupUserInfo_GsonParsing(String gsonStr)
{
	Log.i(CommonUtilities.TAG, "386 Gson Group USER INFO Parsing  " );
	
	try {
        GsonBuilder gson = new GsonBuilder();
        Gson g = gson.create();    
        
        //Log.i(CommonUtilities.TAG, "390 Gson Group USER INFO Parsing  " );
    	
        D1 grpUsrList =  g.fromJson(gsonStr, D1.class);
    	
    	//Log.i(CommonUtilities.TAG, "394 Gson Group USER INFO Parsing  " );
    	
    	aDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    	
    	//Log.i(CommonUtilities.TAG, "400 Gson Group USER INFO Parsing  " + grpUsrList.GrpUserInfo.size() );
    	 
    	for (int i = 0; i < grpUsrList.GrpUserInfo.size(); i++) 
    	{
    		GroupUserInfo arr = grpUsrList.GrpUserInfo.get(i);

    		if(arr != null)
				InsertGroupUserData(arr);
		}
    	
					Log.i(CommonUtilities.TAG, "389 Gson Group USER INFO Parsing  tempStr === " + tempStr);
		
		//String[] arr = (String[]) arrSGID.toArray();
		//String[] arr = arrSGID.toArray(new String[arrSGID.size()]);
		
					//Log.i(CommonUtilities.TAG, "394 Gson arrSGID.toArray().toString() >>  " + Arrays.toString( arr ) );
		
					//String strSGID = Arrays.toString( arr ); //removeDuplicates( arr );
					
		getGroupMessages( byUserID, byEmail, tempStr);// Arrays.toString( arr ) );
		
	} 
	catch (Exception e)
	{
		Log.i(CommonUtilities.TAG, "ERROR Gson Group USER INFO Parsing Exception - " + e.toString() );
	}
	finally
	{
		if(aDataBase.isOpen())
			aDataBase.close();
		
		return "INSERT_CADIE_GROUP_CHAT_INFO_COMPLETED";
	}
}

//private int x = 0;
//private String[] arrTemp1;
private String tempStr = "";
private String oldVal = "";

private void InsertGroupUserData(GroupUserInfo gi)
{
   	try{
   		aDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
		
   		//arrSGID.add( gi.SGID );
   		
   		ContentValues values = new ContentValues();
		values.put("GroupUserID", UUID.randomUUID().toString());
		values.put("GUID", gi.GUID);
		values.put("ServerGroupID", gi.SGID);
		
		values.put("UserID", gi.UserID);
		values.put("Name", gi.UserName);
		values.put("EmailID", gi.UserEmailID);
		values.put("IsAdmin", gi.IsAdmin);
		
		values.put("IsActive", gi.IsActive);
		values.put("AddedOn", gi.AddedOn);
		values.put("RemovedOn", gi.RemovedOn);
		
		aDataBase.insert("GroupUser", null, values);
		values.clear();
		
		
		
   		if( tempStr.length() > 0 ){
   			
   			if( oldVal.contentEquals( gi.SGID ) )
   			{}
   			else{
//   				arrTemp1[x] = gi.SGID;
//   				x++;
   				
   				tempStr = tempStr + "," + gi.SGID;
   			}
   		}
   		else
   		{
//   			arrTemp1[x] = gi.SGID;
//   			x++;
   			
   			tempStr = gi.SGID;
   		}
		
		oldVal = gi.SGID;
	}
   	catch (Exception e)
   	{
   		Log.i(CommonUtilities.TAG, "457 ERROR Gson GROUP USR PARSING - " + e.getMessage()  );
		}
	}


    //////////////////////////////////////////////////////////

	class D1 {
		
	    List<GroupUserInfo>GrpUserInfo;
	}
	
	class GroupUserInfo {
		@SerializedName("GUID")
	    public String GUID;
		
	    @SerializedName("SGID")
	    public String SGID;
	
	    @SerializedName("USER_ID")
	    public String UserID;
	    
	    @SerializedName("USER_NAME")
	    public String UserName;
	    
	    @SerializedName("USER_EMAILID")
	    public String UserEmailID;
	    
	    @SerializedName("IS_ADMIN")
	    public String IsAdmin;
	    
	    @SerializedName("IS_ACTIVE")
	    public String IsActive;
	    
	    @SerializedName("ADDED_ON")
	    public String AddedOn;
	    
	    @SerializedName("REMOVED_ON")
	    public String RemovedOn;
	    
	    @SerializedName("LAST_MESSAGE")
	    public String LastMessage;
	    
	    @SerializedName("LAST_MESSAGE_TYPE")
	    public String LastMessageType;
	    
	    @SerializedName("LAST_MESSAGE_ON")
	    public String LastMessageOn;
	    
	    @SerializedName("LAST_MESSAGE_BY")
	    public String LastMessageBy;
	    
	    @SerializedName("CREATED_ON")
	    public String CreatedOn;
	    
	    @SerializedName("SHARE_TYPE")
	    public String ShareType;
	}
 
	//////////////////////////////////////////////////////
	//////////////////////////////////////////////////////
	///
	///  DOWNLOADING GROUP MESSAGES  
	///
	//////////////////////////////////////////////////////
	
	private void getGroupMessages( String userID, String email, String strSGID )
	{
		String msg = "";
		
		try
		{
			Log.d(CommonUtilities.TAG, "514 USER Group Messages " + strSGID);
			
							Log.d(CommonUtilities.TAG, "515 USER Group Messages " + strSGID);
			
		    msg = GroupServerUtilities.getUserServerData( userID, email, "GET_GROUP_CHAT_MESSAGES", strSGID);//code,emailid
		    
		    				Log.d(CommonUtilities.TAG, "520 USER Group Messages " + msg );
		    
		    String[] vStrings = msg.split("\\^");
	    	
	    	if(vStrings[0].contentEquals("INSERT_GROUP_CHAT_MESSAGES")) 
	    	{
	    					Log.i(CommonUtilities.TAG, "526 Gson Group Messages Parsing - " + vStrings[1]);
	    		
	    		msg = GroupMessage_GsonParsing( vStrings[1] );
	    		GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "INSERTED_CADIE_GROUP_USER" ); 
	    	}
		}catch (Exception ex){
							Log.d(CommonUtilities.TAG, "533 USER Group USER info DATA EXCEPTION: " + ex.toString());
		    msg = ex.toString();
		    
		    GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "INSERTED_CADIE_GROUP_USER" );
		}
			
		stopSelf();
	}

	private String GroupMessage_GsonParsing(String gsonStr)
	{
		Log.i(CommonUtilities.TAG, "531 Gson Group Messages Parsing  " );
		
		try {
	        GsonBuilder gson = new GsonBuilder();
	        Gson g = gson.create();    
	        Log.i(CommonUtilities.TAG, "536 Gson Group Messages Parsing  " );
	    	
	        D2 grpMsg =  g.fromJson(gsonStr, D2.class);
	    	
	    	Log.i(CommonUtilities.TAG, "540 Gson Group Messages Parsing  " );
	    	
	    	if(aDataBase.isOpen())
				aDataBase.close();
	    	
	    	aDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
	    	
	    	Log.i(CommonUtilities.TAG, "554 Gson Group Messages Parsing  " + grpMsg.GrpMsg.size() );
	    	
	    	for (int i = 0; i < grpMsg.GrpMsg.size(); i++) 
	    	{
	    		GroupMessage arr = grpMsg.GrpMsg.get(i);
				//Insert new Result
				if(arr != null)
					InsertGroupMessages(arr);
			}
	    	
			Log.i(CommonUtilities.TAG, "583 Gson Group Messages Parsing  " );
		} 
		catch (Exception e)
		{
			Log.i(CommonUtilities.TAG, "ERROR Gson Group Messages Parsing Exception - " + e.toString() );
		}
		finally
		{
			if(aDataBase.isOpen())
				aDataBase.close();
			
			return "INSERT_CADIE_GROUP_CHAT_MESSAGE";
		}
	}
	

	private void InsertGroupMessages(GroupMessage gi)
	{
	   	try{
	   		aDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
			
	   		ContentValues values = new ContentValues();
			values.put("GMID", UUID.randomUUID().toString());
			values.put("ServerGroupID", gi.SGID);
			values.put("SentByName", gi.Name);
			values.put("SentByEmail", gi.EmailID);
			
			values.put("Message", gi.Msg);
			values.put("MessageType", gi.MsgType);
			
			values.put("NoteID", gi.NoteID);
			values.put("NoteTitle", gi.NoteTitle);
			values.put("DbName", gi.DBName);

//			if( byEmail.contentEquals( gi.EmailID ) )
//				values.put("ShareType", "SHARED");//gi.ShareType);
//			else
//				values.put("ShareType", "RECEIVED");//gi.ShareType);
		
			values.put("ShareType", gi.ShareType);
			
			values.put("isViewed", false);
			values.put("isSynced", 0);
			values.put("isDeleted", false);
			values.put("isNoteDeleted", false);
			values.put("MsgStatus", 2);
			values.put("FileName", gi.FileName);
			values.put("FilePath", gi.FilePath);
			values.put("FileSize", gi.FileSize);
			values.put("SharedOn", gi.SharedOn);
			
			aDataBase.insert("GRP_" + gi.SGID, null, values);
			values.clear();
		}
	   	catch (Exception e)
	   	{
	   		Log.i(CommonUtilities.TAG, "631 ERROR Gson GROUP Message PARSING - " + e.toString() );
		}
	}


    //////////////////////////////////////////////////////////

	class D2 {
		
	    List<GroupMessage>GrpMsg;
	}
	
	class GroupMessage {
		@SerializedName("GMID")
	    public String GMID;
		
	    @SerializedName("SGID")
	    public String SGID;
	
	    @SerializedName("SENT_BY_EMAIL_ID")
	    public String EmailID;
	    
	    @SerializedName("SENT_BY_NAME")
	    public String Name;
	    
	    @SerializedName("MESSAGE")
	    public String Msg;
	    
	    @SerializedName("MESSAGE_TYPE")
	    public String MsgType;
	    
	    @SerializedName("SHARE_TYPE")
	    public String ShareType;
	    
	    @SerializedName("NOTE_ID")
	    public String NoteID;
	    
	    @SerializedName("NOTE_TITLE")
	    public String NoteTitle;
	    
	    @SerializedName("DB_NAME")
	    public String DBName;
	    
	    @SerializedName("FILE_NAME")
	    public String FileName;
	    
	    @SerializedName("FILE_PATH")
	    public String FilePath;
	    
	    @SerializedName("FILE_SIZE")
	    public String FileSize;
	    
	    @SerializedName("SHARED_ON")
	    public String SharedOn;
	}
	
	
	/// Remove user from group
	private void removeUserFromGroup()
    {
		Log.d(CommonUtilities.TAG, "1 REMOVE USER FROM GROUP " );
		
		String msg = "";
		try
		{
			Log.d(CommonUtilities.TAG, "2 REMOVE USER FROM GROUP " );
		    msg = GroupServerUtilities.removeUserFromGroup( "REMOVE_USER_FROM_GROUP", byUserID, byEmail, byName,  byRegID, serverGroupID , grpTitle);
		    
		}
		catch (Exception ex)
		{
			Log.d(CommonUtilities.TAG, "4 REMOVE USER FROM GROUP: " + ex.toString());
		    msg = ex.toString();
		}
    	Log.i(CommonUtilities.TAG, "AFTER REMOVE USER FROM GROUP Msg - " + msg);
    	
    	try{
	    	if(msg.contentEquals(""))
	    		GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
			else if(msg.contentEquals("EXIT_GROUP_SUCCESSFUL"))
				GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "EXIT_GROUP_SUCCESSFUL");
	    	else if(msg.contains("SERVICE_NOT_AVAILABLE"))
	    		GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION" );
	    	else
	    	{
	    		GCMInitFunction.cGCM.dispatchStatusEventAsync("REGISTERED", "ERROR" );
	    	}
    	} catch (Exception e) {
	    	Log.i(CommonUtilities.TAG, "AFTER REMOVE USER FROM GROUP Exc = " + e.toString());
	    }
    	stopSelf();
    }
}