/**
 * 
 */
package com.zahdoo.android.extension.vibration;

import android.content.Context;
import android.os.Vibrator;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.zahdoo.android.extension.VibrationExtensionContext;

public class VibrationVibrateFunction implements FREFunction {

	public FREObject call(FREContext context, FREObject[] passedArgs) {
		FREObject result = null; 
		VibrationExtensionContext vbc = (VibrationExtensionContext)context; 
		
		try { 
			FREObject fro = passedArgs[0];
			int duration = fro.getAsInt();
			
			if(duration == 0){
				vbc.vb = (Vibrator) vbc.getActivity().getSystemService(Context.VIBRATOR_SERVICE);
				long[] vPattern = {0,80};//,100,200,300};
		  	 
				// Only perform this pattern one time (-1 means "do not repeat")
				vbc.vb.vibrate(vPattern, -1);
			}
			else
				vbc.vb.vibrate(duration);
		} 
		catch (Exception e) { 
			e.printStackTrace(); 
		} 
		return result;
	}
}


