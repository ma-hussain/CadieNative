package com.zahdoo.android.extension.GCM;

import android.content.Intent;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.zahdoo.android.extension.CadieGCMExtensionContext;

public class SendMessageFunction implements FREFunction {
	
	public static CadieGCMExtensionContext gcmCon;
	
	@Override
	public FREObject call(FREContext context, FREObject[] passedArgs) {
		FREObject result = null; 
		gcmCon = (CadieGCMExtensionContext)context;
		
		try {
			FREObject fro = passedArgs[0];
			String[] vStrings = fro.getAsString().split("\\^");
			
			Intent sInt = new Intent(gcmCon.getActivity(), SendMessageService.class);
			sInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			
//			// ShareID^ByEmailID^ByName^ToEmailID^NoteID^NoteTitle
//			
			sInt.putExtra("shareID", vStrings[0]);
			sInt.putExtra("byEmail", vStrings[1]);
			sInt.putExtra("byName", vStrings[2]);
			sInt.putExtra("toEmail", vStrings[3]); // Group ID 
			sInt.putExtra("noteID", vStrings[4]);
			sInt.putExtra("noteTitle", vStrings[5]);
			sInt.putExtra("dbName", vStrings[6]);
			sInt.putExtra("senderRegID", vStrings[7]);
			sInt.putExtra("notificationType", vStrings[8]); // GROUP_VIDEO
			
			if(vStrings.length == 10)
				sInt.putExtra("fileSize", vStrings[9]);
			
			if(vStrings.length == 11)
			{
				sInt.putExtra("fileSize", vStrings[9]);
				sInt.putExtra("toName", vStrings[10]);
			}
			gcmCon.getActivity().startService(sInt);
		} 
		catch (Exception fwte) {
			fwte.printStackTrace();
		}
		return result;
	}
}
