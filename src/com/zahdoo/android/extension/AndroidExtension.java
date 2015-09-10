package com.zahdoo.android.extension;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREExtension;

public class AndroidExtension implements FREExtension {

	public FREContext createContext(String contextType) {
		if(contextType.equalsIgnoreCase("Vibrate"))
			return new VibrationExtensionContext();
		
		else if(contextType.equalsIgnoreCase("Alarm"))
			return new AlarmExtensionContext();
		
		else if(contextType.equalsIgnoreCase("Contacts"))
			return new ContactExtensionContext();
		
		else if(contextType.equalsIgnoreCase("VoiceInput"))
			return new VoiceInputExtensionContext();
			
		else if(contextType.equalsIgnoreCase("TTSResponse"))
			return new TTSExtensionContext();
			
		else if(contextType.equalsIgnoreCase("OpenPDF"))
			return new OpenPDFExtensionContext();
		
		else if(contextType.equalsIgnoreCase("Location"))
			return new LocationExtensionContext();
		
		else if(contextType.equalsIgnoreCase("Camera"))
			return new CameraExtensionContext();
		
		else if(contextType.equalsIgnoreCase("Recommendations"))
			return new RecommendationsExtensionContext();
		
		else if(contextType.equalsIgnoreCase("ToastMessage"))
			return new ToastMessageExtensionContext();
		
		else if(contextType.equalsIgnoreCase("GCM"))
			return new CadieGCMExtensionContext();
		else
			return new VibrationExtensionContext();
	}

	public void dispose() {

	}

	public void initialize() {

	}
}
