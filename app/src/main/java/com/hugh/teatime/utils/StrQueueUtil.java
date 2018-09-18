package com.hugh.teatime.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hugh on 2016/3/23 10:31
 */
public class StrQueueUtil {

    /**
     * 用ArrayList存储队列
     */
    private List<String> queue;
    /**
     * 书籍加载结果（true=成功,false=失败）
     */
    private boolean isLoadSuccess = false;
    /**
     * 子字符串截取长度
     */
    public static final int STR_LENGTH = 50;

    /**
     * 构造函数
     */
    public StrQueueUtil() {

        queue = new ArrayList<>();
    }

    /**
     * 添加String到队列
     *
     * @param str 被添加元素
     *
     * @return 是否添加成功
     */
    public boolean put(String str) {

        return queue != null && queue.add(str);
    }

    /**
     * 获取队列队首元素
     *
     * @return 队首字符串
     */
    public String get(int progress) {

        String str = null;
        if (queue != null && queue.size() > 0 && queue.size() > progress) {
            str = queue.get(progress);
        }

        return str;
    }

    /**
     * 获取队列长度
     *
     * @return 队列长度
     */
    public int size() {

        if (queue == null) {
            return 0;
        }
        return queue.size();
    }

    /**
     * 通过字符串创建队列（将一个长字符串分割为多个字符串保存在队列中）
     *
     * @param text 字符串
     *
     * @return 创建是否成功，true=成功，false=失败
     */
    public boolean buildQueueFromString(String text) {

        if (StringUtil.isStrNull(text)) {
            isLoadSuccess = false;
            return false;
        }

        // 游标
        int cursor = 0;
        // 字符串长度
        int length = text.length();
        // 截取的子字符串
        String tempStr;
        do {
            tempStr = text.substring(cursor, cursor + STR_LENGTH);
            put(tempStr);
            cursor = cursor + STR_LENGTH;
            length = length - STR_LENGTH;
        } while (length >= STR_LENGTH);
        if (length > 0 && length < STR_LENGTH) {
            tempStr = text.substring(cursor, cursor + length);
            put(tempStr);
        }
        isLoadSuccess = true;

        return true;
    }

    /**
     * 获取书籍是否加载成功
     *
     * @return true=成功，false=失败
     */
    public boolean isLoadSuccess() {

        return isLoadSuccess;
    }
}
