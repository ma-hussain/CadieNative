package com.zahdoo.android.extension.voiceinput;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.zahdoo.android.extension.alarm.Global;

public class VoiceResponse extends Activity implements OnInitListener, TextToSpeech.OnUtteranceCompletedListener {
    
    private int MY_DATA_CHECK_CODE = 0;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    
    //private TextToSpeech tts;
    
    private String vMsgContactName = "";
	private String vMsgContactNumber = "";
	private String vMsgContactNumberType = "";
	private String vMsgContactEmail = "";
	private String vMsgContactEmailType = "";
	private String vMsgContactAddress = "";
	private String vMsgContactAddressType = "";
    private String vMsgContent = "";
    private String vMsgText = "";
    private String vAction = "";
    
    private String msgContent = "";
    private String text = "";
    private String vResponseMsg = "";
    private String vPromptMsg = "";
    private String vMsgReply = "";
    
    private String sMailSubject = "";
    private String sMailMessage = "";
    
    private int iReply = 0;//0 = msg not read, 1 = msg read, 2 = reply
    
    HashMap<String, String> myHashAlarm = new HashMap<String, String>();
    
    Intent checkIntent = new Intent();
    Intent vIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	 super.onConfigurationChanged(newConfig);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	Bundle extras = getIntent().getExtras(); 
        
        if(extras !=null){
        	vMsgContactName 		= extras.getString("vMsgContactName");
        	vMsgContactNumber 		= extras.getString("vMsgContactNumber");
        	vMsgContactNumberType 	= extras.getString("vMsgContactNumberType");
        	vMsgContactEmail 		= extras.getString("vMsgContactEmail");
        	vMsgContactEmailType 	= extras.getString("vMsgContactEmailType");
        	vMsgContactAddress 		= extras.getString("vMsgContactAddress");
        	vMsgContactAddressType 	= extras.getString("vMsgContactAddressType");
        	vMsgContent 			= extras.getString("vMsgContent");
        	vMsgText 				= extras.getString("vMsgText");
        	vAction 				= extras.getString("vAction");
        }
        
        if(vMsgText.contains("but having"))
       		Toast.makeText(VoiceResponseFunction.vr.getActivity(), vMsgText, Toast.LENGTH_SHORT).show();
        
        if(vAction.equalsIgnoreCase("ReadMessage")){
        	vMsgText = "You have received a text message from " + vMsgContactName;// + ". Message is, " + vMsgText;
        	vPromptMsg = "Would you like to read?";
        }
        else if(vAction.contains("PhoneBook")){
        	vPromptMsg = "";
        	if(!vMsgText.contains("not find any"))
        		vPromptMsg = "Would you like me to read again?";
        	
        	if(vMsgText.contains("but having"))
        		vPromptMsg = "Do you want me to read that?";
        }
        else if(vAction.contains("SendTextMessage")){
        	msgContent = vMsgContent;
        	vPromptMsg = "Would you like to send?";
        	vMsgText += ". Message is - "+ vMsgContent;
        	
        	if(vMsgContactName.length() == 0){
        		vMsgText = "Sorry, I am unable to understand to whom you want to send message. Please try again.";
        		vPromptMsg = "";
        	}
        		
        	if(!isNumeric(vMsgContactNumber)){
	        	if(vMsgText.contains("I don't have"))
	        		vPromptMsg = "Want to text on this number?";
	        	
	        	if((vMsgText.equalsIgnoreCase("I do not find any phone number of " + vMsgContactName)) ||
	        			vMsgText.contains("I do not find any")){
	        		vPromptMsg = "";
	        	}
        	}
        }
        else if(vAction.equalsIgnoreCase("SendEmail")){
        	msgContent = vMsgContent;
        	vPromptMsg = "What is the subject?";
        	
        	if(vMsgText.contains("not found") ||
        			vMsgText.contains("I do not find any")){
        		vPromptMsg = "";
        	}
        	else {
        		if(vMsgContactName.length() == 0){
	        		vMsgText = "Sorry, I am unable to understand to whom you want to email. Please try again.";
	        		vPromptMsg = "";
	        	}
	        	
	        	if(vMsgText.contains("but having"))
	        		vPromptMsg = "Want to send mail on this email address? " + vMsgContactEmail;
        	}
        }
        else if(vAction.equalsIgnoreCase("Calling")){
        	vPromptMsg = "";
        	
        	if(vMsgContactName.length() == 0 && vMsgContactNumber.length() == 0){
        		vMsgText = "Sorry, I am unable to understand to whom you want to call. Please try again.";
        		vPromptMsg = "";
        	}
        	
    		if(vMsgText.contains("I don't have")){
    			vPromptMsg = "Want me to call on this number?";
    		}
        }
        else{
        	if(!vAction.equalsIgnoreCase("Error")){
	        	vMsgText = vMsgContent;
	        	vPromptMsg = "Want me to read again?";
	        	
	        	if(vMsgText.contains("I don't have"))
	        		vPromptMsg = "Do you want that?";
	        		
	        	if((vMsgText.equalsIgnoreCase("I do not find any contact information of " + vMsgContactName)) ||
	        			vMsgText.contains("I do not find any")){
	        		vPromptMsg = "";
	        	}
        	}
        }
        
        try {
        	Global.tts = new TextToSpeech(this, this);
			   
			checkIntent = new Intent();
			checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
			startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
		} 
        catch (IllegalStateException e) {
			Toast.makeText(VoiceResponseFunction.vr.getActivity(), "Create Error : " + e.toString(), Toast.LENGTH_SHORT).show();
		}
    }
    
    private void speakNow(){
    	text = vMsgText;
		
		if (text!=null && text.length()>0) {
	    	Toast.makeText(VoiceResponseFunction.vr.getActivity(), vMsgText, Toast.LENGTH_SHORT).show();
	    	
	    	if(text.equalsIgnoreCase("invalid voice command") 
	    			|| text.toLowerCase().indexOf("error")>-1 
	    			|| text.toLowerCase().indexOf("invalid")>-1){}
	    	else{
	    		if(!vPromptMsg.equals(vMsgText))
	    			text += ". "+ vPromptMsg;//Would you like to create it?";
	    	}
	    	
	    	Global.tts.speak(text, TextToSpeech.QUEUE_ADD, myHashAlarm);
	    	//Toast.makeText(VoiceResponseFunction.vr.getActivity(), text, Toast.LENGTH_SHORT).show();
	    	text = "";
	    }
    }
    
    public static boolean isNumeric(String str)  
    {  
    	double d;
      try{  
        d = Double.parseDouble(str);  
      }  
      catch(NumberFormatException nfe){  
    	  d = 0;
        return Boolean.parseBoolean(d+"");  
      }  
      return true;  
    }
    
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
            	// success, create the TTS instance
            	Global.tts = new TextToSpeech(this,this);
            } 
            else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
        else if (requestCode == VOICE_RECOGNITION_REQUEST_CODE){
        	if(resultCode == RESULT_OK) {
        		// Fill the list view with the strings the recognizer thought it could have heard
	            ArrayList<String> matches1 = data.getStringArrayListExtra(
	                    RecognizerIntent.EXTRA_RESULTS);
	            Object result[] 	= matches1.toArray();
	            vResponseMsg 		= result[0].toString();
	            text = "";
	            
	            if((vResponseMsg.equalsIgnoreCase("yes") || vResponseMsg.equalsIgnoreCase("yeah") ||
	            		vResponseMsg.equalsIgnoreCase("ok") || vResponseMsg.equalsIgnoreCase("okay")))
	            {
	                if(vAction.contains("PhoneBook") && (vPromptMsg.length()>0) ){
	                	if(vMsgText.contains("but having")){
	                		//vMsgText = "";
	                		
	                		if((vMsgContactAddress.length() > 0) && !vMsgText.contains("mail") && !vMsgText.contains("number")&& vMsgText.contains("postal address")){
	                			vMsgText = "The " + vMsgContactAddressType + " postal address of " + vMsgContactName + " is " + vMsgContactAddress;	                			
	                		}
	                		else if((vMsgContactNumber.length()>0) && (!vMsgText.contains("mail") || !vMsgText.contains("email")))
	                		{
	                			vMsgText = "The phone number of " + vMsgContactName + " is " + vMsgContactNumber;
	                		}
	                		else if((vMsgContactEmail.length()>0) && !vMsgText.contains("number"))
	                		{
	                			vMsgText = "The email address of " + vMsgContactName + " is " + vMsgContactEmail;
	                		}
	                		else if((vMsgContactNumber.length()>0) && (vMsgContactEmail.length()>0)){
	                			vMsgText = "The phone number of " + vMsgContactName + " is " + vMsgContactNumber;
	                			vMsgText += ", and the email address of " + vMsgContactName + " is " + vMsgContactEmail;
	                		}
	                		
	                		vPromptMsg = "Would you like to read again?";
	                	}
	                	
	            		finishActivity(VOICE_RECOGNITION_REQUEST_CODE);
		            	startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
	            	}
	            	else if(vAction.equalsIgnoreCase("ReadMessage")){
		            	if(iReply == 0){ /// Reading your message here
			            	iReply = 1;
			            	vPromptMsg = "Would you like to reply?";
			            	vMsgText = vMsgContent;
			            	finishActivity(VOICE_RECOGNITION_REQUEST_CODE);
			            	startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
			            }
			            else if(iReply == 1){
			            	iReply = 2;
			            	vMsgText = "Please say your message now...";
			            	finishActivity(VOICE_RECOGNITION_REQUEST_CODE);
			            	startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
			            }
			            else if(iReply == 3){
			            	iReply = 0;
			            	sendSMS(vMsgContactNumber,vMsgReply);
			            	Toast.makeText(VoiceResponseFunction.vr.getActivity(), "Sending message ... ", Toast.LENGTH_SHORT).show();
			            	vMsgText = "Your message sent";
			            	finishActivity(VOICE_RECOGNITION_REQUEST_CODE);
			            	//onDestroy();
			            }
	                }
	                else if(vAction.equalsIgnoreCase("SendTextMessage")){
	                	iReply = 0;
	                	
		            	sendSMS(vMsgContactNumber,msgContent);
		            	Toast.makeText(VoiceResponseFunction.vr.getActivity(), "Sending message ... ", Toast.LENGTH_SHORT).show();
		            	vMsgText = "Your message sent";
		            	finishActivity(VOICE_RECOGNITION_REQUEST_CODE);
		            	
		            	//onDestroy();
	                }
	                else if(vAction.equalsIgnoreCase("Calling")){
	                	iReply = 0;
	                	Toast.makeText(VoiceResponseFunction.vr.getActivity(), "Calling " + vMsgContactName + " at " + vMsgContactNumberType, Toast.LENGTH_SHORT).show();
	                	finishActivity(VOICE_RECOGNITION_REQUEST_CODE);
	                	
	                	Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + vMsgContactNumber));
	                    startActivity(intent);
	                    finish();
			        	
		            	//onDestroy();
	                }
	                else if(vAction.equalsIgnoreCase("SendEmail")){
	                	Toast.makeText(VoiceResponseFunction.vr.getActivity(), "Sending mail to " + vMsgContactName + " at " + vMsgContactEmail, Toast.LENGTH_SHORT).show();
	                	if(vPromptMsg.contains("Want to send mail on this email address?")){
	                		vPromptMsg = "What is the subject?";
	                		vMsgText = vPromptMsg;
	                		finishActivity(VOICE_RECOGNITION_REQUEST_CODE);
			            	startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
	                	}
	                	else{
	                		finishActivity(VOICE_RECOGNITION_REQUEST_CODE);
	                		sMailMessage = vResponseMsg;
	                		Toast.makeText(VoiceResponseFunction.vr.getActivity(), "Sending mail to " + vMsgContactName + " at " + vMsgContactEmail, Toast.LENGTH_SHORT).show();
	        	        	
	        	        	Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
	        	        	String[] recipients = new String[]{vMsgContactEmail};
	        	        	emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
	        	        	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, sMailSubject);
	        	        	emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, sMailMessage);
	        	        	emailIntent.setType("text/plain");
	        	        	startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	        	        	finish();
	                	}
	                }
	                else
	                {
	                	iReply = 1;
	                		                	
	                	if(vPromptMsg.equalsIgnoreCase("Do you want that?")){
	                		if(vMsgText.contains("mobile number") ||
	                				vMsgText.contains("home phone") ||
	                				vMsgText.contains("work phone") ||
	                				vMsgText.contains("other phone")){
	                			vMsgText = vMsgContactNumberType + " phone number is " + vMsgContactNumber;
	                			vPromptMsg = "";
	                		}
	                		else if(vMsgText.contains("email address")){
	                			if(vMsgText.contains("home email") ||
		                				vMsgText.contains("work email") ||
		                				vMsgText.contains("other email")){
		                			vMsgText = vMsgContactEmailType + " email address is " + vMsgContactEmail;
		                			vPromptMsg = "";
		                		}
	                		}
	                		else if(vMsgText.contains("address")){
	                			vMsgText ="";
	                			
	                			if(vMsgContactNumber.length()>0){
	                				vMsgText = vMsgContactNumberType + " phone number is " + vMsgContactNumber;
	                			}
	                			
	                			if(vMsgContactEmail.length()>0){
	                				vMsgText += vMsgContactEmailType + " email address is " + vMsgContactEmail;
	                			}
	                		}
	                	}
	                	else
	                		vMsgText = vMsgContent;
	                	
		            	finishActivity(VOICE_RECOGNITION_REQUEST_CODE);
		            	startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
	                }
	            }
	            else if(vPromptMsg.equals("What is the subject?")){
	            	vPromptMsg = "What is the message of this mail?";
	            	vMsgText = vPromptMsg;
            		sMailSubject = vResponseMsg;
            		finishActivity(VOICE_RECOGNITION_REQUEST_CODE);
	            	startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
            	}
	            else if(vPromptMsg.equals("What is the message of this mail?")){	            	
	            	sMailMessage = vResponseMsg;
	            	finishActivity(VOICE_RECOGNITION_REQUEST_CODE);
            		
            		Toast.makeText(VoiceResponseFunction.vr.getActivity(), "Sending mail to " + vMsgContactName + " at " + vMsgContactEmail, Toast.LENGTH_SHORT).show();
    	        	
    	        	Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
    	        	String[] recipients = new String[]{vMsgContactEmail};
    	        	emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
    	        	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, sMailSubject);
    	        	emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, sMailMessage);
    	        	emailIntent.setType("text/plain");
    	        	startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    	        	finish();
            	}
		        else if (iReply == 2){
	            	iReply = 3;
	            	vMsgReply = vResponseMsg;
	            	vMsgText = "You said : "+ vResponseMsg;
	            	
	            	vPromptMsg = "Do you want to send this message as reply to " + vMsgContactName ;
	            	finishActivity(VOICE_RECOGNITION_REQUEST_CODE);
		            startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
	            }
		        else{
	            	vMsgText = "You said : "+ vResponseMsg;
	            	Toast.makeText(VoiceResponseFunction.vr.getActivity(), vMsgText, Toast.LENGTH_SHORT).show();
	            	onDestroy();
	            }
        	} 
        	else{
        		Toast.makeText(VoiceResponseFunction.vr.getActivity(), "Invalid response... Please try again", Toast.LENGTH_SHORT).show();
        		onDestroy();
        	}

        	super.onActivityResult(requestCode, resultCode, data);
        }
    }
	
    @Override
    public void onInit(int status) {  
        try {
			myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_NOTIFICATION));
			myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "test");
    
			if (status == TextToSpeech.SUCCESS) {
				Global.tts.setOnUtteranceCompletedListener(this);
				if(!Global.tts.isSpeaking())
					speakNow();
			}
			else if (status == TextToSpeech.ERROR) {
			    Toast.makeText(VoiceResponseFunction.vr.getActivity(), 
			            "Error occurred while initializing Text-To-Speech engine", Toast.LENGTH_SHORT).show();
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
    }
    
    
    @Override
    public void onUtteranceCompleted(String utteranceId) {
    	if(vResponseMsg.equalsIgnoreCase("invalid voice command")){
    		finishActivity(MY_DATA_CHECK_CODE);
    		onDestroy();
    	}
    	else{
    		finishActivity(MY_DATA_CHECK_CODE);
    		
    		if ((vPromptMsg.length()==0) || (iReply == 1) && 
    				vAction.equalsIgnoreCase("Error") &&
    				( vAction.equalsIgnoreCase("PhoneBookAddressSearch") ||
    				  vAction.equalsIgnoreCase("PhoneBookHomeAddressSearch") ||
    				  vAction.equalsIgnoreCase("PhoneBookWorkAddressSearch") ||
    				  vAction.equalsIgnoreCase("PhoneBookOtherAddressSearch") ) ){
    			onDestroy();
    		}
    		else{
    			 if (Build.VERSION.SDK_INT <= 15)//Build.VERSION_CODES.JELLY_BEAN)
    		     {
	        		vIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	        		vIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
	    	                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	        		vIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "CADIE Test Message reply\nReply could be like 'Yes', 'No', 'Ok', 'Cancel'");
	    	        startActivityForResult(vIntent, VOICE_RECOGNITION_REQUEST_CODE);
    		     }
    			 else{
    				 Toast.makeText(this, "\n\nThis feature is not available in Android Jelly Bean.'\n\n", Toast.LENGTH_SHORT).show();
    			     finish();
    			 }
    		}
    	}
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Global.tts.shutdown();
        finish();
    }
    
    private void sendSMS(String phoneNumber, String message)
    {      
    	String SENT = "SMS_SENT";
    	String DELIVERED = "SMS_DELIVERED";
    	
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
            new Intent(SENT), 0);
        
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
            new Intent(DELIVERED), 0);
    	
        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver(){
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode())
				{
				    case Activity.RESULT_OK:
					    Toast.makeText(getBaseContext(), "SMS sent", 
					    		Toast.LENGTH_SHORT).show();
					    break;
				    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
					    Toast.makeText(getBaseContext(), "Generic failure", 
					    		Toast.LENGTH_SHORT).show();
					    break;
				    case SmsManager.RESULT_ERROR_NO_SERVICE:
					    Toast.makeText(getBaseContext(), "No service", 
					    		Toast.LENGTH_SHORT).show();
					    break;
				    case SmsManager.RESULT_ERROR_NULL_PDU:
					    Toast.makeText(getBaseContext(), "Null PDU", 
					    		Toast.LENGTH_SHORT).show();
					    break;
				    case SmsManager.RESULT_ERROR_RADIO_OFF:
					    Toast.makeText(getBaseContext(), "Radio off", 
					    		Toast.LENGTH_SHORT).show();
					    break;
				}
			}
        }, new IntentFilter(SENT));
        
        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver(){
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				switch (getResultCode())
				{
				    case Activity.RESULT_OK:
					    Toast.makeText(getBaseContext(), "SMS delivered", 
					    		Toast.LENGTH_SHORT).show();
					    break;
				    case Activity.RESULT_CANCELED:
					    Toast.makeText(getBaseContext(), "SMS not delivered", 
					    		Toast.LENGTH_SHORT).show();
					    break;					    
				}
			}
        }, new IntentFilter(DELIVERED));        
    	
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);               
    }
}



