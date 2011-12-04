package com.wozia.nophonezonelite;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;


public class SponsorsActivity extends Activity {
	static ImageView sps_viaazul;
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_sponsors);
        
        sps_viaazul = (ImageView) findViewById(R.id.sponsor_img_viaazul);
        //sps_viaazul.setClickable(true);
        sps_viaazul.setOnClickListener(new goToURL());
	}
	
	class goToURL implements ImageView.OnClickListener {
    	public void onClick (View v) {
    		String theURL = "";
    		int id = v.getId();
    		if (id == sps_viaazul.getId()) {
	    		theURL = "http://www.viaazul.org";
    		}
    		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(theURL));
    		startActivity(i);
    	}
    }
	
	/** Override Functions **/
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, NoPhoneZone.QUIT_APP, Menu.NONE, R.string.menu_exit)
		.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
	
		return(super.onCreateOptionsMenu(menu));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case NoPhoneZone.QUIT_APP:
				SettingsActivity.setVolume(getApplicationContext(), false);
				NoPhoneZone.dh.clearWarnings();
				this.finish();
				System.exit(RESULT_OK);
			return(true);
		}
	
		return(super.onOptionsItemSelected(item));
	}
}