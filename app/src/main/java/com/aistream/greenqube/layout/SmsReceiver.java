package com.aistream.greenqube.layout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.aistream.greenqube.mvp.view.ViewLogin;

/**
 * Created by PhuDepTraj on 9/13/2018.
 */

public class SmsReceiver extends BroadcastReceiver {
    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private Context mContext;
    private ViewLogin viewLogin;

    public SmsReceiver() {
    }

    public SmsReceiver(Context mCont, ViewLogin view) {
        this.mContext = mCont;
        this.viewLogin = view;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        if (intent.getAction().equals(SMS_RECEIVED)) {
//            Bundle bundle = intent.getExtras();
//            if (bundle != null) {
//                Object[] pdus = (Object[]) bundle.get("pdus");
//                if (pdus.length == 0) {
//                    return;
//                }
//                SmsMessage[] messages = new SmsMessage[pdus.length];
//                StringBuilder sb = new StringBuilder();
//                for (int i = 0; i < pdus.length; i++) {
//                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
//                    sb.append(messages[i].getMessageBody());
//                }
//                String sender = messages[0].getOriginatingAddress();
//                String message = sb.toString();
//                Log.i("SMSOTP", "SMS OTP: " + message);
//                Log.i("SMSOTP", "SMS OTP: " + sender);
//                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
//                bundle.putString("Sender", sender);
//                bundle.putString("SMSOTP", message);
//                viewLogin.updateOTP(sender, message);
//            }
//        }
    }
}
