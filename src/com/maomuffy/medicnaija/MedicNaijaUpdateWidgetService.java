package com.maomuffy.medicnaija;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MedicNaijaUpdateWidgetService extends Service {
	private static final String MEDICNAIJA_LOG = "com.maomuffy.medicnaija";
	Context ct;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		ct = this.getApplicationContext();

		// Call Feeds API and Populate SQLiteDB
		new aacGetFeedsApiCall().execute();

		Log.i(MEDICNAIJA_LOG, "MedicNaija Widget Service Started");

		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(ct);

		int[] allWidgetIds = intent
				.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

		List<String> cs = new MedicNaijaFeedData(ct).getFeeds();
		TextView tvTitle = new TextView(ct);
		
		for (String cFeed : cs) {
			tvTitle.append(String.format("%s\n\n", cFeed));
		}
		
		String widgetTextTitle = tvTitle.getText().toString();

		for (int widgetId : allWidgetIds) {

			RemoteViews remoteView = new RemoteViews(ct.getPackageName(),
					R.layout.medicnaija_widget_layout);
			if(TextUtils.isEmpty(widgetTextTitle)){
				remoteView.setTextViewText(R.id.tvMedicnaijaUpdate, "\n\n\nTouch the refresh icon to get updates!");
			} else {
				remoteView.setTextViewText(R.id.tvMedicnaijaUpdate, widgetTextTitle);				
			}

			// Register an onClickListener
			Intent clickIntent = new Intent(ct, MedicNaijaWidgetProvider.class);
			Intent activityIntent = new Intent(ct, MainActivity.class);
			clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
					allWidgetIds);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(ct, 0,
					clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			PendingIntent activityPi = PendingIntent.getActivity(ct, 0,
					activityIntent, 0);
			remoteView.setOnClickPendingIntent(R.id.ivRefreshWidget,
					pendingIntent);
			remoteView.setOnClickPendingIntent(R.id.ivMedicNaija, activityPi);
			Log.i(MEDICNAIJA_LOG, "Widget Refreshed from Service");
			appWidgetManager.updateAppWidget(widgetId, remoteView);
		}
		stopSelf(startId);
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	// RSS Feeds AsyncTask
	private class aacGetFeedsApiCall extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... arg0) {

			try {
				HttpClient httpclient = new DefaultHttpClient();
				/* API URL */
				// HttpGet httpget = new HttpGet(
				// "http://10.0.2.2/aac_api/public/home/feeds");
				HttpGet httpget = new HttpGet(
						"http://aacapi.betatesting.com.ng/home/feeds");
				HttpResponse httpResponse = httpclient.execute(httpget);
				MedicNaijaUtils ists = new MedicNaijaUtils();
				String str = ists.inputStreamToString(
						httpResponse.getEntity().getContent()).toString();
				// Return result as string
				return str;
			} catch (ClientProtocolException e) {
				return null;
			} catch (IOException e) {
				return null;
			}
		}

		@Override
		protected void onPostExecute(String r) {
			super.onPostExecute(r);

			Context ct = getApplicationContext();

			// Parse JSON from API Response
			try {
				if( r != null ){
					JSONObject json = new JSONObject(r);
					JSONArray jA = json.getJSONArray("data");
	
					if (json.getString("status").toLowerCase(Locale.getDefault())
							.equalsIgnoreCase("success")) {
						// Get Data object
						MedicNaijaFeedData mFeed = new MedicNaijaFeedData(ct);
						// Reset SQLiteDB Table
						mFeed.resetFeedsDbTable();
						Feeds inFeeds = new Feeds();
						for (int i = 0; i < jA.length(); ++i) {
							try {
								inFeeds.setTitle(jA.getJSONObject(i).getString(
										"title"));
								inFeeds.setDescription(jA.getJSONObject(i)
										.getString("description"));
								inFeeds.setLink(jA.getJSONObject(i).getString(
										"link"));
								inFeeds.setPubDate(jA.getJSONObject(i).getString(
										"pubDate"));
	
								// Insert Into SQLiteDB
								mFeed.insertFeed(inFeeds);
	
							} catch (JSONException e) {
								return;
							}
						}
	
					} else {
						Log.i(MEDICNAIJA_LOG, json.getString("message"));
					}
				} else {
					Log.i(MEDICNAIJA_LOG, "null was received from JSON API Call");
				}
			} catch (JSONException e) {
				return;
			}
		}
	}

}
