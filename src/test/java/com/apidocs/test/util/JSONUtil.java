package com.apidocs.test.util;

import java.util.ArrayList;
import java.util.List;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.apidocs.test.model.ModifyConfig;

/**
 * @author xule
 * @version 2017年5月4日 上午11:11:43
 */
public class JSONUtil {
    /**
     * 修改json对象中的指定属性
     * @author xule
     * @version 2017年5月4日 上午11:02:35
     * @param 
     * @return void
     */
    public static void modifyJsonAttrs(JSONObject jsonObject,List<ModifyConfig> list) throws Exception {
        for (ModifyConfig modifyConfig : list) {
            modifyJsonAttrs(jsonObject,modifyConfig.getJsonpath(),modifyConfig.getValue(),modifyConfig.getOperateType());
        }
    }
    
    /**
     * 修改json对象中的指定属性
     * @author xule
     * @version 2017年5月2日 下午1:33:36
     * @param 
     * @return void
     * @throws Exception 
     */
    public static void modifyJsonAttrs(JSONObject jsonObject, String jsonpath, Object value, int operateType)
            throws Exception {
        /**
         * 提取jsonpath内容
         */
        Object eval = JSONPath.eval(jsonObject, jsonpath);
        if (eval == null||"[]".equals(eval.toString())||"[null]".equals(eval.toString())) {
            JSONPath.set(jsonObject, jsonpath, value);
            return;
        }
        /**
         * 根据jsonpath获得符合规则的对象列表
         */
        if (eval instanceof ArrayList<?>) {
            List<?> list = (ArrayList<?>) eval;
            for (Object object : list) {
                if (operateType==-1) {
                    JSONPath.set(jsonObject, jsonpath, value);
                    continue;
                }
                if (object instanceof JSONArray) {// 当且仅当获取的对象是JSONArray时需要进行判断，因为无法知道是要在指定下标出插入一个元素还是替换指定下标的元素
                    updateJsonArray((JSONArray) object, value, operateType);
                    continue;
                } 
                JSONPath.set(jsonObject, jsonpath, value);
            }
            return;
        }
        /**
         * 对指定对象（包括JSONArray、JSONObject对象）本身进行操作
         */
        if (operateType==-1) {
            JSONPath.set(jsonObject, jsonpath, value);
            return;
        }
        /**
         * 根据jsonpath获得符合规则的一个JSONArray对象
         */
        if (eval instanceof JSONArray) {
            updateJsonArray((JSONArray) eval, value, operateType);
            return;
        }
        JSONPath.set(jsonObject, jsonpath, value);
    }

    /**
     * 更新JSONArray，需要根据operateType来确定执行何种操作
     * @author xule
     * @version 2017年5月3日 下午3:05:38
     * @param 
     * @return void
     */
    private static void updateJsonArray(JSONArray jsonArray, Object value, int operateType) throws Exception {
        switch (operateType) {
        case 0:// 增加
            for (Object object : jsonArray) {
                if (object == null) {
                    continue;
                }
                String string = object.toString();
                if (string.equals(value.toString())) {// 防止JSONArray重复添加元素
                    return;
                }
            }
            jsonArray.add(value);
            break;
        case 1:// 删除
            jsonArray.remove(value);
            break;
        default:
            throw new Exception("无效的operateType，operateType取值范围为[0,1]，且operateType为正整数");
        }
    }
}
