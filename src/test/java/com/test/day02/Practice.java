package com.test.day02;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson.JSON;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * @Project:java26_maven_jiekou
 * @Forum:http://testingpai.com
 * @Copyright:2021
 * @Author:xt
 * @Date:2021-04-03 15:41
 * @DESC:练习
 **/
public class Practice {
    

    @Test(dataProvider = "getLoginDatas")
    public void testLgin(ExcelPojo excelPojo){
        //BaseUrl全局配置
        RestAssured.baseURI = "http://api.lemonban.com/futureloan";
        //入参
        String inputParams=excelPojo.getInputParams();
        //接口地址url
        String url= excelPojo.getUrl();
        //请求头
        String requestHeader = excelPojo.getRequestHeader();
        //把请求头转为map
        Map requestHeaderMap = (Map) JSON.parse(requestHeader);
        //期望结果
        String expected = excelPojo.getExpected();
        //把响应结果转为map
        Map<String,Object> expectedMap = (Map) JSON.parse(expected);
        Response res=
                given().
                        body(inputParams).
                        headers(requestHeaderMap).
                when().
                        post(url).
                then().
                        log().all().extract().response();
        //响应断言：
        for(String key: expectedMap.keySet()){
            Object expectedValue=expectedMap.get(key);
            Object actualValue=res.jsonPath().get(key);
            Assert.assertEquals(actualValue,expectedValue);
        }

    }
    @DataProvider
    public Object[] getLoginDatas(){
        File file=new File("src/test/resources/api_testcases_futureloan_v1.xls");
        ImportParams importParams=new ImportParams();
        importParams.setStartSheetIndex(1);
        List<ExcelPojo> listDatas = ExcelImportUtil.importExcel(file, ExcelPojo.class, importParams);
        return listDatas.toArray();
    }
}
