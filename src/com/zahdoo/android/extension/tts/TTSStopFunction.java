package com.zahdoo.android.extension.tts;

import android.speech.tts.TextToSpeech;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;

public class TTSStopFunction implements FREFunction {

	public FREObject call(FREContext arg0, FREObject[] arg1) {
		
		TextToSpeech tts = TTSController.getInstance().getTTS();
        if (tts.isSpeaking()) {
                tts.stop();
        }
        return null;
	}

}
