package com.maomuffy.medicnaija;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class DashboardActivity extends Activity implements OnClickListener {

	ImageView ivlogout, ivProile, ivPD, ivBp, ivHealthTips, ivMaps;
	TextView tvName;
	Toast toast;
	private final static int EXIT_CODE = 1;
	AlertDialog aDialog;

    // flag for Internet connection status
    Boolean isInternetAvailable = false;
    NetworkConnectionDetector con;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);
		
		String footer = new MedicNaijaUtils().footer_text();
		TextView tvFooter = (TextView) findViewById(R.id.tvFooter);
		tvFooter.setText(footer);

        // creating connection detector class instance
        con = new NetworkConnectionDetector(getApplicationContext());

		SharedPreferences sharedPref = getSharedPreferences(
				"com.maomuffy.medicnaija.userprofilepref", MODE_PRIVATE);
		int isLoggedIn = sharedPref.getInt("isLoggedIn", 0);
		
		if (isLoggedIn == 1) {
			// Check if User is Logged In

			// Set Dashboard Name
			ImageView ivLogout = (ImageView) findViewById(R.id.ivLogout);
			ImageView ivProfile = (ImageView) findViewById(R.id.ivProfile);
			// Button ivDemo = (Button)findViewById(R.id.ivDemo);
			ImageView ivBp = (ImageView) findViewById(R.id.ivBp);
			ImageView ivHealthTips = (ImageView) findViewById(R.id.ivHealthTips);
			ImageView ivMaps = (ImageView) findViewById(R.id.ivMaps);
			ImageView ivPD = (ImageView) findViewById(R.id.ivDiseases);

			ivLogout.setOnClickListener(this);
			ivProfile.setOnClickListener(this);
			ivBp.setOnClickListener(this);
			ivHealthTips.setOnClickListener(this);
			ivMaps.setOnClickListener(this);
			ivPD.setOnClickListener(this);

		}
	}

	@Override
	public void onClick(View v) {
		// Switch between the different button events by ID
		switch (v.getId()) {
		case R.id.ivLogout:
			// Logout
			// Destroy Shared Preferences
			SharedPreferences sharedPref = getSharedPreferences(
					"com.maomuffy.medicnaija.userprofilepref", 0);
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.clear(); // clear all data.
			// editor.remove("name");//its remove name field from your
			// SharedPreferences
			editor.commit(); // Don't forgot to commit SharedPreferences.
			finish();
			Intent im = new Intent(this, MainActivity.class);
			startActivity(im);
			break;
		case R.id.ivProfile:
			// Load Profile Activity
			Intent ip = new Intent(this, ProfileActivity.class);
			startActivity(ip);
			break;
		case R.id.ivMaps:
            isInternetAvailable = con.isNetworkEnabled();
            if( isInternetAvailable ){
                // Load Profile Activity
                Intent imap = new Intent(this, MapsActivity.class);
                startActivity(imap);
            } else {
                con.showDialog(this, "Network Connection", "We Need Internet Access!", isInternetAvailable);
            }
            break;
		case R.id.ivBp:
            isInternetAvailable = con.isNetworkEnabled();
            if( isInternetAvailable ){
                // Load Profile Activity
                Intent bpchecker = new Intent(this, BpCheckerActivity.class);
                startActivity(bpchecker);
            } else {
                con.showDialog(this, "Network Connection", "We Need Internet Access!", isInternetAvailable);
            }
			break;
		case R.id.ivHealthTips:
            isInternetAvailable = con.isNetworkEnabled();
            if( isInternetAvailable ){
                // Load Profile Activity
                Intent ihealthtip = new Intent(this, HealthTipsCategoryActivity.class);
                startActivity(ihealthtip);
            } else {
                con.showDialog(this, "Network Connection", "We Need Internet Access!", isInternetAvailable);
            }
			break;
		case R.id.ivDiseases:
            isInternetAvailable = con.isNetworkEnabled();
            if( isInternetAvailable ){
                // Load Profile Activity
                Intent ipd = new Intent(this, ProminentDiseaseActivity.class);
                startActivity(ipd);
            } else {
                con.showDialog(this, "Network Connection", "We Need Internet Access!", isInternetAvailable);
            }
			break;
		default:
			break;
		}

	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	super.onKeyDown(keyCode, event);
	    	if (keyCode == KeyEvent.KEYCODE_BACK) {
	    		
	    		AlertDialog.Builder dBuilder = new AlertDialog.Builder(this);
				dBuilder.setMessage("Do you want to Exit?");
				dBuilder.setCancelable(false);
				dBuilder.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// Work on Exit Code
								
								setResult(EXIT_CODE, null);
					    		DashboardActivity.this.finish();
							}
	
						});
				dBuilder.setNegativeButton("No", new DialogInterface.OnClickListener(){
		            @Override
					public void onClick(DialogInterface d, int arg1) {
		                //Cancel Dialog
		            	
		            	d.cancel();
		            }
		        });
				aDialog = dBuilder.create();
				aDialog.show();
				return true;

	    	}
	    	
	    	return false;
	    }
	
	
	@Override
	public void onPause() {
	    super.onPause();
	    if(aDialog != null){
	        aDialog.dismiss();
	    }
	}
}
