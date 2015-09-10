package com.zahdoo.android.extension.GCM;

import com.google.gson.annotations.SerializedName;

public class GroupMessageInfo {
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
