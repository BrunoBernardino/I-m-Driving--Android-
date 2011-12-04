package com.wozia.nophonezonelite;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Date;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import com.android.vending.licensing.LicenseChecker;
import com.android.vending.licensing.LicenseCheckerCallback;
import com.android.vending.licensing.ServerManagedPolicy;
import com.android.vending.licensing.AESObfuscator;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.net.Uri;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
//import android.util.Log;
import android.widget.*;

//-- TODO Known Bugs: none :D

public class NoPhoneZone extends TabActivity {
	static DataHelper dh;
	private static String msgAdd_Footer;
	static final int QUIT_APP = 5;
	static final int NOTIFY_APP = 15;
	static final String APP_VERSION = "1.3.0 Lite";
	static Resources res = null;
	private PhoneReceiver PhoneR;
	private SMSReceiver SmsR;
	static Context context;
	@SuppressWarnings("unused")
	private LicenseCheckerCallback mLicenseCheckerCallback;
    private LicenseChecker mChecker;
    private static final String BASE64_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmyT5FFocrsMF8ixpsC0peGvHJaTr1ZL6JkwGykFjL03GcDJj4jkLw7Rpm2wOliTX3o5Th1G32E/8pg1alAV01FD8IzeE/oEsqTbLqF/3TcKqVe+72QsZ/+eG31vwj+cxVgxEnZsYbfS5BkPEA0V2IeIuQ8AXGsohAMMd/7bSC+IHAQfIeBvThbkmprpGt+JkNgW6oavyaxXuv5jUi2ZqYNE9K5jy4fhEfLRtJKCE9ndkJfd2pj+9nejMJ06iVPVio84TIqhb6N79JOOctIH90A7FZla4ufYCpa+0g5Obay7nd+pqg2Up2aO44xfjahZcevVtTQEQlq1/QaVUr+4M7QIDAQAB";
    private static final byte[] SALT = new byte[] { 68, -46, 30, 118, -103, -13, 74, -64, 57, 88, -93, -45, 85, -117, -36, -113, -11, 22, -64, 19 };
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        
        // Construct the LicenseCheckerCallback. The library calls this when done.
        mLicenseCheckerCallback = new MyLicenseCheckerCallback();

        // Construct the LicenseChecker with a Policy.
        mChecker = new LicenseChecker(this, new ServerManagedPolicy(this, new AESObfuscator(SALT, getPackageName(), deviceId)), BASE64_PUBLIC_KEY );
        
        res = getResources();
        TabHost tabHost = getTabHost();
        NoPhoneZone.context = getApplicationContext();
        
        tabHost.addTab(tabHost.newTabSpec("about").setIndicator(res.getString(R.string.app_tab_title_about), res.getDrawable(R.drawable.ic_tab_about)).setContent(new Intent().setClass(this, AboutActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("settings").setIndicator(res.getString(R.string.app_tab_title_settings), res.getDrawable(R.drawable.ic_tab_settings)).setContent(new Intent().setClass(this, SettingsActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("sponsors").setIndicator(res.getString(R.string.app_tab_title_sponsors), res.getDrawable(R.drawable.ic_tab_sponsors)).setContent(new Intent().setClass(this, SponsorsActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("credits").setIndicator(res.getString(R.string.app_tab_title_credits), res.getDrawable(R.drawable.ic_tab_credits)).setContent(new Intent().setClass(this, CreditsActivity.class)));
        
        /*
        tabHost.addTab(tabHost.newTabSpec("about").setIndicator(res.getString(R.string.app_tab_title_about), res.getDrawable(android.R.drawable.ic_menu_compass)).setContent(new Intent().setClass(this, AboutActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("settings").setIndicator(res.getString(R.string.app_tab_title_settings), res.getDrawable(android.R.drawable.ic_menu_preferences)).setContent(new Intent().setClass(this, SettingsActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("sponsors").setIndicator(res.getString(R.string.app_tab_title_sponsors), res.getDrawable(android.R.drawable.ic_menu_myplaces)).setContent(new Intent().setClass(this, SponsorsActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("credits").setIndicator(res.getString(R.string.app_tab_title_credits), res.getDrawable(android.R.drawable.ic_menu_info_details)).setContent(new Intent().setClass(this, CreditsActivity.class)));
        */
        
        tabHost.setCurrentTab(1);
        
        dh = new DataHelper(getApplicationContext());
        
        msgAdd_Footer = res.getString(R.string.nophonezone_msg_footer);
        
        AlertDialog disclaimer = new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle(getString(R.string.nophonezone_disclaimer_title)).setMessage(getString(R.string.nophonezone_disclaimer_msg)).setPositiveButton(getString(R.string.ok), null).create();
        long cDate = 0;
        Date date = new Date();
        
        cDate = date.getTime();
        
        String tmp = dh.get("disclaimer");
        if (tmp.length() == 0) {
        	dh.set("disclaimer", String.valueOf(cDate));
        	disclaimer.show();
        } else {
        	long oDate = Long.parseLong(tmp);
        	long weekDiff = 1209600000;//604800000;
        	if (cDate - oDate >= weekDiff) {// If Date is bigger than two weeks.
        		dh.set("disclaimer", String.valueOf(cDate));
        		disclaimer.show();
        	}
        }
        
        Eula.show(this);
    }
    
    @Override
    public void onResume() {
    	PhoneR = new PhoneReceiver();
        registerReceiver(PhoneR, new IntentFilter("android.intent.action.PHONE_STATE"));
        
        SmsR = new SMSReceiver();
        registerReceiver(SmsR, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
        
        NotificationManager notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notifyManager.cancel(NOTIFY_APP);
        
        super.onResume();
    }
    
    @Override
    public void onStop() {
    	unregisterReceiver(PhoneR);
    	unregisterReceiver(SmsR);
    	
    	NotificationManager notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification note = new Notification(R.drawable.icon, getString(R.string.nophonezone_notification_title), System.currentTimeMillis());
		
		note.flags |= Notification.FLAG_ONGOING_EVENT;
		note.flags |= Notification.FLAG_NO_CLEAR;
		
		Intent intent = new Intent(this, NoPhoneZone.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		 
		PendingIntent pintent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		note.setLatestEventInfo(this, getString(R.string.nophonezone_notification_title), getString(R.string.nophonezone_notification_msg), pintent);
		 
		notifyManager.notify(NOTIFY_APP, note);
    	
    	super.onStop();
    }
    
    /** Useful functions **/
    
	public static void sendMessage(Context context, String PhoneNumber) {
		SmsManager sm = SmsManager.getDefault();
		
		//context = NoPhoneZone.context;
		
		String isContacts = dh.get("contacts_only");
		String lessWarnings = dh.get("less_warnings");
		//String msgText = dh.get("entry_txt");
		String msgText = SettingsActivity.entry_txt.getText().toString();
		boolean sendMsg = false;
		
		if (isContacts.equalsIgnoreCase("true")) {
			if (contactExists(context, PhoneNumber)) sendMsg = true;
		} else {
			sendMsg = true;
		}
		
		//-- Less Warnings
		if (lessWarnings.equalsIgnoreCase("true") && sendMsg && dh.warningExists(PhoneNumber, 60)) {
			sendMsg = false;
		}
		dh.addWarning(PhoneNumber);
		
		msgText += "\n" + NoPhoneZone.msgAdd_Footer;
		
		if (sendMsg) {
			ArrayList<String> msgTexts = sm.divideMessage(msgText);
			sm.sendMultipartTextMessage(PhoneNumber, null, msgTexts, null, null);

			Toast.makeText(context, res.getString(R.string.nophonezone_toast_sms_sent) + " " + PhoneNumber, Toast.LENGTH_LONG).show();
			
			/*
			ContentValues values = new ContentValues();
			values.put("address", PhoneNumber);
			values.put("body", msgText);
			context.getContentResolver().insert(Uri.parse("content://sms/sent"), values);
			*/
			
			final ContentResolver cR = context.getContentResolver(); 
			final String delayedPhone = PhoneNumber;
			final String delayedMsg = msgText;
			
			Handler h = new Handler();
			h.postDelayed(new Runnable() {
				public void run() {
					ContentValues values = new ContentValues();
					values.put("address", delayedPhone);
					values.put("body", delayedMsg);
					cR.insert(Uri.parse("content://sms/sent"), values);
				}
			}, 1000);
		}
	
	}
	
	public static boolean contactExists(Context context, String number) {
		//number = number.substring(4);
		//Log.v("nophonezone","debug: the parsed number = " + number);
		
		//-- Preferred method
		Uri lookupUri = Uri.withAppendedPath(
		PhoneLookup.CONTENT_FILTER_URI, 
		Uri.encode(number));
		String[] mPhoneNumberProjection = { PhoneLookup._ID, PhoneLookup.NUMBER, PhoneLookup.DISPLAY_NAME };
		Cursor cur = context.getContentResolver().query(lookupUri,mPhoneNumberProjection, null, null, null);
		try {
		   if (cur.moveToFirst()) {
			   cur.close();
		      return true;
		   }
		} finally {
			if (cur != null)
				cur.close();
		}
		
		//-- Alternative method
		Uri slookupUri = Uri.withAppendedPath(
		Phone.CONTENT_FILTER_URI, 
		Uri.encode(number));
		String[] smPhoneNumberProjection = { Phone._ID, Phone.NUMBER, Phone.DISPLAY_NAME };
		Cursor scur = context.getContentResolver().query(slookupUri,smPhoneNumberProjection, null, null, null);
		try {
		   if (scur.moveToFirst()) {
			   scur.close();
		      return true;
		   }
		} finally {
			if (scur != null)
				scur.close();
		}
		
		//-- Check on SIM
		String simUrl = "content://icc/adn";
		Intent intent = new Intent();
        intent.setData(Uri.parse(simUrl));
        Uri simUri = intent.getData();
        Cursor cursorSim = context.getContentResolver().query(simUri, null, null,null, null);

        while (cursorSim.moveToNext()) {
        	String checkNum = cursorSim.getString(cursorSim.getColumnIndex("number"));
        	//Log.v("nophonezone","debug: checkNum = " + checkNum);
        	if (PhoneNumberUtils.compare(context, number, checkNum)) {
        		return true;
        	}
        }
        
	    return false;
	}
	
	public static String parsePhoneNumber (String phone) {
		//Log.v("nophonezone", "phone before is: " + phone);
		phone = phone.replaceAll("[^0-9+]", "");

		TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
		try {
		   PhoneNumber phoneN = phoneUtil.parse(phone, telephony.getSimCountryIso().toUpperCase());
		   phone = "+" + phoneN.getCountryCode() + phoneN.getNationalNumber();
		} catch (NumberParseException e) {
		   e.printStackTrace();
		}
	   
		/*if (PhoneNumberUtils.toaFromString(phone) != PhoneNumberUtils.TOA_International) {
		   phone = PhoneNumberUtils.stringFromStringAndTOA(phone, PhoneNumberUtils.TOA_International);
	   	}*/
		//Log.v("nophonezone", "phone after is: " + phone);
		return phone;
	}
	
	public static String formatCharset(String txtInicial) {
		
		/*try {
			msgText = new String(msgText.getBytes("UTF-8"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			//e.printStackTrace();
		}*/
 		 
        Charset charsetOrigem = Charset.forName("UTF-8");  
        CharsetEncoder encoderOrigem = charsetOrigem.newEncoder();
        Charset charsetDestino = Charset.forName("UTF-8");
        CharsetDecoder decoderDestino = charsetDestino.newDecoder();
        
        String txtFinal = "";

        try {
            ByteBuffer bbuf = encoderOrigem.encode(CharBuffer.wrap( txtInicial ));
            CharBuffer cbuf = decoderDestino.decode(bbuf);
            txtFinal = cbuf.toString();
        } catch (CharacterCodingException e) {
            e.printStackTrace();
            //Log.v("nophonezone","error charset exception = " + e.toString());
        }
        
        if (txtFinal.length() == 0) txtFinal = txtInicial;

        return txtFinal;
    }

    public void exitApplication () {
    	SettingsActivity.setVolume(getApplicationContext(), false);
    	dh.clearWarnings();
    	this.finish();
    	System.exit(RESULT_OK);
    }
    
private class MyLicenseCheckerCallback implements LicenseCheckerCallback {
    	
    	@Override
        public void allow() {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            // Should allow user access.
        }
    	
    	@Override
        public void dontAllow() {
            if (isFinishing()) {
                // Don't update UI if Activity is finishing.
                return;
            }
            // Should not allow access. An app can handle as needed,
            // typically by informing the user that the app is not licensed
            // and then shutting down the app or limiting the user to a
            // restricted set of features.
            // In this example, we show a dialog that takes the user to Market.
            showDialog(0);
        }

    	@Override
    	public void applicationError(ApplicationErrorCode errorCode) {
    		// TODO Auto-generated method stub
    		showDialog(0);
    	}
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mChecker.onDestroy();
    }
}