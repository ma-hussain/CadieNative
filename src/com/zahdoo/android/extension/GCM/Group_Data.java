package com.zahdoo.android.extension.GCM;

import java.util.UUID;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
 
public class Group_Data 
{
	private String SGID = "";
	private String myUserID = "";
	private String myEmailID = "";
    
    private SQLiteDatabase aDataBase;
    //private String aDB_PATH = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadieAlertDB.sqlite";
    private String aDB_PATH = "/data/data/air.com.zahdoo.cadie.debug/com.zahdoo.cadie.debug/Local Store/cadieAlertDB.sqlite";
    
   
    public Group_Data()
    {
    	//super("GroupMessageService");
    }


    //////////////////////////////////////////////////////
	//////////////////////////////////////////////////////
	///
	///  DOWNLOADING GROUP DATA
	///
	//////////////////////////////////////////////////////
    
    public String getGroupChatData( String userID, String email, String strSGID)
    {
    	myUserID = userID;
    	myEmailID = email;
    	SGID = strSGID;
    	
    	String msg = "";
 	 	try
		{
 	 		Log.d(CommonUtilities.TAG, "44 getGroupChatData " );
 	 		
			msg = GroupServerUtilities.getUserServerData( userID, email, "GET_GROUP_CHAT_DATA", strSGID);
		    
			Log.d(CommonUtilities.TAG, "48 getGroupChatData " + msg);
			
		    String[] vStrings = msg.split("\\^");
	    	
	    	if(vStrings[0].contentEquals("INSERT_CADIE_GROUP_CHAT_INFO"))
	    	{
	    		msg = CreateNewChatGroup_GsonParsing( vStrings[1] );
	    		
	    		if (msg.contentEquals("NEW_CHAT_GROUP_CREATED"))
	    			msg = getGroupUserData( myUserID, myEmailID, SGID );

	    		if( msg.contentEquals("ALL_USERS_INSERTED") )
	    			msg = getGroupMessages( myUserID, myEmailID, SGID );
	    		
	    		if ( msg.contentEquals("ALL_MESSAGES_INSERTED"))
	    		{
	    			if(aDataBase.isOpen())
	    				aDataBase.close();
	    			
	    			return "GROUP_DOWNLOAD_COMPLETED";
	    		}
	    		else
	    		{
	    			if(aDataBase.isOpen())
	    				aDataBase.close();
	    			
	    			return "ERROR";
	    		}
	    	}
	    	
	    	return "ERROR";
		}
 	 	catch (Exception ex)
		{
 	 		Log.d(CommonUtilities.TAG, "72 >>   " + ex.toString() );
			return "ERROR";
		}
    }
    
    private String CreateNewChatGroup_GsonParsing(String gsonStr)
    {
    	try 
    	{
            GsonBuilder gson = new GsonBuilder();
            Gson g = gson.create();    
        	
            GI grpList =  g.fromJson(gsonStr, GI.class);
        	
            Log.d(CommonUtilities.TAG, "85 CreateNewChatGroup_GsonParsing " + grpList.CadieGroupInfo.size());
            
        	aDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        	
        	String msg = "";
        	
        	for (int i = 0; i < grpList.CadieGroupInfo.size(); i++) 
        	{
        		GroupInfo arr = grpList.CadieGroupInfo.get(i);

        		if(arr != null)
        			msg = CreateNewChatGroup(arr);
        		
        		if( msg.contentEquals("ERROR") )
        			break;
			}
        	
        	if(aDataBase.isOpen())
    			aDataBase.close();
        	
        	if( msg.contentEquals("GROUP_CREATED") )
        		return "NEW_CHAT_GROUP_CREATED";
        	else
        		return "ERROR";
		} 
    	catch (Exception ex)
    	{
    		Log.d(CommonUtilities.TAG, "113 >>   " + ex.toString() );
    		return "ERROR";
		}
    }
    
    private String CreateNewChatGroup(GroupInfo gi)
   	{
    	
    	Log.d(CommonUtilities.TAG, "119 CreateNewChatGroup >> " + gi.SGID);
        
    	
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
					"MsgStatus		INTEGER, " +
			        
					"FileName   	VARCHAR(100), " +
					"FilePath   	VARCHAR(100), " +
					"FileSize   	NUMBER, " +
					"SharedOn     	VARCHAR(36));";
			aDataBase.execSQL(CREATE_GROUP_MESSAGE);
			
			if(aDataBase.isOpen())
				aDataBase.close();
			
			Log.e(CommonUtilities.TAG , "New Group table created >>  '" + gi.SGID + "'" );
			
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
    		
    		return "GROUP_CREATED";
   		}
       	catch (Exception ex)
       	{
       		Log.i(CommonUtilities.TAG, "182 >>  " + ex.toString() );
       		return "ERROR";
       	}
   	}
    

//////////
    

	//////////////////////////////////////////////////////
	//////////////////////////////////////////////////////
	///
	///  DOWNLOADING GROUP USERS
	///
	//////////////////////////////////////////////////////
    
	private String getGroupUserData( String userID, String email, String strSGID )
	{
		String msg = "";
		try
		{
			Log.d(CommonUtilities.TAG, "201 getGroupUserData ");
	        
			msg = GroupServerUtilities.getUserServerData( userID, email, "GET_GROUP_CHAT_USERS_DATA", strSGID );//code,emailid
		    
		    String[] vStrings = msg.split("\\^");
	    	
	    	if(vStrings[0].contentEquals("INSERT_CADIE_GROUP_USER")) 
	    	{
	    		msg = InsertGroupUsers_GsonParsing( vStrings[1] );
	    		
	    		if ( msg.contentEquals("USERS_INSERTED") )
	    			return "ALL_USERS_INSERTED";
	    		else
	    			return "ERROR";
	    	}
	    	
	    	return "ERROR";
		}
		catch (Exception ex){
			Log.d(CommonUtilities.TAG, "222  >>   " + ex.toString() );
			return "ERROR";
		}
	}

	private String InsertGroupUsers_GsonParsing(String gsonStr)
	{
		Log.d(CommonUtilities.TAG, "226 InsertGroupUsers_GsonParsing  " );
		
		try 
		{
	        GsonBuilder gson = new GsonBuilder();
	        Gson g = gson.create();    
	        
	        Log.i(CommonUtilities.TAG, "248 InsertGroupUsers_GsonParsing  " );
	    	
	        GUI grpUsrList =  g.fromJson(gsonStr, GUI.class);
	    	
	        Log.i(CommonUtilities.TAG, "251 InsertGroupUsers_GsonParsing  " );
	        
	        aDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
	    	
	    	String msg = "";
	    	
	    	for (int i = 0; i < grpUsrList.GrpUserInfo.size(); i++) 
	    	{
	    		GroupUserInfo arr = grpUsrList.GrpUserInfo.get(i);
	
	    		if(arr != null)
					msg = InsertGroupUsersData(arr);
	    		
	    		if( msg.contentEquals("ERROR"))
	    			break;
			}
	    	
	    	if(aDataBase.isOpen())
				aDataBase.close();
	
	    	if( msg.contentEquals("USER_INSERTED") )
	    		return "USERS_INSERTED";
	    	else
	    		return "ERROR";
		} 
		catch (Exception ex)
		{
			Log.i(CommonUtilities.TAG, "268 >> - " + ex.toString() );
			return "ERROR";
		}
	}

	private String tempStr = "";
	private String oldVal = "";
	
	private String InsertGroupUsersData(GroupUserInfo gi)
	{
		Log.d(CommonUtilities.TAG, "288 InsertGroupUsersData >> "  );
		
	   	try{
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
			
			if( tempStr.length() > 0 )
	   		{
	   			if( oldVal.contentEquals( gi.SGID ) )
	   			{}
	   			else{
	   				tempStr = tempStr + "," + gi.SGID;
	   			}
	   		}
	   		else
	   		{
	   			tempStr = gi.SGID;
	   		}
			
			oldVal = gi.SGID;
			
			return "USER_INSERTED";
		}
	   	catch (Exception ex)
	   	{
	   		Log.d(CommonUtilities.TAG, "315 >>> " + ex.getMessage() );
	   		return "ERROR";
	   	}
	}


    //////////////////////////////////////////////////////////

	
	//////////////////////////////////////////////////////
	//////////////////////////////////////////////////////
	///
	///  DOWNLOADING GROUP MESSAGES  
	///
	//////////////////////////////////////////////////////
	
	private String getGroupMessages( String userID, String email, String strSGID )
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
	    		
	    		if( msg.contentEquals("MESSAGES_INSERTED"))
	    			return "ALL_MESSAGES_INSERTED";
	    		else
	    			return "ERROR";
	    	}
	    	else if(vStrings[0].contentEquals("NO_GROUP_MESSAGE_FOUND")) 
	    	{
	    		return "ALL_MESSAGES_INSERTED";
	    	}
	    	
	    	return "ERROR";
		}
		catch (Exception ex){
							Log.d(CommonUtilities.TAG, "362 > " + ex.toString());
			return "ERROR";
		}
	}

	private String GroupMessage_GsonParsing(String gsonStr)
	{
		Log.i(CommonUtilities.TAG, "531 Gson Group Messages Parsing  " );
		
		try {
	        GsonBuilder gson = new GsonBuilder();
	        Gson g = gson.create();    
	        Log.i(CommonUtilities.TAG, "536 Gson Group Messages Parsing  " );
	    	
	        GMI grpMsg =  g.fromJson(gsonStr, GMI.class);
	    	
	    	Log.i(CommonUtilities.TAG, "540 Gson Group Messages Parsing  " );
	    	
	    	if(aDataBase.isOpen())
				aDataBase.close();
	    	
	    	aDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
	    	
	    	Log.i(CommonUtilities.TAG, "554 Gson Group Messages Parsing  " + grpMsg.GrpMsg.size() );
	    	
	    	String msg = "";
	    		
	    	for (int i = 0; i < grpMsg.GrpMsg.size(); i++) 
	    	{
	    		GroupMessageInfo arr = grpMsg.GrpMsg.get(i);
				//Insert new Result
				if(arr != null)
					InsertGroupMessages(arr);
				
				if( msg.contentEquals("ERROR") )
					break;
			}
	    	
	    	if(aDataBase.isOpen())
				aDataBase.close();
	    	
			Log.i(CommonUtilities.TAG, "583 Gson Group Messages Parsing  " );
			
			if(msg.contentEquals("MESSAGE_INSERTED"))
				return "MESSAGES_INSERTED";
			else
				return "ERROR";
		} 
		catch (Exception e)
		{
			Log.i(CommonUtilities.TAG, "412 >>>> " + e.toString() );
			return "ERROR";
		}
	}
	

	private String InsertGroupMessages(GroupMessageInfo gi)
	{
	   	try{
	   		//aDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
			
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

//			if( myEmailID.contentEquals( gi.EmailID ) )
//					values.put("ShareType", "SHARED");//gi.ShareType);
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
			
			return "MESSAGE_INSERTED";
		}
	   	catch (Exception e)
	   	{
	   		Log.i(CommonUtilities.TAG, "452 >>>> " + e.toString() );
	   		return "ERROR";
		}
	}
    //////////////////////////////////////////////////////////
	

	public void AddGroupMessage(GroupMessageData gm)
	{
	   	try{
	   		
	   		if(aDataBase != null)
	   			if( aDataBase.isOpen() )
	   				aDataBase.close();
	   		
	   		aDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
			
	   		ContentValues values = new ContentValues();
			values.put("GMID", UUID.randomUUID().toString());
			values.put("ServerGroupID", gm.ServerGroupID);
			values.put("SentByName", gm.ByName);
			values.put("SentByEmail", gm.ByEmail);
			
			values.put("Message", gm.Message);
			values.put("MessageType", gm.MessageType);
			
			values.put("NoteID", gm.NoteID);
			values.put("NoteTitle", gm.NoteTitle);
			values.put("DbName", gm.DBName);

//			if( myEmailID.contentEquals( gi.EmailID ) )
//					values.put("ShareType", "SHARED");//gi.ShareType);
//			else
//				values.put("ShareType", "RECEIVED");//gi.ShareType);
			
			values.put("ShareType", gm.ShareType);
			
			values.put("isViewed", false);
			values.put("isSynced", 0);
			values.put("isDeleted", false);
			values.put("isNoteDeleted", false);
			values.put("MsgStatus", 2);
			values.put("FileName", gm.FileName);
			values.put("FilePath", gm.FilePath);
			values.put("FileSize", gm.FileSize);
			values.put("SharedOn", gm.Time);
			
			Log.i(CommonUtilities.TAG, "INSERTING >>>> to GRP_" + gm.ServerGroupID );
			
			aDataBase.insert("GRP_" + gm.ServerGroupID, null, values);
			values.clear();
			
			
			Log.i(CommonUtilities.TAG, "INSERTED TO >>>> GRO_" + gm.ServerGroupID);
			
			//return "MESSAGE_INSERTED";
		}
	   	catch (Exception e)
	   	{
	   		Log.i(CommonUtilities.TAG, "452 >>>> " + e.toString() );
	   		//return "ERROR";
		}
	}
}