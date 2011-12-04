package com.wozia.nophonezonelite;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class AboutActivity extends Activity {
	Button bt_know_more;
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_about);
        
        bt_know_more = (Button) findViewById(R.id.bt_know_more);
        
        bt_know_more.setOnClickListener(new goToKnowMore());
	}
	
	class goToKnowMore implements Button.OnClickListener {
    	public void onClick (View v) {
    		Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.oprah.com/packages/no-phone-zone.html"));
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