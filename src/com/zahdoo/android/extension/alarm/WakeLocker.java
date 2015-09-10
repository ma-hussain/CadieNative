package com.zahdoo.android.extension.alarm;

import android.content.Context;
import android.os.PowerManager;

public abstract class WakeLocker {
    private static PowerManager.WakeLock wakeLock;

    public static void acquire(Context ctx) {
        if (wakeLock != null) wakeLock.release();
        
        PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isScreenOn();
        
        if(!isScreenOn)
        {
	        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |
	                PowerManager.ACQUIRE_CAUSES_WAKEUP | 
	                PowerManager.ON_AFTER_RELEASE, "CADIE Alarm");
        	
	        
	        wakeLock.acquire(10000);
        }
    }

    public static void release() {
        if (wakeLock != null){
        	
        	if(wakeLock.isHeld())
        		wakeLock.release();
        	
        	wakeLock = null;
        }
    }
}
