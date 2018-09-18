package com.hugh.teatime.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.hugh.teatime.db.MyDBOperater;
import com.hugh.teatime.models.book.Book;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

/**
 * 读书工具（将文字转化为语音）
 * Created by Hugh on 2016/3/22 11:17
 */
public class ReadBookUtil {

    private static ReadBookUtil instance;// 自身实例
    private SpeechSynthesizer mTtsBook;// 听书语音合成器
    private StrQueueUtil queue;
    private Context context;
    private Book book;
    private int progress;
    private int partProgress;

    /**
     * 私有的构造函数
     *
     * @param context 上下文
     */
    private ReadBookUtil(Context context) {

        // 上下文
        this.context = context;

        // 初始化字符串队列
        this.queue = new StrQueueUtil();

        //检查《语记》是否安装
        //如未安装，获取《语记》下载地址进行下载。安装完成后即可使用服务。
        if (!SpeechUtility.getUtility().checkServiceInstalled()) {
            String url = SpeechUtility.getUtility().getComponentUrl();
            Uri uri = Uri.parse(url);
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(it);
        } else {
            mTtsBook = SpeechSynthesizer.createSynthesizer(context, new InitListener() {

                @Override
                public void onInit(int i) {

                    LogUtil.logIResult("init book SpeechSynthesizer");

                }
            });
            mTtsBook.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        }
    }

    /**
     * 获取自身实例
     *
     * @param context 上下文
     *
     * @return 自身实例
     */
    public static ReadBookUtil getInstance(Context context) {

        if (instance == null) {
            instance = new ReadBookUtil(context);
        }

        return instance;
    }

    /**
     * 关闭听书工具
     */
    public void cancel() {

        if (context != null) {
            context = null;
        }
        if (queue != null) {
            queue = null;
        }
        if (mTtsBook != null) {
            mTtsBook = null;
        }
        if (instance != null) {
            instance = null;
        }
    }

    /**
     * 判读是否听书正在运行
     *
     * @return true=正在运行，false=未运行
     */
    public static boolean isRunning() {

        return instance != null;
    }

    /**
     * 打开语音设置界面
     */
    public static void openSetting() {

        // 打开语音设置界面
        SpeechUtility.getUtility().openEngineSettings(SpeechConstant.ENG_TTS);
    }

    //    /**
    //     * 加载书籍
    //     *
    //     * @param text 书籍文本
    //     */
    //    public void loadBook(Book book, String text) {
    //
    //        if (queue == null) {
    //            return;
    //        }
    //        this.book = book;
    //        this.progress = book.getProgress();
    //        queue.buildQueueFromString(text);
    //        // 记录书籍大小到数据库
    //        MyDBOperater.getInstance(context).updateBookSize(book.getBookId(), queue.size());
    //    }

    /**
     * 加载书籍的一部分（提高加载速度）
     *
     * @param book 书籍信息
     * @param text 书籍内容
     *
     * @return 书籍预加载部分内容
     */
    public String loadBookPart(Book book, String text) {

        final int STR_LOAD_PRE = 0;// 文字加载起始位置（相对于当前进度向前偏移量）
        final int STR_LOAD_END = 400;// 文字加载结束位置（相对于当前进度向后偏移量）

        if (queue == null) {
            return null;
        }
        this.book = book;
        this.progress = book.getProgress();
        this.partProgress = 0;
        String textPart;
        if (progress >= STR_LOAD_PRE) {
            textPart = text.substring(StrQueueUtil.STR_LENGTH * (progress - STR_LOAD_PRE), StrQueueUtil.STR_LENGTH * (progress + STR_LOAD_END));
        } else {
            textPart = text.substring(0, StrQueueUtil.STR_LENGTH * (progress + STR_LOAD_END));
        }
        queue.buildQueueFromString(textPart);

        return textPart;
    }

    /**
     * 开始听书语音合成并播放
     */
    public void speakBook() {

        if (mTtsBook == null || queue == null) {
            return;
        }

        if (queue.isLoadSuccess()) {
            String tempStr = queue.get(partProgress);
            mTtsBook.startSpeaking(tempStr, synthesizerListener);
            partProgress++;
            // 记录书籍进度到数据库

            LogUtil.logIResult("size:" + book.getSize() + " progress:" + (progress + partProgress));

            MyDBOperater.getInstance(context).updateBookProgress(book.getBookId(), progress + partProgress);
        }
    }

    /**
     * 暂停听书
     */
    public void pauseBook() {

        if (mTtsBook == null) {
            return;
        }
        mTtsBook.pauseSpeaking();
    }

    /**
     * 停止听书
     */
    public void stopBook() {

        if (mTtsBook == null) {
            return;
        }
        mTtsBook.stopSpeaking();
    }

    /**
     * 语音合成监听器
     */
    SynthesizerListener synthesizerListener = new SynthesizerListener() {

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

            if (speechError == null) {
                speakBook();
            }
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };
}
