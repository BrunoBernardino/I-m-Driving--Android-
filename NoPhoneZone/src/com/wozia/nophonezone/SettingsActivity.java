package com.wozia.nophonezone;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.Contacts;
//import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity {
	static Button bt_modify, bt_private_add, bt_private_remove;
	static CheckBox cb_silent_mode, cb_reply_only_contacts, cb_less_warnings;
	static EditText entry_txt;
	static TextView label_private_lbl;
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
        bt_private_add = (Button) findViewById(R.id.bt_private_add);
        bt_private_remove = (Button) findViewById(R.id.bt_private_remove);
        cb_silent_mode = (CheckBox) findViewById(R.id.cb_silent_mode);
        cb_reply_only_contacts = (CheckBox) findViewById(R.id.cb_reply_only_contacts);
        cb_less_warnings = (CheckBox) findViewById(R.id.cb_less_warnings);
        label_private_lbl = (TextView) findViewById(R.id.label_private_lbl);
        
        bt_modify.setOnClickListener(new saveResponse());
        bt_private_add.setOnClickListener(new addPrivateGroup());
        bt_private_remove.setOnClickListener(new removePrivateGroup());
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
        
        updatePrivateGroupLabel(getApplicationContext());
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
	
	public static void updatePrivateGroupLabel(Context context) {
		List<String> contacts = dh.getAllContacts();
	    StringBuilder sb = new StringBuilder();
	    sb.append(context.getResources().getString(R.string.tab_settings_private_group_pre) + "\n");//-- TODO here
	    for (String contact : contacts) {
	       sb.append(contact + "\n");
	    }
	    TextView tmp = label_private_lbl;
	    if (contacts.size() > 0) {
	    	tmp.setText(sb.toString());
	    } else {
	    	tmp.setText(context.getResources().getString(R.string.tab_settings_private_group_empty));
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

	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);
		
		switch (reqCode) {
			case (PICK_CONTACT) :
				if (resultCode == Activity.RESULT_OK) {
					Uri contactData = data.getData();
					Cursor c =  managedQuery(contactData, null, null, null, null);
					ContentResolver cr = getContentResolver();
					if (c.moveToFirst()) {
						String id = c.getString(c.getColumnIndexOrThrow(Contacts._ID));
						String name = c.getString(c.getColumnIndexOrThrow(Contacts.DISPLAY_NAME));
						ArrayList<String> number = new ArrayList<String>();
						if (Integer.parseInt(c.getString(c.getColumnIndex(Contacts.HAS_PHONE_NUMBER))) > 0) {
							Cursor pCur = cr.query(CommonDataKinds.Phone.CONTENT_URI, null, 
						 	CommonDataKinds.Phone.CONTACT_ID +" = ?", 
						 	new String[]{id}, null);
							String tmp = "";
						 	while (pCur.moveToNext()) {
						 		tmp = pCur.getString(pCur.getColumnIndex(CommonDataKinds.Phone.NUMBER));
						 		if (tmp.length() > 0) {
						 			number.add(tmp);
						 		}
						 	}
						 	pCur.close();
						}
						
						int i = 0;
						String tmpNum ="";
						for (i=0;i<number.size();i++) {
							tmpNum = NoPhoneZone.parsePhoneNumber(number.get(i));
							String[] tmp = { "", ""};
							tmp = dh.getContact(tmpNum);
							if (tmp[0].length() == 0 && tmp[1].length() == 0) {
								dh.addContact(name, tmpNum);
								//Log.v("nophonezone","contact added: " + name + " (" + number.get(i) + ")");
								Toast.makeText(getApplicationContext(), getString(R.string.tab_settings_pvt_add_toast), Toast.LENGTH_SHORT).show();
							} else {
								//Log.v("nophonezone","contact already exists");
								Toast.makeText(getApplicationContext(), getString(R.string.tab_settings_pvt_exists_toast), Toast.LENGTH_SHORT).show();
							}
						}
						
						updatePrivateGroupLabel(getApplicationContext());
						//Log.v("nophonezone", "contact selected: " + name + " (" + number.toString() + ")");
						//Toast.makeText(getApplicationContext(), "Contact Added to Private Group: " + name + " (" + number.toString() + ")", Toast.LENGTH_LONG).show();
					}
					
					c.close();
				}
			break;
			case (PICK_PVT) :
				if (resultCode == Activity.RESULT_OK) {
					String[] contact = dh.getContactAt(selectedUsr);
					final String phoneNumber = contact[1];
					//Log.v("nophonezone", "number selected: " + selectedUsr + " (" + phoneNumber + ")");
					new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle(getString(R.string.tab_settings_dialog_remove_title)).setMessage(String.format(getString(R.string.tab_settings_dialog_remove_msg), contact[0], contact[1])).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
			            @Override
			            public void onClick(DialogInterface dialog, int which) {
			            	dh.deleteContact(phoneNumber);
			            	Toast.makeText(getApplicationContext(), getString(R.string.tab_settings_dialog_remove_toast), Toast.LENGTH_SHORT).show();
			            	updatePrivateGroupLabel(getApplicationContext());
			            }

			        }).setNegativeButton(getString(R.string.no), null).show();
			        //selectedUsr = -1;
				}
			break;
		}
	}
	
	/** Save Functions **/
	
	class saveResponse implements Button.OnClickListener {
    	public void onClick (View v) {
    		dh.set("entry_txt",entry_txt.getText().toString());
    		Toast.makeText(getApplicationContext(), getString(R.string.tab_settings_reply_saved_toast), Toast.LENGTH_SHORT).show();
    	}
    }
    
    class addPrivateGroup implements Button.OnClickListener {
    	public void onClick (View v) {
    		Intent i = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
    		startActivityForResult(i, PICK_CONTACT);
    	}
    }
    
    class removePrivateGroup implements Button.OnClickListener {
    	public void onClick (View v) {
    		Intent i = new Intent().setClass(getApplicationContext(), PrivateGroup.class);
    		startActivityForResult(i, PICK_PVT);
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