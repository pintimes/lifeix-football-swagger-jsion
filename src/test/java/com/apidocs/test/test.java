package com.apidocs.test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class test {
    public static void main(String[] args) {
        // String text="{\"a\":\"b\",\"$ref\":\"$.a\"} ";
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

        String text1 = "{\"a\":{\"b\":{\"b\":\"c\"}},\"d\":{\"$ref\":\"$.b\"}} ";
        JSONObject jsonObject1 = JSONObject.parseObject(text1);
        System.out.println(jsonObject1);

        String text2 = "{\"a\":{\"b\":\"c\"},\"d\":{\"$ref\":\"$.a\"}} ";
        JSONObject jsonObject2 = JSONObject.parseObject(text2);
        System.out.println(jsonObject2);

        String text7 = "{\"a\":{\"b\":\"c\"},\"d\":{\"$ref\":\"a\"}} ";
        JSONObject jsonObject7 = JSONObject.parseObject(text7);
        System.out.println(jsonObject7);

        String text8 = "{\"a\":{\"b\":\"c\"},\"d\":{\"$ref\":\"#/a\"}} ";
        JSONObject jsonObject8 = JSONObject.parseObject(text8);
        System.out.println(jsonObject8);
    }

    @Test
    public void regtest() {
        String reg_response = "#/definitions/Response«([^\\s'\"]*)»";;
        Pattern p_response = Pattern.compile(reg_response);
        String text = "#/definitions/Response«List«User»»";
        Matcher matcher = p_response.matcher(text);
        while(matcher.find()){
            System.out.println("find!");
            System.out.println(matcher.group(1));
        }
    }
}
