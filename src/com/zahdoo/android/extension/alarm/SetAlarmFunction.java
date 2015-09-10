/**
 * 
 */
package com.zahdoo.android.extension.alarm;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;

public class SetAlarmFunction implements FREFunction {

	public FREObject call(FREContext context, FREObject[] passedArgs) {
		FREObject result = null; 
		
		try { 
			Global.AlertID   = 0;
		  	Global.aTitle	 = "";
			new CalculateAlarmActivity();
		} 
		catch (Exception e) { 
			e.printStackTrace(); 
		} 
		return result;
	}
}


