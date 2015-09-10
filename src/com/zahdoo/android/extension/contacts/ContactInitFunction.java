package com.zahdoo.android.extension.contacts;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.zahdoo.android.extension.ContactExtensionContext;

public class ContactInitFunction implements FREFunction {

	public static ContactExtensionContext cContact;
	
	public FREObject call(FREContext context, FREObject[] arg1) {
		cContact = (ContactExtensionContext)context;
		return null;
	}

}
