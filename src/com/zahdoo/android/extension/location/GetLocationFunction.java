package com.zahdoo.android.extension.location;

import android.content.Intent;
import android.widget.Toast;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.zahdoo.android.extension.LocationExtensionContext;

public class GetLocationFunction implements FREFunction {
	
	public static LocationExtensionContext loc;
	
	@Override
	public FREObject call(FREContext context, FREObject[] passedArgs) 
	{

		FREObject result = null; 
		loc = (LocationExtensionContext)context;

		try {
			//FREObject fro = passedArgs[0];
			//String[] vStrings = fro.getAsString().split("\\^");
			
			
			Intent sInt = new Intent(loc.getActivity(),LocationActivity.class);
			sInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			loc.getActivity().startActivity(sInt);
			  
		} 
		catch (Exception e) 
		{ 
			e.printStackTrace(); 
			Toast.makeText(GetLocationFunction.loc.getActivity(), "2 Location error\n" + e.toString() , Toast.LENGTH_LONG).show();
		} 
		return result;
	}
	
}
