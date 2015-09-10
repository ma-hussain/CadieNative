package com.zahdoo.android.extension.camera;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.UUID;

import com.zahdoo.android.extension.GCM.CommonUtilities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Toast;


public class BuiltInAttachment extends Activity {
    /** Called when the activity is first created. */

	private final int CROP_FROM_CAMERA = 1;
	private final int ATTACH_PROFILE_PIC=2;
	private final int ATTACH_IMAGE=3;
	private final int ATTACH_VIDEO=4;
	private final int ATTACH_FILE=5;
	private final int ATTACH_AUDIO=6;
	private final int ATTACH_IMAGE_CHAT=7;
	private final int ATTACH_VIDEO_CHAT=8;
	private final int ATTACH_FILE_CHAT=9;
	private final int ATTACH_AUDIO_CHAT=10;
	private final int ATTACH_CHAT_WALLPAPER = 11;
	 
	private Uri outputFileUri;
	private String cType 		= "";
	private String cAction 		= "";
	private String cFileName 	= "";
	private String profilePicFileName 	= "";
	private String cFileCreatedTime = UUID.randomUUID().toString();
	private String cLocation 	= "";
	private long   cNoteSize 	= 0;
	private Boolean vidAttachError= false;
	private Boolean uriDataContentType = false;//file-> false, content-> true
    private String mSelectedImagePath;
    private String fileType;
    private long xFileSize = 0;
	private FileOutputStream fileOuputStream;
    private ByteArrayOutputStream baos;
    private File sd;
    private File source;
    private File destination;
    private FileInputStream fis;
    private Bitmap imageBitmap;

//    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//    retriever.setDataSource(your_data_source);
//    String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//    long timeInmillisec = Long.parseLong( time );
//    long duration = timeInmillisec / 1000;
//    long hours = duration / 3600;
//    long minutes = (duration - hours * 3600) / 60;
//    long seconds = duration - (hours * 3600 + minutes * 60);
    
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
	}
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras(); 
        
        uriDataContentType = false;
        
        if(extras != null)
        {
        	cType 			= extras.getString("cType");
        	cAction 		= extras.getString("cAction");
        	cFileName		= extras.getString("cFileName");
        	cLocation 		= extras.getString("cLocation");
        	cNoteSize		= Long.parseLong(extras.getString("cNoteSize"));
        }
        
        Log.d("CADIE FILE", "11 BuiltInAttachment - " + cType);
        
        if(cType.equals("ATTACH_PICTURE")){
        	AttachPhoto(false);
        }
        else if(cType.equals("ATTACH_AUDIO")){
        	AttachAudio(false);
        }
        else if(cType.equals("ATTACH_VIDEO")){
        	AttachVideo(false);
        }
        else if(cType.equals("ATTACH_FILE")){
        	AttachFile(false);
        }
        else if(cType.equals("APP_LAUNCH")){
        	LaunchApp();
        }
        else if(cType.equals("ATTACH_PROFILE_PIC")){
        	profilePicFileName = cFileName;
        	Log.d("CADIE FILE", "22 BuiltInAttachment - " + profilePicFileName);
        	AttachProfilePic();
        }
        else if(cType.equals("ATTACH_PICTURE_CHAT")){
        	AttachPhoto(true);
        }
        else if(cType.equals("ATTACH_AUDIO_CHAT")){
        	AttachAudio(true);
        }
        else if(cType.equals("ATTACH_VIDEO_CHAT")){
        	AttachVideo(true);
        }
        else if(cType.equals("ATTACH_FILE_CHAT")){
        	AttachFile(true);
        }
        else if(cType.equals("ATTACH_CHAT_WALLPAPER")){
        	AttachPhoto(false);
        }
    }
	 
	private void LaunchApp() {
	    List<PackageInfo> packs = getPackageManager().getInstalledPackages(0);
	    
	    Boolean isAppInstalled = false;
	    
	    for(int i=0;i<packs.size();i++) {
	        PackageInfo p = packs.get(i);
	        
	        if(p.applicationInfo.loadLabel(getPackageManager()).toString().toLowerCase().contentEquals(cAction))
	        {
	        	Toast.makeText(getApplicationContext(), "\nLaunching app " + cAction + "...\n", Toast.LENGTH_LONG).show();
	        	
	        	 Intent intnt;
	        	 PackageManager manager = getPackageManager();
	        	 try {
	        		 intnt = manager.getLaunchIntentForPackage(p.packageName);
	        	     if (intnt == null)
	        	         throw new PackageManager.NameNotFoundException();
	        	     intnt.addCategory(Intent.CATEGORY_LAUNCHER);
	        	     startActivity(intnt);
	        	     
	        	     
	        	 } catch (PackageManager.NameNotFoundException e) {  	 }
	        	 
	        	 isAppInstalled = true;
	        	 break;
	        }
	    }
	    
	    try {
			if(isAppInstalled ){
				CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED","YES_CAPTURED");
			}
			else{
				Toast.makeText(getApplicationContext(), "\nApplication " + cAction + " not found...\n", Toast.LENGTH_LONG).show();
				CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED","NOT_CAPTURED");
			}
		}catch (Exception e) {
			Log.i("CADIE FILE", "LaunchApp Exception = " + e.toString());
		}
	    
	    finish();
	}
	
	
    private void AttachPhoto(Boolean isPhotoForChat)  
    {
    	cFileName = UUID.randomUUID().toString();
    	cLocation = "SDCARD";
    	
    	try {
    		Toast.makeText(getApplicationContext(), "Opening gallery ...  Please wait ...", Toast.LENGTH_SHORT).show();
            
        	Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);

            if(isPhotoForChat)
            	startActivityForResult(intent, ATTACH_IMAGE_CHAT);
            else
            	 startActivityForResult(intent, ATTACH_IMAGE);
		}
    	catch (Exception e) {
    		try {
				CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED","NOT_CAPTURED");
			} catch (Exception e1) {
				Log.d("CADIE FILE", "AttachPhoto - " + e1.toString());
			} 
        	Toast.makeText(getApplicationContext(), "Sorry image cannot be attached...", Toast.LENGTH_SHORT).show();
		}
	}
    
    private void AttachProfilePic() 
    {
    	cFileName = UUID.randomUUID().toString();
    	cLocation = "SDCARD";
    	Log.d("CADIE FILE", "AttachProfilePic - " + cType);
    	try {
    		Toast.makeText(getApplicationContext(), "Opening gallery ...  Please wait ...", Toast.LENGTH_SHORT).show();
    		Log.d("CADIE FILE", "AttachProfilePic - " + cType);
    		
    		Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(intent, ATTACH_PROFILE_PIC);
		}
    	catch (Exception e) {
    		try {
				CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED","NOT_CAPTURED");
    		} catch (Exception e1) {
				Log.d("CADIE FILE", "AttachProfilePic - " + e1.toString());
			}
        	Toast.makeText(getApplicationContext(), "Sorry image cannot be attached...", Toast.LENGTH_SHORT).show();
		}
	}
    
    private void AttachVideo(Boolean isVideoForChat){
    	try {
        	Toast.makeText(getApplicationContext(), "Loading Videos ...  Please wait ...", Toast.LENGTH_SHORT).show();
            
        	Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_PICK);

            if(isVideoForChat)
            	startActivityForResult(intent, ATTACH_VIDEO_CHAT);
            else
            	startActivityForResult(intent, ATTACH_VIDEO);
		}
    	catch (Exception e) {
    		vidAttachError = true;
    		cType = "ATTACH_PICTURE";
    		
    		 if(isVideoForChat)
    			 AttachPhoto(true);
    		 else
    			 AttachPhoto(false);
		}
    }
    
    private void AttachFile(Boolean isFileForChat) 
    {
    	try {
        	Toast.makeText(getApplicationContext(), "Loading Files ...  Please wait ...", Toast.LENGTH_SHORT).show();
        	
        	Intent intent = new Intent();
        	intent.setType("*/*");
        	intent.addCategory(Intent.CATEGORY_OPENABLE);  
            intent.setAction(Intent.ACTION_GET_CONTENT);

            if(isFileForChat)
            	startActivityForResult(intent, ATTACH_FILE_CHAT);
            else
            	startActivityForResult(intent, ATTACH_FILE);
		}
    	catch (Exception e) {
    		try {
				CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED","NOT_CAPTURED");
			} catch (Exception e1) {
				Log.d("CADIE FILE", "AttachFile - " + e1.toString());
			}
        	Toast.makeText(getApplicationContext(), "Sorry file cannot be attached...", Toast.LENGTH_SHORT).show();
		}
	}
    
    private void AttachAudio(Boolean isAudioForChat) 
    {
    	try {
        	Toast.makeText(getApplicationContext(), "Loading Files ...  Please wait ...", Toast.LENGTH_SHORT).show();
            
        	Intent intent = new Intent();
        	intent.setType("audio/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            if(isAudioForChat)
            	startActivityForResult(intent, ATTACH_AUDIO_CHAT);
            else
            	startActivityForResult(intent, ATTACH_AUDIO);
		}
    	catch (Exception e) {
    		try {
				CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED","NOT_CAPTURED");
			} catch (Exception e1) {
				Log.d("CADIE FILE", "AttachAudio - " + e1.toString());
			}
        	Toast.makeText(getApplicationContext(), "Sorry audio cannot be attached...", Toast.LENGTH_SHORT).show();
		}
	}
    
  ////
    private void doCrop(int outputX ,int outputY ) 
    {
    	int xCrop = 0;
    	
    	Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");
        
        xCrop = 1;
        
        List<ResolveInfo> list = getPackageManager().queryIntentActivities( intent, 0 );
        if(outputX == 0)
        	outputX = 300;
        
        if(outputY == 0)
        	outputY = 300;
        
        int size = list.size();
        
        xCrop = 2;
        
        if (size == 0) {	        
        	Toast.makeText(this, "Can not find image crop app", Toast.LENGTH_SHORT).show();
        	try {
				CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED","NOT_CAPTURED");
        	} catch (Exception e1) {
				Log.d("CADIE FILE", "doCrop 0 - " + e1.toString());
			}
        	finish();
        } else {
        	
        	try {
        		xCrop = 3;
        		
        		intent.setData(outputFileUri);
                
        		xCrop = 4;
        		
                intent.putExtra("outputX", outputX);
                intent.putExtra("outputY", outputY);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("scaleUpIfNeeded", true);
                intent.putExtra("outputFormat", "JPEG");
                intent.putExtra("outputQuality", 100);
                
                xCrop = 5;
                
              String root = Environment.getExternalStorageDirectory().toString();
                //File myDir = new File("/data/data/air.com.zahdoo.cadie.debug/com.zahdoo.cadie.debug/Local Store/thumbnails");  
              //  File myDir = new File(Environment.getDataDirectory() + "/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/thumbnails");
                
              File myDir = new File(root + "/cadie");
              
                xCrop = 6;
                
                myDir.mkdirs();
                String fname = "temp_" + profilePicFileName + ".jpg";
                File file = new File (myDir, fname);
                
                xCrop = 7;
                
                if (file.exists ())
                	file.delete (); 
                
                outputFileUri = Uri.fromFile(file);
                
                xCrop = 8;
                
	            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                
                intent.putExtra("return-data", true);
                
                xCrop = 9;
                
                Intent i 		= new Intent(intent);
                startActivityForResult( i, CROP_FROM_CAMERA);
                
			} catch (Exception e) {
				Log.d("CADIE FILE", "11 doCrop Exc - " + e.toString());
				Toast.makeText(getApplicationContext(), xCrop + "\ndoCrop Exc - " + e.toString(), Toast.LENGTH_SHORT).show();
				
				try {
					CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED","NOT_CAPTURED");
				} catch (Exception e1) {
					Log.d("CADIE FILE", "doCrop 1 - " + e1.toString());
				}
	        	finish();
			}
        }
    }
    
     @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
		try{
			if(resultCode == RESULT_OK)
	    	{
	    		try {
	    			if((requestCode==ATTACH_IMAGE || requestCode==ATTACH_IMAGE_CHAT) && data != null)
		    		{
	    				Boolean bThumb = false;
	    				
	    				mSelectedImagePath = data.getData().getEncodedPath();
	    				
	    				if(mSelectedImagePath.contains("external/"))
	    	    			mSelectedImagePath = getPath(data.getData());
	    	    	
			    		xFileSize = 0;
			    		mSelectedImagePath = mSelectedImagePath.replace("file://", "");
			    		mSelectedImagePath = mSelectedImagePath.replaceAll("%20", " ");
			    		
			    		
			    		if(mSelectedImagePath.lastIndexOf(".") == -1)
			    		{
			    			outputFileUri = data.getData();
				    		
				    		Cursor returnCursor = getContentResolver().query(outputFileUri, null, null, null, null);
				            returnCursor.moveToFirst();
				            
				            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
				            cFileName = returnCursor.getString(nameIndex);
				            
				            int lastIndex1 = cFileName.lastIndexOf(".");
				            
				            try {
					            if(lastIndex1 == -1){
					            	String strMIMEType = getContentResolver().getType( outputFileUri );
									fileType = "." + strMIMEType.substring( strMIMEType.lastIndexOf("/") );
									
									fileType = fileType.replace("/", "");
								}
								else
									 fileType = cFileName.substring(cFileName.lastIndexOf("."));
							} catch (Exception e) {
								Toast.makeText(getApplicationContext(), mSelectedImagePath + "\nFiletype Exc - " + e.toString(), Toast.LENGTH_SHORT).show();
							}
			    		}
			    		else
			    		{
				    		try {

					    		fileType = mSelectedImagePath.substring(mSelectedImagePath.lastIndexOf("."));
							} catch (Exception e) {
								Toast.makeText(getApplicationContext(), mSelectedImagePath + "\nFiletype Exc 1 - " + e.toString(), Toast.LENGTH_SHORT).show();
							}
			    		}
			    		
			    		try {
			    			cFileName = mSelectedImagePath.substring(mSelectedImagePath.lastIndexOf("/"));
						} catch (Exception e) {
							Toast.makeText(getApplicationContext(), mSelectedImagePath + "\nFiletype Exc 2 - " + e.toString(), Toast.LENGTH_SHORT).show();
						}
			    		
			    		////
			    		if(!(new File(mSelectedImagePath)).exists())
			    		{
			    			outputFileUri = data.getData();
				    		
				    		Cursor returnCursor = getContentResolver().query(outputFileUri, null, null, null, null);
				            returnCursor.moveToFirst();
				            
				            /* Get the column indexes of the data in the Cursor,
				             * move to the first row in the Cursor, get the data, and display it. 
				             */
				            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
				            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
				            cFileName = returnCursor.getString(nameIndex);
				             
				            int lastIndex1 = cFileName.lastIndexOf(".");
							
				            try {
					            if(lastIndex1 == -1){
					            	String strMIMEType = getContentResolver().getType( outputFileUri );
									fileType = "." + strMIMEType.substring( strMIMEType.lastIndexOf("/") );
									
									fileType = fileType.replace("/", "");
								}
								else
									 fileType = cFileName.substring(cFileName.lastIndexOf("."));
							} catch (Exception e) {
								Toast.makeText(getApplicationContext(), "Filetype Exc - " + e.toString(), Toast.LENGTH_SHORT).show();
							}
				            xFileSize = returnCursor.getLong(sizeIndex);
				            
				            uriDataContentType = true;
			    		}
			    		
			    		//Toast.makeText(getApplicationContext(), "uriDataContentType - " + uriDataContentType, Toast.LENGTH_SHORT).show();
			    		////
			    		
			    		fileType = fileType.toLowerCase();
			            
		    			if(fileType.contentEquals(".mp4") || fileType.contentEquals(".3gp")
									|| fileType.contentEquals(".mkv") || fileType.contentEquals(".avi")
									|| fileType.contentEquals(".aaf") || fileType.contentEquals(".wrap")
									|| fileType.contentEquals(".wmv") || fileType.contentEquals(".flv")
									|| fileType.contentEquals(".fla") || fileType.contentEquals(".mov")
									|| fileType.contentEquals(".mpeg") || fileType.contentEquals(".mpg"))
		    			{
		    				if(cType.equals("ATTACH_CHAT_WALLPAPER"))
		    					Toast.makeText(getApplicationContext(), "\nVideo cannot be used as a wallpaper...\n " , Toast.LENGTH_LONG).show();
		    				else if(cType.equals("ATTACH_PICTURE_CHAT"))
		    					getOutputMediaFile(ATTACH_VIDEO_CHAT,data);
		    				else
		    					getOutputMediaFile(ATTACH_VIDEO,data);
		    			}
		    			else
		    			{
				    		cFileName = cFileName.replace("/", "");
				    		cFileName = cFileName.replace(fileType, "");
					    	
				    		Boolean sizeExceed = false;
				    		
				    		try 
				    		{
				    			File testSdCard = new File(Environment.getExternalStorageDirectory() + "/cadie");

				    			if(!testSdCard.exists())
				    				testSdCard.mkdir();

				    			if(testSdCard.exists())
				    			{
				    				sd = Environment.getExternalStorageDirectory();
				    			}
				    			else
				    			{
				    				//sd = new File(Environment.getDataDirectory() + "/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store");
				    				sd = new File("/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store");
				    			}
				    			
			                    if (sd.canWrite()) 
			                    {
			                        String destinationImagePath= "/cadie/" + cFileCreatedTime + fileType;   // this is the destination image path.
			                        
			                        if(!uriDataContentType)
			                        {
			                        	source = new File(mSelectedImagePath );
				                        xFileSize = source.length();
			                        }
			                        
			                        if((cNoteSize + xFileSize) > 104857600 ) //26214400)
			                        {
			                        	Toast.makeText(CameraInitFunction.cam.getActivity(), "Oops...Sorry the max note size of 100 MB is exceeded...", Toast.LENGTH_LONG).show();
			                        	sizeExceed = true;

			                        	try {
											if(cType.equals("ATTACH_PICTURE_CHAT"))
												CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED", "ATTACH_IMAGE_CHAT@ATTACH@SizeExceeded" );
											else
												CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED", "ATTACH_IMAGE@ATTACH@SizeExceeded" );
			                        	} catch (Exception e1) {
			                				Log.d("CADIE FILE", "onActivityResult cNoteSize + xFileSize - " + e1.toString());
			                			}
			                        }
			                        else
			                        {
			                        	cNoteSize = cNoteSize + xFileSize;
			                        	destination = new File(sd, destinationImagePath);
			   	                     
				                        if (uriDataContentType) //NEW CODE
				                        {
				                        	AssetFileDescriptor afd = getContentResolver().openAssetFileDescriptor(outputFileUri, "r");
				    	                	FileChannel src = afd.createInputStream().getChannel();
				                            FileChannel dst = new FileOutputStream(destination).getChannel();
				                            dst.transferFrom(src, 0, src.size());       // copy the first file to second.....
				                            src.close();
				                            dst.close();
				                            
				                            if(!cType.equals("ATTACH_CHAT_WALLPAPER"))
				                            	bThumb = generateThumbnailImage(null,"th_" + cFileCreatedTime +fileType);
				                        }
				                        else//OLD Code
				                        {
				                        	FileChannel src = new FileInputStream(source).getChannel();
				                            FileChannel dst = new FileOutputStream(destination).getChannel();
				                            dst.transferFrom(src, 0, src.size());       // copy the first file to second.....
				                            src.close();
				                            dst.close();
				                            
				                            if(!cType.equals("ATTACH_CHAT_WALLPAPER"))
				                            	bThumb = generateThumbnailImage(source,"th_" + cFileCreatedTime +fileType);
				                        }
			                        }
			                    }
			                    else
			                    {
			                        Toast.makeText(getApplicationContext(), "SDCARD Not writable.", Toast.LENGTH_LONG).show();
			                    }
			                }
				    		catch (Exception e) 
				    		{
			                	try {
									CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED","NOT_CAPTURED");
			                	} catch (Exception e1) {
	                				Log.d("CADIE FILE", "onActivityResult excp 1 - " + e1.toString());
	                			}
			                	Toast.makeText(getApplicationContext(), "AI Error :" + e.toString(), Toast.LENGTH_LONG).show();
			                }
			                
				    		if(!sizeExceed)
			                {
				    			try {
									if(cType.equals("ATTACH_CHAT_WALLPAPER"))
										CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED", "ATTACH_CHAT_WALLPAPER@" + cFileName+ "@" +fileType + "@"+cFileCreatedTime + fileType +"@0@" + cNoteSize + "@" + xFileSize);
									else if(cType.equals("ATTACH_PICTURE_CHAT"))
									{
										if(bThumb)
											CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED", "ATTACH_IMAGE_CHAT@" + cFileName + "@" +fileType + "@th_"+cFileCreatedTime + fileType +"@0@" + cNoteSize + "@" + xFileSize);
										else
											CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED", "ATTACH_IMAGE_CHAT@" + cFileName+ "@" +fileType + "@"+cFileCreatedTime + fileType +"@0@" + cNoteSize + "@" + xFileSize);
									}
									else
									{
										if(bThumb)
											CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED", "ATTACH_IMAGE@" + cFileName + "@" +fileType + "@th_"+cFileCreatedTime + fileType +"@0@" + cNoteSize + "@" + xFileSize);
										else
											CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED", "ATTACH_IMAGE@" + cFileName+ "@" +fileType + "@"+cFileCreatedTime + fileType +"@0@" + cNoteSize + "@" + xFileSize);
									}
				    			} catch (Exception e1) {
	                				Log.d("CADIE FILE", "onActivityResult excp 2 - " + e1.toString());
	                			}
			                }
		    			}
	    			}
	    			else if(requestCode== CROP_FROM_CAMERA)
	    	        {
	    	         Bundle extras = data.getExtras();
	    	         
	    	               try 
	    	            {
	    	                Bitmap photo = extras.getParcelable("data");
	    	                  
	    	                  
	    	                Log.d("CADIE FILE", "In  CROP_FROM_CAMERA result  " );
	    	            
    	                	String root = Environment.getExternalStorageDirectory().toString();
                        	//File myDir = new File("/data/data/air.com.zahdoo.cadie.debug/com.zahdoo.cadie.debug/Local Store/thumbnails");  
                        	//File myDir = new File("/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/thumbnails");
                        
                        	File myDir = new File(root + "/cadie");
                        	
							myDir.mkdirs();
							String fname = "temp_" + profilePicFileName + ".jpg";
							File file = new File (myDir, fname);
							 
							if (file.exists ())
							{
								Log.d("CADIE FILE", "File exists " );
								// file.delete (); 
							}
							else
							{
								FileOutputStream out = new FileOutputStream(file);
							    photo.compress(Bitmap.CompressFormat.JPEG, 100, out);
							    out.flush();
							    out.close();
							}
							     
							try {
								CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED", "ATTACH_PROFILE_PIC@" + profilePicFileName + ".jpg" );
							} catch (Exception e1) {
								Log.d("CADIE FILE", "CROP_FROM_CAMERA  - " + e1.toString());
							}
	    	         } catch (Exception e) {
	    	        	 try {
	    	        		 CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED","NOT_CAPTURED");
	    	        	 } catch (Exception e1) {
              				Log.d("CADIE FILE", "CROP_FROM_CAMERA  - " + e1.toString());
              			}
	    	          Log.d("CADIE FILE", "11 CROP_FROM_CAMERA Exc - " + e.toString());
	    	                      //Toast.makeText(getApplicationContext(), "AI Error :" + e.toString(), Toast.LENGTH_LONG).show();
	    	         }
	    	         finally
	    	         {
	    	           	finish();
	    	         }
	    	         
	    	         /////////////
	    	        }
	    			else if(requestCode==ATTACH_PROFILE_PIC )
	    			{
	    				int xPic = 0;
	    				
	    				try {
	    					outputFileUri = data.getData();
		    		    	
	    					xPic = 1;
		    				
	    				//	Bundle extras1 = data.getExtras();
		    				//int widthImg = 0;
		    			//	int heightImg = 0;
		    				
		    				xPic = 2;
		    				
//		    		        if (extras1 != null) {	        	
//		    		        	xPic = 3;
//		    		        	
//		    		        	Bitmap photo = extras1.getParcelable("data");
//		    		        	
//		    		        	xPic = 4;
//		    		            
//		    		            heightImg = photo.getHeight();
//		    		            widthImg = photo.getWidth();
//		    		        }
		    		        
		    		        if(outputFileUri == null )
		    		        	Toast.makeText(getApplicationContext(), outputFileUri + "\nOutput File Uri is null \n", Toast.LENGTH_LONG).show();
		    		        else
		    		        	Toast.makeText(getApplicationContext(), "\nOpening image in crop mode...\n", Toast.LENGTH_LONG).show();
		    		        
		    		        //Log.d("CADIE FILE", "Image Height -  " + heightImg);
		    		       // Log.d("CADIE FILE", "Image Width -  " + widthImg);
		    		        
		    		        xPic = 5;
		    		        doCrop(0,0);
		    		        
		    		        /*if(widthImg >= 400 && heightImg >= 400)
		    		        	doCrop(400,400);
		    		        else if(widthImg <= 400 && heightImg >= 400) 
		    		        	doCrop(widthImg,widthImg);
		    		        else if(heightImg <= 400 && widthImg >= 400) 
		    		        	doCrop(heightImg,heightImg);
		    		        else if(heightImg < 400 && widthImg < 400) 
		    		        {
		    		        	if(heightImg < widthImg)
		    		        		doCrop(widthImg,widthImg);
		    		        	else
		    		        		doCrop(heightImg,heightImg);
		    		        }*/
						} catch (Exception e) {
							Toast.makeText(getApplicationContext(), xPic + "\nProfile Pic error ...\n", Toast.LENGTH_LONG).show();
		    		    }
	    				
	    		    	//doCrop();
	    			}
	    			else if(requestCode==ATTACH_VIDEO ){
	    				getOutputMediaFile(ATTACH_VIDEO,data);
	    			}
	    			else if(requestCode==ATTACH_VIDEO_CHAT ){
	    				getOutputMediaFile(ATTACH_VIDEO_CHAT,data);
	    			}
	    			else if(requestCode==ATTACH_FILE ){
	    				moveSelectedDoc(ATTACH_FILE,data);
	    			}
	    			else if(requestCode==ATTACH_FILE_CHAT){
	    				moveSelectedDoc(ATTACH_FILE_CHAT,data);
	    			}
	    			else if(requestCode==ATTACH_AUDIO ){
	    				moveSelectedDoc(ATTACH_AUDIO,data);
	    			}
	    			else if(requestCode==ATTACH_AUDIO_CHAT){
	    				moveSelectedDoc(ATTACH_AUDIO_CHAT,data);
	    			}
		    	} 
	    		catch (Error e) 
	    		{
		    		try {
						CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED","NOT_CAPTURED");
		    		} catch (Exception e1) {
           				Log.d("CADIE FILE", "CROP_FROM_CAMERA err - " + e1.toString());
					}
		    		Toast.makeText(CameraInitFunction.cam.getActivity(), "Inside Exc ...", Toast.LENGTH_SHORT).show();
		    		e.printStackTrace();
				} 
		    	finally
				{
		        	data = null;
		    		sd = null;
		    		source = null;
		    		destination = null;
		    		
		    		if(requestCode != ATTACH_PROFILE_PIC )
		    		{
		    			Log.d("CADIE FILE", "11 Activity Finished ");
		    			finish();
		    		}
		    	}
		    }
	    	else{
	        	if(vidAttachError){
	        		vidAttachError = false;
	        	}else{
	        		try {
						CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED","NOT_CAPTURED");
	        		} catch (Exception e1) {
	    				Log.d("CADIE FILE", "vidAttachError - " + e1.toString());
	    			}
	        		
		    		finish();
	        	}
	    	}
		}
		catch (Exception e) {
			try {
				CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED","NOT_CAPTURED");
			} catch (Exception e1) {
				Log.d("CADIE FILE", "Crop vidAttachError - " + e1.toString());
			}
			Toast.makeText(getApplicationContext(),  "requestCode Error" + requestCode + e.toString(), Toast.LENGTH_LONG).show();
			finish();
		}
    }
    
    private Boolean generateThumbnailImage(File source, String fPath)
    {
    	byte[] imageData = null;
    	int x =0;
        try     
        {
        	//sd = new File(Environment.getDataDirectory() + "/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/thumbnails");
        	sd = new File("/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/thumbnails");
        	
        	Boolean s = false;
            
            if(!sd.exists())
    			s = sd.mkdir();
            
            File TH_destination= new File(sd, fPath);//TH_destinationImagePath);
            int THUMBNAIL_HEIGHT = 250;
            int THUMBNAIL_WIDTH = 240;
            
            if(null == source)
            {
            	//Toast.makeText(CameraInitFunction.cam.getActivity(), "Thumbnail - 1" , Toast.LENGTH_LONG).show();
            	AssetFileDescriptor afd = getContentResolver().openAssetFileDescriptor(outputFileUri, "r");
            	
            	fis  = afd.createInputStream();
            }
            else
            {
            	fis = new FileInputStream(source);
            }

            imageBitmap = decodeSampledBitmapFromResourceMemOpt(fis,0,0);
            
            if(imageBitmap == null)
            {
            	 baos = null;
                 imageBitmap = null;
                 fis = null;
                 fileOuputStream = null;
                 return false;
            }
            
            x =1;
            Float width  = Float.valueOf(imageBitmap.getWidth());
            Float height = Float.valueOf(imageBitmap.getHeight());
            x =2;
            
            //if(height < THUMBNAIL_HEIGHT){
            if(width < THUMBNAIL_WIDTH){
            	
            	if(null == source )
            	{
            		//Toast.makeText(CameraInitFunction.cam.getActivity(), "Thumbnail - 2" , Toast.LENGTH_LONG).show();
            		FileChannel src = new FileInputStream(new File(Environment.getExternalStorageDirectory().getPath()+ "/cadie/" + fPath.replace("th_", ""))).getChannel();
                    FileChannel dst = new FileOutputStream(TH_destination).getChannel();
                    dst.transferFrom(src, 0, src.size());       // copy the first file to second.....
                    src.close();
                    dst.close();
            	}
            	else if (source.exists()) 
                {
                    FileChannel src = new FileInputStream(source).getChannel();
                    FileChannel dst = new FileOutputStream(TH_destination).getChannel();
                    dst.transferFrom(src, 0, src.size());       // copy the first file to second.....
                    src.close();
                    dst.close();
                }
                
                imageBitmap = null;
                fis = null;
            	return true;
            }
            	
            x =3;
            
            Float ratio;
            
            if(height <= width){
            	ratio = width/height;
            	imageBitmap = Bitmap.createScaledBitmap(imageBitmap,(int)(THUMBNAIL_HEIGHT*ratio), THUMBNAIL_HEIGHT, false);
            }
            else{
            	ratio = height/width;
            	imageBitmap = Bitmap.createScaledBitmap(imageBitmap, THUMBNAIL_WIDTH,(int)(THUMBNAIL_WIDTH*ratio),  false);
            }
             
            x =4;
            baos = new ByteArrayOutputStream();  
            
            fileType = fileType.toLowerCase();
            
            if(fileType.contains(".png"))
            	imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            else
            	imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            
            x =5;
            imageData = baos.toByteArray();
            fileOuputStream = new FileOutputStream(TH_destination); 
            fileOuputStream.write(imageData);
            fileOuputStream.close();
            x =6;
            imageBitmap.recycle();
            x =7;
            imageData = null;
            baos = null;
            imageBitmap = null;
            fis = null;
            fileOuputStream = null;
        	return true;
        }
        catch(Exception ex) 
        {
        	Toast.makeText(getApplicationContext(), x+ "\nTH Not writable."+ ex.toString(), Toast.LENGTH_LONG).show();
        }
        return false;
    }
    
    public Bitmap decodeSampledBitmapFromResourceMemOpt( InputStream inputStream, int reqWidth, int reqHeight) 
    {
        byte[] byteArr = new byte[0];
        byte[] buffer = new byte[1024];
        int len;
        int count = 0;

        try {
            while ((len = inputStream.read(buffer)) > -1) {
                if (len != 0) {
                    if (count + len > byteArr.length) {
                        byte[] newbuf = new byte[(count + len) * 2];
                        System.arraycopy(byteArr, 0, newbuf, 0, count);
                        byteArr = newbuf;
                    }

                    System.arraycopy(buffer, 0, byteArr, count, len);
                    count += len;
                }
            }

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(byteArr, 0, count, options);

            String type = fileType.toLowerCase();

            if(type.contentEquals(".png"))
            {
            	options.inSampleSize = 2;//calculateInSampleSize(options, reqWidth, reqHeight);
            	options.inPurgeable = true;
            	options.inInputShareable = true;
            	options.inJustDecodeBounds = false;
            	options.inPreferredConfig = Bitmap.Config.RGB_565;// .ARGB_8888;
            }
            else
            {
            	options.inSampleSize = 4;//calculateInSampleSize(options, reqWidth, reqHeight);
                options.inPurgeable = true;
                options.inInputShareable = true;
                options.inJustDecodeBounds = false;
                options.inPreferredConfig = Bitmap.Config.ALPHA_8;//RGB_565;// .ARGB_8888;
            }
          
            return BitmapFactory.decodeByteArray(byteArr, 0, count, options);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public String getPath(Uri uri) 
    {
    	try {
    		 String[] projection = { MediaStore.Images.Media.DATA };
    	        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
    	        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
    	        cursor.moveToFirst();
    	        return cursor.getString(column_index);
		} 
    	catch (Exception e) {
			Toast.makeText(CameraInitFunction.cam.getActivity(), "Inside getPath ..." + e.toString(), Toast.LENGTH_SHORT).show();
			return "ERROR";
		}
    }
    
    /** Create a File for saving an image or video */
    private void getOutputMediaFile(int type, Intent data)
    {
    	int test = 0;
    	try
        {
    		outputFileUri = data.getData();
    		test = 11;
    		mSelectedImagePath = data.getData().getEncodedPath();// getPath(data.getData());
    		
    		if(mSelectedImagePath.contains("external/"))
    		{
    			test = 22;
    			mSelectedImagePath = getPath(data.getData());
    		}
    		test = 33;
    		
    		mSelectedImagePath = mSelectedImagePath.replace("file://", "");
    		mSelectedImagePath = mSelectedImagePath.replaceAll("%20", " ");
    		//mSelectedImagePath = URLDecoder.decode(mSelectedImagePath, "UTF-8");
    		test = 44;
    		try {
    			fileType = mSelectedImagePath.substring(mSelectedImagePath.lastIndexOf("."));
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "Filetype Exc - " + e.toString(), Toast.LENGTH_SHORT).show();
			}
    		
    		test = 55;
    		if(!(new File(mSelectedImagePath)).exists())
    		{
	    		test = 551;
	    		Cursor returnCursor = getContentResolver().query(outputFileUri, null, null, null, null);
	    		test = 552;
	            
	            returnCursor.moveToFirst();
	            test = 66;
	            /* Get the column indexes of the data in the Cursor,
	             * move to the first row in the Cursor, get the data, and display it. 
	             */
	            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
	            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
	            cFileName = returnCursor.getString(nameIndex);
	            
	            int lastIndex1 = cFileName.lastIndexOf(".");
				
	            if(lastIndex1 == -1){
	            	String strMIMEType = getContentResolver().getType( outputFileUri );
					fileType = "." + strMIMEType.substring( strMIMEType.lastIndexOf("/") );
					
					fileType = fileType.replace("/", "");
				}
				else{
					 fileType = cFileName.substring( cFileName.lastIndexOf(".") );
				}
	            
	            xFileSize = returnCursor.getLong(sizeIndex);
	            
	            uriDataContentType = true;
    		}
    		
    		//Toast.makeText(getApplicationContext(), "Output Media uriDataContentType - " + uriDataContentType, Toast.LENGTH_SHORT).show();
    		test = 77;
    		AssetFileDescriptor videoAsset = getContentResolver().openAssetFileDescriptor(outputFileUri, "r");
    		test = 88;
    		test = 1;
    		if(!uriDataContentType)
    			xFileSize = videoAsset.getLength();
    		
    		if((cNoteSize + xFileSize) > 104857600) //26214400)
    		{
    			Toast.makeText(CameraInitFunction.cam.getActivity(), "Oops...Sorry the max note size of 100 MB is exceeded...", Toast.LENGTH_LONG).show();
    			
    			try {
					if(type == ATTACH_VIDEO_CHAT)
						CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED", "ATTACH_VIDEO_CHAT@VIDEO@SizeExceeded" );
					else
						CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED", "ATTACH_VIDEO@VIDEO@SizeExceeded" );
    			} catch (Exception e1) {
    				Log.d("CADIE FILE", "getOutputMediaFile - " + e1.toString());
    			}
    		}
    		else
    		{
    			cNoteSize = cNoteSize + xFileSize;
        		FileInputStream fis = videoAsset.createInputStream();
        		File root = Environment.getExternalStorageDirectory();
        		File testSdCard = new File(Environment.getExternalStorageDirectory() + "/cadie");

        		if(!testSdCard.exists())
        			testSdCard.mkdir();

        		if(testSdCard.exists())
        		{
        			root = new File(Environment.getExternalStorageDirectory(),"cadie");
        		}
        		else
        		{
        			//root = new File(Environment.getDataDirectory() + "/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadie");
        			root = new File("/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadie");
        		}
    	         
    	        String fName = new String();

    	        if(!uriDataContentType)
    	        	cFileName = mSelectedImagePath.substring(mSelectedImagePath.lastIndexOf("/"));
    	        
    	        test = 2 ;
	        	cFileName = cFileName.replace("/", "");
                cFileName = cFileName.replace(fileType, "");
                
                fName = cFileCreatedTime + fileType;
    	        
    	        File file;
    	        file = new File(root, fName );
    	        FileOutputStream fos = new FileOutputStream(file);
    	        byte[] buf = new byte[1024];
    	        int len;
    	         
    	        while ((len = fis.read(buf)) > 0) {
    	        	fos.write(buf, 0, len);
    	        }       
    	        
    	        fis.close();
    	        fos.close();
    	        
    	        byte[] vidImageData = null;
    	        ByteArrayOutputStream baos;
    	        //Bitmap bMap = ThumbnailUtils.createVideoThumbnail(mSelectedImagePath, MediaStore.Video.Thumbnails.MINI_KIND);
    	        test = 3;
    	        Bitmap bMap = ThumbnailUtils.createVideoThumbnail(Environment.getExternalStorageDirectory().getPath()+ "/cadie/" + fName, MediaStore.Video.Thumbnails.MINI_KIND);
    	        test = 4;
    	        try {
    	        	if( bMap != null )
    	        	{
	    	        	baos = new ByteArrayOutputStream();  
						bMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
						vidImageData = baos.toByteArray();
						
						//File vidThRoot = new File(Environment.getDataDirectory() + "/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/thumbnails");
		    	        File vidThRoot = new File("/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/thumbnails");
						
		    	        if (!vidThRoot.exists()) 
		    	        {
		    	        	vidThRoot.mkdirs();
		    	        }
		    	        
		    	        test = 6;
		            	Boolean s = false;
		                
		                if(!vidThRoot.exists())
		        			s = vidThRoot.mkdir();
		                
		                File TH_destination= new File(vidThRoot, "th_" + cFileCreatedTime + ".jpg");//TH_destinationImagePath);
		                test = 7;
		                fileOuputStream = new FileOutputStream(TH_destination); 
		                fileOuputStream.write(vidImageData);
		                fileOuputStream.close();
		                test = 8;
		                bMap.recycle();
		                
		                try {
							if(type == ATTACH_VIDEO_CHAT)
								CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED", "ATTACH_VIDEO_CHAT@" + cFileName + "@" + fileType + "@"  + fName + "@" + cLocation + "@" + cNoteSize + "@" + xFileSize);
							else
								CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED", "ATTACH_VIDEO@" + cFileName + "@" + fileType + "@"  + fName + "@" + cLocation + "@" + cNoteSize + "@" + xFileSize);
		                } catch (Exception e1) {
		    				Log.d("CADIE FILE", "getOutputMediaFile ATTACH_VIDEO_CHAT - " + e1.toString());
		    			}
    	        	}
    	        	else
    	        	{
    	        		fileType = fileType.toLowerCase();
    	                
    	        		if(fileType.contentEquals(".jpeg") || fileType.contentEquals(".jpg")
    	        		        || fileType.contentEquals(".bmp") || fileType.contentEquals(".tif")
    	        		        || fileType.contentEquals(".png") || fileType.contentEquals(".dib")
    	        		        || fileType.contentEquals(".tiff") || fileType.contentEquals(".gif"))
    	        		{
    	        			Boolean bThumb = false;
    	        		                
    	        			if(uriDataContentType)
    	        				bThumb = generateThumbnailImage(null,"th_" + cFileCreatedTime + fileType);
    	        		    else
    	        		    	bThumb = generateThumbnailImage(file,"th_" + cFileCreatedTime + fileType);
    	        		 
    	        			if(bThumb)
    	        				cFileCreatedTime = "th_" + cFileCreatedTime;
    	        		        
    	        			try {
								if(cType.equals("ATTACH_VIDEO_CHAT"))
								{
									if(bThumb)
										CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED", "ATTACH_IMAGE_CHAT@" + cFileName + "@" +fileType + "@th_"+cFileCreatedTime + fileType +"@0@" + cNoteSize + "@" + xFileSize);
									else
										CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED", "ATTACH_IMAGE_CHAT@" + cFileName+ "@" +fileType + "@"+cFileCreatedTime + fileType +"@0@" + cNoteSize + "@" + xFileSize);
								}
								else
								{
									if(bThumb)
										CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED", "ATTACH_IMAGE@" + cFileName + "@" +fileType + "@th_"+cFileCreatedTime + fileType +"@0@" + cNoteSize + "@" + xFileSize);
									else
										CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED", "ATTACH_IMAGE@" + cFileName+ "@" +fileType + "@"+cFileCreatedTime + fileType +"@0@" + cNoteSize + "@" + xFileSize);
								}
    	        			} catch (Exception e1) {
    		    				Log.d("CADIE FILE", "ATTACH_VIDEO_CHAT Err - " + e1.toString());
    		    			}
    	        		}
    	        		else if(type == ATTACH_VIDEO_CHAT)
							try {
								CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED", "ATTACH_AUDIO_CHAT@" + cFileName + "@" + fileType + "@"  + fName + "@" + cLocation + "@" + cNoteSize + "@" + xFileSize);
							} catch (Exception e1) {
    		    				Log.d("CADIE FILE", "ATTACH_VIDEO_CHAT Err 100 - " + e1.toString());
    		    			}
						else
							try {
								CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED", "ATTACH_AUDIO_ONLY@" + cFileName + "@" + fileType + "@"  + fName + "@" + cLocation + "@" + cNoteSize + "@" + xFileSize);
							} catch (Exception e1) {
    		    				Log.d("CADIE FILE", "ATTACH_VIDEO_CHAT Err 200 - " + e1.toString());
    		    			}
    	        	}
				} catch (Exception e) {
					try {
						CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED","NOT_CAPTURED");
					} catch (Exception e1) {
	    				Log.d("CADIE FILE", "ATTACH_VIDEO_CHAT Err 500 - " + e1.toString());
	    			}
					Toast.makeText(getApplicationContext(), "667 Exc - " + e.toString(), Toast.LENGTH_LONG).show();
				}
    	        test = 5;
    	        
                vidImageData = null;
                baos = null;
                bMap = null;
                fis = null;
                fileOuputStream = null;
    		}
        }
        catch (Exception ex) 
        {
        	try {
				CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED","NOT_CAPTURED");
        	} catch (Exception e1) {
				Log.d("CADIE FILE", "Output media Err 800 - " + e1.toString());
			}
        	
        	Log.d("CADIE FILE", "Output media Exc - " + ex.toString());
        	Toast.makeText(getApplicationContext(), mSelectedImagePath + "\nOutput media Exc - " + test + " " + ex.toString(), Toast.LENGTH_LONG).show();
        } 
    	finally
        {
			finish();
    	}
    }
    
    private void moveSelectedDoc(int type, Intent data)
    {
    	int x = 0;
    	
    	try 
		{
    		outputFileUri = data.getData();
    		
    		mSelectedImagePath = outputFileUri.getPath();//getPath(data.getData());
    		
			if(mSelectedImagePath.contains("external/")){
				mSelectedImagePath = getPath(outputFileUri);
			}
		x = 1;
			mSelectedImagePath = mSelectedImagePath.replace("file://", "");
			mSelectedImagePath = mSelectedImagePath.replaceAll("%20", " ");
			x = 2;	
			
			try {
				cFileName = mSelectedImagePath.substring(mSelectedImagePath.lastIndexOf("/"));
			
				x = 3;			
				int lastIndex = cFileName.lastIndexOf(".");
				
				if(lastIndex == -1){
					fileType = "";//cFileName.substring(lastIndex);
				}
				else{
					fileType = cFileName.substring(cFileName.lastIndexOf("."));
				}
				x = 4;
			
				if(!(new File(mSelectedImagePath)).exists())
	    		{
	    			Cursor returnCursor = getContentResolver().query(outputFileUri, null, null, null, null);
		            returnCursor.moveToFirst();
		            
		            x = 5;
		            
		            /* Get the column indexes of the data in the Cursor,
		             * move to the first row in the Cursor, get the data, and display it. 
		             */
		            int nameIndex = returnCursor.getColumnIndex( OpenableColumns.DISPLAY_NAME );
		            int sizeIndex = returnCursor.getColumnIndex( OpenableColumns.SIZE );
		            
		            cFileName = returnCursor.getString( nameIndex );
		            
		            int lastIndex1 = cFileName.lastIndexOf(".");
					
		            if(lastIndex1 == -1){
		            	String strMIMEType = getContentResolver().getType( outputFileUri );
						fileType = "." + strMIMEType.substring( strMIMEType.lastIndexOf("/") );
						
						fileType = fileType.replace("/", "");
					}
					else{
						 fileType = cFileName.substring( cFileName.lastIndexOf(".") );
					}
		            
		            xFileSize = returnCursor.getLong( sizeIndex );
		            
		            uriDataContentType = true;
	    		}
			} 
			catch (Exception e) {
				Toast.makeText(getApplicationContext(), "732 Exc - cFileName -" + cFileName + "  " + e.toString(), Toast.LENGTH_SHORT).show();
			}
			 x = 6;
			 
			//Toast.makeText(getApplicationContext(), "778 uriDataContentType - " + uriDataContentType + " " + cFileName + " " + getContentResolver().getType( outputFileUri ), Toast.LENGTH_SHORT).show();
			 fileType = fileType.toLowerCase();
	            
			if(fileType.contentEquals(".mp4") || fileType.contentEquals(".3gp")
					|| fileType.contentEquals(".mkv") || fileType.contentEquals(".avi")
					|| fileType.contentEquals(".aaf") || fileType.contentEquals(".wrap")
					|| fileType.contentEquals(".wmv") || fileType.contentEquals(".flv")
					|| fileType.contentEquals(".fla") || fileType.contentEquals(".mov")
					|| fileType.contentEquals(".mpeg") || fileType.contentEquals(".mpg"))
			{
				if(type == ATTACH_FILE_CHAT || type == ATTACH_AUDIO_CHAT)
					getOutputMediaFile(ATTACH_VIDEO_CHAT,data);
				else
					getOutputMediaFile(ATTACH_VIDEO,data);
			}
			else
			{
				 x = 7;
				 
				File testSdCard = new File(Environment.getExternalStorageDirectory() + "/cadie");
	
				if(!testSdCard.exists())
					testSdCard.mkdir();
	
				if(testSdCard.exists())
				{
					sd = Environment.getExternalStorageDirectory();
				}
				else
				{
					//sd = new File(Environment.getDataDirectory() + "/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store");
					sd = new File("/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store");
				}
				
				if(!uriDataContentType)
				{
					source = new File(mSelectedImagePath );
		            xFileSize = source.length();
				}
				
	            if (sd.canWrite()) 
	            {
	            	if((cNoteSize + xFileSize) > 104857600) //26214400)
	            	{
	         			Toast.makeText(CameraInitFunction.cam.getActivity(), "Oops...Sorry the max note size of 100 MB is exceeded...", Toast.LENGTH_LONG).show();
	         			
	         			try {
							if(type == 6){
								CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED", "ATTACH_AUDIO@" + cFileName + ".jpg@SizeExceeded" );
							}
							else{
								CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED", "ATTACH_FILE@" + cFileName + ".jpg@SizeExceeded" );
							}
	         			} catch (Exception e1) {
	        				Log.d("CADIE FILE", "moveSelectedDoc Err 100 - " + e1.toString());
	        			}
	         		}
	         		else
	         		{
	         			cNoteSize = cNoteSize + xFileSize;
	         			String destinationImagePath= "/cadie/" + cFileCreatedTime + fileType;//".jpg";   // this is the destination image path.
	              
		                destination= new File(sd, destinationImagePath);
		             
		                if (uriDataContentType) 
		                {
		                	AssetFileDescriptor afd = getContentResolver().openAssetFileDescriptor(outputFileUri, "r");
		                	FileChannel src = afd.createInputStream().getChannel();
		                	FileChannel dst = new FileOutputStream(destination).getChannel();
			                dst.transferFrom(src, 0, src.size());       // copy the first file to second.....
			                src.close();
			                dst.close();
		                }
		                else
		                {
		                	source = new File(mSelectedImagePath );
		                	FileChannel src = new FileInputStream(source).getChannel();
		                    FileChannel dst = new FileOutputStream(destination).getChannel();
		                    dst.transferFrom(src, 0, src.size());       // copy the first file to second.....
		                    src.close();
		                    dst.close();
		                }
		                
		                Boolean bThumb = false;
		                fileType = fileType.toLowerCase();
		                
		                if(fileType.contentEquals(".jpeg") || fileType.contentEquals(".jpg")
								|| fileType.contentEquals(".bmp") || fileType.contentEquals(".tif")
								|| fileType.contentEquals(".png") || fileType.contentEquals(".dib")
								|| fileType.contentEquals(".tiff") || fileType.contentEquals(".gif"))
		                {
		                	if(uriDataContentType)
		                		bThumb = generateThumbnailImage(null,"th_" + cFileCreatedTime + fileType);
		                	else
		                		bThumb = generateThumbnailImage(source,"th_" + cFileCreatedTime + fileType);
	
	 	                	if(bThumb)
	 	    					cFileCreatedTime = "th_" + cFileCreatedTime;
		                }
		                
		                cFileName = cFileName.replace("/", "");
		                cFileName = cFileName.replace(fileType, "");
	     				
		                try {
							if(type == ATTACH_AUDIO){
								CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED", "ATTACH_AUDIO@" + cFileName+ "@" +fileType + "@"+ cFileCreatedTime + fileType + "@0@" + cNoteSize + "@" + xFileSize);
							}
							else if(type == ATTACH_AUDIO_CHAT){
								CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED", "ATTACH_AUDIO_CHAT@" + cFileName+ "@" +fileType + "@"+ cFileCreatedTime + fileType + "@0@" + cNoteSize + "@" + xFileSize);
							}
							else if(type == ATTACH_FILE_CHAT){
								CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED", "ATTACH_FILE_CHAT@" + cFileName+ "@" +fileType + "@"+ cFileCreatedTime + fileType + "@0@" + cNoteSize + "@" + xFileSize);
							}
							else{//ATTACH_FILE
								CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED", "ATTACH_FILE@" + cFileName+ "@" +fileType + "@"+ cFileCreatedTime + fileType + "@0@" + cNoteSize + "@" + xFileSize);
							}
		                } catch (Exception e1) {
	        				Log.d("CADIE FILE", "moveSelectedDoc Err 200 - " + e1.toString());
	        			}
		     		}
	            }
	            else{
		        	if(type == 6){
		        		Toast.makeText(getApplicationContext(), "Attach Audio >> SDCARD Not writable.", Toast.LENGTH_LONG).show();
		    		}
		    		else{
		    			Toast.makeText(getApplicationContext(), "Attach File >> SDCARD Not writable.", Toast.LENGTH_LONG).show();
		    		}
	            }
			}
		}
		catch (Exception e) 
		{
	    	if(type == 6)
	    	{
	    		Toast.makeText(getApplicationContext(), "Attach Audio >> Error : x -" + x + " " + e.toString(), Toast.LENGTH_LONG).show();
			}
			else{
				Toast.makeText(getApplicationContext(), "Attach File >> Error : x -" + x + " " + e.toString(), Toast.LENGTH_LONG).show();
			}
	    	
	    	try {
				CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED","NOT_CAPTURED");
	    	} catch (Exception e1) {
				Log.d("CADIE FILE", "moveSelectedDoc Err 500 - " + e1.toString());
			}
	    }
		finally
	    {
			finish();
	    }
	}
}