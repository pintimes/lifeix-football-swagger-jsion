package com.venizeng.swagger.read;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.json.JSONObject;

public class CombineJsons {

    public String toSwaggerJson(File root) {
        // JSON.DEFAULT_GENERATE_FEATURE |=
        // SerializerFeature.DisableCircularReferenceDetect.getMask();
        JSONObject swagger = readSwagger(root.getAbsolutePath() + File.separator + "swagger.json");
        JSONObject paths = readPaths(root.getAbsolutePath() + File.separator + "paths");
        JSONObject definitions = readDefinitions(root.getAbsolutePath() + File.separator + "definitions");
        swagger.put("definitions", definitions);
        swagger.put("paths", paths);
        return swagger.toString(4);
    }

    public String toSwaggerJson(File swaggerFile, File definationRoot, File pathRoot) {
        JSONObject swagger = readSwagger(swaggerFile.getAbsolutePath());
        JSONObject paths = readPaths(pathRoot.getAbsolutePath());
        JSONObject definitions = readDefinitions(definationRoot.getAbsolutePath());
        swagger.put("definitions", definitions);
        swagger.put("paths", paths);
        return swagger.toString(4);
    }

    private JSONObject readSwagger(String filepath) {
        return fromFile(new File(filepath));
    }

    private JSONObject readDefinitions(String definitionsDirfilepath) {
        File dir = new File(definitionsDirfilepath);
        File[] definitionFiles = dir.listFiles();
        JSONObject jsonObject = new JSONObject();
        for (File file : definitionFiles) {
            jsonObject.put(FileUtil.getFileName(file), fromFile(file));
        }
        return jsonObject;
    }

    private JSONObject readPaths(String pathsDirfilepath) {
        JSONObject jsonObject = new JSONObject();
        LinkedHashMap<String, JSONObject> map = readPaths(new File(pathsDirfilepath),
                new File(pathsDirfilepath));
        Set<String> set = map.keySet();
        for (String key : set) {
            jsonObject.put(key, map.get(key));
        }
        return jsonObject;
    }

    private LinkedHashMap<String, JSONObject> readPaths(File root, File dir) {
        LinkedHashMap<String, JSONObject> map = new LinkedHashMap<String, JSONObject>();
        File[] files = dir.listFiles();
        List<File> methods = new ArrayList<File>();
        for (File file : files) {
            if (file.isDirectory()) {
                LinkedHashMap<String, JSONObject> temp = readPaths(root, file);
                map.putAll(temp);
            } else {
                methods.add(file);
            }
        }
        if (methods.isEmpty()) {
            return map;
        }
        JSONObject path = new JSONObject();
        for (File methodFile : methods) {
            System.out.println(methodFile.getAbsolutePath());
            JSONObject jsonObject = fromFile(methodFile);
            String method = FileUtil.getFileName(methodFile);
            path.put(method, jsonObject);
        }
        int rootPathLength = root.getAbsolutePath().length();
        String relativeRoot = dir.getAbsolutePath().substring(rootPathLength, dir.getAbsolutePath().length());
        String removeSlash = StringUtil.removeSlash(relativeRoot);
        map.put(removeSlash, path);
        return map;
    }

    private JSONObject fromFile(File file) {
        String json = FileUtil.readFileContent(file.getAbsolutePath());
        JSONObject jsonObj = new JSONObject(json);
        return jsonObj;
    }

}
