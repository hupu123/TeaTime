package com.hugh.teatime.models.note;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.hugh.teatime.R;
import com.hugh.teatime.app.GlobalVar;
import com.hugh.teatime.models.home.BaseActivity;
import com.hugh.teatime.utils.DialogUtil;
import com.hugh.teatime.utils.DimensUtil;
import com.hugh.teatime.utils.LogUtil;
import com.hugh.teatime.view.MarkerView;
import com.hugh.teatime.view.TitlebarView;

import java.util.ArrayList;

public class PickLocationActivity extends BaseActivity {

    private MapView mapView;
    private SearchView svSearch;

    private LocationBean locationBean;
    private AMap aMap;
    private Marker markerSelected;
    private ArrayList<Marker> markersSearched = new ArrayList<>();
    private ArrayList<MarkerView> markerViewsSearched = new ArrayList<>();
    private float cameraZoom = 18;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_location);

        initView(savedInstanceState);
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 初始化控件
     */
    private void initView(Bundle savedInstanceState) {

        TitlebarView tbv = findViewById(R.id.tbv_pick_location);
        tbv.setRightBtnText(getResources().getString(R.string.confirm));
        tbv.setListener(new TitlebarView.TitlebarListener() {
            @Override
            public void onLeftBtnClick() {
                finish();
            }

            @Override
            public void onRightBtnClick() {
                confirm();
            }
        });
        svSearch = findViewById(R.id.sv_search);
        svSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                query(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        svSearch.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtil.logHugh("setOnSearchClickListener");
                int marginSize = DimensUtil.getInstance(PickLocationActivity.this).dp2px(20);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, svSearch.getHeight());
                params.setMargins(marginSize, marginSize, marginSize, marginSize);
                svSearch.setLayoutParams(params);
            }
        });
        svSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                LogUtil.logHugh("setOnCloseListener");
                int marginSize = DimensUtil.getInstance(PickLocationActivity.this).dp2px(20);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(svSearch.getHeight(), svSearch.getHeight());
                params.setMargins(marginSize, marginSize, marginSize, marginSize);
                svSearch.setLayoutParams(params);
                return false;
            }
        });
        mapView = findViewById(R.id.mv_pick_location);
        mapView.onCreate(savedInstanceState);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        locationBean = (LocationBean) getIntent().getSerializableExtra(GlobalVar.INTENT_LOCATION);
        aMap = mapView.getMap();
        aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationBean.getLatitude(), locationBean.getLongitude()), cameraZoom));
        markerSelected = aMap.addMarker(new MarkerOptions().position(new LatLng(locationBean.getLatitude(), locationBean.getLongitude())).draggable(false).icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_location_selected)));
        markerSelected.setClickable(false);
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                LogUtil.logHugh("onMapClick");
                hideSelectedMarker(null);
                markerSelected.setPosition(latLng);
                locationBean.setLatitude(latLng.latitude);
                locationBean.setLongitude(latLng.longitude);
            }
        });
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LogUtil.logHugh("onMarkerClick");
                hideSelectedMarker(marker);
                markerSelected.setPosition(marker.getPosition());
                locationBean.setLatitude(marker.getPosition().latitude);
                locationBean.setLongitude(marker.getPosition().longitude);
                return true;
            }
        });
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                LogUtil.logHugh("current zoom level is " + cameraPosition.zoom);
                if (cameraZoom != cameraPosition.zoom) {
                    boolean isLabelVisibal;
                    isLabelVisibal = !(cameraPosition.zoom <= 9);
                    LogUtil.logHugh("isLabelVisibal=" + isLabelVisibal);
                    for (MarkerView markerView : markerViewsSearched) {
                        markerView.setLabelVisibal(isLabelVisibal);
                    }
                    cameraZoom = cameraPosition.zoom;
                }
            }
        });
    }

    /**
     * 清除所有搜索到得markers
     */
    private void cleanAllSearchedMarkers() {
        if (markersSearched != null) {
            for (Marker marker : markersSearched) {
                marker.destroy();
            }
            markersSearched.clear();
        }
        if (markerViewsSearched != null) {
            markerViewsSearched.clear();
        }
    }

    /**
     * 隐藏选中的marker
     *
     * @param marker 选中的marker
     */
    private void hideSelectedMarker(Marker marker) {
        for (Marker marker1 : markersSearched) {
            if (marker1.equals(marker)) {
                marker1.setVisible(false);
            } else {
                marker1.setVisible(true);
            }
        }
    }

    /**
     * 通过关键字查询
     *
     * @param keyWord 关键字
     */
    private void query(String keyWord) {
        LogUtil.logHugh("cityCode=" + locationBean.getCityCode());
        PoiSearch.Query query = new PoiSearch.Query(keyWord, "", locationBean.getCityCode());
        query.setPageSize(100);
        query.setPageNum(1);
        PoiSearch poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int i) {
                if (i == 1000) {
                    ArrayList<PoiItem> resultPois = poiResult.getPois();

                    LogUtil.logHugh("resultPois size=" + resultPois.size());

                    cleanAllSearchedMarkers();
                    LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
                    for (PoiItem poiItem : resultPois) {
                        MarkerView markerView = new MarkerView(PickLocationActivity.this);
                        markerView.setLabel(poiItem.getTitle());
                        Marker marker = aMap.addMarker(new MarkerOptions().position(new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude())).icon(BitmapDescriptorFactory.fromView(markerView)));
                        marker.setClickable(true);
                        boundsBuilder.include(marker.getPosition());
                        markersSearched.add(marker);
                        markerViewsSearched.add(markerView);
                    }
                    aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100));
                }
            }

            @Override
            public void onPoiItemSearched(PoiItem poiItem, int i) {

            }
        });
        poiSearch.searchPOIAsyn();
    }

    /**
     * 位置选择确认，获取地址信息并返回结果
     */
    private void confirm() {
        final DialogUtil dialogWaiting = new DialogUtil(this);
        dialogWaiting.showDialog();
        GeocodeSearch geocodeSearch = new GeocodeSearch(this);
        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                dialogWaiting.hideDialog();
                if (i == 1000) {
                    String address = regeocodeResult.getRegeocodeAddress().getFormatAddress();
                    locationBean.setAddress(address);
                    locationBean.setCityCode(regeocodeResult.getRegeocodeAddress().getCityCode());
                    LogUtil.logHugh("confirm address=" + address);

                    Intent intent = new Intent();
                    intent.putExtra(GlobalVar.INTENT_LOCATION_EDIT, locationBean);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });
        geocodeSearch.getFromLocationAsyn(new RegeocodeQuery(new LatLonPoint(locationBean.getLatitude(), locationBean.getLongitude()), 50, GeocodeSearch.AMAP));
    }
}
