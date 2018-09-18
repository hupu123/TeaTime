package com.hugh.teatime.listener;

/**
 * Created by Hugh on 2016/2/5 14:28
 */
public interface OnVolleyResponseListener {

    void onVolleyResponse(String result);

    void onVolleyResponseError(String msg);
}
