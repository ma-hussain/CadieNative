package com.zahdoo.android.extension.recommendations;

import android.content.Intent;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.zahdoo.android.extension.RecommendationsExtensionContext;

public class SearchRecommendationFunction implements FREFunction {

	
	public static RecommendationsExtensionContext rs;
	
	public FREObject call(FREContext context, FREObject[] passedArgs) {

		FREObject result = null; 
		rs = (RecommendationsExtensionContext)context;

		try {
			FREObject fro = passedArgs[0];
			String[] vStrings = fro.getAsString().split("\\^");
			
			//Toast.makeText(SearchRecommendationFunction.rs.getActivity(), vStrings + "\nFetching data ... please wait...", Toast.LENGTH_SHORT).show();
	        
			Intent sInt = new Intent(rs.getActivity(),RecommendationSearchService.class);
			sInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			sInt.putExtra("vSearchText", vStrings[0]);
			sInt.putExtra("vSearchType", vStrings[1]);
			rs.getActivity().startService(sInt);
		} 
		catch (Exception e) { 
			e.printStackTrace(); 
		} 
		
		return result;
	}
}
