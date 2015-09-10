package com.zahdoo.android.extension;

import java.util.HashMap;
import java.util.Map;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.zahdoo.android.extension.recommendations.SearchRecommendationFunction;

public class RecommendationsExtensionContext extends FREContext {

	@Override
	public void dispose() {

	}

	public Map<String, FREFunction> getFunctions() 
	{
		Map<String, FREFunction> functionMap = new HashMap<String, FREFunction>();
		functionMap.put("searchRecommendation", new SearchRecommendationFunction());
		return functionMap;
	}
}
