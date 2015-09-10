package com.zahdoo.android.extension.voiceinput;

import java.util.ArrayList;

import com.zahdoo.android.extension.alarm.Global;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

public class VoiceReg implements RecognitionListener {

	private Intent intent;
	
	public VoiceReg(String option)
    { 
		try 
		{
			if(option.contentEquals("restartListening")){
				
				if(Global.sr != null){
					Global.sr.stopListening();
					Global.sr.cancel();
					Global.sr.destroy();
					Global.sr = null;
				}
				
				if(Global.sr == null){
					Global.sr = SpeechRecognizer.createSpeechRecognizer(VoiceInputListenFunction.vi.getActivity());        
					Global.sr.setRecognitionListener(this);
					
					intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);        
					intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
					//intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"air.com.zahdoo.cadie");
					Global.mAudioManager = (AudioManager)VoiceInputListenFunction.vi.getActivity().getSystemService(Context.AUDIO_SERVICE);
					intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,1); 
				}
				
				Global.sr.startListening(intent);
				
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
			    {
					Global.mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
			    }
			}
			else{
				if(Global.sr != null){
					Global.sr.stopListening();
					Global.sr.cancel();
					Global.sr.destroy();
					Global.sr = null;
				}
				
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
			    {
					Global.mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
			    }
			}
			
		} catch (Exception e) {
			Log.d("CADIE GCM", "Voice Recognition Error - " + e.toString());
		}
		
    }
	
    public void onReadyForSpeech(Bundle params)
    {
    	try {
    		//VoiceInputListenFunction.vi.dispatchStatusEventAsync("VOICE_LISTEN_INFO", "VOICE_READY");
    		VoiceInputListenFunction.vi.dispatchStatusEventAsync("VOICE_READY", "^^READY^^");
		} catch (Exception e) {
			Log.e("CADIE GCM", "Voice Reg onReadyForSpeech Exception -" + e.toString());
		}
    }
   	
    public void onBeginningOfSpeech()
    {
    	try {
    		//VoiceInputListenFunction.vi.dispatchStatusEventAsync("VOICE_LISTEN_INFO", "VOICE_LISTENING");
    		VoiceInputListenFunction.vi.dispatchStatusEventAsync("VOICE_LISTENING", "^^LISTENING^^");
		} catch (Exception e) {
			Log.e("CADIE GCM", "Voice Reg onBeginningOfSpeech Exception -" + e.toString());
		}
    }
    
    public void onRmsChanged(float rmsdB)
    { }
    
    public void onBufferReceived(byte[] buffer)
    { }
    
    public void onEndOfSpeech()
    {
    	try {
    		//VoiceInputListenFunction.vi.dispatchStatusEventAsync("VOICE_LISTEN_INFO", "VOICE_PROCESSING");
    		VoiceInputListenFunction.vi.dispatchStatusEventAsync("VOICE_PROCESSING", "^^PROCESSING^^");
		} catch (Exception e) {
			Log.e("CADIE GCM", "Voice Reg onEndOfSpeech Exception -" + e.toString());
		}
    }
    
    public void onError(int errorCode)
    {
    	try 
    	{
    		if(errorCode == SpeechRecognizer.ERROR_RECOGNIZER_BUSY)
       	 	{
       		 	Global.sr.cancel();
       	 	}
       	 	else{ }
       	  
       	 	Global.sr.startListening(intent);
       	 	
       	 	//VoiceInputListenFunction.vi.dispatchStatusEventAsync("VOICE_LISTEN_INFO", "VOICE_ERROR");
       	 	VoiceInputListenFunction.vi.dispatchStatusEventAsync("VOICE_ERROR", String.valueOf(errorCode));
       	 	
		} catch (Exception e2) {
			Log.e("CADIE GCM", "Voice Reg onError Exception -" + e2.toString());
		}
    	
    }
    
    public void onResults(Bundle results)                   
    {
    	try 
    	{   
    		ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            
        	//VoiceInputListenFunction.vi.dispatchStatusEventAsync("VOICE_LISTEN_INFO", "VOICE_RECOGNIZED^" + data.get(0));
    		VoiceInputListenFunction.vi.dispatchStatusEventAsync("VOICE_RECOGNIZED", (String) data.get(0));
        	Global.sr.startListening(intent);
            
        	
		} catch (Exception e) {
			Log.e("CADIE GCM", "Voice Reg onResults Exception -" + e.toString());
		}
   	 	
    }
    
    public void onPartialResults(Bundle partialResults)
    { }
    
    public void onEvent(int eventType, Bundle params)
    { }
}
