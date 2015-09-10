package com.zahdoo.android.extension.toastmessage;

import android.content.Intent;
import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.zahdoo.android.extension.ToastMessageExtensionContext;

public class DownloadFriendsProfilePicFunction implements FREFunction {
	
	public static ToastMessageExtensionContext tmsg;
	
	@Override
	public FREObject call(FREContext context, FREObject[] passedArgs) {
		FREObject result = null; 
		tmsg = (ToastMessageExtensionContext)context;
		
		try {
			Log.d("CADIE PROFILE PIC DOWNLOAD", "DownloadFriendsProfilePicFunction " );
			FREObject fro = passedArgs[0];
			//String[] vStrings = fro.getAsString().split("\\^");
			
			Intent sInt = new Intent(tmsg.getActivity(), DownloadProfilePicService.class);
			sInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			sInt.putExtra("allEmailIds", fro.getAsString());
			tmsg.getActivity().startService(sInt);
		} 
		catch (Exception fwte) {
			fwte.printStackTrace();
		}
		return result;
	}
}



