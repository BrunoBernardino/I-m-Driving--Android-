package com.wozia.nophonezonefree;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
//import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends Activity {
	static Button bt_modify;
	static CheckBox cb_silent_mode, cb_reply_only_contacts, cb_less_warnings;
	static EditText entry_txt;
	static DataHelper dh;
	static final int PICK_CONTACT = 0;
	static final int PICK_PVT = 1;
	static int selectedUsr = -1;
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_settings);
        
        entry_txt = (EditText) findViewById(R.id.entry_txt);
        bt_modify = (Button) findViewById(R.id.bt_modify);
        cb_silent_mode = (CheckBox) findViewById(R.id.cb_silent_mode);
        cb_reply_only_contacts = (CheckBox) findViewById(R.id.cb_reply_only_contacts);
        cb_less_warnings = (CheckBox) findViewById(R.id.cb_less_warnings);
        
        bt_modify.setOnClickListener(new saveResponse());
        cb_silent_mode.setOnClickListener(new saveSilentMode());
        cb_reply_only_contacts.setOnClickListener(new saveReplyContactsMode());
        cb_less_warnings.setOnClickListener(new saveLessWarningsMode());
        
        dh = new DataHelper(getApplicationContext());
        
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);//-- This disables the keyboard on start
        
        /** Start Getting and Setting Data from Database **/
        String tmp = dh.get("entry_txt");
        if (tmp.length() == 0) {
        	dh.set("entry_txt",entry_txt.getText().toString());
        } else {
        	entry_txt.setText(tmp);
        	dh.set("entry_txt",entry_txt.getText().toString());
        }
        
        tmp = dh.get("silent");
        if (tmp.length() == 0) {
        	if (cb_silent_mode.isChecked()) {
        		dh.set("silent","true");
        		setVolume(getApplicationContext(),true);
        	} else {
        		dh.set("silent","false");
        		setVolume(getApplicationContext(),false);
        	}
        } else {
        	if (tmp.equalsIgnoreCase("true")) {
        		cb_silent_mode.setChecked(true);
        		setVolume(getApplicationContext(),true);
        	} else {
        		cb_silent_mode.setChecked(false);
        		setVolume(getApplicationContext(),false);
        	}
        }
        
        tmp = dh.get("contacts_only");
        if (tmp.length() == 0) {
        	if (cb_reply_only_contacts.isChecked()) {
        		dh.set("contacts_only","true");
        	} else {
        		dh.set("contacts_only","false");
        	}
        } else {
        	if (tmp.equalsIgnoreCase("true")) {
        		cb_reply_only_contacts.setChecked(true);
        	} else {
        		cb_reply_only_contacts.setChecked(false);
        	}
        }
        
        tmp = dh.get("less_warnings");
        if (tmp.length() == 0) {
        	if (cb_less_warnings.isChecked()) {
        		dh.set("less_warnings","true");
        	} else {
        		dh.set("less_warnings","false");
        	}
        } else {
        	if (tmp.equalsIgnoreCase("true")) {
        		cb_less_warnings.setChecked(true);
        	} else {
        		cb_less_warnings.setChecked(false);
        	}
        }
        
        /** End Getting and Setting Data from Database **/
        
	}
	
	/** Useful Functions **/
	public static void setVolume(Context context, boolean mute) {
    	AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		/*audioManager.setStreamMute(AudioManager.STREAM_MUSIC, mute);
		audioManager.setStreamMute(AudioManager.STREAM_RING, mute);
		audioManager.setStreamMute(AudioManager.STREAM_ALARM, mute);
		audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, mute);
		audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, mute);
		audioManager.setStreamMute(AudioManager.STREAM_VOICE_CALL, mute);*/
		if (mute) {
			audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);
			audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_OFF);
			audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		} else {
			audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ON);
			audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION, AudioManager.VIBRATE_SETTING_ON);
			audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
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
				dh.clearWarnings();
				this.finish();
				System.exit(RESULT_OK);
			return(true);
		}
	
		return(super.onOptionsItemSelected(item));
	}
	
	/** Save Functions **/
	
	class saveResponse implements Button.OnClickListener {
    	public void onClick (View v) {
    		dh.set("entry_txt",entry_txt.getText().toString());
    		Toast.makeText(getApplicationContext(), getString(R.string.tab_settings_reply_saved_toast), Toast.LENGTH_SHORT).show();
    	}
    }
    
    class saveSilentMode implements View.OnClickListener {
    	public void onClick (View v) {
            if (cb_silent_mode.isChecked()) {
            	dh.set("silent","true");
            	setVolume(getApplicationContext(),true);
                Toast.makeText(getApplicationContext(), getString(R.string.tab_settings_silent_on_toast), Toast.LENGTH_SHORT).show();
            } else {
            	dh.set("silent","false");
            	setVolume(getApplicationContext(),false);
                Toast.makeText(getApplicationContext(), getString(R.string.tab_settings_silent_off_toast), Toast.LENGTH_SHORT).show();
            }
    	}
    }
    
    class saveReplyContactsMode implements View.OnClickListener {
    	public void onClick (View v) {
            if (cb_reply_only_contacts.isChecked()) {
            	dh.set("contacts_only","true");
                Toast.makeText(getApplicationContext(), getString(R.string.tab_settings_reply_contacts_on_toast), Toast.LENGTH_SHORT).show();
            } else {
            	dh.set("contacts_only","false");
                Toast.makeText(getApplicationContext(), getString(R.string.tab_settings_reply_contacts_off_toast), Toast.LENGTH_SHORT).show();
            }
    	}
    }
    
    class saveLessWarningsMode implements View.OnClickListener {
    	public void onClick (View v) {
            if (cb_less_warnings.isChecked()) {
            	dh.set("less_warnings","true");
                Toast.makeText(getApplicationContext(), getString(R.string.tab_settings_less_warnings_on_toast), Toast.LENGTH_SHORT).show();
            } else {
            	dh.set("less_warnings","false");
                Toast.makeText(getApplicationContext(), getString(R.string.tab_settings_less_warnings_off_toast), Toast.LENGTH_SHORT).show();
            }
    	}
    }
}