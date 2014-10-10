package com.jammala.mapapp;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

public class MainActivity extends Activity {

	AutoCompleteTextView from;
	AutoCompleteTextView to;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        from = (AutoCompleteTextView) findViewById(R.id.from);
        to = (AutoCompleteTextView) findViewById(R.id.to);
        from.setAdapter(new PlacesAutocompleteAdapter(this, R.layout.list_autocompleted_item));
        to.setAdapter(new PlacesAutocompleteAdapter(this, R.layout.list_autocompleted_item));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getDirections(View view){
    	DirectionsTask directions = new DirectionsTask();
    	directions.execute(from.getText().toString(), to.getText().toString());
    }
    
    
    public class DirectionsTask extends AsyncTask<String, Integer, String>{

    	private static final String DIRECTIONS_API_BASE = "https://maps.googleapis.com/maps/api/directions/json";
    	
        private static final String API_KEY = "AIzaSyBUPTRBg0TSE4E6m02XDJpNp1fxzwX8aRw";

        
    	@Override
    	protected String doInBackground(String... origDest) {
    		HttpURLConnection conn = null;
    		StringBuilder jsonResults = new StringBuilder();
    		ArrayList<String> resultList = null;
    		
    		String origin = origDest[0];
    		String destination = origDest[1];
    		StringBuilder sb = new StringBuilder(DIRECTIONS_API_BASE);
    		sb.append("?origin=" + origin);
    		sb.append("?destination=" + destination);
    		sb.append("?key=" + API_KEY);
    		
    		URL url;
    		try {
    			url = new URL(sb.toString());
    			conn = (HttpURLConnection) url.openConnection();
    	        InputStreamReader in = new InputStreamReader(conn.getInputStream());
    	        
    	     // Load the results into a StringBuilder
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1) {
                    jsonResults.append(buff, 0, read);
                }
    		} catch (MalformedURLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            
    		try {
                // Create a JSON object hierarchy from the results
                JSONObject jsonObj = new JSONObject(jsonResults.toString());
                JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

                // Extract the Place descriptions from the results
                resultList = new ArrayList<String>(predsJsonArray.length());
                for (int i = 0; i < predsJsonArray.length(); i++) {
                    resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
                }
            } catch (JSONException e) {
            	//log SOMETHING
            }

            return null;
        }
        
    	protected void onPostExecute(Long result) {
        	TextView distance = (TextView)findViewById(R.id.distance);
        }
    }
}
