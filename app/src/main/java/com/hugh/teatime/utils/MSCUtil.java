package com.hugh.teatime.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.hugh.teatime.app.GlobalVar;
import com.iflytek.cloud.FaceRequest;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.RequestListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import java.io.ByteArrayOutputStream;

/**
 * 讯飞语音工具类
 * Created by Hugh on 2016/2/29 10:52
 */
public class MSCUtil {

    private Context context;//上下文
    private static MSCUtil instance;//自身对象
    private RecognizerDialog iatDialog;// 录音UI
    private SpeechRecognizer mIat;// 语音听写器
    private SpeechSynthesizer mTts;// 语音合成器

    /**
     * 私有的构造函数
     *
     * @param context 上下文
     */
    private MSCUtil(Context context) {

        //赋值上下文
        this.context = context;

        //初始化录音UI器
        //创建RecognizerDialog对象，第二个参数：本地听写时传InitListener
        iatDialog = new RecognizerDialog(context, null);

        //初始化语音听写器
        //创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener
        mIat = SpeechRecognizer.createRecognizer(context, null);
        //设置听写参数，详见《科大讯飞MSC API手册(Android)》SpeechConstant类
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");

        //初始化语音合成器
        //创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
        mTts = SpeechSynthesizer.createSynthesizer(context, null);
        //合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer类
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");//设置发音人
        mTts.setParameter(SpeechConstant.SPEED, "50");//设置语速
        mTts.setParameter(SpeechConstant.VOLUME, "80");//设置音量，范围0~100
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD); //设置云端
    }

    /**
     * 获取自身实例
     *
     * @param context 上下文
     *
     * @return 自身实例
     */
    public static MSCUtil getInstance(Context context) {

        if (instance == null) {
            instance = new MSCUtil(context);
        }

        return instance;
    }

    /**
     * 开始语音听写
     */
    public void startListen(RecognizerListener mRecoListener) {

        //展示UI界面
        iatDialog.setListener(mDiaListener);
        iatDialog.show();

        //开始录音
        mIat.startListening(mRecoListener);
    }

    /**
     * 结束语音听写
     */
    public void stopListen() {

        //关闭UI界面
        if (iatDialog.isShowing()) {
            iatDialog.cancel();
        }

        //结束录音
        if (mIat.isListening()) {
            mIat.stopListening();
        }
    }

    /**
     * 开始语音合成并播放
     *
     * @param text 被合成语音文字内容
     */
    public void speak(String text) {

        mTts.startSpeaking(text, mSynListener);
    }

    /**
     * 人脸检测
     *
     * @param bitmap 人脸图片
     * @param rl     回调监听
     */
    public void faceDetect(Bitmap bitmap, RequestListener rl) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);

        FaceRequest faceRequest = new FaceRequest(context);
        faceRequest.setParameter(SpeechConstant.WFR_SST, "detect");
        faceRequest.sendRequest(baos.toByteArray(), rl);
    }

    /**
     * 人脸注册
     *
     * @param bitmap 人脸图片
     * @param rl     回调监听
     */
    public void regFace(Bitmap bitmap, RequestListener rl) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);

        FaceRequest faceRequest = new FaceRequest(context);
        faceRequest.setParameter(SpeechConstant.WFR_SST, "reg");
        faceRequest.setParameter(SpeechConstant.AUTH_ID, GlobalVar.USER_ID);
        faceRequest.sendRequest(baos.toByteArray(), rl);
    }

    /**
     * 人脸验证
     *
     * @param bitmap 人脸图片
     * @param rl     回调监听
     */
    public void verifyFace(Bitmap bitmap, RequestListener rl) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);

        FaceRequest faceRequest = new FaceRequest(context);
        faceRequest.setParameter(SpeechConstant.WFR_SST, "verify");
        faceRequest.setParameter(SpeechConstant.AUTH_ID, GlobalVar.USER_ID);
        faceRequest.sendRequest(baos.toByteArray(), rl);
    }

    /**
     * 录音UI监听
     */
    RecognizerDialogListener mDiaListener = new RecognizerDialogListener() {

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {

        }

        @Override
        public void onError(SpeechError speechError) {

        }
    };

    /**
     * 语音合成监听
     */
    SynthesizerListener mSynListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {

        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {

        }

        @Override
        public void onSpeakPaused() {

        }

        @Override
        public void onSpeakResumed() {

        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {

        }

        @Override
        public void onCompleted(SpeechError speechError) {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };
}
