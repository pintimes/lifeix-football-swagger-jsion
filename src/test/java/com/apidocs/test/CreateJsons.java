package com.apidocs.test;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.apidocs.test.util.JSONHandler;
import com.apidocs.test.util.SmbUtil;
import com.lifeix.football.common.util.FileUtil;
import com.lifeix.football.common.util.HttpUtil;
import com.lifeix.football.common.util.StringUtil;
import io.swagger.codegen.SwaggerCodegen;

/**
 * @author xule
 * @version 2017年4月25日 下午1:30:40
 */
public class CreateJsons {
    private static final String API_HOST = "http://54.223.127.33:8300/";

    public static final String SMBDIR = "smb://192.168.1.17/fb/temp/api_docs/";

    private static final Map<String, String> map_system_apipath = new HashMap<>();

    public static final String temp_$ref_replacer = "￥ref";

    static {
        /**
         * 全局禁止循环引用检查，当使用JSONPath时，如果开启循环引用检查，则所有相同的值都会出现引用同一个值的情况
         */
        JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.DisableCircularReferenceDetect.getMask();
        /**
         * 配置api文档链接
         */
        map_system_apipath.put("decision", API_HOST + "football/decision/v2/api-docs");
        map_system_apipath.put("app", API_HOST + "football/app/v2/api-docs");
        map_system_apipath.put("console", API_HOST + "football/console/v2/api-docs");
        map_system_apipath.put("elearning", API_HOST + "football/elearning/v2/api-docs");
        map_system_apipath.put("wemedia", API_HOST + "football/wemedia/v2/api-docs");
        map_system_apipath.put("mary", API_HOST + "football/mary/v2/api-docs");
        map_system_apipath.put("user", API_HOST + "football/user/v2/api-docs");
    }
 
    //TODO 主流程入口
    public static void main(String[] args) throws Exception {
        /**
         * 获得原始json。 map数据形式 ："user":UAERJSON
         */
        Map<String, String> originalJsons = getOriginalJsons();
        /**
         * 解析json
         */
        List<Map<String, JSONObject>> newJsons = parseOriginalJsons(originalJsons);
        /**
         * 将json生成文件，并写入指定目录的文件夹中
         */
        writeApiJsonFiles(newJsons);
        System.out.println("CreateJsons success!");
    }

    /**
     * 将json生成文件，并写入指定目录的文件夹中
     * 
     * @author xule
     * @version 2017年4月27日 下午5:21:04
     * @param
     * @return void
     * @throws Exception
     */
    private static void writeApiJsonFiles(List<Map<String, JSONObject>> newJsons) throws Exception {
        String apiv1DirPath = SMBDIR + "api/";
        String innerFilePath = SMBDIR + "api-v2/inner/";
        String outerFilePath = SMBDIR + "api-v2/outer/";
        /**
         * 写入文件
         */
        for (int i = 0, size = newJsons.size(); i < size; i++) {
            Map<String, JSONObject> map = newJsons.get(i);
            for (String system : map.keySet()) {
                JSONObject json = map.get(system);
                System.out.println(json);
                System.out.println(json.toJSONString());
                String text = json.toJSONString().replaceAll(temp_$ref_replacer, "\\$ref");
                /**
                 * 创建文件夹
                 */
                String dirpath = (i == 0 ? apiv1DirPath : (i == 1 ? innerFilePath : outerFilePath)) + system
                        + "/";
                SmbUtil.createDirs(dirpath);
                /**
                 * 写入json文件
                 */
                String filePath = dirpath + StringUtil.firstUpcase(system) + ".json";
                SmbUtil.writeFile(filePath, text);
                /**
                 * 写入html文件
                 */
                String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
                path = path.substring(0, path.indexOf("target"));
                File dir = new File(path + "/temp/");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File file = new File(dir.getAbsolutePath() + File.separator + system + ".json");
                FileUtil.writeContent(file, text);// 将json文件写入工程中
                SwaggerCodegen.main(new String[] { "generate", "-i", file.getAbsolutePath(), "-l", "html",
                        "-o", dir.getAbsolutePath() });
                File html = new File(dir.getAbsolutePath() + "/index.html");
                SmbUtil.writeFile(html, SmbUtil.getSmbfile(dirpath + "index.html"));
                /**
                 * 写入html2文件
                 */
                SwaggerCodegen.main(new String[] { "generate", "-i", file.getAbsolutePath(), "-l", "html2",
                        "-o", dir.getAbsolutePath() });
                SmbUtil.writeFile(html, SmbUtil.getSmbfile(dirpath + "index2.html"));
            }

        }
    }

    /**
     * 解析原始json
     * 
     * @author xule
     * @version 2017年4月27日 下午5:18:48
     * @param
     * @return List<List<JSONObject>>
     */
    private static List<Map<String, JSONObject>> parseOriginalJsons(Map<String, String> originalJsons)
            throws Exception {
        Map<String, JSONObject> mapV1 = new HashMap<>();
        Map<String, JSONObject> mapV2Inner = new HashMap<>();
        Map<String, JSONObject> mapV2Outer = new HashMap<>();
        for (String system : originalJsons.keySet()) {
            String jsonString = originalJsons.get(system);
            System.out.println(jsonString);
            JSONObject jsonObject_v1=null;
            try {
                jsonObject_v1 = JSONObject.parseObject(jsonString);// v1版本
            } catch (Exception e) {
                continue; 
            }
            JSONObject jsonObject_v2_inner = JSONObject.parseObject(jsonString);// v2版本内部使用
            JSONObject jsonObject_v2_outer = JSONObject.parseObject(jsonString);// v2版本外部使用
            /**
             * 分别处理内部和外部使用的json对象
             */
            JSONHandler.handleV1Jsons(jsonObject_v1, system);
            JSONHandler.handleV2InnerJsons(jsonObject_v2_inner, system);
            JSONHandler.handleV2OuterJsons(jsonObject_v2_outer, system);

            mapV1.put(system, jsonObject_v1);
            mapV2Inner.put(system, jsonObject_v2_inner);
            mapV2Outer.put(system, jsonObject_v2_outer);
        }
        return Arrays.asList(mapV1, mapV2Inner, mapV2Outer);
    }

    /**
     * 发送http请求，获得原始json数据
     * 
     * @author xule
     * @version 2017年4月25日 下午2:02:46
     * @param
     * @return List<String>
     */
    private static Map<String, String> getOriginalJsons() throws Exception {
        Map<String, String> map = new HashMap<>();
        System.out.println(map_system_apipath.size());
        for (String system : map_system_apipath.keySet()) {
            String url = map_system_apipath.get(system);
            try {
                String result = HttpUtil.sendGet(url);
                System.out.println(result);
                if (StringUtils.isEmpty(result)) {
                    continue;
                }
                if (result.contains("\"error\":\"Not Found\"")||result.contains("api not found")) {
                    continue;
                }
                /**
                 * 使用￥ref临时替换$ref，防止出现循环引用
                 */
                result = result.replaceAll("\\$ref", temp_$ref_replacer);
                map.put(system, result);
            } catch (Exception e) {
                throw new Exception("getOriginalJsons error, message:" + e.getMessage());
            }
        }
        return map;
    }
}
