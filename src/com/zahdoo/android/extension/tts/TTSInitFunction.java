package com.zahdoo.android.extension.tts;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;

public class TTSInitFunction implements FREFunction {

	public FREObject call(FREContext context, FREObject[] arg1) {
		// TODO Auto-generated method stub
		TTSController.getInstance().createTTS(context);

		return null;
	}

}
