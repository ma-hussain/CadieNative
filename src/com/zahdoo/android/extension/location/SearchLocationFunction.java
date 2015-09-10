package com.zahdoo.android.extension.location;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.location.Address;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.zahdoo.android.extension.LocationExtensionContext;

public class SearchLocationFunction implements FREFunction {
	
	public static LocationExtensionContext loc;
	private double currentLatitude;
	private double currentLongitude;
	
	@Override
	public FREObject call(FREContext context, FREObject[] passedArgs) 
	{

		FREObject result = null; 
		loc = (LocationExtensionContext)context;

		try {
			FREObject fro = passedArgs[0];
			String[] vStrings = fro.getAsString().split("\\^");
			
			 currentLatitude = Double.parseDouble(vStrings[0]);
			 currentLongitude = Double.parseDouble(vStrings[1]);
			  AsyncTask<Void, Void, Void> mFileTask;
			    
			  try
              {
              	 	mFileTask = new AsyncTask<Void, Void, Void>() 
                   {
              	 		String locName = "";
              	 		
                       @Override
       	               protected Void doInBackground(Void... params) 
                       {
                    	   Boolean isError = false;
                    	   try {
	           					List<Address> addresses = getFromLocation( currentLatitude, currentLongitude);
	           					if(addresses != null) 
	           					{
	           						StringBuilder strReturnedAddress = new StringBuilder("");
	           						strReturnedAddress.append(addresses.get(0).getAddressLine(0));
	           						locName = strReturnedAddress.toString();
	           					}
	           					else{
	           						locName = "No Address returned!";
	           					}
	           				} 
                    	    catch (Exception e) 
	           				{
                    	    	isError = true;
	           					//locName = "Can't get Address!";
	           					//Toast.makeText(SearchLocationFunction.loc.getActivity(), "address error\n" + e , Toast.LENGTH_LONG).show();
	           				}
                    	   
                    	   if(isError)
                    	   {
                    		   try {
                    			   List<Address> addresses = getFromLocation2( currentLatitude, currentLongitude);
	   	           					if(addresses != null) 
	   	           					{
	   	           						StringBuilder strReturnedAddress = new StringBuilder("");
	   	           						strReturnedAddress.append(addresses.get(0).getAddressLine(0));
	   	           						locName = strReturnedAddress.toString();
	   	           					}
	   	           					else{
	   	           						locName = "No Address returned!";
	   	           					}
								} catch (Exception e) {
									locName = "Can't get Address!";
		           					Toast.makeText(SearchLocationFunction.loc.getActivity(), "22 address error\n" + e , Toast.LENGTH_LONG).show();
								}
                    	   }
                    	   
                    	   return null;
                       }
       	     
                       @Override
                       protected void onPostExecute(Void result) 
                       {
                    	   SearchLocationFunction.loc.dispatchStatusEventAsync("LOCATION_FOUND",locName);
                       }
                   };
                   mFileTask.execute(null, null, null);
              } 
              catch (Exception e) 
              {
              	Log.d("CADIE Location" , e.toString() );
              	Toast.makeText(SearchLocationFunction.loc.getActivity(), "1 Location error\n" + e.toString() , Toast.LENGTH_LONG).show();
              }
		} 
		catch (Exception e) 
		{ 
			e.printStackTrace(); 
			Toast.makeText(SearchLocationFunction.loc.getActivity(), "2 Location error\n" + e.toString() , Toast.LENGTH_LONG).show();
		} 
		
		return result;
	}
	
	private List<Address> getFromLocation(double lat, double lng)
	{
		List<Address> retList = null;
		
		try {
			String address = String.format(Locale.ENGLISH,"http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&language="+Locale.getDefault().getCountry(), lat, lng);
			HttpGet httpGet = new HttpGet(address);
			HttpClient client = new DefaultHttpClient();
			HttpResponse response;
			StringBuilder stringBuilder = new StringBuilder();
			
			try 
			{
				response = client.execute(httpGet);
				HttpEntity entity = response.getEntity();
				InputStream stream = entity.getContent();
				int b;
				while ((b = stream.read()) != -1) {
					stringBuilder.append((char) b);
				}
				
				JSONObject jsonObject = new JSONObject();
				jsonObject = new JSONObject(stringBuilder.toString());
				
				retList = new ArrayList<Address>();
				
				if("OK".equalsIgnoreCase(jsonObject.getString("status")))
				{
					JSONArray results = jsonObject.getJSONArray("results");
					
					for (int i=0;i<results.length();i++ ) 
					{
						JSONObject result 	= results.getJSONObject(i);
						String indiStr 		= result.getString("formatted_address");
						String indiStr2		= result.getString("types");
						Log.d("CADIE LOCATION", result.toString());
						Log.d("CADIE LOCATION", indiStr);
						Log.d("CADIE LOCATION", indiStr2);
						
						Address addr 		= new Address(Locale.getDefault());
						
						if(indiStr2.contentEquals("[\"locality\",\"political\"]"))//Seen from json
						{
							addr.setAddressLine(0, indiStr);
							retList.add(addr);
						}
					}
				}
				
				//Toast.makeText(SearchLocationFunction.loc.getActivity(), "got the address." +  retList.size(), Toast.LENGTH_LONG).show();
			}
			catch (Exception e) {
				Toast.makeText(SearchLocationFunction.loc.getActivity(), "Error webservice response.\n" + e, Toast.LENGTH_LONG).show();
			}
			
			return retList;
		} catch (Exception e) {
			Toast.makeText(SearchLocationFunction.loc.getActivity(), "Error  .\n" + e, Toast.LENGTH_LONG).show();
		}
		
		return retList;
	}
	
	public List<Address> getFromLocation2(double lat, double lng){
		
		List<Address> retList = null;
		
		try {
			
			String address = String.format(Locale.ENGLISH,"http://maps.googleapis.com/maps/api/geocode/json?latlng=%1$f,%2$f&sensor=true&language="+Locale.getDefault().getCountry(), lat, lng);
			HttpGet httpGet = new HttpGet(address);
			HttpClient client = new DefaultHttpClient();
			HttpResponse response;
			StringBuilder stringBuilder = new StringBuilder();

			
			try
			{
				response = client.execute(httpGet);
				HttpEntity entity = response.getEntity();
				InputStream stream = entity.getContent();
				int b;
				while ((b = stream.read()) != -1) {
					stringBuilder.append((char) b);
				}
				
				JSONObject jsonObject = new JSONObject();
				jsonObject = new JSONObject(stringBuilder.toString());
				
				
				retList = new ArrayList<Address>();
				
				
				if("OK".equalsIgnoreCase(jsonObject.getString("status")))
				{
					JSONArray results = jsonObject.getJSONArray("results");
					
					for (int i=0;i<results.length();i++ ) 
					{
						JSONObject result 	= results.getJSONObject(i);
						String indiStr 		= result.getString("formatted_address");
						Address addr 		= new Address(Locale.US);
						addr.setAddressLine(0, indiStr);
						retList.add(addr);
					}
				}
			} 
			catch (Exception e) {
				Toast.makeText(SearchLocationFunction.loc.getActivity(), "Error webservice response.\n" + e, Toast.LENGTH_LONG).show();
			}
			
			return retList;
		}
		catch (Exception e) {
			Toast.makeText(SearchLocationFunction.loc.getActivity(), "Error  .\n" + e, Toast.LENGTH_LONG).show();
		}
		
		return retList;
	}
}
