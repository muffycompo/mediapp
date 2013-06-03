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

public class HealthTipsActivity extends Activity {

	ListView lvTips;
	ProgressDialog pDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tips);
		
		String footer = new MedicNaijaUtils().footer_text();
		TextView tvFooter = (TextView) findViewById(R.id.tvFooter);
		tvFooter.setText(footer);

		lvTips = (ListView) findViewById(R.id.lvTips);

		// Hit API
		new aacGetTipCategoryApiCall().execute();
	}

	private class aacGetTipCategoryApiCall extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			
			Intent ihealthTip = getIntent();
	        String jsonString = ihealthTip.getStringExtra("health_tip");

			return jsonString;
		}

		@Override
		protected void onPostExecute(String r) {
			super.onPostExecute(r);

			Context ct = getApplicationContext();

			// Parse JSON from API Response
			try {
				JSONArray jsonArray = new JSONArray(r);
				
				renderViewFromAdapter(ct, jsonArray);
				
			} catch (JSONException e) {
				Toast.makeText(ct, "Network error...", Toast.LENGTH_SHORT).show();
			}
		}

		public void renderViewFromAdapter(Context c, JSONArray jA) {
			List<String> items = new ArrayList<String>(jA.length());
			final Integer[] idItems = new Integer[jA.length()];

			for (int i = 0; i < jA.length(); ++i) {
				try {
					String dName = jA.getJSONObject(i)
							.getString("tips_title");
					int dId = jA.getJSONObject(i).getInt("id");
					items.add(dName);
					idItems[i] = dId;
				} catch (JSONException e) {
					Toast.makeText(getApplicationContext(), "Network error...", Toast.LENGTH_SHORT).show();
				}
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(c,
					R.layout.custom_listview_text_color, R.id.aac_custom_liststyle, items);
			lvTips.setAdapter(adapter);
			lvTips.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View v,
						int pos, long id) {
					//Log.i("AAC_Click",pos + " - " + id);
					String tipId = prepareTipCategoryId(idItems, pos);
					new aacPostTipCategoryApiCall().execute(tipId);
				}
			});
		}
		
		public String prepareTipCategoryId(Integer[] s, Integer pos){
			return s[pos] + "";
		}
	}
	
private class aacPostTipCategoryApiCall extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... tip_id) {

			try {
				HttpClient httpclient = new DefaultHttpClient();
				/* API URL */
//				HttpGet httpget = new HttpGet(
//						"http://10.0.2.2/aac_api/public/api/v1/tip/single/" + tip_id[0]);
				HttpGet httpget = new HttpGet(
						"http://aacapi.betatesting.com.ng/api/v1/tip/single/" + tip_id[0]);
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
					
					// Get Tip Detail
					String tip_title = jsonArray.getJSONObject(0).getString("tips_title");
					String tip_body = jsonArray.getJSONObject(0).getString("tip_body");
					
					Intent tipDetailIntent = new Intent(ct, HealthTipsDetailActivity.class);
					tipDetailIntent.putExtra("tips_title", tip_title);
					tipDetailIntent.putExtra("tip_body", tip_body);
					startActivity(tipDetailIntent);

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
