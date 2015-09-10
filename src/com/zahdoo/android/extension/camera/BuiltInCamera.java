package com.zahdoo.android.extension.camera;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.zahdoo.android.extension.GCM.CommonUtilities;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;


public class BuiltInCamera extends Activity {
    /** Called when the activity is first created. */
	private final int TAKE_PICTURE=1;
	private final int TAKE_VIDEO=2; 
	private Uri outputFileUri;
	private String cType 		= "";
	//private String cAction 		= "";
	private String cFileName 	= "";
	private String cFileCreatedTime = System.currentTimeMillis() + "";
	
	private String cAllPicName 	= "";
	private String cAllPicSize 	= "";
	
	private String cLocation 	= "";
	private long   cNoteSize 	= 0;
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
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
      
      //Toast.makeText(getApplicationContext(), "Inside Configuration Changed.. BuiltInCamera..", Toast.LENGTH_SHORT).show();
	}
    
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
        Bundle extras = getIntent().getExtras(); 
        
        cAllPicName 	= "";
    	cAllPicSize 	= "";
        
        
//          wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
//          
//          if(wifiManager.isWifiEnabled()){
//            wifiManager.setWifiEnabled(false);
//            Toast.makeText(CameraInitFunction.cam.getActivity(), "Wifi Turned Off...", Toast.LENGTH_LONG).show();
//            Toast.makeText(CameraInitFunction.cam.getActivity(), "Wifi Turned Off...", Toast.LENGTH_LONG).show();
//          }else{
//            wifiManager.setWifiEnabled(true);
//            Toast.makeText(CameraInitFunction.cam.getActivity(), "Wifi Turned On...", Toast.LENGTH_LONG).show();
//            Toast.makeText(CameraInitFunction.cam.getActivity(), "Wifi Turned On...", Toast.LENGTH_LONG).show();
//          }
        
        if(extras != null)
        {
        	cType 			= extras.getString("cType");
        	//cAction 		= extras.getString("cAction");
        	cFileName		= extras.getString("cFileName");
        	cLocation 		= extras.getString("cLocation");
        	cNoteSize		= Long.parseLong(extras.getString("cNoteSize"));
        }
        
        if(cType.equals("PICTURE"))
        {
        	TakePhoto();
        }
        else if(cType.equals("VIDEO")){
        	TakeVideo();
        }
    }
	
	 
    private void TakePhoto() 
    {
    	
    	try {
    		cFileCreatedTime = UUID.randomUUID().toString();//  System.currentTimeMillis() + "";
    		Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        	File file = Environment.getExternalStorageDirectory();
        	
        	////
        	File testSdCard = new File(Environment.getExternalStorageDirectory() + "/cadie");

        	if(!testSdCard.exists())
        		testSdCard.mkdir();

        	if(testSdCard.exists())
        	{
        		file = new File(Environment.getExternalStorageDirectory() + "/cadie",cFileCreatedTime+".jpg");
        	}
        	else
        	{
        		file = new File("/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadie",cFileCreatedTime+".jpg");
        	}
        	////
        	
        	mSelectedImagePath = file.getAbsolutePath();
        	
        	outputFileUri = Uri.fromFile(file);//file!=null?Uri.fromFile(file): null;
        	i.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        	startActivityForResult(i, TAKE_PICTURE);
		}
    	catch (Exception e) {
    		try {
				CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED","NOT_CAPTURED");
			} catch (Exception e1) {
				Log.d("CADIE GCM", "image cannot be captured = " + e1.toString());
			}
			
        	Toast.makeText(getApplicationContext(), "Sorry image cannot be captured...", Toast.LENGTH_SHORT).show();
		}
    }
   
    private void TakeVideo(){
    	
    	try {
    		 cFileCreatedTime = UUID.randomUUID().toString();//new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "";
    		
    		Intent i = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
    		File file = Environment.getExternalStorageDirectory();

    		////
    		File testSdCard = new File(Environment.getExternalStorageDirectory() + "/cadie");

    		if(!testSdCard.exists())
    			testSdCard.mkdir();

    		if(testSdCard.exists())
    		{
    			file = new File(Environment.getExternalStorageDirectory() + "/cadie",cFileCreatedTime+".mp4");
    		}
    		else
    		{
    			file = new File("/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadie",cFileCreatedTime+".mp4");
    		}
    		////
    		
        	mSelectedImagePath = file.getAbsolutePath();
        	outputFileUri = Uri.fromFile(file);//file!=null?Uri.fromFile(file): null;
        	i.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        	
        	Log.d("CADIE GCM", "outputFileUri 233 = " + outputFileUri.toString());
        	startActivityForResult(i, TAKE_VIDEO);
        	
		}
    	catch (Exception e) {
    		try {
				CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED","NOT_CAPTURED");
    		} catch (Exception e1) {
				Log.d("CADIE GCM", "TakeVideo = " + e1.toString());
			}
        	Toast.makeText(getApplicationContext(), "Sorry video cannot be captured...", Toast.LENGTH_SHORT).show();
		}
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
    	
		try{
        	
			if(resultCode == RESULT_OK)
	    	{
	    		int xRotate = 0;
	    		Uri myUri;
	    		
	    		try {
	        		
	    			Boolean bThumb = false;
	    			
	    			if(requestCode==TAKE_PICTURE)
	    			{
	    				fileType = ".jpg";
	    						
	    				source = new File(mSelectedImagePath );//Environment.getExternalStorageDirectory() + "/cadie",cFileName+".jpg" );
	    				xFileSize = source.length();
	 	    				
	    				if((cNoteSize + xFileSize) > 104857600) //26214400)
	    				{
	    	    			Toast.makeText(CameraInitFunction.cam.getActivity(), "Oops...Sorry the max note size of 100 MB is exceeded...", Toast.LENGTH_LONG).show();
	    	    			
	    	    			try {
	    	    				
	    	    				if(cAllPicName.length() > 0)
		    	    				CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED", "TAKE_PICTURE@" + cAllPicName + "@" +fileType + "@th_" + cFileCreatedTime + fileType + "@0@" + cNoteSize + "@" + cAllPicSize);
		    	    			else
	    	    					CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED", "TAKE_PICTURE@" + cFileCreatedTime + ".jpg@SizeExceeded" );
	    	    					
	    	    			} catch (Exception e1) {
	    	    				Log.d("CADIE GCM", "max note size err = " + e1.toString());
	    	    			}
	    	    			finally
	    	    			{
	    	    				data = null;
	    			    		sd = null;
	    			    		source = null;
	    			    		destination = null;
	    			    		
	    						finish();
	    	    			}
	    	    		}
	    	    		else{
	    	    			cNoteSize = cNoteSize + xFileSize;
	    	    			generateThumbnailImage(source,"th_" + cFileCreatedTime + fileType);
	        				cAllPicName = cAllPicName + "^" + cFileCreatedTime;
	    	    			cAllPicSize = cAllPicSize + "^" + xFileSize;
		    	    	}
	    			}
	    			else if(requestCode==TAKE_VIDEO ){
	    				getOutputMediaFile(TAKE_VIDEO,data);
	    			}
		    	} catch (Error e) {

		    		CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED","NOT_CAPTURED");
		        	
		    		Toast.makeText(CameraInitFunction.cam.getActivity(), "Inside Exc ...", Toast.LENGTH_SHORT).show();
		    		e.printStackTrace();
				} 
		    	finally
				{
					if(requestCode==TAKE_PICTURE){
						Toast.makeText(getApplicationContext(), "\n      IMAGE SAVED...      \n\n      Take more pictures if you want       \n\n      OR \n\n      Press back button to close camera...      \n\n" , Toast.LENGTH_SHORT).show();
						Toast.makeText(getApplicationContext(), "\n      IMAGE SAVED...      \n\n      Take more pictures if you want       \n\n      OR \n\n      Press back button to close camera...      \n\n" , Toast.LENGTH_LONG).show();
						
						TakePhoto();
					}
					else{
						data = null;
			    		sd = null;
			    		source = null;
			    		destination = null;
			    		
						finish();
					}
		    	}
		    }
	    	else{
	    		
	    		if(requestCode==TAKE_PICTURE){
	    			if(cAllPicName.equalsIgnoreCase("")){
		    			CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED","NOT_CAPTURED");
		    		}
		    		else{
		    			CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED", "TAKE_PICTURE@" + cAllPicName + "@" +fileType + "@th_" + cFileCreatedTime + fileType + "@0@" + cNoteSize + "@" + cAllPicSize);
		    		}
	    		}
	    		else{
	    			CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED","NOT_CAPTURED");
	    		}
	    		
	    		
	    		data = null;
	    		sd = null;
	    		source = null;
	    		destination = null;
	        	
	    		finish();
	    	}
		}
		catch (Exception e) {
			CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED","NOT_CAPTURED");
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
        	sd = new File("/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/thumbnails");
        	
        	if(!sd.exists())
    			sd.mkdir();
            
            File TH_destination= new File(sd, fPath);//TH_destinationImagePath);
            
            int THUMBNAIL_HEIGHT = 250;
            int THUMBNAIL_WIDTH = 240;
            
            fis = new FileInputStream(source);
            
            //CameraInitFunction.imageBitmap = BitmapFactory.decodeStream(CameraInitFunction.fis);
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
            	
                if (source.exists()) 
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
            
            imageData = null;
            
            x =7;
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
    
    private Bitmap decodeSampledBitmapFromResourceMemOpt( InputStream inputStream, int reqWidth, int reqHeight) {

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

            options.inSampleSize = 2;//calculateInSampleSize(options, reqWidth, reqHeight);
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;// .ARGB_8888;
			
			return BitmapFactory.decodeByteArray(byteArr, 0, count, options);

        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }
    
    /** Create a File for saving an image or video */
    private void getOutputMediaFile(int type, Intent data){
    	int k = 0;
    	
    	try
        {
    		fileType = mSelectedImagePath.substring(mSelectedImagePath.lastIndexOf("."));
    		
    		k = 1;
    		
    		AssetFileDescriptor videoAsset;
    		
    		Log.d("CADIE GCM", "outputFileUri >> " + outputFileUri);
    		
    		if(data == null || data.getData() == null){
    			k = 1000;
    			videoAsset = CameraInitFunction.cam.getActivity().getContentResolver().openAssetFileDescriptor(outputFileUri, "r");
    			
    			Log.d("CADIE GCM", "videoAsset? null >> " + (videoAsset == null));
    		}
    		else{
    			k = 1111;
    			videoAsset = CameraInitFunction.cam.getActivity().getContentResolver().openAssetFileDescriptor(data.getData(), "r");
    		}
    		
    		k = 2;
    		
    		Log.d("CADIE GCM", " K == 2 ");
    		
    		xFileSize = videoAsset.getLength();
    		
    		Log.d("CADIE GCM", " xFileSize == " + xFileSize);
    		
    		if((cNoteSize + xFileSize) > 104857600) //26214400)
    		{
    			Toast.makeText(CameraInitFunction.cam.getActivity(), "Oops...Sorry the max note size of 100 MB is exceeded...", Toast.LENGTH_LONG).show();
    			
    			if(type == 2){
    				CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED", "TAKE_VIDEO@VIDEO@SizeExceeded" );
    			}
    			else{
    				CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED", "ATTACH_VIDEO@VIDEO@SizeExceeded" );
    			}
    		}
    		else{
    			cNoteSize = cNoteSize + xFileSize;
    			
    			k = 3;
        		
        		File root = Environment.getExternalStorageDirectory();

        		File testSdCard = new File(Environment.getExternalStorageDirectory() + "/cadie");

        		if(!testSdCard.exists())
        			testSdCard.mkdir();

        		if(testSdCard.exists())
        			root = new File(Environment.getExternalStorageDirectory(),"cadie");
        		else
        			root = new File("/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadie");
        		
        		k = 4;

    	        if (!root.exists()) 
    	        	 root.mkdirs();
    	         
    	        String fName = new String();
    	        cFileName = cFileCreatedTime;
    	        
    	        k = 5;
    	        
	        	cFileName = cFileName.replace("/", "");
                cFileName = cFileName.replace(fileType, "");
                
    	       	fName = cFileCreatedTime+".mp4";
    	        
    	        k = 6;
    	        
    	        byte[] vidImageData = null;
    	        
    	        ByteArrayOutputStream baos;
    	        
    	        Bitmap bMap = ThumbnailUtils.createVideoThumbnail(mSelectedImagePath, MediaStore.Video.Thumbnails.MINI_KIND);
    	        
    	        k = 10;
    	        
    	        baos = new ByteArrayOutputStream();  
                
    	        bMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
    	        
    	        k = 11;
    	        
    	        vidImageData = baos.toByteArray();
    	        
    	        File vidThRoot = new File("/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/thumbnails");

    	        if (!vidThRoot.exists()) 
    	        	vidThRoot.mkdirs();
    	        
            	if(!vidThRoot.exists())
        			vidThRoot.mkdir();
                
                
                File TH_destination= new File(vidThRoot, "th_" + cFileCreatedTime + ".jpg");//TH_destinationImagePath);
                
                
                fileOuputStream = new FileOutputStream(TH_destination); 
                fileOuputStream.write(vidImageData);
                fileOuputStream.close();
                
                k = 13;
                bMap.recycle();
                
                vidImageData = null;
                
                baos = null;
                bMap = null;
                fis = null;
                fileOuputStream = null;
    	        
    	        CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED", "TAKE_VIDEO@" + cFileName+ "@" +fileType + "@" + fName + "@" + cLocation + "@" + cNoteSize + "@" + xFileSize);
    		}
        } 
        catch (Exception ex) 
        {
        	CameraInitFunction.cam.dispatchStatusEventAsync("ITEM_CAPTURED","NOT_CAPTURED");
        	
        	Log.d("CADIE GCM", "getOutputMediaFile Exception - " + k +  ex.getMessage());
        	Log.d("CADIE GCM", "getOutputMediaFile Exception - " + k +  ex.getLocalizedMessage());
        	Log.d("CADIE GCM", "getOutputMediaFile Exception - " + k +  ex.toString());
        	
        	Toast.makeText(getApplicationContext(), "getOutputMediaFile > "+ k +"  "+ ex.toString(), Toast.LENGTH_LONG).show();
        	
        	Toast.makeText(getApplicationContext(), "getOutputMediaFile > "+ k +"  "+ ex.toString(), Toast.LENGTH_LONG).show();
        	
        	ex.printStackTrace();
        } finally
        {
			finish();
    	}
    }
}
