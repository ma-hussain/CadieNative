package com.zahdoo.android.extension.voiceinput;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.widget.Button;
import android.widget.Toast;

import com.zahdoo.android.extension.alarm.Global;

public class VoiceResultTTS extends Activity implements  TextToSpeech.OnInitListener {
    
    private int MY_DATA_CHECK_CODE = 0;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    
    //private TextToSpeech tts;
    private Button speakButton;
    private String vResponseMsg = "";
    //private String vPromptMsg = "";
    
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
        	//vPromptMsg = extras.getString("vPromptMsg");
        }
        
    	//speakButton = (Button) findViewById(R.id.speak_button);
    	 
//    	speakButton.setOnClickListener(new OnClickListener() {           
//    		//@Override
//	    	public void onClick(View v) {
//	    		String text = vResponseMsg;
//	    	    if (text!=null && text.length()>0) {
//	    	    	
//	    	    	if(text.equalsIgnoreCase("invalid voice command") 
//	    	    			|| text.toLowerCase().indexOf("error")>-1 
//	    	    			|| text.toLowerCase().indexOf("invalid")>-1){}
//	    	    	else{
//	    	    		text += ". "+ vPromptMsg;//Would you like to create it?";
//	    	    	}
//	    	    	
//	    	    	Global.tts.speak(text, TextToSpeech.QUEUE_ADD, myHashAlarm);
//	    	    	Toast.makeText(VoiceResponseFunction.vr.getActivity(), text, Toast.LENGTH_SHORT).show();
//	    	    }
//	    	}
//    	});
//    	   
//    	Intent checkIntent = new Intent();
//    	checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
//    	startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
    }
 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
            	Global.tts = new TextToSpeech(VoiceResponseFunction.vr.getActivity(), this);
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
	            ArrayList<String> matches = data.getStringArrayListExtra(
	                    RecognizerIntent.EXTRA_RESULTS);
	            Object result[] 	= matches.toArray();
	           	            
	            Toast.makeText(VoiceResponseFunction.vr.getActivity(), "You said : "+ vResponseMsg, Toast.LENGTH_SHORT).show();
	           
	            VoiceResponseFunction.vr.dispatchStatusEventAsync("VOICE_RESPONSE", result[0].toString());
	            finish();
        	} 
        	else{
        		Toast.makeText(VoiceResponseFunction.vr.getActivity(), "Invalid response... Please try again", Toast.LENGTH_SHORT).show();
        	}

	        super.onActivityResult(requestCode, resultCode, data);
        }
    }
	
	
    //@Override
    public void onInit(int status) {  
        myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_NOTIFICATION));
        myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "test");
     
        if (status == TextToSpeech.SUCCESS) {
        	Global.tts.setOnUtteranceCompletedListener(new OnUtteranceCompletedListener() {

                //@Override
                public void onUtteranceCompleted(String utteranceId) {

                    runOnUiThread(new Runnable() {

                        //@Override
                        public void run() {
                        	if(vResponseMsg.equalsIgnoreCase("invalid voice command")){
                        		finishActivity(MY_DATA_CHECK_CODE);
                        		onDestroy();
                        	}
                        	else{
                        		finishActivity(MY_DATA_CHECK_CODE);
                        		
                        		if (Build.VERSION.SDK_INT <= 15)//Build.VERSION_CODES.JELLY_BEAN)
                        	    {
                        			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                         	        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                         	                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                         	        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "CADIE Voice Commands\nReply could be like 'Yes', 'No', 'Ok', 'Cancel'");
                         	        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
                        	    }
                        	    else{
                        	        	Toast.makeText(getApplicationContext(), "\n\nThis feature is not available in Android Jelly Bean.'\n\n", Toast.LENGTH_SHORT).show();
                        	        	 finish();
                        	    }
                        	}
                        }
                    });
                }
            });
        	
            speakButton.performClick();
        }
        else if (status == TextToSpeech.ERROR) {
            Toast.makeText(VoiceResponseFunction.vr.getActivity(), 
                    "Error occurred while initializing Text-To-Speech engine", Toast.LENGTH_SHORT).show();
        }
    }
}

