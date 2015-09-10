package com.zahdoo.android.extension;

import java.util.HashMap;
import java.util.Map;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.zahdoo.android.extension.tts.TTSInitFunction;
import com.zahdoo.android.extension.tts.TTSShutdownFunction;
import com.zahdoo.android.extension.tts.TTSSpeakFunction;
import com.zahdoo.android.extension.tts.TTSStopFunction;

public class TTSExtensionContext extends FREContext {

	public TTSExtensionContext() {
		super();
	}
	
	
	@Override
	public void dispose() { }
	
	
	@Override
	public Map<String, FREFunction> getFunctions() {
	        Map<String, FREFunction> funcs = new HashMap<String, FREFunction>();
	        funcs.put("initTTS", new TTSInitFunction());
	        funcs.put("speak", new TTSSpeakFunction());
	        funcs.put("shutdown", new TTSShutdownFunction());
	        funcs.put("stop", new TTSStopFunction());
	        return funcs;
	}

}
