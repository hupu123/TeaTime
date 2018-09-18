package com.hugh.teatime.models.robot;

/**
 * 键值对实体类
 * Created by Hugh on 2016/2/4 16:21
 */
public class KeyValuePair {

    private String key;   // 键
    private String value; // 值

    /**
     * 构造函数
     *
     * @param key   键
     * @param value 值
     */
    public KeyValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
