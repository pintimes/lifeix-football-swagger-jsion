package com.lifeix.swagger.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import com.venizeng.swagger.read.CombineJsons;

public class Footaball {

    public static void main(String[] args) throws Exception {
        File swaggerFile = new File("src/main/resources/football/app/swagger.json");
        File definationRoot = new File("src/main/resources/football/definitions");
        File pathRoot = new File("src/main/resources/football/app/paths");
        String json = new CombineJsons().toSwaggerJson(swaggerFile, definationRoot, pathRoot);
        String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        path = path.substring(0, path.indexOf("target"));
        File file = new File(path + "/football.json");
        PrintWriter out = new PrintWriter(new FileOutputStream(file));
        out.println(json);
        out.close();
    }

}
