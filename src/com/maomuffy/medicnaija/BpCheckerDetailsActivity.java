package com.maomuffy.medicnaija;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class BpCheckerDetailsActivity extends Activity {

	TextView tvBpStatus, tvRecommendations;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bp_feedback);
		
		String footer = new MedicNaijaUtils().footer_text();
		TextView tvFooter = (TextView) findViewById(R.id.tvFooter);
		tvFooter.setText(footer);

		// Select Item be Id
		tvBpStatus = (TextView)findViewById(R.id.tvBpStatus);
		tvRecommendations = (TextView)findViewById(R.id.tvRecommendations);

		
		// Get Extra Information from Previous Intent
		String bpStatus = getIntent().getExtras().getString("bpstatus");
		String bpRecommendations = getIntent().getExtras().getString("bprecommendations");
		Spanned bpTreament = Html.fromHtml(bpRecommendations);
		
		// Set Text to new call
		tvBpStatus.setText(bpStatus);
		tvRecommendations.setText(bpTreament);
		// To enable clickable Links in TextView Object, you must set the method below
		tvRecommendations.setMovementMethod(LinkMovementMethod.getInstance());
		
		
	}

}
