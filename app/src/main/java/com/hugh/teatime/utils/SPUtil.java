package com.hugh.teatime.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Hugh on 2016/3/25 10:48
 */
public class SPUtil {

    private final String SP_PSW_NAME = "sp_teatime_psw";                            // 登陆密码
    private final String SP_IS_FIRST_LOGIN = "sp_teatime_is_first";                 // 是否第一次启动
    private final String SP_IS_INIT_IMAGE_TABLE = "sp_is_init_image_table";         // 是否初始化image_table
    private final String SP_SMS_BLACK_LIST = "sp_sms_black_list";                   // 短信黑名单
    private final String SP_FP_PATH = "sp_fp_path";                                 // 上次文件选择路径
    private final String SP_COMIC_POSITION = "sp_comic_position";                   // 漫画阅读位置
    private final String SP_HOME_ICON_ORDER = "sp_home_icon_order";                 // 首页图标顺序
    private final String SP_DISABLE_LOGIN_TIME = "sp_disable_login_time";           // 禁止登陆时间戳
    private final String SP_WRONG_PSW_COUNT = "sp_wrong_psw_count";                 // 错误密码输入次数
    private final String SP_EXIT_TIME = "sp_exit_time";                             // 退出时间戳
    private final String SP_LAST_INPUT_CARNO = "sp_last_input_carno";               // 上次车牌号输入
    private final String SP_LAST_SELECTED_PAY_TYPE = "sp_last_selected_pay_type";   // 上次支付方式选择

    private static SPUtil instance;
    private SharedPreferences sp;

    /**
     * 私有的构造函数
     *
     * @param context 上下文
     */
    private SPUtil(Context context) {
        String SP_NAME = "sp_teatime";
        sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 获取自身实体
     *
     * @param context 上下文
     * @return 自身实体
     */
    public static SPUtil getInstance(Context context) {
        if (instance == null) {
            instance = new SPUtil(context);
        }

        return instance;
    }

    /**
     * 保存密码
     *
     * @param psw 密码
     */
    public void setPsw(String psw) {
        sp.edit().putString(SP_PSW_NAME, psw).apply();
    }

    /**
     * 获取密码
     *
     * @return 密码
     */
    public String getPsw() {
        return sp.getString(SP_PSW_NAME, "");
    }

    /**
     * 设置第一次登录状态
     *
     * @param isFirst 是否第一次登录，true=第一次登录，false=非第一次登录
     */
    public void setIsFirst(boolean isFirst) {
        sp.edit().putBoolean(SP_IS_FIRST_LOGIN, isFirst).apply();
    }

    /**
     * 是否第一次登录
     *
     * @return true=第一次登录，false=非第一次登录
     */
    public boolean isFirstLogin() {
        return sp.getBoolean(SP_IS_FIRST_LOGIN, true);
    }

    /**
     * 更改Image Table是否已经初始化状态
     *
     * @param isInit true=已初始化，false=未初始化
     */
    public void setIsInitImageTable(boolean isInit) {
        sp.edit().putBoolean(SP_IS_INIT_IMAGE_TABLE, isInit).apply();
    }

    /**
     * 返回Image Table是否初始化状态
     *
     * @return true=已初始化，false=未初始化
     */
    boolean isInitImageTable() {
        return sp.getBoolean(SP_IS_INIT_IMAGE_TABLE, false);
    }

    /**
     * 添加号码到短信黑名单
     *
     * @param number 号码
     */
    public void addSMSToBlackList(String number) {
        Set<String> smsBlackList = getSMSBlackList();
        smsBlackList.add(number);
        sp.edit().putStringSet(SP_SMS_BLACK_LIST, smsBlackList).apply();
    }

    /**
     * 从短信黑名单移除号码
     *
     * @param number 号码
     */
    public void removeSMSFromBlackList(String number) {
        Set<String> smsBlackList = getSMSBlackList();
        smsBlackList.remove(number);
        sp.edit().putStringSet(SP_SMS_BLACK_LIST, smsBlackList).apply();
    }

    /**
     * 获取短信黑名单
     *
     * @return 短信黑名单
     */
    public Set<String> getSMSBlackList() {
        Set<String> smsBlackList = sp.getStringSet(SP_SMS_BLACK_LIST, null);
        if (smsBlackList == null || smsBlackList.size() == 0) {
            smsBlackList = new HashSet<>();
        }

        return smsBlackList;
    }

    /**
     * 保存上次文件选择路径
     *
     * @param path 文件路径
     */
    public void setFilePickPath(String path) {
        sp.edit().putString(SP_FP_PATH, path).apply();
    }

    /**
     * 获取上次文件选择路径
     *
     * @return 文件路径
     */
    public String getFilePickPath() {
        return sp.getString(SP_FP_PATH, "");
    }

    /**
     * 保存漫画阅读位置
     *
     * @param position 漫画位置
     */
    public void setComicPosition(int position) {
        sp.edit().putInt(SP_COMIC_POSITION, position).apply();
    }

    /**
     * 获取漫画阅读位置
     *
     * @return 漫画位置
     */
    public int getComicPosition() {
        return sp.getInt(SP_COMIC_POSITION, 0);
    }

    /**
     * 设置首页图标顺序
     *
     * @param iconOrder 图标顺序
     */
    public void setHomeIconOrder(String iconOrder) {
        sp.edit().putString(SP_HOME_ICON_ORDER, iconOrder).apply();
    }

    /**
     * 获取首页图标顺序
     *
     * @return 图标顺序
     */
    public String getHomeIconOrder() {
        return sp.getString(SP_HOME_ICON_ORDER, "");
    }

    /**
     * 设置禁止登陆时间戳
     *
     * @param time 时间戳
     */
    public void setDisableLoginTime(long time) {
        sp.edit().putLong(SP_DISABLE_LOGIN_TIME, time).apply();
    }

    /**
     * 获取禁止登陆时间戳
     *
     * @return 时间戳
     */
    public long getDisableLoginTime() {
        return sp.getLong(SP_DISABLE_LOGIN_TIME, 0);
    }

    /**
     * 设置输入错误密码次数
     *
     * @param count 次数
     */
    public void setWrongPswCount(int count) {
        sp.edit().putInt(SP_WRONG_PSW_COUNT, count).apply();
    }

    /**
     * 获取输入密码错误次数
     *
     * @return 次数
     */
    public int getWrongPswCount() {
        return sp.getInt(SP_WRONG_PSW_COUNT, 0);
    }

    /**
     * 设置上次退出时间
     *
     * @param time 退出时间
     */
    public void setExitTime(long time) {
        sp.edit().putLong(SP_EXIT_TIME, time).apply();
    }

    /**
     * 获取上次退出时间
     *
     * @return 退出时间
     */
    public long getExitTime() {
        return sp.getLong(SP_EXIT_TIME, 0);
    }

    /**
     * 设置上次输入车牌号
     *
     * @param carNO 车牌号
     */
    public void setLastInputCarNO(String carNO) {
        sp.edit().putString(SP_LAST_INPUT_CARNO, carNO).apply();
    }

    /**
     * 获取上次输入车牌号
     *
     * @return 车牌号
     */
    public String getLastInputCarNO() {
        return sp.getString(SP_LAST_INPUT_CARNO, "");
    }

    /**
     * 设置上次选择的支付类型
     *
     * @param payType 支付类型
     */
    public void setLastSelectedPayType(String payType) {
        sp.edit().putString(SP_LAST_SELECTED_PAY_TYPE, payType).apply();
    }

    /**
     * 获取上次选择的支付类型
     *
     * @return 支付类型
     */
    public String getLastSelectedPayType() {
        return sp.getString(SP_LAST_SELECTED_PAY_TYPE, "");
    }
}
