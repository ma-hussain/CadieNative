package com.zahdoo.android.extension;

import java.util.HashMap;
import java.util.Map;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.zahdoo.android.extension.camera.CameraInitFunction;
import com.zahdoo.android.extension.camera.CameraSupportedFunction;
import com.zahdoo.android.extension.camera.CaptureFromAttachmentFunction;
import com.zahdoo.android.extension.camera.CaptureFromCameraFunction;
import com.zahdoo.android.extension.camera.PlayAudioFunction;
import com.zahdoo.android.extension.camera.PlayVideoFunction;

public class CameraExtensionContext extends FREContext {

	@Override
	public void dispose() {

	}

	@Override
	public Map<String, FREFunction> getFunctions() 
	{
		Map<String, FREFunction> functionMap = new HashMap<String, FREFunction>();
		functionMap.put("initCamera", new CameraInitFunction()); 
		functionMap.put("playAudio", new PlayAudioFunction());
		functionMap.put("playVideo", new PlayVideoFunction());
		functionMap.put("captureFromAttachment", new CaptureFromAttachmentFunction());
		functionMap.put("captureFromCamera", new CaptureFromCameraFunction());
		functionMap.put("isCameraSupported", new CameraSupportedFunction());
		return functionMap;
	}
}
