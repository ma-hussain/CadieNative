package com.zahdoo.android.extension;

import java.util.HashMap;
import java.util.Map;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.zahdoo.android.extension.alarm.AlarmInitFunction;
import com.zahdoo.android.extension.alarm.SetAlarmFunction;

public class AlarmExtensionContext extends FREContext {
	
	@Override
	public void dispose() {
		
	}

	@Override
	public Map<String, FREFunction> getFunctions() 
	{
		Map<String, FREFunction> functionMap = new HashMap<String, FREFunction>();
		functionMap.put("initAlarm", new AlarmInitFunction()); 
		functionMap.put("setAlarm", new SetAlarmFunction());
		return functionMap;
	}
}
