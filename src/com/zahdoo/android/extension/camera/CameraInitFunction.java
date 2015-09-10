package com.zahdoo.android.extension.camera;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.zahdoo.android.extension.CameraExtensionContext;

public class CameraInitFunction implements FREFunction {

	public static CameraExtensionContext cam;
	//public static FileOutputStream fileOuputStream;
	//public static ByteArrayOutputStream baos;
	//public static File sd;
	//public static File source;
	//public static File destination;
	
	//public static FileInputStream fis;
	//public static Bitmap imageBitmap;
	
	@Override
	public FREObject call(FREContext context, FREObject[] arg1) {
		// TODO Auto-generated method stub
		cam = (CameraExtensionContext)context;
		return null;
	}

}
