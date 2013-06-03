package com.maomuffy.medicnaija;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HealthTipsCategoryActivity extends Activity {

	ListView lvTipCategory;
	ProgressDialog pDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tips_category);
		
		String footer = new MedicNaijaUtils().footer_text();
		TextView tvFooter = (TextView) findViewById(R.id.tvFooter);
		tvFooter.setText(footer);

		lvTipCategory = (ListView) findViewById(R.id.lvTipCategory);

		// Hit API
		new aacGetTipApiCall().execute();
	}

	private class aacGetTipApiCall extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... arg0) {

			try {
				HttpClient httpclient = new DefaultHttpClient();
				/* API URL */
//				HttpGet httpget = new HttpGet(
//						"http://10.0.2.2/aac_api/public/api/v1/tip/category");
				HttpGet httpget = new HttpGet(
						"http://aacapi.betatesting.com.ng/api/v1/tip/category");
				HttpResponse httpResponse = httpclient.execute(httpget);
				MedicNaijaUtils ists = new MedicNaijaUtils();
				String str = ists.inputStreamToString(
						httpResponse.getEntity().getContent()).toString();
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
			
			
			pDialog = new ProgressDialog(HealthTipsCategoryActivity.this);
			//pDialog.setTitle("Sync");
			pDialog.setMessage("Loading...");
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
			super.onPostExecute(r);

			Context ct = getApplicationContext();

			// Parse JSON from API Response
			try {
				JSONObject json = new JSONObject(r);
				JSONArray jsonArray = json.getJSONArray("data");

				if (json.getString("status").toLowerCase(Locale.getDefault())
						.equalsIgnoreCase("success")) {
					// Get Data object

					renderViewFromAdapter(ct, jsonArray);

				} else {
					// Notify User of Failure
					Toast toast = Toast.makeText(ct, json.getString("message"),
							Toast.LENGTH_SHORT);
					toast.show();
				}
			} catch (JSONException e) {
				Toast.makeText(ct, "Network error...", Toast.LENGTH_SHORT).show();
			}
			
			pDialog.dismiss();
		}

		public void renderViewFromAdapter(Context c, JSONArray jA) {
			List<String> items = new ArrayList<String>(jA.length());
			final Integer[] idItems = new Integer[jA.length()];

			for (int i = 0; i < jA.length(); ++i) {
				try {
					String dName = jA.getJSONObject(i)
							.getString("category_title");
					int dId = jA.getJSONObject(i).getInt("id");
					items.add(dName);
					idItems[i] = dId;
				} catch (JSONException e) {
					Toast.makeText(getApplicationContext(), "Network error...", Toast.LENGTH_SHORT).show();
				}
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(c,
					R.layout.custom_listview_text_color, R.id.aac_custom_liststyle, items);
			lvTipCategory.setAdapter(adapter);
			lvTipCategory.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View v,
						int pos, long id) {
					String tipId = prepareDiseaseId(idItems, pos);
					new aacPostTipApiCall().execute(tipId);
				}
			});
		}
		
		public String prepareDiseaseId(Integer[] s, Integer pos){
			return s[pos] + "";
		}
	}

private class aacPostTipApiCall extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... cat_id) {

			try {
				HttpClient httpclient = new DefaultHttpClient();
				/* API URL */
//				HttpGet httpget = new HttpGet(
//						"http://10.0.2.2/aac_api/public/api/v1/tip/10/" + cat_id[0]);
				HttpGet httpget = new HttpGet(
						"http://aacapi.betatesting.com.ng/api/v1/tip/10/" + cat_id[0]);
				HttpResponse httpResponse = httpclient.execute(httpget);
				MedicNaijaUtils ists = new MedicNaijaUtils();
				String str = ists.inputStreamToString(
						httpResponse.getEntity().getContent()).toString();
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
		protected void onPostExecute(String r) {
			super.onPostExecute(r);

			Context ct = getApplicationContext();

			// Parse JSON from API Response
			try {
				JSONObject json = new JSONObject(r);
				JSONArray jsonArray = json.getJSONArray("data");

				if (json.getString("status").toLowerCase(Locale.getDefault())
						.equalsIgnoreCase("success")) {
					// Get Data object
					
					Intent tipIntent = new Intent(ct, HealthTipsActivity.class);
					tipIntent.putExtra("health_tip", jsonArray.toString());
					startActivity(tipIntent);

				} else {
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

}
