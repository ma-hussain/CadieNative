package com.zahdoo.android.extension.GCM;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.zahdoo.android.extension.CadieGCMExtensionContext;

public class GCMInitFunction implements FREFunction {

	public static CadieGCMExtensionContext cGCM;
	
	@Override
	public FREObject call(FREContext context, FREObject[] arg1) {
		cGCM = (CadieGCMExtensionContext) context;
		return null;
	}
}
