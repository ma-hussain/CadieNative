package com.zahdoo.android.extension.contacts;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;

public class EmailContactFunction implements FREFunction {
	private static final Uri URI = ContactsContract.Contacts.CONTENT_URI;
	
	private static final Uri EURI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
	private static final String ID = ContactsContract.Contacts._ID;
	private static final String DNAME = ContactsContract.Contacts.DISPLAY_NAME;
	private static final String EID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
	private static final String EMAIL = ContactsContract.CommonDataKinds.Email.DATA;
	private static final String EMAILTYPE = ContactsContract.CommonDataKinds.Email.TYPE;
	private String id = "";
	private String name = "";
	private String em[];
	private String emType[];
	//
	private int emcounter;
	
	private String vMsgContactName 			= "";
	private String vMsgContactEmail 		= "";
	private String vMsgContactEmailType 	= "";
	
	    
	private Boolean bFound=false;
	
	public FREObject call(FREContext arg0, FREObject[] passedArgs){
		FREObject result = null; 
		try { 
			FREObject fro = passedArgs[0];
			
			em =new String[4];
			emType =new String[4];
			
			vMsgContactName =	fro.getAsString();
			
			if(vMsgContactName.contentEquals("GET_ALL_CONTACT_EMAIL_ADDRESS")){
				GetAllEmailAddress();
			}
			else{
				GetEmailAddress();
			}
		} 
		catch (Exception e) { 
			e.printStackTrace(); 
		} 
		
		return result;
	}

	  private void GetEmailAddress(){
	        try{
		        // Main loop to query the contacts database, extracting the information.  See
				// http://www.higherpass.com/Android/Tutorials/Working-With-Android-Contacts/
				
				ContentResolver cr = ContactInitFunction.cContact.getActivity().getContentResolver();
		        Cursor cu = cr.query(URI, null, null, null, null);//
		        
		        if (cu.getCount() > 0 && vMsgContactName.length()>0) {	
		        	Toast.makeText(ContactInitFunction.cContact.getActivity(),  "Searching for " + vMsgContactName + " details", Toast.LENGTH_SHORT).show();
		        	// Loop over all contacts
		    	    while (cu.moveToNext()) { 	
		    	    	// Initialize storage variables for the new contact
		    	    	bFound = false;
		            	
		            	id = cu.getString(cu.getColumnIndex(ID));
		    	        name = cu.getString(cu.getColumnIndex(DNAME));          
		    	        // lookupKey = cu.getString(cu.getColumnIndex(LOOKY));   	        
		    	        
		    	        if(vMsgContactName.equalsIgnoreCase(name)){
		    	        	//searchType is email
	    	        		emcounter = 0;
			     	        Cursor emailCur = cr.query(EURI, null, EID + " = ?",  new String[]{id}, null); 
		     	    		while (emailCur.moveToNext() && (emcounter <4)) { 
		     	    			 em[emcounter] =  emailCur.getString(emailCur.getColumnIndex(EMAIL));
		     	    			 emType[emcounter] =  emailCur.getString(emailCur.getColumnIndex(EMAILTYPE));
		     	    	 	     emcounter ++;
		     	    	 	 } 
			 	    	 	 emailCur.close();
		    	        	 bFound = true;
			 	    	 	 break;
		    	        }
		    	    }
		    	    cu.close();
		     	}
		        
		        if(name.length()>0 && bFound){
		        	vMsgContactName = name;
		        	
		        	if(em[0] != null){
		        		vMsgContactEmail = em[0];
		        		vMsgContactEmailType = getEmailType(emType[0]);
		        		
			        	if(vMsgContactEmailType.length()>1)
			        		Toast.makeText(ContactInitFunction.cContact.getActivity(), "Sending Email to " + vMsgContactName + " at " + vMsgContactEmailType + " mail address", Toast.LENGTH_SHORT).show();
			        	else
			        		Toast.makeText(ContactInitFunction.cContact.getActivity(), "Sending Email to " + vMsgContactName + " at " + vMsgContactEmail , Toast.LENGTH_SHORT).show();
			        	
			        	sendEmail(vMsgContactEmail);
			        	//ContactInitFunction.cContact.getActivity().finish();
			        	return;
		        	}
		        }
		      
		        Toast.makeText(ContactInitFunction.cContact.getActivity(), "Email address of " + vMsgContactName + " not found.", Toast.LENGTH_SHORT).show();
		        //ContactInitFunction.cContact.getActivity().finish();
		        
	        } catch (Error e) {
				e.printStackTrace();
	        }
	    }
	    
	    private void sendEmail(String pEmail) {
	    	try{
	    		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		  	  	String[] recipients = new String[]{pEmail, "",};
		  	  	emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
		  	  	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Hi");
		  	  	emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "type your message here...");
		  	  	emailIntent.setType("text/plain");
		  	    ContactInitFunction.cContact.getActivity().startActivity(Intent.createChooser(emailIntent, "Send mail..."));
		  	    //ContactInitFunction.cContact.getActivity().finish();
	    	}catch(Exception e){}
	    }
	    
	    private String getEmailType(String index){
	  	 	 if(index.trim().equals("1")){
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
	    
	    private void GetAllEmailAddress(){
	        try{
		        // Main loop to query the contacts database, extracting the information.  See
				// http://www.higherpass.com/Android/Tutorials/Working-With-Android-Contacts/
				
	        	em = new String[4];
				emType = new String[4];
				
				ContentResolver cr = ContactInitFunction.cContact.getActivity().getContentResolver();
		        Cursor cu = cr.query(URI, null, null, null, null);//
		        
		        if (cu.getCount() > 0) 
		        {	
		        	aDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
		        	
		        	Toast.makeText(ContactInitFunction.cContact.getActivity(),  "Reading phonebook...", Toast.LENGTH_SHORT).show();
		        	// Loop over all contacts
		    	    while (cu.moveToNext()) { 	
		    	    	// Initialize storage variables for the new contact
		    	    	
		            	id = cu.getString(cu.getColumnIndex(ID));
		    	        name = cu.getString(cu.getColumnIndex(DNAME));          
		    	        // lookupKey = cu.getString(cu.getColumnIndex(LOOKY));   	        
		    	        
		    	        emcounter = 0;
		     	        Cursor emailCur = cr.query(EURI, null, EID + " = ?",  new String[]{id}, null);
		     	        
	     	    		while ( emailCur.moveToNext() && (emcounter < 4) ) 
	     	    		{ 
	     	    			 em[emcounter] =  emailCur.getString(emailCur.getColumnIndex(EMAIL));
	     	    			 emType[emcounter] =  emailCur.getString(emailCur.getColumnIndex(EMAILTYPE));
	     	    			 
	     	    			 InsertPhoneBookData ( id, name, em[emcounter], emType[emcounter] );
	     	    	 	     emcounter ++;
	     	    	 	 } 
		 	    	 	 emailCur.close();
	    	        	
		    	    }
		    	    cu.close();
		     	}
		        else
		        {
		        	Toast.makeText(ContactInitFunction.cContact.getActivity(),  "No contact found ", Toast.LENGTH_SHORT).show();
		        }
		        
	        } catch (Error e) {
				e.printStackTrace();
	        }
	        finally
	        {
	        	if(aDataBase.isOpen())
	        		aDataBase.close();
	        }
	    }
	    
	    private SQLiteDatabase aDataBase;
		
		private String aDB_PATH = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadieAlertDB.sqlite";
	    
		public void InsertPhoneBookData(String id, String name, String email, String emailType )
		{
	    	try{
	    		ContentValues values = new ContentValues();
	    		values.put("ID", id);
	    		values.put("Name", name);
	    		values.put("Email", email);
	    		values.put("EmailType", emailType);
	    		values.put("isUser", 0);
	    		
	    		aDataBase.insert("PhoneBook", null, values);
			}
	    	catch (Exception e)
	    	{
				Toast.makeText(ContactInitFunction.cContact.getActivity(), "Phonebook Error \n" + e.toString(), Toast.LENGTH_LONG).show();
			}
		}
	    
}
