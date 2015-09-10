package com.zahdoo.android.extension.GCM;

import java.io.File;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.zahdoo.android.extension.CadieGCMExtensionContext;
import com.zahdoo.android.extension.alarm.Global;

public class FileTransferFunction implements FREFunction {

	public static CadieGCMExtensionContext gcmCon;
	
	@Override
	public FREObject call(final FREContext context, FREObject[] passedArgs) 
	{
		FREObject result = null; 
		gcmCon = (CadieGCMExtensionContext)context;
		AsyncTask<Void, Void, Void> mFileTask;
	    
		
		try {
			FREObject fro = passedArgs[0];
			final String[] vStrings = fro.getAsString().split("\\^");
			
        	if( vStrings[3].contentEquals("CANCEL_S3_DOWNLOAD"))
            {
                try
                {
                	 mFileTask = new AsyncTask<Void, Void, Void>() 
                     {
                         @Override
         	            protected Void doInBackground(Void... params) {
                        	 try {
                        		 if(Global.mDownload != null)
                        			 Global.mDownload.abort();
                        		 
                        		 if(Global.mTransferManager != null){
                        			 Global.mTransferManager.shutdownNow();
                                   	 Global.mTransferManager = null;
                        		 }
                        		 
                        		 if(!vStrings[0].contentEquals("CadieServerFiles"))
                        		 {
                        			 File file = new File(Environment.getExternalStorageDirectory() + "/cadie",vStrings[0]);
                            		 
                            		 if(file.exists())
                            		 {
                            			 Boolean bool = file.delete();
                            			 Log.d(CommonUtilities.TAG , "File Deleted - " + bool);
                            		 }
                        		 }
							} 
                        	catch (Exception e) {
								Log.d(CommonUtilities.TAG , e.toString() + " File Abort ERROR  ");
							}
         	                return null;
         	            }
         	     
                         @Override
                         protected void onPostExecute(Void result) {
                        	 try {
             					 try {
									FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ABORT_SUCCESSFUL");
								} catch (Exception e) {
									Log.d(CommonUtilities.TAG , " File Abort ERROR 1 - " + e.toString());
								}
             					 
             					if(Global.gContext != null){
             						((FileTransferService)Global.gContext).stopSelf();
                					 Global.gContext = null;
             					}
							}
                        	catch (Exception  e) {
								Log.d(CommonUtilities.TAG , e.toString() + " File Abort ERROR  ");
								try{
									FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR "  );
								} catch (Exception e1) {
									Log.d(CommonUtilities.TAG , " File Abort ERROR 2 - " + e1.toString());
								}
							}
                         }
                     };
                     mFileTask.execute(null, null, null);
                } 
                catch (Exception e) 
                {
                	Log.d(CommonUtilities.TAG , e.toString() + " File Abort ERROR  ");
                	try{
	                	FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR "  );
	                } catch (Exception e1) {
						Log.d(CommonUtilities.TAG , " File Abort ERROR 3 - " + e1.toString());
					}
                }
            }
        	else if(vStrings[3].contentEquals("CANCEL_S3_UPLOAD"))
        	{
        		try
                {
                	 mFileTask = new AsyncTask<Void, Void, Void>() 
                     {
                         @Override
         	            protected Void doInBackground(Void... params) {
                        	 try {
                        		 if(Global.mUpload != null)
                        			 Global.mUpload.abort();
                        		 
                        		 if(Global.mTransferManager != null){
                        			 Global.mTransferManager.shutdownNow();
                                   	 Global.mTransferManager = null;
                        		 }
							} 
                        	catch (Exception e) {
								Log.d(CommonUtilities.TAG , e.toString() + " File Upload Abort ERROR  ");
							}
         	                return null;
         	            }
         	     
                         @Override
                         protected void onPostExecute(Void result) {
                        	 try {
                        		 try{
	             					 FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ABORT_SUCCESSFUL");
	                        	 } catch (Exception e1) {
	         						Log.d(CommonUtilities.TAG , " onPostExecute err - " + e1.toString());
	         					 }
                        		 
             					if(Global.gContext != null){
             						((FileTransferService)Global.gContext).stopSelf();
                					 Global.gContext = null;
             					}
							}
                        	catch (Exception  e) {
								Log.d(CommonUtilities.TAG , e.toString() + " File Abort ERROR  ");
								try{
									FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR "  );
                        	 	} catch (Exception e1) {
	         						Log.d(CommonUtilities.TAG , " onPostExecute err 1 - " + e1.toString());
	         					}
							}
                         }
                     };
                     mFileTask.execute(null, null, null);
                } 
                catch (Exception e) 
                {
                	Log.d(CommonUtilities.TAG , e.toString() + " File Upload Abort ERROR  ");
                	try{
	                	FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR "  );
	                } catch (Exception e1) {
						Log.d(CommonUtilities.TAG , " REGISTERED err 1 - " + e1.toString());
					}
                }
        	}
//        	else if(vStrings[3].contentEquals("SHUTDOWN_S3_TRANSFER_MANAGER"))
//        	{
//        		try
//                {
//                	 mFileTask = new AsyncTask<Void, Void, Void>() 
//                     {
//                         @Override
//         	            protected Void doInBackground(Void... params) {
//                        	 try {
//                        		 if(Global.mTransferManager != null){
//                        			 Global.mTransferManager.shutdownNow(false);
//                                   	 Global.mTransferManager = null;
//                        		 }
//                        		 Toast.makeText(gcmCon.getActivity(),"Shutdown S3 Transfer Manager", Toast.LENGTH_LONG).show();
//                             	
//							} 
//                        	catch (Exception e) {
//								Log.d(CommonUtilities.TAG , e.toString() + " Transfer Manager Shutdown ERROR  ");
//							}
//         	                return null;
//         	            }
//         	     
//                         @Override
//                         protected void onPostExecute(Void result) {
//                        	 try {
//             					// FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "SHUTDOWN_SUCCESSFUL");
//							 }
//                        	 catch (Exception  e) {
//								Log.d(CommonUtilities.TAG , e.toString() + " Transfer Manager Shutdown ERROR  ");
//								//FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR "  );
//							}
//                         }
//                     };
//                     mFileTask.execute(null, null, null);
//                } 
//                catch (Exception e) 
//                {
//                	Log.d(CommonUtilities.TAG , e.toString() + " Transfer Manager Shutdown ERROR  ");
//                	//FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR "  );
//                }
//        	}
        	else
        	{
        		Intent sInt = new Intent(gcmCon.getActivity(),FileTransferService.class);
    			sInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    			sInt.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    			sInt.putExtra("fileName", vStrings[0]);
    			sInt.putExtra("uniqueServerName", vStrings[1]);
    			sInt.putExtra("senderEmail", vStrings[2]);
    			sInt.putExtra("action", vStrings[3]);
    			gcmCon.getActivity().startService(sInt);
        	}
			
		} 
		catch (Exception fwte) {
			fwte.printStackTrace();
		}
		return result;
	}
}
