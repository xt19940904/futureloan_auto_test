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
import static io.restassured.config.JsonConfig.jsonConfig;
import static io.restassured.path.json.config.JsonPathConfig.NumberReturnType.BIG_DECIMAL;

/**
 * @Project:java26_maven_jiekou
 * @Forum:http://testingpai.com
 * @Copyright:2021
 * @Author:xt
 * @Date:2021-03-29 21:15
 * @DESC:数据驱动  通过pojo实体类传参 一维数组
 **/
public class DataDrivenDemo2 {
    //运行登录用例前一定要运行注册，在登录用例最前面加一条注册成功的用例作为前置条件，前置条件不需要断言，所以不需要期望结果
    @Test(dataProvider = "getLoginDatas2")
    public void testLogin( ExcelPojo excelPojo) {
        //excelPojo.getCaseId();
        //RestAssured全局配置
        //json小数返回类型是BigDecimal
        RestAssured.config = RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
        //BaseUrl全局配置
        RestAssured.baseURI = "http://api.lemonban.com/futureloan";
        //接口入参
        String inputParams= excelPojo.getInputParams();
        //接口地址
        String url= excelPojo.getUrl();
        //请求头
        String requestHeader= excelPojo.getRequestHeader();
        //把请求头转为map
        //Map requestHeaderMap = JSONObject.parseObject(requestHeader, Map.class);
        Map requestHeaderMap = (Map) JSON.parse(requestHeader);//等价于上面的方法
        //parse方法返回值是Object  (Map)是Object向下强转成Map类型的意思
        //期望的响应结果
        String expected=excelPojo.getExpected();
        //把响应结果转成map
        Map<String,Object> expectedMap = (Map) JSON.parse(expected);
        Response res =
                given().
                        body(inputParams).
                        headers(requestHeaderMap).
                when().
                        post(url).
                then().
                        log().all().extract().response();
        //断言
        //读取响应map里面的每一个key
        //作业：完成响应断言
        //1、循环遍历响应map，取到里面每一个key（实际上就是我们设计的jsonPath表达式）
        //2、通过res.jsonPath.get(key)取到实际的结果，再跟期望的结果做对比（key对应的value）

        for(String key:expectedMap.keySet()){
            //获取期望结果（获取map里面的value）
            Object expectedValue=expectedMap.get(key);
            //获取接口返回的实际结果（）
            Object actualValue = res.jsonPath().get(key);
            //断言
            Assert.assertEquals(actualValue,expectedValue);
        }
    }

    @DataProvider
    public Object[] getLoginDatas2(){
        File file=new File("src/test/resources/api_testcases_futureloan_v1.xls");
        //导入的参数对象
        ImportParams importParams=new ImportParams();
        importParams.setStartSheetIndex(1);//第一个sheet表单默认为0
        //读取Excel
        //<ExcelPojo>表示list集合当前每一个元素都是ExcelPojo对象
        //list接收的数据已经生成了对象，可以直接在testLogin方法中传对象了 ，不需要去new对象了
        //ExcelImportUtil批量导入数据
        List<ExcelPojo> listDatas = ExcelImportUtil.importExcel(file, ExcelPojo.class, importParams);
        for (Object object:listDatas){
            System.out.println(object);
        }
        //listDatas是一个集合，我们要返回的是数组，用toArray方法将集合转化为一维数组
        return listDatas.toArray();
   }

//    public static void main(String[] args) {
//        File file = new File("D:\\svn_ppt\\api_testcases_futureloan_v1.xls");
//        //导入的参数对象
//        ImportParams importParams = new ImportParams();
//        //读取第二个sheet
//        importParams.setStartSheetIndex(1);
//        //设置读取的起始行
//        importParams.setStartRows(0);
//        //设置读取的行数
//        importParams.setReadRows(1);
//        //读取Excel
//        List<ExcelPojo> listDatas = ExcelImportUtil.importExcel(file, ExcelPojo.class,importParams);
//        for (ExcelPojo excelPojo:listDatas){
//            System.out.println(excelPojo);
//        }
//    }


}
