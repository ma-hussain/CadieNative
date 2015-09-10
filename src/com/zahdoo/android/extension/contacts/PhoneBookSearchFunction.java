package com.zahdoo.android.extension.contacts;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;

public class PhoneBookSearchFunction implements FREFunction {
	
	// To suppress notational clutter and make structure clearer, define some shorthand constants.
		private static final int DIALOG_ACCOUNTS=1;
		
		private static final Uri URI = ContactsContract.Contacts.CONTENT_URI;
		private static final Uri PURI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		
		private static final Uri EURI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
		private static final Uri AURI = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
		private static final String ID = ContactsContract.Contacts._ID;
		private static final String DNAME = ContactsContract.Contacts.DISPLAY_NAME;
		private static final String HPN = ContactsContract.Contacts.HAS_PHONE_NUMBER;
		//private static final String LOOKY = ContactsContract.Contacts.LOOKUP_KEY;
		private static final String CID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
		private static final String EID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
		private static final String AID = ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID;
		private static final String PNUM = ContactsContract.CommonDataKinds.Phone.NUMBER;
		private static final String PHONETYPE = ContactsContract.CommonDataKinds.Phone.TYPE;
		private static final String EMAIL = ContactsContract.CommonDataKinds.Email.DATA;
		private static final String EMAILTYPE = ContactsContract.CommonDataKinds.Email.TYPE;
		private static final String STREET = ContactsContract.CommonDataKinds.StructuredPostal.STREET;
		private static final String CITY = ContactsContract.CommonDataKinds.StructuredPostal.CITY;
		private static final String STATE = ContactsContract.CommonDataKinds.StructuredPostal.REGION;
		private static final String POSTCODE = ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE;
		private static final String COUNTRY = ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY;
		
		private String id = "";
		//private String lookupKey;
		private String name = "";
		private String street = "";
		private String city = "";
		private String state = "";
		private String postcode = "";
		private String country = "";
		private String ph[];
		private String phType[];
		private String em[];
		private String emType[];

		private int emcounter;
		private int phcounter;
		private int addcounter;
		
		private String vMsgContactName 			= "";
		private String vMsgContactNumber 		= "";
		private String vMsgContactNumberType 	= "";
		private String vMsgContactEmail 		= "";
		private String vMsgContactEmailType 	= "";
		private String vMsgContactAddress 		= "";
		
		private String vMsgContent = "";
		private String vAction = "";
		private String vMsgText = "";
		    
		private Boolean bFound=false;

	//	private List<ContactDetails> contactsList;
		//private Boolean bOtherInfo = false;

	
		

	//	private CADIEExtensionContext context;
		private Activity activity;
		private FREObject result=null;

	@Override
	public FREObject call(FREContext arg0, FREObject[] passedArgs) {
		FREObject result = null; 
		try { 
			FREObject fro = passedArgs[0];
			String[] vStrings = fro.getAsString().split("\\^");
			
			ph =new String[4];
			phType =new String[4];
			
			em =new String[4];
			emType =new String[4];
			
			vMsgContactName =	vStrings[0];
			vAction 	= vStrings[1];
			
			GetAddressDetails(vAction);
		} 
		catch (Exception e) { 
			e.printStackTrace(); 
		} 
		
		return result;
	}

	  private void GetAddressDetails(String strType){
	        try{
	        	vMsgContactNumber 	= "";
	        	vMsgContactEmail	= "";
	        	
		        ContentResolver cr = activity.getContentResolver();
		        Cursor cu = cr.query(URI, null, null, null, null);//
		        
		        if (cu.getCount() > 0 && vMsgContactName.length() > 0) {	
		        	
		        	// Loop over all contacts
		    	    while (cu.moveToNext()) { 	
		    	    	bFound = false;
		    	    	// Initialize storage variables for the new contact
		            	id = cu.getString(cu.getColumnIndex(ID));
		    	        name = cu.getString(cu.getColumnIndex(DNAME));          
		    	        
			    	    if(vMsgContactName.equalsIgnoreCase(name)){
			    	    	phcounter = 0;
			    	        try{
				    	    	if (Integer.parseInt(cu.getString(cu.getColumnIndex(HPN))) > 0) {	        	
				    	        	Cursor pCur = cr.query(PURI,  null, CID + " = ?",  new String[]{id}, null);
				         	        while (pCur.moveToNext() && (phcounter <4)) {
				         	        	ph[phcounter] = pCur.getString(pCur.getColumnIndex(PNUM));
				         	        	phType[phcounter]  = pCur.getString(pCur.getColumnIndex(PHONETYPE)); 
				         	        	
				         	        	 if(ph[phcounter] == null)
				         	        		ph[phcounter] = "";
				     	    			 
				     	    			 if(phType[phcounter] == null)
				     	    				phType[phcounter] = "";
				     	    			 
				         	        	phcounter++;
				         	        } 
				         	        pCur.close();
				    	        }
			    	        } catch (Error e) {
			    	        	Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
			    	        }
				    	       
			    	        emcounter = 0;
			    	        try{
				    	    	Cursor emailCur = cr.query(EURI, null, EID + " = ?",  new String[]{id}, null); 
			     	    		while (emailCur.moveToNext() && (emcounter <4)) { 
			     	    			 em[emcounter] =  emailCur.getString(emailCur.getColumnIndex(EMAIL));
			     	    			 emType[emcounter] =  emailCur.getString(emailCur.getColumnIndex(EMAILTYPE));
			     	    			 
			     	    			 if(em[emcounter] == null)
			     	    				 em[emcounter] = "";
			     	    			 
			     	    			 if(emType[emcounter] == null)
			     	    				emType[emcounter] = "";
			     	    			 
			     	    	 	     emcounter ++;
			     	    	 	 } 
				 	    	 	 emailCur.close();
			    	        } catch (Error e) {
			    	        	Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT);
			    	        }
			 	    	 	 
			 	    		 addcounter = 0;
			 	    		try{
				    	    	Cursor addCur = cr.query(AURI, null, AID + " = ?",  new String[]{id}, null); 
			     	    		 while (addCur.moveToNext()) { 
			     	    		    street 		= addCur.getString(addCur.getColumnIndex(STREET));
			     	    		    city 		= addCur.getString(addCur.getColumnIndex(CITY));
			     	    		    state 		= addCur.getString(addCur.getColumnIndex(STATE));
			     	    		    postcode 	= addCur.getString(addCur.getColumnIndex(POSTCODE));
			     	    		    country 	= addCur.getString(addCur.getColumnIndex(COUNTRY));
			     	    		    addcounter ++;
			     	    		    
			     	    		    if(street == null)
			     	    		    	street = "";
			     	    		    
			     	    		    if(city == null)
			     	    		    	city = "";
			     	    		    
			     	    		    if(state == null)
			     	    		    	state = "";
			     	    		    
			     	    		    if(postcode == null)
			     	    		    	postcode = "";
			     	    		    
			     	    		    if(country == null)
			     	    		    	country = "";
			     	    	 	 } 
			     	    		 addCur.close();
			 	    		} catch (Error e) {
			    	        	Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT);
			    	        }
		     	    		 bFound = true;
		     	    		 break;
			    	     }
		    	    }
		    	    cu.close();
		     	}
		        
		       if(name.length()>0 && bFound){
		        	vMsgContactName = name;
		        	
		        	if(ph[0] != null){
		        		vMsgContactNumber = ph[0];
		        		vMsgContactNumberType = getPhoneType(phType[0]);
		        	}
		        	
		        	if(em[0] != null){
		        		vMsgContactEmail = em[0];
		        		vMsgContactEmailType = getEmailType(emType[0]);
		        	}
		        	
		        	vMsgContactAddress = "";
		        	
		        	if(street.length()>0)
		        		vMsgContactAddress =  street;
		        	
		        	if(city.length()>0)
		        		vMsgContactAddress += ", " + city;
		        	
		        	if(state.length()>0)
		        		vMsgContactAddress += ", " + state;
		        	
		        	if(postcode.length()>0)
		        		vMsgContactAddress += ", postcode " + postcode;
		        		
		        	if(country.length()>0)
		        		vMsgContactAddress += ", " + country;
		        	
		        	if(strType.equalsIgnoreCase("PhoneBookSearch")){
			        	if(vMsgContactAddress.length()>0){
			        		vMsgText = "The address of " + vMsgContactName + " is, " + vMsgContactAddress;
			        	}
			        	
			        	if(vMsgContactNumber.length()>0){
			        		if(vMsgText.length()>0){
			        			vMsgText += ", and phone number is " + vMsgContactNumber;
			        		}
			        		else{
			        			vMsgText += "The phone number of " + vMsgContactName + " is " + vMsgContactNumber;
			        		}
			        	}
			        	
			        	if(vMsgContactEmail.length()>0){
			        		if(vMsgText.length()>0){
			        			vMsgText += ", and email address is " + vMsgContactNumber;
			        		}
			        		else{
			        			vMsgText += "The email address of " + vMsgContactName + " is " + vMsgContactNumber;
			        		}
			        	}
			        	
			        	if(vMsgText.length()==0){
		    	        	vMsgText = "I do not find any contact information of " + vMsgContactName;
		    	        }
		        	}
		        	else if(strType.equalsIgnoreCase("PhoneBookPhoneSearch")){
		        		if(vMsgContactNumber.length()>0){
		        			vMsgText = "The phone number of " + vMsgContactName + " is " + vMsgContactNumber;
		        		}
		        		else{
		    	        	vMsgText = "I do not find any phone number of " + vMsgContactName;
		    	        }
		        	}
					else if(strType.equalsIgnoreCase("PhoneBookMobilePhoneSearch")){
		        		if(getContactNumber("mobile")){
		        			vMsgText = "The mobile number of " + vMsgContactName + " is " + vMsgContactNumber;
			        	}
		        		else if(vMsgContactNumber.length()>0){
		        			vMsgText = "I don't have mobile number of " + vMsgContactName + " but having " + vMsgContactNumberType + " phone number.";
		        		}
		        		else{
		    	        	vMsgText = "I do not find any phone number of " + vMsgContactName;
		    	        }
		        	}
		        	else if(strType.equalsIgnoreCase("PhoneBookHomePhoneSearch")){
		        		if(getContactNumber("home")){
		        			vMsgText = "The home phone number of " + vMsgContactName + " is " + vMsgContactNumber;
			        	}
		        		else if(vMsgContactNumber.length()>0){
		        			vMsgText = "I don't have home phone number of " + vMsgContactName + " but having " + vMsgContactNumberType + " phone number.";
		        		}
		        		else{
		    	        	vMsgText = "I do not find any phone number of " + vMsgContactName;
		    	        }
		        	}
		        	else if(strType.equalsIgnoreCase("PhoneBookWorkPhoneSearch")){
		        		if(getContactNumber("work")){
		        			vMsgText = "The work phone number of " + vMsgContactName + " is " + vMsgContactNumber;
			        	}
		        		else if(vMsgContactNumber.length()>0){
		        			vMsgText = "I don't have work phone number of " + vMsgContactName + " but having " + vMsgContactNumberType + " phone number.";
		        		}
		        		else{
		    	        	vMsgText = "I do not find any phone number of " + vMsgContactName;
		    	        }
		        	}
		        	else if(strType.equalsIgnoreCase("PhoneBookOtherPhoneSearch")){
		        		if(getContactNumber("other")){
		        			vMsgText = "The other phone number of " + vMsgContactName + " is " + vMsgContactNumber;
			        	}
		        		else if(vMsgContactNumber.length()>0){
		        			vMsgText = "I don't have other phone number of " + vMsgContactName + " but having " + vMsgContactNumberType + " phone number.";
		        		}
		        		else{
		    	        	vMsgText = "I do not find any phone number of " + vMsgContactName;
		    	        }
		        	}
		        	else if(strType.equalsIgnoreCase("PhoneBookEmailSearch")){
		        		if(vMsgContactEmail.length()>0){
		        			vMsgText = "The email address of " + vMsgContactName + " is " + vMsgContactEmail;
		        		}
		        		else{
		    	        	vMsgText = "I do not find any email address of " + vMsgContactName;
		    	        }
		        	}
		        	else if(strType.equalsIgnoreCase("PhoneBookHomeEmailSearch")){
		        		if(getContactEmail("home")){
		        			vMsgText = "The home email address of " + vMsgContactName + " is " + vMsgContactEmail;
			        	}
		        		else if(vMsgContactEmail.length()>0){
		        			vMsgText = "I don't have home email address of " + vMsgContactName + " but having " + vMsgContactEmailType + " email address.";
		        		}
		        		else{
		    	        	vMsgText = "I do not find any email address of " + vMsgContactName;
		    	        }
		        	}
		        	else if(strType.equalsIgnoreCase("PhoneBookWorkEmailSearch")){
		        		if(getContactEmail("work")){
		        			vMsgText = "The work email address of " + vMsgContactName + " is " + vMsgContactEmail;
			        	}
		        		else if(vMsgContactEmail.length()>0){
		        			vMsgText = "I don't have work email address of " + vMsgContactName + " but having " + vMsgContactEmailType + " email address.";
		        		}
		        		else{
		    	        	vMsgText = "I do not find any email address of " + vMsgContactName;
		    	        }
		        	}
		        	else if(strType.equalsIgnoreCase("PhoneBookOtherEmailSearch")){
		        		if(getContactEmail("other")){
		        			vMsgText = "The other email address of " + vMsgContactName + " is " + vMsgContactEmail;
			        	}
		        		else if(vMsgContactEmail.length()>0){
		        			vMsgText = "I don't have other email address of " + vMsgContactName + " but having " + vMsgContactEmailType + " email address.";
		        		}
		        		else{
		    	        	vMsgText = "I do not find any email address of " + vMsgContactName;
		    	        }
		        	}
		        	else if(strType.equalsIgnoreCase("PhoneBookAddressSearch")){
		        		if(vMsgContactAddress.length()>0){
		        			vMsgText = "";
		        			if(vMsgContactNumber.length()>0 && vMsgContactEmail.length()>0){
			        			vMsgText = "The phone number of " + vMsgContactName + " is " + vMsgContactNumber + ", and email address is " + vMsgContactEmail;
			        		}
		        			else if(vMsgContactNumber.length()>0 && vMsgContactEmail.length()==0){
			        			vMsgText = "The phone number of " + vMsgContactName + " is " + vMsgContactNumber;
			        		}
		        			else if(vMsgContactNumber.length()==0 && vMsgContactEmail.length()>0){
			        			vMsgText = "The email address of " + vMsgContactName + " is " + vMsgContactEmail;
			        		}
		        			
		        			if(vMsgText.length()>0)
		        				vMsgText += " and ";
		        			
			        		vMsgText += "The address of " + vMsgContactName + " is, " + vMsgContactAddress;
			        	}
		        		else{
		        			Boolean bPrepared = false; 
			        		
			        		if((vMsgContactNumber.length()>0) && (vMsgContactEmail.length()>0)){
				        		vMsgText = "I don't have " + vMsgContactName + "'s address but having phone number and email address.";
				        		bPrepared = true;
				        	}
			        		
			        		if(vMsgContactNumber.length()>0 && !bPrepared){
				        		vMsgText = "I don't have " + vMsgContactName + "'s postal address but having phone number.";
				        	}
			        		
			        		if(vMsgContactEmail.length()>0 && !bPrepared){
				        		vMsgText = "I don't have " + vMsgContactName + "'s postal address but having email address.";
				        	}
			        		
			        		if(vMsgText.length()==0){
			    	        	vMsgText = "I do not find address details of " + vMsgContactName;
			    	        }
			        	}
		        	}
		        }
		        else{
		        	vMsgText = "I do not find any contact information of " + vMsgContactName;
		        }
		        	
//		        Intent mRead = new Intent(activity,ReadMessage.class);
//	        	mRead.putExtra("vMsgContactName", vMsgContactName);
//	        	mRead.putExtra("vMsgContactNumber", vMsgContactNumber);
//	        	mRead.putExtra("vMsgContactNumberType", vMsgContactNumberType);
//	        	mRead.putExtra("vMsgContactEmail", vMsgContactEmail);
//	        	mRead.putExtra("vMsgContactEmailType", vMsgContactEmailType);
//	        	mRead.putExtra("vMsgContactAddress", vMsgContactAddress);
//	        	mRead.putExtra("vMsgContent", vMsgContent);
//	        	mRead.putExtra("vMsgText", vMsgText);
//	        	mRead.putExtra("vAction", vAction);
//	        	mRead.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//	        	activity.startActivity(mRead);
	        	//activity.finish();
	        	
		        // Flush the PrintWriter to ensure everything pending is output before closing    
	        } catch (Error e) {
	        	Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
	        }
	    }
	  /** Method to return label corresponding to phone type code. Data for correspondence from
 	   http://developer.android.com/reference/android/provider/ContactsContract.
 	   CommonDataKinds.Phone.html  */
     
   private String getPhoneType(String index){
 	 	 if(index.trim().equals( "1")){
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
     
   private String getEmailType(String index){
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
   
   private Boolean getContactNumber(String strType){
   		phcounter = 0;
   		while (phcounter <4) { 
   			if(ph[phcounter] != null){
	    		if(getPhoneType(phType[phcounter]).equalsIgnoreCase(strType)){
	    			vMsgContactNumber = ph[phcounter];
	    			vMsgContactNumberType = phType[phcounter];
	    			return true;
	    		}
   			}
   			phcounter ++;
   		} 
   	
   		return false;
   }
   
   private Boolean getContactEmail(String strType){
   	emcounter = 0;
   	while (emcounter <4) { 
   		if(em[emcounter] != null){
	    		if(getPhoneType(emType[emcounter]).equalsIgnoreCase(strType)){
	    			vMsgContactEmail = em[emcounter];
	    			return true;
	    		}
   		}
	 	    emcounter ++;
	 	 } 
   	
   	return false;
   }
	    
}
