package com.maomuffy.medicnaija;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity implements OnClickListener {

	TextView tvSignUp;
	Button btLogin;
	EditText etEmail,etPassword;
	private final static int EXIT_CODE = 1;
	ProgressDialog pDialog;
	
	// flag for Internet connection status
    Boolean isInternetAvailable = false;
	NetworkConnectionDetector con;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		String footer = new MedicNaijaUtils().footer_text();
		TextView tvFooter = (TextView) findViewById(R.id.tvFooter);
		tvFooter.setText(footer);
		
		 // creating connection detector class instance
        con = new NetworkConnectionDetector(getApplicationContext());
        
		SharedPreferences sharedPref = getSharedPreferences("com.maomuffy.medicnaija.userprofilepref", MODE_PRIVATE);
		int isLoggedIn = sharedPref.getInt("isLoggedIn", 0);
		if( isLoggedIn == 1 ){
		
			// Check if User is Logged In
			Intent i = new Intent(this, DashboardActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(i, EXIT_CODE);
		}
		tvSignUp = (TextView) findViewById(R.id.tvSignUp);
		btLogin = (Button) findViewById(R.id.btLogin);
		etEmail = (EditText) findViewById(R.id.etEmailAddress);
		etPassword = (EditText) findViewById(R.id.etPassword);

		btLogin.setOnClickListener(this);
		tvSignUp.setOnClickListener(this);
				
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btLogin:
			isInternetAvailable = con.isNetworkEnabled();
			if( isInternetAvailable ){
				String loginEmail = etEmail.getText().toString();
				String loginPassword = etPassword.getText().toString();
				new aacAuthApiCall().execute(loginEmail,loginPassword);
			} else {
				con.showDialog(this, "Network Connection", "We Need Internet Access!", isInternetAvailable);
			}		
		break;
		case R.id.tvSignUp:
			isInternetAvailable = con.isNetworkEnabled();
			if( isInternetAvailable ){
				Intent iSignup = new Intent(this, SignUpActivity.class);
				startActivity(iSignup);
			} else {
				con.showDialog(this, "Network Connection", "We Need Internet Access!", isInternetAvailable);
			}
			break;
		default:
			break;
		}
		
	}
	
	private class aacAuthApiCall extends AsyncTask<String, Integer, String>{

		@Override
		protected String doInBackground(String... params) {
			// Detach operation from UI thread
			
			try {
				// Create a new HttpClient and Post Header
				HttpClient httpclient = new DefaultHttpClient();
				/* API URL */
//				HttpPost httppost = new HttpPost("http://10.0.2.2/aac_api/public/api/v1/authenticate");
				HttpPost httppost = new HttpPost("http://aacapi.betatesting.com.ng/api/v1/authenticate");
				
				List<NameValuePair> nvp = new ArrayList<NameValuePair>();
				nvp.add(new BasicNameValuePair("email", params[0]));
				nvp.add(new BasicNameValuePair("password", params[1]));
				httppost.setEntity(new UrlEncodedFormEntity(nvp));

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				MedicNaijaUtils ists = new MedicNaijaUtils();
				String str = ists.inputStreamToString(response.getEntity().getContent()).toString();				
				// Return result as string
				return str;

			} catch (ClientProtocolException e) {
				Toast.makeText(getApplicationContext(), "Network error...", Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				Toast.makeText(getApplicationContext(), "Network error...", Toast.LENGTH_SHORT).show();
			}
			
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
			//pDialog.setTitle("Sync");
			pDialog.setMessage("Signing in...");
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected void onProgressUpdate(Integer... v) {
			super.onProgressUpdate(v);
			pDialog.setProgress(v[0]);
		}

		@Override
		protected void onPostExecute(String r) {
			// Attach result back to UI thread
			super.onPostExecute(r);
			
			Context ct = getApplicationContext();
			
			// Parse JSON from API Response
			try {
				JSONObject json = new JSONObject(r);
				
				if( json.getString("status").toLowerCase(Locale.getDefault()).equalsIgnoreCase("success") ){
					// Get Data object
					JSONObject data = json.getJSONObject("data");
					
					// Prepare for storage
					String email = etEmail.getText().toString();
					String surname = data.getString("surname");
					String firstName = data.getString("firstname");
					int userID = data.getInt("users_id");
					String gsm = data.getString("gsm");
					int lgaID = data.getInt("lga_id");
					int stateID = data.getInt("states_id");
					
					// Store Returned User Profile Object in Shared Preferences
					
					// Create object of SharedPreferences.
					SharedPreferences sharedPref = getSharedPreferences("com.maomuffy.medicnaija.userprofilepref", 0);
					// now get Editor
					SharedPreferences.Editor editor = sharedPref.edit();
					// put your value
					editor.putInt("isLoggedIn", 1);
					editor.putString("email", email);
					editor.putString("surname", surname);
					editor.putString("firstname", firstName);
					editor.putString("gsm", gsm);
					editor.putInt("user_id", userID);
					editor.putInt("lga_id", lgaID);
					editor.putInt("state_id", stateID);
					// commits your edits
					editor.commit();
					
					Intent i = new Intent(ct, DashboardActivity.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivityForResult(i, EXIT_CODE);
					
					pDialog.dismiss();
					
					
				} else {
					
					pDialog.dismiss();
					
					// Notify User of Failure
					Toast toast = Toast.makeText(ct, json.getString("message"), Toast.LENGTH_SHORT);
					toast.show();
				}
			} catch (JSONException e) {
				Toast.makeText(ct, "Network error...", Toast.LENGTH_SHORT).show();
			}
		}
		
	}

	@Override
	protected void onResume() {
		SharedPreferences sharedPref = getSharedPreferences("com.maomuffy.medicnaija.userprofilepref", MODE_PRIVATE);
		int isLoggedIn = sharedPref.getInt("isLoggedIn", 0);
		if( isLoggedIn == 1 ){
			Intent i = new Intent(this, DashboardActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(i, EXIT_CODE);
		}
		super.onResume();
	}
	
	
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		SharedPreferences sharedPref = getSharedPreferences("com.maomuffy.medicnaija.userprofilepref", MODE_PRIVATE);
		int isLoggedIn = sharedPref.getInt("isLoggedIn", 0);
		if( isLoggedIn == 1 ){
			Intent i = new Intent(this, DashboardActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivityForResult(i, EXIT_CODE);
		}
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    if(requestCode == EXIT_CODE){
	        MainActivity.this.finish();
	    }
	}

    
}
