package com.maomuffy.medicnaija;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ProfileActivity extends Activity implements OnClickListener {
	
	TextView tvEmail,tvSurname,tvFirstName,tvGsm,tvStates,tvLga;
	Button btEditProfile;
	String[] state_array, lga_array;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		
		String footer = new MedicNaijaUtils().footer_text();
		TextView tvFooter = (TextView) findViewById(R.id.tvFooter);
		tvFooter.setText(footer);

		// Refresh Content
		refreshProfile();
		btEditProfile = (Button)findViewById(R.id.btEditProfile);
		btEditProfile.setOnClickListener(this);
	}
	
	public void refreshProfile(){
		SharedPreferences sharedPref = getSharedPreferences("com.maomuffy.medicnaija.userprofilepref", MODE_PRIVATE);
		int isLoggedIn = sharedPref.getInt("isLoggedIn", 0);
		if( isLoggedIn == 1 ){
			// Check if User is Logged In
			
			String surname = sharedPref.getString("surname", null);
			String firstname = sharedPref.getString("firstname", null);
			String email = sharedPref.getString("email", null);
			String gsm = sharedPref.getString("gsm", null);
			int state_id = sharedPref.getInt("state_id", 0);
			int lga_id = sharedPref.getInt("lga_id", 0);
			
			tvEmail = (TextView)findViewById(R.id.tvEmail);
			tvSurname = (TextView)findViewById(R.id.tvSurname);
			tvFirstName = (TextView)findViewById(R.id.tvFirstName);
			tvGsm = (TextView)findViewById(R.id.tvGsm);
			tvStates = (TextView)findViewById(R.id.tvStates);
			tvLga = (TextView)findViewById(R.id.tvLga);
			
			tvEmail.setText(email);
			tvSurname.setText(surname);
			tvFirstName.setText(firstname);
			tvGsm.setText(gsm);
			tvStates.setText(getStateName(state_id));
			tvLga.setText(getLgaName(lga_id, state_id));
		}
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent i = new Intent(this, ProfileEditActivity.class);
		startActivity(i);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refreshProfile();
		
	}
	
	public String getStateName(Integer si){
		state_array = getResources().getStringArray(R.array.States);
		return state_array[si - 1];
	}
	
	public String getLgaName(Integer li, Integer si){
		final String rname;
		final int rs;
		final int lc;
		lc = new MedicNaijaUtils().selectedLgaId(li);
		rname = "State_" + si + "_Lga";
		rs = getResources().getIdentifier(rname, "array", this.getPackageName());
		lga_array = getResources().getStringArray(rs);
		return lga_array[lc];
	}
	
	
	
}
