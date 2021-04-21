package com.lemon.testcases;

import com.alibaba.fastjson.JSON;
import com.lemon.common.BaseTest;
import com.lemon.data.Constants;
import com.lemon.data.Environment;
import com.lemon.pojo.ExcelPojo;
import com.lemon.util.JDBCUtils;
import com.lemon.util.PhoneRandomUtil;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;

/**
 * @Project:java26_maven_jiekou
 * @Forum:http://testingpai.com
 * @Copyright:2021
 * @Author:xt
 * @Date:2021-04-12 16:16
 * @DESC:
 **/
public class InvestFlowTest extends BaseTest {
    @BeforeClass //全局配置代码在全部的测试类运行之前
    public void setup(){
        //生成三个角色的随机手机号码
        String borrowerphone = PhoneRandomUtil.getUnregisterPhone();
        String adminphone = PhoneRandomUtil.getUnregisterPhone();
        String investphone = PhoneRandomUtil.getUnregisterPhone();
        Environment.envData.put("borrower_phone",borrowerphone);
        Environment.envData.put("admin_phone",adminphone);
        Environment.envData.put("invest_phone",investphone);
        //读取用例数据从第一条~第九条
        List<ExcelPojo> list = readSpecifyExcelData(4, 0, 9);
        for (int i=0;i<list.size();i++){
            //发送请求
            ExcelPojo excelPojo = list.get(i);
            //替换用例数据
            excelPojo = caseReplace(excelPojo);
            Response res = request(excelPojo,"InvestFlow");
            //判断是否要提取响应数据
            if(excelPojo.getExtract() != null){
                //将返回数据存到环境变量中
                extractToEnvironment(excelPojo,res);
            }
        }
    }

    @Test
    public void testInvest(){
        List<ExcelPojo> list = readSpecifyExcelData(4, 9);
        ExcelPojo excelPojo = caseReplace(list.get(0));
        //发送投资请求
        Response res = request(excelPojo,"InvestFlow");
        //响应断言
        assertResponse(excelPojo,res);
        //数据库断言
        assertSQL(excelPojo);
    }

    @AfterTest
    public void teardown(){

    }
}
