package com.maomuffy.medicnaija;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class HealthTipsDetailActivity extends Activity {

	TextView tvTipTitle, tvTipBody;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tips_detail);
		
		String footer = new MedicNaijaUtils().footer_text();
		TextView tvFooter = (TextView) findViewById(R.id.tvFooter);
		tvFooter.setText(footer);

		// Select Item be Id
		tvTipTitle = (TextView)findViewById(R.id.tvTipTitle);
		tvTipBody = (TextView)findViewById(R.id.tvTipBody);

		
		// Get Extra Information from Previous Intent
		String title = getIntent().getExtras().getString("tips_title");
		String body = getIntent().getExtras().getString("tip_body");
		
		// Set Text to new call
		tvTipTitle.setText(title);
		tvTipBody.setText(body);
		
	}

}
