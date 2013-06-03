package com.maomuffy.medicnaija;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity {

	GoogleMap map;
	ProgressDialog pDialog;
	
	// GPSTracker class
    GPSTracker gps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_maps);
        
        String footer = new MedicNaijaUtils().footer_text();
		TextView tvFooter = (TextView) findViewById(R.id.tvFooter);
		tvFooter.setText(footer);
		
		// create class object
        gps = new GPSTracker(this);

        // check if GPS enabled
        if(gps.canGetLocation()){

        	SharedPreferences sharedPref = getSharedPreferences(
    				"com.maomuffy.medicnaija.userprofilepref", MODE_PRIVATE);
    		String surname = sharedPref.getString("surname", null);
    		String firstname = sharedPref.getString("firstname", null);
        	
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            // Do Map Related Stuff
            
    		GooglePlayServicesUtil
    				.isGooglePlayServicesAvailable(getApplicationContext());

    		GooglePlayServicesUtil
    				.getOpenSourceSoftwareLicenseInfo(getApplicationContext());

    		 map = ((SupportMapFragment) getSupportFragmentManager()
    		 .findFragmentById(R.id.maps)).getMap();

    		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    		map.getUiSettings().setZoomControlsEnabled(false);

    		// Get from GPS
    		 CameraUpdate c =
    		 CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude));
    		 CameraUpdate z = CameraUpdateFactory.zoomTo(18);
    		
    		 map.moveCamera(c);
    		 map.animateCamera(z);

    		// My Location
    		addMarkerWithString(map, latitude, longitude, "You", firstname + " " + surname);

    		map.setMyLocationEnabled(true);
    		map.setInfoWindowAdapter(new PopupAdapter(getLayoutInflater())); // Set InfoWindow
        }else{
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }
		
		
	}
	
	@Override
	protected void onPause() {
		gps.stopUsingGPS();
		super.onPause();
	}

	private void addMarkerWithString(GoogleMap map, double lat, double lon,
			String title, String snippet) {
		map.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
				.title(title).snippet(snippet));
	}
}
