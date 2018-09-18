package com.hugh.teatime.app;

import android.app.Application;

import com.hugh.teatime.utils.LogUtil;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

/**
 * Created by Hugh on 2016/2/29 10:36
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化讯飞语音
        SpeechUtility.createUtility(getApplicationContext(), SpeechConstant.APPID + "=" + GlobalVar.MSC_API_ID);

        LogUtil.logIResult("init SpeechUtility");
    }
}
