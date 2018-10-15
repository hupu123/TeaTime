package com.hugh.teatime.utils;

import android.content.Context;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

public class AMLocationUtil {

    private static AMLocationUtil instance;
    private AMapLocationClient locationClient;

    private AMLocationUtil(Context context) {
        locationClient = new AMapLocationClient(context);
    }

    public static AMLocationUtil getInstance(Context context) {
        if (instance == null) {
            instance = new AMLocationUtil(context);
        }
        return instance;
    }

    /**
     * 开始定位一次
     *
     * @param listener 定位结果监听
     */
    public void startLocateOnce(AMapLocationListener listener) {
        AMapLocationClientOption locationClientOption = new AMapLocationClientOption();
        locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        locationClientOption.setOnceLocation(true);
        locationClientOption.setNeedAddress(true);
        locationClientOption.setMockEnable(true);

        locationClient.setLocationOption(locationClientOption);
        locationClient.setLocationListener(listener);
        locationClient.stopLocation();
        locationClient.startLocation();
    }

    /**
     * 停止定位
     */
    public void stopLocate() {
        locationClient.stopLocation();
    }
}
