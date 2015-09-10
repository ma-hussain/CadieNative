package com.zahdoo.android.extension.tts;

import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;

public class TTSSpeakFunction implements FREFunction {

	public FREObject call(FREContext context, FREObject[] passedArgs) {
		try {
			FREObject fro = passedArgs[0];
			String text = fro.getAsString();
			TextToSpeech tts = TTSController.getInstance().getTTS();

			try {

				if (text.contains("AppName") || text.contains("AboutApp")
						|| text.contains("AboutTime")) {
					String[] vStrings = fro.getAsString().split("\\^");

					if (vStrings[1].equalsIgnoreCase("AppName")
							|| vStrings[1].equalsIgnoreCase("AboutTime")
							|| vStrings[1].equalsIgnoreCase("AboutApp")) 
					{
						TTSController.getInstance().speak(vStrings[1], vStrings[0]);
					}
				} else {
					TTSController.getInstance().speak(text,"");
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (Exception e) {
			Toast.makeText(context.getActivity(), "error : " + e.toString(),
					Toast.LENGTH_SHORT).show();
		}
		return null;
	}

}
