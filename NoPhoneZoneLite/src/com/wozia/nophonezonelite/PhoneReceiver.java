package com.wozia.nophonezonelite;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
//import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class PhoneReceiver extends BroadcastReceiver {
	static Context context;
	
	@Override
	public void onReceive(Context context, Intent intent) {
	    TelephonyManager telephony = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
	    PhoneState pl = new PhoneState();
	    //PhoneStateListener pl = new PhoneStateListener();
	    PhoneReceiver.context = context;

	    telephony.listen(pl, PhoneStateListener.LISTEN_CALL_STATE);
	    
	    //Bundle bundle = intent.getExtras();
	        
	    //Toast.makeText(context,"Phone Call Received From: " + bundle.getString("incoming_number"), Toast.LENGTH_LONG).show();

	    //NoPhoneZone.sendMessage(context, bundle.getString("incoming_number"));
	}
}