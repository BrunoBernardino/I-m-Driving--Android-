package com.wozia.nophonezonelite;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class PhoneState extends PhoneStateListener {

	@Override
	public void onCallStateChanged(int state, String incomingNumber) {

        switch(state) {
        	case TelephonyManager.CALL_STATE_RINGING:
        		String phone = "";
        		phone = NoPhoneZone.parsePhoneNumber(incomingNumber);
        		NoPhoneZone.sendMessage(PhoneReceiver.context.getApplicationContext(), phone);
        	break;
        }
	}
}
