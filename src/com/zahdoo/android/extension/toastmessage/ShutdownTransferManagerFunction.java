package com.zahdoo.android.extension.toastmessage;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.zahdoo.android.extension.ToastMessageExtensionContext;
import com.zahdoo.android.extension.GCM.CommonUtilities;
import com.zahdoo.android.extension.GCM.FileTransferFunction;
import com.zahdoo.android.extension.alarm.Global;

public class ShutdownTransferManagerFunction implements FREFunction {

	public static ToastMessageExtensionContext tmsg;
	
	public FREObject call(FREContext context, FREObject[] passedArgs) {
		FREObject result = null; 
		AsyncTask<Void, Void, Void> mFileTask;
		tmsg = (ToastMessageExtensionContext)context;
		
		try
        {
        	 mFileTask = new AsyncTask<Void, Void, Void>() 
             {
                 @Override
 	            protected Void doInBackground(Void... params) {
                	 try {
                		 if(Global.mTransferManager != null){
                			 Global.mTransferManager.shutdownNow(false);
                           	 Global.mTransferManager = null;
                		 }
                		 
                		 Log.d("CADIE S3" , "Shutdown S3 Transfer Manager");
					} 
                	catch (Exception e) {
						Log.d("CADIE S3" , e.toString() + " Transfer Manager Shutdown ERROR  ");
					}
                	 catch(Throwable ex) {
                 		Log.d("CADIE S3" , ex.toString() + " Transfer Manager Shutdown ERROR  ");
                  	} 
                 	 
                	 
 	                return null;
 	            }
 	     
                 @Override
                 protected void onPostExecute(Void result) {
                	 
                 }
             };
             mFileTask.execute(null, null, null);
        } 
        catch (Exception e) 
        {
        	Log.d("CADIE S3" , e.toString() + " Transfer Manager Shutdown ERROR  ");
        }
			
		return result;
	}
}
