/**
 * 
 */
package com.zahdoo.android.extension.vibration;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.adobe.fre.FREWrongThreadException;
import com.zahdoo.android.extension.VibrationExtensionContext;

public class VibrationSupportedFunction implements FREFunction {

	public FREObject call(FREContext context, FREObject[] passedArgs) {
		FREObject result = null; 
		VibrationExtensionContext vbc = (VibrationExtensionContext)context;
		
		try {
			if (vbc.vb == null) 
				result = FREObject.newObject(false);
			else  
				result = FREObject.newObject(true); 
		} catch (FREWrongThreadException fwte) {
			fwte.printStackTrace();
		}
		return result;
	}
}
