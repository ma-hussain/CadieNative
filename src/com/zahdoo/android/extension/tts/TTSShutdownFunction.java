package com.zahdoo.android.extension.tts;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;

public class TTSShutdownFunction implements FREFunction {

	public FREObject call(FREContext arg0, FREObject[] arg1) {

		TTSController.getInstance().getTTS().shutdown();

		return null;
	}

}
