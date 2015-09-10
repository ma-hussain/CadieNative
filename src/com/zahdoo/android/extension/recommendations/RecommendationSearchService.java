package com.zahdoo.android.extension.recommendations;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

public class RecommendationSearchService extends IntentService{

	private String vSearchText = "";
	private String vSearchType = "";
	private SQLiteDatabase aDataBase;
	//private String dbPath = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/Recommendation.db";
	
	private String aDB_PATH = "/data/data/air.com.zahdoo.cadie.debug/com.zahdoo.cadie.debug/Local Store/cadieAlertDB.sqlite";
	//private String aDB_PATH = "/data/data/air.com.zahdoo.cadie/com.zahdoo.cadie/Local Store/cadieAlertDB.sqlite";
	
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
	}
    
    public RecommendationSearchService()
    {
    	super("RecommendationSearchService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        if(intent != null){
        	vSearchText		= intent.getStringExtra("vSearchText");
        	vSearchType		= intent.getStringExtra("vSearchType");
        }
        
        if(vSearchText.contains(" ")){
        	String[] vStrings = vSearchText.split(" ");
        	vSearchText = "";
        	for(int x=0; x < vStrings.length ; x++){
        		if(x == 0)
        			vSearchText = vStrings[x];
        		else
        			vSearchText += ("+" + vStrings[x]);
        	}
        }
        
        //Toast.makeText(SearchRecommendationFunction.rs.getActivity(), "Searching recommendations for '" + vSearchText + "'\n" + "please wait...", Toast.LENGTH_SHORT).show();
        
        //Delete Old Result
        //db.delete(table_name, KEY_RESULTID + " != 0 " ,null );
        
        
        //String queryText = "shooting";
        //String bingUrl = "https://api.datamarket.azure.com/Bing/Search/Web?Query='multiple'&$top=4&$skip=1&$format=json";
        
       // String bingUrl = "https://api.datamarket.azure.com/Data.ashx/Bing/Search/v1/"+vSearchType+"?Query=%27"+vSearchText+"%27&$top=25&$format=json";
        
        
        registerInBackground();
        
        
        
//        String bingUrl = "https://api.datamarket.azure.com/Bing/Search/v1/"+vSearchType+"?Query=%27"+vSearchText+"%27&Adult=%27Moderate%27&$top=25&$format=json";
//        
//        
//    	String accountKey = "Ig6EUYlHHnSEAiO8BrhFgyFRjvSE2KSpxnhdqqUBbV0";
//    	byte[] accountKeyBytes = org.apache.commons.codec.binary.Base64.encodeBase64((accountKey + ":" + accountKey).getBytes());
//    	String accountKeyEnc = new String(accountKeyBytes);
//    	
//    	int matchCount = 0;
//    	URLConnection urlConnection;
//		try 
//		{
////			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
////
////			StrictMode.setThreadPolicy(policy);
//			
//			URL url = new URL(bingUrl);
//			urlConnection = url.openConnection();
//			urlConnection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);
//			
//	        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//	        StringBuffer sb = new StringBuffer();
//	        
//	        String inputLine;
//	        
//	        while ((inputLine = in.readLine()) != null){
//	            sb.append(inputLine);
//	        }
//	        
//	        try {
//	        	
//	            GsonBuilder gson = new GsonBuilder();
//	            Gson g = gson.create();    
//	            
//	        	RootObj ro = g.fromJson(sb.toString(), RootObj.class);
//				
//	        	matchCount = ro.d.results.size()-1;
//	        	
//	        	//Toast.makeText(SearchRecommendationFunction.rs.getActivity(), "Records found >> " + (ro.d.results.size()-1)  , Toast.LENGTH_LONG).show();
//	    		
//	        	aDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
//	             
//	        	
//				for (int i = 0; i < ro.d.results.size()-1; i++) {
//					Result arr = ro.d.results.get(i);
//					//Insert new Result
//					if(arr != null)
//						InsertSearchData(arr);
//				}
//			} catch (Exception e) {
//				Toast.makeText(SearchRecommendationFunction.rs.getActivity(), "1) " + e.getMessage(), Toast.LENGTH_SHORT).show();
//				//Toast.makeText(SearchRecommendationFunction.rs.getActivity(), "1) " + e.getMessage(), Toast.LENGTH_SHORT).show();
//			}
//
//	        in.close();
//		}catch(Exception e){
//			Toast.makeText(SearchRecommendationFunction.rs.getActivity(), "2) " + e.toString() , Toast.LENGTH_LONG).show();
//			//Toast.makeText(SearchRecommendationFunction.rs.getActivity(), "2) " + e.toString() , Toast.LENGTH_LONG).show();
//		}
//		finally{
//			SearchRecommendationFunction.rs.dispatchStatusEventAsync("RESULT_FOUND", matchCount + "");
//			finish();
//		}
    }
    
    private void doGsonParsing(String gsonStr)
    {
    	int matchCount = 0;
    	
    	try {
            GsonBuilder gson = new GsonBuilder();
            Gson g = gson.create();    
            
        	RootObj ro = g.fromJson(gsonStr, RootObj.class);
        	Log.i("CADIE GCM", "1 Gson Parsing - gsonStr =  " + gsonStr);
        	Log.i("CADIE GCM", "5 Gson Parsing - ro =  " + ro.toString() );
        	Log.i("CADIE GCM", "6 Gson Parsing - ro.d =  " + ro.d.toString() );
        	Log.i("CADIE GCM", "7 Gson Parsing - ro.d.results =  " + ro.d.results.toString() );
        	Log.i("CADIE GCM", "8 Gson Parsing - ro.d.results.size =  " + ro.d.results.size());
			
        	matchCount = ro.d.results.size()-1;
        	aDataBase = SQLiteDatabase.openDatabase(aDB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
        	
			for (int i = 0; i < ro.d.results.size()-1; i++) {
				Result arr = ro.d.results.get(i);
				//Insert new Result
				if(arr != null)
					InsertSearchData(arr);
			}
		} catch (Exception e) {
			//Toast.makeText(SearchRecommendationFunction.rs.getActivity(), "1) " + e.getMessage(), Toast.LENGTH_SHORT).show();
			//Toast.makeText(SearchRecommendationFunction.rs.getActivity(), "1) " + e.getMessage(), Toast.LENGTH_SHORT).show();
		}
    	finally{
			SearchRecommendationFunction.rs.dispatchStatusEventAsync("RESULT_FOUND", matchCount + "");
			stopSelf();
		}
    }
    
    
    private void registerInBackground() {
//        new AsyncTask<Void, Void, String>() 
//        {
//            @Override
//            protected String doInBackground(Void... params) {
            		
            	String bingUrl = "https://api.datamarket.azure.com/Bing/Search/v1/"+vSearchType+"?Query=%27"+vSearchText+"%27&Adult=%27Moderate%27&$top=25&$format=json";
                
                
            	String accountKey = "Ig6EUYlHHnSEAiO8BrhFgyFRjvSE2KSpxnhdqqUBbV0";
            	byte[] accountKeyBytes = org.apache.commons.codec.binary.Base64.encodeBase64((accountKey + ":" + accountKey).getBytes());
            	String accountKeyEnc = new String(accountKeyBytes);
            	URLConnection urlConnection;
            	StringBuffer sb = new StringBuffer();

                try {
                	URL url = new URL(bingUrl);
        			urlConnection = url.openConnection();
        			urlConnection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);
        			
        	        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        	        
        	        
        	        String inputLine;
        	        
        	        while ((inputLine = in.readLine()) != null){
        	            sb.append(inputLine);
        	        }
        	        
                } catch (Exception ex) {
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
//                return sb.toString();
//            }
//
//            @Override
//            protected void onPostExecute(String msg) {
            	doGsonParsing(sb.toString());
//            }
//        }.execute(null, null, null);
    }
    
    
    private void InsertSearchData(Result ar)
	{
    	try{
    		ContentValues values = new ContentValues();
    		values.put("Title", ar.title);
    		values.put("Description", ar.desc);
    		values.put("BingUrl", ar.bingUrl);
    		values.put("DisplayUrl", ar.displayUrl);
    		values.put("Url", ar.url);
    		values.put("MediaUrl", ar.mediaUrl);
    		values.put("SourceUrl", ar.sourceUrl);
    		values.put("Source", ar.source);
    		values.put("PostedOnDate", ar.date);
    		values.put("Width", ar.width);
    		values.put("Height", ar.height);
    		
    		if(ar.thumbnail != null){
    			Thumbnail thumb = ar.thumbnail;
    			
    			values.put("ThumbMediaUrl", thumb.mediaUrl);
    			values.put("ThumbWidth", thumb.width);
    			values.put("ThumbHeight", thumb.height);
    		}
    		
    		aDataBase.insert("SearchResults", null, values);
    		
    		
		}
    	catch (Exception e)
    	{
			//Toast.makeText(SearchRecommendationFunction.rs.getActivity(), "Recommendation Insert Error \n" + e.toString(), Toast.LENGTH_LONG).show();
			//Toast.makeText(AlarmActivity.this, z + " Update Error \n" + e.toString(), Toast.LENGTH_LONG).show();
		}
    	
		//Toast.makeText(SearchRecommendationFunction.rs.getActivity(), "Title >> `=" +  ar.title + "=`\nBingURL > `="+ ar.bingUrl +"=`", Toast.LENGTH_LONG).show();
	}
    
//    public void InsertSearchData_Test(String ar)
//	{
//    	ContentValues values = new ContentValues();
//		values.put("Title", ar);
//		
//		db.insert(table_name, null, values);
//	}
}

class RootObjDeserializer implements JsonDeserializer<RootObj>
{
	@Override
    public RootObj deserialize(JsonElement json, Type typeOfT,
    		JsonDeserializationContext context) throws JsonParseException 
    {
		return new RootObj();

    }
}


    
class RootObj {
    D d;
}

class D {
    List<Result> results;
}

class Thumbnail {
	@SerializedName("MediaUrl") 
    public String mediaUrl;
	
	@SerializedName("Width")
    public String width;
	
	@SerializedName("Height")
	public int height;
}

class Result {
    @SerializedName("Title")
    public String title;

    @SerializedName("Description")
    public String desc;
    
    @SerializedName("BingUrl")
    public String bingUrl;
    
    @SerializedName("DisplayUrl")
    public String displayUrl;

    @SerializedName("Url")
    public String url;
    
    @SerializedName("MediaUrl")
    public String mediaUrl;
    
    @SerializedName("SourceUrl")
    public String sourceUrl;
    
    @SerializedName("Source")
    public String source;

    @SerializedName("Date")
    public String date;
    
    @SerializedName("Width")
    public int width;
    
    @SerializedName("Height")
    public int height;
    
    @SerializedName("Thumbnail")
    public Thumbnail thumbnail;
}



