/**
 * 
 */
package com.zahdoo.android.extension.alarm;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.zahdoo.android.extension.AlarmExtensionContext;

public class AlarmInitFunction implements FREFunction {

	public static AlarmExtensionContext ac;

	public FREObject call(FREContext context, FREObject[] passedArgs) {
		ac = (AlarmExtensionContext)context;
		
		return null;
	}
}
