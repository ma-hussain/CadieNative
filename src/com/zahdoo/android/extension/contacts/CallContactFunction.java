package com.zahdoo.android.extension.contacts;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;

public class CallContactFunction implements FREFunction {

	private static final Uri URI 	= ContactsContract.Contacts.CONTENT_URI;
	private static final Uri PURI 	= ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
	
	private static final String ID 			= ContactsContract.Contacts._ID;
	private static final String DNAME 		= ContactsContract.Contacts.DISPLAY_NAME;
	private static final String HPN 		= ContactsContract.Contacts.HAS_PHONE_NUMBER;
	private static final String CID 		= ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
	private static final String PNUM 		= ContactsContract.CommonDataKinds.Phone.NUMBER;
	private static final String PHONETYPE 	= ContactsContract.CommonDataKinds.Phone.TYPE;
	
	private String id 		= "";
	private String name 	= "";
	private String ph[];
	private String phType[];
	private int phcounter;
	
	private String vAction 				= "";
	private String vMsgContactName 		= "";
	private String vMsgContactNumber 	= "";
	
	private Boolean bFound=false;
	
	public FREObject call(FREContext context, FREObject[] passedArgs) {
		FREObject result = null; 
		try { 
			FREObject fro = passedArgs[0];
			String[] vStrings = fro.getAsString().split("\\^");
			
			ph 		= new String[4];
	        phType 	= new String[4];
		        
	        vAction = vStrings[0];
			vMsgContactNumber =	vStrings[1];
			
			vMsgContactName = vMsgContactNumber.replace("+91", "");
			vMsgContactName = vMsgContactNumber.replace("+1", "");
			
	        DoAction();
		} 
		catch (Exception e) { 
			e.printStackTrace(); 
		} 
		
		return result;
	}
	
	private void DoAction(){
        try{
	        // Main loop to query the contacts database, extracting the information.  See
			// http://www.higherpass.com/Android/Tutorials/Working-With-Android-Contacts/
			
			ContentResolver cr = ContactInitFunction.cContact.getActivity().getContentResolver();
	        Cursor cu = cr.query(URI, null, null, null, null);
	        
	        name = "";
	        
	        if (cu.getCount() > 0 && vMsgContactName.length()>0) {	
	        	// Loop over all contacts
	    	    while (cu.moveToNext()) { 	
	    	    	bFound = false;
	    	    	// Initialize storage variables for the new contact
	            	id = cu.getString(cu.getColumnIndex(ID));
	    	        name = cu.getString(cu.getColumnIndex(DNAME));          
	    	        
	    	        if(vMsgContactName.equalsIgnoreCase(name)){
	    	        	
	    	        	phcounter = 0;
	    	        	
		    	        if (Integer.parseInt(cu.getString(cu.getColumnIndex(HPN))) > 0) {	        	
		    	        	Cursor pCur = cr.query(PURI,  null, CID + " = ?",  new String[]{id}, null);
		         	        while (pCur.moveToNext() && (phcounter <4)) {
		         	        	ph[phcounter] = pCur.getString(pCur.getColumnIndex(PNUM));
		         	        	phType[phcounter]  = pCur.getString(pCur.getColumnIndex(PHONETYPE)); 
		         	        	
		         	        	if(getPhoneType(phType[phcounter]).equals("mobile")){
		         	        		vMsgContactNumber = ph[phcounter];
		         	        		bFound = true;
				    	        	break;
				    	        }
		         	        	
		         	        	phcounter++;
		         	        } 
		         	        pCur.close();
		    	        }
	    	        }
	    	        
	    	        if(bFound)
	    	        	break;
	    	        else
	    	        	name = "";
	    	    }
	    	    cu.close();
	     	}
	        
	        
	        if(bFound){
	        	if(vAction.equals("SMS")){
		        	Intent sendIntent = new Intent(Intent.ACTION_VIEW);
		        	if(bFound){
		        		sendIntent.putExtra("address", vMsgContactNumber);
			        }
			        else{
			        	sendIntent.putExtra("address", getContactNumber());
			        }
			        
			        sendIntent.putExtra("sms_body", "Content of the SMS goes here..."); 
		    	  	sendIntent.setType("vnd.android-dir/mms-sms");
		    	  	ContactInitFunction.cContact.getActivity().startActivity(sendIntent);
		    	  	//ContactInitFunction.cContact.getActivity().finish();
	        	}
	        	else{
	        		Toast.makeText(ContactInitFunction.cContact.getActivity(), "Calling " + vMsgContactName + " at " + vMsgContactNumber, Toast.LENGTH_SHORT).show();

                	Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + vMsgContactNumber));
                	ContactInitFunction.cContact.getActivity().startActivity(intent);
                	//ContactInitFunction.cContact.getActivity().finish();
	        	}
        	}
	        else{
	        	Toast.makeText(ContactInitFunction.cContact.getActivity(), "Phone number or Contact not found", Toast.LENGTH_SHORT).show();
	        }
        }
        catch (Error e) {
        	Toast.makeText(ContactInitFunction.cContact.getActivity(), "Contact not found or Phonebook not avilable", Toast.LENGTH_SHORT).show();
        }
    }
	
	private String getPhoneType(String index){
 	 	 if(index.trim().equals("1")){
 	 		 return "home";
 	 	 } else if (index.trim().equals("2")){
 	 		 return "mobile";
 	 	 } else if (index.trim().equals("3")){
 	 		 return "work";
 	 	 } else if (index.trim().equals("7")){
 	 		 return "other";
 	 	 } else {
 	 		 return "?";
 	 	 }
   }  
     
   /** Method to return label corresponding to email type code. Data for correspondence from
        http://developer.android.com/reference/android/provider/ContactsContract.
        CommonDataKinds.Email.html  */
     
   private String getContactNumber(){
	   phcounter = 0;
	   while (phcounter <4) { 
		   if(ph[phcounter] != null){
	    		if(getPhoneType(phType[phcounter]).equalsIgnoreCase("home") ||
	    				getPhoneType(phType[phcounter]).equalsIgnoreCase("work")){
	    			vMsgContactNumber = ph[phcounter];
	    			return vMsgContactNumber;
	    		}
   			}
   			phcounter ++;
	   } 
   	
   		Toast.makeText(ContactInitFunction.cContact.getActivity(), "Not found any contact number of " + vMsgContactNumber, Toast.LENGTH_SHORT).show();
   		return "";
	}
}
