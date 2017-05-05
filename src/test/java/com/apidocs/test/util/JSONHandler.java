package com.apidocs.test.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.apidocs.test.CreateJsons;
import com.apidocs.test.model.ModifyConfig;
import com.apidocs.test.model.TypeMapper;
import com.lifeix.football.common.util.StringUtil;

/**
 * @author xule
 * @version 2017年5月5日 下午5:04:10
 */
public class JSONHandler {
    public static final String SCOPE_V1 = "v1";

    public static final String SCOPE_V2_INNER = "v2_inner";

    public static final String SCOPE_V2_OUTER = "v2_outer";

    private static final String reg_response = "#/definitions/Response«([^\\s'\"«»]*)»";

    private static final String reg_list = "#/definitions/Response«List«([^\\s'\"«»]*)»»";

    private static final String reg_object = "#/definitions/(?!Response)([^\\s'\"«»]*)";

    private static final Pattern p_response = Pattern.compile(reg_response);

    private static final Pattern p_list = Pattern.compile(reg_list);

    private static final Pattern p_object = Pattern.compile(reg_object);

    private static final List<TypeMapper> TypeMappers = Arrays.asList(
            new TypeMapper("Byte", "byte", "string", "byte", "#/definitions/Response«byte»"),
            new TypeMapper("Character", "char", "string", "", "#/definitions/Response«Character»"),
            new TypeMapper("Boolean", "boolean", "boolean", "", "#/definitions/Response«boolean»"),
            new TypeMapper("Integer", "int", "integer", "int32", "#/definitions/Response«int»"),
            new TypeMapper("Short", "short", "integer", "int32", "#/definitions/Response«int»"),
            new TypeMapper("Long", "long", "integer", "int64", "#/definitions/Response«long»"),
            new TypeMapper("Double", "double", "number", "double", "#/definitions/Response«double»"),
            new TypeMapper("Float", "float", "number", "float", "#/definitions/Response«float»"),
            new TypeMapper("", "string", "string", "", "#/definitions/Response«string»"));

    public static TypeMapper findTypeMapper(String key, String value) throws Exception {
        Class<TypeMapper> clazz = TypeMapper.class;
        Field field = clazz.getDeclaredField(key);
        for (TypeMapper typeMapper : TypeMappers) {
            field.setAccessible(true);
            String value2 = (String) (field.get(typeMapper));
            if (value.equals(value2)) {
                return typeMapper;
            }
        }
        return null;
    }

    public static TypeMapper findTypeMapperByTypeAndFormat(String jsontype, String jsonformat)
            throws Exception {
        for (TypeMapper typeMapper : TypeMappers) {
            if (typeMapper.getJsontype().equals(jsontype) && typeMapper.getJsonformat().equals(jsonformat)) {
                return typeMapper;
            }
        }
        return null;
    }

    /**
     * 将json解析成v1版本的形式
     * 
     * @author xule
     * @version 2017年5月4日 下午4:24:38
     * @param
     * @return void
     * @throws Exception
     */
    public static void handleV1Jsons(JSONObject jsonObject, String system) throws Exception {
        /**
         * 添加key
         */
        JSONPropertiesHandler.addKey(jsonObject);
        /**
         * 修改response code
         */
        String jsonpath1 = "$.paths..responses";
        Object eval1 = JSONPath.eval(jsonObject, jsonpath1);
        List<JSONObject> list1 = (ArrayList<JSONObject>) eval1;
        for (JSONObject jsonObject1 : list1) {
            jsonObject1.put("400", JSONObject.parseObject("{\"description\":\"请求参数错误\"}"));
            jsonObject1.put("401", JSONObject.parseObject("{\"description\":\"未登录\"}"));
            jsonObject1.put("403", JSONObject.parseObject("{\"description\":\"无权访问\"}"));
            jsonObject1.put("404", JSONObject.parseObject("{\"description\":\"无效的API路径\"}"));
            jsonObject1.put("500", JSONObject.parseObject("{\"description\":\"服务器异常\"}"));
            jsonObject1.put("504", JSONObject.parseObject("{\"description\":\"服务器无响应\"}"));
            jsonObject1.put("503", null);
            jsonObject1.put("201", null);
        }
        /**
         * 移除headers
         */
        JSONPropertiesHandler.removeAllParametersHeaders(jsonObject);

    }

    /**
     * 将json解析成服务端内部使用的形式
     * 
     * @author xule
     * @version 2017年4月25日 下午2:01:52
     * @param
     * @return String
     * @throws Exception
     */
    public static void handleV2InnerJsons(JSONObject jsonObject1, String system) throws Exception {
        /**
         * 处理paths
         */
        JSONObject pathsJson = jsonObject1.getJSONObject("paths");
        for (String path : pathsJson.keySet()) {
            JSONObject methodsJson = pathsJson.getJSONObject(path);
            for (String methodKey : methodsJson.keySet()) {
                JSONObject methodJsonObject = methodsJson.getJSONObject(methodKey);

                /**************************************************** 处理responses ********************************************************/
                JSONObject responsesJsonObject = methodJsonObject.getJSONObject("responses");
                /**
                 * 处理responses.200
                 */
                JSONObject responses200JsonObject = responsesJsonObject.getJSONObject("200");
                JSONObject responses200SchemaJsonObject = responses200JsonObject.getJSONObject("schema");
                if (responses200SchemaJsonObject==null) {
                    continue;
                }
                String $refValue = responses200SchemaJsonObject.getString(CreateJsons.temp_$ref_replacer);
                /**
                 * $ref存在，可能使用了Response进行了包装，也可能是其他对象的引用
                 */
                if (!StringUtils.isEmpty($refValue)) {
                    /**
                     * 匹配"#/definitions/Response《xxx》"
                     */
                    Matcher m = p_response.matcher($refValue);
                    boolean flag = false;
                    if (m.find()) {// 匹配成功
                        flag = true;
                        TypeMapper typeMapper = findTypeMapper("response", $refValue);
                        if (typeMapper == null) {// "xxx"为引用类型
                            responses200SchemaJsonObject.put(CreateJsons.temp_$ref_replacer,
                                    "#/definitions/" + StringUtil.firstUpcase(m.group(1)));// 修改返回类型
                        } else {// "xxx"为基本数据类型或string类型
                            responses200SchemaJsonObject.remove(CreateJsons.temp_$ref_replacer);// 移除$ref，改为基本数据类型的书写格式
                            responses200SchemaJsonObject.put("type", typeMapper.getJsontype());// 修改返回类型
                            if (!StringUtils.isEmpty(typeMapper.getJsonformat())) {
                                responses200SchemaJsonObject.put("format", typeMapper.getJsonformat());
                            }
                        }
                    }
                    /**
                     * 匹配"#/definitions/Response《xxx》"失败，尝试匹配"#/definitions/Response《List《xxx》》"
                     */
                    if (!flag) {
                        Matcher m2 = p_list.matcher($refValue);// "#/definitions/Response《xxx》"不会匹配成功，只有"#/definitions/Response《List《xxx》》"才会匹配成功
                        if (m2.find()) {// 匹配成功
                            String dataType = m2.group(1);// 获得数组元素类型
                            TypeMapper typeMapper = findTypeMapper("basetype", dataType);
                            responses200SchemaJsonObject.remove(CreateJsons.temp_$ref_replacer);// 移除$ref，改为基本数据类型的书写格式
                            responses200SchemaJsonObject.put("type", "array");// 修改返回类型
                            JSONObject itemsJsonObject = new JSONObject();
                            if (typeMapper != null) {// #/definitions/Response《List《xxx》》中的xxx是基本数据类型或string类型
                                itemsJsonObject.put("type", typeMapper.getJsontype());
                                if (!StringUtils.isEmpty(typeMapper.getJsonformat())) {
                                    itemsJsonObject.put("format", typeMapper.getJsonformat());
                                }
                            } else {// #/definitions/Response《List《xxx》》中的xxx是引用类型
                                itemsJsonObject.put(CreateJsons.temp_$ref_replacer,
                                        "#/definitions/" + StringUtil.firstUpcase(dataType));
                            }
                            responses200SchemaJsonObject.put("items", itemsJsonObject);
                        }
                    }
                }
                /**
                 * 处理responses其他code
                 */
                JSONPropertiesHandler.handleOtherResponseCode(responsesJsonObject, SCOPE_V2_INNER);
                /**
                 * 处理produces
                 */
                JSONPropertiesHandler.handleProduces(methodJsonObject, responses200SchemaJsonObject);
                /**
                 * 处理parameters
                 */
                JSONPropertiesHandler.handleParameters(methodJsonObject.getJSONArray("parameters"));
            }
        }
        /**
         * 处理basePath
         */
        jsonObject1.put("basePath", jsonObject1.getString("basePath").replaceAll("/football", ""));
        /**
         * 移除headers
         */
        JSONPropertiesHandler.removeAllParametersHeaders(jsonObject1);
        /**
         * 处理其他属性
         */
        List<ModifyConfig> list = Arrays.asList(
        // TODO 处理其他属性
        );
        JSONUtil.modifyJsonAttrs(jsonObject1, list);
    }

    /**
     * 将json解析成客户端使用的形式
     * 
     * @author xule
     * @version 2017年4月25日 下午2:01:54
     * @param system
     * @param
     * @return String
     * @throws Exception
     */
    public static void handleV2OuterJsons(JSONObject jsonObject2, String system) throws Exception {
        /**
         * 处理paths
         */
        JSONObject pathsJson = jsonObject2.getJSONObject("paths");
        for (String path : pathsJson.keySet()) {
            JSONObject methodsJson = pathsJson.getJSONObject(path);
            for (String methodKey : methodsJson.keySet()) {
                JSONObject methodJsonObject = methodsJson.getJSONObject(methodKey);
                /**************************************************** 处理responses ********************************************************/
                JSONObject responsesJsonObject = methodJsonObject.getJSONObject("responses");
                /**
                 * 处理responses.200
                 */
                JSONObject responses200JsonObject = responsesJsonObject.getJSONObject("200");
                /**
                 * 更新200状态码的描述
                 */
                JSONObject responses200SchemaJsonObject = responses200JsonObject.getJSONObject("schema");
                /**
                 * api返回类型为void，引用并创建Response
                 */
                if (responses200SchemaJsonObject==null) {
                    responses200SchemaJsonObject=JSONObject.parseObject("{\""+CreateJsons.temp_$ref_replacer+"\":\"#/definitions/Response\"}");
                    responses200JsonObject.put("schema", responses200SchemaJsonObject);
                }else{
                    String $refValue = responses200SchemaJsonObject.getString(CreateJsons.temp_$ref_replacer);
                    /**
                     * $ref存在，可能使用了Response进行了包装(#/definitions/Response
                     * <Obj>)，也可能是对象的引用(#/definitions/Obj)
                     */
                    if (!StringUtils.isEmpty($refValue)) {
                        /**
                         * 匹配#/definitions/Obj
                         */
                        Matcher m = p_object.matcher($refValue);
                        /**
                         * 匹配成功，说明返回类型为$ref="#/definitions/Obj"形式，对其进行包装。如果匹配失败，则返回类型已经使用了Response进行了包装(
                         * #/definitions/Response<Obj>)，不需要做任何处理
                         */
                        while (m.find()) {
                            String dataType = m.group(1);// 获得返回数据类型
                            responses200SchemaJsonObject.put(CreateJsons.temp_$ref_replacer,
                                    "#/definitions/Response«" + StringUtil.firstUpcase(dataType) + "»");// 修改返回类型
                        }
                    }
                    /**
                     * $ref不存在，则需要使用Response进行包装
                     */
                    else {
                        String responseType = responses200SchemaJsonObject.getString("type");
                        if (!"array".equals(responseType)) {// 返回类型是基本数据类型或string类型
                            String responseFormat = responses200SchemaJsonObject.getString("format");
                            TypeMapper typeMapper = findTypeMapperByTypeAndFormat(
                                    responseType == null ? "" : responseType,
                                            responseFormat == null ? "" : responseFormat);
                            /**
                             * 返回类型是基本数据类型或string类型，对其进行包装
                             */
                            if (typeMapper != null) {
                                responses200SchemaJsonObject.remove("type");
                                responses200SchemaJsonObject.remove("format");
                                responses200SchemaJsonObject.put(CreateJsons.temp_$ref_replacer,
                                        typeMapper.getResponse());
                            }
                        } else {// 返回类型是数组
                            JSONObject itemsJsonObject = responses200SchemaJsonObject.getJSONObject("items");
                            String itemsResponseType = itemsJsonObject.getString("type");
                            String itemsResponseFormat = itemsJsonObject.getString("format");
                            TypeMapper typeMapper2 = findTypeMapperByTypeAndFormat(
                                    itemsResponseType == null ? "" : itemsResponseType,
                                            itemsResponseFormat == null ? "" : itemsResponseFormat);
                            if (typeMapper2 != null) {// 数组元素为基本数据类型或string类型
                                responses200SchemaJsonObject.put(CreateJsons.temp_$ref_replacer,
                                        "#/definitions/Response«List«" + typeMapper2.getBasetype() + "»»");
                            } else {// 数组元素是引用类型
                                String items$ref = itemsJsonObject.getString(CreateJsons.temp_$ref_replacer);
                                if (StringUtils.isEmpty(items$ref)) {
                                    throw new Exception();
                                }
                                Matcher m = p_object.matcher(items$ref);
                                boolean flag = false;
                                while (m.find()) {// 匹配引用类型的数组元素成功
                                    flag = true;
                                    responses200SchemaJsonObject.put(CreateJsons.temp_$ref_replacer,
                                            "#/definitions/Response«List«" + m.group(1) + "»»");
                                }
                                if (!flag) {
                                    throw new Exception("p_object匹配失败，path=" + path + ",method=" + methodKey);
                                }
                            }
                            responses200SchemaJsonObject.remove("type");
                            responses200SchemaJsonObject.remove("items");
                        }
                    }
                }
                /**
                 * 处理responses其他code
                 */
                JSONPropertiesHandler.handleOtherResponseCode(responsesJsonObject, SCOPE_V2_OUTER);
                /**
                 * 处理produces
                 */
                JSONPropertiesHandler.handleProduces(methodJsonObject, responses200SchemaJsonObject);
            }
        }
        /**
         * 添加各方法中所引用的不存在的对象，只处理"#/definitions/Response<xxx>"形式的引用
         */
        JSONPropertiesHandler.addInexistentRefResponseObjs(jsonObject2);
        /**
         * 处理basePath
         */
        jsonObject2.put("basePath", "/v2" + jsonObject2.getString("basePath"));
        /**
         * 移除headers
         */
        JSONPropertiesHandler.removeAllParametersHeaders(jsonObject2);
        /**
         * 添加key
         */
        JSONPropertiesHandler.addKey(jsonObject2);
    }
}
