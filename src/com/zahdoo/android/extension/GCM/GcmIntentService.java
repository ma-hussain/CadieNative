package com.zahdoo.android.extension.GCM;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.services.s3.transfer.TransferManager;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.zahdoo.android.extension.alarm.Global;

public class GcmIntentService extends IntentService
{
	private NotificationManager mNotificationManager;
    private SQLiteDatabase cDataBase;
	
    //private String cDB_PATH = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadieMainDB.sqlite";
    private String cDB_PATH = "/data/data/air.com.zahdoo.cadie.debug/com.zahdoo.cadie.debug/Local Store/cadieMainDB.sqlite";

    //private String aDB_PATH = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadieAlertDB.sqlite";
    private String aDB_PATH = "/data/data/air.com.zahdoo.cadie.debug/com.zahdoo.cadie.debug/Local Store/cadieAlertDB.sqlite";
    
    public GcmIntentService()
    {
    	super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);
		Log.d(CommonUtilities.TAG, "Message Type - " + messageType);
	
		if (!extras.isEmpty()) // has effect of unparcelling Bundle
		{
		    /*
		     * Filter messages based on message type. Since it is likely that
		     * GCM will be extended in the future with new message types, just
		     * ignore any message types you're not interested in, or that you
		     * don't recognize.
		     */
		    if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType))
		    {
		    	//sendErrorNotification("Send error: " + extras.toString());
		    }
		    else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType))
		    {
		    	//sendErrorNotification("Deleted messages on server: " + extras.toString());
		    }
		    else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType))
		    {
				// Post notification of received message.
		    	String ToEmail = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
						.getString("ToEmail"));
		    	
		    	Log.d(CommonUtilities.TAG, "ToEmail  - " + ToEmail);
		    	
		    	String NotificationType = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
						.getString("NotificationType"));
		    	
		    	Log.d(CommonUtilities.TAG, "Notification Type - " + NotificationType);
		    	
		    	try{
		    		cDataBase = SQLiteDatabase.openDatabase(cDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
		    		Cursor c;
					
					c = cDataBase.rawQuery("SELECT * FROM Config WHERE Key = 'GCMRegisteredEmailID' LIMIT 1" , null);
					
					//Log.d(CommonUtilities.TAG, "Cursor value -  "  + c);
					
					if(c != null ) {
						if(c.moveToFirst())
						{
							String emailId = c.getString(c.getColumnIndex("Value"));
							
							if(ToEmail.contentEquals(emailId))
							{
								Log.d(CommonUtilities.TAG, "Correct email receiver - " + ToEmail + " -- " + emailId);
								Log.d(CommonUtilities.TAG, "DATA received - " + intent.getExtras());
								
								if(NotificationType.startsWith("SELF"))//ALL_SELF_NOTIFICATIONS
								{
									if(NotificationType.contentEquals("SELF_ACCEPT_REQUEST"))//When user accepts a request
									{
										String Time = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("Time"));
										
										String ReqSenderUserID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ReqSenderUserID"));
											
					                    Log.d(CommonUtilities.TAG, "ReqSenderUserID -  "  + ReqSenderUserID); 
					                    
							    		String ReqSenderEmailId = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ReqSenderEmailId"));
							    		
							    		 Log.d(CommonUtilities.TAG, "ReqSenderEmailId -  "  + ReqSenderEmailId);
							    		
										String ReqMsgShareID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ReqMsgShareID"));
										
										Log.d(CommonUtilities.TAG, "ReqMsgShareID -  "  + ReqMsgShareID);
										
										String RequestSenderName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("RequestSenderName"));
										
										Log.d(CommonUtilities.TAG, "RequestSenderName -  "  + RequestSenderName);
										
										String ReqSenderPhoneNo = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ReqSenderPhoneNo"));
										
										Log.d(CommonUtilities.TAG, "ReqSenderPhoneNo -  "  + ReqSenderPhoneNo);
										
										String ReqSenderProfileMessage = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ReqSenderProfileMessage"));
										
										Log.d(CommonUtilities.TAG, "ReqSenderProfileMessage -  "  + ReqSenderProfileMessage);
											
							    		String ReqSenderDOB = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ReqSenderDOB"));
							    		
							    		Log.d(CommonUtilities.TAG, "ReqSenderDOB -  "  + ReqSenderDOB);
							    		
										String ReqSenderLocation = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ReqSenderLocation"));
										
										Log.d(CommonUtilities.TAG, "ReqSenderLocation -  "  + ReqSenderLocation);
										
										String ReqSenderProfilePicName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ReqSenderProfilePicName"));
										
										Log.d(CommonUtilities.TAG, "ReqSenderProfilePicName -  "  + ReqSenderProfilePicName);
										
										DeleteCadieRequests(ReqSenderEmailId,ToEmail);
										
										InsertCadieContacts(ReqSenderUserID, ReqSenderEmailId, RequestSenderName,ReqMsgShareID,ToEmail, 
												ReqSenderPhoneNo,ReqSenderProfileMessage,ReqSenderDOB,ReqSenderLocation,ReqSenderProfilePicName,extras ,true,Time);
									}
									else if(NotificationType.contentEquals("SELF_CONTACT_REQUEST"))//When user sends a request
									{
										String Time = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("Time"));

							    		String RequestFromEmail = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("RequestFromEmail"));
										
										Log.d(CommonUtilities.TAG, "RequestFromEmail -  "  + RequestFromEmail);
											
										String RequestFromName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("RequestFromName"));
										
										Log.d(CommonUtilities.TAG, "RequestFromName -  "  + RequestFromName);
										
										String RequestToEmail = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("RequestToEmail"));
										
										Log.d(CommonUtilities.TAG, "RequestToEmail -  "  + RequestToEmail);
										
										String RequestMessage = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("RequestMessage"));
										
										Log.d(CommonUtilities.TAG, "RequestMessage -  "  + RequestMessage);
										
										String RequestShareId = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("RequestShareId"));
										
										Log.d(CommonUtilities.TAG, "RequestShareId -  "  + RequestShareId);
										
										String ServerRequestId = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ServerRequestId"));
										
										Log.d(CommonUtilities.TAG, "ServerRequestId -  "  + ServerRequestId);
										
										String ToUserName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ToUserName"));
										
										Log.d(CommonUtilities.TAG, "ToUserName -  "  + ToUserName);
										
										InsertContactRequestData(RequestFromEmail ,RequestFromName, RequestMessage, RequestToEmail, RequestShareId , ServerRequestId , ToUserName , extras ,true,Time);
										
									}
									else if(NotificationType.contentEquals("SELF_PROFILE_INFO_CHANGED"))//When user changes his/her's profile info
									{
										String Time = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("Time"));

							    		String FriendUserName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("FriendUserName"));
										
										Log.d(CommonUtilities.TAG, "FriendUserName -  "  + FriendUserName);
							    		
										String FriendStatusMsg = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("FriendStatusMsg"));
								    	
										Log.d(CommonUtilities.TAG, "FriendStatusMsg -  "  + FriendStatusMsg);
										
										String FriendDateOfBirth = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("FriendDateOfBirth"));
										
										Log.d(CommonUtilities.TAG, "FriendDateOfBirth -  "  + FriendDateOfBirth);
							    		
										String FriendPhoneNo = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("FriendPhoneNo"));
										
										Log.d(CommonUtilities.TAG, "FriendPhoneNo -  "  + FriendPhoneNo);
								    
										String FriendLocation = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("FriendLocation"));
										
										Log.d(CommonUtilities.TAG, "FriendLocation -  "  + FriendLocation);
										
										String FriendProfilePicName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("FriendProfilePicName"));
										
										Log.d(CommonUtilities.TAG, "FriendProfilePicName -  "  + FriendProfilePicName);
										
										String IsFriendProfilePicChanged = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("IsFriendProfilePicChanged"));
										
										Log.d(CommonUtilities.TAG, "IsFriendProfilePicChanged -  "  + IsFriendProfilePicChanged);
										
										String IsFriendProfileNameChanged = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("IsFriendProfileNameChanged"));
										
										Log.d(CommonUtilities.TAG, "IsFriendProfileNameChanged -  "  + IsFriendProfileNameChanged);
										
										UpdateConfigKeyValue(ToEmail , FriendUserName , FriendStatusMsg , FriendDateOfBirth , FriendPhoneNo ,
												FriendLocation , FriendProfilePicName , IsFriendProfilePicChanged , IsFriendProfileNameChanged , Time);
										
									}
									else if(NotificationType.contentEquals("SELF_CONTACT_UNFRIEND"))//When user unfriends any buddy
									{
										String Time = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("Time"));

							    		String UnfriendFromEmail = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("UnfriendFromEmail"));
										
										Log.d(CommonUtilities.TAG, "UnfriendFromEmail -  "  + UnfriendFromEmail);
								    
										String UnfriendedEmail = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("UnfriendedEmail"));
										
										Log.d(CommonUtilities.TAG, "UnfriendedEmail -  "  + UnfriendedEmail);
										
										deleteUserChatHistory(UnfriendFromEmail , UnfriendedEmail);
									}
									else if(NotificationType.contentEquals("SELF_MESSAGE_GROUP_CREATED"))//When user sends a message to any buddy
									{
										String Time = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("Time"));

							    		String ServerGroupID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ServerGroupID"));
							    		
							    		Log.d("CADIE GCM", "282 GCM Intent - Server Group ID - " + ServerGroupID);
											
							    		String ByUserID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ByUserID"));
							    		
										String ByEmail = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ByEmail"));
							    		
										String ByName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ByName"));
										
										String GroupTitle = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("GroupTitle"));
								    
										String Message = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("Message"));
										
										String grpProfilePic = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ProfilePic"));
										
										Log.d("CADIE GCM", "Message - " + Message);
										
										InsertGroupNotificationData("SELF_GROUP_CREATED" , ServerGroupID);//, GroupTitle, grpProfilePic, ByUserID, ByEmail,
												//ByName, Message, Time, arrUserID, arrUserEmail, arrUserName);
								    	
										//InsertSelfShareHistory("MESSAGE" , ShareID, ByEmail , ByName, SentToEmail, "", Message,"" ,"" ,"0" , ToName ,Time);
										
									}
									else if(NotificationType.contentEquals("SELF_GROUP_MESSAGE"))
									{
										Log.d("CADIE GCM", "At 312 - " + NotificationType);
										
										GroupMessageData gm = new GroupMessageData();
									
										gm.Time = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("Time"));

										gm.ServerGroupID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ServerGroupID"));
							    		
							    		Log.d("CADIE GCM", "320 GCM Intent - " + NotificationType);
											
							    		gm.GroupTitle = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("GroupTitle"));
							    		
							    		gm.ByUserID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ByUserID"));
							    		
										gm.ByEmail = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ByEmail"));
							    		
										gm.ByName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ByName"));
										
										gm.ToEmail = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ToEmail"));
								    
										gm.Message = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("Message"));
										
										gm.MessageType = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("MessageType"));
										
										gm.ShareType = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ShareType"));
										
										gm.NoteID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("NoteID"));
										
										gm.NoteTitle = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("NoteTitle"));

										gm.DBName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("DBName"));
										
										gm.FileName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("FileName"));
										
										gm.FilePath = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("FilePath"));

										gm.FileSize = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("FileSize"));
										
										gm.NotificationMessage = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("NotificationMessage"));
										
										gm.NotificationType = NotificationType;
										
										showGroupMessageNotification( gm, extras );
									}
									else if(NotificationType.contentEquals("SELF_MESSAGE"))//When user sends a message to any buddy
									{
										String Time = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("Time"));

							    		String ShareID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ShareID"));
							    		
							    		Log.d("CADIE GCM", "386 Shared ID - " + ShareID);
											
							    		String ByEmail = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ByEmail"));
							    		
							    		Log.d("CADIE GCM", "ByEmail - " + ByEmail);
							    		
										String ByName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ByName"));
										
										Log.d("CADIE GCM", "ByName - " + ByName);
										
										String SentToEmail = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("SentToEmail"));
										
										Log.d("CADIE GCM", "SentToEmail - " + SentToEmail);
										
										String ToName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ToName"));
										
										Log.d("CADIE GCM", "ToName - " + ToName);
								    
										String Message = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("Message"));
										
										Log.d("CADIE GCM", "Message - " + Message);
										
										InsertSelfShareHistory("MESSAGE" , ShareID, ByEmail , ByName, SentToEmail, "", Message,"" ,"" ,"0" , ToName ,Time);
										
									}
									else if(NotificationType.contentEquals("SELF_NOTE"))//When user shares a note to any buddy
									{
										String Time = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("Time"));

							    		String ShareID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ShareID"));
							    		
							    		Log.d("CADIE GCM", "424 Shared ID - " + ShareID);
											
							    		String ByEmail = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ByEmail"));
							    		
							    		Log.d("CADIE GCM", "ByEmail - " + ByEmail);
							    		
										String ByName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ByName"));
										
										Log.d("CADIE GCM", "ByName - " + ByName);
										
										String SentToEmail = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("SentToEmail"));
										
										Log.d("CADIE GCM", "SentToEmail - " + SentToEmail);
										
										String ToName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ToName"));
										
										Log.d("CADIE GCM", "ToName - " + ToName);
										
										String NoteID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("NoteID"));
										
										Log.d("CADIE GCM", "NoteID - " + NoteID);
										
										String NoteTitle = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("NoteTitle"));
										
										Log.d("CADIE GCM", "NoteTitle - " + NoteTitle);
										
										String DbName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("DbName"));
										
										Log.d("CADIE GCM", "DbName - " + DbName);
										
										InsertSelfShareHistory("NOTE" , ShareID, ByEmail , ByName, SentToEmail, NoteID, NoteTitle,DbName ,"" ,"0" , ToName ,Time );
										
									}
									else if(NotificationType.contentEquals("SELF_PICTURE_VIDEO_VOICE_FILE"))//When user shares a video,pic,audio or file to any buddy
									{
										String Time = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("Time"));

							    		String ShareID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ShareID"));
										
										Log.d("CADIE GCM", "472 Shared ID - " + ShareID);
											
										String ByEmail = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ByEmail"));
							    		
							    		Log.d("CADIE GCM", "ByEmail - " + ByEmail);
							    		
										String ByName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ByName"));
										
										Log.d("CADIE GCM", "ByName - " + ByName);
										
										String SentToEmail = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("SentToEmail"));
										
										Log.d("CADIE GCM", "SentToEmail - " + SentToEmail);
										
										String ToName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ToName"));
										
										Log.d("CADIE GCM", "ToName - " + ToName);
										
										String Message = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("Message"));
										
										Log.d("CADIE GCM", "Message - " + Message);
										
										String ServerFileID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ServerFileID"));
										
										Log.d("CADIE GCM", "ServerFileID - " + ServerFileID);
										
										String FileName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("FileName"));
										
										Log.d("CADIE GCM", "FileName - " + FileName);
										
										String FileSize = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("FileSize"));
										
										Log.d("CADIE GCM", "FileSize - " + FileSize);
										
										String ShareType = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ShareType"));
										
										Log.d("CADIE GCM", "ShareType - " + ShareType);
										
										DownloadThumbnailFromS3(ShareType ,ShareID , ByEmail , ByName , SentToEmail , Message , ServerFileID , FileName , FileSize ,extras , true ,ToName ,Time);
									    
									}
									else if(NotificationType.contentEquals("SELF_INVITATION"))//When user invites a non Cadie user via email
									{
										String Time = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("Time"));

							    		String InvitedByUserID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("InvitedByUserID"));
										
										Log.d("CADIE GCM", "InvitedByUserID - " + InvitedByUserID);
											
										String InvitedByEmail = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("InvitedByEmail"));
							    		
							    		Log.d("CADIE GCM", "InvitedByEmail - " + InvitedByEmail);
							    		
										String InvitedByName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("InvitedByName"));
										
										Log.d("CADIE GCM", "InvitedByName - " + InvitedByName);
										
										String InvitationToEmail = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("InvitationToEmail"));
										
										Log.d("CADIE GCM", "InvitationToEmail - " + InvitationToEmail);
										
										String InvitationToName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("InvitationToName"));
										
										Log.d("CADIE GCM", "InvitationToName - " + InvitationToName);
										
										if( InvitationToName == null )
											InvitationToName = "";
										
										String InvitationMessage = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("InvitationMessage"));
										
										Log.d("CADIE GCM", "InvitationMessage - " + InvitationMessage);
										
										String ServerReqInviteId = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ServerReqInviteId"));
										
										Log.d("CADIE GCM", "ServerReqInviteId - " + ServerReqInviteId);
										
										String ReqInviteMsgId = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ReqInviteMsgId"));
										
										Log.d("CADIE GCM", "ReqInviteMsgId - " + ReqInviteMsgId);

										insertCadieInvitationData(ServerReqInviteId ,InvitationMessage,InvitationToEmail,InvitationToName,InvitedByEmail,InvitedByName,Time);
										InsertSelfShareHistory("CONTACT_REQUEST_MESSAGE" , ReqInviteMsgId, InvitedByEmail , InvitedByName, InvitationToEmail, "", InvitationMessage,"" ,"" ,"0" , InvitationToName ,Time );
									}
									else if(NotificationType.contentEquals("SELF_EXIT_GROUP"))
									{
										Log.d("CADIE GCM", "At 576 - " + NotificationType);
										 
										GroupMessageData gm = new GroupMessageData();
										
										gm.Time = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("Time"));

										gm.ServerGroupID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ServerGroupID"));
							    		
							    		Log.d("CADIE GCM", "586 GCM Intent - " + NotificationType);
											
							    		gm.GroupTitle = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("GroupTitle"));
							    		
							    		gm.ByUserID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ByUserID"));
							    		
										gm.ByEmail = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ByEmail"));
							    		
										gm.ByName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ByName"));
										
										gm.ToEmail = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ToEmail"));
								    
										gm.Message = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("Message"));
										
										gm.MessageType = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("MessageType"));
										
										gm.ShareType = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("ShareType"));
										
										gm.NotificationMessage = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
												.getString("NotificationMessage"));
										
										gm.NotificationType = NotificationType;
										
										showGroupLeftNotification( gm, extras );
									}
								}
								else if(NotificationType.contentEquals("GROUP_MESSAGE"))
								{
									Log.d("CADIE GCM", "At 576 - " + NotificationType);
									
									GroupMessageData gm = new GroupMessageData();
								
									gm.Time = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("Time"));

									gm.ServerGroupID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ServerGroupID"));
						    		
						    		Log.d("CADIE GCM", "586 GCM Intent - " + NotificationType);
										
						    		gm.GroupTitle = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("GroupTitle"));
						    		
						    		gm.ByUserID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ByUserID"));
						    		
									gm.ByEmail = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ByEmail"));
						    		
									gm.ByName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ByName"));
									
									gm.ToEmail = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ToEmail"));
							    
									gm.Message = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("Message"));
									
									gm.MessageType = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("MessageType"));
									
									gm.ShareType = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ShareType"));
									
									gm.NoteID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("NoteID"));
									
									gm.NoteTitle = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("NoteTitle"));

									gm.DBName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("DBName"));
									
									gm.FileName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("FileName"));
									
									gm.FilePath = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("FilePath"));

									gm.FileSize = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("FileSize"));
									
									gm.NotificationMessage = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("NotificationMessage"));
									
									gm.NotificationType = NotificationType;
									
									showGroupMessageNotification( gm, extras );
								}
								else if(NotificationType.contentEquals("LINK_DROPBOX")){
						    		String RequestKey = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("RequestKey"));
										
									String RequestSecret = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("RequestSecret"));
									
									String AccessKey = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("AccessKey"));
									
									String AccessSecret = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("AccessSecret"));
									
						    		InsertDropboxKey(RequestKey , RequestSecret , AccessKey , AccessSecret);
						    	}
						    	else if(NotificationType.contentEquals("CONTACT_REQUEST"))
						    	{
						    		String Time = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("Time"));

						    		String RequestFromEmail = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("RequestFromEmail"));
										
									String RequestFromName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("RequestFromName"));
									
									String RequestMessage = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("RequestMessage"));
									
									String RequestShareId = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("RequestShareId"));
									
									String ServerRequestId = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ServerRequestId"));
									
									String ToUserName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ToUserName"));
									

									InsertContactRequestData(RequestFromEmail ,RequestFromName, RequestMessage, ToEmail, RequestShareId , ServerRequestId , ToUserName , extras , false,Time);
						    	}
						    	else if(NotificationType.contentEquals("CONTACT_REQUEST_ACCEPTED"))
						    	{
						    		String Time = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("Time"));

						    		String AcceptedByName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("AcceptedByName"));
										
						    		String AcceptedByEmailId = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("AcceptedByEmailId"));
						    		
									String AcceptedByUserId = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("AcceptedByUserId"));
									
									//String ServerRequestId = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
									//		.getString("ServerRequestId"));
									
									String SharedID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("SharedID"));
									
									
									String AcceptedByPhoneNo = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("AcceptedByPhoneNo"));
										
						    		String AcceptedByProfileMessage = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("AcceptedByProfileMessage"));
						    		
									String AcceptedByDOB = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("AcceptedByDOB"));
									
									String AcceptedByLocation = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("AcceptedByLocation"));
									
									String AcceptedByProfilePicName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("AcceptedByProfilePicName"));
									
									
									//DeleteCadieRequests(ServerRequestId);
									DeleteCadieRequests(ToEmail, AcceptedByEmailId);

									InsertCadieContacts(AcceptedByUserId, AcceptedByEmailId, AcceptedByName,SharedID,ToEmail, 
											AcceptedByPhoneNo,AcceptedByProfileMessage,AcceptedByDOB,AcceptedByLocation,AcceptedByProfilePicName,extras ,false ,Time);
							    	
						    	}
//						    	else if(NotificationType.contentEquals("CONTACT_REQUEST_DECLINED"))
//						    	{
//						    		String Time = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
//											.getString("Time"));
//
//						    		Log.d("CADIE GCM", "Time - " + Time);
//						    		
//						    		String AcceptedByName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
//											.getString("AcceptedByName"));
//										
//						    		String AcceptedByEmailId = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
//											.getString("AcceptedByEmailId"));
//						    		
//									//String AcceptedByUserId = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
//									//		.getString("AcceptedByUserId"));
//									
//									String ServerRequestId = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
//											.getString("ServerRequestId"));
//							    
//									
//									UpdateCadieRequests(AcceptedByName, AcceptedByEmailId,  ServerRequestId , extras);
//							    	
//						    	}
								
								//////////////////////////////////////////////////////////////////
								//////////////////////////////////////////////////////////////////
								//////////////////    Group Created Message    ///////////////////
								//////////////////////////////////////////////////////////////////
								//////////////////////////////////////////////////////////////////
								//////////////////////////////////////////////////////////////////
						    	else if(NotificationType.contentEquals("GROUP_CREATED"))
						    	{
						    		String Time = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("Time"));

						    		String ServerGroupID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ServerGroupID"));
						    		
						    		Log.d("CADIE GCM", "629 Server Group ID - " + ServerGroupID);
										
						    		String ByUserID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ByUserID"));
						    		
						    		String ByEmail = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ByEmail"));
						    		
									String ByName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ByName"));
									
									String GroupTitle = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("GroupTitle"));
							    
									String Message = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("Message"));
									
									String grpProfilePic = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ProfilePic"));

									showNewGroupNotification(ServerGroupID , ByUserID,  ByEmail , ByName , ToEmail , GroupTitle , grpProfilePic , Message , extras , Time);
						    	}
						    	else if(NotificationType.contentEquals("MESSAGE"))
						    	{
						    		String Time = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("Time"));

						    		String ShareID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ShareID"));
						    		
						    		Log.d("CADIE GCM", "727 Shared ID - " + ShareID);
										
						    		String ByEmail = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ByEmail"));
						    		
									String ByName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ByName"));
							    
									String Message = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("Message"));
							    	

									showMessageNotification(ShareID , ByEmail , ByName , ToEmail , Message , extras ,Time);
						    	}
						    	else if(NotificationType.contentEquals("GROUP_FILE_MESSAGE"))
						    	{
						    		GroupMessageData gm = new GroupMessageData();
									
						    		gm.Time = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("Time"));

						    		gm.ServerGroupID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ServerGroupID"));
						    		
						    		Log.d("CADIE GCM", "812 ServerGroupID ID - " + gm.ServerGroupID);
										
						    		gm.ByEmail = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ByEmail"));
						    		
						    		gm.ByName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ByName"));
							    
						    		gm.Message = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("Message"));
									
						    		gm.ServerFileID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ServerFileID"));
						    		
						    		gm.MessageType = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("MessageType"));
									
									gm.ShareType = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ShareType"));
									
									gm.NoteID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("NoteID"));
									
									gm.NoteTitle = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("NoteTitle"));

									gm.DBName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("DBName"));
									
									gm.FileName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("FileName"));
							    
						    		gm.FilePath = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("FilePath"));

									gm.FileSize = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("FileSize"));

						    		DownloadGroupThumbnailFromS3(gm, extras, false);
									//DownloadGroupThumbnailFromS3(MessageType, ServerGroupID , ByEmail , ByName , Message , ServerFileID , FileName , FileSize ,extras , false , Time);
						    	}
						    	else if(NotificationType.contentEquals("PICTURE") || NotificationType.contentEquals("VIDEO") || 
						    			NotificationType.contentEquals("VOICE") || NotificationType.contentEquals("FILE"))
						    	{
						    		String Time = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("Time"));

						    		String ShareID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ShareID"));
						    		
						    		Log.d("CADIE GCM", "843 Shared ID - " + ShareID);
										
						    		String ByEmail = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ByEmail"));
						    		
									String ByName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ByName"));
							    
									String Message = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("Message"));
									
									String ServerFileID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ServerFileID"));
						    		
									String FileName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("FileName"));
							    
									String FileSize = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("FileSize"));

									DownloadThumbnailFromS3(NotificationType ,ShareID , ByEmail , ByName , ToEmail , Message , ServerFileID , FileName , FileSize ,extras , false , "" ,Time);
						    	}
						    	else if(NotificationType.contentEquals("CONTACT_UNFRIEND"))
						    	{
						    		String Time = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("Time"));

						    		String UnfriendFromEmail = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("UnfriendFromEmail"));
							    
									String UnfriendFromName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("UnfriendFromName"));

									updateUnfriendContact(UnfriendFromEmail,UnfriendFromName,ToEmail,extras ,Time);
									//showMessageNotification(ShareID , ByEmail , ByName , ToEmail , Message , extras);
						    	}
						    	else if(NotificationType.contentEquals("PROFILE_INFO_CHANGED"))
						    	{
						    		String FriendUserName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("FriendUserName"));
						    		
									String FriendEmailID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("FriendEmailID"));
							    
									String FriendStatusMsg = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("FriendStatusMsg"));
							    	
									String FriendDateOfBirth = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("FriendDateOfBirth"));
						    		
									String FriendPhoneNo = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("FriendPhoneNo"));
							    
									String FriendLocation = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("FriendLocation"));
									
									String FriendProfilePicName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("FriendProfilePicName"));
									
									String IsFriendProfilePicChanged = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("IsFriendProfilePicChanged"));
									
									String IsFriendProfileNameChanged = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("IsFriendProfileNameChanged"));
									
									updateFriendProfileInfo(FriendUserName,FriendEmailID,FriendStatusMsg,
											FriendDateOfBirth,FriendPhoneNo,FriendLocation,FriendProfilePicName,IsFriendProfilePicChanged , IsFriendProfileNameChanged );
						    	}
						    	else if(NotificationType.contentEquals("EXIT_GROUP"))
								{
									Log.d("CADIE GCM", "At 576 - " + NotificationType);
									
									GroupMessageData gm = new GroupMessageData();
									
									gm.Time = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("Time"));

									gm.ServerGroupID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ServerGroupID"));
						    		
						    		Log.d("CADIE GCM", "586 GCM Intent - " + NotificationType);
										
						    		gm.GroupTitle = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("GroupTitle"));
						    		
						    		gm.ByUserID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ByUserID"));
						    		
									gm.ByEmail = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ByEmail"));
						    		
									gm.ByName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ByName"));
									
									gm.ToEmail = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ToEmail"));
							    
									gm.Message = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("Message"));
									
									gm.MessageType = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("MessageType"));
									
									gm.ShareType = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ShareType"));
									
									gm.NotificationMessage = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("NotificationMessage"));
									
									gm.NotificationType = NotificationType;
									
									showGroupLeftNotification( gm, extras );
								}
						    	else{
						    		String Time = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("Time"));

						    		String ShareID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ShareID"));
						    		
						    		Log.d("CADIE GCM", "825 Shared ID - " + ShareID);
										
						    		String ByEmail = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ByEmail"));
						    		
									String ByName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("ByName"));
									
									String NoteID = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("NoteID"));
									
									String NoteTitle = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("NoteTitle"));
									
									String DbName = ((intent.getExtras() == null) ? "Empty Bundle" : intent.getExtras()
											.getString("DbName"));
									

//						    		if (intent.getExtras() != null
//										&& "com.zahdoo.mycadie.CLEAR_NOTIFICATION".equals(intent.getExtras()
//											.getString("action")))
//									{
//									   // clearNotification();
//									}
//									else
//									{
										startSharedDbDownload(ByEmail,DbName,NotificationType, ShareID, ByName, ToEmail, NoteID, NoteTitle, extras ,Time);//File Path and db name
									    //sendNotification( NotificationType, ShareID, ByEmail , ByName, ToEmail, NoteID, NoteTitle,DbName,extras);
									//}
						    		
						    		Log.i(CommonUtilities.TAG, "Received: " + ByEmail);
						    	}
							}
							else{
								Log.e(CommonUtilities.TAG, "NOT CURRENT USER - Notification is for - " + ToEmail );
								c.close();
								

								Log.d(CommonUtilities.TAG,"926 &&&&&& Inside else " ); 
								
								stopSelf();
								
								
								return;
							}
						}
					}
					
					Log.d(CommonUtilities.TAG,"936 ===  " ); 
					
					c.close();
				}
		    	catch (Exception e)
		    	{
		    		Log.d(CommonUtilities.TAG,"Get Config Java Code Error - " + e.toString()); 
				}
		    	finally{
		    		if(cDataBase.isOpen())
		    			cDataBase.close();
		    	}
		    }
		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
    
///////////////// GROUP FILES //////////////////
	private void DownloadGroupThumbnailFromS3(GroupMessageData gm,
			Bundle extras, Boolean isSelfNotification) {
		try {
			if (gm.MessageType.contentEquals("GROUP_PICTURE")) {
				downloadMediaThumbnail(gm.FileName);
			} else if (gm.MessageType.contentEquals("GROUP_VIDEO")) {
				String fName = gm.FileName;
				fName = fName.substring(0, fName.lastIndexOf("."));
				fName = fName + ".jpg";
				downloadMediaThumbnail(fName);
			}

			InsertGroupMessageData( gm );
			
			if (Global.bRun) {
				GCMInitFunction.cGCM.dispatchStatusEventAsync(
						"RETRIEVE_NOTE", "REFRESH_FILE_MESSAGE^"
								+ gm.ServerGroupID);				
//				if (gm.MessageType.contentEquals("GROUP_PICTURE")
//						|| gm.MessageType.contentEquals("VIDEO")
//						|| gm.MessageType.contentEquals("VOICE")
//						|| gm.MessageType.contentEquals("FILE"))
//					GCMInitFunction.cGCM.dispatchStatusEventAsync(
//							"RETRIEVE_NOTE", "REFRESH_FILE_MESSAGE^"
//									+ gm.ServerGroupID);
			}
//			if (isSelfNotification) {
//				InsertSelfShareHistory(gm.MessageType, gm.ServerGroupID,
//						gm.ByEmail, gm.ByName, "", gm.Message, gm.ServerFileID,
//						gm.FileName, gm.FileSize, gm.Time);
//			} else {
//				Log.d(CommonUtilities.TAG, "Insert and show notification");
//				showMediaChatNotification(gm.MessageType, gm.ServerGroupID,
//						gm.ByEmail, gm.ByName, gm.Message, gm.ServerFileID,
//						gm.FileName, gm.FileSize, extras, gm.Time);
//			}
		} catch (Exception e) {
			Log.d(CommonUtilities.TAG, e.toString()
					+ " Group File Download ERROR  ");
			FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED",
					"ERROR ");
		}
	}

    ///////////////////////////////////
    private void DownloadThumbnailFromS3(String FileType, String ShareID ,String ByEmail ,String ByName ,String ToEmail ,
    		String Message ,String ServerFileID ,String FileName ,String FileSize ,Bundle extras , Boolean isSelfNotification ,String ToName , String time)
    {
    	try
		 {
    		if(FileType.contentEquals("PICTURE") )
		 	{
		 		downloadMediaThumbnail(FileName);
		 	}
		 	else if(FileType.contentEquals("VIDEO") )
	 		{
		 		String fName = FileName;
		 		fName = fName.substring(0,fName.lastIndexOf("."));
		 		fName = fName + ".jpg";
		 		downloadMediaThumbnail(fName);
	 		}
		 	
    		if(isSelfNotification)
		 	{
		 		InsertSelfShareHistory(FileType , ShareID, ByEmail , ByName, ToEmail, "", Message,ServerFileID ,FileName ,FileSize , ToName ,time );
		 	}
		 	else
		 	{
		 		Log.d(CommonUtilities.TAG , "Insert and show notification" );
			 	showMediaChatNotification(FileType , ShareID , ByEmail , ByName , ToEmail , Message , ServerFileID , FileName ,FileSize, extras ,time);
		 	}
		 }  
		 catch (Exception e) 
		 {
		 	Log.d(CommonUtilities.TAG , e.toString() + " File Download ERROR  ");
		 	FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR "  );
		 }
    }
    
    private void downloadMediaThumbnail(String strFileName) {
    	int test = 0;
    	try {
    		Log.d(CommonUtilities.TAG, "mTransferManager THUMB_DOWNLOAD Initialize " );
			
			if(Global.mTransferManager == null)
				Global.mTransferManager = new TransferManager(Util.getCredProvider(getApplicationContext()));
			
		 	Log.d(CommonUtilities.TAG, "mTransferManager THUMB_DOWNLOAD Initialized 2 " );
		 	
    		String PATH = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/thumbnails";
    		test = 1;
            
            Log.d(CommonUtilities.TAG , "Device File Path - " + PATH);
            File file = new File(PATH); // PATH = /mnt/sdcard/download/
            
            if (!file.exists()) {
                file.mkdirs();
            }
            
            if (!file.exists()) {
            	file.getParentFile().mkdirs();
            }
    		
    		test =2 ;
    		strFileName = "th_" + strFileName;
            File file2 = new File(file, strFileName);
            test = 3; 
            
            Log.d(CommonUtilities.TAG , "Starting thumbnail download" );
            Global.mDownload = Global.mTransferManager.download(
                    Constants.BUCKET_NAME, strFileName, file2);
            Log.d(CommonUtilities.TAG , "Thumbnail download started" );
         // waitForCompletion blocks the current thread until the transfer completes
         // and will throw an AmazonClientException or AmazonServiceException if
         // anything went wrong.
            Global.mDownload.waitForCompletion();
            Log.d(CommonUtilities.TAG , "Thumbnail download completed" );
         // After the upload is complete, call shutdownNow to release the resources.
            //tx.shutdownNow();
		} catch (Exception e) {
			Log.d("CADIE S3", "Error - " + test + " " + e.toString());
            Global.mTransferManager.shutdownNow();
            Global.mTransferManager = null;
		}
    }
    
    ///////////////////////////////
    private void  startSharedDbDownload( final String filePath1 ,final String fileName1,final String notificationType,final String shareID,
    		final String ByName,final  String ToEmail,final String NoteID,final String NoteTitle,final  Bundle extras ,String time)
    {
    	URL url = null;
        //URLConnection con = null;
    	HttpURLConnection con = null;

        try {
        	//fileName = "A72C7C5B-3C02-13D5-8C79-FFA0D509126BcadieShareDB.sqlite";
        	String urlpath = "http://54.88.103.38/gcm_server_php/CadieUpload/"+ filePath1.toString() +"/"+ fileName1.toString();
            url = new URL(urlpath.toString());
            con = (HttpURLConnection)url.openConnection();
            
            String PATH = Environment.getExternalStorageDirectory() + "/Cadie/frmServer";
            File file = new File(PATH); // PATH = /mnt/sdcard/download/
            
            if (!file.exists()) {
                file.mkdirs();
            }
            
            if (!file.exists()) {
            	file.getParentFile().mkdirs();
            }
            
            if (!file.exists()) 
            {
            	PATH = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadie/frmServer";
            }
            
            if (!file.exists()) {
                file.mkdirs();
            }
            
            
            if (!file.exists()) {
            	file.getParentFile().mkdirs();
            }
           
            File outputFile = new File(file,fileName1);  
            FileOutputStream fos = new FileOutputStream(outputFile);

            con.setRequestMethod("GET");
            con.setDoOutput(false);
            //con.setChunkedStreamingMode(0);
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
            
            Log.d(CommonUtilities.TAG , "File downloaded to device  ");
            
            fos.flush();
            fos.close();
            bis.close();
            
            //if (con != null)
            //	con.disconnect();

            sendNotification( notificationType, shareID, filePath1 , ByName, ToEmail, NoteID, NoteTitle,fileName1,extras ,time);

        } catch (MalformedInputException miex) {
        	Log.d(CommonUtilities.TAG , " File Exception  - " + miex.toString() );
        } catch (IOException ioException) {
        	Log.d(CommonUtilities.TAG , " File IOException  - " + ioException.toString() );
        }
	    catch (Exception exc) {
	    	Log.d(CommonUtilities.TAG , " File Download Exception  - " + exc.toString() );
	    }
        finally{
        	if (con != null)
            	con.disconnect();
        }
    }

    /**
     * Put the message into a notification and post it.
     * This is just one simple example of what you might choose to do with 
     * a GCM message.
     * @param msg
     * @param extras
     *  ShareID, ByEmailID , ByName, ToEmailID, NoteID, NoteTitle
     */
    private void sendNotification(String notificationType, String shareID, String ByEmail , String ByName, String ToEmail,String NoteID,String NoteTitle, String DbName,Bundle extras , String time)
    {
    	InsertSharedNotificationData(notificationType , shareID, ByEmail , ByName, ToEmail, NoteID, NoteTitle,DbName ,"" ,"0" ,time);
    	
		Log.d(CommonUtilities.TAG , "GET MESSAGE COUNT   " );
		Cursor c = null;
		int sharedNoteCount	= 0;
		int sharedUserCount = 0;
		
		try{
			c = cDataBase.rawQuery("SELECT * FROM ShareHistory " +
					"WHERE  ShareType = 'RECEIVED' AND NotificationType = 'NOTE' And isViewed = 0 And isDeleted = 0 GROUP BY NoteID" , null);
			if(c != null ) {
				if(c.moveToFirst())
				{
					do {
						sharedNoteCount++;
						
			        }while (c.moveToNext());
				}
				
				Log.d(CommonUtilities.TAG , "SharedNoteCount - " + sharedNoteCount);
			}
		}
		catch (Exception e)
		{
			Log.d(CommonUtilities.TAG,"ShareHistory Insert Error - " + e.toString()); 
		}
		finally{
			Log.d(CommonUtilities.TAG,"Share Info inserted.."); 
			c = cDataBase.rawQuery("SELECT * FROM ShareHistory " +
					"WHERE  ShareType = 'RECEIVED' AND NotificationType = 'NOTE' And isViewed = 0 And isDeleted = 0 GROUP BY ByEmailID" , null);
			
			if(c != null ) {
				if(c.moveToFirst())
				{
					do {
						sharedUserCount++;
						
			        }while (c.moveToNext());
				}
				
				Log.d(CommonUtilities.TAG , "SharedUserCount - " + sharedUserCount);
				
			}
			
			if(c != null ) 
				c.close();
			
			if(cDataBase.isOpen())
				cDataBase.close();
		}
		
		//////////////////
		try {
			mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
			
			Intent intent = new Intent(this, RetrieveNoteService.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			// If this is a notification type message include the data from the message 
			// with the intent
			if (extras != null)
			{
			    intent.putExtras(extras);
			    //intent.setAction("com.zahdoo.mycadie.NOTIFICATION^" + shareID);
			    intent.setData((Uri.parse("custom://"+shareID)));
			}
			
			PendingIntent contentIntent = PendingIntent.getService(this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
			
			int resId0 = 0;
			
			try {
				resId0 = getResources().getIdentifier("icon", "drawable", getApplicationContext().getPackageName());
			} catch (Exception e) {
				Log.d("CADIE ICON", "2 Icon Integer Exception - " + e.toString());
			}
			
			//Bitmap bm = BitmapFactory.decodeResource(getResources(), resId0);
			NotificationCompat.Builder mBuilder = 
				new NotificationCompat.Builder(this)
				.setSmallIcon(resId0)				
				//.setLargeIcon(bm)
				.setContentTitle(ByName + " shared a note with you")
				//.setStyle(new NotificationCompat.BigTextStyle().bigText("Click to retrieve"))
				.setContentText("Note Title - " + NoteTitle)
				.setTicker("Cadie Shared Note")
				.setAutoCancel(true)
				.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
				.setVibrate(new long[]{100, 100, 100, 100});
			
			
			if(sharedNoteCount > 1 || sharedUserCount > 1)
			{
				mBuilder.setContentTitle("Cadie Shared Notes");
				mBuilder.setContentText(sharedNoteCount + " shared notes from " + sharedUserCount + " Cadie buddies");
			}
		
			mBuilder.setContentIntent(contentIntent);
			
			Log.d(CommonUtilities.TAG, "Share Id Notification - " + Integer.parseInt(shareID));
			
			//mNotificationManager.notify(Integer.parseInt(shareID), mBuilder.build());
			mNotificationManager.notify("SHARED_NOTE",1, mBuilder.build());
			
			if( Global.bRun )
				GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", "REFRESH_SHARED_NOTES^" + ByEmail);
			
		} catch (Exception e) {
			Log.d("CADIE", "Cadie Shared Note Notification Exception - " +  e.toString());
		}
    }
    
    
    
  //NotificationType = EXIT_GROUP
    private void showGroupLeftNotification( GroupMessageData gm, Bundle extras )
    {
    	InsertGroupMessageData( gm );
    	
    	try 
    	{
    		cDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);

    		ContentValues values = new ContentValues();                          
    		values.put("IsActive", false);
    		//values.put("LastModifiedDateTime", getDateTime());
    		values.put("RemovedOn", gm.Time);
    		
    		String where = "ServerGroupID = ? AND UserID = ? ";  
    		final String[] whereArgs = { gm.ServerGroupID, gm.ByUserID };
    		cDataBase.update("GroupUser", values, where, whereArgs);
    		values.clear();
		}
		catch (Exception e)
		{
			Log.d(CommonUtilities.TAG,"updateFriendProfileInfo Update Error"); 
		}
		finally{
			Log.d(CommonUtilities.TAG,"updateFriendProfileInfo updated.."); 
			
			if(cDataBase.isOpen())
				cDataBase.close();
		}
    	    	
		mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
	
		Intent intent = new Intent(this, RetrieveNoteService.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		// If this is a notification type message include the data from the message 
		// with the intent
		if (extras != null)
		{
		   intent.putExtras(extras);
		   intent.setData((Uri.parse("custom://MESSAGE")));
		}
		
		PendingIntent contentIntent = PendingIntent.getService(this, 0, intent,
			PendingIntent.FLAG_UPDATE_CURRENT);
		
		int resId2 = 0;
		try {
			resId2 = getResources().getIdentifier("icon", "drawable", getApplicationContext().getPackageName());
			
			Log.d("CADIE ICON", "2 Icon Integer Value - " + resId2);
			
		} catch (Exception e) {
			Log.d("CADIE ICON", "2 Icon Integer Exception - " + e.toString());
		}
		
		//Bitmap bm = BitmapFactory.decodeResource(getResources(), resId2);
		try {
			NotificationCompat.Builder mBuilder = 
					new NotificationCompat.Builder(this)
					.setSmallIcon(resId2)
					//.setLargeIcon(bm)
					.setContentTitle(gm.NotificationMessage)
					//.setStyle(new NotificationCompat.BigTextStyle().bigText("Click to retrieve"))
					.setContentText(gm.Message)
					.setTicker(gm.NotificationMessage)
					.setAutoCancel(true)
					.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
					.setVibrate(new long[]{100, 100, 100, 100});
				
			
				mBuilder.setContentIntent(contentIntent);
				Log.d(CommonUtilities.TAG, "Message Id Notification - " + Integer.parseInt( gm.ServerGroupID ));
				
				//mNotificationManager.notify(Integer.parseInt(shareID), mBuilder.build());
				mNotificationManager.notify("EXIT_GROUP", 1, mBuilder.build());
				
				if( Global.bRun )
					GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", "GROUP_MESSAGE_NEW^" + gm.ServerGroupID);
					//GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", "REFRESH_MESSAGE^" + ByEmail);
				
		} catch (Exception e) {
			Log.d(CommonUtilities.TAG, "Message Notification Exception - " + e.toString());
		}
    }
    
    /**
     * Put the message into a notification and post it.
     * This is just one simple example of what you might choose to do with 
     * a GCM message.
     * @param msg
     * @param extras
     *  ShareID, ByEmailID , ByName, ToEmailID, NoteID, NoteTitle
     */
    
  //NotificationType = GROUP_CREATED
    private void showGroupMessageNotification( GroupMessageData gm, Bundle extras )
    {
    	InsertGroupMessageData( gm );
    	
    	Log.d(CommonUtilities.TAG , "GRP MSG NOTIFICATION >>  " );
    	
    	cDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    	
    	Cursor c = null;
    	int msgCount 		= 0;
    	int cnvrstnCount 	= 0;
		
    	try{
    		c = cDataBase.rawQuery("Select Count(*)  as MsgCount FROM GRP_" + gm.ServerGroupID + " WHERE isViewed = 0 And isDeleted = 0" , null);
			//Log.d(CommonUtilities.TAG, "Cursor value -  "  + c); 
			
			if(c != null ) {
				if(c.moveToFirst())
				{
					do {
						Log.d(CommonUtilities.TAG , "MsgCount1 - " + msgCount);
						msgCount 		= msgCount + Integer.parseInt(c.getString(c.getColumnIndex("MsgCount")));
						cnvrstnCount++;
						Log.d(CommonUtilities.TAG , "MsgCount2 - " + msgCount);
						//Log.d(CommonUtilities.TAG , "ConversationCount - " + cnvrstnCount);
						
			        }while (c.moveToNext());
				}
			}
		}
    	catch (Exception e)
    	{
    		Log.d(CommonUtilities.TAG,"showNewGroupNotification Insert Error - " + e.toString()); 
    		Toast.makeText(getApplicationContext(), "GCM IS 1123 Error"  + e.toString(), Toast.LENGTH_LONG).show();
		}
    	finally{
    		Log.d(CommonUtilities.TAG,"Share Info inserted.."); 
    		
    		if(c != null ) 
    			c.close();
    		
    		if(cDataBase.isOpen())
    			cDataBase.close();
    	}
    	
		mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
	
		Intent intent = new Intent(this, RetrieveNoteService.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		// If this is a notification type message include the data from the message 
		// with the intent
		if (extras != null)
		{
		   intent.putExtras(extras);
		   intent.setData((Uri.parse("custom://MESSAGE")));
		}
		
		PendingIntent contentIntent = PendingIntent.getService(this, 0, intent,
			PendingIntent.FLAG_UPDATE_CURRENT);
		
		int resId2 = 0;
		try {
			resId2 = getResources().getIdentifier("icon", "drawable", getApplicationContext().getPackageName());
			
			Log.d("CADIE ICON", "2 Icon Integer Value - " + resId2);
			
		} catch (Exception e) {
			Log.d("CADIE ICON", "2 Icon Integer Exception - " + e.toString());
		}
		
		//Bitmap bm = BitmapFactory.decodeResource(getResources(), resId2);
		try {
			NotificationCompat.Builder mBuilder = 
					new NotificationCompat.Builder(this)
					.setSmallIcon(resId2)
					//.setLargeIcon(bm)
					.setContentTitle(gm.GroupTitle)
					//.setStyle(new NotificationCompat.BigTextStyle().bigText("Click to retrieve"))
					.setContentText(gm.Message)
					.setTicker(gm.NotificationMessage)
					.setAutoCancel(true)
					.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
					.setVibrate(new long[]{100, 100, 100, 100});
				
				if(msgCount > 1 || cnvrstnCount > 1)
				{
					mBuilder.setContentTitle("Cadie messages");
					mBuilder.setContentText(msgCount + " new message(s) in " + gm.GroupTitle);
				}
			
				mBuilder.setContentIntent(contentIntent);
				Log.d(CommonUtilities.TAG, "Message Id Notification - " + Integer.parseInt( gm.ServerGroupID ));
				
				//mNotificationManager.notify(Integer.parseInt(shareID), mBuilder.build());
				mNotificationManager.notify("GROUP_MESSAGE", 1, mBuilder.build());
				
				if( Global.bRun )
					GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", "GROUP_MESSAGE_NEW^" + gm.ServerGroupID);
					//GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", "REFRESH_MESSAGE^" + ByEmail);
				
		} catch (Exception e) {
			Log.d(CommonUtilities.TAG, "Message Notification Exception - " + e.toString());
		}
    }
    
    
  //NotificationType = GROUP_CREATED
    private void showNewGroupNotification( String serverGroupID, String ByUserID, String ByEmail, String ByName, String ToEmail, 
    		String GroupTitle, String ProfilePic, String Message, Bundle extras, String time)
    		//String[] arrUserID, String[] arrUserEmail, String[] arrUserName)
    {
    	InsertGroupNotificationData("ADDED_TO_NEW_GROUP" , serverGroupID);// GroupTitle, ProfilePic, 
    							//ByUserID, ByEmail , ByName, Message, time, arrUserID, arrUserEmail, arrUserName);
    	
    	Log.d(CommonUtilities.TAG , "showNewGroupNotification >> GET MESSAGE COUNT   " );
    	
    	cDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    	
    	Cursor c = null;
    	int msgCount 		= 0;
    	int cnvrstnCount 	= 0;
		
    	try{
    		c = cDataBase.rawQuery("Select Count(*)  as MsgCount FROM GRP_" + serverGroupID + " WHERE isViewed = 0 And isDeleted = 0" , null);
			//Log.d(CommonUtilities.TAG, "Cursor value -  "  + c); 
			
			if(c != null ) {
				if(c.moveToFirst())
				{
					do {
						Log.d(CommonUtilities.TAG , "MsgCount - " + msgCount);
						msgCount 		= msgCount + Integer.parseInt(c.getString(c.getColumnIndex("MsgCount")));
						cnvrstnCount++;
						Log.d(CommonUtilities.TAG , "MsgCount - " + msgCount);
						//Log.d(CommonUtilities.TAG , "ConversationCount - " + cnvrstnCount);
						
			        }while (c.moveToNext());
				}
			}
		}
    	catch (Exception e)
    	{
    		Log.d(CommonUtilities.TAG,"showNewGroupNotification Insert Error - " + e.toString()); 
    		Toast.makeText(getApplicationContext(), "GCM IS 1123 Error"  + e.toString(), Toast.LENGTH_LONG).show();
		}
    	finally{
    		Log.d(CommonUtilities.TAG,"Share Info inserted.."); 
    		
    		if(c != null ) 
    			c.close();
    		
    		if(cDataBase.isOpen())
    			cDataBase.close();
    	}
    	
		mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
	
		Intent intent = new Intent(this, RetrieveNoteService.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		// If this is a notification type message include the data from the message 
		// with the intent
		if (extras != null)
		{
		   intent.putExtras(extras);
		   intent.setData((Uri.parse("custom://MESSAGE")));
		}
		
		PendingIntent contentIntent = PendingIntent.getService(this, 0, intent,
			PendingIntent.FLAG_UPDATE_CURRENT);
		
		int resId2 = 0;
		try {
			resId2 = getResources().getIdentifier("icon", "drawable", getApplicationContext().getPackageName());
			
			Log.d("CADIE ICON", "2 Icon Integer Value - " + resId2);
			
		} catch (Exception e) {
			Log.d("CADIE ICON", "2 Icon Integer Exception - " + e.toString());
		}
		
		//Bitmap bm = BitmapFactory.decodeResource(getResources(), resId2);
		try {
			NotificationCompat.Builder mBuilder = 
					new NotificationCompat.Builder(this)
					.setSmallIcon(resId2)
					//.setLargeIcon(bm)
					.setContentTitle(ByName + " has added you")
					//.setStyle(new NotificationCompat.BigTextStyle().bigText("Click to retrieve"))
					.setContentText(Message)
					.setTicker(ByName + " messaged you")
					.setAutoCancel(true)
					.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
					.setVibrate(new long[]{100, 100, 100, 100});
				
				if(msgCount > 1 || cnvrstnCount > 1)
				{
					mBuilder.setContentTitle("Cadie messages");
					mBuilder.setContentText(msgCount + " new messages in " + GroupTitle);
				}
			
				mBuilder.setContentIntent(contentIntent);
				Log.d(CommonUtilities.TAG, "Message Id Notification - " + Integer.parseInt(serverGroupID));
				
				//mNotificationManager.notify(Integer.parseInt(shareID), mBuilder.build());
				mNotificationManager.notify("GROUP_MESSAGE", 1, mBuilder.build());
				
				if( Global.bRun )
					GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", "GROUP_MESSAGE^" + serverGroupID);
					//GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", "REFRESH_MESSAGE^" + ByEmail);
				
		} catch (Exception e) {
			Log.d(CommonUtilities.TAG, "Message Notification Exception - " + e.toString());
		}
    }
    
    //NotificationType = MESSAGE
    private void showMessageNotification( String shareID, String ByEmail , String ByName, String ToEmail, String Message, Bundle extras, String time )
    {
    	InsertSharedNotificationData("MESSAGE" , shareID, ByEmail , ByName, ToEmail, "", Message,"" ,"" ,"0" ,time);
    	
    	Log.d(CommonUtilities.TAG , "GOT CHAT MESSAGE  " );
    	
    	Cursor c = null;
    	int msgCount 		= 0;
    	int cnvrstnCount 	= 0;
		
    	try{
    		c = cDataBase.rawQuery("SELECT " +
					"(Select Count(*) FROM ShareHistory WHERE ShareType = 'RECEIVED' AND (NotificationType = 'MESSAGE' OR NotificationType = 'CONTACT_ACCEPT_MESSAGE') And ByEmailID = sh.ByEmailID And isViewed = 0 And isDeleted = 0 ) as MsgCount," +
					" * FROM ShareHistory sh WHERE  ShareType = 'RECEIVED' AND NotificationType = 'MESSAGE' And isViewed = 0 And isDeleted = 0 " +
					"GROUP BY ByEmailID" , null);
			//Log.d(CommonUtilities.TAG, "Cursor value -  "  + c); 
			if(c != null ) {
				if(c.moveToFirst())
				{
					do {
						Log.d(CommonUtilities.TAG , "MsgCount - " + msgCount);
						msgCount 		= msgCount + Integer.parseInt(c.getString(c.getColumnIndex("MsgCount")));
						cnvrstnCount++;
						Log.d(CommonUtilities.TAG , "MsgCount - " + msgCount);
						Log.d(CommonUtilities.TAG , "ConversationCount - " + cnvrstnCount);
						
			        }while (c.moveToNext());
				}
			}
		}
    	catch (Exception e)
    	{
    		Log.d(CommonUtilities.TAG,"ShareHistory Insert Error - " + e.toString()); 
		}
    	finally{
    		Log.d(CommonUtilities.TAG,"Share Info inserted.."); 
    		
    		if(c != null ) 
    			c.close();
    		
    		if(cDataBase.isOpen())
    			cDataBase.close();
    	}
    	
		mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
	
		Intent intent = new Intent(this, RetrieveNoteService.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		// If this is a notification type message include the data from the message 
		// with the intent
		if (extras != null)
		{
		   intent.putExtras(extras);
		   intent.setData((Uri.parse("custom://MESSAGE")));
		}
		
		PendingIntent contentIntent = PendingIntent.getService(this, 0, intent,
			PendingIntent.FLAG_UPDATE_CURRENT);
		
		
		int resId2 = 0;
		try {
			resId2 = getResources().getIdentifier("icon", "drawable", getApplicationContext().getPackageName());
			
			Log.d("CADIE ICON", "2 Icon Integer Value - " + resId2);
			
		} catch (Exception e) {
			Log.d("CADIE ICON", "2 Icon Integer Exception - " + e.toString());
		}
		
		//Bitmap bm = BitmapFactory.decodeResource(getResources(), resId2);
		try {
			NotificationCompat.Builder mBuilder = 
					new NotificationCompat.Builder(this)
					.setSmallIcon(resId2)
					//.setLargeIcon(bm)
					.setContentTitle(ByName + " messaged you")
					//.setStyle(new NotificationCompat.BigTextStyle().bigText("Click to retrieve"))
					.setContentText(Message)
					.setTicker(ByName + " messaged you")
					.setAutoCancel(true)
					.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
					.setVibrate(new long[]{100, 100, 100, 100});
				
				if(msgCount > 1 || cnvrstnCount > 1)
				{
					mBuilder.setContentTitle("Cadie messages");
					mBuilder.setContentText(msgCount + " messages from " + cnvrstnCount + " conversations");
				}
			
				mBuilder.setContentIntent(contentIntent);
				Log.d(CommonUtilities.TAG, "Message Id Notification - " + Integer.parseInt(shareID));
				
				//mNotificationManager.notify(Integer.parseInt(shareID), mBuilder.build());
				mNotificationManager.notify("MESSAGE", 1, mBuilder.build());
				
				if( Global.bRun )
					GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", "REFRESH_MESSAGE^" + ByEmail);
		} catch (Exception e) {
			Log.d(CommonUtilities.TAG, "Message Notification Exception - " + e.toString());
			
		}
    }
    
    //NotificationType = PICTURE
    private void showMediaChatNotification(String fileType , String shareID, String ByEmail , String ByName, String ToEmail,String Message,
    		String ServerFileID ,String FileName ,String FileSize ,Bundle extras , String time )
    {
    	InsertSharedNotificationData(fileType , shareID, ByEmail , ByName, ToEmail, "", Message,ServerFileID, FileName , FileSize ,time);
    	Log.d(CommonUtilities.TAG , "GET MESSAGE COUNT   " );
    	Cursor c = null;
    	int msgCount 		= 0;
    	int cnvrstnCount 	= 0;
		
    	try
    	{
			c = cDataBase.rawQuery("SELECT " +
					"(Select Count(*) FROM ShareHistory WHERE ShareType = 'RECEIVED' AND " +
					"(NotificationType = 'MESSAGE' OR NotificationType = 'CONTACT_ACCEPT_MESSAGE' " +
					"OR NotificationType = 'PICTURE' OR NotificationType = 'VIDEO' OR NotificationType = 'VOICE' OR NotificationType = 'FILE') " +
					" And ByEmailID = sh.ByEmailID And isViewed = 0 And isDeleted = 0 ) as MsgCount, " +
					" * FROM ShareHistory sh WHERE  ShareType = 'RECEIVED' AND " +
					"(NotificationType = 'MESSAGE' OR NotificationType = 'CONTACT_ACCEPT_MESSAGE' " +
					"OR NotificationType = 'PICTURE' OR NotificationType = 'VIDEO' OR NotificationType = 'VOICE' OR NotificationType = 'FILE') " +
					" And isViewed = 0 And isDeleted = 0 " +
					"GROUP BY ByEmailID" , null);
			//Log.d(CommonUtilities.TAG, "Cursor value -  "  + c); 
			if(c != null ) {
				if(c.moveToFirst())
				{
					do {
						Log.d(CommonUtilities.TAG , "MsgCount - " + msgCount);
						msgCount 		= msgCount + Integer.parseInt(c.getString(c.getColumnIndex("MsgCount")));
						cnvrstnCount++;
						Log.d(CommonUtilities.TAG , "MsgCount - " + msgCount);
						Log.d(CommonUtilities.TAG , "ConversationCount - " + cnvrstnCount);
						
			        }while (c.moveToNext());
				}
			}
		}
    	catch (Exception e)
    	{
    		Log.d(CommonUtilities.TAG,"ShareHistory Insert Error - " + e.toString()); 
		}
    	finally
    	{
    		Log.d(CommonUtilities.TAG,"Share Info inserted.."); 
    		
    		if(c != null ) 
    			c.close();
    		
    		if(cDataBase.isOpen())
    			cDataBase.close();
    	}
    	
    	//////////////////
		mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
	
		Intent intent = new Intent(this, RetrieveNoteService.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		// If this is a notification type message include the data from the message 
		// with the intent
		if (extras != null)
		{
		   intent.putExtras(extras);
		   intent.setData((Uri.parse("custom://MESSAGE")));
		}
		
		PendingIntent contentIntent = PendingIntent.getService(this, 0, intent,
			PendingIntent.FLAG_UPDATE_CURRENT);
		
		
		int resId2 = 0;
		try {
			resId2 = getResources().getIdentifier("icon", "drawable", getApplicationContext().getPackageName());
			
			Log.d("CADIE ICON", "2 Icon Integer Value - " + resId2);
			
		} catch (Exception e) {
			Log.d("CADIE ICON", "2 Icon Integer Exception - " + e.toString());
		}
		
		//Bitmap bm = BitmapFactory.decodeResource(getResources(), resId2);
		try {
			NotificationCompat.Builder mBuilder = 
					new NotificationCompat.Builder(this)
					.setSmallIcon(resId2)
					//.setLargeIcon(bm)
					.setContentTitle(ByName + " sent a picture to you")
					//.setStyle(new NotificationCompat.BigTextStyle().bigText("Click to retrieve"))
					.setContentText(Message)
					.setTicker(ByName + " sent a picture to you")
					.setAutoCancel(true)
					.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
					.setVibrate(new long[]{100, 100, 100, 100});
				
				if(msgCount > 1 || cnvrstnCount > 1)
				{
					mBuilder.setContentTitle("Cadie messages");
					mBuilder.setContentText(msgCount + " messages from " + cnvrstnCount + " conversations");
				}
				else if(fileType.contentEquals("VIDEO"))
				{
					mBuilder.setContentTitle(ByName + " sent a video to you");
					mBuilder.setTicker(ByName + " sent a video to you");
					mBuilder.setContentText(Message);
				}
				else if(fileType.contentEquals("VOICE"))
				{
					mBuilder.setContentTitle(ByName + " sent a audio file to you");
					mBuilder.setTicker(ByName + " sent a audio file to you");
					mBuilder.setContentText(Message);
				}
				else if(fileType.contentEquals("FILE"))
				{
					mBuilder.setContentTitle(ByName + " sent a file to you");
					mBuilder.setTicker(ByName + " sent a file to you");
					mBuilder.setContentText(Message);
				}
			
				mBuilder.setContentIntent(contentIntent);
				Log.d(CommonUtilities.TAG, "Message Id Notification - " + Integer.parseInt(shareID));
				mNotificationManager.notify("MESSAGE", 1, mBuilder.build());
				
				if( Global.bRun )
					GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", "REFRESH_MESSAGE^" + ByEmail);
		} 
		catch (Exception e) 
		{
			Log.d(CommonUtilities.TAG, "Message Notification Exception - " + e.toString());
		}
    }
    
    /**
     * Put the message into a notification and post it.
     * This is just one simple example of what you might choose to do with 
     * a GCM message.
     * @param msg
     * @param extras
     *  ShareID, ByEmailID , ByName, ToEmailID, NoteID, NoteTitle
     */
//    private void sendErrorNotification(String msg)
//    {
//		mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//	
//		Intent intent = new Intent(this, RetrieveNoteActivity.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//		// If this is a notification type message include the data from the message 
//		// with the intent
////		if (extras != null)
////		{
////		    intent.putExtras(extras);
////		    intent.setAction("com.zahdoo.mycadie.NOTIFICATION");
////		}
//		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent,
//			PendingIntent.FLAG_UPDATE_CURRENT);
//		
//		
//		int resId0 = 0;
//		
//		try {
//			//resId0 = getResources().getIdentifier("icon", "drawable", "air.com.zahdoo.cadie");
//			resId0 = getResources().getIdentifier("icon", "drawable", getApplicationContext().getPackageName());
//			
//			Log.d("CADIE ICON", "2 Icon Integer Value - " + resId0);
//		} catch (Exception e) {
//			Log.d("CADIE ICON", "2 Icon Integer Exception - " + e.toString());
//		}
//		
//		
//		//Bitmap bm = BitmapFactory.decodeResource(getResources(),resId0);
//	
//		
//		NotificationCompat.Builder mBuilder = 
//			new NotificationCompat.Builder(this)
//			.setSmallIcon(resId0)
//			//.setLargeIcon(bm)
//			.setContentTitle(msg)
//			//.setStyle(new NotificationCompat.BigTextStyle().bigText("Click to retrieve"))
//			.setContentText(msg)
//			.setTicker("Cadie Shared Note")
//			.setAutoCancel(true)
//			.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
//			.setVibrate(new long[]{100, 100, 100, 100});
//	
//		mBuilder.setContentIntent(contentIntent);
//		
//		Global.notificationID++;
//		
//		mNotificationManager.notify(Global.notificationID, mBuilder.build());
//    }

    /**
     * Remove the app's notification
     */
//    private void clearNotification()
//    {
//		mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//		mNotificationManager.cancel(NOTIFICATION_ID);
//    }
    
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        Date date = new Date();
        //Date fromDate = Calendar.getInstance().getTime();
        return dateFormat.format(date);
    }
    

    /**
     * Insert received Group info in BroadcastGroup table
     */
    private void InsertGroupMessageData( GroupMessageData gm )
	{
    	Log.e(CommonUtilities.TAG, " Insert Group Message DATA >> " + gm.NotificationType);
    	
    	Group_Data gd = new Group_Data();
    	
    	gd.AddGroupMessage( gm );
    	
    	Log.e(CommonUtilities.TAG, "AFTER -- gd.getGroupChatData "); 
	}

    /**
     * Insert received Group info in BroadcastGroup table
     */
    private void InsertGroupNotificationData(String notificationType, String serverGroupID)//, String GroupTitle, String ProfilePic,
    									//String ByUserID, String ByEmail, String ByName, String Message, String time,
    									//String[] arrUserID, String[] arrUserEmail, String[] arrUserName)
	{
    	
    	Log.e(CommonUtilities.TAG, " InsertGroupNotificationData >> " + notificationType);
    	Group_Data gd = new Group_Data();
    	
    	gd.getGroupChatData("0","NA", serverGroupID);
    	
    	Log.e(CommonUtilities.TAG, "AFTER -- gd.getGroupChatData "); 
    	
	}

    
    
    /**
     * Insert received notes info in ShareHistory table
     */
    private void InsertSharedNotificationData(String notificationType, String shareID, String ByEmail , String ByName, 
    		String ToEmail,String NoteID,String NoteTitle,String DbName ,String FileName ,String FileSize ,String time)
	{
    	Cursor c = null;
    	cDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
		
    	try{
			c = cDataBase.rawQuery("SELECT * FROM ShareHistory WHERE ShareID = ?" , new String [] {shareID});
			Log.d(CommonUtilities.TAG, "Cursor value -  "  + c); 
			if(c != null ) {
				if(c.moveToFirst())
				{
					
				}
				else{
		    		ContentValues values = new ContentValues();
		    		values.put("ShareHistoryID", UUID.randomUUID().toString());
		    		values.put("NotificationType", notificationType);
		    		values.put("ShareID", shareID);
		    		values.put("ByEmailID", ByEmail);
		    		
		    		values.put("ByName", ByName);
		    		values.put("ToEmailID", ToEmail);
		    		values.put("FriendsEmailID", ByEmail);
		    		values.put("FriendsName", ByName);
		    		values.put("ShareType", "RECEIVED");
		    		
		    		if(notificationType.contentEquals("NOTE"))
			    		values.put("NoteID", NoteID);
			    	else
			    		values.put("NoteID", UUID.randomUUID().toString());
		    		
		    		values.put("NoteTitle", NoteTitle);
		    		values.put("DbName", DbName);
		    		values.put("isViewed", false);
		    		values.put("isNoteViewed", false);
		    		
		    		values.put("isSynced", 0);
		    		values.put("isDeleted", 0);
		    		values.put("isNoteDeleted", false);
		    		values.put("isContactUnfriended", false);
		    		values.put("FileName", FileName);
		    		values.put("FileSize", Integer.parseInt(FileSize));
		    		//values.put("SharedOn", getDateTime());
		    		//Log.d(CommonUtilities.TAG, "SharedOn Date -  "  + getDateTime()); 
		    		values.put("SharedOn", time);
		    		
		    		cDataBase.insert("ShareHistory", null, values);
		    		values.clear();
				}
			}
		}
    	catch (Exception e)
    	{
    		Log.d(CommonUtilities.TAG,"ShareHistory Insert Error - " + e.toString()); 
		}
    	finally{
    		Log.d(CommonUtilities.TAG,"Share Info inserted.."); 
    		
    		if(c != null ) 
    			c.close();
    	}
	}

    /**
     * Insert self shared items from other logged in devices in ShareHistory table
     */
	private void InsertSelfShareHistory(String notificationType,
			String shareID, String ByEmail, String ByName, String SentToEmail,
			String NoteID, String NoteTitle, String DbName, String FileName,
			String FileSize, String SentToName, String time) {
		Cursor c = null;
		cDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null,
				SQLiteDatabase.OPEN_READWRITE);

		try {
			c = cDataBase.rawQuery(
					"SELECT * FROM ShareHistory WHERE ShareID = ?",
					new String[] { shareID });
			Log.d(CommonUtilities.TAG, "Cursor value -  " + c);
			if (c != null) {
				if (c.moveToFirst()) {
				} else {
					ContentValues values = new ContentValues();
					values.put("ShareHistoryID", UUID.randomUUID().toString());
					values.put("NotificationType", notificationType);
					values.put("ShareID", shareID);
					values.put("ByEmailID", ByEmail);

					values.put("ByName", ByName);

					values.put("ToEmailID", SentToEmail);
					values.put("FriendsEmailID", SentToEmail);
					values.put("FriendsName", SentToName);
					values.put("ShareType", "SHARED");

					if (notificationType.contentEquals("NOTE"))
						values.put("NoteID", NoteID);
					else
						values.put("NoteID", UUID.randomUUID().toString());

					values.put("NoteTitle", NoteTitle);
					values.put("DbName", DbName);
					values.put("isViewed", false);
					values.put("isNoteViewed", false);

					values.put("isSynced", 0);
					values.put("isDeleted", 0);
					values.put("isNoteDeleted", false);
					values.put("isContactUnfriended", false);
					values.put("FileName", FileName);
					values.put("FileSize", Integer.parseInt(FileSize));
					// values.put("SharedOn", getDateTime());
					values.put("SharedOn", time);

					cDataBase.insert("ShareHistory", null, values);
					values.clear();

					if (Global.bRun) {
						if (notificationType.contentEquals("MESSAGE"))
							GCMInitFunction.cGCM.dispatchStatusEventAsync(
									"RETRIEVE_NOTE", "REFRESH_MESSAGE^"
											+ SentToEmail);
						else if (notificationType
								.contentEquals("CONTACT_REQUEST_MESSAGE"))
							GCMInitFunction.cGCM.dispatchStatusEventAsync(
									"RETRIEVE_NOTE",
									"REFRESH_CONTACTS_INVITATION^"
											+ SentToEmail);
						else if (notificationType.contentEquals("NOTE"))
							GCMInitFunction.cGCM.dispatchStatusEventAsync(
									"RETRIEVE_NOTE", "REFRESH_SHARED_NOTES^"
											+ SentToEmail);
						else if (notificationType.contentEquals("PICTURE")
								|| notificationType.contentEquals("VIDEO")
								|| notificationType.contentEquals("VOICE")
								|| notificationType.contentEquals("FILE"))
							GCMInitFunction.cGCM.dispatchStatusEventAsync(
									"RETRIEVE_NOTE", "REFRESH_MESSAGE^"
											+ SentToEmail);
					}
				}
			}
		} catch (Exception e) {
			Log.d(CommonUtilities.TAG,
					"ShareHistory Insert Error - " + e.toString());
		} finally {
			Log.d(CommonUtilities.TAG, "Share Info inserted..");

			if (c != null)
				c.close();

			if (cDataBase.isOpen())
				cDataBase.close();
		}
	}
    
//    private void DownloadSharedDb( String filePath ,String fileName )
//    {
//    	URL url = null;
//    	
//        //URLConnection con = null;
//    	HttpURLConnection con = null;
//
//        try {
//
//        	//fileName = "A72C7C5B-3C02-13D5-8C79-FFA0D509126BcadieShareDB.sqlite";
//        	String urlpath = "http://65.182.104.219/CadieUpload/"+ filePath.toString() +"/"+ fileName.toString();
//
//        	
//            url = new URL(urlpath.toString());
//
//            con = (HttpURLConnection)url.openConnection();
//
//            
//            String PATH = Environment.getExternalStorageDirectory() + "/Cadie/frmServer";
//            
//            File file = new File(PATH); // PATH = /mnt/sdcard/download/
//            
//            if (!file.exists()) {
//                file.mkdirs();
//            }
//            
//            if (!file.exists()) {
//            	file.getParentFile().mkdirs();
//            }
//           
//            File outputFile = new File(file,fileName);  
//            FileOutputStream fos = new FileOutputStream(outputFile);
//
//            con.setRequestMethod("GET");
//            con.setDoOutput(false);
//            con.setChunkedStreamingMode(0);
//            con.connect();
//            
//            int status = con.getResponseCode();
//            
//            Log.d(CommonUtilities.TAG , "11 File Status  " + status);
//            
//            BufferedInputStream bis = new BufferedInputStream(
//
//                    con.getInputStream());
//            
//            Log.d(CommonUtilities.TAG , "22 InputStream - " + bis.toString() );
//            
//            byte[] buffer = new byte[1024];
//            int len1 = 0;
//            while ((len1 = bis.read(buffer)) != -1) {
//                fos.write(buffer, 0, len1); // Write In FileOutputStream.
//            }
//            
//            Log.d(CommonUtilities.TAG , "File downloaded to device  ");
//            
//            
//            fos.flush();
//            fos.close();
//            bis.close();
//
//        } catch (MalformedInputException miex) {
//        	Log.d(CommonUtilities.TAG , " File Exception  - " + miex.toString() );
//        	
//
//        } catch (IOException ioException) {
//
//        	Log.d(CommonUtilities.TAG , " File IOException  - " + ioException.toString() );
//
//        }
//        finally{
//        	if (con != null)
//            	con.disconnect();
//        }
//    }
    
    
////////////////////////////////////////////////////////////////////   
    
//    private void DownloadSharedDb2( String filePath ,String fileName )
//    {
//        try{
//        	//887F9EE6-C990-669C-B2E2-FF2F22FA018B1395850186828.docx
//        	//6E53CB40-FE0D-6704-AE67-FEF9175C678B.jpg
//        	fileName = "887F9EE6-C990-669C-B2E2-FF2F22FA018B1395850186828.docx";
//        	String urlpath = "http://65.182.104.219/CadieUpload/"+ filePath.toString() +"/"+ fileName.toString();
////        	StringBuilder b = new StringBuilder("http://65.182.104.219/CadieUpload/");
////        	b.append(filePath);
////        	b.append("/");
////        	b.append(fileName);
//        	
//        	//String urlpath = "http://65.182.104.219/CadieUpload/Nexus@gmail.Com/6E53CB40-FE0D-6704-AE67-FEF9175C678BcadieShareDB";
//
//        	Log.d(CommonUtilities.TAG , "Server File Path - " + urlpath.toString());
//            URL url = new URL(urlpath.toString()); // Your given URL.
//
//            URLConnection c = url.openConnection();
//            String PATH = Environment.getExternalStorageDirectory() + "/Cadie/frmServer";
//            
//            Log.d(CommonUtilities.TAG , "Device File Path - " + PATH);
//            File file = new File(PATH); // PATH = /mnt/sdcard/download/
//            
//            
//            if (!file.exists()) {
//                file.mkdirs();
//            }
//            
//            if (!file.exists()) {
//            	file.getParentFile().mkdirs();
//            }
//           
//            File outputFile = new File(file,fileName);           
//            FileOutputStream fos = new FileOutputStream(outputFile);
//
//            Log.d(CommonUtilities.TAG , "About to connect " );
//            c.connect();
//            
//            Log.d(CommonUtilities.TAG , "Connected " );
//            
//            //InputStream is = c.getInputStream(); // Get from Server and Catch In Input Stream Object.
//
//            
//            int maxBufferSize = 1*1024*1024;
//           InputStream is = new BufferedInputStream(url.openStream(), maxBufferSize); 
//            
//            Log.d(CommonUtilities.TAG , "InputStream - " + is.toString() );
//            
//            byte[] buffer = new byte[1024];
//            int len1 = 0;
//            while ((len1 = is.read(buffer)) != -1) {
//                fos.write(buffer, 0, len1); // Write In FileOutputStream.
//            }
//            
//            Log.d(CommonUtilities.TAG , "File downloaded to device  ");
//            
//            fos.flush();
//            fos.close();
//            is.close();
//        } 
//        catch (IOException e) 
//        {
//        	Log.d(CommonUtilities.TAG , "11 File Download ERROR - " + e.toString());
//        	
//        	Log.d(CommonUtilities.TAG , "22 File Download ERROR - " + e.getLocalizedMessage());
//        	
//        	Log.d(CommonUtilities.TAG , "33 File Download ERROR - " + e.getMessage());
//        	
//        	if(e.toString().contains("Network is unreachable")){
//        		//alertbox("CADIE Error","No network found, please check your wifi connection and try again.");
//        	}
//        	else if(e.toString().contains("java.io.FileNotFoundException")){
//        		//alertbox("CADIE Error","Unable to save the file on device or SD card.");
//        	}
//        }           
//    }

///////////////////////////////////////////////////////////////        
    
//    private void DownloadSharedDb( String filePath ,String fileName )
//    {
//    	
//    	final int BUFFER_SIZE = 4096 ;
//    	fileName = "887F9EE6-C990-669C-B2E2-FF2F22FA018B1395850186828.docx";
//    	
//        //long startTime = System.currentTimeMillis() ;
////        String ftpUrl = ftp://**username**:**password**@filePath ;
////        String file= "filename" ; // name of the file which has to be download
////        String host = host_name ; //ftp server
////        String user = "username" ; //user name of the ftp server
////        String pass = "**password" ; // password of the ftp server
// 
////        String savePath = "c:\\" ;
////        ftpUrl = String.format(ftpUrl, user, pass, host) ;
////        System.out.println("Connecting to FTP server") ;
//        
//        StringBuilder b = new StringBuilder("http://65.182.104.219/CadieUpload/");
//    	b.append(filePath);
//    	b.append("/");
//    	b.append(fileName);
// 
//        try{
//            URL url = new URL(b.toString()) ;
//            URLConnection conn = url.openConnection() ;
//            
//            Log.d(CommonUtilities.TAG , "11 File  - " );
//        	
//            long filesize = conn.getContentLength() ;
//            
//            Log.d(CommonUtilities.TAG , "22 File length - " + filesize );
//            
//            InputStream inputStream = conn.getInputStream() ;
//            Log.d(CommonUtilities.TAG , "33 File  - " );
//            
//            System.out.println("Size of the file to download in kb is:-" + filesize/1024 ) ;
// 
//            String PATH = Environment.getExternalStorageDirectory() + "/Cadie/frmServer";
//            
//            FileOutputStream outputStream = new FileOutputStream(PATH) ;
// 
//            byte[] buffer = new byte[BUFFER_SIZE] ;
//            int bytesRead = -1 ;
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                outputStream.write(buffer, 0, bytesRead) ;
//            }
////            long endTime = System.currentTimeMillis() ;
////            System.out.println("File downloaded") ;
////            System.out.println("Download time in sec. is:-" + (endTime-startTime)/1000)  ;
//            outputStream.close() ;
//            inputStream.close() ;
//        }
//        catch (IOException ex){
//        	Log.d(CommonUtilities.TAG , "11 File Download ERROR - " + ex.toString());
//        	
//            ex.printStackTrace() ;
//        }
//    }

    
    /////////////////////////////
    
    private void UpdateConfigKeyValue(String EmailId, String FriendUserName, String FriendStatusMsg, String FriendDateOfBirth, String FriendPhoneNo ,
    		String FriendLocation, String FriendProfilePicName, String IsFriendProfilePicChanged, String IsFriendProfileNameChanged ,String time)
	{
    	try 
    	{
    		cDataBase = SQLiteDatabase.openDatabase(cDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);

    		//NAME
    		ContentValues values = new ContentValues();                          
    		values.put("Value", FriendUserName);
    		//values.put("LastModifiedDateTime", getDateTime());
    		values.put("LastModifiedDateTime", time);
    		
    		String where = "Key = ?";  
    		final String[] whereArgs = { "GCMRegisteredUsername" };
    		cDataBase.update("Config", values, where, whereArgs);
    		values.clear();
    		
    		
    		//STATUS
    		values = new ContentValues();                          
    		values.put("Value", FriendStatusMsg);
    		//values.put("LastModifiedDateTime", getDateTime());
    		values.put("LastModifiedDateTime", time);
    		
    		where = "Key = ?";  
    		final String[] whereArgs1 = { "UserStatus" };
    		cDataBase.update("Config", values, where, whereArgs1);
    		values.clear();
    		
    		
    		//DOB
    		if(!FriendDateOfBirth.contentEquals(""))
    		{
    			values = new ContentValues();                          
        		values.put("Value", FriendDateOfBirth);
        		//values.put("LastModifiedDateTime", getDateTime());
        		values.put("LastModifiedDateTime", time);
        		
        		where = "Key = ?";  
        		final String[] whereArgs2 = { "UserDOB" };
        		cDataBase.update("Config", values, where, whereArgs2);
        		values.clear();
    		}
    		
    		//PHONE_NO
    		if(!FriendPhoneNo.contentEquals(""))
    		{
    			values = new ContentValues();                          
        		values.put("Value", FriendPhoneNo);
        		//values.put("LastModifiedDateTime", getDateTime());
        		values.put("LastModifiedDateTime", time);
        		
        		where = "Key = ?";  
        		final String[] whereArgs3 = { "UserPhoneNo" };
        		cDataBase.update("Config", values, where, whereArgs3);
        		values.clear();
    		}
    		
    		//LOCATION
    		if(!FriendLocation.contentEquals(""))
    		{
    			values = new ContentValues();                          
        		values.put("Value", FriendLocation);
        		//values.put("LastModifiedDateTime", getDateTime());
        		values.put("LastModifiedDateTime", time);
        		
        		where = "Key = ?";  
        		final String[] whereArgs4 = { "UserLocation" };
        		cDataBase.update("Config", values, where, whereArgs4);
        		values.clear();
    		}
    		
    		//USER_PIC_NAME
    		if(!FriendProfilePicName.contentEquals(""))
    		{
    			values = new ContentValues();                          
        		values.put("Value", FriendProfilePicName);
        		//values.put("LastModifiedDateTime", getDateTime());
        		values.put("LastModifiedDateTime", time);
        		
        		where = "Key = ?";  
        		final String[] whereArgs5 = { "UserPicName" };
        		cDataBase.update("Config", values, where, whereArgs5);
        		values.clear();
    		}
    	}
    	catch (Exception e)
    	{
    		Log.d(CommonUtilities.TAG,"updateFriendProfileInfo Update Error"); 
		}
    	finally{
    		Log.d(CommonUtilities.TAG,"updateFriendProfileInfo updated.."); 
    		
    		if(cDataBase.isOpen())
    			cDataBase.close();
    		
    		if(IsFriendProfilePicChanged.contentEquals("YES"))
    		{
    			Log.d(CommonUtilities.TAG,"IsFriendProfilePicChanged YES..");
    			startFriendsProfilePicDownload(EmailId,FriendProfilePicName ,true ,FriendUserName);
    		}
    		else
    		{
    			Log.d(CommonUtilities.TAG,"IsFriendProfilePicChanged NO..");
    			if( Global.bRun )
        			GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", "FRIEND_PROFILE_CHANGED^" + EmailId + "^" + FriendUserName);
    		}
    	}
	}
    
    /////////////////////////////
    private void deleteUserChatHistory(String unfriendSelfEmail, String unfriendedEmailId)
	{
    	try{
    		cDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);

    		String where = "(ByEmailID = ? OR ByEmailID = ? ) " +
				"AND (ToEmailID = ? OR ToEmailID = ? ) " +
				"AND (ToEmailID <> ByEmailID) ";// +
			//	"AND isSynced = 0 ";
    		
    		final String[] whereArgs = { unfriendSelfEmail,unfriendedEmailId ,unfriendSelfEmail,unfriendedEmailId};
			cDataBase.delete("ShareHistory",  where, whereArgs);
			
			
			///
//			ContentValues values = new ContentValues();                          
//    		values.put("isSynced", 0);
//    		values.put("isDeleted", 1);
//    		values.put("isContactUnfriended", true);
//    		
//			String where1 = "(ByEmailID = ? OR ByEmailID = ? ) " +
//					"AND (ToEmailID = ? OR ToEmailID = ? ) " +
//					"AND (ToEmailID <> ByEmailID) " +
//					"AND isSynced > 0 ";  
//			
//			final String[] whereArgs1 = { unfriendSelfEmail,unfriendedEmailId ,unfriendSelfEmail,unfriendedEmailId };
//			cDataBase.update("ShareHistory", values, where1, whereArgs1);
//			
//			values.clear();
			
			///
			//cDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    		Log.d(CommonUtilities.TAG, "Inside Insert ContactRequests  " ); 
    		
			String where3 = "EmailID = ?";
    		final String[] whereArgs3 = { unfriendedEmailId };
			cDataBase.delete("CADIEContacts",  where3, whereArgs3);

			//sql = "Delete from CADIEContacts WHERE EmailID = ?";
			
			if( Global.bRun )
    			GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", "REFRESH_CONTACT_UNFRIEND_SELF" );
		
		}
    	catch (Exception e)
    	{
    		Log.d(CommonUtilities.TAG,"deleteUserChatHistory Error - " + e.toString()); 
    		
		}
    	finally{
    		
    		if(cDataBase.isOpen())
    			cDataBase.close();
    	}
	}
    
    /////////////////////////////
    /**
     * Insert invitation info from self email id other logged devices
     */
    private void insertCadieInvitationData(String ServerReqInviteId, String InvitationMessage, String InvitationToEmail,
			String InvitationToName , String InvitedByEmail, String InvitedByName , String time)
    {
    	Cursor c = null;
    	
    	try{
    		cDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);

			ContentValues values = new ContentValues();
    		values.put("ServerRequestID", Integer.parseInt(ServerReqInviteId));
    		values.put("Message", InvitationMessage);
    		values.put("FromEmailID", InvitedByEmail);
    		values.put("FromUserName", InvitedByName);
    		values.put("ToEmailID", InvitationToEmail);
    		values.put("ToUserName", InvitationToName);
    		values.put("InviteRequestType", "INVITATION");
    		values.put("RequestType", "INVITATION");
    		values.put("Status", 0);
    		//values.put("OnDateTime", getDateTime());
    		values.put("OnDateTime", time);
    		
    		cDataBase.insert("CADIEInvitesRequests", null, values);
    		values.clear();
		}
    	catch (Exception e)
    	{
    		Log.d(CommonUtilities.TAG,"CadieInvitationData Insert Error"); 
		}
    	finally
    	{
    		//Log.d(CommonUtilities.TAG,"insertCadieInvitationData inserted.."); 
    		
    		if(c != null )
    			c.close();
    		
    		if(cDataBase.isOpen())
    			cDataBase.close();
    	}
	}
    
    /////////////////////////////
    
    /**
     * Insert received notes info in ShareHistory table
     */
    private void InsertDropboxKey(String reqKey, String reqSecret, String acckey, String accSecret)
	{
    	Cursor c = null;
    	
    	try{
    		cDataBase = SQLiteDatabase.openDatabase(cDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);

    		Log.d(CommonUtilities.TAG, "Inside InsertDropbox  " ); 
    		
			c = cDataBase.rawQuery("SELECT * FROM DropboxInfo" , null);
			Log.d(CommonUtilities.TAG, "Cursor value -  "  + c); 
			if(c != null ) {
				if(c.moveToFirst())
				{
					
				}
				else{
					ContentValues values = new ContentValues();
		    		values.put("UserID", 1);
		    		values.put("Request_Key", reqKey);
		    		values.put("Request_Secret", reqSecret);
		    		values.put("Access_Key", acckey);
		    		values.put("Access_Secret", accSecret);
		    		
		    		cDataBase.insert("DropboxInfo", null, values);
		    		values.clear();
				}
			}
			
		}
    	catch (Exception e)
    	{
    		Log.d(CommonUtilities.TAG,"DropboxInfo Insert Error"); 
    		
		}
    	finally{
    		Log.d(CommonUtilities.TAG,"DropboxInfo inserted.."); 
    		
    		if(c != null )
    			c.close();
    		
    		if(cDataBase.isOpen())
    			cDataBase.close();
    		Log.d(CommonUtilities.TAG," Global Run - " + Global.bRun ); 
    		
    		if( Global.bRun )
    			GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", "DROPBOX_LINKED");
    	}
    	
	}
    
    
    /**
     * Insert contact request info in CADIERequests table
     */
    
    private void InsertContactRequestData(String reqFromEmail, String reqFromName, String reqMsg, String reqToEmail , String requestShareId,
    		String serverReqId , String ToUserName ,Bundle extras , Boolean isSelfNotification , String time)
	{
    	try{
    		cDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);

    		Log.d(CommonUtilities.TAG, "Inside Insert ContactRequests  " ); 
    		
			ContentValues values = new ContentValues();
    		values.put("FromEmailID", reqFromEmail);
    		values.put("FromUserName", reqFromName);
    		
    		if(isSelfNotification)
    			values.put("RequestType", "SENT");
    		else
    			values.put("RequestType", "RECEIVED");
    		
    		values.put("InviteRequestType", "REQUEST");
    		values.put("Message", reqMsg);
    		values.put("Status", 0);
    		values.put("ToEmailID", reqToEmail);
    		values.put("ToUserName", ToUserName);
    		values.put("ServerRequestID", Integer.parseInt(serverReqId));
    		
    		Log.d(CommonUtilities.TAG," Server Request ID - " + serverReqId);
    		
    		cDataBase.insert("CADIEInvitesRequests", null, values);
    		//cDataBase = SQLiteDatabase.openDatabase(cDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);

    		values = new ContentValues();
    		values.put("ShareHistoryID", UUID.randomUUID().toString());
    		values.put("NotificationType", "CONTACT_REQUEST_MESSAGE");
    		values.put("ShareID", requestShareId);
    		values.put("ByEmailID", reqFromEmail);
    		
    		values.put("ByName", reqFromName);
    		values.put("ToEmailID", reqToEmail);
    		values.put("FriendsEmailID", reqFromEmail);
    		values.put("FriendsName", reqFromName);
    		
    		if(isSelfNotification)
    			values.put("ShareType", "SHARED");
    		else
    			values.put("ShareType", "RECEIVED");
    		
    		values.put("NoteID", UUID.randomUUID().toString());
    		values.put("NoteTitle", reqMsg);
    		values.put("DbName", "");
    		values.put("isViewed", true);
    		values.put("isNoteViewed", false);
    		
    		values.put("isSynced", 0);
    		values.put("isDeleted", 0);
    		values.put("isNoteDeleted", false);
    		values.put("isContactUnfriended", false);
    		values.put("FileName", "");
    		values.put("FileSize", 0);
    		//values.put("SharedOn", getDateTime());
    		values.put("SharedOn", time);
    		
    		cDataBase.insert("ShareHistory", null, values);
    		values.clear();
    		
    		if(!isSelfNotification)
    			contactRequestNotification(reqFromEmail, reqFromName ,reqMsg , reqToEmail , serverReqId , extras);
    		else
    		{
    			if( Global.bRun )
        			GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", "REFRESH_CONTACTS^" + reqToEmail);
    		}
		}
    	catch (Exception e)
    	{
    		Log.d(CommonUtilities.TAG,"ContactRequests Insert Error"); 
		}
    	finally{
    		Log.d(CommonUtilities.TAG,"ContactRequests inserted.."); 
    		
    		if(cDataBase.isOpen())
    			cDataBase.close();
    		
    		Log.d(CommonUtilities.TAG," Global Run - " + Global.bRun ); 
    	}
	}
    
    /**
     * Put the message into a notification and post it.
     * This is just one simple example of what you might choose to do with 
     * a GCM message.
     * @param msg
     * @param extras
     *  ShareID, ByEmailID , ByName, ToEmailID, NoteID, NoteTitle
     */
    private void contactRequestNotification(String reqFromEmail, String reqFromName, String reqMsg, String reqToEmail , String serverReqId , Bundle extras)
    {
    	try {
			Log.d(CommonUtilities.TAG , "GET MESSAGE COUNT   " );
			Cursor c = null;
			int reqstCount	= 0;
			
			try{
				cDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
				c = cDataBase.rawQuery("SELECT * FROM CADIEInvitesRequests WHERE RequestType = 'RECEIVED' AND InviteRequestType = 'REQUEST' GROUP BY FromEmailID" , null);
				
				if(c != null ) {
					if(c.moveToFirst())
					{
						do {
							reqstCount++;
							Log.d(CommonUtilities.TAG , "RequestCount - " + reqstCount);
							
				        }while (c.moveToNext());
					}
				}
			}
			catch (Exception e)
			{
				Log.d(CommonUtilities.TAG,"ShareHistory Insert Error - " + e.toString()); 
			}
			finally{
				Log.d(CommonUtilities.TAG,"Share Info inserted.."); 
				
				if(c != null ) 
					c.close();
				
				if(cDataBase.isOpen())
					cDataBase.close();
			}
			
    		mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
    		
    		Intent intent = new Intent(this, RetrieveNoteService.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    		// If this is a notification type message include the data from the message 
    		// with the intent
    		if (extras != null)
    		{
    	       intent.putExtras(extras);
    	       intent.setData((Uri.parse("custom://CONTACT_REQUEST")));
    	    }
    		
    		PendingIntent contentIntent = PendingIntent.getService(this, 0, intent,
    			PendingIntent.FLAG_UPDATE_CURRENT);
    		
    		int resId0 = 0;
    		
    		try {
    			resId0 = getResources().getIdentifier("icon", "drawable", getApplicationContext().getPackageName());
    			Log.d("CADIE ICON", "2 Icon Integer Value - " + resId0);
    		} catch (Exception e) {
    			Log.d("CADIE ICON", "2 Icon Integer Exception - " + e.toString());
    		}
    		
    		//Bitmap bm = BitmapFactory.decodeResource(getResources(), resId0);
    	
    		NotificationCompat.Builder mBuilder = 
    			new NotificationCompat.Builder(this)
    			.setSmallIcon(resId0)
    			//.setLargeIcon(bm)
    			.setContentTitle(reqFromName + " wants to be your friend")
    			//.setStyle(new NotificationCompat.BigTextStyle().bigText("Click to retrieve"))
    			.setContentText(reqMsg )
    			.setTicker("Cadie Buddy Request - " + reqFromName)
    			.setAutoCancel(true)
    			.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
    			.setVibrate(new long[]{100, 100, 100, 100});
    		
    		if(reqstCount > 1)
    			mBuilder.setContentText("You have " + reqstCount + " Cadie buddy requests");
    	
    		mBuilder.setContentIntent(contentIntent);
    		
    		Log.d(CommonUtilities.TAG, "Share Id Notification - " + Integer.parseInt(serverReqId));
    		
    		//mNotificationManager.notify(Integer.parseInt(serverReqId), mBuilder.build());
    		mNotificationManager.notify("CONTACT_REQUEST", 1, mBuilder.build());
    		
    		if( Global.bRun )
    			GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", "REFRESH_CONTACTS^" + reqFromEmail);
		} catch (Exception e) {
			Log.d(CommonUtilities.TAG, "Notification Exception - " + e.toString());
		}
    }
    
    /**
     * Delete CADIE request on getting request response
     */
    private void DeleteCadieRequests(String FromEmailId , String ToEmailId)//ServerRequestId )
	{
    	try{
    		cDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);

    		//String where = "ServerRequestID = ? ";
    		String where = "FromEmailID = ? AND ToEmailID = ?";
			//final String[] whereArgs = { ServerRequestId };
    		final String[] whereArgs = { FromEmailId,ToEmailId };
			int x ;
			x = cDataBase.delete("CADIEInvitesRequests",  where, whereArgs);
		
			Log.d(CommonUtilities.TAG,"CadieRequests delete X Value - " + x ); 
		}
    	catch (Exception e)
    	{
    		Log.d(CommonUtilities.TAG,"CadieRequests delete Error"); 
    		
		}
    	finally{
    		Log.d(CommonUtilities.TAG,"CadieRequests row deleted.."); 
    		
    		if(cDataBase.isOpen())
    			cDataBase.close();
    	}
	}
    
    /**
     * Insert contact request info in CADIEContacts table
     */
    
    private void InsertCadieContacts(String AcceptedByUserId, String AcceptedByEmailId, String AcceptedByName,String sharedID, String toEmail,
    		String AcceptedByPhoneNo,String AcceptedByProfileMessage,String AcceptedByDOB,String AcceptedByLocation,String AcceptedByProfilePicName,Bundle extras , Boolean isSelfNotification , String time)
	{
    	try{
    		cDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    		Log.d(CommonUtilities.TAG, "Inside Insert ContactRequests  " ); 
    		
			String where = "EmailID = ?";
    		final String[] whereArgs = { AcceptedByEmailId};
			cDataBase.delete("CADIEContacts",  where, whereArgs);
			
			ContentValues values = new ContentValues();
    		values.put("FriendsUserID", AcceptedByUserId);
    		values.put("EmailID", AcceptedByEmailId);
    		values.put("Name", AcceptedByName);
    		values.put("Status", 1);
    		//values.put("AddedOn", getDateTime());
    		values.put("AddedOn", time);
    		
    		values.put("PhoneNo", AcceptedByPhoneNo);
    		values.put("ProfileMessage", AcceptedByProfileMessage);
    		values.put("DateOfBirth", AcceptedByDOB);
    		values.put("Location", AcceptedByLocation);
    		values.put("ProfilePicName", AcceptedByProfilePicName);
    		
    		values.put("isActive", 1);
    		//values.put("LastActivityOn", getDateTime());
    		values.put("LastActivityOn", time);
    		
    		cDataBase.insert("CADIEContacts", null, values);
    		values.clear();
    		
    		//cDataBase = SQLiteDatabase.openDatabase(cDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);

    		values = new ContentValues();
    		values.put("ShareHistoryID", UUID.randomUUID().toString());
    		values.put("NotificationType", "CONTACT_ACCEPT_MESSAGE");
    		values.put("ShareID", sharedID);
    		values.put("ByEmailID", AcceptedByEmailId);
    		
    		values.put("ByName", AcceptedByName);
    		values.put("ToEmailID", toEmail);
    		values.put("FriendsEmailID", AcceptedByEmailId);
    		values.put("FriendsName", AcceptedByName);

    		if(isSelfNotification)
    			values.put("ShareType", "SHARED");
    		else
    			values.put("ShareType", "RECEIVED");
    		
    		values.put("NoteID", UUID.randomUUID().toString());
    		values.put("NoteTitle", "Cadie Buddy Request Accepted");
    		values.put("DbName", "");
    		
    		if(isSelfNotification)
    			values.put("isViewed", true);
    		else
    			values.put("isViewed", false);
    		
    		values.put("isNoteViewed", false);
    		
    		values.put("isSynced", 0);
    		values.put("isDeleted", 0);
    		values.put("isNoteDeleted", false);
    		values.put("isContactUnfriended", false);
    		values.put("FileName", "");
    		values.put("FileSize", 0);
    		//values.put("SharedOn", getDateTime());
    		values.put("SharedOn", time);
    		
    		cDataBase.insert("ShareHistory", null, values);
    		
    		values.clear();
    		
    		values = new ContentValues();                          
    		values.put("isContactUnfriended", false);
			String where1 = "FriendsEmailID = ?";  
			final String[] whereArgs1 = { AcceptedByEmailId };
			cDataBase.update("ShareHistory", values, where1, whereArgs1);
			
			values.clear();
			
			startFriendsProfilePicDownloadOnRequestAccept(AcceptedByEmailId);
			
			if(!isSelfNotification)
	    		requestAcceptDeclinedNotification(AcceptedByName, AcceptedByEmailId , "REQUEST_ACCEPTED", extras);
			else
			{
				if( Global.bRun )
	    			GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", "REFRESH_CONTACTS^" + AcceptedByEmailId );
			}
		}
    	catch (Exception e)
    	{
    		Log.d(CommonUtilities.TAG,"ContactRequests Insert Error"); 
		}
    	finally{
    		Log.d(CommonUtilities.TAG,"ContactRequests inserted.."); 
    		
    		if(cDataBase.isOpen())
    			cDataBase.close();
    	}
	}
    
    /**
     * Put the message into a notification and post it.
     * This is just one simple example of what you might choose to do with 
     * a GCM message.
     * @param msg
     * @param extras
     *  ShareID, ByEmailID , ByName, ToEmailID, NoteID, NoteTitle
     */
    private void requestAcceptDeclinedNotification(String acceptedByName, String acceptedByEmailId, String reqResponseType, Bundle extras)
    {
    	try {
    		mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
    		
    		Intent intent = new Intent(this, RetrieveNoteService.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    		// If this is a notification type message include the data from the message 
    		// with the intent
    		
    		if (extras != null)
    	    {
    	       intent.putExtras(extras);
    	       intent.setData((Uri.parse("custom://CONTACT_REQUEST_ACCEPTED")));
    	    }
    		
    		PendingIntent contentIntent = PendingIntent.getService(this, 0, intent,
        			PendingIntent.FLAG_UPDATE_CURRENT);
        		
    		
    		int resId0 = 0;
    		
    		try {
    			resId0 = getResources().getIdentifier("icon", "drawable", getApplicationContext().getPackageName());
    			
    			//Log.d("CADIE ICON", "2 Icon Integer Value - " + resId0);
    		} catch (Exception e) {
    			//Log.d("CADIE ICON", "2 Icon Integer Exception - " + e.toString());
    		}
    		
    		//Bitmap bm = BitmapFactory.decodeResource(getResources(), resId0);
    		
    		NotificationCompat.Builder mBuilder = 
    			new NotificationCompat.Builder(this)
    			.setSmallIcon(resId0)
    			//.setLargeIcon(bm)
    			.setContentTitle(acceptedByName + " accepted your Cadie buddy request")
    			//.setStyle(new NotificationCompat.BigTextStyle().bigText("Click to retrieve"))
    			.setContentText(acceptedByEmailId )
    			.setTicker("Cadie Buddy Request Accepted - " + acceptedByName)
    			.setAutoCancel(true)
    			.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
    			.setVibrate(new long[]{100, 100, 100, 100});
    		
    		
    		if(reqResponseType.contentEquals("REQUEST_DECLINED"))
    		{
    			mBuilder.setContentTitle(acceptedByName + " declined your Cadie buddy request");
    			mBuilder.setTicker("Cadie Buddy Request declined - " + acceptedByName);
    		}
    		
    	
    		mBuilder.setContentIntent(contentIntent);
    		
    		//Global.notificationID++;
    		
    		//Log.d(CommonUtilities.TAG, "Share Id Notification - " + Integer.parseInt(intent.getExtras().getString("ServerReqId")));
    		//CONTACT_REQUEST_ACCEPTED
    		//mNotificationManager.notify(Global.notificationID, mBuilder.build());
    		mNotificationManager.notify("CONTACT_REQUEST_ACCEPTED",1, mBuilder.build());
    		
    		if( Global.bRun )
    			GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", "REFRESH_CONTACTS^" + acceptedByEmailId );
		} catch (Exception e) {
			Log.d(CommonUtilities.TAG, "Notification Exception - " + e.toString());
		}
    }
    
    
    /**
     * Update contact request status = 2 to denote that contact request has been declined
     */
    private void UpdateCadieRequests(String acceptedByName, String acceptedByEmailId,String ServerRequestId , Bundle extras)
	{
    	try{
    		cDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    		Log.d(CommonUtilities.TAG, "Inside update ContactRequests  " ); 
    		
    		ContentValues dataToUpdate = new ContentValues();                          
			dataToUpdate.put("Status", 2);
			String where = "ServerRequestID=?";  
			final String[] whereArgs = { ServerRequestId };
			cDataBase.update("CADIEInvitesRequests", dataToUpdate, where, whereArgs);
					
			dataToUpdate.clear();
		    requestAcceptDeclinedNotification(acceptedByName, acceptedByEmailId , "REQUEST_DECLINED" , extras);
		}
    	catch (Exception e)
    	{
    		Log.d(CommonUtilities.TAG,"ContactRequests Insert Error"); 
		}
    	finally{
    		Log.d(CommonUtilities.TAG,"ContactRequests updated.."); 
    		
    		if(cDataBase.isOpen())
    			cDataBase.close();
    	}
	}
	
    /**
     * Update cadie contact status = 2 to denote that contact has been unfriended
     */

    private void updateUnfriendContact(String unfriendFromEmail ,String unfriendFromName ,String toEmail , Bundle extras , String time )
	{
    	try{
    		cDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);

    		ContentValues values = new ContentValues();                          
    		values.put("Status", 2);
			String where = "EmailID = ?";  
			final String[] whereArgs = { unfriendFromEmail };
			int numRowAffected = cDataBase.update("CADIEContacts", values, where, whereArgs);
			
			Log.d(CommonUtilities.TAG, "Inside updateUnfriendContact - " + numRowAffected );
			values.clear();
			
			//////////////////////////////
			//cDataBase = SQLiteDatabase.openDatabase(cDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);

    		values = new ContentValues();
    		values.put("ShareHistoryID", UUID.randomUUID().toString());
    		values.put("NotificationType", "CONTACT_UNFRIEND");
    		values.put("ShareID", 1000000);
    		values.put("ByEmailID", unfriendFromEmail);
    		
    		values.put("ByName", unfriendFromName);
    		values.put("ToEmailID", toEmail);
    		values.put("FriendsEmailID", unfriendFromEmail);
    		values.put("FriendsName", unfriendFromName);
    		values.put("ShareType", "RECEIVED");
    		
    		values.put("NoteID", UUID.randomUUID().toString());
    		values.put("NoteTitle", "Cadie buddy unfriended");
    		values.put("DbName", "");
    		values.put("isViewed", false);
    		values.put("isNoteViewed", false);
    		
    		values.put("isSynced", 0);
    		values.put("isDeleted", 0);
    		values.put("isNoteDeleted", false);
    		values.put("isContactUnfriended", false);
    		values.put("FileName", "");
    		values.put("FileSize ", 0);
    		//values.put("SharedOn", getDateTime());
    		values.put("SharedOn", time);
    		
    		cDataBase.insert("ShareHistory", null, values);
    		
    		values.clear();
    		
    		values = new ContentValues();                          
    		values.put("isContactUnfriended", true);
			String where1 = "FriendsEmailID = ?";  
			final String[] whereArgs1 = { unfriendFromEmail };
			int numRowAffected1 = cDataBase.update("ShareHistory", values, where1, whereArgs1);
			
			Log.d(CommonUtilities.TAG, "2 Inside updateUnfriendContact - " + numRowAffected1 );
			values.clear();
		    contactUnfriendNotification(unfriendFromName, unfriendFromEmail , "CONTACT_UNFRIEND" , extras);
		}
    	catch (Exception e)
    	{
    		Log.d(CommonUtilities.TAG,"ContactRequests Insert Error"); 
		}
    	finally{
    		Log.d(CommonUtilities.TAG,"ContactRequests updated.."); 
    		
    		if(cDataBase.isOpen())
    			cDataBase.close();
    	}
	}
    
    /////////////////
    private void contactUnfriendNotification(String unfriendFromName, String unfriendFromEmail, String reqResponseType, Bundle extras)
    {
    	try {
    		mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
    		Intent intent = new Intent(this, RetrieveNoteService.class).setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

    		if (extras != null)
    		{
    	          intent.putExtras(extras);
    	          intent.setData((Uri.parse("custom://CONTACT_UNFRIEND")));
    	    }
    		
    		PendingIntent contentIntent = PendingIntent.getService(this, 0, intent,
    			PendingIntent.FLAG_UPDATE_CURRENT);
    		
    		int resId0 = 0;
    		
    		try {
    			resId0 = getResources().getIdentifier("icon", "drawable", getApplicationContext().getPackageName());
    			
    			Log.d("CADIE ICON", "2 Icon Integer Value - " + resId0);
    		} catch (Exception e) {
    			Log.d("CADIE ICON", "2 Icon Integer Exception - " + e.toString());
    		}
    		
    		//Bitmap bm = BitmapFactory.decodeResource(getResources(), resId0);
    		
    		NotificationCompat.Builder mBuilder = 
    			new NotificationCompat.Builder(this)
    			.setSmallIcon(resId0)
    			//.setLargeIcon(bm)
    			.setContentTitle(unfriendFromName + " has unfriend you")
    			//.setStyle(new NotificationCompat.BigTextStyle().bigText("Click to retrieve"))
    			.setContentText(unfriendFromEmail )
    			.setTicker("Cadie Buddy Unfriend - " + unfriendFromName)
    			.setAutoCancel(true)
    			.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
    			.setVibrate(new long[]{100, 100, 100, 100});
    		
    		
//    		if(reqResponseType.contentEquals("REQUEST_DECLINED"))
//    		{
//    			mBuilder.setContentTitle(acceptedByName + " declined your contact request");
//    			mBuilder.setTicker("Cadie Request declined - " + acceptedByName);
//    		}
    		
    		mBuilder.setContentIntent(contentIntent);
    		mNotificationManager.notify("CONTACT_UNFRIEND",1, mBuilder.build());
    		
    		if( Global.bRun )
    			GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", "REFRESH_CONTACT_UNFRIEND^" + unfriendFromEmail  );
		} catch (Exception e) {
			Log.d(CommonUtilities.TAG, "Notification Exception - " + e.toString());
		}
    }
    
    /**
     * Update cadie contact info when a friend changes its profile info 
     */
    
    private void updateFriendProfileInfo(String friendUserName ,String friendEmailID ,String friendStatusMsg ,
    		String FriendDateOfBirth ,String FriendPhoneNo ,String FriendLocation , 
    		String FriendProfilePicName , String IsFriendProfilePicChanged , String IsFriendProfileNameChanged )
	{
    	try{
    		cDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);

    		ContentValues values = new ContentValues();                          
    		values.put("Name", friendUserName);
    		values.put("ProfileMessage", friendStatusMsg);
    		values.put("DateOfBirth", FriendDateOfBirth);
    		values.put("PhoneNo", FriendPhoneNo);
    		values.put("Location", FriendLocation);
    		values.put("ProfilePicName", FriendProfilePicName);
    		
			String where = "EmailID = ?";  
			final String[] whereArgs = { friendEmailID };
			int numRowAffected = cDataBase.update("CADIEContacts", values, where, whereArgs);
			
			Log.d(CommonUtilities.TAG, "Inside updateFriendProfileInfo - " + numRowAffected );
			values.clear();
			
			if(IsFriendProfileNameChanged.contentEquals("YES"))
			{
				values = new ContentValues();   
	    		values.put("FromUserName", friendUserName);
	    		
	    		String where1 = "FromEmailID = ? AND RequestType = ?";  
				final String[] whereArgs1 = { friendEmailID ,"RECEIVED"};
				int numRowAffected1 = cDataBase.update("CADIEInvitesRequests", values, where1, whereArgs1);
				Log.d(CommonUtilities.TAG, "11 Inside updateFriendName - " + numRowAffected1 );
				values.clear();
				
				////
				//cDataBase = SQLiteDatabase.openDatabase(cDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
				values = new ContentValues();                          
	    		values.put("FriendsName", friendUserName);
	    		
				String where2 = "FriendsEmailID = ? AND ShareType = ?";  
				final String[] whereArgs2 = { friendEmailID ,"RECEIVED" };
				int numRowAffected2 = cDataBase.update("ShareHistory", values, where2, whereArgs2);
				
				Log.d(CommonUtilities.TAG, "22 Inside updateFriendName - " + numRowAffected2 );
				values.clear();
			}
			
    	}
    	catch (Exception e)
    	{
    		Log.d(CommonUtilities.TAG,"updateFriendProfileInfo Update Error"); 
		}
    	finally{
    		Log.d(CommonUtilities.TAG,"updateFriendProfileInfo updated.."); 
    		
    		if(cDataBase.isOpen())
    			cDataBase.close();
    		
    		if(IsFriendProfilePicChanged.contentEquals("YES"))
    		{
    			Log.d(CommonUtilities.TAG,"IsFriendProfilePicChanged YES..");
    			startFriendsProfilePicDownload(friendEmailID,FriendProfilePicName,false,"");
    		}
    		else
    		{
    			Log.d(CommonUtilities.TAG,"IsFriendProfilePicChanged NO..");
    			if( Global.bRun )
        			GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", "FRIEND_PROFILE_CHANGED^" + friendEmailID);
    		}
    		
    	}
	}
    
    private void  startFriendsProfilePicDownload(String friendEmailID ,String profilePicFileName , Boolean isSelfNotification ,String selfUserName)
    {
    	URL url = null;
    	HttpURLConnection con = null;

        try {
        	String urlpath = "http://54.88.103.38/gcm_server_php/CadieUserProfilePic/"+ profilePicFileName;
        	
            url = new URL(urlpath.toString());
            con = (HttpURLConnection)url.openConnection();
            
            String PATH = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/thumbnails";//Environment.getExternalStorageDirectory() + "/Cadie";
            File file = new File(PATH); 
            
//            if (!file.exists()) 
//                file.mkdirs();
//            
//            if (!file.exists())
//            	file.getParentFile().mkdirs();
//            
//            if (!file.exists()) 
//            	PATH = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadie";
            
            if (!file.exists()) 
                file.mkdirs();
            
            if (!file.exists()) 
            	file.getParentFile().mkdirs();
           
            File outputFile = new File(file,profilePicFileName);  
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
            
            Log.d(CommonUtilities.TAG , "File downloaded to device - " + profilePicFileName);
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
        	
        	if( Global.bRun )
        	{
        		if(isSelfNotification)
        			GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", "FRIEND_PROFILE_CHANGED^" + friendEmailID + "^" + selfUserName);
        		else
        			GCMInitFunction.cGCM.dispatchStatusEventAsync("RETRIEVE_NOTE", "FRIEND_PROFILE_CHANGED^" + friendEmailID);
        	}
        }
    }
    
    private void  startFriendsProfilePicDownloadOnRequestAccept(String friendEmailID )
    {
     URL url = null;
     HttpURLConnection con = null;

        try {
         String[] strProfilePicName = friendEmailID.split("\\@");
         String urlpath = "http://54.88.103.38/gcm_server_php/CadieUserProfilePic/"+ strProfilePicName[0] + ".jpg";
         
            url = new URL(urlpath.toString());
            con = (HttpURLConnection)url.openConnection();
            
            String PATH = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/thumbnails";
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
}
