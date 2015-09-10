package com.zahdoo.android.extension.toastmessage;

import android.view.Gravity;
import android.widget.Toast;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.zahdoo.android.extension.ToastMessageExtensionContext;

public class DisplayToastMessageFunction implements FREFunction {

	public static ToastMessageExtensionContext tmsg;
	
	public FREObject call(FREContext context, FREObject[] passedArgs) {
		FREObject result = null; 
		
		tmsg = (ToastMessageExtensionContext)context;
		try { 
			FREObject fro = passedArgs[0];
			String msg = fro.getAsString();
			
//			Toast toast = new Toast(context.getActivity());
//			toast.setGravity(Gravity.CENTER, 0, 0);
//			toast.setDuration(Toast.LENGTH_LONG);
//			toast.setText(msg);
//			toast.show();
//			
			if(msg.equals("HIDE_DOUBLE_TAP_MSG"))
			{
				Global.toast.cancel();
			}
			else
			{
				if( Global.toast != null)
					Global.toast.cancel();
				
				Global.toast = Toast.makeText(context.getActivity(), msg, Toast.LENGTH_SHORT);
				Global.toast.setGravity(Gravity.CENTER, 0, 0);
				Global.toast.show();
			}
		} 
		catch (Exception e) { 
			e.printStackTrace(); 
			Toast.makeText(context.getActivity(), e.toString(), Toast.LENGTH_LONG).show();
		} 
		return result;
	}

}
