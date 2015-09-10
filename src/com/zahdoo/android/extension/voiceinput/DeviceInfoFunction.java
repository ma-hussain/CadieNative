package com.zahdoo.android.extension.voiceinput;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.zahdoo.android.extension.VoiceInputExtensionContext;

public class DeviceInfoFunction implements FREFunction {

	public static VoiceInputExtensionContext vr;

	@Override
	public FREObject call(FREContext context, FREObject[] passedArgs) {
		vr = (VoiceInputExtensionContext)context;
		
		FREObject result = null;
		try {
			vr.dispatchStatusEventAsync("DEVICE_INFO", android.os.Build.MANUFACTURER + ", "
			+ android.os.Build.MODEL + ", " + android.os.Build.VERSION.SDK_INT);
		} 
		catch (Exception fwte) {
		}
		
		return result;
	}
}
