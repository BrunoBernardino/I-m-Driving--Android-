package com.wozia.nophonezonelite;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();

		Object messages[] = (Object[]) bundle.get("pdus");
		SmsMessage smsMessage[] = new SmsMessage[messages.length];
		for (int n = 0; n < messages.length; n++) {
			smsMessage[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
		}

		//Toast.makeText(context,"Received SMS: " + smsMessage[0].getMessageBody() + "\nFrom: " + smsMessage[0].getDisplayOriginatingAddress(), Toast.LENGTH_LONG).show();
		String phone = "";
		phone = NoPhoneZone.parsePhoneNumber(smsMessage[0].getDisplayOriginatingAddress());
		NoPhoneZone.sendMessage(context, phone);
	}

}

