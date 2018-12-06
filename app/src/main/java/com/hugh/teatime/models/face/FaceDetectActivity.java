package com.hugh.teatime.models.face;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.hugh.teatime.R;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.utils.JsonParseUtil;
import com.hugh.teatime.utils.LogUtil;
import com.hugh.teatime.utils.MSCUtil;
import com.hugh.teatime.utils.ToastUtil;
import com.hugh.teatime.view.TitlebarView;
import com.iflytek.cloud.RequestListener;
import com.iflytek.cloud.SpeechError;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * 人脸识别页
 */
public class FaceDetectActivity extends BaseActivity {

    private ImageView ivTI;
    private ImageView ivDI;

    private Bitmap targetBitmap;
    private Bitmap detectedBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detect);

        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {

        TitlebarView tbv = findViewById(R.id.tbv);
        Button btnVerifyFace = findViewById(R.id.btn_verify_face);
        ivTI = findViewById(R.id.iv_target_img);
        ivDI = findViewById(R.id.iv_detected_img);

        tbv.setListener(new TitlebarView.TitlebarListener() {
            @Override
            public void onLeftBtnClick() {
                finish();
            }

            @Override
            public void onRightBtnClick() {

            }
        });
        btnVerifyFace.setOnClickListener(clickListener);
        ivTI.setOnClickListener(clickListener);
        ivDI.setOnClickListener(clickListener);
    }

    /**
     * 打开相册
     */
    private void openPhotoBook(int requestCode) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 按钮点击事件
     */
    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_verify_face:
                    if (detectedBitmap == null || targetBitmap == null) {
                        ToastUtil.showInfo(FaceDetectActivity.this, "请选择检测图片", true);
                        break;
                    }
                    MSCUtil.getInstance(FaceDetectActivity.this).verifyFace(detectedBitmap, verifyRL);
                    break;
                case R.id.iv_target_img:
                    openPhotoBook(1);
                    break;
                case R.id.iv_detected_img:
                    openPhotoBook(2);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 数据返回回调
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();
            ContentResolver cr = this.getContentResolver();
            try {
                if (requestCode == 1) {
                    targetBitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    ivTI.setImageBitmap(targetBitmap);
                    MSCUtil.getInstance(this).faceDetect(targetBitmap, targetRL);
                } else if (requestCode == 2) {
                    detectedBitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    ivDI.setImageBitmap(detectedBitmap);
                    MSCUtil.getInstance(this).faceDetect(detectedBitmap, detectedRL);
                }
            } catch (FileNotFoundException e) {
                LogUtil.logIResult(e.toString());
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 目标图片人脸检测，请求回调
     */
    RequestListener targetRL = new RequestListener() {

        @Override
        public void onEvent(int i, Bundle bundle) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

            try {
                String result = new String(bytes, "utf-8");
                List<int[]> faces = JsonParseUtil.parseFaces(result);

                signFacePosition(ivTI, targetBitmap, faces);
                // 人脸注册
                MSCUtil.getInstance(FaceDetectActivity.this).regFace(targetBitmap, regRL);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCompleted(SpeechError speechError) {

            if (speechError == null) {
                return;
            }

            LogUtil.logIResult(speechError.toString());
        }
    };

    /**
     * 被检验图片人脸检测，请求回调
     */
    RequestListener detectedRL = new RequestListener() {

        @Override
        public void onEvent(int i, Bundle bundle) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

            try {
                String result = new String(bytes, "utf-8");
                List<int[]> faces = JsonParseUtil.parseFaces(result);

                signFacePosition(ivDI, detectedBitmap, faces);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCompleted(SpeechError speechError) {

            if (speechError == null) {
                return;
            }

            LogUtil.logIResult(speechError.toString());
        }
    };

    /**
     * 人脸注册监听回调
     */
    RequestListener regRL = new RequestListener() {

        @Override
        public void onEvent(int i, Bundle bundle) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

            try {
                String result = new String(bytes, "utf-8");
                RegFace rf = JsonParseUtil.parseRegFace(result);

                LogUtil.logIResult("regRL rst:" + rf.getRst() + " gid:" + rf.getGid());

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCompleted(SpeechError speechError) {

            if (speechError == null) {
                return;
            }

            LogUtil.logIResult(speechError.toString());
        }
    };

    /**
     * 人脸验证监听回调
     */
    RequestListener verifyRL = new RequestListener() {

        @Override
        public void onEvent(int i, Bundle bundle) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

            try {
                String result = new String(bytes, "utf-8");
                String rst = JsonParseUtil.parseVerifyFace(result);

                if (rst.equals("success")) {
                    ToastUtil.showInfo(FaceDetectActivity.this, R.string.toast_same_persion, true);
                } else {
                    ToastUtil.showInfo(FaceDetectActivity.this, R.string.toast_different_persion, true);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onCompleted(SpeechError speechError) {

            if (speechError == null) {
                return;
            }

            LogUtil.logIResult(speechError.toString());
        }
    };

    /**
     * 标记人脸位置
     *
     * @param iv     图片显示控件
     * @param mImage 图片
     * @param faces  人脸位置集合
     */
    private void signFacePosition(ImageView iv, Bitmap mImage, List<int[]> faces) {

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(Math.max(mImage.getWidth(), mImage.getHeight()) / 100f);

        Bitmap bitmap = Bitmap.createBitmap(mImage.getWidth(), mImage.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(mImage, new Matrix(), null);
        for (int i = 0; i < faces.size(); i++) {
            float x1 = faces.get(i)[0];
            float y1 = faces.get(i)[1];
            float x2 = faces.get(i)[2];
            float y2 = faces.get(i)[3];
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(new Rect((int) x1, (int) y1, (int) x2, (int) y2), paint);
        }

        mImage = bitmap;
        iv.setImageBitmap(mImage);
    }
}
