/*
 * Copyright 2010-2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.zahdoo.android.extension.GCM;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.transfer.Transfer;
import com.zahdoo.android.extension.alarm.Global;

/*
 * Class that encapsulates downloads, handling all the interaction with the
 * underlying Download and TransferManager
 */
public class DownloadModel extends TransferModel {
    //private static final String TAG = "DownloadModel";

    //private PersistableDownload mPersistableDownload;
    private ProgressListener mListener;
    //private String mKey;
    private Status mStatus;
    private Uri mUri;
    private Context context;
    private String strFileName;
    private String fileDownloadType;
    private int progressVal =  0;

    public DownloadModel(Context context, String key, String downloadType) {
       super(context, Uri.parse(key));
       
       this.context = context;
       strFileName = key;//key is the name of the file;
       fileDownloadType = downloadType;
       
       mStatus = Status.IN_PROGRESS;
       mListener = new ProgressListener() {
           @Override
           public void progressChanged(ProgressEvent event) 
           {
        	   Log.d("CADIE S3", "Download Progress - " + (int)Global.mDownload.getProgress().getPercentTransferred());
               
        	   	try {
        	   		
//        	   		if(!fileDownloadType.contentEquals("THUMB_DOWNLOAD"))
//        	   		{
        	   			if((int)Global.mDownload.getProgress().getPercentTransferred() != 0  && progressVal != (int)Global.mDownload.getProgress().getPercentTransferred() ) 
        	   			{
              			  progressVal = (int)Global.mDownload.getProgress().getPercentTransferred();
              			  Log.d("CADIE S3", "Download Progress Event Dispatch - " + progressVal);
              			  try {
								FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "TRANSFER_PROGRESS^"+ progressVal);
							} catch (Exception e) {
								 Log.d("CADIE S3", "Download err - " + e.toString());
							}
						}
        	   		//}
        	   		
		           if(event.getEventCode() == ProgressEvent.COMPLETED_EVENT_CODE) 
		           {
		        	   Global.mDownload.removeProgressListener(mListener);
		        	   mStatus = Status.COMPLETED;
		        	   
		        	   if(fileDownloadType.contentEquals("THUMB_DOWNLOAD"))
		        	   {
		        		   Log.d("CADIE S3", "Thumb Downloaded");
		        		   try {
								FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "THUMB_DOWNLOAD");
							} catch (Exception e) {
								Log.d("CADIE S3", "Thumb Download err - " + e.toString());
							}
		        	   }
		        	   else
		        	   {
		        		   Log.d("CADIE S3", "File Downloaded");
		        		   try {
								FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "DOWNLOAD_SUCCESSFUL");
		        		   } catch (Exception e) {
								Log.d("CADIE S3", "File Download err - " + e.toString());
							}
		        	   }
		        	   ((FileTransferService)DownloadModel.this.context).stopSelf();
		           }
		           else if(event.getEventCode() == ProgressEvent.FAILED_EVENT_CODE) 
	              	{
		        	   Global.mDownload.removeProgressListener(mListener);
	              		
	              		try {
	                        AmazonClientException e = Global.mDownload.waitForException();
	                        Log.e("CADIE ","CADIE S3 Exception - "  +  e.toString() + " " + event.getBytesTransferred());
	                        
	                        try {
								FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR "  );
	                        } catch (Exception e1) {
								Log.d("CADIE S3", "CADIE S3 Exception 100 - " + e1.toString());
							}
	                        
	                        Global.mTransferManager.shutdownNow();
	                        Global.mTransferManager = null;
	                        ((FileTransferService)DownloadModel.this.context).stopSelf();
	                    }
	              		catch (InterruptedException e) {} 
	              	}
		           
        	   	} catch (Exception e) {
					Log.d("CADIE S3", "S3 Download Exc - " + e.toString());
				}
           }
       };
    }

    @Override
    public Status getStatus() { return mStatus; }

    @Override
    public Transfer getTransfer() { return Global.mDownload; }

    @Override
    public Uri getUri() { return mUri; }

    @Override
    public void abort() {
        if(Global.mDownload != null) {
            mStatus = Status.CANCELED;
            try {
            	Global.mDownload.abort();
            	Global.mTransferManager.shutdownNow();
            	Global.mTransferManager = null;
                try {
					FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ABORT_SUCCESSFUL");
                } catch (Exception e1) {
					Log.d("CADIE S3", "CADIE S3 Abort 100 - " + e1.toString());
				}
				
	        	((FileTransferService)DownloadModel.this.context).stopSelf();
            } catch(IOException e) {
                Log.e("CADIE S3","CADIE S3 Abort exc - " + e.toString());
            }
        }
    }

    public void download() {
    	int test = 0;
    	try {
    		mStatus = Status.IN_PROGRESS;
    		
    		String PATH = new String();
    		
    		if(fileDownloadType.contentEquals("THUMB_DOWNLOAD"))
            	PATH = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/thumbnails";
            else
            {
            	if(fileDownloadType.contentEquals("DOWNLOAD_SHARED_DB"))
            	{
                	File testSdCard = new File(Environment.getExternalStorageDirectory() + "/cadie");

                	if(!testSdCard.exists())
                		testSdCard.mkdir();

                	if(testSdCard.exists())
                		PATH = Environment.getExternalStorageDirectory() + "/cadie/frmServer";
                	else
                		PATH = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadie/frmServer";
            	}
            	else
            	{
                	File testSdCard = new File(Environment.getExternalStorageDirectory() + "/cadie");

                	if(!testSdCard.exists())
                		testSdCard.mkdir();

                	if(testSdCard.exists())
                		PATH = Environment.getExternalStorageDirectory() + "/cadie";
                	else
                		PATH = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadie";
            	}
            }
    		
    		test = 1;
            
            Log.d(CommonUtilities.TAG , "Device File Path - " + PATH);
            File file = new File(PATH); // PATH = /mnt/sdcard/download/
            
            if (!file.exists()) {
                file.mkdirs();
            }
            
            if (!file.exists()) {
            	file.getParentFile().mkdirs();
            }
    		
    		test =2 ;
            File file2 = new File(file,strFileName);
            mUri = Uri.fromFile(file2);

            test = 3; 
            Global.mDownload = Global.mTransferManager.download(Constants.BUCKET_NAME, strFileName, file2);
            test = 4;
            if(mListener != null) {
            	Global.mDownload.addProgressListener(mListener);
            }
            
            test = 5;
            
         // waitForCompletion blocks the current thread until the transfer completes
         // and will throw an AmazonClientException or AmazonServiceException if
         // anything went wrong.
         //upload.waitForCompletion();
         // After the upload is complete, call shutdownNow to release the resources.
            //tx.shutdownNow();
		} catch (Exception e) {
			Log.d("CADIE S3", "Error - " + test + " " + e.toString());
			
			Global.mDownload.removeProgressListener(mListener);
            try {
				FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR "  );
			} catch (Exception e1) {
					Log.d("CADIE S3", "CADIE S3 err - " + e1.toString());
			}
            Global.mTransferManager.shutdownNow();
            Global.mTransferManager = null;
            ((FileTransferService)DownloadModel.this.context).stopSelf();
		}
    }

    @Override
    public void pause() { 
//        if(mStatus == Status.IN_PROGRESS) {
//            mStatus = Status.PAUSED;
//            try {
//                mPersistableDownload = Global.mDownload.pause();
//            } catch(PauseException e) {
//                Log.d(TAG, "", e);
//            }
//        }
    }

    @Override
    public void resume() { 
//        if(mStatus == Status.PAUSED) {
//            mStatus = Status.IN_PROGRESS;
//            if(mPersistableDownload != null) {
//            	Global.mDownload = Global.mTransferManager.resumeDownload(
//                        mPersistableDownload);
//            	Global.mDownload.addProgressListener(mListener);
//                mPersistableDownload = null;
//            } else {
//                download();
//            } 
//        }
    }
}
