package com.zahdoo.android.extension.toastmessage;

import android.app.NotificationManager;
import android.content.Context;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.zahdoo.android.extension.ToastMessageExtensionContext;

public class ClearNotificationFunction implements FREFunction {

	public static ToastMessageExtensionContext tmsg;
	
	public FREObject call(FREContext context, FREObject[] passedArgs) {
		FREObject result = null; 
		
		tmsg = (ToastMessageExtensionContext)context;
		try { 
			FREObject fro = passedArgs[0];
			String notfctnType = fro.getAsString();
			
//			Toast toast = new Toast(context.getActivity());
//			toast.setGravity(Gravity.CENTER, 0, 0);
//			toast.setDuration(Toast.LENGTH_LONG);
//			toast.setText(msg);
//			toast.show();
//			
			//Toast.makeText(context.getActivity(), msg, Toast.LENGTH_SHORT).show();
			
			
//			Toast toast = Toast.makeText(context.getActivity(), msg, Toast.LENGTH_SHORT);
//			toast.setGravity(Gravity.CENTER, 0, 0);
//			toast.show();
			
			// Clear all notification
			NotificationManager nMgr = (NotificationManager) context.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
			
			if(notfctnType.contentEquals("MESSAGE"))
				nMgr.cancel("MESSAGE", 1);
			else if(notfctnType.contentEquals("SHARED_NOTE"))
				nMgr.cancel("SHARED_NOTE", 1);
			else if(notfctnType.contentEquals("CONTACT_REQUEST"))
				nMgr.cancel("CONTACT_REQUEST", 1);
			else if(notfctnType.contentEquals("CONTACT_REQUEST_ACCEPTED"))
				nMgr.cancel("CONTACT_REQUEST_ACCEPTED", 1);
			else if(notfctnType.contentEquals("ALERT"))
				nMgr.cancel("ALERT", 1);
			else if(notfctnType.contentEquals("CONTACT_UNFRIEND"))
				nMgr.cancel("CONTACT_UNFRIEND", 1);
			else if(notfctnType.contentEquals("ALL"))
				nMgr.cancelAll();
			
		} 
		catch (Exception e) { 
			e.printStackTrace(); 
		} 
		return result;
	}
}
