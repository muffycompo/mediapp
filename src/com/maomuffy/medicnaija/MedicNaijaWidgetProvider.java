package com.maomuffy.medicnaija;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

import java.util.List;

public class MedicNaijaWidgetProvider extends AppWidgetProvider {
	private static final String MEDICNAIJA_LOG = "com.maomuffy.medicnaija";

	@Override
	public void onUpdate(Context ct, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		Log.i(MEDICNAIJA_LOG, "Broadcast Sent to MedicNaija Widget Service");

		ComponentName thisWidget = new ComponentName(ct,
				MedicNaijaWidgetProvider.class);

		// Get all ids
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

		// Build the intent to call the service
		Intent intent = new Intent(ct.getApplicationContext(),
				MedicNaijaUpdateWidgetService.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

		String widgetTextTitle = ct
				.getString(R.string.medicnaija_default_widget_text);

		List<String> cs = new MedicNaijaFeedData(ct).getFeeds();
		TextView tvTitle = new TextView(ct);
		for (String cFeed : cs) {
			tvTitle.append(String.format("%s\n\n", cFeed));
		}

		// Find an elegant way of doing this
		widgetTextTitle = tvTitle.getText().toString();

		for (int widgetId : allWidgetIds) {

			RemoteViews remoteView = new RemoteViews(ct.getPackageName(),
					R.layout.medicnaija_widget_layout);
			// Set Text
			remoteView
					.setTextViewText(R.id.tvMedicnaijaUpdate, widgetTextTitle);

			// Register an onClickListener
			Intent clickIntent = new Intent(ct, MedicNaijaWidgetProvider.class);
			Intent activityIntent = new Intent(ct, MainActivity.class);
			clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
					allWidgetIds);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(ct, 0,
					clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			PendingIntent activityPi = PendingIntent.getActivity(ct, 0, activityIntent, 0);

			remoteView.setOnClickPendingIntent(R.id.ivRefreshWidget,
					pendingIntent);
			remoteView.setOnClickPendingIntent(R.id.ivMedicNaija, activityPi);
			Log.i(MEDICNAIJA_LOG, "Widget Refreshed from Widget Provider");
			appWidgetManager.updateAppWidget(widgetId, remoteView);
		}

		// Update the widgets via the service
		ct.startService(intent);
		// super.onUpdate(context, appWidgetManager, appWidgetIds);
	}

	@Override
	public void onDeleted(Context ct, int[] appWidgetIds) {
		Log.i(MEDICNAIJA_LOG, "MedicNaija Widget Removed");
		super.onDeleted(ct, appWidgetIds);
	}

	@Override
	public void onEnabled(Context ct) {
		Log.i(MEDICNAIJA_LOG, "MedicNaija Widget Enabled");
		super.onEnabled(ct);
	}

}
