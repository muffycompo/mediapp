package com.maomuffy.medicnaija;

import android.app.Application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

public class MedicNaijaUtils extends Application {
	
	public MedicNaijaUtils() {
	}
	
	public int selectedLgaId(Integer s) {

		if (s <= 18) {
			return s - 1;
		} else if (s <= 40) {
			return s - 18;
		} else if (s <= 71) {
			return s - 40;
		} else if (s <= 92) {
			return s - 71;
		} else if (s <= 111) {
			return s - 92;
		} else if (s <= 119) {
			return s - 111;
		} else if (s <= 142) {
			return s - 119;
		} else if (s <= 169) {
			return s - 142;
		} else if (s <= 187) {
			return s - 169;
		} else if (s <= 212) {
			return s - 187;
		} else if (s <= 224) {
			return s - 212;
		} else if (s <= 239) {
			return s - 224;
		} else if (s <= 255) {
			return s - 239;
		} else if (s <= 271) {
			return s - 255;
		} else if (s <= 282) {
			return s - 271;
		} else if (s <= 309) {
			return s - 282;
		} else if (s <= 335) {
			return s - 309;
		} else if (s <= 358) {
			return s - 335;
		} else if (s <= 403) {
			return s - 358;
		} else if (s <= 437) {
			return s - 403;
		} else if (s <= 458) {
			return s - 437;
		} else if (s <= 479) {
			return s - 458;
		} else if (s <= 494) {
			return s - 479;
		} else if (s <= 514) {
			return s - 494;
		} else if (s <= 527) {
			return s - 514;
		} else if (s <= 552) {
			return s - 527;
		} else if (s <= 572) {
			return s - 552;
		} else if (s <= 591) {
			return s - 572;
		} else if (s <= 621) {
			return s - 591;
		} else if (s <= 653) {
			return s - 621;
		} else if (s <= 670) {
			return s - 653;
		} else if (s <= 693) {
			return s - 670;
		} else if (s <= 716) {
			return s - 693;
		} else if (s <= 732) {
			return s - 716;
		} else if (s <= 749) {
			return s - 732;
		} else if (s <= 764) {
			return s - 749;
		} else if (s <= 769) {
			return s - 764;
		}
		// Return 0
		return 0;
	}
	
	public StringBuilder inputStreamToString(InputStream is) {
	     String line = "";
	     StringBuilder total = new StringBuilder();
	     // Wrap a BufferedReader around the InputStream
	     BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	     // Read response until the end
	     try {
	      while ((line = rd.readLine()) != null) { 
	        total.append(line); 
	      }
	     } catch (IOException e) {
	      e.printStackTrace();
	     }
	     // Return full string
	     return total;
	    }
	
	public String footer_text(){
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		String f = "Copyright \u00A9 " + year + " - MedicNaija Team";
		// Return current Year
		return f;
		
	}

}
