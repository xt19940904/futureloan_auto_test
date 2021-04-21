package com.test.day02;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

import static io.restassured.RestAssured.*;
import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;

/**
 * @Project:java26_maven_jiekou
 * @Forum:http://testingpai.com
 * @Copyright:2021
 * @Author:xt
 * @Date:2021-03-29 21:15
 * @DESC:数据驱动  二维数组传参
 **/
public class DataDrivenDemo {
    @Test(dataProvider = "getLoginDatas")
    public void testLogin(String mobilephone,String pwd) {
        //RestAssured全局配置
        //json小数返回类型是BigDecimal
        RestAssured.config = RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
        //BaseUrl全局配置
        RestAssured.baseURI = "http://api.lemonban.com/futureloan";

        String json = "{\"mobile_phone\":"+mobilephone+",\"pwd\":"+pwd+"}";
        Response res =
                given().
                        body(json).
                        header("Content-Type", "application/json").
                        header("X-Lemonban-Media-Type", "lemonban.v2").
                when().
                        post("/member/login").
                then().
                        log().all().extract().response();
    }

    @DataProvider
    public Object[][] getLoginDatas(){
        Object[][] datas={{"13323230000","123456"},
                     {"13323231111","123456"},
                     {"13323232222","123456"}};
        return datas;
    }


}
