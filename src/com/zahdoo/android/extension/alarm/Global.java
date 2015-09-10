package com.zahdoo.android.extension.alarm;

import android.content.Context;
import android.media.AudioManager;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;

import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;

public class Global 
{
	public static Boolean bRun = false;
	public static int notificationID = 0; 
	
	public static Long iInterval = (long) 0;
	public static String aTitle = "";
	public static String aStatus = "";
	public static String EventReminderType = "";
	
	public static String EventReminderID  = "";
	public static int AlertID = 0;
	
	public static int FrequencyID = -10;
	public static int FrequencyValue = -10;
	public static String SelectedWeekDays = "";
	
	public static String RemindDate = "";
	public static String RemindTime = "";
	public static String UntillDate = "";
	
	public static AudioManager mAudioManager;
	public static SpeechRecognizer sr;
	public static TextToSpeech tts;
	
	public static TransferManager mTransferManager;
	public static Download mDownload;
	public static Upload mUpload;
	
	public static Context gContext;
}