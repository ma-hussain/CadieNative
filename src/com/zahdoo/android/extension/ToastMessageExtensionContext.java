package com.zahdoo.android.extension;

import java.util.HashMap;
import java.util.Map;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.zahdoo.android.extension.toastmessage.ClearNotificationFunction;
import com.zahdoo.android.extension.toastmessage.DisplayToastMessageFunction;
import com.zahdoo.android.extension.toastmessage.DownloadFriendsProfilePicFunction;
import com.zahdoo.android.extension.toastmessage.ShutdownTransferManagerFunction;

public class ToastMessageExtensionContext extends FREContext {

	@Override
	public void dispose() {

	}

	@Override
	public Map<String, FREFunction> getFunctions() 
	{
		Map<String, FREFunction> functionMap = new HashMap<String, FREFunction>();
		functionMap.put("showToastMessage", new DisplayToastMessageFunction());
		functionMap.put("clearNotification", new ClearNotificationFunction());
		functionMap.put("shutdownS3TransferManager", new ShutdownTransferManagerFunction());
		functionMap.put("downloadAllFriendsProfilePic", new DownloadFriendsProfilePicFunction());
		return functionMap;
	}
}
