package com.zahdoo.android.extension.voiceinput;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.zahdoo.android.extension.VoiceInputExtensionContext;

public class VoiceResponseFunction implements FREFunction {

	public static VoiceInputExtensionContext vr;
	
	private static final Uri URI = ContactsContract.Contacts.CONTENT_URI;
	private static final Uri PURI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
	
	private static final Uri EURI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
	private static final Uri AURI = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI;
	private static final String ID = ContactsContract.Contacts._ID;
	private static final String DNAME = ContactsContract.Contacts.DISPLAY_NAME;
	private static final String HPN = ContactsContract.Contacts.HAS_PHONE_NUMBER;
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
	private static final String ADDTYPE = ContactsContract.CommonDataKinds.StructuredPostal.TYPE;
	
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
	private String add[];
	private String addType[];

	private int emcounter;
	private int phcounter;
	private int addcounter;
	
	private String vMsgContactName 			= "";
	private String vMsgContactNumber 		= "";
	private String vMsgContactNumberType 	= "";
	private String vMsgContactEmail 		= "";
	private String vMsgContactEmailType 	= "";
	private String vAddress 			    = "";
	private String vMsgContactAddress 		= "";
	private String vMsgContactAddressType   = "";
	
	private String vMsgContent = "";
	private String vAction = "";
	private String vMsgText = "";
	    
	private Boolean bFound=false;

	private List<ContactDetails> contactsList;
	
	public FREObject call(FREContext context, FREObject[] passedArgs) 
	{
		vr = (VoiceInputExtensionContext)context;
		
		ph 		= new String[4];
        phType 	= new String[4];
        em 		= new String[4];
        emType 	= new String[4];
		add		= new String[4];
        addType	= new String[4];
	        
		try{
			FREObject fro = passedArgs[0];
			String[] vStrings = fro.getAsString().split("\\^");
			
			if(vStrings.length > 1){
				if(vStrings[0].equalsIgnoreCase("CallContact"))
				{
					Toast.makeText(VoiceResponseFunction.vr.getActivity(), "Calling " + vStrings[1] + " at " + vStrings[2], Toast.LENGTH_SHORT).show();
                	
                	Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + vStrings[2]));
                    vr.getActivity().startActivity(intent);
				}
				else if(vStrings[0].equalsIgnoreCase("PhoneBookResponse"))
				{
					vr.dispatchStatusEventAsync("VOICE_CONVERSATION", vStrings[0]);
				}
				else if(vStrings[0].equalsIgnoreCase("SendEmailResponse"))
				{
					vr.dispatchStatusEventAsync("VOICE_CONVERSATION", vStrings[0]);
				}
				else if(vStrings[0].equalsIgnoreCase("SendEmail"))
				{
					Toast.makeText(vr.getActivity(), "Sending mail ...", Toast.LENGTH_SHORT).show();
    	        	
					Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
    	        	String[] recipients = new String[]{vStrings[1]};
    	        	emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
    	        	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, vStrings[2]);
    	        	emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, vStrings[3]);
    	        	emailIntent.setType("text/plain");
    	        	
    	        	final PackageManager pm = vr.getActivity().getPackageManager();
    	            final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
    	            ResolveInfo best = null;
    	            for (final ResolveInfo info : matches)
    	              if (info.activityInfo.packageName.endsWith(".gm") ||
    	                  info.activityInfo.name.toLowerCase().contains("gmail")) best = info;
    	            if (best != null)
    	            	emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
    	            
    	            vr.getActivity().startActivity(emailIntent);
				}
				else if(vStrings[0].equalsIgnoreCase("SendSMS"))
				{
					Toast.makeText(vr.getActivity(), "Sending text message to " + vStrings[1] + " ...", Toast.LENGTH_SHORT).show();
					sendSMS(vStrings[2],vStrings[3]);
				}
				else if(vStrings[1].equalsIgnoreCase("Context"))
				{
					  Intent vRec = new Intent(vr.getActivity(),ReadMessage.class);
					  vRec.putExtra("vResponseMsg", vStrings[0]);
					  vRec.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					  vr.getActivity().startActivity(vRec);
				}
				else if(vStrings[1].equalsIgnoreCase("CallContact")||
						  vStrings[1].equalsIgnoreCase("CallContactAtMobile")||
						  vStrings[1].equalsIgnoreCase("CallContactAtHome")||
						  vStrings[1].equalsIgnoreCase("CallContactAtWork")||
						  vStrings[1].equalsIgnoreCase("CallContactAtOther"))
				{
					try {
						vMsgContactName 	= vStrings[0];
						vMsgText			= "Calling";
						vAction 			= vStrings[1];
							
						FindContactNumber(vAction);
					} 
					catch (Exception e) {
						Toast.makeText(context.getActivity(), "Error : " + e.toString(), Toast.LENGTH_SHORT).show();
					  	e.printStackTrace();
					}
				}
				else if(vStrings[1].equalsIgnoreCase("SendTextMessage")		||
						  vStrings[1].equalsIgnoreCase("SendTextMessageAtHome") ||
						  vStrings[1].equalsIgnoreCase("SendTextMessageAtWork") ||
						  vStrings[1].equalsIgnoreCase("SendTextMessageAtOther")||
						  vStrings[1].equalsIgnoreCase("SendTextMessageAtMobile"))
				  {
					  String[] arrNameNMessage = vStrings[0].toString().split("\\@");
					  
					  vMsgContactName 	= arrNameNMessage[0];
					  vMsgText			= arrNameNMessage[1];
					  vAction 			= vStrings[1];
					  vMsgContent 		= vMsgText;

					  if(vMsgContactName.length()>0)
					  {
						  GetContactFromPhoneBook(vMsgContactName,"number");
					  }
					  else{
						  Toast.makeText(vr.getActivity(), "Phone number or contact not found", Toast.LENGTH_SHORT).show();
					  }
				  }
				  else if(vStrings[1].equalsIgnoreCase("SendEmail")||
						  vStrings[1].equalsIgnoreCase("SendEmailAtHome") ||
						  vStrings[1].equalsIgnoreCase("SendEmailAtWork") ||
						  vStrings[1].equalsIgnoreCase("SendEmailAtOther"))
				  {
					  try {
						  	vMsgContactName 	= vStrings[0];
							vMsgText			= "Email";
							vAction 			= vStrings[1];
							vMsgContent 		= vMsgText;
							
							vMsgContactEmail		= "";
							vMsgContactEmailType	= "";
				        	
				        	if(vMsgContactName.length()>0){
				        		GetContactFromPhoneBook(vMsgContactName,"email");
				        	}
				        	else{
				        		Toast.makeText(vr.getActivity(), "Email address or contact not found", Toast.LENGTH_SHORT).show();
				        	}
					  	} catch (Exception e) {
					  		Toast.makeText(vr.getActivity(), "Error : " + e.toString(), Toast.LENGTH_SHORT).show();
					  	}
				  }
				  else if(vStrings[1].equalsIgnoreCase("PhoneBookSearch") ||
						  vStrings[1].equalsIgnoreCase("PhoneBookEmailSearch")||
						  vStrings[1].equalsIgnoreCase("PhoneBookHomeEmailSearch")||
						  vStrings[1].equalsIgnoreCase("PhoneBookWorkEmailSearch")||
						  vStrings[1].equalsIgnoreCase("PhoneBookOtherEmailSearch")||
						  vStrings[1].equalsIgnoreCase("PhoneBookPhoneSearch")||
						  vStrings[1].equalsIgnoreCase("PhoneBookMobilePhoneSearch")||
						  vStrings[1].equalsIgnoreCase("PhoneBookHomePhoneSearch")||
						  vStrings[1].equalsIgnoreCase("PhoneBookWorkPhoneSearch")||
						  vStrings[1].equalsIgnoreCase("PhoneBookOtherPhoneSearch")||
						  vStrings[1].equalsIgnoreCase("PhoneBookHomeAddressSearch")||
						  vStrings[1].equalsIgnoreCase("PhoneBookWorkAddressSearch")||
						  vStrings[1].equalsIgnoreCase("PhoneBookOtherAddressSearch")||
						  vStrings[1].equalsIgnoreCase("PhoneBookAddressSearch"))
				  {
					  try {						  
						  	vMsgContactName 	= vStrings[0];
							vMsgText			= "";
							vAction 			= vStrings[1];
							
							GetAddressDetails(vr.getActivity(),vAction);
							
					  	} catch (Exception e) {
					  		Toast.makeText(vr.getActivity(), "Error : " + e.toString(), Toast.LENGTH_SHORT).show();
					  		e.printStackTrace();
					  	}
				  }
				  else{
					  Intent vRec = new Intent(vr.getActivity(),VoiceResultTTS.class);
					  vRec.putExtra("vResponseMsg", vStrings[0]);// inStream.readUTF());
					  vRec.putExtra("vPromptMsg", vStrings[1]);
					  vRec.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					 // vr.getActivity().startActivity(vRec);
				  }
			  }
		  }catch(Exception e){
			  Toast.makeText(vr.getActivity(), "Catch : " + e.toString(), Toast.LENGTH_SHORT).show();
		  }
		
		return null;
	}
	
	
	private void FindContactNumber(String strType){
		contactsList = GetDetails(vMsgText);
    	ListIterator li = contactsList.listIterator();
    	
    	int exactCount		=	0;
    	int startWithCount	=	0;
    	ContactDetails onePersoneDetails = null;
    	
    	String tempInput = vMsgContactName.toLowerCase();
    	String tempName = "";
    	
    	while(li.hasNext()){
    		ContactDetails cd = (ContactDetails) li.next();
    		
    		tempName = cd.getName().toLowerCase();
    		
    		if(vMsgContactName.equalsIgnoreCase(cd.getName()))
    		{
    			onePersoneDetails = cd;
    			exactCount++;
    		}
    		else if(tempName.startsWith(tempInput) ||
    				tempName.endsWith(tempInput))
    		{
     		   	onePersoneDetails = cd;
     		   	startWithCount++;
    		}
    	}
    	
    	//Toast.makeText(vr.getActivity(), startWithCount + " <<startWithCount || exactCount >> " + exactCount, Toast.LENGTH_SHORT).show();
		
    	
    	if( exactCount == 1 && startWithCount == 0 ){
        	callContact(onePersoneDetails,vAction);
        }
    	else if((exactCount == 1 && startWithCount > 0) || (exactCount > 1) )
    	{
      	  sayMessage("Got too many " + vMsgContactName + ", please say full name");
        }
        else if( exactCount == 0 ){
    		if( startWithCount > 0 ){
    			if( startWithCount > 1 )
    				sayMessage("Got too many " + vMsgContactName + ", please say full name");
    			else
    				callContact(onePersoneDetails,vAction);
    		}
    		else{
    			if(isNumeric(vMsgContactName)){
    				
    				Toast.makeText(vr.getActivity(), "NOW HERE" , Toast.LENGTH_SHORT).show();
			  		
                	Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + vMsgContactName));
                	vr.getActivity().startActivity(intent);
                	vr.getActivity().finish();
            	}
    			else
    				sayMessage("Contact not found");
    		}
    	}
	}
	
	 private List<ContactDetails> GetDetails(String strType){
	    	List<ContactDetails> list = new ArrayList<ContactDetails>();
	    	
	    	ph 		= new String[4];
	        phType 	= new String[4];
	        em 		= new String[4];
	        emType 	= new String[4];
	        
	        try{
		        ContentResolver cr = vr.getActivity().getContentResolver();
		        Cursor cu = cr.query(URI, null, null, null, null);//
		        
		        if (cu.getCount() > 0 && vMsgContactName.length() > 0) {	
		        	// Loop over all contacts
		    	    while (cu.moveToNext()) { 
		    	    	bFound = false;
		    	    	// Initialize storage variables for the new contact
		    	    	ContactDetails cDetails = new ContactDetails();
		    	    	
		            	id 		= 	cu.getString( cu.getColumnIndex( ID ) );
		    	        name 	= 	cu.getString( cu.getColumnIndex( DNAME ) );
		    	        
			    	    if( name!=null && 
			    	    		(name.toLowerCase().contains(vMsgContactName.toLowerCase()) || 
			    	    				name.toLowerCase().equalsIgnoreCase(vMsgContactName.toLowerCase())
			    	    		)
			    	    	)
			    	    {
			    	    	cDetails.setId( id );
			    	        cDetails.setName( name );
			    	        
			    	    	if(strType.equalsIgnoreCase( "Calling" ) )
			    	    	{
			    	    		phcounter = 0;
			    	    	
				    	        if (Integer.parseInt(cu.getString(cu.getColumnIndex(HPN))) > 0) 
				    	        {	        	
				    	        	Cursor pCur = cr.query(PURI,  null, CID + " = ?",  new String[]{id}, null);
				    	        	
				         	        while (pCur.moveToNext() && (phcounter <4)) 
				         	        {
				         	        	ph[phcounter] 		= 	pCur.getString( pCur.getColumnIndex( PNUM ) );
				         	        	phType[phcounter]  	=	pCur.getString( pCur.getColumnIndex( PHONETYPE ) ); 
				         	        	 
				         	        	vMsgContactNumber 	= 	ph[phcounter];

				         	        	if(ph[phcounter] == null)
				         	        		ph[phcounter] = "";
				     	    			 
				     	    			 if(phType[phcounter] == null)
				     	    				phType[phcounter] = "";
				     	    			 
				         	        	phcounter++;
				         	        } 
				         	        
				         	        cDetails.setPhNumber( ph );
				         	        cDetails.setPhType( phType );
				         	        pCur.close();
				    	        }
			    	    	}
			    	    	else if(strType.equalsIgnoreCase( "Email" ) )
			    	    	{
				    	        emcounter = 0;
				     	        Cursor emailCur = cr.query(EURI, null, EID + " = ?",  new String[]{id}, null); 
			     	    		
				     	        while (emailCur.moveToNext() && (emcounter <4)) 
			     	    		{ 
			     	    			 em[emcounter] 		=  emailCur.getString( emailCur.getColumnIndex( EMAIL ) );
			     	    			 emType[emcounter] 	=  emailCur.getString( emailCur.getColumnIndex( EMAILTYPE ) );
			     	    			 
			     	    			 if(em[emcounter] == null)
			     	    				 em[emcounter] = "";
			     	    			 
			     	    			 if(emType[emcounter] == null)
			     	    				emType[emcounter] = "";
			     	    			 
			     	    	 	     emcounter ++;
			     	    	 	}
			     	    		
				     	        cDetails.setEMail( em );
				     	    	cDetails.setEMailType( emType );
				 	    	 	emailCur.close();
			    	    	}
			 	    	 	 
			    	    	//if(bFound){
			    	    		list.add(cDetails);
			    	    	//	break;
			    	    	//}
			    	     }
		    	    }
		    	    cu.close();
		     	}
	        } catch (Error e) {
	        	Toast.makeText(vr.getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
	        }
			return list;
	    }
	
	private void callContact(ContactDetails contactDetails, String strType){
    	
    	name  	= contactDetails.getName();
    	ph 		= contactDetails.getPhNumber();
    	phType 	= contactDetails.getPhType();
    	em 		= contactDetails.getEMail();
    	emType 	= contactDetails.getEMailType();
    	
    	if(name.length()>0 && bFound)
        	vMsgContactName = name;
        	
    	if(ph != null)
    		if(ph[0] != null){
    			vMsgContactNumber = ph[0];
    			vMsgContactNumberType = getPhoneType(phType[0]);
    			
    			//Toast.makeText(vr.getActivity(),vMsgContactNumber +  "  &&&  " + vMsgContactNumberType, Toast.LENGTH_SHORT).show();
    		}
    	
    	if(em != null)
    		if(em[0] != null){
    			vMsgContactEmail = em[0];
    			vMsgContactEmailType = getEmailType(emType[0]);
    		}
    	
    	if(vMsgContactNumber.length() == 0){
    		Toast.makeText(vr.getActivity(), "Phone number not found", Toast.LENGTH_SHORT).show();
    		//vr.getActivity().finish();
    		return;
    	}
    	
    	if(vMsgText.equalsIgnoreCase("Calling")){
        	Boolean havingNumber = false;
        	
        	if(strType.equalsIgnoreCase("CallContactAtMobile")){
        		if(getContactNumber("mobile")){
        			havingNumber = true;
	        	}
        		else if(vMsgContactNumber.length()>0){
        			vMsgText = "I don't have mobile number of " + vMsgContactName + " but having " + vMsgContactNumberType + " phone number.";
        		}
        		else{
    	        	vMsgText = "I do not find any phone number of " + vMsgContactName;
    	        }
        	}
        	else if(strType.equalsIgnoreCase("CallContactAtHome")){
        		if(getContactNumber("home")){
        			havingNumber = true;
	        	}
        		else if(vMsgContactNumber.length()>0){
        			vMsgText = "I don't have home phone number of " + vMsgContactName + " but having " + vMsgContactNumberType + " phone number.";
        		}
        		else{
    	        	vMsgText = "I do not find any phone number of " + vMsgContactName;
    	        }
        	}
        	else if(strType.equalsIgnoreCase("CallContactAtWork")){
        		if(getContactNumber("work")){
        			havingNumber = true;
	        	}
        		else if(vMsgContactNumber.length()>0){
        			vMsgText = "I don't have work phone number of " + vMsgContactName + " but having " + vMsgContactNumberType + " phone number.";
        		}
        		else{
    	        	vMsgText = "I do not find any phone number of " + vMsgContactName;
    	        }
        	}
        	else if(strType.equalsIgnoreCase("CallContactAtOther")){
        		if(getContactNumber("other")){
        			havingNumber = true;
	        	}
        		else if(vMsgContactNumber.length()>0){
        			vMsgText = "I don't have other phone number of " + vMsgContactName + " but having " + vMsgContactNumberType + " phone number.";
        		}
        		else{
    	        	vMsgText = "I do not find any phone number of " + vMsgContactName;
    	        }
        	}
        	else if(strType.equalsIgnoreCase("CallContact")){
                if(getContactNumber(vMsgContactNumberType)){
	                havingNumber = true;
	            }
	            else if(vMsgContactNumber.length()>0){
	            	vMsgText = "I don't have other phone number of " + vMsgContactName + " but having " + vMsgContactNumberType + " phone number.";
	            	
	            }
	            else{
	                vMsgText = "I do not find any phone number of " + vMsgContactName;
	            }
	        }
        	else{
        		havingNumber = false;
        	}

//        	if(isNumeric(vMsgContactNumber))
//        		havingNumber = true;
        	
        	//Toast.makeText(vr.getActivity(), havingNumber+"", Toast.LENGTH_SHORT).show();
        	
        	if(!havingNumber){
//        		Intent mRead = new Intent(vr.getActivity(),VoiceResponse.class);
//            	mRead.putExtra("vMsgContactName", vMsgContactName);
//            	mRead.putExtra("vMsgContactNumber", vMsgContactNumber);
//            	mRead.putExtra("vMsgContactNumberType", vMsgContactNumberType);
//            	mRead.putExtra("vMsgContactEmail", vMsgContactEmail);
//            	mRead.putExtra("vMsgContactEmailType", vMsgContactEmailType);
//            	mRead.putExtra("vMsgContactAddress", vMsgContactAddress);
//            	mRead.putExtra("vMsgContactAddressType", vMsgContactAddressType);
//            	mRead.putExtra("vMsgContent", vMsgContent);
//            	mRead.putExtra("vMsgText", vMsgText);
//            	mRead.putExtra("vAction", "Calling");
//            	mRead.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            	vr.getActivity().startActivity(mRead);
//            	//vr.getActivity().finish();
            	
            	String strResponse = vAction +"^"+ vMsgContactName +"^"+ 
            						vMsgContactNumber +"^"+ vMsgContactNumberType +"^"+
            						vMsgContactEmail +"^"+ vMsgContactEmailType +"^"+
            						vMsgContactAddress +"^"+ vMsgContactAddressType +"^"+
            						vMsgContent +"^"+ vMsgText;
            	vr.dispatchStatusEventAsync("VOICE_CONVERSATION", strResponse);
        	}
        	else{
        		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + vMsgContactNumber));
            	vr.getActivity().startActivity(intent);
        	}
        	return;
        }    
    }
	
	private void GetContactFromPhoneBook(String strInput, String searchType){
        try{
	        ContentResolver cr = vr.getActivity().getContentResolver();
	        Cursor cu = cr.query(URI, null, null, null, null);//
	        
	        if (cu.getCount() > 0 && vMsgContactName.length()>0) {	
	        	// Loop over all contacts
	    	    while (cu.moveToNext()) { 
	    	    	bFound = false;
	    	    	// Initialize storage variables for the new contact
	            	
	            	id 		= cu.getString(cu.getColumnIndex(ID));
	    	        name 	= cu.getString(cu.getColumnIndex(DNAME));          
	    	        
	    	        if(vMsgContactName.equalsIgnoreCase(name)){
	    	        	bFound = true;
		    	       
	    	        	phcounter = 0;
	    	        	if(searchType.equals("number"))
	    	        	{
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
	    	        	}
	    	        	else{ //searchType == email
	    	        		emcounter = 0;
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
	    	        	}
	    	        	
		    	        if(bFound){
		    	        	break;
		    	        }
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
	        	
		        if(searchType.equals("number")){
		        	if(vAction.equalsIgnoreCase("SendTextMessageAtMobile")){
		        		if(getContactNumber("mobile")){
		        			vMsgText = "Sending message to " + vMsgContactName + " on mobile phone - " + vMsgContactNumber;
			        	}
		        		else if(vMsgContactNumber.length()>0){
		        			vMsgText = "I don't have mobile phone number of " + vMsgContactName + " but having " + vMsgContactNumberType + " phone number : " + vMsgContactNumber + ".";
		        		}
		        		else{
		    	        	vMsgText = "I do not find any phone number of " + vMsgContactName;
		    	        }
		        	}
		        	else if(vAction.equalsIgnoreCase("SendTextMessageAtHome")){
		        		if(getContactNumber("home")){
		        			vMsgText = "Sending message to " + vMsgContactName + " on home phone - " + vMsgContactNumber;
			        	}
		        		else if(vMsgContactNumber.length()>0){
		        			vMsgText = "I don't have home phone number of " + vMsgContactName + " but having " + vMsgContactNumberType + " phone number : " + vMsgContactNumber + ".";
		        		}
		        		else{
		    	        	vMsgText = "I do not find any phone number of " + vMsgContactName;
		    	        }
		        	}
		        	else if(vAction.equalsIgnoreCase("SendTextMessageAtWork")){
		        		if(getContactNumber("work")){
		        			vMsgText = "Sending message to " + vMsgContactName + " on work phone - " + vMsgContactNumber;
			        	}
		        		else if(vMsgContactNumber.length()>0){
		        			vMsgText = "I don't have work phone number of " + vMsgContactName + " but having " + vMsgContactNumberType + " phone number : " + vMsgContactNumber + ".";
		        		}
		        		else{
		    	        	vMsgText = "I do not find any phone number of " + vMsgContactName;
		    	        }
		        	}
		        	else if(vAction.equalsIgnoreCase("SendTextMessageAtOther")){
		        		if(getContactNumber("other")){
		        			vMsgText = "Sending message to " + vMsgContactName + " on other phone - " + vMsgContactNumber;
			        	}
		        		else if(vMsgContactNumber.length()>0){
		        			vMsgText = "I don't have other phone number of " + vMsgContactName + " but having " + vMsgContactNumberType + " phone number : " + vMsgContactNumber + ".";
		        		}
		        		else{
		    	        	vMsgText = "I do not find any phone number of " + vMsgContactName;
		    	        }
		        	}
		        	else
		        		vMsgText = "Sending message to " + vMsgContactName + " on phone - " + vMsgContactNumber;
		        	
		        	vAction = "SendTextMessage";
		        }
		        else  ///For Sending Email ////////////////////////////////////////////////
		        {
		        	if(vAction.equalsIgnoreCase("SendEmailAtHome")){
		        		if(getContactEmail("home")){
		        			vMsgText = "Sending email to " + vMsgContactName + " on home email address - " + vMsgContactEmail;
			        	}
		        		else if(vMsgContactEmail.length()>0){
		        			vMsgText = "I don't have home email address of " + vMsgContactName + " but having " + vMsgContactEmailType + " email address.";
		        		}
		        		else{
		    	        	vMsgText = "I do not find any email address of " + vMsgContactName;
		    	        }
		        	}
		        	else if(vAction.equalsIgnoreCase("SendEmailAtWork")){
		        		if(getContactEmail("work")){
		        			vMsgText = "Sending email to " + vMsgContactName + " on work email address - " + vMsgContactEmail;
			        	}
		        		else if(vMsgContactEmail.length()>0){
		        			vMsgText = "I don't have work email address of " + vMsgContactName + " but having " + vMsgContactEmailType + " email address.";
		        		}
		        		else{
		        			vMsgText = "I do not find any email address of " + vMsgContactName;
		    	        }
		        	}
		        	else if(vAction.equalsIgnoreCase("SendEmailAtOther")){
		        		if(getContactEmail("other")){
		        			vMsgText = "Sending email to " + vMsgContactName + " on other email address - " + vMsgContactEmail;
			        	}
		        		else if(vMsgContactEmail.length()>0){
		        			vMsgText = "I don't have other email address of " + vMsgContactName + " but having " + vMsgContactEmailType + " email address.";
		        		}
		        		else{
		        			vMsgText = "I do not find any email address of " + vMsgContactName;
		    	        }
		        	}
		        	else{
		        		if(vMsgContactEmail.length()>0 )
		        			vMsgText = "Sending email to " + vMsgContactName + " at " + vMsgContactEmail;
		        		else
		        			vMsgText = "I do not find any email address of " + vMsgContactName;
		        	}
		        		
		        	
		        	vAction = "SendEmail";
		        }
	        }
	        else{ // Contact not found
	        	if(searchType.equals("number")){
	        		vAction = "SendTextMessage";
	        		vMsgText = "Sending message to " + vMsgContactNumber;
	        		
	        		if(isNumeric(vMsgContactName)){
	        			vMsgContactNumber = vMsgContactName;
	        			vMsgText = "Sending message to " + vMsgContactName;
	        		}
	        		else{
	        			Toast.makeText(vr.getActivity(), "Phone number or contact not found", Toast.LENGTH_SHORT).show();
	        			return;
	        		}
	        	}
	        	else{
	        		vAction = "SendEmail";
	        		vMsgText = "Sending email to " + vMsgContactEmail;
	        		
	        		if(vMsgContactName.contains("@") && vMsgContactName.contains(".")){// (vMsgContactName)){
	        			vMsgContactEmail = vMsgContactName;
	        			vMsgText = "Sending email to " + vMsgContactName;
	        		}
	        		else{
	        			vMsgText = "Email address or contact not found";
	        		}
	        	}
	        }
	        
	        if((vMsgText.equals("I do not find any phone number of " + vMsgContactName)) && (isNumeric(vMsgContactNumber)))
	        	vMsgText = "Sending message to " + vMsgContactNumber;
	        
//	        Intent mRead = new Intent(vr.getActivity(),VoiceResponse.class);
//        	mRead.putExtra("vMsgContactName", 		vMsgContactName);
//        	mRead.putExtra("vMsgContactNumber", 	vMsgContactNumber);
//        	mRead.putExtra("vMsgContactNumberType", vMsgContactNumberType);
//        	mRead.putExtra("vMsgContactEmail", 		vMsgContactEmail);
//        	mRead.putExtra("vMsgContactEmailType", 	vMsgContactEmailType);
//        	mRead.putExtra("vMsgContactAddress", 	vMsgContactAddress);
//        	mRead.putExtra("vMsgContactAddressType",vMsgContactAddressType);
//        	mRead.putExtra("vMsgContent", vMsgContent);
//        	mRead.putExtra("vMsgText", vMsgText);
//        	mRead.putExtra("vAction", vAction);
//        	
//        	mRead.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        	vr.getActivity().startActivity(mRead);
        	
        	String strResponse = vAction +"^"+ vMsgContactName +"^"+ 
					vMsgContactNumber +"^"+ vMsgContactNumberType +"^"+
					vMsgContactEmail +"^"+ vMsgContactEmailType +"^"+
					vMsgContactAddress +"^"+ vMsgContactAddressType +"^"+
					vMsgContent +"^"+ vMsgText;
        	vr.dispatchStatusEventAsync("VOICE_CONVERSATION", strResponse);
	        
        } catch (Error e) {
			e.printStackTrace();
        }
    }
    
	
	private void GetAddressDetails(Activity act,String strType){
        try{
        	vMsgContactNumber 	= "";
        	vMsgContactEmail	= "";
        	
	        ContentResolver cr = act.getContentResolver();
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
		    	        	Toast.makeText(act, e.getMessage(), Toast.LENGTH_SHORT).show();
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
		    	        	Toast.makeText(act, e.getMessage(), Toast.LENGTH_SHORT).show();
		    	        }
		 	    	 	 
		 	    		addcounter = 0;
		 	    		
		 	    		try{
			    	    	Cursor addCur = cr.query(AURI, null, AID + " = ?",  new String[]{id}, null); 
		     	    		 while (addCur.moveToNext() && (addcounter <4)) { 
		     	    		    street 		= addCur.getString(addCur.getColumnIndex(STREET));
		     	    		    city 		= addCur.getString(addCur.getColumnIndex(CITY));
		     	    		    state 		= addCur.getString(addCur.getColumnIndex(STATE));
		     	    		    postcode 	= addCur.getString(addCur.getColumnIndex(POSTCODE));
		     	    		    country 	= addCur.getString(addCur.getColumnIndex(COUNTRY));
		     	    		    
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

		     	    		    vAddress = "";
		     		        	
		     		        	if(street.length()>0)
		     		        		vAddress =  street;
		     		        	
		     		        	if(city.length()>0)
		     		        		vAddress += ", " + city;
		     		        	
		     		        	if(state.length()>0)
		     		        		vAddress += ", " + state;
		     		        	
		     		        	if(postcode.length()>0)
		     		        		vAddress += ", postcode " + postcode;
		     		        		
		     		        	if(country.length()>0)
		     		        		vAddress += ", " + country;
		     		        	
		     		        	add[addcounter] = vAddress;
		     		        	addType[addcounter] = addCur.getString(addCur.getColumnIndex(ADDTYPE));
		     		        	
		     		        	if(add[addcounter] == null)
		     		        		add[addcounter] = "";
		     	    			 
		     	    			 if(addType[addcounter] == null)
		     	    				addType[addcounter] = "";
		     		        	
		     		        	addcounter ++;
		     	    	 	 } 
		     	    		 addCur.close();
		 	    		} catch (Error e) {
		    	        	Toast.makeText(act, e.getMessage(), Toast.LENGTH_SHORT).show();
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
	        	
	        	if(add[0] != null){
	        		vMsgContactAddress = add[0];
	        		vMsgContactAddressType = getAddressType(addType[0]);
	        	}
	        	
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
		        			vMsgText += ", and email address is " + vMsgContactEmail;
		        		}
		        		else{
		        			vMsgText += "The email address of " + vMsgContactName + " is " + vMsgContactEmail;
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
	        			vMsgText = "The postal address of " + vMsgContactName + " is " + vMsgContactAddress;
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
		    	        	vMsgText = "I do not find any address details of " + vMsgContactName;
		    	        }
	    	        }
	        	}
	        	else if(strType.equalsIgnoreCase("PhoneBookHomeAddressSearch")){
	        		if(getContactAddress("home")){
	        			vMsgText = "The home postal address of " + vMsgContactName + " is " + vMsgContactAddress;
		        	}
	        		else if(vMsgContactAddress.length()>0){
	        			vMsgText = "I don't have home postal address of " + vMsgContactName + " but having " + vMsgContactAddressType + " postal address.";
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
		    	        	vMsgText = "I do not find any address details of " + vMsgContactName;
		    	        }
	    	        }
	        	}
	        	else if(strType.equalsIgnoreCase("PhoneBookWorkAddressSearch")){
	        		if(getContactAddress("work")){
	        			vMsgText = "The work postal address of " + vMsgContactName + " is " + vMsgContactAddress;
		        	}
	        		else if(vMsgContactAddress.length()>0){
	        			vMsgText = "I don't have work postal address of " + vMsgContactName + " but having " + vMsgContactAddressType + " postal address.";
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
		    	        	vMsgText = "I do not find any address details of " + vMsgContactName;
		    	        }
	        			
	    	        }
	        	}
	        	else if(strType.equalsIgnoreCase("PhoneBookOtherAddressSearch")){
	        		if(getContactAddress("other")){
	        			vMsgText = "The other postal address of " + vMsgContactName + " is " + vMsgContactAddress;
		        	}
	        		else if(vMsgContactAddress.length()>0){
	        			vMsgText = "I don't have other postal address of " + vMsgContactName + " but having " + vMsgContactAddressType + " postal address.";
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
		    	        	vMsgText = "I do not find any address details of " + vMsgContactName;
		    	        }
	        			
	    	        }
	        	}
	        }
	        else{
	        	vMsgText = "I do not find any contact information of " + vMsgContactName;
	        }
	        
	        try {
//				Intent mRead = new Intent(act,VoiceResponse.class);
//				mRead.putExtra("vMsgContactName", vMsgContactName);
//				mRead.putExtra("vMsgContactNumber", vMsgContactNumber);
//				mRead.putExtra("vMsgContactNumberType", vMsgContactNumberType);
//				mRead.putExtra("vMsgContactEmail", vMsgContactEmail);
//				mRead.putExtra("vMsgContactEmailType", vMsgContactEmailType);
//				mRead.putExtra("vMsgContactAddress", vMsgContactAddress);
//				mRead.putExtra("vMsgContactAddressType", vMsgContactAddressType);
//				mRead.putExtra("vMsgContent", vMsgContent);
//				mRead.putExtra("vMsgText", vMsgText);
//				mRead.putExtra("vAction", vAction);
//				mRead.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				act.startActivity(mRead);
				
				String strResponse = vAction +"^"+ vMsgContactName +"^"+ 
						vMsgContactNumber +"^"+ vMsgContactNumberType +"^"+
						vMsgContactEmail +"^"+ vMsgContactEmailType +"^"+
						vMsgContactAddress +"^"+ vMsgContactAddressType +"^"+
						vMsgContent +"^"+ vMsgText;
				vr.dispatchStatusEventAsync("VOICE_CONVERSATION", strResponse);
			} 
	        catch (IllegalStateException e) {
				Toast.makeText(act,"Error : " + e.toString(), Toast.LENGTH_SHORT).show();
			}
        	
	        // Flush the PrintWriter to ensure everything pending is output before closing    
        } catch (Error e) {
        	Toast.makeText(act, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
	
	 private Boolean getContactEmail(String strType){
	    	emcounter = 0;
	    	while (emcounter < 4) { 
	    		if(em[emcounter] != null){
		    		if(getEmailType(emType[emcounter]).equalsIgnoreCase(strType)){
		    			vMsgContactEmail = em[emcounter];
		    			return true;
		    		}
	    		}
		 	    emcounter ++;
		 	 } 
	    	
	    	return false;
	    }
	    
	private Boolean getContactAddress(String strType){
		addcounter = 0;
	    while (addcounter <4) { 
	    	if(add[addcounter] != null){
		    	if(getAddressType(addType[addcounter]).equalsIgnoreCase(strType)){
		    		vMsgContactAddress = add[addcounter];
		    		return true;
		    	}
	    	}
	    	addcounter ++;
	 	} 
		return false;
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
	
	/** Method to return label corresponding to phone type code. Data for correspondence from
	   http://developer.android.com/reference/android/provider/ContactsContract.
	   CommonDataKinds.Phone.html  */
   
	 private String getPhoneType(String index){
		 index = index.toLowerCase();
	 
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
 	 } else {
 		 return "?";
 	 }
 }
    

private String getAddressType(String index){
 if(index.trim().equals( "1")){
	 return "home";
 } else if (index.trim().equals("2")){
	 return "work";
 } else if (index.trim().equals("3")){
	 return "other";
 } else {
	 return "?";
 }
}

 public static boolean isNumeric(String str)  
 {  
	try  
    {  
		double d = Double.parseDouble(str);  
    }  
	catch(NumberFormatException nfe)  
	{  
		return false;  
	}  
	return true;  
 }
 
public void sayMessage(String str){
 	vMsgText = str;
 	
	Intent mRead = new Intent(vr.getActivity(),VoiceResponse.class);
   	mRead.putExtra("vMsgContactName",        vMsgContactName);
   	mRead.putExtra("vMsgContactNumber",      vMsgContactNumber);
   	mRead.putExtra("vMsgContactNumberType",  vMsgContactNumberType);
   	mRead.putExtra("vMsgContactEmail",       vMsgContactEmail);
   	mRead.putExtra("vMsgContactEmailType",   vMsgContactEmailType);
   	mRead.putExtra("vMsgContactAddress",     vMsgContactAddress);
   	mRead.putExtra("vMsgContactAddressType", vMsgContactAddressType);
   	mRead.putExtra("vMsgContent", vMsgText);
   	mRead.putExtra("vMsgText", vMsgText);
   	mRead.putExtra("vAction", "Calling");
   	mRead.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
   	vr.getActivity().startActivity(mRead);
}
 
 
private void sendSMS(String phoneNumber, String message)
{      
	String SENT = "SMS_SENT";
	String DELIVERED = "SMS_DELIVERED";
	
    PendingIntent sentPI = PendingIntent.getBroadcast(vr.getActivity(), 0,
        new Intent(SENT), 0);
    
    PendingIntent deliveredPI = PendingIntent.getBroadcast(vr.getActivity(), 0,
        new Intent(DELIVERED), 0);
	
    //---when the SMS has been sent---
    vr.getActivity().registerReceiver(new BroadcastReceiver(){
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			switch (getResultCode())
			{
			    case Activity.RESULT_OK:
				    Toast.makeText(vr.getActivity().getBaseContext(), "SMS sent", 
				    		Toast.LENGTH_SHORT).show();
				    break;
			    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
				    Toast.makeText(vr.getActivity().getBaseContext(), "Generic failure", 
				    		Toast.LENGTH_SHORT).show();
				    break;
			    case SmsManager.RESULT_ERROR_NO_SERVICE:
				    Toast.makeText(vr.getActivity().getBaseContext(), "No service", 
				    		Toast.LENGTH_SHORT).show();
				    break;
			    case SmsManager.RESULT_ERROR_NULL_PDU:
				    Toast.makeText(vr.getActivity().getBaseContext(), "Null PDU", 
				    		Toast.LENGTH_SHORT).show();
				    break;
			    case SmsManager.RESULT_ERROR_RADIO_OFF:
				    Toast.makeText(vr.getActivity().getBaseContext(), "Radio off", 
				    		Toast.LENGTH_SHORT).show();
				    break;
			}
		}
    }, new IntentFilter(SENT));
    
    //---when the SMS has been delivered---
    vr.getActivity().registerReceiver(new BroadcastReceiver(){
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			switch (getResultCode())
			{
			    case Activity.RESULT_OK:
				    Toast.makeText(vr.getActivity().getBaseContext(), "SMS delivered", 
				    		Toast.LENGTH_SHORT).show();
				    break;
			    case Activity.RESULT_CANCELED:
				    Toast.makeText(vr.getActivity().getBaseContext(), "SMS not delivered", 
				    		Toast.LENGTH_SHORT).show();
				    break;					    
			}
		}
    }, new IntentFilter(DELIVERED));        
	
    SmsManager sms = SmsManager.getDefault();
    sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);               
}
}
