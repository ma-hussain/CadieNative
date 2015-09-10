package com.zahdoo.android.extension.GCM;

import android.content.Intent;
import android.widget.Toast;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.zahdoo.android.extension.CadieGCMExtensionContext;

public class RegisterFunction implements FREFunction {

	public static CadieGCMExtensionContext gcmCon;
	
	@Override
	public FREObject call(FREContext context, FREObject[] passedArgs) 
	{
		FREObject result = null; 
		gcmCon = (CadieGCMExtensionContext)context;
		
		try {
			FREObject fro = passedArgs[0];
			String[] vStrings = fro.getAsString().split("\\^");
			
			
			Intent sInt = new Intent(gcmCon.getActivity(),RegisterService.class);
			sInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			sInt.putExtra("action", vStrings[0]);
			sInt.putExtra("name", vStrings[1]);
			sInt.putExtra("email", vStrings[2]);
			sInt.putExtra("password", vStrings[3]);
			
			if(vStrings.length == 5)
				sInt.putExtra("regID", vStrings[4]);
			
			if(vStrings.length == 6){
				sInt.putExtra("regID", vStrings[4]);
				sInt.putExtra("serverUserID", vStrings[5]);
			}
			
			if(vStrings[0].contentEquals("VERIFY_CODE"))
			{
				sInt.putExtra("regID", vStrings[4]);
				sInt.putExtra("serverUserID", vStrings[5]);
				
				if(vStrings.length == 6)
					sInt.putExtra("macAddress", "");
				else
					sInt.putExtra("macAddress", vStrings[6]);
			}
			
			if(vStrings[0].contentEquals("VERIFY_HAVING_CODE"))
			{
				sInt.putExtra("verificationCode", vStrings[4]);
				if(vStrings.length == 5)
					sInt.putExtra("macAddress", "");
				else
					sInt.putExtra("macAddress", vStrings[5]);
			}
			
			gcmCon.getActivity().startService(sInt);
		} 
		catch (Exception fwte) {
			Toast.makeText(gcmCon.getActivity(),"Inside Register Function Error " + fwte.toString() , Toast.LENGTH_LONG).show();
			fwte.printStackTrace();
		}
		
		return result;
	}
}
