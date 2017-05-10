package com.apidocs.test.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.apidocs.test.CreateJsons;
import com.apidocs.test.model.ModifyConfig;
import com.apidocs.test.model.TypeMapper;

/**
 * @author xule
 * @version 2017年5月5日 下午4:49:36
 */
public class JSONPropertiesHandler {
    /**
     * 添加各方法中所引用的不存在的对象，只处理"#/definitions/Response<xxx>"形式的引用
     * 
     * @author xule
     * @version 2017年4月27日 下午3:09:35
     * @param
     * @return void
     * @throws Exception
     */
    public static void addInexistentRefResponseObjs(JSONObject jsonObject) throws Exception {
        String reg_refObj = "#/definitions/Response(«([^'\"]*)»)?";
        Pattern p_refObj = Pattern.compile(reg_refObj);
        String jsonString = jsonObject.toJSONString();
        Matcher matcher = p_refObj.matcher(jsonString);
        Set<String> set = new HashSet<>();
        /**
         * 匹配所有Response对象引用，并获取包装对象
         */
        while (matcher.find()) {
            String innerObj = matcher.group(2);
            if (StringUtils.isEmpty(innerObj)) {
                innerObj="";
            }
            set.add(innerObj);// "Response«"+innerObj+"»"
        }
        /**
         * 获得已经存在的引用对象
         */
        JSONObject definitionsJsonObject = jsonObject.getJSONObject("definitions");
        Set<String> definitionsKeySet = definitionsJsonObject.keySet();
        /**
         * 获得不存在的引用对象，并将不存在的引用对象添加到jsonObject中
         */
        for (String innerObj : set) {
            String newRefObjName = "".equals(innerObj)?"Response":("Response«" + innerObj + "»");
            if (definitionsKeySet.contains(newRefObjName)) {// 引用对象已经存在
                continue;
            }
            /**
             * 引用对象不存在，则创建引用对象
             */
            JSONObject refObj = createInexistentRefObj(innerObj,definitionsJsonObject);
            /**
             * 将新创建的引用对象添加到jsonObject中
             */
            definitionsJsonObject.put(newRefObjName, refObj);
        }

    }

    /**
     * 创建经Response包装的引用对象
     * 
     * @author xule
     * @version 2017年4月27日 下午4:12:53
     * @param
     * @return JSONObject
     * @throws Exception
     */
    public static JSONObject createInexistentRefObj(String innerObj,JSONObject definitionsJsonObject) throws Exception {
        String baseResponseObjJsonString = "{description: \"通用的接口返回结构\"," + "type: \"object\","
                + "properties: {code: {type: \"string\",description: \"返回类型为Response对象时，code属性（非http状态码）说明："
                + "200 请求成功；400 请求参数错误；401 未登录；403 无权访问（如以visitor身份访问user才能访问的api）；500 服务器异常\"}," + "message: {type: \"string\"}}}";
        /**
         * 获得基本的Response对象
         */
        JSONObject baseResponseJsonObject = JSONObject.parseObject(baseResponseObjJsonString);
        JSONObject baseResponsePropertiesJsonObject = baseResponseJsonObject.getJSONObject("properties");
        /**
         * innerObj==""，说明Response内部没有包装任何对象
         */
        if ("".equals(innerObj)) {
            return baseResponseJsonObject;
        }
        /**
         * 判断innerObj是否已经定义，如果已经定义，则直接包装并返回
         */
        if (definitionsJsonObject.containsKey(innerObj)) {
            baseResponsePropertiesJsonObject.put("data", JSONObject.parseObject("{\""+CreateJsons.temp_$ref_replacer+"\":\"#/definitions/" + innerObj+"\"}"));
            return baseResponseJsonObject;
        }
        /**
         * 判断innerObj是否为基本数据类型或string类型
         */
        TypeMapper typeMapper = JSONHandler.findTypeMapper("responseInnerObj", innerObj);
        JSONObject dataJsonObject = new JSONObject();
        /**
         * innerObj是基本数据类型或string类型
         */
        if (typeMapper != null) {
            dataJsonObject.put("type", typeMapper.getJsontype());
            if (!StringUtils.isEmpty(typeMapper.getJsonformat())) {
                dataJsonObject.put("format", typeMapper.getJsonformat());
            }
        }
        /**
         * innerObj是引用类型或数组
         */
        else {
            String reg_list = "^List«([^\\s'\"]*)»$";
            Pattern p_list = Pattern.compile(reg_list);
            Matcher matcher = p_list.matcher(innerObj);
            boolean flag = false;
            /**
             * innerObj是数组
             */
            while (matcher.find()) {
                flag = true;
                dataJsonObject.put("type", "array");
                String listInnerObj = matcher.group(1);
                TypeMapper typeMapper2 = JSONHandler.findTypeMapper("basetype", listInnerObj);
                JSONObject itemsJsonObject = new JSONObject();
                if (typeMapper2 != null) {// innerObj数组元素为基本数据类型或string类型
                    itemsJsonObject.put("type", typeMapper2.getJsontype());
                    if (!StringUtils.isEmpty(typeMapper2.getJsonformat())) {
                        itemsJsonObject.put("format", typeMapper2.getJsonformat());
                    }
                } else {// innerObj数组元素为引用类型
                    itemsJsonObject.put(CreateJsons.temp_$ref_replacer, "#/definitions/" + listInnerObj);
                }
                dataJsonObject.put("items", itemsJsonObject);
            }
            /**
             * innerObj是引用类型
             */
            if (!flag) {
                dataJsonObject.put(CreateJsons.temp_$ref_replacer, "#/definitions/" + innerObj);
            }
        }
        /**
         * 给Response设置data属性
         */
        baseResponsePropertiesJsonObject.put("data", dataJsonObject);
        return baseResponseJsonObject;
    }

    /**
     * 处理responses除200以外的其他code
     * 
     * @author xule
     * @version 2017年4月27日 下午4:04:59
     * @param
     * @return void
     */
    public static void handleOtherResponseCode(JSONObject responsesJsonObject, String scope) {
        /**
         * 移除除200以外的其他所有code
         */
        List<String> toRemove = new ArrayList<>();
        for (String key : responsesJsonObject.keySet()) {
            if (!"200".equals(key)) {
                toRemove.add(key);
            }
        }
        for (String key : toRemove) {
            responsesJsonObject.remove(key);
        }
        /**
         * 添加新的code
         */
        switch (scope) {
        case JSONHandler.SCOPE_V2_OUTER:
            responsesJsonObject.put("404", JSONObject.parse("{\"description\":\"无效的API路径\"}"));
            responsesJsonObject.put("504", JSONObject.parse("{\"description\":\"服务器无响应\"}"));
            break;
        default:
            break;
        }
    }

    /**
     * 处理produces
     * 
     * @author xule
     * @version 2017年4月27日 下午4:07:25
     * @param
     * @return void
     */
    public static void handleProduces(JSONObject methodJsonObject, JSONObject responses200SchemaJsonObject) {
        JSONArray producesJsonArray = methodJsonObject.getJSONArray("produces");
        if (producesJsonArray.size() != 0) {
            producesJsonArray.remove(0);
        }
        if (responses200SchemaJsonObject==null) {
            producesJsonArray.add(MimeTypeUtils.TEXT_PLAIN_VALUE);// "text/plain"
            return;
        }
        String responseType = responses200SchemaJsonObject.getString("type");
        String responseFormat = responses200SchemaJsonObject.getString("format");
        if ("string".equals(responseType) && StringUtils.isEmpty(responseFormat)) {// 接口返回类型为String
            producesJsonArray.add(MimeTypeUtils.TEXT_PLAIN_VALUE);// "text/plain"
        } else {
            producesJsonArray.add(MimeTypeUtils.APPLICATION_JSON_VALUE);// "application/json"
        }
    }

    /**
     * 处理parameters:移除所有header、处理map形式的参数
     * 
     * @author xule
     * @version 2017年5月4日 下午2:11:27
     * @param parametersJsonArray 
     * @param
     * @return void
     */
    public static void handleParameters(JSONArray parametersJsonArray, JSONObject definitionsJsonObject) {
        if (parametersJsonArray==null) {
            parametersJsonArray=new JSONArray();
        }
        List<String> exclude = Arrays.asList("header");
        List<Object> toRemoveList = new ArrayList<>();
        for (Object object : parametersJsonArray) {
            JSONObject jsonObject=(JSONObject) object;
            String in = jsonObject.getString("in");
            /**
             * 收集需要移除的header参数
             */
            if (exclude.contains(in)) {
                toRemoveList.add(object);
                continue;
            }
            /**
             * 处理map参数
             */
            JSONObject itemsJsonObject = jsonObject.getJSONObject("items");
            if (itemsJsonObject==null) {
                continue;
            }
            if (itemsJsonObject.containsKey("additionalProperties")) {//参数是map形式
                jsonObject.remove("items");
                jsonObject.put("schema", JSONObject.parseObject("{\""+CreateJsons.temp_$ref_replacer+"\":\"#/definitions/Map«key,value»\"}"));
                jsonObject.put("in", "body");
                /**
                 * 在definitions中添加Map定义，如果Map已经存在，则不做任何操作
                 */
                defineMap(definitionsJsonObject);
            }
        }
        parametersJsonArray.removeAll(toRemoveList);
    }

    /**
     * 在definitions中添加Map定义，如果Map已经存在，则不做任何操作
     * @author xule
     * @version 2017年5月9日 上午10:57:45
     * @param definitionsJsonObject 
     * @param 
     * @return void
     */
    public static void defineMap(JSONObject definitionsJsonObject) {
        JSONObject mapJsonObject = definitionsJsonObject.getJSONObject("Map«key,value»");
        if (mapJsonObject!=null) {//已存在map定义
            return;
        }
        /**
         * 不存在map定义，则添加
         */
        definitionsJsonObject.put("Map«key,value»", JSONObject.parseObject("{\"type\": \"object\",\"additionalProperties\":{\"type\": \"string\"}}"));
    }

    /**
     * 移除所有Parameters.header，处理map形式的参数
     * 
     * @author xule
     * @version 2017年5月5日 下午4:50:19
     * @param
     * @return void
     */
    public static void handleParameters(JSONObject jsonObject) {
        String jsonpath2 = "$.paths..parameters";
        Object eval2 = JSONPath.eval(jsonObject, jsonpath2);
        List<JSONArray> list2 = (ArrayList<JSONArray>) eval2;
        for (JSONArray jsonArray : list2) {
            handleParameters(jsonArray,jsonObject.getJSONObject("definitions"));
        }
    }

    /**
     * 添加通用的api参数key
     * 
     * @author xule
     * @version 2017年5月5日 下午4:57:24
     * @param
     * @return void
     */
    public static void addKey(JSONObject jsonObject) throws Exception {
        List<ModifyConfig> list = Arrays.asList(
                new ModifyConfig("$.paths..parameters",
                        JSONObject.parseObject("{\"" + CreateJsons.temp_$ref_replacer
                                + "\": \"#/parameters/apiKeyHeader\"}"),
                        0),
                new ModifyConfig("$.parameters.apiKeyHeader",
                        JSONObject.parseObject(
                                "{" + "\"description\": \"key，所有api都需要该参数，用户未登录状态下为key=visitor，用户登录状态下为用户key\","
                                        + "\"name\": \"key\"," + "\"type\": \"string\","
                                        + "\"required\": true," + "\"in\": \"header\"}")));
        JSONUtil.modifyJsonAttrs(jsonObject, list);
    }

    /**
     * 替换所有map的引用为#/definitions/Map<key,value>
     * @author xule
     * @version 2017年5月9日 下午2:11:41
     * @param 
     * @return void
     */
    public static JSONObject replaceAllRefMap(JSONObject jsonObject) {
        String reg="#/definitions/Map(«[^'\"]*,[^'\"]*»)?";
        String jsonString = jsonObject.toJSONString();
        jsonString=jsonString.replaceAll(reg, "#/definitions/Map«key,value»");
        JSONObject parseObject = JSONObject.parseObject(jsonString);
        return parseObject;
    }
    
    /**
     * 添加额外的对象定义，如List
     * @author xule
     * @version 2017年5月9日 下午3:36:47
     * @param 
     * @return void
     */
    public static void addExtraObjsDefinitions(JSONObject definitionsJsonObject) {
        String listObjString="{\"type\":\"object\",\"description\": \"有序对象集合，元素可重复\"}";
        String setObjString="{\"type\":\"object\",\"description\": \"无序对象集合，元素不可重复\"}";
        String mapObjString="{\"type\":\"object\",\"additionalProperties\": {\"type\":\"string\"}}";
        if (!definitionsJsonObject.containsKey("List")) {
            definitionsJsonObject.put("List", JSONObject.parseObject(listObjString));
        }
        if (!definitionsJsonObject.containsKey("Set")) {
            definitionsJsonObject.put("Set", JSONObject.parseObject(setObjString));
        }
        if (!definitionsJsonObject.containsKey("Map")) {
            definitionsJsonObject.put("Map", JSONObject.parseObject(mapObjString));
        }
    }

}
