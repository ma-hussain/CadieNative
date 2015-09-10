package com.zahdoo.android.extension.GCM;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.zahdoo.android.extension.CadieGCMExtensionContext;

public class GroupMessageFunction implements FREFunction {

	public static CadieGCMExtensionContext gcmCon;
	
	@Override
	public FREObject call(FREContext context, FREObject[] passedArgs) {
		FREObject result = null; 
		gcmCon = (CadieGCMExtensionContext)context;
		
		try {
			   FREObject fro = passedArgs[0];
			   String[] vStrings = fro.getAsString().split("\\^");
		
			   Log.d(CommonUtilities.TAG, "G M S: " + fro.getAsString());
			   Intent sInt = new Intent(gcmCon.getActivity(), GroupMessageService.class);
			   sInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			   
			   if(vStrings[0].contentEquals("UPDATE_GROUP_TABLE"))
			   {
				   try {
					   Log.e(CommonUtilities.TAG, "=== UPDATE TABLE === " );
					   
					   SQLiteDatabase aDataBase;
					   //String aDB_PATH = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadieAlertDB.sqlite";
					   String aDB_PATH = "/data/data/air.com.zahdoo.cadie.debug/com.zahdoo.cadie.debug/Local Store/cadieAlertDB.sqlite";
					   aDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
						
					   ContentValues values = new ContentValues();
					   values.put("isViewed", true); //These Fields should be your String values of actual column names
//					   String where = "1 = ?";  
//					   final String[] whereArgs = { null };
						
					   aDataBase.update("GRP_" + vStrings[1], values, "1=1", null);
					   
					   Log.e(CommonUtilities.TAG, "=== UPDATING GROUP TABLE === " );
				   }
				   catch (Exception e) {
					   Log.e(CommonUtilities.TAG, "UPDATE_GROUP_TABLE >> " + e.toString() );
					   Log.e(CommonUtilities.TAG, "UPDATE_GROUP_TABLE >> " + e.toString() );
						
				   }
			   }
			   else if( vStrings[0].contentEquals("CREATE_GROUP") )
			   {

				   Log.e(CommonUtilities.TAG, "1 : GMID ");
				   Log.e(CommonUtilities.TAG, "1 : GMID ");
				   
				   sInt.putExtra("actionType"	, vStrings[0]);
				   sInt.putExtra("grpTitle"		, vStrings[1]);
				   sInt.putExtra("grpProfilePic", vStrings[2]);
				   sInt.putExtra("byUserID"		, vStrings[3]);
				   sInt.putExtra("byEmail"		, vStrings[4]);
				   sInt.putExtra("byName"		, vStrings[5]);
				   sInt.putExtra("byRegID"		, vStrings[6]);
				   sInt.putExtra("friendsID"	, vStrings[7]);
				   sInt.putExtra("friendsEmail"	, vStrings[8]);
				   sInt.putExtra("friendsName"	, vStrings[9]);
				   sInt.putExtra("shareID"		, "");
				   sInt.putExtra("serverGroupID", "");
				   
				   sInt.putExtra("fileTitle", "");
				   sInt.putExtra("gmID"			, "");
				   sInt.putExtra("fileName"		, "");
				   sInt.putExtra("fileSize"		, "");
				   sInt.putExtra("serverFileID"	, "");
			   }
			   else if( vStrings[0].contentEquals("GET_GROUP_CHAT_DATA") ||
					   vStrings[0].contentEquals("GET_GROUP_CHAT_USERS_DATA") )
			   {

				   Log.e(CommonUtilities.TAG, "2 : GMID ");
				   Log.e(CommonUtilities.TAG, "2 : GMID ");
				
				   sInt.putExtra("actionType"	, vStrings[0]);
				   sInt.putExtra("grpTitle"		, "");
				   sInt.putExtra("grpProfilePic", "");
				   sInt.putExtra("byUserID"		, vStrings[1]);
				   sInt.putExtra("byEmail"		, vStrings[2]);
				   sInt.putExtra("byName"		, vStrings[3]);
				   sInt.putExtra("byRegID"		, vStrings[4]);
				   sInt.putExtra("friendsID"	, "");
				   sInt.putExtra("friendsEmail"	, "");
				   sInt.putExtra("friendsName"	, "");
				   sInt.putExtra("shareID"		, "");
				   sInt.putExtra("serverGroupID", "");
				   
				   sInt.putExtra("fileTitle", "");
				   sInt.putExtra("gmID"			, "");
				   sInt.putExtra("fileName"		, "");
				   sInt.putExtra("fileSize"		, "");
				   sInt.putExtra("serverFileID"	, "");
			   }
			   else if( vStrings[0].contentEquals("SEND_GROUP_MESSAGE") )
			   {
				   Log.e(CommonUtilities.TAG, "3 : GMID ");
				   Log.e(CommonUtilities.TAG, "3 : GMID ");
				   
				   sInt.putExtra("actionType"	, vStrings[0]);
				   sInt.putExtra("serverGroupID", vStrings[1]);
				   sInt.putExtra("txtToSend"	, vStrings[2]);
				   sInt.putExtra("byUserID"		, vStrings[3]);
				   sInt.putExtra("byEmail"		, vStrings[4]);
				   sInt.putExtra("byName"		, vStrings[5]);
				   sInt.putExtra("byRegID"		, vStrings[6]);
				   sInt.putExtra("messageType"	, vStrings[7]);
				   sInt.putExtra("shareType"	, vStrings[8]);
				   
				   // Note ID
				   if(vStrings[9].contentEquals("NOTE_ID"))
					   sInt.putExtra("noteID"		, "");
				   else
					   sInt.putExtra("noteID"		, vStrings[9]);
				   
				   // Note Title
				   if(vStrings[10].contentEquals("NOTE_TITLE"))
					   sInt.putExtra("noteTitle"		, "");
				   else
					   sInt.putExtra("noteTitle"		, vStrings[10]);
				   
				   // DB Name
				   if(vStrings[11].contentEquals("DB_NAME"))
					   sInt.putExtra("dbName"		, "");
				   else
					   sInt.putExtra("dbName"		, vStrings[11]);
				   
				   // File Name
				   if(vStrings[12].contentEquals("FILE_NAME"))
					   sInt.putExtra("fileName"		, "");
				   else
					   sInt.putExtra("fileName"		, vStrings[12]);

				// File Size
				   if(vStrings[13].contentEquals("FILE_PATH"))
					   sInt.putExtra("filePath"		, "");
				   else
					   sInt.putExtra("filePath"		, vStrings[13]);
				   
				   // File Size
				   if(vStrings[14].contentEquals("FILE_SIZE"))
					   sInt.putExtra("fileSize"		, "");
				   else
					   sInt.putExtra("fileSize"		, vStrings[14]);
				   
				// Title
				   if(vStrings[15].contentEquals("GROUP_TITLE"))
					   sInt.putExtra("grpTitle"		, " grp titleeeee ");
				   else
					   sInt.putExtra("grpTitle"		, vStrings[15]);
				   
				   Log.e(CommonUtilities.TAG, "G M S: GMID >> " + vStrings[16]);
				   Log.e(CommonUtilities.TAG, "G M S: GMID >> " + vStrings[16]);
				   
				   sInt.putExtra("gmID"		, vStrings[16]);
				   
				   sInt.putExtra("fileTitle", "");
				   sInt.putExtra("grpProfilePic", "");
				   sInt.putExtra("friendsID"	, "");
				   sInt.putExtra("friendsEmail"	, "");
				   sInt.putExtra("friendsName"	, "");
				   sInt.putExtra("serverFileID"	, "");
			   }
			   else if( vStrings[0].contentEquals("SEND_GROUP_FILES") )
			   {
				   sInt.putExtra("actionType"	, vStrings[0]); //"notificationType", vStrings[8]); // GROUP_VIDEO
				   sInt.putExtra("serverGroupID", vStrings[1]);
				   sInt.putExtra("byUserID"		, vStrings[2]);
				   sInt.putExtra("byEmail"		, vStrings[3]);
				   sInt.putExtra("byName"		, vStrings[4]);
				   sInt.putExtra("byRegID"		, vStrings[5]);
				   sInt.putExtra("messageType"	, vStrings[6]); // Type to find out the action
				   sInt.putExtra("shareType"	, vStrings[7]);
				   sInt.putExtra("noteID"		, vStrings[8]);
				   sInt.putExtra("noteTitle"	, vStrings[9]);
				   
				   sInt.putExtra("dbName"		, vStrings[10]); /// FileTitle
				   sInt.putExtra("fileTitle"	, vStrings[10]); /// FileTitle
				   
				   sInt.putExtra("fileName"		, vStrings[11]);
				   sInt.putExtra("filePath"		, vStrings[12]);
				   sInt.putExtra("fileSize"		, vStrings[13]);
					   
				   sInt.putExtra("grpTitle"		, vStrings[14]);
				   sInt.putExtra("gmID"			, vStrings[15]);
				   sInt.putExtra("serverFileID"	, vStrings[16]);
				   
				   sInt.putExtra("txtToSend"	, "");
				   sInt.putExtra("grpProfilePic", "");
				   sInt.putExtra("friendsID"	, "");
				   sInt.putExtra("friendsEmail"	, "");
				   sInt.putExtra("friendsName"	, "");
			   }
			   else if( vStrings[0].contentEquals("EXIT_GROUP") )
			   {
				   sInt.putExtra("actionType"	, vStrings[0]);
				   sInt.putExtra("serverGroupID", vStrings[1]);
				   sInt.putExtra("grpTitle"		, vStrings[2]);
				   sInt.putExtra("byUserID"		, vStrings[3]);
				   sInt.putExtra("byEmail"		, vStrings[4]);
				   sInt.putExtra("byName"		, vStrings[5]);
				   sInt.putExtra("byRegID"		, vStrings[6]);
				   
			   }
			   
			   gcmCon.getActivity().startService(sInt);
		}
		catch (Exception fwte) {
			fwte.printStackTrace();
		}
		return result;
	}

}
