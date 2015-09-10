package com.zahdoo.android.extension.openPDF;

import java.io.File;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.zahdoo.android.extension.OpenPDFExtensionContext;

public class PDFopenFunction implements FREFunction {

	private OpenPDFExtensionContext op;
	private String FileName = "";
	private String Action = "";
	
	@Override
	public FREObject call(FREContext context, FREObject[] passedArgs) {
		FREObject result = null; 
		op = (OpenPDFExtensionContext)context;
		
		try {
			Log.d("CADIE PDF", "OpenFile");
			FREObject fro = passedArgs[0];			
			String[] vStrings = fro.getAsString().split("\\^");
			FileName = vStrings[0];
			Action = vStrings[1];

			if(FileName.contains(".pdf"))
				openCreatedPDF(FileName);
			else
				openFile(FileName); 
		} 
		catch (Exception e) { 
			e.printStackTrace(); 
		} 
				return result;
	}
	
	private void openCreatedPDF(String fileName)
	{
		File pdfFile = new File(Environment.getExternalStorageDirectory() + "/cadie/" + fileName);
		
		if(!pdfFile.exists())
			pdfFile = new File("/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadie/" + fileName);

		 if(Action.equals("Email"))
			 Toast.makeText(op.getActivity(), "Attaching PDF to mail...", Toast.LENGTH_SHORT).show();
		 else
			 Toast.makeText(op.getActivity(), "Searching app to open this PDF...", Toast.LENGTH_SHORT).show();
		 
		if(pdfFile.exists()) 
	    {
			if(Action.equals("Email"))
            	SendEmail( fileName );
			else
			{
				 Uri path = Uri.fromFile(pdfFile); 
		        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
		        pdfIntent.setDataAndType(path, "application/pdf" );
		        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		        try
		        {
		        	op.getActivity().startActivity(pdfIntent);
		        }
		        catch(Exception e)
		        {
		        	Log.d("CADIE PDF", "OpenFile Error - " + e.toString());
		            Toast.makeText(op.getActivity(), "No Application found to view this PDF file", Toast.LENGTH_SHORT).show(); 
		        }
			        
			 }
		}
		else{
			Toast.makeText(op.getActivity(), "Pdf file does not exist ..  " + fileName, Toast.LENGTH_LONG).show();
		}
	}
		
		private void openFile(String fileName){
			File file = new File(Environment.getExternalStorageDirectory() + "/cadie/" + fileName);
			
			if(!file.exists())
				file = new File("/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadie" + fileName);

			 Toast.makeText(op.getActivity(), "Searching app to open this file...", Toast.LENGTH_SHORT).show();
			
			if(file.exists()) 
		    {
				
		        Uri path = Uri.fromFile(file); 
//		        if(Action.equals("AUDIO"))
//		        {
//		        	Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
//			        pdfIntent.setDataAndType(path, "audio/*" );
//			
//			        try
//			        {
//			        	op.getActivity().startActivity(pdfIntent);
//			        }
//			        catch(Exception e)
//			        {
//			        	Log.d("CADIE PDF", "OpenFile Error - " + e.toString());
//			            Toast.makeText(op.getActivity(), "No Application found to view this file", Toast.LENGTH_SHORT).show(); 
//			        }
//		        }
//		        else if(Action.equals("VIDEO"))
//		        {
//		        	Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
//			        pdfIntent.setDataAndType(path, "video/*" );
//			
//			        try
//			        {
//			        	op.getActivity().startActivity(pdfIntent);
//			        }
//			        catch(Exception e)
//			        {
//			        	Log.d("CADIE PDF", "OpenFile Error - " + e.toString());
//			            Toast.makeText(op.getActivity(), "No Application found to view this file", Toast.LENGTH_SHORT).show(); 
//			        }
//		        }
//		        else 
		        if(Action.equals("FILE"))
		        {
		        	Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
			        pdfIntent.setDataAndType(path, "*/*" );
			
			        try
			        {
			        	op.getActivity().startActivity(pdfIntent);
			        }
			        catch(Exception e)
			        {
			        	Log.d("CADIE PDF", "OpenFile Error - " + e.toString());
			            Toast.makeText(op.getActivity(), "No Application found to view this file", Toast.LENGTH_SHORT).show(); 
			        }
		        }
		        else{
		        	Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
		        	pdfIntent.setDataAndType(path, "application/*" );
			
			        try
			        {
			        	op.getActivity().startActivity(pdfIntent);
			        }
			        catch(Exception e)
			        {
			        	Log.d("CADIE PDF", "OpenFile Error - " + e.toString());
			            Toast.makeText(op.getActivity(), "No Application found to view this file", Toast.LENGTH_SHORT).show(); 
			        }
		        }
		    }
			else{
				Toast.makeText(op.getActivity(), "File does not exist ..  " + fileName, Toast.LENGTH_LONG).show();
			}
		}
		
	
	public void SendEmail(String fileName)
	  {
		  try {
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.setType("text/plain");
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{""});
			//emailIntent.putExtra(android.content.Intent.EXTRA_CC, new String[]{emailCC});
			//has to be an ArrayList
			//ArrayList<Uri> uris = new ArrayList<Uri>();
			//convert from paths to Android friendly Parcelable Uri's
			//for (String file : filePaths)
			//{
			//    File fileIn = new File(filePaths);
			//    Uri u = Uri.fromFile(fileIn);
			//    uris.add(u);
			//}
			//emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
			
			Toast.makeText(op.getActivity(), "Preparing to send mail", Toast.LENGTH_SHORT).show();
			
			emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			emailIntent.putExtra(Intent.EXTRA_SUBJECT,"CADIE Note PDF attached ...");
			emailIntent.putExtra(Intent.EXTRA_TEXT,"Mail Message ...");
			emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(Environment.getExternalStorageDirectory()+"/cadie/",fileName)));
			//emailIntent.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(new File(vn_PATH+"/"+vnFileName)));
			emailIntent.setType("application/pdf");
			op.getActivity().startActivity(emailIntent);
			
			Toast.makeText(op.getActivity(), "PDF attached...", Toast.LENGTH_SHORT).show();
		} 
		catch (Exception e) {
			new AlertDialog.Builder(op.getActivity())
			.setMessage("Not able to send email").setTitle("Error").setCancelable(true)
			.setNeutralButton("OK",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton){
					
				}
			})
			.show();
			return;
		}
	  }
}
