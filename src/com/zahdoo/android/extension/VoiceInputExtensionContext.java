package com.zahdoo.android.extension;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.zahdoo.android.extension.voiceinput.DeviceInfoFunction;
import com.zahdoo.android.extension.voiceinput.ReadMessageFunction;
import com.zahdoo.android.extension.voiceinput.VoiceInputDestroyFunction;
import com.zahdoo.android.extension.voiceinput.VoiceInputInitFunction;
import com.zahdoo.android.extension.voiceinput.VoiceInputListenFunction;
import com.zahdoo.android.extension.voiceinput.VoiceInputSupportedFunction;
import com.zahdoo.android.extension.voiceinput.VoiceResponseFunction;

public class VoiceInputExtensionContext extends FREContext {

	public static Intent voiceIntent;
	
	@Override
	public void dispose() {

	}

	@Override
	public Map<String, FREFunction> getFunctions() {
		Map<String, FREFunction> functionMap = new HashMap<String, FREFunction>();
		
		functionMap.put("initVoice", new VoiceInputInitFunction()); 
		functionMap.put("destroy", new VoiceInputDestroyFunction()); 
		functionMap.put("isSupported", new VoiceInputSupportedFunction()); 
		functionMap.put("voiceInputListen", new VoiceInputListenFunction());
		functionMap.put("voiceResponse", new VoiceResponseFunction());
		functionMap.put("readMessage", new ReadMessageFunction());
		
		functionMap.put("getDeviceInfo",new DeviceInfoFunction());
		
		return functionMap;
	}
}
