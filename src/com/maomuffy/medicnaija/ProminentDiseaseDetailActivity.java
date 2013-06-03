package com.maomuffy.medicnaija;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ProminentDiseaseDetailActivity extends Activity implements OnClickListener {

	TextView tvDiseaseName, tvDiseaseCause, tvDiseasePrevention, tvDiseaseCure, tvDiseaseControl;
	Button btFindSpecialist;
	String name, cause, prevention, cure, control;
	int lga_id, state_id;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_disease_detail);
		
		String footer = new MedicNaijaUtils().footer_text();
		TextView tvFooter = (TextView) findViewById(R.id.tvFooter);
		tvFooter.setText(footer);

		SharedPreferences sharedPref = getSharedPreferences(
				"com.maomuffy.medicnaija.userprofilepref", MODE_PRIVATE);
		lga_id = sharedPref.getInt("lga_id", 0);
		state_id = sharedPref.getInt("state_id", 0);
		
		// Select Item be Id
		tvDiseaseName = (TextView)findViewById(R.id.tvDiseaseName);
		tvDiseaseCause = (TextView)findViewById(R.id.tvDiseaseCause);
		tvDiseasePrevention = (TextView)findViewById(R.id.tvDiseasePrevention);
		tvDiseaseCure = (TextView)findViewById(R.id.tvDiseaseCure);
		tvDiseaseControl = (TextView)findViewById(R.id.tvDiseaseControl);
		btFindSpecialist = (Button)findViewById(R.id.btFindSpecialist);
		
		// Get Extra Information from Previous Intent
		name = getIntent().getExtras().getString("name");
		cause = getIntent().getExtras().getString("causes");
		prevention = getIntent().getExtras().getString("prevention");
		cure = getIntent().getExtras().getString("cure");
		control = getIntent().getExtras().getString("control");
		
		// Set Text to new call
		tvDiseaseName.setText(name);
		tvDiseaseCause.setText(cause);
		tvDiseasePrevention.setText(prevention);
		tvDiseaseCure.setText(cure);
		tvDiseaseControl.setText(control);
		
		btFindSpecialist.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		Intent iSpecialistMap = new Intent(this, SpecialistMapActivity.class);
		iSpecialistMap.putExtra("disease_name", name);
		iSpecialistMap.putExtra("state_id", state_id);
		iSpecialistMap.putExtra("lga_id", lga_id);
		startActivity(iSpecialistMap);
	}

}
