package com.zahdoo.android.extension.GCM;

import com.google.gson.annotations.SerializedName;

public class GroupInfo {

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
