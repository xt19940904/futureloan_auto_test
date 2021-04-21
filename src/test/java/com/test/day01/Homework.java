package com.test.day01;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

/**
 * @Project:java26_maven_jiekou
 * @Forum:http://testingpai.com
 * @Copyright:2021
 * @Author:xt
 * @Date:2021-04-04 20:12
 * @DESC:
 **/
public class Homework {
    String investMobilePhone="17711114444";
    String investPwd="12345678";
    int investType=1;
    int investMemberId;
    String investToken;
    static String adminMobilePhone="17711119999";
    static String adminPwd="123456789";
    static int adminType=0;
    static String title="平安30万";
    static int adminMemberId;
    static String adminToken;
    static int loanId;
    @Test
    public void testRegister(){
        String  jsonData  =  "{\"mobile_phone\":"+investMobilePhone+",\"pwd\":"+investPwd+",\"type\":"+investType+"}";
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
        String  jsonData1 =  "{\"mobile_phone\":"+investMobilePhone+",\"pwd\":"+investPwd+"}";
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
        investMemberId=response.jsonPath().get("data.id");
        //2、获取响应数据的token
        investToken=response.jsonPath().get("data.token_info.token");
//        System.out.println(investMemberId);
//        System.out.println(investToken);
    }

    @Test(dependsOnMethods = "testLogin")
    public void testRecharge(){
        String jsonData2  =  "{\"member_id\":"+investMemberId+",\"amount\":100000.00}";
        Response response1 =
                given().
                        body(jsonData2).
                        header("Content-Type","application/json").
                        header("X-Lemonban-Media-Type","lemonban.v2").
                        header("Authorization","Bearer "+investToken).
                when().
                        post("http://api.lemonban.com/futureloan/member/recharge").
                then().
                        log().all().extract().response();
        System.out.println("当前可用余额:"+response1.jsonPath().get("data.leave_amount"));
    }
    @Test
    public void testRegister1(){
        String  jsonData  =  "{\"mobile_phone\":"+adminMobilePhone+",\"pwd\":"+adminPwd+",\"type\":"+adminType+"}";
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

    @Test(dependsOnMethods = "testRegister1")
    public void testLogin1(){
        String  jsonData1 =  "{\"mobile_phone\":"+adminMobilePhone+",\"pwd\":"+adminPwd+"}";
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
        adminMemberId=response.jsonPath().get("data.id");
        //2、获取响应数据的token
        adminToken=response.jsonPath().get("data.token_info.token");
        System.out.println(adminMemberId);
        System.out.println(adminToken);
    }

    @Test(dependsOnMethods ="testLogin1")
    public void testAdd(){
        String  jsonData =  "{\"member_id\":"+adminMemberId+",\"title\":\""+title+"\",\"amount\":200000.00,\"loan_rate\":14.0,\"loan_term\":6,\"loan_date_type\":1,\"bidding_days\":10}";
        Response res2=
                given().
                        body(jsonData).
                        header("Content-Type","application/json").//等价于contentType("application/json")
                        header("X-Lemonban-Media-Type","lemonban.v2").
                        header("Authorization","Bearer "+adminToken).
                when().
                        post("http://api.lemonban.com/futureloan/loan/add").
                then().
                        //获取所有的响应数据
                        log().all().extract().response();
        //1、获取响应数据的项目id
        loanId=res2.jsonPath().get("data.id");
        System.out.println(loanId);
    }
    @Test(dependsOnMethods = "testAdd")
    public void testAudit(){
        String json="{\"loan_id\":"+loanId+",\"approved_or_not\":\"true\"}";
        Response response =
                given().
                        body(json).
                        header("Content-Type","application/json").//等价于contentType("application/json")
                        header("X-Lemonban-Media-Type","lemonban.v2").
                        header("Authorization","Bearer "+adminToken).
                when().
                        patch("http://api.lemonban.com/futureloan/loan/audit").
                then().
                        //获取所有的响应数据
                        log().all().extract().response();
    }

    @Test(dependsOnMethods = {"testAudit","testLogin","testRecharge"})
    public void testInvest(){
        String json="{\"member_id\":"+investMemberId+",\"loan_id\":"+loanId+",\"amount\":10000.00}";
        Response response =
                given().
                        body(json).
                        header("Content-Type","application/json").//等价于contentType("application/json")
                        header("X-Lemonban-Media-Type","lemonban.v2").
                        header("Authorization","Bearer "+investToken).
                when().
                        post("http://api.lemonban.com/futureloan/member/invest").
                then().
                        //获取所有的响应数据
                        log().all().extract().response();
    }
}
