package com.lemon.testcases;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lemon.common.BaseTest;
import com.lemon.data.Constants;
import com.lemon.data.Environment;
import com.lemon.pojo.ExcelPojo;
import com.lemon.util.PhoneRandomUtil;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;

/**
 * @Project:java26_maven_jiekou
 * @Forum:http://testingpai.com
 * @Copyright:2021
 * @Author:xt
 * @Date:2021-04-06 21:12
 * @DESC:
 **/
public class RechargeTest extends BaseTest {
    @BeforeClass
    public void setup(){
        //生成没有注册过的手机号
        String phone = PhoneRandomUtil.getUnregisterPhone();
        //将手机号存到环境变量中
        Environment.envData.put("phone",phone);
        //读取Excel里面的前两条数据
        List<ExcelPojo> listDatas = readSpecifyExcelData(3, 0, 2);
        //ExcelPojo excelPojo = listDatas.get(0);
        //替换phone
        caseReplace(listDatas.get(0));
        //发起接口请求
        //【注册】请求
        Response resRegister= request(listDatas.get(0),"Recharge");
        //获取【提取返回数据】这列
        //将接口返回对应字段存到环境变量中
        extractToEnvironment(listDatas.get(0),resRegister);
        //获取注册的响应数据，进行正则替换
        caseReplace(listDatas.get(1));
        //【登录】请求
        Response resLogin = request(listDatas.get(1),"Recharge");
        //获取【提取返回数据】这列
        //将返回数据中需要串联使用的字段存到环境变量中
        extractToEnvironment(listDatas.get(1),resLogin);

    }

    @Test(dataProvider = "getRechargeDatas")
    public void testRecharge(ExcelPojo excelPojo) {
        //用例执行之前替换{{member_id}}为环境变量
        caseReplace(excelPojo);
        Response res = request(excelPojo,"Recharge");
        //断言
        assertResponse(excelPojo,res);
    }

    @DataProvider
    public Object[] getRechargeDatas(){
        List<ExcelPojo> listDatas = readSpecifyExcelData(3, 2);
        return listDatas.toArray();
    }
}
