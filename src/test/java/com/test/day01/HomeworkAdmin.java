package com.test.day01;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

/**
 * @Project:java26_maven_jiekou
 * @Forum:http://testingpai.com
 * @Copyright:2021
 * @Author:xt
 * @Date:2021-04-02 18:03
 * @DESC:
 **/
public class HomeworkAdmin {
    static String mobilePhone="17711119999";
    static String pwd="123456789";
    static int type=0;
    static String title="平安30万";
    static int adminMemberId;
    static String adminToken;
    static int loanId;

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
        adminMemberId=response.jsonPath().get("data.id");
        //2、获取响应数据的token
        adminToken=response.jsonPath().get("data.token_info.token");
        System.out.println(adminMemberId);
        System.out.println(adminToken);
    }

    @Test(dependsOnMethods ="testLogin")
    public
    void testAdd(){
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
//        if (response.statusCode()==200) {
//            new HomeworkInvest().testInvest(loanId);
//        }

    }
}