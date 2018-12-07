package com.hugh.teatime.app;

import com.hugh.teatime.R;

/**
 * Created by Hugh on 2016/2/4 15:43
 */
public class GlobalVar {

    /**
     * intent传递数据相关常亮
     */
    // 漫画数据
    public static final String INTENT_COMIC_DATA_LIST = "intent_comic_data_list";
    // 漫画游标位置
    public static final String INTENT_COMIC_POSITION = "intent_comic_position";
    // 加油记录数据
    public static final String INTENT_GASOLINE_RECORD = "intent_gasoline_record";
    // 加油记录数据修改
    public static final String INTENT_GASOLINE_RECORD_EDIT = "intent_gasoline_record_edit";
    // 事件数据
    public static final String INTENT_EVENT = "intent_event";
    // 事件数据修改
    public static final String INTENT_EVENT_EDIT = "intent_event_edit";
    // 位置数据
    public static final String INTENT_LOCATION = "intent_location";
    // 位置数据修改
    public static final String INTENT_LOCATION_EDIT = "intent_location_edit";
    // 判断数据是否来自事件详情标识
    public static final String INTENT_IS_FROM_EVENT = "intent_is_from_event";

    /**
     * 返回数据请求代码相关
     */
    // 请求漫画路径
    public static final int REQUEST_CODE_COMIC_PATH = 998;
    // 请求修改事件
    public static final int REQUEST_CODE_EDIT_EVENT = 999;
    // 请求修改加油记录
    public static final int REQUEST_CODE_EDIT_RECORD = 1000;
    // 请求修改地理位置
    public static final int REQUEST_CODE_EDIT_LOCATION = 1001;
    // 请求在事件中修改加油记录
    public static final int REQUEST_CODE_EDIT_RECORD_IN_EVENT = 1002;

    /**
     * 应用全局常量
     */
    // 双击返回按钮退出应用时间间隔（2秒）
    public static final long DOUBLE_CLICK_BACKBTN_TO_EXIT_INTERVAL = 2 * 1000;
    // 登录密码允许输入错误次数
    public static final int TOTAL_WRONG_PSW_INPUT_COUNT = 5;
    // 禁止输入密码后，重新输入密码登陆间隔时间
    public static final long REINPUT_PSW_INTERVAL = 60 * 60 * 1000;
    // 消息查询偏移量累计数
    public static int TEMP_OFF_SET = 0;
    // 图片缩略图缩放比例
    public static final float IMG_THUMBNAIL_SCALE = 0.1f;
    // 账单收支类型名称集合
    public static final String[] BILL_IO_TYPE_NAME_ARRAY = new String[]{"支出", "收入"};
    // 账单类型名称集合
    public static final String[] BILL_TYPE_NAME_ARRAY = new String[]{"其他", "出行", "饮食", "娱乐", "医疗", "工具", "薪资", "奖励", "红利"};
    // 账单类型图标集合
    public static final int[] BILL_TYPE_ICON_ARRAY = new int[]{R.mipmap.icon_type_other, R.mipmap.icon_type_travel, R.mipmap.icon_type_food, R.mipmap.icon_type_entertainment, R.mipmap.icon_type_medical, R.mipmap.icon_type_tool, R.mipmap.icon_type_salary, R.mipmap.icon_type_reward, R.mipmap.icon_type_dividend};
    // 账单类型颜色集合
    public static final int[] BILL_TYPE_COLOR_ARRAY = new int[]{R.color.colorChart_1, R.color.colorChart_2, R.color.colorChart_3, R.color.colorChart_4, R.color.colorChart_5, R.color.colorGreen, R.color.colorAccent, R.color.colorPrimary, R.color.colorPurple};
    // 图灵机器人API KEY
    public static final String TURING_API_KEY = "3bd72c037bfaa2f5ff592ba7add4598f";
    // 讯飞语音API ID
    public static final String MSC_API_ID = "56d3a6e5";
    // 用户ID
    public static final String USER_ID = "test_user_id";
    // 消息类型-文字型
    public static final int MSG_TYPE_TEXT = 100000;
    // 消息类型-链接型
    public static final int MSG_TYPE_URL = 200000;
    // 消息类型-新闻型
    public static final int MSG_TYPE_NEWS = 302000;
    // 消息类型-菜谱型
    public static final int MSG_TYPE_COOK_BOOK = 308000;
    // 消息类型-儿歌型
    public static final int MSG_TYPE_SONG = 313000;
    // 消息类型-诗词型
    public static final int MSG_TYPE_POETRY = 314000;
    // 汽油型号名称集合
    public static final String[] GASOLINE_TYPE_NAME_ARRAY = new String[]{"92#", "95#", "98#", "0#"};
    // 支付方式名称集合
    public static final String[] PAY_TYPE_NAME_ARRAY = new String[]{"现金", "信用卡", "储蓄卡", "微信", "支付宝"};
    // 每页数据数量
    public static final int PAGE_SIZE = 10;
    // 记事本闹钟请求码
    public static final int NOTE_ALARM_REQUEST_CODE = 0;
}
