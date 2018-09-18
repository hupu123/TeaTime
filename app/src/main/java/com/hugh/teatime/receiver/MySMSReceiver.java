package com.hugh.teatime.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.hugh.teatime.utils.LogUtil;

/**
 * Created by Hugh on 2016/4/18 14:11
 */
public class MySMSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // 第一步、获取短信的内容和发件人
        StringBuilder body = new StringBuilder();// 短信内容
        StringBuilder number = new StringBuilder();// 短信发件人
        Bundle bundle = intent.getExtras();
        String format = intent.getStringExtra("format");
        if (bundle == null) {
            return;
        }
        Object[] _pdus = (Object[]) bundle.get("pdus");
        if (_pdus == null) {
            return;
        }
        SmsMessage[] messages = new SmsMessage[_pdus.length];
        for (int i = 0; i < _pdus.length; i++) {
            messages[i] = SmsMessage.createFromPdu((byte[]) _pdus[i], format);
        }
        for (SmsMessage currentMessage : messages) {
            body.append(currentMessage.getDisplayMessageBody());
        }
        if (messages.length > 0) {
            number.append(messages[0].getDisplayOriginatingAddress());
        }
        String smsNumber = number.toString();
        String smsBody = body.toString();
        if (smsNumber.contains("+86")) {
            smsNumber = smsNumber.substring(3);
        }

        LogUtil.logIResult("number:" + smsNumber + " body:" + smsBody);

        //        该方法Android4.4及以上版本已不可用
        //        // 第二步:确认该短信内容是否满足过滤条件
        //        boolean flags_filter = false;
        //        if (smsNumber.equals("10086")) {
        //            flags_filter = true;
        //        }
        //        // 第三步:取消
        //        if (flags_filter) {
        //
        //            LogUtil.logIResult("abort broadcast");
        //
        //            this.abortBroadcast();
        //        }

    }
}
