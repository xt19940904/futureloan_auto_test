package com.lemon.testcases;

import com.lemon.common.BaseTest;
import com.lemon.data.Environment;
import com.lemon.pojo.ExcelPojo;
import com.lemon.util.PhoneRandomUtil;
import io.restassured.response.Response;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

/**
 * @Project:java26_maven_jiekou
 * @Forum:http://testingpai.com
 * @Copyright:2021
 * @Author:xt
 * @Date:2021-04-14 22:12
 * @DESC:
 **/
public class AddLoanTest extends BaseTest {
    @BeforeClass
    public void setup(){
        //生成随机手机号码
        String borrowerPhone = PhoneRandomUtil.getUnregisterPhone();
        String adminPhone = PhoneRandomUtil.getUnregisterPhone();
        //将手机号存在环境变量中
        Environment.envData.put("borrower_phone",borrowerPhone);
        Environment.envData.put("admin_phone",adminPhone);
        //读取用例数据1-4条
        List<ExcelPojo> list = readSpecifyExcelData(4, 0, 4);
        for (int i=0;i<list.size();i++){
            ExcelPojo excelPojo = list.get(i);
            //替换用例数据
            caseReplace(excelPojo);
            //发送请求
            Response res = request(excelPojo,"AddLoan");
            //判断是否要提取响应数据
            if(excelPojo.getExtract() != null){
                //将返回数据存到环境变量中
                extractToEnvironment(excelPojo,res);
            }
        }
    }

    @Test(dataProvider = "getAddLoanDatas")
    public void testAddLoan(ExcelPojo excelPojo){
        caseReplace(excelPojo);
        Response res = request(excelPojo,"AddLoan");
        //断言
        assertResponse(excelPojo,res);
    }

    @DataProvider
    public Object[] getAddLoanDatas(){
        List<ExcelPojo> listDatas = readSpecifyExcelData(4, 4);
        return listDatas.toArray();
    }

    @AfterTest
    public void teardown(){
    }
}
