package com.zahdoo.android.extension.GCM;

 
public final class CommonUtilities {
     
    // give your server registration url here
    //static final String SERVER_URL = "http://65.182.104.219/gcm_register.php";
	static final String SERVER_URL = "http://54.88.103.38/gcm_server_php/GCM_register1.php";
    
    // give your server send msg url here
    static final String SERVER_SEND_MSG_URL = "http://54.88.103.38/gcm_server_php/GCM_send_cadie_msg_SELF.php";
 
    // Google project id
    	static final String SENDER_ID = "818824275478";
 
    static final String TAG = "CADIE GCM";
    //public static final String PREFS_NAME = "CADIE_GCM_DEMO";
    //public static final String PREFS_PROPERTY_REG_ID = "registration_id";
    public static final long GCM_TIME_TO_LIVE = 60L * 60L * 24L * 7L * 4L; // 4 Weeks
 
}