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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.services.s3.transfer.PersistableUpload;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.exception.PauseException;
import com.zahdoo.android.extension.alarm.Global;

/* UploadModel handles the interaction between the Upload and TransferManager.
 * This also makes sure that the file that is uploaded has the same file extension
 *
 * One thing to note is that we always create a copy of the file we are given. This
 * is because we wanted to demonstrate pause/resume which is only possible with a
 * File parameter, but there is no reliable way to get a File from a Uri(mainly
 * because there is no guarantee that the Uri has an associated File).
 *
 * You can easily avoid this by directly using an InputStream instead of a Uri.
 */
public class UploadModel extends TransferModel {
    private static final String TAG = "CADIE UploadModel";

    private PersistableUpload mPersistableUpload;
    private ProgressListener mListener;
    private Status mStatus;
    private File mFile;
    private String mExtension;
    private String filePath;
    private String strFileName;
    private String fileUploadType;
    private Boolean isTempFile;
    private Context context;
    private int progressVal =  0;

    public UploadModel(Context context, Uri uri, String fileName , final String uploadType ,final String serverResponse ) {
        super(context, uri);
        
        this.context = context;
        
        strFileName = fileName;
        
        isTempFile = false;
        fileUploadType = uploadType;
        
        try {
          if(uploadType.contentEquals("THUMB")){
      		Log.d("CADIE GCM", "UPLOADING THUMB");
      		filePath = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/thumbnails/" + fileName;
      	}
      	else
      	{
      		filePath = Environment.getExternalStorageDirectory() + "/cadie/" + fileName;
      		
      		if(!(new File(filePath).exists()))
      		{
      			filePath = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadie/" + fileName;
      		}
      	}
          
          mFile =  new File(filePath);
          
          if(mFile.exists())
          {
          	Log.d("CADIE S3", "File Exists");
          }
          else
          {
          	isTempFile = true;
          	Log.d("CADIE S3", "File does not exist" );
          }
          
          int i = fileName.lastIndexOf('.');
          
          if (i >= 0) {
          	mExtension = fileName.substring(i+1);
          }

          Log.d("CADIE S3", "File Extension - " + mExtension);
          
          mListener = new ProgressListener() 
          {
              @Override
              public void progressChanged(ProgressEvent event) 
              { 
              	Log.d("CADIE S3", "Upload Progress - " + (int)Global.mUpload.getProgress().getPercentTransferred());
                  
            	  if(fileUploadType.contentEquals("ALL_FILES"))
                  {
            		  if((int)Global.mUpload.getProgress().getPercentTransferred() != 0  && progressVal != (int)Global.mUpload.getProgress().getPercentTransferred() ) 
            		  {
            			  progressVal = (int)Global.mUpload.getProgress().getPercentTransferred();
            			  Log.d("CADIE S3", "Upload Progress Event Dispatch - " + progressVal);
                          try {
								FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "TRANSFER_PROGRESS^"+ progressVal);
							} catch (Exception e) {
								Log.d("CADIE S3", "Upload Progress Event Dispatch Error - " + e.toString());
							}
            		  }
                  }
                  
              	if(event.getEventCode() == ProgressEvent.COMPLETED_EVENT_CODE) 
                  {
                      Log.d("CADIE S3", "File Uploaded");
                      
                      Global.mUpload.removeProgressListener(mListener);
                      
                      if(mFile != null) 
                      {
                      	if(isTempFile)
                      		mFile.delete();
                      	
                          Log.d("CADIE S3", "File Deleted");
                      
                          if(fileUploadType.contentEquals("ALL_FILES"))
                          {
                          	String[] vStrings = serverResponse.split("\\^");
                          	
                          	try {
								if(vStrings[0].contentEquals("FILE_UPLOADED"))
								{
								  	FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "FILE_UPLOADED^" + vStrings[1]);
								}
								else
								{
									FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR "  );
								}
                          	} catch (Exception e) {
    							Log.d("CADIE S3", "File Upload Error - " + e.toString());
    						}
                          }
                          else//THUMB
                          {
                          	Log.d(CommonUtilities.TAG, "THUMB UPLOADED");
                          	try {
								FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "THUMB_UPLOADED");
                          	} catch (Exception e) {
    							Log.d(CommonUtilities.TAG, "THUMB UPLOADED Error - " + e.toString());
    						}
                          }
                          
                         //Global.mTransferManager.shutdownNow(false);
                          //Global.mTransferManager = null;
                          ((FileTransferService)UploadModel.this.context).stopSelf();
                      }
                  }
              	else if(event.getEventCode() == ProgressEvent.FAILED_EVENT_CODE) 
              	{
              		Global.mUpload.removeProgressListener(mListener);
              		//upload();
              		
              		try {
                        AmazonClientException e = Global.mUpload.waitForException();
                        Log.e("CADIE ","CADIE S3 Exception - "  +  e.toString() + " " + event.getBytesTransferred());
                        
                        try {
							FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR "  );
                        } catch (Exception e1) {
							Log.d(CommonUtilities.TAG, "CADIE S3 Exception - " + e1.toString());
						}
                        
                        Global.mTransferManager.shutdownNow(false);
                        Global.mTransferManager = null;
                        ((FileTransferService)UploadModel.this.context).stopSelf();
                    }
              		catch (InterruptedException e) {} 
              	}
              }
          };
		} catch (Exception e) {
			Log.d(CommonUtilities.TAG, "UPLOAD EXCEPTIOn -  " + e.toString());
		}
    }

    public Runnable getUploadRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                upload();
            }
        };
    }

    @Override
    public void abort() {
        if(Global.mUpload != null) {
            mStatus = Status.CANCELED;
            Global.mUpload.abort();
//            if(mFile != null) {
//                mFile.delete();
//            }
        }
    }

    @Override
    public Status getStatus() { return mStatus; }
    @Override
    public Transfer getTransfer() { return Global.mUpload; }

    @Override
    public void pause() {
        if(mStatus == Status.IN_PROGRESS) {
            if(Global.mUpload != null) {
                mStatus = Status.PAUSED;
                try {
                    mPersistableUpload = Global.mUpload.pause();
                } catch(PauseException e) { 
                    Log.d(TAG, "", e);
                }
            }
        }
    }

    @Override
    public void resume() {
        if(mStatus == Status.PAUSED) {
            mStatus = Status.IN_PROGRESS;
            if(mPersistableUpload != null) {
                //if it paused fine, resume
            	Global.mUpload = Global.mTransferManager.resumeUpload(mPersistableUpload);
            	Global.mUpload.addProgressListener(mListener);
                mPersistableUpload = null;
            } else {
                //if it was actually aborted, start a new one
                upload();
            }
        }
    }

    public void upload() {
        if(!mFile.exists() || mFile == null) {
            saveTempFile();
        }
        
        if(mFile.exists() ) {
            try {
            	Log.e(TAG, "S3 Uploading");
            	Log.e(TAG, "S3 File " + mFile.getPath());
            	
            	Global.mUpload = Global.mTransferManager.upload(
                        Constants.BUCKET_NAME,
                        strFileName ,
                        mFile);
            	
            	Log.e(TAG, "S3 File Name - " + strFileName);
            	
            	Global.mUpload.addProgressListener(mListener);
            } catch(Exception e) {
                Log.e(TAG, "S3 Upload error", e);
            }
        }
        else
        {
        	Log.e(TAG, "File does not exist");
        }
    }

    private void saveTempFile() {
        ContentResolver resolver = getContext().getContentResolver();
        InputStream in = null;
        FileOutputStream out = null;

        Log.e(TAG, "strFileName " + strFileName);
        
        try {
            in = resolver.openInputStream(getUri());
            mFile = File.createTempFile(
            		strFileName,
                    mExtension, 
                    getContext().getCacheDir());
            out = new FileOutputStream(mFile, false);
            byte[] buffer = new byte[1024];
            int read;
            while((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
        } catch(IOException e) {
            Log.e("CADIE", "Exception S3 - " + e.toString());
        } finally {
            if(in != null) {
                try { in.close(); } catch(IOException e) { Log.e("CADIE","Exception S3 - " + e.toString()); }
            }
            if(out != null) {
                try { out.close(); } catch(IOException e) { Log.e("CADIE", "Exception S3 - " + e.toString()); }
            }
        }
    }
}
