package com.zahdoo.android.extension.voiceinput;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.widget.Toast;

import com.zahdoo.android.extension.alarm.Global;

public class ReadMessage extends Activity implements OnInitListener, TextToSpeech.OnUtteranceCompletedListener {
    
    private int MY_DATA_CHECK_CODE = 0;
    
    //private TextToSpeech tts;
    private String vResponseMsg = "";
    //private String vDateInfo = "";
    //private String appStrArr[];
    
    private Boolean notifyApp = false;
    
    
    HashMap<String, String> myHashAlarm = new HashMap<String, String>();
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	 super.onConfigurationChanged(newConfig);
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
         
    	super.onCreate(savedInstanceState);
    	Bundle extras = getIntent().getExtras(); 
         
        if(extras !=null){
        	vResponseMsg = extras.getString("vResponseMsg");
        	//vDateInfo = extras.getString("vDate");
        }
    	   
    	try {
    		Global.tts = new TextToSpeech(this, this);
			
			Intent checkIntent = new Intent();
			checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
			startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
		} catch (Exception e) {
			//DownloadOnSDcard();
		}
    }
 
    private void speakNow(){
    	
    	 String text = vResponseMsg;
//    	 if (text.equalsIgnoreCase("AppName")) 
//    	 {
//    		 String appStr = "Hi, I am Cadie, Your Personal Assistant..";
//    		 Global.tts.speak(appStr, TextToSpeech.QUEUE_ADD, myHashAlarm);
//	     }
//    	 else if (text.equalsIgnoreCase("AboutApp")) 
//    	 {
//    		 Random rndm = new Random();
//    		 appStrArr = new String[10];
//    		 
//    		 //Toast.makeText(this, "Tap anywhere to stop", Toast.LENGTH_LONG).show();
//    		 
//    		 appStrArr[0] = "Hi this is Cadie, I can create appointments, Reminders, ToDo Lists and many more like, ";
//    		 appStrArr[0] += "I can do most of your tasks by your voice commands. ";
//    		 appStrArr[0] += "I can make phone calls, send text messages, and send emails to anyone from your phonebook. ";
//    		 appStrArr[0] += "I can create contexts and extract tags to help you to organize and find items related to certain tags or context. ";
//    		 appStrArr[0] += "I can get your current location and remind you for any work related to that location. ";
//    		 appStrArr[0] += "You can use me to create Voice Notes. ";
//    		 appStrArr[0] += "I can help you to create a notes having text, pictures, videos with appointments, reminders and todo lists, you can even attach an existing appointment, reminder or list to a note. " ;
//    		 appStrArr[0] += "You can ask me to show maps, route directions, give weather forecast of any place, ";
//    		 appStrArr[0] += "I can also assist you in doing any web search. ";
//    		 appStrArr[0] += "Thank you. ";
//    		 
//    		// Toast.makeText(this, "Tap anywhere to stop", Toast.LENGTH_LONG).show();
//    		 
//    		 appStrArr[1] = "Hello i am Cadie, I can create appointments, Reminders, ToDo Lists and many more like. ";
//    		 appStrArr[1] += "I can make phone calls, send text messages, and send emails to anyone from your phonebook. ";
//    		 appStrArr[1] += "I can do most of your tasks by your voice commands. ";
//    		 appStrArr[1] += "You can ask me to show maps, route directions, give weather forecast of any place, ";
//    		 appStrArr[1] += "I can also assist you in doing any web search. ";
//    		 appStrArr[1] += "I can create contexts and extract tags to help you to organize and find items related to certain tags or context. ";
//    		 appStrArr[1] += "I can get your current location and remind you for any work related to that location. ";
//    		 appStrArr[1] += "You can use me to create Voice Notes. ";
//    		 appStrArr[1] += "I can help you to create a notes having text, pictures, videos with appointments, reminders and todo lists, you can even attach an existing appointment, reminder or list to a note. " ;
//    		 appStrArr[1] += "Thank you. ";
//    		 
//    		 //Toast.makeText(this, "Tap anywhere to stop", Toast.LENGTH_LONG).show();
//    		 
//    		 appStrArr[2] = "Welcome to Cadie, I can create appointments, Reminders, ToDo Lists and many more like. ";
//    		 appStrArr[2] += "I can do most of your tasks by your voice commands. ";
//    		 appStrArr[2] += "I can get your current location and remind you for any work related to that location. ";
//    		 appStrArr[2] += "You can use me to create Voice Notes. ";
//    		 appStrArr[2] += "You can ask me to show maps, route directions, give weather forecast of any place, ";
//    		 appStrArr[2] += "I can help you to create a notes having text, pictures, videos with appointments, reminders and todo lists, you can even attach an existing appointment, reminder or list to a note. " ;
//    		 appStrArr[2] += "I can also assist you in doing any web search. ";
//    		 appStrArr[2] += "I can make phone calls, send text messages, and send emails to anyone from your phonebook. ";
//    		 appStrArr[2] += "I can create contexts and extract tags to help you to organize and find items related to certain tags or context. ";
//    		 appStrArr[2] += "Thank you. ";
//    		 
//    		 //Toast.makeText(this, "Tap anywhere to stop", Toast.LENGTH_LONG).show();
//    		 
//    		 appStrArr[3] = "I am Cadie, I can create appointments, Reminders, ToDo Lists and many more like. ";
//    		 appStrArr[3] += "I can do most of your tasks by your voice commands. ";
//    		 appStrArr[3] += "You can ask me to show maps, route directions, give weather forecast of any place, ";
//    		 appStrArr[3] += "I can get your current location and remind you for any work related to that location. ";
//    		 appStrArr[3] += "You can use me to create Voice Notes. ";
//    		 appStrArr[3] += "I can make phone calls, send text messages, and send emails to anyone from your phonebook. ";
//    		 appStrArr[3] += "I can create contexts and extract tags to help you to organize and find items related to certain tags or context. ";
//    		 appStrArr[3] += "I can help you to create a notes having text, pictures, videos with appointments, reminders and todo lists, you can even attach an existing appointment, reminder or list to a note. " ;
//    		 appStrArr[3] += "I can also assist you in doing any web search. ";
//    		 appStrArr[3] += "Thank you. ";
//    		 
//    		 //Toast.makeText(this, "Tap anywhere to stop", Toast.LENGTH_LONG).show();
//    		 Global.tts.speak(appStrArr[rndm.nextInt(4)], TextToSpeech.QUEUE_ADD, myHashAlarm);
//	    } 
//    	else if (text.equalsIgnoreCase("AboutTime")) {
//    		
//    		Calendar cal1 = new GregorianCalendar();
//    		String dtInfo = new String();
//    		String am_pm = "";
//    		String dayNames[] = new DateFormatSymbols().getWeekdays();
//    		String hr,min,sec =new String();
//    		
//    		if(vDateInfo.contains("what is the time now") 	|| 
//    		   vDateInfo.contains("what's the time now") 	||
//    		   vDateInfo.contains("whats the time now") 	||
//    		   vDateInfo.contains("what is the time") 		||
//    		   vDateInfo.contains("what's the time") 		||
//    		   vDateInfo.contains("whats the time") 		||
//    		   vDateInfo.contains("tell me the time") 		||
//    		   vDateInfo.contains("tell me the current time") ||
//    		   vDateInfo.contains("tell me current time") 	||
//    		   vDateInfo.contains("tell the current time") 	||
//    		   vDateInfo.contains("tell current time") 		||
//    		   vDateInfo.contains("tell the time")		 	||
//    		   vDateInfo.contains("show me the current time") ||
//    		   vDateInfo.contains("show me current time") 	||
//    		   vDateInfo.contains("show the current time") 	||
//    		   vDateInfo.contains("show me the time") 		||
//    		   vDateInfo.contains("show the time") 			||
//    		   vDateInfo.contains("get current time") 		||
//    		   vDateInfo.contains("get the current time") 	||
//    		   vDateInfo.contains("get the time") 			||
//    		   vDateInfo.contains("get me the time") 		||
//    		   vDateInfo.contains("get me current time") 	||
//    		   vDateInfo.contains("get me the current time") ){
//    			
//    				if(cal1.get(Calendar.AM_PM) == 0)
//    					am_pm = "AM";
//    				else
//    					am_pm = "PM";
//    				
//    				if(cal1.get(Calendar.HOUR)==0)
//    					hr = "12";
//    				else 
//    					hr = cal1.get(Calendar.HOUR) + "";
//    				
//    				if(cal1.get(Calendar.MINUTE)<10)
//    					min = "0" + cal1.get(Calendar.MINUTE);
//    				else
//    					min = cal1.get(Calendar.MINUTE) + "";
//    				
//    				if(cal1.get(Calendar.SECOND)<10)
//    					sec = "0" + cal1.get(Calendar.SECOND);
//    				else
//    					sec = cal1.get(Calendar.SECOND) + "";
//    					
//    				dtInfo = " The time is " + hr + ":" + min + ":" + sec +" " + am_pm;
//    			
//    		}
//    		else if((vDateInfo.contains("what is the date" )		||
//    				vDateInfo.contains("what's the date" )			||
//    				vDateInfo.contains("whats the date" )			||
//    				vDateInfo.contains("what is today's date" )		||
//    				vDateInfo.contains("what is todays date" )		||
//    				vDateInfo.contains("what's the date today" )	||
//    				vDateInfo.contains("whats the date today" )		||
//    				vDateInfo.contains("tell me today's date" )		||
//    				vDateInfo.contains("tell me todays date" )		||
//    				vDateInfo.contains("tell today's date" )		||
//    				vDateInfo.contains("tell todays date" )			||
//    				vDateInfo.contains("give me today's date" )		||
//    				vDateInfo.contains("give me todays date" )		||
//    				vDateInfo.contains("give today's date" )		||
//    				vDateInfo.contains("give todays date" )			||
//    				vDateInfo.contains("give me date" )				||
//    				vDateInfo.contains("give me the date" )			||
//    				vDateInfo.contains("get me today's date" )		||
//    				vDateInfo.contains("get me todays date" )		||
//    				vDateInfo.contains("get me the date" )			||
//    				vDateInfo.contains("get me date" )				||
//    				vDateInfo.contains("give the date" )			||
//    				vDateInfo.contains("tell me date" )				||
//    				vDateInfo.contains("tell the date" )			||
//    				vDateInfo.contains("tell me the date" )) && !vDateInfo.contains("tomorrow")){
//    			
//    				dtInfo = "Todays date is " + (cal1.get(Calendar.MONTH) + 1) + "/" + 
//    						 cal1.get(Calendar.DAY_OF_MONTH) + "/" +  cal1.get(Calendar.YEAR);
//    			
//    		}
//    		else if(vDateInfo.contains("what is the date tomorrow" )||
//    				vDateInfo.contains("what's the date tomorrow" ) ||
//    				vDateInfo.contains("what is tomorrow's date")	||
//    				vDateInfo.contains("what is tomorrows date")	||
//    				vDateInfo.contains("what will be tomorrows date")	||
//    				vDateInfo.contains("what date is tomorrow")		||
//    				vDateInfo.contains("tell me tomorrow's date")	||
//    				vDateInfo.contains("tell me tomorrows date")	||
//    				vDateInfo.contains("tell tomorrows date")		||
//    				vDateInfo.contains("tell tomorrow's date")		||
//    				vDateInfo.contains("tomorrow's date")			||
//    				vDateInfo.contains("tomorrow's date will be")	||
//    				vDateInfo.contains("get tomorrow's date")		||
//    				vDateInfo.contains("get tomorrows date")		||
//    				vDateInfo.contains("give me tomorrow's date")	||
//    				vDateInfo.contains("give me tomorrows date")	||
//    				vDateInfo.contains("give tomorrow's date")		||
//    				vDateInfo.contains("give tomorrows date")){
//    			
//    				dtInfo = "Tomorrows date is " + (cal1.get(Calendar.MONTH) + 1) + "/" + 
//    						 (cal1.get(Calendar.DAY_OF_MONTH) + 1) + "/" +  cal1.get(Calendar.YEAR);
//    			
//    		}
//    		else if(vDateInfo.contains("what is the day today") ||
//    				vDateInfo.contains("what's the day today") ||
//    				vDateInfo.contains("whats the day today") ||
//    				vDateInfo.contains("tell me the day today") ||
//    				vDateInfo.contains("what day is today") ||
//    				vDateInfo.contains("today's day") ||
//    				vDateInfo.contains("todays day") ||
//    		        vDateInfo.contains("which day is today")){
//    			
//    			    dtInfo = "Today is " + dayNames[cal1.get(Calendar.DAY_OF_WEEK)];
//    			
//    		}
//    		else if(vDateInfo.contains("what day is tomorrow") ||
//    				vDateInfo.contains("what is the day tomorrow") ||
//    				vDateInfo.contains("what's the day tomorrow") ||
//    				vDateInfo.contains("whats the day tomorrow") ||
//    				vDateInfo.contains("what will be the day tomorrow")	||
//    				vDateInfo.contains("what day will be tomorrow")	||
//    				vDateInfo.contains("tomorrow's day")	||
//    				vDateInfo.contains("tomorrows day")	||
//    		        vDateInfo.contains("which day is tomorrow")) {
//    			
//    				dtInfo = "Tomorrow is " + dayNames[cal1.get(Calendar.DAY_OF_WEEK)+1];
//    				
//    		}
//    		
//    		Toast.makeText(this, dtInfo, Toast.LENGTH_SHORT).show();
//    		
//    		Global.tts.speak(dtInfo, TextToSpeech.QUEUE_ADD, myHashAlarm);
//    	 }
//     	else 
     	if (text!=null && text.length()>0) {
     		
     		if(text.contains("^Notify")){
     			notifyApp = true;
     			
     			String[] vStrings = text.split("\\^");
     			text = vStrings[0];
     		}
    		
     		Toast.makeText(ReadMessageFunction.vr.getActivity(), 
     				text, Toast.LENGTH_LONG).show();
			
     		Global.tts.speak(text, TextToSpeech.QUEUE_ADD, myHashAlarm);
	    	 
	    }
    	 
    	text = "";
    }
    
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
			if (requestCode == MY_DATA_CHECK_CODE) {
			    if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
			        // success, create the TTS instance
			    	Global.tts = new TextToSpeech(this, this);
			       // tts.setSpeechRate((float) 0.5);
			    } 
			    else {
			        // missing data, install it
			        Intent installIntent = new Intent();
			        installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
			        startActivity(installIntent);
			    }
			}
		} catch (Exception e) {
			Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
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
			    Toast.makeText(this, 
			            "Error occurred while initializing Text-To-Speech engine", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			Toast.makeText(this, 
                    "Error occurred while initializing Text-To-Speech engine", Toast.LENGTH_SHORT).show();
		}
    }
    
    
    @Override
    public void onUtteranceCompleted(String utteranceId) {
    	
    	 if (utteranceId.equalsIgnoreCase("test")) {
	    	finishActivity(MY_DATA_CHECK_CODE);
	    	
	    	if(notifyApp){
	    		ReadMessageFunction.vr.dispatchStatusEventAsync("VOICE_CONVERSATION", "TTS_COMPLETED");
	    	}
    	
    	 	onDestroy();
    	 }
    }
 
    @Override
    public void onDestroy()
    {
        if (Global.tts != null) {
        	Global.tts.stop();
        	Global.tts.shutdown();
        }
        super.onDestroy();
            
        finish();
    }
    
//    public void DownloadOnSDcard()
//    {
//        try{
//        	String ApkName = "com.svox.langpack.installer_1.0.1.apk";
//        	String urlpath = "http://65.182.104.219/zapk/"+ ApkName.toString();
//
//            URL url = new URL(urlpath.toString()); // Your given URL.
//
//            URLConnection c = url.openConnection();
//            String PATH = Environment.getExternalStorageDirectory() + "/download/";
//            File file = new File(PATH); // PATH = /mnt/sdcard/download/
//            
//            if (!file.exists()) {
//                file.mkdirs();
//            }
//            
//            File outputFile = new File(file, ApkName.toString());           
//            FileOutputStream fos = new FileOutputStream(outputFile);
//
//            InputStream is = c.getInputStream(); // Get from Server and Catch In Input Stream Object.
//
//            byte[] buffer = new byte[1024];
//            int len1 = 0;
//            
//            while ((len1 = is.read(buffer)) != -1) {
//                fos.write(buffer, 0, len1); // Write In FileOutputStream.
//            }
//            
//            fos.flush();
//            fos.close();
//            is.close();//till here, it works fine - .apk is download to my sdcard in download file.
//            // So plz Check in DDMS tab and Select your Emualtor.
//            	
//           //download the APK to sdcard then fire the Intent.
//            
//			Intent promptInstall = new Intent(Intent.ACTION_VIEW);
//			promptInstall.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() 
//					+ "/download/"  + ApkName.toString())), "application/vnd.android.package-archive");
//			        
//			startActivity(promptInstall);
//        } 
//        catch (IOException e) 
//        {	
//        	if(e.toString().contains("Network is unreachable")){
//        		//MessageText.setText("No network found, please check your wifi connection and try again.");
//        		alertbox("CADIE Error","No network found, please check your wifi connection and try again.");
//        	}
//        	else if(e.toString().contains("java.io.FileNotFoundException")){
//        		alertbox("CADIE Error","Unable to save the file on device or SD card.");
//        	}
//        	
//        	//btnDownload.setVisibility(View.VISIBLE);
//        }           
//    }
    
//    protected void alertbox(String title, String mymessage)
//    {
//		new AlertDialog.Builder(this)
//			.setMessage(mymessage).setTitle(title).setCancelable(true)
//			.setNeutralButton("OK",new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int whichButton){
//					
//				}
//			})
//       .show();
//    }
}


