package com.test.day01;

import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import java.io.File;

import static io.restassured.RestAssured.given;//引用不加static，使用given时需要RestAssured.given

/**
 * @Project:java26_maven_jiekou
 * @Forum:http://testingpai.com
 * @Copyright:2021
 * @Author:xt
 * @Date:2021-03-29 16:24
 * @DESC:
 **/
public class RestAssuredDemo {
    @Test
    public void firstGetRequest(){
        given().
                //设置请求：请求头、请求体...
        when().
                get("https://www.baidu.com").
        then().
                log().body();
    }

    @Test//get传参
    public void getDemo01(){
        given().
                queryParam("mobilephone", "13323231212").
                queryParam("password", "123456").
        when().
                get("http://www.httpbin.org/get").
        then().
                log().body();
    }

    @Test//post form表单传参 *********重点掌握
    public void postDemo01(){
        given().
                formParam("mobilephone", "13323234545").
                formParam("password", "123456").
        when().
                post("http://www.httpbin.org/post").
        then().
                log().body();
    }

    @Test//post json传参 *********重点掌握
    public void postDemo02(){
        String  jsonData  =  "{\"mobilephone\":\"13323231212\",\"password\":\"123456\"}";
        given().
                body(jsonData).
                contentType(ContentType. JSON).
        when().
                post("http://www.httpbin.org/post").
        then().
                log().body();
    }

    @Test//post xml传参
    public void postDemo03(){
        String  xmlStr  =  "<?xml  version=\"1.0\"  encoding=\"utf-8\"?>\n"  +
                "<suite>\n"  +
                "        <class>测试xml</class>\n"  +
                "</suite>";
        given().
                body(xmlStr).
                contentType(ContentType.XML).
        when().
                post("http://www.httpbin.org/post").
        then().
                log().body();
    }

    @Test//post multipart表单传参，传文件，传图片
    public void postDemo04(){
        File file=new File("C:\\Users\\xt\\Desktop\\test.txt");
        given().
                multiPart(file).
                //multiPart(new File("C:\\Users\\xt\\Desktop\\test.txt")).
        when().
                post("http://www.httpbin.org/post").
        then().
                log().body();
    }
}
