package com.zahdoo.android.extension.tts;


import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import android.media.AudioManager;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.widget.Toast;

import com.adobe.fre.FREContext;

public class TTSController implements OnInitListener, OnUtteranceCompletedListener {

        private static final String END_OF_WAKEUP_MESSAGE_ID = "end of wakeup message ID";
        private static TTSController _instance;
        private TextToSpeech _tts;
        private FREContext _freContext;
        private HashMap<String, String> _alarmParam;
        private String vDateInfo = "";
        private String appStrArr[];
        
        private int MY_DATA_CHECK_CODE = 0;
        private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
        
        public static TTSController getInstance() {
			if (_instance == null) {
				_instance = new TTSController();
			}
			return _instance;
        }

        public void createTTS(FREContext context) {
			if (_tts == null) {
				_tts = new TextToSpeech(context.getActivity(), this);
				_alarmParam = new HashMap<String, String>();
				_alarmParam.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
						END_OF_WAKEUP_MESSAGE_ID);
				_alarmParam.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
						String.valueOf(AudioManager.STREAM_NOTIFICATION));
				_alarmParam.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "test");
			}
			_freContext = context;
        }

        public void speak(String text, String msg) {
        	//String text = vResponseMsg;
        	vDateInfo = msg;
        	
        	
        	if (text.equalsIgnoreCase("AppName")) 
       	 	{
        		
        		String appStr = "Hi, I am Cadie, Your Personal Assistant..";
        		_tts.speak(appStr, TextToSpeech.QUEUE_ADD, _alarmParam);
        		
       	 	}
       	 	else if (text.equalsIgnoreCase("AboutApp")) 
       	 	{
       	 		Toast.makeText(_freContext.getActivity(), text,Toast.LENGTH_LONG).show();
       	 	
				Random rndm = new Random();
				appStrArr = new String[10];
				
				appStrArr[0] = "Hi this is Cadie, I can create appointments, Reminders, ToDo Lists and many more, like. ";
				appStrArr[0] += "I can do most of your tasks by your voice commands. ";
				appStrArr[0] += "I can make phone calls, send text messages, and send emails to anyone from your phonebook. ";
				appStrArr[0] += "I can create contexts and extract tags to help you to organize and find items related to certain tags or context. ";
				appStrArr[0] += "I can get your current location and remind you for any work related to that location. ";
				appStrArr[0] += "You can use me to create Voice Notes. ";
				appStrArr[0] += "I can help you to create a notes having text, pictures, videos with appointments, reminders and todo lists, you can even attach an existing appointment, reminder or list to a note. ";
				appStrArr[0] += "You can ask me to show maps, route directions, give weather forecast of any place, ";
				appStrArr[0] += "I can also assist you in doing any web search. ";
				appStrArr[0] += "Thank you. ";

				appStrArr[1] = "Hello i am Cadie, I can create appointments, Reminders, ToDo Lists and many more, like. ";
				appStrArr[1] += "I can make phone calls, send text messages, and send emails to anyone from your phonebook. ";
				appStrArr[1] += "I can do most of your tasks by your voice commands. ";
				appStrArr[1] += "You can ask me to show maps, route directions, give weather forecast of any place, ";
				appStrArr[1] += "I can also assist you in doing any web search. ";
				appStrArr[1] += "I can create contexts and extract tags to help you to organize and find items related to certain tags or context. ";
				appStrArr[1] += "I can get your current location and remind you for any work related to that location. ";
				appStrArr[1] += "You can use me to create Voice Notes. ";
				appStrArr[1] += "I can help you to create a notes having text, pictures, videos with appointments, reminders and todo lists, you can even attach an existing appointment, reminder or list to a note. ";
				appStrArr[1] += "Thank you. ";
	
				appStrArr[2] = "Welcome to Cadie, I can create appointments, Reminders, ToDo Lists and many more, like. ";
				appStrArr[2] += "I can do most of your tasks by your voice commands. ";
				appStrArr[2] += "I can get your current location and remind you for any work related to that location. ";
				appStrArr[2] += "You can use me to create Voice Notes. ";
				appStrArr[2] += "You can ask me to show maps, route directions, give weather forecast of any place, ";
				appStrArr[2] += "I can help you to create a notes having text, pictures, videos with appointments, reminders and todo lists, you can even attach an existing appointment, reminder or list to a note. ";
				appStrArr[2] += "I can also assist you in doing any web search. ";
				appStrArr[2] += "I can make phone calls, send text messages, and send emails to anyone from your phonebook. ";
				appStrArr[2] += "I can create contexts and extract tags to help you to organize and find items related to certain tags or context. ";
				appStrArr[2] += "Thank you. ";
	
				appStrArr[3] = "I am Cadie, I can create appointments, Reminders, ToDo Lists and many more, like. ";
				appStrArr[3] += "I can do most of your tasks by your voice commands. ";
				appStrArr[3] += "You can ask me to show maps, route directions, give weather forecast of any place, ";
				appStrArr[3] += "I can get your current location and remind you for any work related to that location. ";
				appStrArr[3] += "You can use me to create Voice Notes. ";
				appStrArr[3] += "I can make phone calls, send text messages, and send emails to anyone from your phonebook. ";
				appStrArr[3] += "I can create contexts and extract tags to help you to organize and find items related to certain tags or context. ";
				appStrArr[3] += "I can help you to create a notes having text, pictures, videos with appointments, reminders and todo lists, you can even attach an existing appointment, reminder or list to a note. ";
				appStrArr[3] += "I can also assist you in doing any web search. ";
				appStrArr[3] += "Thank you. ";
	

				_tts.speak(appStrArr[rndm.nextInt(4)], TextToSpeech.QUEUE_ADD,
						_alarmParam);
				
			} 
       	 	else if (text.equalsIgnoreCase("AboutTime")) {
	
				Calendar cal1 = new GregorianCalendar();
				String dtInfo = new String();
				String am_pm = "";
				String dayNames[] = new DateFormatSymbols().getWeekdays();
				String hr, min, sec = new String();
	
				if (vDateInfo.contains("what is the time now")
						|| vDateInfo.contains("what's the time now")
						|| vDateInfo.contains("whats the time now")
						|| vDateInfo.contains("what is the time")
						|| vDateInfo.contains("what's the time")
						|| vDateInfo.contains("whats the time")
						|| vDateInfo.contains("tell me the time")
						|| vDateInfo.contains("tell me the current time")
						|| vDateInfo.contains("tell me current time")
						|| vDateInfo.contains("tell the current time")
						|| vDateInfo.contains("tell current time")
						|| vDateInfo.contains("tell the time")
						|| vDateInfo.contains("show me the current time")
						|| vDateInfo.contains("show me current time")
						|| vDateInfo.contains("show the current time")
						|| vDateInfo.contains("show me the time")
						|| vDateInfo.contains("show the time")
						|| vDateInfo.contains("get current time")
						|| vDateInfo.contains("get the current time")
						|| vDateInfo.contains("get the time")
						|| vDateInfo.contains("get me the time")
						|| vDateInfo.contains("get me current time")
						|| vDateInfo.contains("get me the current time")) {
	
					if (cal1.get(Calendar.AM_PM) == 0)
						am_pm = "AM";
					else
						am_pm = "PM";
	
					if (cal1.get(Calendar.HOUR) == 0)
						hr = "12";
					else
						hr = cal1.get(Calendar.HOUR) + "";
	
					if (cal1.get(Calendar.MINUTE) < 10)
						min = "0" + cal1.get(Calendar.MINUTE);
					else
						min = cal1.get(Calendar.MINUTE) + "";
	
					if (cal1.get(Calendar.SECOND) < 10)
						sec = "0" + cal1.get(Calendar.SECOND);
					else
						sec = cal1.get(Calendar.SECOND) + "";
	
					dtInfo = " The time is " + hr + ":" + min + ":" + sec + " "
							+ am_pm;
	
				} else if ((vDateInfo.contains("what is the date")
						|| vDateInfo.contains("what's the date")
						|| vDateInfo.contains("whats the date")
						|| vDateInfo.contains("what is today's date")
						|| vDateInfo.contains("what is todays date")
						|| vDateInfo.contains("what's the date today")
						|| vDateInfo.contains("whats the date today")
						|| vDateInfo.contains("tell me today's date")
						|| vDateInfo.contains("tell me todays date")
						|| vDateInfo.contains("tell today's date")
						|| vDateInfo.contains("tell todays date")
						|| vDateInfo.contains("give me today's date")
						|| vDateInfo.contains("give me todays date")
						|| vDateInfo.contains("give today's date")
						|| vDateInfo.contains("give todays date")
						|| vDateInfo.contains("give me date")
						|| vDateInfo.contains("give me the date")
						|| vDateInfo.contains("get me today's date")
						|| vDateInfo.contains("get me todays date")
						|| vDateInfo.contains("get me the date")
						|| vDateInfo.contains("get me date")
						|| vDateInfo.contains("give the date")
						|| vDateInfo.contains("tell me date")
						|| vDateInfo.contains("tell the date") || vDateInfo
							.contains("tell me the date"))
						&& !vDateInfo.contains("tomorrow")) {
	
					dtInfo = "Todays date is " + (cal1.get(Calendar.MONTH) + 1)
							+ "/" + cal1.get(Calendar.DAY_OF_MONTH) + "/"
							+ cal1.get(Calendar.YEAR);
	
				} else if (vDateInfo.contains("what is the date tomorrow")
						|| vDateInfo.contains("what's the date tomorrow")
						|| vDateInfo.contains("what is tomorrow's date")
						|| vDateInfo.contains("what is tomorrows date")
						|| vDateInfo.contains("what will be tomorrows date")
						|| vDateInfo.contains("what date is tomorrow")
						|| vDateInfo.contains("tell me tomorrow's date")
						|| vDateInfo.contains("tell me tomorrows date")
						|| vDateInfo.contains("tell tomorrows date")
						|| vDateInfo.contains("tell tomorrow's date")
						|| vDateInfo.contains("tomorrow's date")
						|| vDateInfo.contains("tomorrow's date will be")
						|| vDateInfo.contains("get tomorrow's date")
						|| vDateInfo.contains("get tomorrows date")
						|| vDateInfo.contains("give me tomorrow's date")
						|| vDateInfo.contains("give me tomorrows date")
						|| vDateInfo.contains("give tomorrow's date")
						|| vDateInfo.contains("give tomorrows date")) {
	
					cal1.add(Calendar.DAY_OF_MONTH, 1);
					
					dtInfo = "Tomorrows date is " + (cal1.get(Calendar.MONTH) + 1)
							+ "/" + (cal1.get(Calendar.DAY_OF_MONTH)) + "/"
							+ cal1.get(Calendar.YEAR);
					
					
	
				} else if (vDateInfo.contains("what is the day today")
						|| vDateInfo.contains("what's the day today")
						|| vDateInfo.contains("whats the day today")
						|| vDateInfo.contains("tell me the day today")
						|| vDateInfo.contains("what day is today")
						|| vDateInfo.contains("today's day")
						|| vDateInfo.contains("todays day")
						|| vDateInfo.contains("which day is today")) {
	
					dtInfo = "Today is " + dayNames[cal1.get(Calendar.DAY_OF_WEEK)];
	
				} else if (vDateInfo.contains("what day is tomorrow")
						|| vDateInfo.contains("what is the day tomorrow")
						|| vDateInfo.contains("what's the day tomorrow")
						|| vDateInfo.contains("whats the day tomorrow")
						|| vDateInfo.contains("what will be the day tomorrow")
						|| vDateInfo.contains("what day will be tomorrow")
						|| vDateInfo.contains("tomorrow's day")
						|| vDateInfo.contains("tomorrows day")
						|| vDateInfo.contains("which day is tomorrow")) {
	
					dtInfo = "Tomorrow is "
							+ dayNames[cal1.get(Calendar.DAY_OF_WEEK) + 1];
	
				}
	
				Toast.makeText(_freContext.getActivity(), dtInfo,
						Toast.LENGTH_SHORT).show();
				_tts.speak(dtInfo, TextToSpeech.QUEUE_ADD, _alarmParam);
	
			}else if (text != null && text.length() > 0) {
	
				_tts.speak(text, TextToSpeech.QUEUE_ADD, _alarmParam);
			}
	
        	
			text = "";
        }

        public TextToSpeech getTTS() {
                return _tts;
        }

        @Override
        public void onInit(int status) {
            String ttsStatus = "ERROR";
            switch (status) {
                    case TextToSpeech.SUCCESS:
                            _tts.setLanguage(Locale.getDefault());
                            ttsStatus = "SUCCESS";
                            break;
            }
            _tts.setOnUtteranceCompletedListener(this);
            _freContext.dispatchStatusEventAsync("CREATE_STATUS", ttsStatus);
        }

        @Override
        public void onUtteranceCompleted(String utteranceId) {
//        	Toast.makeText(_freContext.getActivity(), utteranceId,
//					Toast.LENGTH_SHORT).show();
        	
			//if (END_OF_WAKEUP_MESSAGE_ID.equals(utteranceId)) {
				_freContext.dispatchStatusEventAsync("PLAY_STATUS", "COMPLETED");
			//}
        }
}