package com.zahdoo.android.extension.camera;

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;

public class PlayVideoFunction implements FREFunction {

	@Override
	public FREObject call(FREContext context, FREObject[] passedArgs) {
			FREObject result = null; 
		
		try {
			FREObject fro = passedArgs[0];
			String vString = fro.getAsString();
			
			Intent i = new Intent(Intent.ACTION_VIEW);

			String path =  ""; 

			////
			File testSdCard = new File(Environment.getExternalStorageDirectory() + "/cadie");

			if(!testSdCard.exists())
				testSdCard.mkdir();

			if(testSdCard.exists())
			{
				path = Environment.getExternalStorageDirectory()+ "/cadie/" + vString; 
			}
			else
			{
				path = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadie/" + vString;
			}
			////
			
			
			File file = new File(path);
		    i.setDataAndType(Uri.fromFile(file), "video/*");
		    
		    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    CameraInitFunction.cam.getActivity().startActivity(i);
    	
		} 
		catch (Exception e) 
		{ 
			e.printStackTrace(); 
		} 
		
		return result;
	}
		  
//		  else if(vStrings[0].equals("VIDEO")){
//			  File file = new File("/sdcard/cadie/"+vStrings[1]);
//			 // Toast.makeText(CADIENativeService.this, Uri.fromFile(file) + "" , Toast.LENGTH_SHORT).show();
//		      i.setDataAndType(Uri.fromFile(file), "video/*");
//		  }
		  
	
	

}