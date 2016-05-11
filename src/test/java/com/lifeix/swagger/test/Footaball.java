package com.lifeix.swagger.test;

import java.io.File;

import com.venizeng.swagger.read.CombineJsons;

public class Footaball {

    public static void main(String[] args) {
        File swaggerFile = new File("src/main/resources/football/app/swagger.json");
        File definationRoot = new File("src/main/resources/football/definitions");
        File pathRoot = new File("src/main/resources/football/app/paths");
        String json = new CombineJsons().toSwaggerJson(swaggerFile, definationRoot, pathRoot);
        System.out.println(json);
        
    }
}
