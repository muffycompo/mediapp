package com.maomuffy.medicnaija;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Locale;

public class SpecialistMapActivity extends FragmentActivity {
	GoogleMap map;
	ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_specialists_map);
		
		String footer = new MedicNaijaUtils().footer_text();
		TextView tvFooter = (TextView) findViewById(R.id.tvFooter);
		tvFooter.setText(footer);
		
		GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getApplicationContext());

		GooglePlayServicesUtil
				.getOpenSourceSoftwareLicenseInfo(getApplicationContext());

		 map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.specialistMap)).getMap();

		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		map.getUiSettings().setZoomControlsEnabled(false);
				
		String disease_name = getIntent().getExtras().getString("disease_name");
		int state_id = getIntent().getExtras().getInt("state_id");
		int lga_id = getIntent().getExtras().getInt("lga_id");

		Log.i("AAC_MAP", disease_name);
		
		new mapSpecialistApiCall().execute(disease_name, lga_id + "", state_id + "");

		//map.setMyLocationEnabled(true);
		map.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater())); // Set InfoWindow
		
	}

	@SuppressWarnings("unused")
	private void addMarker(GoogleMap map, double lat, double lon, int title,
			int snippet) {
		map.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
				.title(getString(title)).snippet(getString(snippet)));
	}

	private void addMarkerWithString(GoogleMap map, double lat, double lon,
			String title, String snippet) {
		map.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
				.title(title).snippet(snippet));
	}

	private class mapSpecialistApiCall extends
			AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... url_param) {

			try {
				HttpClient httpclient = new DefaultHttpClient();
				/* URLEncode Disease Name */
				String name_of_disease = URLEncoder.encode(url_param[0], "UTF-8");
				String dname= name_of_disease.replace("+", "%20");
				
				/* API URL */
//				HttpGet httpget = new HttpGet(
//						"http://10.0.2.2/aac_api/public/api/v1/specialist/map/"
//								+ dname + "/" + url_param[1] + "/"
//								+ url_param[2]);
				 HttpGet httpget = new HttpGet(
				 "http://aacapi.betatesting.com.ng/api/v1/specialist/map/" + dname + "/" + url_param[1] + "/" + url_param[2]);
				 
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
			pDialog = new ProgressDialog(SpecialistMapActivity.this);
			// pDialog.setTitle("Sync");
			pDialog.setMessage("Checking Specialists...");
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
				
				if (json.getString("status").toLowerCase(Locale.getDefault())
						.equalsIgnoreCase("success")) {
					// Get Data object
					JSONArray jsonArray = json.getJSONArray("data");

					for (int i = 0; i < jsonArray.length(); ++i) {
						try {
							// int dId =
							// jsonArray.getJSONObject(i).getInt("id");
							// Specialist Data
							Double lon = Double.parseDouble(jsonArray.getJSONObject(i).getString(
									"location_longitude"));
							Double lat = Double.parseDouble(jsonArray.getJSONObject(i).getString(
									"location_latitude"));
							String specialist_name = jsonArray.getJSONObject(i)
									.getString("specialist_name");
							String specialist_hospital = jsonArray
									.getJSONObject(i).getString(
											"specialist_hospital_name");
							String specialist_address = jsonArray
									.getJSONObject(i).getString(
											"specialist_hostpital_address");
							final String specialist_gsm = jsonArray.getJSONObject(i)
									.getString("specialist_contact_gsm");

							//Log.i("Longitude", lon + "");
							//Log.i("Latitude", lat + "");
							
							addMarkerWithString(map, lon, lat,
									specialist_hospital, specialist_name + "\n"
											+ specialist_address + "\n"
											+ specialist_gsm);
							
							map.setOnInfoWindowClickListener(new OnInfoWindowClickListener(){
								
								@Override
								public void onInfoWindowClick(Marker m) {
									Intent specialistMarkerCallIntent = new Intent(Intent.ACTION_DIAL);
									specialistMarkerCallIntent.setData(Uri.parse("tel:" + specialist_gsm));
									startActivity(specialistMarkerCallIntent);
								}
							});

						} catch (JSONException e) {
							Toast.makeText(ct, "Network error...", Toast.LENGTH_SHORT).show();
						}
					}

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

	}
}
