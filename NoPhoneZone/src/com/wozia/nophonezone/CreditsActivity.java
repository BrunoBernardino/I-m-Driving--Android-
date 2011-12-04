package com.wozia.nophonezone;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class CreditsActivity extends Activity {
	static ImageView logo_wozia;
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_credits);
        
        TextView t = (TextView) findViewById(R.id.label_version_desc);
        String txt = "";
        
        txt = NoPhoneZone.APP_VERSION + " - " + t.getText().toString() + " Wozia\nwww.imdrivingapp.net";
        
        t.setText(txt);
        
        logo_wozia = (ImageView) findViewById(R.id.credits_logo);
        //logo_wozia.setClickable(true);
        logo_wozia.setOnClickListener(new goToURL());
	}
	
	class goToURL implements ImageView.OnClickListener {
    	public void onClick (View v) {
    		String theURL = "http://www.imdrivingapp.net";
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