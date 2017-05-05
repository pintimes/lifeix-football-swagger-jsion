//package com.apidocs.test;
//
//import java.net.MalformedURLException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import org.junit.Test;
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.StringUtils;
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.fastjson.JSONPath;
//import com.apidocs.test.model.ModifyConfig;
//import com.apidocs.test.util.JSONUtil;
//import com.apidocs.test.util.SmbUtil;
//
//import jcifs.smb.SmbException;
//import jcifs.smb.SmbFile;
//
///**
// * 更新json文件，支持对json文件属性的增、删、该操作
// * 更新jsons操作步骤：
// * （1）指定文件（一个或多个）
// * （2）指定属性
// * （3）指定新属性值
// * （4）指定操作类型
// * 
// * 1、指定文件
// * （1）指定文件夹列表（默认对inner和outer均生效）
// * （2）指定文件名列表（如["User.json","Wemedia.json"]，参数类型 List<String>，默认对inner和outer均生效）
// * 
// * 2、指定属性
// * 假设文档内容为{"A":{"B":{"C":["D1","D2"]}}}，其中包含3个json对象，1个jsonarray对象
// * 例如：
// * （1）需要更新{"A":{"B":{"C":["D1","D2"]}}}对象的属性A，则该参数为"$.A"
// * （2）需要更新{"B":{"C":["D1","D2"]}}对象的属性B，则该参数为"$.A.B"
// * （3）需要更新{"C":["D1","D2"]}对象的属性C，则该参数为"$.A.B.C"
// * （4）需要删除{"C":["D1","D2"]}对象的属性C中的D1的值，则该参数为"$.A.B.C"，然后指定操作类型为1即可
// * （5）需要在{"A":"B"}中添加属性C:D，则该参数为"$.C"，然后指定属性值为D即可
// * 
// * 3、指定新属性值
// * 任意类型：Object
// * 
// * 【额外参数】：指定操作类型
// * 当且仅当指定属性为JSONArray对象时该参数才有效
// * -1 对JSONArray对象本身进行操作，如：将该对象替换成其他对象；
// *  0 对JSONArray对象中的元素进行操作，在JSONArray中添加元素；
// *  1 对JSONArray对象中的元素进行操作，删除JSONArray中的元素。
// * 
// * 备注：对key的值（可能是String、JSONArray、JSONObject类型）进行操作时，如果指定key不存在，则添加该键值对；如果存在，则修改属性值；如果指定属性值为null，则删除指定key
// * 
// * @author xule
// * @version 2017年5月2日 上午9:22:17
// */
//public class UpdateJsons {
//    @Test
//    public static void doUpdate() throws Exception {
//        List<String> dirs=Arrays.asList(CreateJsons.SCOPE_V2_OUTER);
//        List<ModifyConfig> list=Arrays.asList(
//                new ModifyConfig(dirs,null,"$.paths..parameters", JSONObject.parseObject("{\""+CreateJsons.temp_$ref_replacer+"\": \"#/parameters/apiKeyHeader\"}"), 0),
//                new ModifyConfig(dirs,null,"$.parameters.apiKeyHeader", JSONObject.parseObject("{"
//                        + "\"description\": \"key，所有api都需要该参数，用户未登录状态下为key=visitor，用户登录状态下为用户key\","
//                        + "\"name\": \"key\","
//                        + "\"type\": \"string\","
//                        + "\"required\": true,"
//                        + "\"in\": \"header\"}"))
//        );
//        for (ModifyConfig modifyConfig : list) {
//            update(modifyConfig.getScopes(),modifyConfig.getSystems(),modifyConfig.getJsonpath(),modifyConfig.getValue(),modifyConfig.getOperateType());
//        }
//        System.out.println("UpdateJsons success!");
//    }
//    
//    /**
//     * 修改json文件内容
//     * @author xule
//     * @version 2017年5月3日 下午4:05:27
//     * @param 
//     * @return void
//     */
//    public static void update(List<String> dirs,List<String> fileNames,String jsonpath,Object value,int operateType) throws Exception {
//        /**
//         * 参数校验
//         */
//        validate(dirs,fileNames,jsonpath,value);
//        /**
//         * 获得指定文件列表
//         */
//        List<SmbFile> files=getJsonFiles(dirs,fileNames);
//        /**
//         * 更新指定的json对象属性
//         */
//        updateJsonsAttrs(files,jsonpath,value,operateType);
//    }
//
//    /**
//     * 参数校验
//     * @author xule
//     * @version 2017年5月2日 下午1:36:09
//     * @param 
//     * @return void
//     * @throws Exception 
//     */
//    private static void validate(List<String> dirs, List<String> fileNames, String jsonpath, Object value) throws Exception {
//        if (StringUtils.isEmpty(jsonpath)) {
//            throw new Exception("attr不能为空！");
//        }
//    }
//
//    /**
//     * 指定文件夹列表，以及文件名列表（如["User.json","Wemedia.json"]，参数类型 List<String>）
//     * @author xule
//     * @version 2017年5月2日 上午10:15:40
//     * @param dir 文件夹，缺省时默认对inner和outer文件夹均生效
//     * @param fileNames 文件名列表，缺省时默认对inner和outer文件夹中的所有json文件均生效
//     * @return List<SmbFile>
//     * @throws MalformedURLException 
//     */
//    private static List<SmbFile> getJsonFiles(List<String> dirs,List<String> fileNames) throws Exception{
//        List<SmbFile> files=new ArrayList<>();
//        if (CollectionUtils.isEmpty(dirs)) {
//            dirs=Arrays.asList(CreateJsons.SCOPE_V2_INNER,CreateJsons.SCOPE_V2_OUTER);
//        }
//        if (CollectionUtils.isEmpty(fileNames)) {
//            fileNames=getAllJsonFileNames();
//        }
//        for (String dir : dirs) {
//            for (String fileName : fileNames) {
//                String filePath=CreateJsons.SMBDIR+dir+"/"+fileName;
//                try {
//                    files.add(SmbUtil.getSmbfile(filePath));
//                } catch (MalformedURLException e) {
//                }
//            }
//        }
//        return files;
//    }
//    
//    /**
//     * @author xule
//     * @version 2017年5月2日 上午11:17:26
//     * @param 
//     * @return List<String>
//     * @throws MalformedURLException 
//     * @throws SmbException 
//     */
//    private static List<String> getAllJsonFileNames() throws Exception {
//        List<String> dirPaths=Arrays.asList(CreateJsons.SMBDIR+CreateJsons.SCOPE_V2_INNER+"/",CreateJsons.SMBDIR+CreateJsons.SCOPE_V2_OUTER+"/");
//        Set<String> allJsonFileNames=new HashSet<>();
//        for (String dirPath : dirPaths) {
//            SmbFile smbFile = SmbUtil.getSmbfile(dirPath);
//            SmbFile[] listFiles = smbFile.listFiles();
//            for (int i = 0,len=listFiles.length; i < len; i++) {
//                allJsonFileNames.add(listFiles[i].getName());
//            }
//        }
//        return new ArrayList<>(allJsonFileNames);
//    }
//
//    /**
//     * @author xule
//     * @version 2017年5月2日 上午10:35:17
//     * @param 
//     * @return void
//     * @throws Exception 
//     */
//    private static void updateJsonsAttrs(List<SmbFile> files, String jsonpath, Object value,int operateType) throws Exception {
//        if (CollectionUtils.isEmpty(files)) {
//            throw new Exception("找不到指定的json文件，更新失败！");
//        }
//        for (SmbFile smbFile : files) {
//            /**
//             * 读取json文件内容
//             */
//            String content = SmbUtil.readFile(smbFile);
//            /**
//             * 将文件内容解析为json对象
//             */
//            JSONObject jsonObject = JSONObject.parseObject(content.replaceAll("\\$ref", CreateJsons.temp_$ref_replacer));
//            /**
//             * 更新json对象中的指定属性
//             */
//            JSONUtil.updateJsonAttrs(jsonObject,jsonpath,value,operateType);
//            /**
//             * 回写json文件
//             */
//            String jsonString = jsonObject.toJSONString();
//            System.out.println(jsonString);
//            jsonString=jsonString.replaceAll(CreateJsons.temp_$ref_replacer, "\\$ref");
//            SmbUtil.writeFile(smbFile.getPath(), jsonString);
//        }
//    }
//
//    
//}
