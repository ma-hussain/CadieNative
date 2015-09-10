package com.zahdoo.android.extension;

import java.util.HashMap;
import java.util.Map;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.zahdoo.android.extension.location.GetLocationFunction;
import com.zahdoo.android.extension.location.SearchLocationFunction;

public class LocationExtensionContext extends FREContext {

	@Override
	public void dispose() {

	}

	@Override
	public Map<String, FREFunction> getFunctions() 
	{
		Map<String, FREFunction> functionMap = new HashMap<String, FREFunction>();
		functionMap.put("searchLocation", new SearchLocationFunction());
		functionMap.put("getLocation", new GetLocationFunction());
		return functionMap;
	}
}
