package com.zahdoo.android.extension.voiceinput;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.zahdoo.android.extension.VoiceInputExtensionContext;

public class VoiceInputListenFunction implements FREFunction {

	public static VoiceInputExtensionContext vi;
	
	public FREObject call(FREContext context, FREObject[] passedArgs) {
		vi = (VoiceInputExtensionContext)context;
		
		try {
			FREObject fro = passedArgs[0];
			String vStrings = fro.getAsString();
			new VoiceReg(vStrings);
		} 
		catch (Exception fwte) {
		}
		
		return null;
	}
}
