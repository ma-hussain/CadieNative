package com.zahdoo.android.extension.contacts;


import java.io.File;
import java.util.ArrayList;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.Html;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;

public class HTMLEmailFunction implements FREFunction 
{
	private String htmlCode 			= "";
	
	public FREObject call(FREContext arg0, FREObject[] passedArgs){
		FREObject result = null; 
		try { 
			FREObject fro = passedArgs[0];
			htmlCode =	fro.getAsString();
			sendEmail();
		} 
		catch (Exception e) { 
			e.printStackTrace(); 
		} 
		
		return result;
	}
	    
    private void sendEmail() 
    {
    	try{
    		//final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
    		//emailIntent.setType("text/html");
    		//emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
    		//emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(body));
    		//startActivity(Intent.createChooser(emailIntent, "Email:"));
    		
//	    		 File F = new File("/sdcardpath/imagename.png");
//	    		 Uri U = Uri.fromFile(F);
//	    		 Intent i = new Intent(Intent.ACTION_SEND);
//	    		 i.setType("image/*");
//	    		 i.putExtra(Intent.EXTRA_STREAM, U);
//	    		 startActivity(Intent.createChooser(i,"Email:"));
//	    		 
//	    		 final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
//	    		 emailIntent.setType("text/html");
//	    		 emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
//	    		 emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(body));
//	    		 startActivity(Intent.createChooser(emailIntent, "Email:"));
    		 
    		
//	    		String[] filePaths = new String[] {"sdcard/sample.png", "sdcard/sample.png"};
//	    		for (String file : filePaths)
//	    		{
//	    		    fileIn = new File(file);
//	    		    Uri u = Uri.fromFile(fileIn);
//	    		    uris.add(u);
//	    		}
    		
    		
    		File file;
    		ArrayList<Uri> uris = new ArrayList<Uri>();
    		String[] vStrings = htmlCode.split("\\^");
    		
    		for(int x=0; x < vStrings.length -1; x++){
    			file = new File(Environment.getExternalStorageDirectory() + "/cadie/",vStrings[x]);
    			
    			if(!file.exists())
    			{
    				file = new File("/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadie",vStrings[x]);
    			}
    			
    			Uri u = Uri.fromFile(file);
    		    uris.add(u);
    		}
			
    		String body = new String();
    		body =	vStrings[vStrings.length -1];
    		
    		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
	  	  	//emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
	  	  	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "CADIE Note");
	  	  	emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(body));
	  	  	emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
	  	  	emailIntent.setType("text/*");
	  	    ContactInitFunction.cContact.getActivity().startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    	}catch(Exception e){}
    }
}
