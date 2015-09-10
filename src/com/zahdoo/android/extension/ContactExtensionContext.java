package com.zahdoo.android.extension;

import java.util.HashMap;
import java.util.Map;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.zahdoo.android.extension.contacts.CallContactFunction;
import com.zahdoo.android.extension.contacts.ContactInitFunction;
import com.zahdoo.android.extension.contacts.EmailContactFunction;
import com.zahdoo.android.extension.contacts.HTMLEmailFunction;
import com.zahdoo.android.extension.contacts.PhoneBookSearchFunction;

public class ContactExtensionContext extends FREContext {

	@Override
	public void dispose() {

	}

	@Override
	public Map<String, FREFunction> getFunctions() 
	{
		Map<String, FREFunction> functionMap = new HashMap<String, FREFunction>();
		functionMap.put("initContacts", new ContactInitFunction()); 
		functionMap.put("callContact", new CallContactFunction());
		functionMap.put("emailContact", new EmailContactFunction());
		functionMap.put("emailNote", new HTMLEmailFunction());
		functionMap.put("phoneBookSearch", new PhoneBookSearchFunction());
		return functionMap;
	}
}
