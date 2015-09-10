package com.zahdoo.android.extension.GCM;

import com.google.gson.annotations.SerializedName;

public class GroupUserInfo {
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
