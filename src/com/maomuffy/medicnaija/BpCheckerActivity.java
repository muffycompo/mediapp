package com.maomuffy.medicnaija;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

public class BpCheckerActivity extends Activity implements OnClickListener {

	EditText etSystolic, etDiastolic;
	Button btCheckBp;
	ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bp);
		
		String footer = new MedicNaijaUtils().footer_text();
		TextView tvFooter = (TextView) findViewById(R.id.tvFooter);
		tvFooter.setText(footer);

		btCheckBp = (Button) findViewById(R.id.btCheckBp);
		etSystolic = (EditText) findViewById(R.id.etSystolic);
		etDiastolic = (EditText) findViewById(R.id.etDiastolic);

		btCheckBp.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		String systolic = etSystolic.getText().toString();
		String diastolic = etDiastolic.getText().toString();

		new aacBpApiCall().execute(systolic, diastolic);
	}

	private class aacBpApiCall extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			// Detach operation from UI thread

			try {
				// Create a new HttpClient and Post Header
				HttpClient httpclient = new DefaultHttpClient();
				/* API URL */
				// HttpPost httppost = new
				// HttpPost("http://10.0.2.2/aac_api/public/api/v1/bp/" +
				// params[0] + "/" + params[1]);

				HttpGet httpget = new HttpGet(
						"http://aacapi.betatesting.com.ng/api/v1/bp/"
								+ params[0] + "/" + params[1]);

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httpget);
				MedicNaijaUtils ists = new MedicNaijaUtils();
				String str = ists.inputStreamToString(
						response.getEntity().getContent()).toString();
				// Return result as string
				return str;

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(BpCheckerActivity.this);
			// pDialog.setTitle("Sync");
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
					String bpStatus = data.getString("bpstatus");
					String bpCode = data.getString("bpcode");
					String bpBpRecommenrdations = data.getString("bprecommendation");

					Intent i = new Intent(ct, BpCheckerDetailsActivity.class);
					i.putExtra("bpstatus", bpStatus);
					i.putExtra("bpcode", bpCode);
					i.putExtra("bprecommendations", bpBpRecommenrdations);
					startActivity(i);

					pDialog.dismiss();

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

}
