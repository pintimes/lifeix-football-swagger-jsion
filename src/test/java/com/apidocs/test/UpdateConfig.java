package com.apidocs.test;
import java.util.List;

/**
 * @author xule
 * @version 2017年5月3日 下午4:06:51
 */
public class UpdateConfig {
    private List<String> dirs;
    private List<String> fileNames;
    private String jsonpath;
    private Object value;
    private int operateType=-1;//操作类型：0-增，1-删，2-改，当且仅当指定属性为JSONArray对象时该参数才有效
    
    public List<String> getDirs() {
        return dirs;
    }
    public void setDirs(List<String> dirs) {
        this.dirs = dirs;
    }
    public List<String> getFileNames() {
        return fileNames;
    }
    public void setFileNames(List<String> fileNames) {
        this.fileNames = fileNames;
    }
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
    public UpdateConfig(List<String> dirs, List<String> fileNames, String jsonpath, Object value,
            int operateType) {
        super();
        this.dirs = dirs;
        this.fileNames = fileNames;
        this.jsonpath = jsonpath;
        this.value = value;
        this.operateType = operateType;
    }
    public UpdateConfig(List<String> dirs, List<String> fileNames, String jsonpath, Object value) {
        super();
        this.dirs = dirs;
        this.fileNames = fileNames;
        this.jsonpath = jsonpath;
        this.value = value;
    }
    public UpdateConfig(String jsonpath, Object value, int operateType) {
        super();
        this.jsonpath = jsonpath;
        this.value = value;
        this.operateType = operateType;
    }
    public UpdateConfig(String jsonpath, Object value) {
        super();
        this.jsonpath = jsonpath;
        this.value = value;
    }
    public UpdateConfig() {
        super();
    }
}
