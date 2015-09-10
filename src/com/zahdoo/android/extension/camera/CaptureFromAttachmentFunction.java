package com.zahdoo.android.extension.camera;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;

public class CaptureFromAttachmentFunction implements FREFunction {

	@Override
	public FREObject call(FREContext context, FREObject[] passedArgs) {

		FREObject result = null; 
		
		try{
			FREObject fro = passedArgs[0];	
			String[] vStrings = fro.getAsString().split("\\^");
		    
			  Intent vCam = new Intent(CameraInitFunction.cam.getActivity(),BuiltInAttachment.class);
			  vCam.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			  vCam.putExtra("cType", vStrings[0]);
			  vCam.putExtra("cAction", vStrings[1]);
			  vCam.putExtra("cFileName", vStrings[2]);
			  vCam.putExtra("cNoteSize", vStrings[3]);
			  
			  Log.d("CADIE FILE", "CaptureFromAttachmentFunction - " + vStrings[0]);
			  CameraInitFunction.cam.getActivity().startActivity(vCam);
		}
		catch(Exception e)
		{
			  Toast.makeText(CameraInitFunction.cam.getActivity(),"C > " +  e.getMessage().toString() , Toast.LENGTH_SHORT).show();
		}
		
		return result;
	}

}
