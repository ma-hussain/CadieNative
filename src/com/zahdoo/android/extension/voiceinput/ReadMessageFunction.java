package com.zahdoo.android.extension.voiceinput;

import android.content.Intent;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREInvalidObjectException;
import com.adobe.fre.FREObject;
import com.adobe.fre.FRETypeMismatchException;
import com.zahdoo.android.extension.VoiceInputExtensionContext;

public class ReadMessageFunction implements FREFunction {
	public static VoiceInputExtensionContext vr;

	@Override
	public FREObject call(FREContext context, FREObject[] passedArgs) {
		vr = (VoiceInputExtensionContext)context;
		
		FREObject result = null;
		try {
			FREObject fro = passedArgs[0];
			String vStrings = fro.getAsString();
			
			Intent vRec = new Intent(vr.getActivity(),ReadMessage.class);
			vRec.putExtra("vResponseMsg", vStrings);
			vRec.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			vr.getActivity().startActivity(vRec);
		} 
		catch (FREInvalidObjectException fioe) {
			fioe.printStackTrace();
		}catch (FRETypeMismatchException ftme) {
			ftme.printStackTrace();
		}
		catch (Exception fwte) {
			
		}
		return result;
	}

}
