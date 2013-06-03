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
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
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

public class SignUpActivity extends Activity implements OnClickListener {

	public static final int EXIT_CODE = 1;
	TextView etSurname, etFirstName, etGsm, etPass, etEmailAddress;
	Spinner spState, spLga;
	Button btSignUp;
	Toast toast;
	String[] state;
	String[] lga;
	int lgaStateIndex;
	int indexLga;
	int stateIndex;
	int lgaIdIncrementor;
	int lgaId;
	ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		
		String footer = new MedicNaijaUtils().footer_text();
		TextView tvFooter = (TextView) findViewById(R.id.tvFooter);
		tvFooter.setText(footer);

		etEmailAddress = (TextView) findViewById(R.id.etSignUpEmail);
		etSurname = (TextView) findViewById(R.id.etSurname);
		etFirstName = (TextView) findViewById(R.id.etFirstName);
		etGsm = (TextView) findViewById(R.id.etGsm);
		etPass = (TextView) findViewById(R.id.etPass);

		btSignUp = (Button) findViewById(R.id.btSignUp);

		state = getResources().getStringArray(R.array.States);
		Spinner state_spinner = (Spinner) findViewById(R.id.spState);
		ArrayAdapter<String> state_adapter = new ArrayAdapter<String>(this,
//				android.R.layout.simple_spinner_dropdown_item, state);
//				android.R.layout.simple_spinner_item, state);
		R.layout.medicnaija_item_style, state);
		state_spinner.setAdapter(state_adapter);
		
		state_spinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> sv,
							View arg1, int pos, long id) {
						stateIndex = sv.getSelectedItemPosition();
						lgaStateIndex = stateIndex + 1;
						// Call Method Here
						lga = resourceForIndex(lgaStateIndex);

						final Spinner lga_spinner = (Spinner) findViewById(R.id.spLga);
						ArrayAdapter<String> lga_adapter = new ArrayAdapter<String>(
								SignUpActivity.this,
//								android.R.layout.simple_spinner_dropdown_item,
//								android.R.layout.simple_spinner_item,
								R.layout.medicnaija_item_style,
								lga);
						lga_spinner.setAdapter(lga_adapter);
						lga_spinner
								.setOnItemSelectedListener(new OnItemSelectedListener() {

									@Override
									public void onItemSelected(
											AdapterView<?> lv, View arg1,
											int arg2, long arg3) {
										indexLga = lv
												.getSelectedItemPosition();
										lgaId = indexLga + lgaIdIncrementor;
										
									}

									@Override
									public void onNothingSelected(
											AdapterView<?> arg0) {
										lga_spinner.setAdapter(null);

									}
								});
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
					}

				});

		btSignUp.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// Switch between the different button events by ID
		switch (v.getId()) {
		case R.id.btSignUp:
			// Make API Call

			String apiEmail = etEmailAddress.getText().toString();
			String apiSurname = etSurname.getText().toString();
			String apiFirstName = etFirstName.getText().toString();
			String apiGsm = etGsm.getText().toString();
			String apiState = lgaStateIndex + "";
			String apiLga = lgaId + "";
			String apiPass = etPass.getText().toString();
			
			new aacSignUpApiCall().execute(apiEmail, apiSurname,
					apiFirstName, apiPass, apiGsm, apiState, apiLga);			
			break;
		default:
			break;
		}

	}

	private class aacSignUpApiCall extends
			AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			// Detach operation from UI thread

			try {
				// Create a new HttpClient and Post Header
				HttpClient httpclient = new DefaultHttpClient();
				/* API URL */
//				HttpPost httppost = new HttpPost(
//						"http://10.0.2.2/aac_api/public/api/v1/user");
				HttpPost httppost = new HttpPost(
						"http://aacapi.betatesting.com.ng/api/v1/user");

				List<NameValuePair> nvp = new ArrayList<NameValuePair>();
				nvp.add(new BasicNameValuePair("email", params[0]));
				nvp.add(new BasicNameValuePair("surname", params[1]));
				nvp.add(new BasicNameValuePair("firstname", params[2]));
				nvp.add(new BasicNameValuePair("password", params[3]));
				nvp.add(new BasicNameValuePair("gsm", params[4]));
				nvp.add(new BasicNameValuePair("state", params[5]));
				nvp.add(new BasicNameValuePair("lga", params[6]));
				httppost.setEntity(new UrlEncodedFormEntity(nvp));

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				
				MedicNaijaUtils ists = new MedicNaijaUtils();
				String str = ists.inputStreamToString(
						response.getEntity().getContent()).toString();
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
			pDialog = new ProgressDialog(SignUpActivity.this);
			//pDialog.setTitle("Sync");
			pDialog.setMessage("Signing Up...");
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

				if (json.getString("status").toLowerCase(Locale.getDefault())
						.equalsIgnoreCase("success")) {
					// Get Data object
					JSONObject data = json.getJSONObject("data");

					// Prepare for storage
					String email = data.getString("email");
					String surname = data.getString("surname");
					String firstName = data.getString("firstname");
					String gsm = data.getString("gsm");
					int lgaID = data.getInt("lga_id");
					int stateID = data.getInt("states_id");

					// Store Returned User Profile Object in Shared Preferences

					// Create object of SharedPreferences.
					SharedPreferences sharedPref = getSharedPreferences(
							"com.maomuffy.medicnaija.userprofilepref", 0);
					// now get Editor
					SharedPreferences.Editor editor = sharedPref.edit();
					// put your value
					editor.putInt("isLoggedIn", 1);
					editor.putString("email", email);
					editor.putString("surname", surname);
					editor.putString("firstname", firstName);
					editor.putString("gsm", gsm);
					editor.putInt("lga_id", lgaID);
					editor.putInt("state_id", stateID);
					// commits your edits
					editor.commit();
					
					pDialog.setMessage("Sign Up Complete!");
					
					pDialog.dismiss();
					
					// Kill Activity
					SignUpActivity.this.finish();
					Intent i = new Intent(ct, DashboardActivity.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivityForResult(i, EXIT_CODE);
					
				} else {
					
					pDialog.dismiss();
					
					// Notify User of Failure
					Toast toast = Toast.makeText(ct, json.getString("message"),
							Toast.LENGTH_SHORT);
					toast.show();
				}
			} catch (JSONException e) {
				Toast.makeText(ct, "Network error...", Toast.LENGTH_SHORT).show();
			}
			
		}

	}
	
	public String[] resourceForIndex(Integer r){
		String[] lga_resource = null;
		switch (r) {
		case 1:
			lgaIdIncrementor = 1;
			lga_resource = getResources().getStringArray(
					R.array.State_1_Lga);
			break;
		case 2:
			lgaIdIncrementor = 19;
			lga_resource = getResources().getStringArray(
					R.array.State_2_Lga);
			break;
		case 3:
			lgaIdIncrementor = 40;
			lga_resource = getResources().getStringArray(
					R.array.State_3_Lga);
			break;
		case 4:
			lgaIdIncrementor = 71;
			lga_resource = getResources().getStringArray(
					R.array.State_4_Lga);
			break;
		case 5:
			lgaIdIncrementor = 92;
			lga_resource = getResources().getStringArray(
					R.array.State_5_Lga);
			break;
		case 6:
			lgaIdIncrementor = 111;
			lga_resource = getResources().getStringArray(
					R.array.State_6_Lga);
			break;
		case 7:
			lgaIdIncrementor = 119;
			lga_resource = getResources().getStringArray(
					R.array.State_7_Lga);
			break;
		case 8:
			lgaIdIncrementor = 142;
			lga_resource = getResources().getStringArray(
					R.array.State_8_Lga);
			break;
		case 9:
			lgaIdIncrementor = 169;
			lga_resource = getResources().getStringArray(
					R.array.State_9_Lga);
			break;
		case 10:
			lgaIdIncrementor = 187;
			lga_resource = getResources().getStringArray(
					R.array.State_10_Lga);
			break;
		case 11:
			lgaIdIncrementor = 212;
			lga_resource = getResources().getStringArray(
					R.array.State_11_Lga);
			break;
		case 12:
			lgaIdIncrementor = 224;
			lga_resource = getResources().getStringArray(
					R.array.State_12_Lga);
			break;
		case 13:
			lgaIdIncrementor = 239;
			lga_resource = getResources().getStringArray(
					R.array.State_13_Lga);
			break;
		case 14:
			lgaIdIncrementor = 255;
			lga_resource = getResources().getStringArray(
					R.array.State_14_Lga);
			break;
		case 15:
			lgaIdIncrementor = 271;
			lga_resource = getResources().getStringArray(
					R.array.State_15_Lga);
			break;
		case 16:
			lgaIdIncrementor = 282;
			lga_resource = getResources().getStringArray(
					R.array.State_16_Lga);
			break;
		case 17:
			lgaIdIncrementor = 309;
			lga_resource = getResources().getStringArray(
					R.array.State_17_Lga);
			break;
		case 18:
			lgaIdIncrementor = 335;
			lga_resource = getResources().getStringArray(
					R.array.State_18_Lga);
			break;
		case 19:
			lgaIdIncrementor = 358;
			lga_resource = getResources().getStringArray(
					R.array.State_19_Lga);
			break;
		case 20:
			lgaIdIncrementor = 403;
			lga_resource = getResources().getStringArray(
					R.array.State_20_Lga);
			break;
		case 21:
			lgaIdIncrementor = 437;
			lga_resource = getResources().getStringArray(
					R.array.State_21_Lga);
			break;
		case 22:
			lgaIdIncrementor = 458;
			lga_resource = getResources().getStringArray(
					R.array.State_22_Lga);
			break;
		case 23:
			lgaIdIncrementor = 479;
			lga_resource = getResources().getStringArray(
					R.array.State_23_Lga);
			break;
		case 24:
			lgaIdIncrementor = 494;
			lga_resource = getResources().getStringArray(
					R.array.State_24_Lga);
			break;
		case 25:
			lgaIdIncrementor = 514;
			lga_resource = getResources().getStringArray(
					R.array.State_25_Lga);
			break;
		case 26:
			lgaIdIncrementor = 527;
			lga_resource = getResources().getStringArray(
					R.array.State_26_Lga);
			break;
		case 27:
			lgaIdIncrementor = 552;
			lga_resource = getResources().getStringArray(
					R.array.State_27_Lga);
			break;
		case 28:
			lgaIdIncrementor = 572;
			lga_resource = getResources().getStringArray(
					R.array.State_28_Lga);
			break;
		case 29:
			lgaIdIncrementor = 519;
			lga_resource = getResources().getStringArray(
					R.array.State_29_Lga);
			break;
		case 30:
			lgaIdIncrementor = 621;
			lga_resource = getResources().getStringArray(
					R.array.State_30_Lga);
			break;
		case 31:
			lgaIdIncrementor = 653;
			lga_resource = getResources().getStringArray(
					R.array.State_31_Lga);
			break;
		case 32:
			lgaIdIncrementor = 670;
			lga_resource = getResources().getStringArray(
					R.array.State_32_Lga);
			break;
		case 33:
			lgaIdIncrementor = 693;
			lga_resource = getResources().getStringArray(
					R.array.State_33_Lga);
			break;
		case 34:
			lgaIdIncrementor = 716;
			lga_resource = getResources().getStringArray(
					R.array.State_34_Lga);
			break;
		case 35:
			lgaIdIncrementor = 732;
			lga_resource = getResources().getStringArray(
					R.array.State_35_Lga);
			break;
		case 36:
			lgaIdIncrementor = 749;
			lga_resource = getResources().getStringArray(
					R.array.State_36_Lga);
			break;
		case 37:
			lgaIdIncrementor = 764;
			lga_resource = getResources().getStringArray(
					R.array.State_37_Lga);
			break;

		default:
			break;
		}
		
		return lga_resource;
	}

}
