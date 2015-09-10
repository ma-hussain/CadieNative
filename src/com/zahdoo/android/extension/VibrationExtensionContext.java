package com.zahdoo.android.extension;

import java.util.HashMap;
import java.util.Map;

import android.os.Vibrator;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.zahdoo.android.extension.vibration.VibrationVibrateFunction;

public class VibrationExtensionContext extends FREContext {

	public Vibrator vb;

	@Override
	public void dispose() {

	}

	@Override
	public Map<String, FREFunction> getFunctions() 
	{
		Map<String, FREFunction> functionMap = new HashMap<String, FREFunction>();
		functionMap.put("vibrateMe", new VibrationVibrateFunction());
		return functionMap;
	}
}
