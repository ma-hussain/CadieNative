package com.zahdoo.android.extension.voiceinput;

import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.adobe.fre.FREWrongThreadException;

public class VoiceInputSupportedFunction implements FREFunction {

	public FREObject call(FREContext context, FREObject[] arg1) {
		FREObject returnValue = null;
		
		try {
			returnValue = FREObject.newObject(false);
			
			//VoiceInputExtensionContext vi = (VoiceInputExtensionContext) context;
			
			// Check to see if a recognition activity is present
	        PackageManager pm = context.getActivity().getPackageManager();
//	        List<ResolveInfo> activities = pm.queryIntentActivities(VoiceInputExtensionContext.voiceIntent, 0);
	        List<ResolveInfo> activities = pm.queryIntentActivities(
	                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);

	        if (activities.size() != 0) {
	        	returnValue = FREObject.newObject(true);
	        } else {
	        	returnValue = FREObject.newObject(false);
	        }
		} catch (FREWrongThreadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return returnValue;
	}

}
