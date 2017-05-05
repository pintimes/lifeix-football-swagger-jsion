package com.apidocs.test.model;
import java.util.Arrays;
import java.util.List;

/**
 * @author xule
 * @version 2017年5月3日 下午4:06:51
 */
public class ModifyConfig {
    private String jsonpath;
    private Object value;
    private int operateType=-1;//操作类型：-1 对JSONArray对象本身进行操作；0 添加;1 删除。当且仅当指定属性为JSONArray对象时该参数才有效

    public String getJsonpath() {
        return jsonpath;
    }
    public void setJsonpath(String jsonpath) {
        this.jsonpath = jsonpath;
    }
    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }
    public int getOperateType() {
        return operateType;
    }
    public void setOperateType(int operateType) {
        this.operateType = operateType;
    }
    
    public ModifyConfig(String jsonpath, Object value, int operateType) {
        super();
        this.jsonpath = jsonpath;
        this.value = value;
        this.operateType = operateType;
    }
    public ModifyConfig(String jsonpath, Object value) {
        super();
        this.jsonpath = jsonpath;
        this.value = value;
    }
    public ModifyConfig() {
        super();
    }
}
