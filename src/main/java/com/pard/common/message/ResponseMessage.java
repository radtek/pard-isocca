package com.pard.common.message;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pard.common.utils.DateTimeUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 响应消息，controller中处理后，返回此对象，响应请求结果给客户端
 * Created by wawe on 17/5/30.
 */
public class ResponseMessage implements Serializable {

    /**
     * 是否成功
     */
    private boolean success;
    /**
     * 反馈数据
     */
    private Object data;
    /**
     * 反馈信息
     */
    private String message;

    /**
     * 响应码
     */
    private int code;

    /**
     * 过滤字段：指定需要序列化的字段
     */
    private transient Map<Class<?>, Set<String>> includes;

    /**
     * 过滤字段：指定不需要序列化的字段
     */
    private transient Map<Class<?>, Set<String>> excludes;

    private transient boolean onlyData;

    private transient String callback;

    protected ResponseMessage(String message) {
        this.code = 500;
        this.message = message;
        this.success = false;
    }

    protected ResponseMessage(int code) {
        this.code = code;
        this.success = false;
    }

    protected ResponseMessage(boolean success, Object data) {
        this.code = success ? 200 : 500;
        this.data = data;
        this.success = success;
    }

    protected ResponseMessage(boolean success, Object data, int code) {
        this(success, data);
        this.code = code;
    }

    public static ResponseMessage fromJson(String json) {
        return JSON.parseObject(json, ResponseMessage.class);
    }

    public static ResponseMessage ok() {
        return ok(null);
    }

    public static ResponseMessage ok(String message) {
        return new ResponseMessage(message).setSuccess(true).setCode(200);
    }

    public static ResponseMessage ok(Object data) {
        return new ResponseMessage(true, data);
    }

    public static ResponseMessage created(Object data) {
        return new ResponseMessage(true, data, 201);
    }

    public static ResponseMessage error(String message) {
        return new ResponseMessage(message);
    }

    public static ResponseMessage error(String message, int code) {
        return new ResponseMessage(code).setMessage(message);
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = Maps.newHashMap();
        map.put("success", this.success);
        if (data != null) {
            map.put("data", this.getData());
        }
        if (message != null) {
            map.put("message", this.getMessage());
        }
        map.put("code", this.getCode());
        return map;
    }

    public ResponseMessage include(Class<?> type, String... fields) {
        return include(type, Lists.newArrayList(fields));
    }

    public ResponseMessage include(Class<?> type, Collection<String> fields) {
        if (includes == null) {
            includes = Maps.newHashMap();
        }
        if (fields == null || fields.isEmpty()) return this;
        fields.forEach(field -> {
            if (field.contains(".")) {
                String tmp[] = field.split("[.]", 2);
                try {
                    Field field1 = type.getDeclaredField(tmp[0]);
                    if (field1 != null) {
                        include(field1.getType(), tmp[1]);
                    }
                } catch (Throwable e) {
                }
            } else {
                getStringListFormMap(includes, type).add(field);

            }
        });
        return this;
    }

    public ResponseMessage exclude(Class type, Collection<String> fields) {
        if (excludes == null) {
            excludes = Maps.newHashMap();
        }
        if (fields == null || fields.isEmpty()) return this;
        fields.forEach(field -> {
            if (field.contains(".")) {
                String tmp[] = field.split("[.]", 2);
                try {
                    Field field1 = type.getDeclaredField(tmp[0]);
                    if (field1 != null) {
                        exclude(field1.getType(), tmp[1]);
                    }
                } catch (Throwable e) {

                }
            } else {
                getStringListFormMap(excludes, type).add(field);
            }
        });
        return this;
    }

    public ResponseMessage exclude(Collection<String> fields) {
        if (excludes == null) {
            excludes = Maps.newHashMap();
        }
        if (fields == null || fields.isEmpty()) return this;
        Class type;
        if (data != null) type = data.getClass();
        else return this;
        exclude(type, fields);
        return this;
    }

    public ResponseMessage include(Collection<String> fields) {
        if (includes == null) {
            includes = Maps.newHashMap();
        }
        if (fields == null || fields.isEmpty()) return this;
        Class<?> type;
        if (data != null) type = data.getClass();
        else return this;
        include(type, fields);
        return this;
    }

    public ResponseMessage exclude(Class type, String... fields) {
        return exclude(type, Lists.newArrayList(fields));
    }

    public ResponseMessage exclude(String... fields) {
        return exclude(Lists.newArrayList(fields));
    }

    public ResponseMessage include(String... fields) {
        return include(Lists.newArrayList(fields));
    }

    protected Set<String> getStringListFormMap(Map<Class<?>, Set<String>> map, Class type) {
        Set<String> list = map.get(type);
        if (list == null) {
            list = Sets.newHashSet();
            map.put(type, list);
        }
        return list;
    }

    public boolean isSuccess() {
        return success;
    }

    public ResponseMessage setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public Object getData() {
        return data;
    }

    public ResponseMessage setData(Object data) {
        this.data = data;
        return this;
    }

    public int getCode() {
        return code;
    }

    public ResponseMessage setCode(int code) {
        this.code = code;
        return this;
    }

    @Override
    public String toString() {
        return JSON.toJSONStringWithDateFormat(this, DateTimeUtils.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND);
    }

    public Map<Class<?>, Set<String>> getExcludes() {
        return excludes;
    }

    public Map<Class<?>, Set<String>> getIncludes() {
        return includes;
    }

    public ResponseMessage onlyData() {
        setOnlyData(true);
        return this;
    }

    public boolean isOnlyData() {
        return onlyData;
    }

    public void setOnlyData(boolean onlyData) {
        this.onlyData = onlyData;
    }

    public ResponseMessage callback(String callback) {
        this.callback = callback;
        return this;
    }

    public String getCallback() {
        return callback;
    }

    public String getMessage() {
        return message;
    }

    public ResponseMessage setMessage(String message) {
        this.message = message;
        return this;
    }
}
