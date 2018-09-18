package com.hugh.teatime.utils;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.hugh.teatime.R;
import com.hugh.teatime.models.robot.KeyValuePair;
import com.hugh.teatime.listener.OnVolleyResponseListener;

/**
 * Volley工具类
 *
 * @author Hugh
 */
public class VolleyUtil {

    private static VolleyUtil volleyUtil;// 本身对象
    private RequestQueue requestQueue;// 请求队列
    private Context context;

    /**
     * 私有的构造函数
     *
     * @param context
     */
    private VolleyUtil(Context context) {

        if (requestQueue == null) {
            this.context = context.getApplicationContext();
            requestQueue = Volley.newRequestQueue(this.context);
        }
    }

    /**
     * 获取实例对象
     *
     * @param context
     *
     * @return
     */
    public static synchronized VolleyUtil getInstance(Context context) {

        if (volleyUtil == null) {
            volleyUtil = new VolleyUtil(context);
        }

        return volleyUtil;
    }

    /**
     * 添加请求到队列
     *
     * @param request
     */
    private <T> void addRequestToRQ(Request<T> request) {

        requestQueue.add(request);
    }

    /**
     * POST方式请求网络
     *
     * @param url              请求URL
     * @param params           请求参数，如果不需要传参，传入null值
     * @param responseListener 回调监听
     */
    public void requestByPost(String url, final List<KeyValuePair> params, final OnVolleyResponseListener responseListener) {

        // 创建String请求
        StringRequest sr = new StringRequest(Method.POST, url, new Listener<String>() {

            @Override
            public void onResponse(String responseStr) {

                LogUtil.logI("resulthp", responseStr);

                responseListener.onVolleyResponse(responseStr);
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                LogUtil.logI("resulthp", "error msg: " + error.getMessage());

                responseListener.onVolleyResponseError(error.getMessage());
            }
        }) {

            // 携带参数
            @Override
            protected HashMap<String, String> getParams() throws AuthFailureError {

                if (params == null || params.size() == 0) {
                    return null;
                }
                HashMap<String, String> hmParams = new HashMap<>();
                for (KeyValuePair param : params) {
                    hmParams.put(param.getKey(), param.getValue());
                }

                return hmParams;
            }

            // Volley请求类提供了一个 getHeaders()的方法，重载这个方法可以自定义HTTP 的头信息。（也可不实现）
            //            public Map<String, String> getHeaders() throws AuthFailureError {
            //
            //                HashMap<String, String> headers = new HashMap<>();
            //                headers.put("Accept", "application/json");
            //                headers.put("Content-Type", "application/json;charset=UTF-8");
            //                return headers;
            //            }
        };

        // 添加请求
        addRequestToRQ(sr);
    }

    /**
     * 用Volley ImageLoader加载图片
     *
     * @param imgView   图片容器控件
     * @param url       图片网络地址
     * @param maxWidth  图片最大宽度
     * @param maxHeight 图片最大高度
     */
    public void requestImageByImageLoader(ImageView imgView, String url, int maxWidth, int maxHeight) {

        ImageLoader.ImageListener imgListener = ImageLoader.getImageListener(imgView, R.mipmap.ic_launcher, R.mipmap.ic_launcher);
        ImageLoader imgLoader = new ImageLoader(requestQueue, new BitmapCache());
        imgLoader.get(url, imgListener, maxWidth, maxHeight);
    }
}
