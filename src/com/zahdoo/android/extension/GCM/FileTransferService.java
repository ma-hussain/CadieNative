package com.zahdoo.android.extension.GCM;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.amazonaws.services.s3.transfer.TransferManager;
import com.zahdoo.android.extension.alarm.Global;
 
public class FileTransferService extends IntentService 
{
//    private AsyncTask<Void, Void, Void> mFileTask;
//    private AsyncTask<String, Void, String> fileTask;
    private ConnectionDetector cd;
    
    //public static String name;
    private String senderEmail;
    private String fileName;
    private String action;
    private String uniqueServerName;
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
	}
    
    public FileTransferService()
    {
    	super("FileTransferService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        cd = new ConnectionDetector(getApplicationContext());
 
        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
//        	Toast.makeText(FileTransferActivity.this,
//                    "Internet Connection Error...\n" +
//                    "Please connect to working Internet connection", Toast.LENGTH_LONG).show();
        	FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_INTERNET_CONNECTION");
	    	stopSelf();
            return;
        }
        
        // Getting msg, email from intent
       // Intent i = getIntent();Bundle extras = intent.getExtras();
        fileName = intent.getStringExtra("fileName");
        uniqueServerName = intent.getStringExtra("uniqueServerName");
        action = intent.getStringExtra("action");     
        senderEmail = intent.getStringExtra("senderEmail");
        
        final Context context = FileTransferService.this;
        Global.gContext	= context;
        Log.d(CommonUtilities.TAG, "File Transfer Action - "  + action );
        
        try {
        	if( action.contentEquals("UPLOAD"))
            {
//                mFileTask = new AsyncTask<Void, Void, Void>() 
//                {
//                    @Override
//    	            protected Void doInBackground(Void... params) {
    	                            
    	            	try {
							uploadFiles( fileName,uniqueServerName,senderEmail );
						} catch (Throwable e) {
							Log.d(CommonUtilities.TAG, "mTransferManager error " + e.toString());
							e.printStackTrace();
						}
//    	                return null;
//    	            }
//    	     
//                    @Override
//                    protected void onPostExecute(Void result) {
//                    	mFileTask = null;
                    	Log.d(CommonUtilities.TAG, "FILE UPLOADED");
                    	FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "UPLOADED");
                    	stopSelf();
//                    }
//                };
//                mFileTask.execute(null, null, null);
            }
        	else if( action.contentEquals("UPLOAD_PROFILE_PIC") || action.contentEquals("UPLOAD_GROUP_PROFILE_PIC"))
            {
//                mFileTask = new AsyncTask<Void, Void, Void>() 
//                {
//                    @Override
//    	            protected Void doInBackground(Void... params) {
    	                            
    	            	try {
							uploadFiles( fileName,uniqueServerName,senderEmail );
						} catch (Throwable e) {
							Log.d(CommonUtilities.TAG, "mTransferManager error " + e.toString());
							e.printStackTrace();
						}
//    	                return null;
//    	            }
//    	     
//                    @Override
//                    protected void onPostExecute(Void result) {
//                    	mFileTask = null;
                    	Log.d(CommonUtilities.TAG, "FILE UPLOADED");
                    	FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "UPLOADED");
                    	stopSelf();
//                    }
//                };
//                mFileTask.execute(null, null, null);
            }
        	else if( action.contentEquals("UPLOAD_ALL_FILES"))
            {
//        		fileTask = new AsyncTask<String, Void, String>() 
//                {
//                    @Override
//    	            protected String doInBackground(String... params) {
                    	
                    	Log.d(CommonUtilities.TAG, "mTransferManager UPLOAD_ALL_FILES Initialize " );
                    	if(Global.mTransferManager == null)
                    		Global.mTransferManager = new TransferManager(Util.getCredProvider(FileTransferService.this));
        	       	 	Log.d(CommonUtilities.TAG, "mTransferManager UPLOAD_ALL_FILES Initialized 2 " );
	            		
    	                       
                    	String msg = "";
                    	
    	            	try {
    	            		uploadAllFilesS3(fileName,uniqueServerName,senderEmail );
    	            		//msg = uploadAllFiles(fileName,uniqueServerName,senderEmail );
						} catch (Throwable e) {
							e.printStackTrace();
						}
//    	                return msg;
//    	            }
//    	     
//                    @Override
//                    protected void onPostExecute(String msg) {
        		    	Log.i(CommonUtilities.TAG, "SERVER RESPONSE MSG - " + msg);
        		    	
//        		    	fileTask = null;
//        		    	
//                    }
//                };
//                fileTask.execute(senderEmail);
            }
        	else if( action.contentEquals("UPLOAD_THUMB"))
            {
//                mFileTask = new AsyncTask<Void, Void, Void>() 
//                {
//                    @Override
//    	            protected Void doInBackground(Void... params) 
//                    {
                    	Log.d(CommonUtilities.TAG, "mTransferManager UPLOAD_THUMB Initialize " );
                    	if(Global.mTransferManager == null)
                    		Global.mTransferManager = new TransferManager(Util.getCredProvider(FileTransferService.this));
        	       	 	Log.d(CommonUtilities.TAG, "mTransferManager UPLOAD_THUMB Initialized 2 " );
	            		
    	                            
    	            	try {
    	            		uploadFilesS3( fileName,uniqueServerName,senderEmail );
							//uploadFiles( fileName,uniqueServerName,senderEmail );
						} catch (Throwable e) {
							e.printStackTrace();
						}
//    	                return null;
//    	            }
//    	     
//                    @Override
//                    protected void onPostExecute(Void result) {
//                    	mFileTask = null;
//                    }
//                };
//                mFileTask.execute(null, null, null);
            }
        	else if( action.contentEquals("DOWNLOAD_SERVER_FILES"))
            {
//        		fileTask = new AsyncTask<String, Void, String>() 
//                {
//                    @Override
//    	            protected String doInBackground(String... params) 
//                    {
                    	Log.d(CommonUtilities.TAG, "mTransferManager Initialize " );
                    	if(Global.mTransferManager == null)
                    		Global.mTransferManager = new TransferManager(Util.getCredProvider(FileTransferService.this));
        	       	 	Log.d(CommonUtilities.TAG, "mTransferManager Initialized 2 " );
	            		
                    	String msg = "";
                    	
    	            	DownloadServerFilesS3(fileName);
//    	                return msg;
//    	            }
//    	     
//                    @Override
//                    protected void onPostExecute(String msg) {
//                    	fileTask = null;
//                    }
//                };
//                fileTask.execute(senderEmail);
            }
        	else if( action.contentEquals("THUMB_DOWNLOAD"))
            {
//                mFileTask = new AsyncTask<Void, Void, Void>() 
//                {
//                    @Override
//    	            protected Void doInBackground(Void... params) 
//                    {
                    	Log.d(CommonUtilities.TAG, "mTransferManager THUMB_DOWNLOAD Initialize " );
                    	if(Global.mTransferManager == null)
                    		Global.mTransferManager = new TransferManager(Util.getCredProvider(FileTransferService.this));
        	       	 	Log.d(CommonUtilities.TAG, "mTransferManager THUMB_DOWNLOAD Initialized 2 " );
        	       	 	
        	       	 	DownloadFilesS3( fileName );
//    	                return null;
//    	            }
//    	     
//                    @Override
//                    protected void onPostExecute(Void result) 
//                    {
//                    	mFileTask = null;
//                    }
//                };
//                mFileTask.execute(null, null, null);
            }
            else //DOWNLOAD
            {
//            	mFileTask = new AsyncTask<Void, Void, Void>() 
//                {
//                    @Override
//    	            protected Void doInBackground(Void... params) 
//                    {
                    	DownloadFiles(senderEmail, fileName );
//    	                return null;
//    	            }
//    	     
//                    @Override
//                    protected void onPostExecute(Void result) {
//                    	mFileTask = null;
                    	Log.d(CommonUtilities.TAG, "FILE DOWNLOADED");
                    	//FileTransferFunction.gcmCon.dispatchStatusEventAsync("DOWNLOADED", "OK");
                    	FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "DOWNLOADED");
                    	stopSelf();
//                    }
//                };
//                mFileTask.execute(null, null, null);
            }
		} catch (Exception e) {
			Log.d(CommonUtilities.TAG , "File Transfer Exception - " + e.toString());
			FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR " + e.toString() );
			stopSelf();
		}
	}
    
    private void uploadFilesS3( String fileName, String uniqueServerName, String senderEmail) throws Throwable
    {
    	try
    	{
	    	String pathToOurFile = new String();
	    	
	    	if(action.contentEquals("UPLOAD_THUMB")){
	    		File vidThRoot = new File("/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/thumbnails");
	            //File vidThRoot = new File(Environment.getExternalStorageDirectory() + "/cadie");
	            
	            if (!vidThRoot.exists()) {
	            	vidThRoot.mkdirs();
	            }
	    	}
	    	
	    	if(action.contentEquals("UPLOAD_THUMB")){
	    		uniqueServerName = "th_" + uniqueServerName;
	    		Log.d("CADIE GCM", "UPLOADING THUMB");
	    		pathToOurFile = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/thumbnails/th_" + fileName;
	    	}
	    	else
	    	{
	    		pathToOurFile = Environment.getExternalStorageDirectory() + "/cadie/" + fileName;
	    		
	    		if(!(new File(pathToOurFile).exists()))
	    			pathToOurFile = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadie/" + fileName;
	    	}
	    	
	    	Log.d(CommonUtilities.TAG, "FILE PATH - " + pathToOurFile );
	    	UploadModel model = new UploadModel(this, Uri.fromFile(new File(pathToOurFile)),  uniqueServerName , "THUMB" , "");
	        new Thread(model.getUploadRunnable()).run();
	    	Log.d("CADIE S3", "After upload");
    	}
    	catch (Exception ex )
    	{
    		Log.d(CommonUtilities.TAG, "UPLOAD EXCEPTION " + ex.toString());
    		FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR " + ex.toString() );
    		stopSelf();
    	}
    	catch(Throwable e) {
    		Log.d(CommonUtilities.TAG, "THROWABLE UPLOAD EXCEPTION " + e);
    		Log.d(CommonUtilities.TAG , "File Transfer Exception - " + e.toString());
			FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR " + e.toString() );
			stopSelf();
    	}
    }
    
    public void DownloadFilesS3( String fileName )
    {
        try
        {
        	DownloadModel model = new DownloadModel(this, fileName, "THUMB_DOWNLOAD");
            model.download();
        } 
        catch (Exception e) 
        {
        	Log.d(CommonUtilities.TAG , e.toString() + " File Download ERROR  ");
        	FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR "  );
        }
    }
    
    public void DownloadServerFilesS3( String fileName )
    {
        try
        {
        	DownloadModel model = new DownloadModel(this, fileName, "DOWNLOAD_SERVER_FILES");
            model.download();
        } 
        catch (Exception e) 
        {
        	Log.d(CommonUtilities.TAG , e.toString() + " File Download ERROR  ");
        	FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR "  );
        }
    }
    
    public void DownloadFiles(String filePath, String fileName )
    {
        try{
        	String urlpath = "";
        	
        	if(action.contentEquals("THUMB_DOWNLOAD"))
         		urlpath = "http://54.88.103.38/gcm_server_php/CadieServerFiles/"+ fileName.toString();
        	else
        		urlpath = "http://54.88.103.38/gcm_server_php/CadieUpload/"+ filePath.toString() +"/"+ fileName.toString();

        	Log.d(CommonUtilities.TAG , "Server File Path - " + urlpath);
            URL url = new URL(urlpath.toString()); // Your given URL.
            URLConnection c = url.openConnection();
            String PATH = new String();
            
//            if(action.contentEquals("DOWNLOAD_THUMB")){
//	    		File vidThRoot = new File("/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/thumbnails");
//	            //File vidThRoot = new File(Environment.getExternalStorageDirectory() + "/cadie");
//	            
//	            
//	            if (!vidThRoot.exists()) {
//	            	vidThRoot.mkdirs();
//	            }
//	    	}
            
            if(action.contentEquals("THUMB_DOWNLOAD"))
            	PATH = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/thumbnails";
            else
            {
            	if(action.contentEquals("DOWNLOAD_SHARED_DB"))
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
            
            Log.d(CommonUtilities.TAG , "Device File Path - " + PATH);
            File file = new File(PATH); // PATH = /mnt/sdcard/download/
            
            if (!file.exists()) {
                file.mkdirs();
            }
            
            if (!file.exists()) {
            	file.getParentFile().mkdirs();
            }
           
            Log.d("CADIE", "FILE DOWNLOAD NAME - " + fileName);
            File outputFile = new File(file, fileName.toString());           
            FileOutputStream fos = new FileOutputStream(outputFile);
            InputStream is = c.getInputStream(); // Get from Server and Catch In Input Stream Object.

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1); // Write In FileOutputStream.
            }
            
            Log.d(CommonUtilities.TAG , "File downloaded to device  ");
            fos.flush();
            fos.close();
            is.close();
        } 
        catch (Exception e) 
        {
        	Log.d(CommonUtilities.TAG , e.toString() + " File Download ERROR  ");
        }
    }
    
    private String downloadServerFiles(String fileName )
    {
    	String resp = "";
        try{
        	String urlpath = "http://54.88.103.38/gcm_server_php/CadieServerFiles/"+ fileName.toString();

        	Log.d(CommonUtilities.TAG , "Server File Path - " + urlpath);
            URL url = new URL(urlpath.toString()); // Your given URL.

            URLConnection c = url.openConnection();
            
            String PATH = new String();
            
        	File testSdCard = new File(Environment.getExternalStorageDirectory() + "/cadie");

        	if(!testSdCard.exists())
        		testSdCard.mkdir();

        	if(testSdCard.exists())
        	{
        		PATH = Environment.getExternalStorageDirectory() + "/cadie";
        	}
        	else
        	{
        		PATH = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadie";
        	}
            
            Log.d(CommonUtilities.TAG , "Device File Path - " + PATH);
            File file = new File(PATH); // PATH = /mnt/sdcard/download/
            
            if (!file.exists()) {
                file.mkdirs();
            }
            
            if (!file.exists()) {
            	file.getParentFile().mkdirs();
            }
           
            Log.d("CADIE", "FILE DOWNLOAD NAME - " + fileName);
            
            File outputFile = new File(file, fileName.toString());           
            FileOutputStream fos = new FileOutputStream(outputFile);

            InputStream is = c.getInputStream(); // Get from Server and Catch In Input Stream Object.

            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1); // Write In FileOutputStream.
            }
            
            Log.d(CommonUtilities.TAG , "File downloaded to device  ");
            
            fos.flush();
            fos.close();
            is.close();
            
            resp =  "DOWNLOAD_SUCCESSFUL";
        } 
        catch (IOException e) 
        {
        	Log.d(CommonUtilities.TAG , e.toString() + " Download ERROR  ");
        	if(e.toString().contains("Network is unreachable")){
        		//alertbox("CADIE Error","No network found, please check your wifi connection and try again.");
        	}
        	else if(e.toString().contains("java.io.FileNotFoundException")){
        		//alertbox("CADIE Error","Unable to save the file on device or SD card.");
        	}
        	
        	resp = "ERROR";
        }
        finally
        {
        	return resp;
        }
    }
    
    
    private void uploadFiles( String fileName, String uniqueServerName, String senderEmail) throws Throwable
    {
    	HttpURLConnection connection = null;
    	DataOutputStream outputStream = null;
    	DataInputStream inputStream = null;
    	
    	try
    	{
	    	String pathToOurFile = new String();
	    	
	    	
	    	if(action.contentEquals("UPLOAD_THUMB")){
	    		File vidThRoot = new File("/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/thumbnails");
	            //File vidThRoot = new File(Environment.getExternalStorageDirectory() + "/cadie");
	            
	            
	            if (!vidThRoot.exists()) {
	            	vidThRoot.mkdirs();
	            }
	    	}
	    	
	    	if(action.contentEquals("UPLOAD_THUMB")){
	    		uniqueServerName = "th_" + uniqueServerName;
	    		Log.d("CADIE GCM", "UPLOADING THUMB");
	    		pathToOurFile = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/thumbnails/th_" + fileName;
	    	}
	    	else if(action.contentEquals("UPLOAD_PROFILE_PIC") || action.contentEquals("UPLOAD_GROUP_PROFILE_PIC"))
	    	{
	    		pathToOurFile = Environment.getExternalStorageDirectory() + "/cadie/temp_" + fileName;
	    		
	    		if(!(new File(pathToOurFile).exists()))
	    		{
	    			pathToOurFile = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadie/temp_" + fileName;
	    		}
	    	}
	    	else
	    	{
	    		pathToOurFile = Environment.getExternalStorageDirectory() + "/cadie/" + fileName;
	    		
	    		if(!(new File(pathToOurFile).exists()))
	    		{
	    			pathToOurFile = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadie/" + fileName;
	    		}
	    	}
	    	
	    	Log.d(CommonUtilities.TAG, "FILE PATH - " + pathToOurFile );
	    	
	    	String urlServer = "";
	    	
	    	if(action.contentEquals("UPLOAD_THUMB"))
	    	{
	    		urlServer = "http://54.88.103.38/gcm_server_php/GCM_cadie_thumb_upload.php";
	    	}
	    	else if(action.contentEquals("UPLOAD_PROFILE_PIC"))
	    	{
	    		urlServer = "http://54.88.103.38/gcm_server_php/Cadie_ProfilePic_Upload.php";
	    	}
	    	else if(action.contentEquals("UPLOAD_GROUP_PROFILE_PIC"))
	    	{
	    		urlServer = "http://54.88.103.38/gcm_server_php/Cadie_Group_ProfilePic_Upload.php";
	    	}
	    	else
	    	{
	    		urlServer = "http://54.88.103.38/gcm_server_php/Cadie_handle_upload.php";//GCM_cadie_file_upload.php
	    	}
	    	
	    	String lineEnd = "\r\n";
	    	String twoHyphens = "--";
	    	String boundary =  "*****";
	
	    	int bytesRead, bytesAvailable, bufferSize;
	    	byte[] buffer;
	    	int maxBufferSize = 1*1024*1024;

    	
	    	FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile) );
	
	    	URL url = new URL(urlServer);
	    	connection = (HttpURLConnection) url.openConnection();
	
	    	// Allow Inputs & Outputs
	    	connection.setDoInput(true);
	    	connection.setDoOutput(true);
	    	connection.setUseCaches(false);
	    	//connection.setChunkedStreamingMode(0);
	
	    	// Enable POST method
	    	connection.setRequestMethod("POST");
	
	    	connection.setRequestProperty("Connection", "Keep-Alive");
	    	connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
	
	    	outputStream = new DataOutputStream( connection.getOutputStream() );
	    	
	    	outputStream.writeBytes(twoHyphens + boundary + lineEnd);
	    	outputStream.writeBytes("Content-Disposition: form-data; name=\"email\""
	                + lineEnd);
	    	outputStream.writeBytes(lineEnd);
	    	outputStream.writeBytes(senderEmail);
	    	outputStream.writeBytes(lineEnd);
	    	
	    	
	    	
	    	outputStream.writeBytes(twoHyphens + boundary + lineEnd);
	    	outputStream.writeBytes("Content-Disposition: form-data; name=\"fileName\""
	                + lineEnd);
	    	outputStream.writeBytes(lineEnd);
	    	outputStream.writeBytes(uniqueServerName);
	    	outputStream.writeBytes(lineEnd);
	    	
	    	
	    	
	    	outputStream.writeBytes(twoHyphens + boundary + lineEnd);
	    	outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
	                + pathToOurFile + "\"" + lineEnd);
	    	outputStream.writeBytes(lineEnd);

	    	bytesAvailable = fileInputStream.available();
	    	bufferSize = Math.min(bytesAvailable, maxBufferSize);
	    	buffer = new byte[bufferSize];
	
	    	// Read file
	    	bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	
	    	while (bytesRead > 0)
	    	{
		    	outputStream.write(buffer, 0, bufferSize);
		    	bytesAvailable = fileInputStream.available();
		    	bufferSize = Math.min(bytesAvailable, maxBufferSize);
		    	bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	    	}
	
	    	outputStream.writeBytes(lineEnd);
	    	outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
	
	    	// Responses from the server (code and message)
	    	int serverResponseCode = connection.getResponseCode();
	    	
	    	InputStream inStream = null; 
	    	 
	    	if (serverResponseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) { 
	    		Log.d(CommonUtilities.TAG, "Error - IF > ");
	    		inStream = connection.getErrorStream();    
	    	}  
	    	else{
	    		Log.d(CommonUtilities.TAG,  "Error - ELSE > ");
	    		inStream = connection.getInputStream();  
	    	} 
	    	
	    	Log.d(CommonUtilities.TAG, serverResponseCode +  " << - >> " + HttpURLConnection.HTTP_INTERNAL_ERROR);
	    	
	    	Log.d(CommonUtilities.TAG, inStream.getClass().getName()+ "\n" + inStream.toString() + "\n"
	    			+ "SERVER RESPONSE CODE - >>> " + serverResponseCode);
	    	
	    	//Log.d(CommonUtilities.TAG, "SERVER RESPONSE CODE - " + serverResponseCode);
	    	String serverResponseMessage = connection.getResponseMessage();
	    	
	    	Log.d(CommonUtilities.TAG, "SERVER RESPONSE MESSAGE - " + serverResponseMessage);
	
	    	fileInputStream.close();
    	}
    	catch (Exception ex )
    	{
    		Log.d(CommonUtilities.TAG, "UPLOAD EXCEPTION " + ex.toString());
    		
    		//Exception handling
    		//Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG ).show();
    	}
    	catch(Throwable e) {
    		Log.d(CommonUtilities.TAG, "THROWABLE UPLOAD EXCEPTION " + e);
    		Log.d(CommonUtilities.TAG , "File Transfer Exception - " + e.toString());
			FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR " + e.toString() );
			stopSelf();
    	}
    	finally{
    		//if(connection != null)
    		//	connection.disconnect();
	    	
	    	outputStream.flush();
	    	outputStream.close();
    	}
    	
    }
    
    
    private void uploadAllFilesS3( String fileName, String uniqueServerName, String senderEmail) throws Throwable
    {
    	String pathToOurFile = new String();
    	
		pathToOurFile = Environment.getExternalStorageDirectory() + "/cadie/" + fileName;
		
		if(!(new File(pathToOurFile).exists()))
		{
			pathToOurFile = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadie/" + fileName;
		}
    	
    	Log.d(CommonUtilities.TAG, "FILE PATH - " + pathToOurFile );

    	String serverResponse = ServerUtilities.insertS3FileMap(uniqueServerName);
    	
    	UploadModel model = new UploadModel(this, Uri.fromFile(new File(pathToOurFile)), uniqueServerName , "ALL_FILES", serverResponse);
        new Thread(model.getUploadRunnable()).run();
    	
    	Log.d("CADIE S3", "After upload");
    	
    }
    
    private String uploadAllFiles( String fileName, String uniqueServerName, String senderEmail) throws Throwable
    {
    	HttpURLConnection connection = null;
    	DataOutputStream outputStream = null;
    	DataInputStream inputStream = null;
    	
    	String response = "";
    	
    	try
    	{
	    	String pathToOurFile = new String();
	    	
    		pathToOurFile = Environment.getExternalStorageDirectory() + "/cadie/" + fileName;
    		
    		if(!(new File(pathToOurFile).exists()))
    		{
    			pathToOurFile = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadie/" + fileName;
    		}
	    	
	    	Log.d(CommonUtilities.TAG, "FILE PATH - " + pathToOurFile );
	    	String urlServer = "http://54.88.103.38/gcm_server_php/GCM_cadie_file_upload.php";
	    	String lineEnd = "\r\n";
	    	String twoHyphens = "--";
	    	String boundary =  "*****";
	
	    	int bytesRead, bytesAvailable, bufferSize;
	    	byte[] buffer;
	    	int maxBufferSize = 1*1024*1024;

    	
	    	FileInputStream fileInputStream = new FileInputStream(new File(pathToOurFile) );
	
	    	URL url = new URL(urlServer);
	    	connection = (HttpURLConnection) url.openConnection();
	    	
	    	// Allow Inputs & Outputs
	    	connection.setDoInput(true);
	    	connection.setDoOutput(true);
	    	connection.setUseCaches(false);
	    	//connection.setChunkedStreamingMode(0);
	    	
	    	// Enable POST method
	    	connection.setRequestMethod("POST");
	    	
	    	connection.setRequestProperty("Connection", "Keep-Alive");
	    	connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
	    	
	    	outputStream = new DataOutputStream( connection.getOutputStream() );
	    	
	    	outputStream.writeBytes(twoHyphens + boundary + lineEnd);
	    	outputStream.writeBytes("Content-Disposition: form-data; name=\"email\""
	                + lineEnd);
	    	outputStream.writeBytes(lineEnd);
	    	outputStream.writeBytes(senderEmail);
	    	outputStream.writeBytes(lineEnd);
	    	
	    	
	    	outputStream.writeBytes(twoHyphens + boundary + lineEnd);
	    	outputStream.writeBytes("Content-Disposition: form-data; name=\"fileName\""
	                + lineEnd);
	    	outputStream.writeBytes(lineEnd);
	    	outputStream.writeBytes(uniqueServerName);
	    	outputStream.writeBytes(lineEnd);
	    	
	    	
	    	outputStream.writeBytes(twoHyphens + boundary + lineEnd);
	    	outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\""
	                + pathToOurFile + "\"" + lineEnd);
	    	outputStream.writeBytes(lineEnd);

	    	bytesAvailable = fileInputStream.available();
	    	bufferSize = Math.min(bytesAvailable, maxBufferSize);
	    	buffer = new byte[bufferSize];
	    	// Read file
	    	bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	    	while (bytesRead > 0)
	    	{
		    	outputStream.write(buffer, 0, bufferSize);
		    	bytesAvailable = fileInputStream.available();
		    	bufferSize = Math.min(bytesAvailable, maxBufferSize);
		    	bytesRead = fileInputStream.read(buffer, 0, bufferSize);
	    	}
	    	outputStream.writeBytes(lineEnd);
	    	outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
	    	
	    	// Responses from the server (code and message)
	    	int serverResponseCode = connection.getResponseCode();
	    	if (serverResponseCode != 200) {
	             // throw new IOException("Post failed with error code " + serverResponseCode);
	        }
	        else{
	            	// Get the response from Server PHP file 
	                BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	                String line = "";
	                
	                response = "";
	                
	                while ((line = rd.readLine()) != null) 
	                {
	                	response += line;
	                }
	                
	                Log.e("Post Response ", "> " + response); //CODE SENT || REG ERROR
	               
	        }
	    	
	    	
	    	InputStream inStream = null; 
	    	 
	    	if (serverResponseCode == HttpURLConnection.HTTP_INTERNAL_ERROR) { 
	    		Log.d(CommonUtilities.TAG, "Error - IF > ");
	    		inStream = connection.getErrorStream();    
	    	}  
	    	else{
	    		Log.d(CommonUtilities.TAG,  "Error - ELSE > ");
	    		inStream = connection.getInputStream();  
	    	} 
	    	
	    	Log.d(CommonUtilities.TAG, serverResponseCode +  " << - >> " + HttpURLConnection.HTTP_INTERNAL_ERROR);
	    	
	    	Log.d(CommonUtilities.TAG, inStream.getClass().getName()+ "\n" + inStream.toString() + "\n"
	    			+ "SERVER RESPONSE CODE - > " + serverResponseCode);
	    	
	    	//Log.d(CommonUtilities.TAG, "SERVER RESPONSE CODE - " + serverResponseCode);
	    	String serverResponseMessage = connection.getResponseMessage();
	    	
	    	Log.d(CommonUtilities.TAG, "SERVER RESPONSE MESSAGE - " + serverResponseMessage);
	
	    	fileInputStream.close();
    	}
    	catch (Exception ex )
    	{
    		Log.d(CommonUtilities.TAG, "UPLOAD EXCEPTION " + ex.toString());
    		
    		//Exception handling
    		//Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG ).show();
    	}
    	catch(Throwable e) {
    		Log.d(CommonUtilities.TAG, "THROWABLE UPLOAD EXCEPTION " + e);
    		Log.d(CommonUtilities.TAG , "File Transfer Exception - " + e.toString());
			FileTransferFunction.gcmCon.dispatchStatusEventAsync("REGISTERED", "ERROR " + e.toString() );
			stopSelf();
    	}
    	finally{
    		//if(connection != null)
    			//connection.disconnect();
	    	
	    	outputStream.flush();
	    	outputStream.close();
	    	
	    	
	    	if(response.contentEquals(""))
	    		response = "ERROR^0";
	    	
	    	return response;
    	}
    	
    }
}