package com.lemon.testcases;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lemon.common.BaseTest;
import com.lemon.data.Environment;
import com.lemon.pojo.ExcelPojo;
import com.lemon.util.JDBCUtils;
import com.lemon.util.PhoneRandomUtil;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.w3c.dom.ls.LSOutput;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

/**
 * @Project:java26_maven_jiekou
 * @Forum:http://testingpai.com
 * @Copyright:2021
 * @Author:xt
 * @Date:2021-04-15 16:12
 * @DESC:
 **/
public class RegisterTest extends BaseTest {

    @BeforeClass
    public void setup() {
        //生成没有注册过的手机号
        String phone1 = PhoneRandomUtil.getUnregisterPhone();
        String phone2 = PhoneRandomUtil.getUnregisterPhone();
        String phone3 = PhoneRandomUtil.getUnregisterPhone();
        //将手机号存到环境变量中
        Environment.envData.put("phone1", phone1);
        Environment.envData.put("phone2", phone2);
        Environment.envData.put("phone3", phone3);
    }


    @Test(dataProvider = "getRegisterDatas")
    public void testRegister(ExcelPojo excelPojo) throws FileNotFoundException {
        //替换phone
        caseReplace(excelPojo);
        //发起注册请求
        Response res = request(excelPojo,"Register");
        //响应断言
        assertResponse(excelPojo,res);
        //数据库断言
        assertSQL(excelPojo);
    }

    @DataProvider
    public Object[] getRegisterDatas(){
        List<ExcelPojo> listDatas = readSpecifyExcelData(1, 0);
        return listDatas.toArray();
    }

    @AfterTest
    public void teardown(){
        //清空环境变量,也可以不清空，没影响
        Environment.envData.clear();
    }
}



