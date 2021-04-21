package com.test.day01;

import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIConversion;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

/**
 * @Project:java26_maven_jiekou
 * @Forum:http://testingpai.com
 * @Copyright:2021
 * @Author:xt
 * @Date:2021-04-02 16:33
 * @DESC:
 **/
public class HomeworkInvest {
    static String mobilePhone="17755551111";
    static String pwd="12345678";
    static int type=1;
    static  int memberId;
    static String token;
    @Test
    public void testRegister(){
        String  jsonData  =  "{\"mobile_phone\":"+mobilePhone+",\"pwd\":"+pwd+",\"type\":"+type+"}";
        Response res =
                given().
                        body(jsonData).
                        header("Content-Type","application/json").//等价于contentType("application/json")
                        header("X-Lemonban-Media-Type","lemonban.v2").
                when().
                        post("http://api.lemonban.com/futureloan/member/register").
                then().
                        //获取所有的响应数据
                        log().all().extract().response();
    }

    @Test(dependsOnMethods = "testRegister")
    public void testLogin(){
        String  jsonData1 =  "{\"mobile_phone\":"+mobilePhone+",\"pwd\":"+pwd+"}";
        Response response =
                given().
                        body(jsonData1).
                        header("Content-Type","application/json").//等价于contentType("application/json")
                        header("X-Lemonban-Media-Type","lemonban.v2").
                when().
                        post("http://api.lemonban.com/futureloan/member/login").
                then().
                        //获取所有的响应数据
                        log().all().extract().response();
        //1、获取响应数据的member_id
        memberId=response.jsonPath().get("data.id");
        //2、获取响应数据的token
        token=response.jsonPath().get("data.token_info.token");
        System.out.println(memberId);
        System.out.println(token);
    }

    @Test(dependsOnMethods = "testLogin")
    public void testRecharge(){
        String jsonData2  =  "{\"member_id\":"+memberId+",\"amount\":100000.00}";
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
    @Test(dependsOnMethods ="testRecharge")
    public void testInvest(int loanId){

        String json="{\"member_id\":"+memberId+",\"loan_id\":"+loanId+",\"amount\":10000.00}";
        Response response =
                given().
                        body(json).
                        header("Content-Type","application/json").//等价于contentType("application/json")
                        header("X-Lemonban-Media-Type","lemonban.v2").
                        header("Authorization","Bearer "+token).
                        when().
                        post("http://api.lemonban.com/futureloan/member/invest").
                        then().
                        //获取所有的响应数据
                                log().all().extract().response();
    }





}
