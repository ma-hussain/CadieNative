package com.zahdoo.android.extension.alarm;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

public class OnAlarmAnsweredActivity extends Activity 
{
	private String launchType 		= "";
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        Log.d("CADIE","In On Create");
	        
	        try {
	        	Bundle extras = getIntent().getExtras(); 
	        	
				if(extras != null)
				{
					launchType 			= extras.getString("launchType");
					Log.d("CADIE", "LaunchType = " + launchType);
				}
				
				if(savedInstanceState !=  null)
					Log.d("CADIE", "Get Saved Instance Data - " + savedInstanceState.getString("launchType"));
				else
					Log.d("CADIE", "NULL Saved Instance Data " );
				
				if(launchType.equals("DONT_LAUNCH"))
				{
					Log.d("CADIE", "In Dont Launch" );
					getIntent().removeExtra("launchType");
					Log.d("CADIE", "2 LaunchType Value  - " + extras.getString("launchType"));
					
					Bundle newExtras = new Bundle();
					newExtras.putString("launchType", "LAUNCH");
					onSaveInstanceState(newExtras);
					Log.d("CADIE", " 22 Saving Instance State" );
				}
				else
				{
					Log.d("CADIE", "In Launch" );
					Global.bRun = true;
		        	
			        Intent i = new Intent( Intent.ACTION_MAIN );
			        PackageManager manager = getPackageManager();
			        i = manager.getLaunchIntentForPackage( "air.com.zahdoo.cadie" );
			        i.addCategory( Intent.CATEGORY_LAUNCHER );
			        startActivity( i );
			        System.exit( 0 );
				}
			} catch (Exception e) {
				Log.d("CADIE ", "OnAlarmAnsweredActivity Exception - " + e.toString());
			}
	        finally
	        {
	        	finish();
	        }
	    }
    
    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        Log.d("CADIE", " 11 Saving Instance State" );
    }
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
    	Log.d("CADIE","In On Restore");
        super.onRestoreInstanceState(savedInstanceState);
    }
}
