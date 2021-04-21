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

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;

/**
 * @Project:java26_maven_jiekou
 * @Forum:http://testingpai.com
 * @Copyright:2021
 * @Author:xt
 * @Date:2021-03-29 21:15
 * @DESC:单接口用例设计  业务场景下的用例设计
 **/
public class LoginTest extends BaseTest {
    //运行登录用例前一定要运行注册，在登录用例最前面加一条注册成功的用例作为前置条件，前置条件不需要断言，所以不需要期望结果
    @BeforeClass //  前置类要在测试类刚开始的时候去执行，全局配置代码在全部测试类运行之前
    public void setup(){
        //生成没有注册过的手机号
        String phone = PhoneRandomUtil.getUnregisterPhone();
        //将手机号存到环境变量中
        Environment.envData.put("phone",phone);
        //前置条件
        //读取Excel里面的第一条数据->执行->生成一条注册过了手机号码
        List<ExcelPojo> listDatas = readSpecifyExcelData(2, 0, 1);
        ExcelPojo excelPojo = listDatas.get(0);
        //替换phone
        excelPojo = caseReplace(excelPojo);
        //执行【注册】接口请求
        Response res=request(excelPojo,"Login");
        //提取注册返回的手机号
        extractToEnvironment(excelPojo,res);

    }

    @Test(dataProvider = "getLoginDatas")
    public void testLogin( ExcelPojo excelPojo) {
        //替换用例数据
        excelPojo=caseReplace(excelPojo);
        //发起登录请求
        Response res = request(excelPojo,"Login");
        //断言
        //读取响应map里面的每一个key
        //作业：完成响应断言
        //1、循环遍历响应map，取到里面每一个key（实际上就是我们设计的jsonPath表达式）
        //2、通过res.jsonPath.get(key)取到实际的结果，再跟期望的结果做对比（key对应的value）
        assertResponse(excelPojo,res);
    }

    @DataProvider
    public Object[] getLoginDatas(){
        final List<ExcelPojo> listDatas = readSpecifyExcelData(2, 1);
        return listDatas.toArray();
    }
}
