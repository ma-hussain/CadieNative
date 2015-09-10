package com.zahdoo.android.extension.GCM;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.zahdoo.android.extension.CadieGCMExtensionContext;

public class GetAllContactEmailFunction implements FREFunction {
	
	private static final Uri URI = ContactsContract.Contacts.CONTENT_URI;
	
	private static final Uri EURI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
	private static final String ID = ContactsContract.Contacts._ID;
	private static final String DNAME = ContactsContract.Contacts.DISPLAY_NAME;
	private static final String EID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
	private static final String EMAIL = ContactsContract.CommonDataKinds.Email.DATA;
	private static final String EMAILTYPE = ContactsContract.CommonDataKinds.Email.TYPE;
	private String id = "";
	private String name = "";
	
	private int emcounter;
	
	
	private String em[];
	private String emType[];
	
	public static CadieGCMExtensionContext gcmCon;
	private ContentResolver cr;
	
	@Override
	public FREObject call(FREContext context, FREObject[] passedArgs) {
		FREObject result = null; 
		gcmCon = (CadieGCMExtensionContext)context;
		cr = gcmCon.getActivity().getContentResolver();
		
		try {
			dorInBackground();
		} 
		catch (Exception fwte) {
			Log.d(CommonUtilities.TAG,"Exc 0 - " + fwte.toString());
		}
		return result;
	}

	
	
	private void dorInBackground() {
		try {
			new AsyncTask<Void, Void, String>() 
	        {
	            @Override
	            protected String doInBackground(Void... params) {
	            	return GetAllEmailAddress();
	            }

	            @Override
	            protected void onPostExecute(String msg) {
	            	//doGsonParsing(msg);
	            	Log.d(CommonUtilities.TAG,"Msg - " + msg);
	    			
	            	try{
		            	if(msg.contentEquals("ALL_CONTACTS_INSERTED"))
		            		gcmCon.dispatchStatusEventAsync("REGISTERED", "ALL_CONTACTS_INSERTED");
		            	else
		            		gcmCon.dispatchStatusEventAsync("REGISTERED", "NO_CONTACTS_FOUND");
	            	}
	            	catch(Exception e){
	            		Log.d(CommonUtilities.TAG,"Error MSG - " + e.toString());
	            	}
	            }
	        }.execute(null, null, null);
		} catch (Exception e) {
			 Log.d(CommonUtilities.TAG,"exception - " + e.toString());
		}
    }
	
	private String GetAllEmailAddress()
	{
		Boolean contactFound = false;
		
        try{
	        // Main loop to query the contacts database, extracting the information.  See
			// http://www.higherpass.com/Android/Tutorials/Working-With-Android-Contacts/
			
        	em = new String[4];
			emType = new String[4];
			Cursor cu = cr.query(URI, null, null, null, null);//
	       
	        Cursor emailCur ;
	        
	        if (cu.getCount() > 0) 
	        {	
	        	Log.d(CommonUtilities.TAG,"2  ");
				
	        	aDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
	        	
	        	//Toast.makeText(gcmCon.getActivity(),  "Reading phonebook...", Toast.LENGTH_SHORT).show();
	        	// Loop over all contacts
	    	    while (cu.moveToNext()) { 	
	    	    	// Initialize storage variables for the new contact
	    	    	
	            	id = cu.getString(cu.getColumnIndex(ID));
	    	        name = cu.getString(cu.getColumnIndex(DNAME));          
	    	        // lookupKey = cu.getString(cu.getColumnIndex(LOOKY));   	        
	    	        
	    	        emcounter = 0;
	     	        emailCur = cr.query(EURI, null, EID + " = ?",  new String[]{id}, null);
	     	        
     	    		while ( emailCur.moveToNext() && (emcounter < 4) ) 
     	    		{ 
     	    			 em[emcounter] =  emailCur.getString(emailCur.getColumnIndex(EMAIL));
     	    			 emType[emcounter] =  emailCur.getString(emailCur.getColumnIndex(EMAILTYPE));
     	    			 
     	    			 InsertPhoneBookData ( id, name, em[emcounter], getEmailType(emType[emcounter]) );
     	    	 	     emcounter ++;
     	    	 	     
     	    	 	     contactFound = true;
     	    	 	 } 
	 	    	 	 emailCur.close();
	    	    }
	    	    
	    	    cu.close();
	    	    aDataBase.close();
	     	}
	        else
	        {
	        	cu.close();
	        }
	        
        } catch (Error e) {
        	Log.d(CommonUtilities.TAG,"EXception - " + e.toString());
			e.printStackTrace();
        }
        finally
        {
        	Log.d(CommonUtilities.TAG,"contactFound - " + contactFound);
			
        	if(contactFound)
        		return "ALL_CONTACTS_INSERTED";
        	else
        		return "NO_CONTACTS_FOUND";
        }
    }
    
    private SQLiteDatabase aDataBase;
	
	private String aDB_PATH = "/data/data/air.com.zahdoo.cadie.debug/com.zahdoo.cadie.debug/Local Store/cadieAlertDB.sqlite";
    //private String aDB_PATH = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadieAlertDB.sqlite";
    
    private String getDateTime() 
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        Date date = new Date();
        //Date fromDate = Calendar.getInstance().getTime();
        return dateFormat.format(date);
    }
    
    public void InsertPhoneBookData(String id, String name, String email, String emailType )
	{
    	try{
    		ContentValues values = new ContentValues();
    		values.put("ID", id);
    		values.put("Name", name);
    		values.put("Email", email.toLowerCase());
    		values.put("EmailType", emailType);
    		values.put("isUser", 0);
    		values.put("AddedOn", getDateTime());
    		
    		aDataBase.insert("PhoneBook", null, values);
		}
    	catch (Exception e)
    	{
    		Log.d(CommonUtilities.TAG,"Exception 2 - " + e.toString());
		}
	}
	
	/** Method to return label corresponding to email type code. Data for correspondence from
    http://developer.android.com/reference/android/provider/ContactsContract.
    CommonDataKinds.Email.html  */
 
	private String getEmailType(String index)
	{
	 	 if(index.trim().equals( "1")){
	 		 return "home";
	 	 } else if (index.trim().equals("2")){
	 		 return "work";
	 	 } else if (index.trim().equals("3")){
	 		 return "other";
	 	 } else if (index.trim().equals("4")){
	 		 return "mobile";
	 	 } else {
	 		 return "?";
	 	 }
	}
}
