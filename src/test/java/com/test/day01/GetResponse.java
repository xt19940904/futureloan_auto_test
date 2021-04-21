package com.test.day01;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.given;

/**
 * @Project:java26_maven_jiekou
 * @Forum:http://testingpai.com
 * @Copyright:2021
 * @Author:xt
 * @Date:2021-03-29 17:10
 * @DESC:
 **/
public class GetResponse {
    @Test
    public void getResponseHeader(){
        Response response =
            given().
            when().
                post("http://www.httpbin.org/post").
            then().
                log().all().extract().response();
        System.out.println("接口响应时间："+response.time());
        System.out.println(response.getHeader("Content-Type"));
    }

    @Test
    public void getResponseJson01(){
        String  jsonData  =  "{\"mobile_phone\":\"13323231212\",\"pwd\":\"777777771\"}";
        Response response =
                given().
                        //设置请求体
                        body(jsonData).
                        //设置请求头
                        header("Content-Type","application/json").//等价于contentType("application/json")
                        header("X-Lemonban-Media-Type","lemonban.v1").
                when().
                        post("http://api.lemonban.com/futureloan/member/login").
                then().
                        //获取所有的响应数据
                        log().all().extract().response();
        //获取响应数据的member_id
//        System.out.println(response.jsonPath().get("data.id"));
    }

    @Test
    public void getResponseJson02(){
        Response response =
                given().
                when().
                        get("http://www.httpbin.org/json").
                then().
                        //获取所有的响应数据
                        log().all().extract().response();
        //获取响应数据的member_id
        String res=response.jsonPath().get("slideshow.slides.title[0]");//得到的是字符串
        System.out.println(res);
        List<String> list = response.jsonPath().getList("slideshow.slides.title");//得到的集合
        System.out.println(list.get(0));
        System.out.println(list.get(1));
    }

    @Test
    public void getResponseHtml(){
        Response response =
                given().
                when().
                        get("https://www.baidu.com/").
                then().
                        //获取所有的响应数据
                        log().all().extract().response();
        //获取响应数据的title
        //htmlPath().get()只能获取到元素的文本，不能获取到元素的属性(Content-Type=application/json),获取属性需要.@属性名
        //System.out.println(response.htmlPath().get("html.head.title"));//打印出 百度一下，你就知道
        //System.out.println(response.htmlPath().get("html.head.meta"));//打印出  nullnullnull
        //System.out.println(response.htmlPath().getList("html.head.meta"));//打印出  [null,null,null]
        response.htmlPath().get("html.head.meta.@content");// [text/html;charset=utf-8, IE=Edge, always]
        response.htmlPath().get("html.head.meta[0].@content");// text/html;charset=utf-8
    }

    @Test
    public void getResponseXml(){
        Response response =
                given().
                when().
                        get("http://www.httpbin.org/xml").
                then().
                        //获取所有的响应数据
                        log().all().extract().response();
        //获取响应数据的
        response.xmlPath().get("slideshow.slide[1].title");
        response.xmlPath().get("slideshow.slide[1].@type");
    }

    @Test
    public void loginRecharge(){
        String  jsonData  =  "{\"mobile_phone\":\"13323231212\",\"pwd\":\"777777771\"}";
        Response response =
                given().
                        body(jsonData).
                        header("Content-Type","application/json").//等价于contentType("application/json")
                        header("X-Lemonban-Media-Type","lemonban.v2").
                when().
                        post("http://api.lemonban.com/futureloan/member/login").
                then().
                        //获取所有的响应数据
                        log().all().extract().response();
        //1、获取响应数据的member_id
        int memberId=response.jsonPath().get("data.id");
        //2、获取响应数据的token
        String token=response.jsonPath().get("data.token_info.token");
        System.out.println(memberId);
        System.out.println(token);

        //发起“充值”接口请求
        String jsonData2  =  "{\"member_id\":"+memberId+",\"amount\":10000.00}";
        Response response1 =
                given().
                        body(jsonData2).
                        header("Content-Type","application/json").
                        header("X-Lemonban-Media-Type","lemonban.v2").
                        header("Authorization","Bearer "+token).
                when().
                        post("http://api.lemonban.com/futureloan/member/recharge").
                then().
                        log().all().extract().response();
        System.out.println("当前可用余额:"+response1.jsonPath().get("data.leave_amount"));
    }
}
