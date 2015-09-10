package com.zahdoo.android.extension;

import java.util.HashMap;
import java.util.Map;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.zahdoo.android.extension.GCM.FileTransferFunction;
import com.zahdoo.android.extension.GCM.GCMInitFunction;
import com.zahdoo.android.extension.GCM.GetAllContactEmailFunction;
import com.zahdoo.android.extension.GCM.GroupMessageFunction;
import com.zahdoo.android.extension.GCM.RegisterFunction;
import com.zahdoo.android.extension.GCM.SendMessageFunction;

public class CadieGCMExtensionContext extends FREContext {

	@Override
	public void dispose() {

	}

	@Override
	public Map<String, FREFunction> getFunctions()
	{
		Map<String, FREFunction> functionMap = new HashMap<String, FREFunction>();
		functionMap.put("initGCM", new GCMInitFunction()); 
		functionMap.put("Register", new RegisterFunction()); 
		functionMap.put("SendMessage", new SendMessageFunction());
		functionMap.put("GroupMessage", new GroupMessageFunction());
		functionMap.put("FileTransfer", new FileTransferFunction());
		functionMap.put("GetAllContactEmails", new GetAllContactEmailFunction());
		return functionMap;
	}
}
