package com.apidocs.test.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.apidocs.test.util.SmbUtil;

import javassist.expr.Instanceof;

public class test {
    static{
        JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.DisableCircularReferenceDetect.getMask();
    }
    public static void main(String[] args) throws Exception {
        // String text="{\"a\":\"b\",\"ref\":\"$.a\"} ";
        // JSONObject jsonObject=JSONObject.parseObject(text);
        // System.out.println(jsonObject);
        //
        // String text5="{\"a\":{\"b\":\"c\"},\"$ref\":\"$.a\"} ";
        // JSONObject jsonObject5=JSONObject.parseObject(text5);
        // System.out.println(jsonObject5);
        //
        // String text1="{\"a\":\"b\",\"d\":{\"$ref\":\"$.a\"}} ";
        // JSONObject jsonObject1=JSONObject.parseObject(text1);
        // System.out.println(jsonObject1);
        //
        // String text2="{\"a\":{\"b\":\"c\"},\"d\":{\"$ref\":\"$.a\"}} ";
        // JSONObject jsonObject2=JSONObject.parseObject(text2);
        // System.out.println(jsonObject2);
        //
        // String text3="{\"a\":{\"b\":\"c\"},\"d\":{\"$ref\":\"$.b\"}} ";
        // JSONObject jsonObject3=JSONObject.parseObject(text3);
        // System.out.println(jsonObject3);
        //
        // String text4="{\"a\":{\"b\":{\"c\":\"c\"}},\"d\":{\"$ref\":\"$.b\"}} ";
        // JSONObject jsonObject4=JSONObject.parseObject(text4);
        // System.out.println(jsonObject4);

        // String text6="{\"a\":{\"b\":\"c\"},\"d\":{\"$ref\":\"$.a\",\"e\":\"f\"}} ";
        // JSONObject jsonObject6=JSONObject.parseObject(text6);
        // System.out.println(jsonObject6);
        //
        // String text1 = "{\"a\":{\"b\":{\"b\":\"c\"}},\"d\":{\"$ref\":\"$.b\"}} ";
        // JSONObject jsonObject1 = JSONObject.parseObject(text1);
        // System.out.println(jsonObject1);
        //
        // String text2 = "{\"a\":{\"b\":\"c\"},\"d\":{\"$ref\":\"$.a\"}} ";
        // JSONObject jsonObject2 = JSONObject.parseObject(text2);
        // System.out.println(jsonObject2);
        //
        // String text7 = "{\"a\":{\"b\":\"c\"},\"d\":{\"$ref\":\"a\"}} ";
        // JSONObject jsonObject7 = JSONObject.parseObject(text7);
        // System.out.println(jsonObject7);
        //
        // String text8 = "{\"a\":{\"b\":\"c\"},\"d\":{\"$ref\":\"#/a\"}} ";
        // JSONObject jsonObject8 = JSONObject.parseObject(text8);
        // System.out.println(jsonObject8);
        //
        // String text8 =
        // "{\"a\":{\"b\":\"c\",\"g\":{\"e\":\"f\"}},\"d\":{\"e\":\"f\",\"h\":[\"s1\"]}} ";
        // JSONObject jsonObject8 = JSONObject.parseObject(text8);
        // System.out.println(jsonObject8);
        //
        // String jsonpath="$..a";
        // Object eval = JSONPath.eval(jsonObject8, jsonpath);
        // System.out.println(eval);
        // System.out.println(eval instanceof JSONArray);
        // System.out.println(eval instanceof JSONObject);
        // System.out.println(eval instanceof String);
        // System.out.println(eval.getClass());
        // System.out.println(JSONPath.size(jsonObject8, "d.h"));
        // JSONPath.set(jsonObject8, "d.h[" + JSONPath.size(jsonObject8, "d.h") + "]", "s2");
        // System.out.println(jsonObject8);
        // JSONPath.set(jsonObject8, "d", null);
        // System.out.println(jsonObject8);

        // String text8 =
        // "{\"a\":{\"x\":[{\"e\":\"f\",\"e2\":\"f2\"}]},\"b\":{\"x\":[{\"e\":\"f\"}]},\"c\":{\"x\":[{\"e\":\"f\"}]}}
        // ";
        // String text8 =
        // "{\"a\":{\"x\":{\"e\":{\"e\":\"f\"}}},\"b\":{\"x\":[\"s1\"]},\"c\":{\"x\":[\"s1\",\"s2\"]}}
        // ";
//        String text8 = "{\"x\":[\"e1\",\"e2\"]}";
//        String text8 = "{\"a\":{\"x\":[{\"in\":\"header\"},{\"in\":\"header2\"}]},\"b\":{\"x\":[\"e1\",\"e2\"]}}";
//        JSONObject jsonObject8 = JSONObject.parseObject(text8);
//        System.out.println(jsonObject8);
//        String jsonpath = "$..x[in='header']";
//        Object eval = JSONPath.eval(jsonObject8, jsonpath);
//        System.out.println(eval);
//        System.out.println(eval instanceof JSONArray);
//        System.out.println(eval instanceof JSONObject);
//        System.out.println(eval instanceof String);
//        System.out.println(eval.getClass());
//        // JSONPath.set(jsonObject8, jsonpath, "f1");
//        // System.out.println(jsonObject8);
////        JSONPath.arrayAdd(jsonObject8, jsonpath, "f1");
//         updateJsonAttr(jsonObject8, jsonpath, "b", -1);
//        System.out.println(jsonObject8);
//
//        SerializeConfig serializeConfig;
//        ParserConfig parserConfig=new ParserConfig();
//        String text = "{\"a\":{\"x\":[\"e1\",\"e2\"]},\"b\":{\"x\":[\"e1\",\"e2\"]}}";
//        JSONObject jsonObject = JSONObject.parseObject(text);
//        System.out.println(jsonObject);
//        String jsonpath = "$..x";
//        String value="y";
//        JSONPath.set(jsonObject, jsonpath, value);
//        System.out.println(jsonObject);
//        SmbUtil.createDirs("smb://192.168.1.17/fb/temp/api/user/");
//        SmbUtil.writeFile("smb://192.168.1.17/fb/temp/api/user/user.json", "123");
        System.out.println(Thread.currentThread().getContextClassLoader().getResource("").getPath());
    }

    @Test
    public void testUpdateJsonAttr() throws Exception {
        /**
         * 如："produces": [ "xx/xx" ], 形式，需要添加"application/json"元素
         */
        String text = "{\"a\":{\"c\":\"d\"},\"b\":\"e\"} ";
        JSONObject jsonObject = JSONObject.parseObject(text);
        System.out.println(jsonObject);
        String jsonpath = "$.a";
        updateJsonAttr(jsonObject, jsonpath,"e", 0);
        System.out.println(jsonObject);
        updateJsonAttr(jsonObject, jsonpath,null, 0);
        System.out.println(jsonObject);
        System.out.println("------------------------------------------------------------------");
        /**
         * 如："produces": [ "xx/xx" ], 形式，需要添加"application/json"元素
         */
        String text1 = "{\"a\":{\"x\":[\"e1\",\"e2\",\"e3\"]},\"b\":{\"x\":[\"e1\",\"e2\",\"e3\"]}}";
        JSONObject jsonObject1 = JSONObject.parseObject(text1);
        System.out.println(jsonObject1);
        String jsonpath1 = "$..x";
        updateJsonAttr(jsonObject1, jsonpath1, "e1", 1);
        System.out.println(jsonObject1);
        updateJsonAttr(jsonObject1, jsonpath1, "e4", 0);
        System.out.println(jsonObject1);
        updateJsonAttr(jsonObject1, jsonpath1, "e5", -1);
        System.out.println(jsonObject1);
        System.out.println("------------------------------------------------------------------");
        /**
         * 如："parameters": [ {"type": "string"},{"type": "integer"} ], 形式，需要添加{"$ref":
         * "#/difinitions/User"}元素
         */
        String text2 = "{\"a\":{\"x\":[{\"type\": \"string\"},{\"type\": \"integer\"}]},"
                + "\"b\":{\"x\":[{\"type\": \"string\"},{\"type\": \"integer\"}]}} ";
        JSONObject jsonObject2 = JSONObject.parseObject(text2);
        System.out.println(jsonObject2);
        String jsonpath2 = "$..x";
        updateJsonAttr(jsonObject2, jsonpath2, JSONObject.parseObject("{\"￥ref\": \"#/difinitions/User\"}"), 0);
        System.out.println(jsonObject2);
        updateJsonAttr(jsonObject2, jsonpath2, JSONObject.parseObject("{\"￥ref\": \"#/difinitions/User\"}"), 0);
        System.out.println(jsonObject2);
        updateJsonAttr(jsonObject2, jsonpath2, JSONObject.parseObject("{\"￥ref\": \"#/difinitions/User\"}"), 1);
        System.out.println(jsonObject2);
        System.out.println("------------------------------------------------------------------");
        /**
         * 
         */
        String text3 = "{\"a\":{\"x\":[{\"in\":\"header\"},{\"in\":\"header2\"}]},\"b\":{\"x\":[\"e1\",\"e2\"]}}";
        JSONObject jsonObject3 = JSONObject.parseObject(text3);
        System.out.println(jsonObject3);
        String jsonpath3 = "$.a.x[in='header']";
        updateJsonAttr(jsonObject3, jsonpath3, null, 3);
        System.out.println(jsonObject3);

    }

    private static void updateJsonAttr(JSONObject jsonObject, String jsonpath, Object value, int operateType)
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

    private static void updateJsonArray(JSONArray jsonArray, Object value, int operateType) throws Exception {
        switch (operateType) {
        case 0:// 添加元素
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
        case 1:// 删除指定元素
            jsonArray.remove(value);
            break;
//        case 2:// 删除所有元素
//            jsonArray.clear();
//            break;
//        case 3:// 删除JSONArray
//            jsonArray=null;
//            break;
        default:
            throw new Exception("无效的operateType，operateType取值范围为[0,1]，且operateType为正整数");
        }
    }

    @Test
    public void regtest() {
        String reg_response = "#/definitions/(?!Response)([^\\s'\"]*)";
        Pattern p_response = Pattern.compile(reg_response);
        String text = "#/definitions/Map«key,value»";
        Matcher matcher = p_response.matcher(text);
        while (matcher.find()) {
            System.out.println("find!");
            System.out.println(matcher.group(1));
        }
    }

    @Test
    public void regtest2() {
        String reg = "(.*)\\[(\\d+)\\]$";
        Pattern p = Pattern.compile(reg);
        String text = "a.b[100]";
        StringBuffer sb = new StringBuffer(text);
        Matcher matcher = p.matcher(text);
        while (matcher.find()) {
            System.out.println("find!");
            System.out.println(matcher.group(1));
            String group = matcher.group(2);
            System.out.println(group);
            String temp = text.replaceAll(group, "10");
            System.out.println(temp);
        }
    }

    @Test
    public void regtest3() {
        String text = "{\"a\":{\"b\":\"c\",\"g\":{\"e\":\"f\"}},\"d\":{\"e\":\"f\",\"h\":[\"s1\",\"s2\"]}}";
        JSONObject jsonObject = JSONObject.parseObject(text);
        System.out.println("jsonObject=" + jsonObject);
        List<String> jsonPaths = getJsonPaths(jsonObject);
        for (String string : jsonPaths) {
            System.out.println(string);
        }
    }

    private static List<String> getJsonPaths(Object object) {
        return getJsonPaths(object, "$");
    }

    private static List<String> getJsonPaths(Object object, String path) {
        List<String> result = new ArrayList<>();
        result.add(path);
        if (object instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) object;
            for (String key : jsonObject.keySet()) {
                result.addAll(getJsonPaths(jsonObject.get(key), path + "." + key));
            }
        } else if (object instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) object;
            for (int i = 0, len = jsonArray.size(); i < len; i++) {
                result.addAll(getJsonPaths(jsonArray.get(i), path + "[" + i + "]"));
            }
        }
        return result;
    }
}
