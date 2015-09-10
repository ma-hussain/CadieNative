/**
 * 
 */
package com.zahdoo.android.extension.camera;

import android.content.pm.PackageManager;
import android.widget.Toast;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.adobe.fre.FREWrongThreadException;
import com.zahdoo.android.extension.CameraExtensionContext;

public class CameraSupportedFunction implements FREFunction {

	public FREObject call(FREContext context, FREObject[] passedArgs) {
		FREObject result = null; 
		CameraExtensionContext cam = (CameraExtensionContext)context;
		
		try {
			PackageManager pm = cam.getActivity().getPackageManager();
			boolean frontCam, rearCam;

			//It would be safer to use the constant PackageManager.FEATURE_CAMERA_FRONT
			//but since it is not defined for Android 2.2, I substituted the literal value
			frontCam = pm.hasSystemFeature("android.hardware.camera.front");

			rearCam = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
			
			if ( frontCam || rearCam ) { 
				result = FREObject.newObject(true);
				Toast.makeText(cam.getActivity(), "Launching camera ...   Please wait ...", Toast.LENGTH_LONG).show();
			}
			else { 
				result = FREObject.newObject(false); 
				Toast.makeText(cam.getActivity(), "OOPS ...   Camera not found ...", Toast.LENGTH_LONG).show();
			}
		} catch (FREWrongThreadException fwte) {
			fwte.printStackTrace();
		}
		return result;
	}
}
