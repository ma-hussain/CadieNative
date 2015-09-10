/**
 * 
 */
package com.zahdoo.android.extension.alarm;

import android.widget.Toast;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;

/**
 * @author ZTOUCH
 *
 */
public class ShowToastMessageFunction implements FREFunction {

	/* (non-Javadoc)
	 * @see com.adobe.fre.FREFunction#call(com.adobe.fre.FREContext, com.adobe.fre.FREObject[])
	 */
	public FREObject call(FREContext context, FREObject[] passedArgs) {
		FREObject result = null; 
		
		try { 
			FREObject fro = passedArgs[0];
			String msg = fro.getAsString();
			
			Toast.makeText(context.getActivity(), msg, Toast.LENGTH_SHORT).show();
		} 
		catch (Exception e) { 
			e.printStackTrace(); 
		} 
		return result;
	}
}


