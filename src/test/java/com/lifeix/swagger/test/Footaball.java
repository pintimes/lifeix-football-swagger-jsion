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

        File file = new File("D:/football.json");
        if (file.exists()) {
            file.delete();
            file.mkdir();
        }
        PrintWriter out = new PrintWriter(new FileOutputStream(file));
        out.println(json);
        out.close();
    }
}
