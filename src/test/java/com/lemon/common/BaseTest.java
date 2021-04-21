package com.lemon.common;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson.JSON;
import com.lemon.data.Constants;
import com.lemon.data.Environment;
import com.lemon.pojo.ExcelPojo;
import com.lemon.util.JDBCUtils;
import io.qameta.allure.Allure;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.math.BigDecimal;
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
 * @Date:2021-04-09 15:52
 * @DESC:
 **/
public class BaseTest {

    @BeforeTest
    public void GlobalSetup() throws FileNotFoundException {
        //RestAssured全局配置  json小数返回类型是BigDecimal
        RestAssured.config = RestAssured.config().jsonConfig(jsonConfig().numberReturnType(BIG_DECIMAL));
        //BaseUrl全局配置
        RestAssured.baseURI = Constants.BASE_URL;
        //日志全局重定向到本地文件中
//        File file = new File(System.getProperty("user.dir")+"\\log");
//        if(!file.exists()){//如果文件不存在
//            //创建目录
//            file.mkdir();
//        }
//        PrintStream fileOutPutStream = new PrintStream(new File("log/test_all.log"));
//        RestAssured.filters(new RequestLoggingFilter(fileOutPutStream),new ResponseLoggingFilter(fileOutPutStream));
    }

    /**
     * 对get、post、patch、put、delete...做了二次封装
     * @param excelPojo excel每行数据对应对象
     * @return 接口响应结果
     */
    public Response request(ExcelPojo excelPojo,String interfaceModuleName){
        //为每一个请求单独做日志保存
        //如果指定输出到文件的话，那么设置重定向输出到文件中
        String logFilePath;
        if(Constants.LOG_TO_FILE) {
            File file = new File(System.getProperty("user.dir") + "\\log\\"+interfaceModuleName);
            if (!file.exists()) {//如果文件不存在
                //创建目录
                file.mkdirs();
            }
            logFilePath = file +"\\test" + excelPojo.getCaseId() + ".log";
            PrintStream fileOutPutStream = null;
            try {
                fileOutPutStream = new PrintStream(new File(logFilePath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            RestAssured.config = RestAssured.config().logConfig(LogConfig.logConfig().defaultStream(fileOutPutStream));
        }
        //接口请求地址
        String url = excelPojo.getUrl();
        //请求方法
        String method =excelPojo.getMethod();
        //请求头
        String headers=excelPojo.getRequestHeader();
        //请求参数
        String params =excelPojo.getInputParams();
        //将请求头转换成Map
        Map headersMap = JSON.parseObject(headers);
        Response res=null;
        //对get、post、patch、put做封装
        //equalsIgnoreCase忽略大小写
        if("get".equalsIgnoreCase(method)){
            res=given().log().all().headers(headersMap).when().get(url).then().log().all().extract().response();
        }else if("post".equalsIgnoreCase(method)){
            res=given().log().all().headers(headersMap).body(params).when().post(url).then().log().all().extract().response();
        }else if("patch".equalsIgnoreCase(method)){
            res=given().log().all().headers(headersMap).body(params).when().patch(url).then().log().all().extract().response();
        }
        //向allure报表中添加日志
        if(Constants.LOG_TO_FILE) {
            try {
                Allure.addAttachment("接口请求响应信息", new FileInputStream(logFilePath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    /**
     * 对响应结果断言
     * @param excelPojo 用例数据实体类对象
     * @param res  接口响应
     */
    public void assertResponse(ExcelPojo excelPojo,Response res ){
        if (excelPojo.getExpected()!=null){
            Map<String,Object> expectedMap = JSON.parseObject(excelPojo.getExpected());
            for(String key:expectedMap.keySet()){
                //获取期望结果（获取map里面的value）
                Object expectedValue=expectedMap.get(key);
                //获取接口返回的实际结果（）
                Object actualValue = res.jsonPath().get(key);
                //断言
                Assert.assertEquals(actualValue,expectedValue);
            }
        }
    }

    /**
     * 数据库断言
     * @param excelPojo
     */
    public void assertSQL(ExcelPojo excelPojo){
        String dbAssert = excelPojo.getDbAssert();
        if(dbAssert !=null){
            Map<String,Object> dbAssertMap = JSON.parseObject(dbAssert);
            for(String key: dbAssertMap.keySet()){
                //key是sql语句
                //value是数据库断言的期望
                Object expectedValue = dbAssertMap.get(key);
                //System.out.println("exceptedValue数据类型："+exceptedValue.getClass());//int
                if(expectedValue instanceof BigDecimal){
                    Object actualValue = JDBCUtils.querySingleData(key);
                    Assert.assertEquals(actualValue,expectedValue);
                }else if(expectedValue instanceof Integer){
                    //此时从excel里面读取到的是integer类型
                    //此时从数据库中读取到的是Long类型
                    Long expectedValue2=((Integer)expectedValue).longValue();
                    Object actualValue = JDBCUtils.querySingleData(key);
                    Assert.assertEquals(actualValue,expectedValue2);
                }
            }
        }
    }

    /**
     * 将接口返回字段提取到环境变量中
     * @param excelPojo  用例数据对象
     * @param res  接口返回response对象
     */
    public void extractToEnvironment(ExcelPojo excelPojo,Response res){
        Map<String,Object> extractMap = JSON.parseObject(excelPojo.getExtract());
        //通过循环遍历extractMap
        for(String key:extractMap.keySet()){
            Object path = extractMap.get(key).toString();
            //根据【提取返回数据】里面的路径表达式去提取实际接口对应返回字段的值
            Object value = res.jsonPath().get(path.toString());
            //存到环境变量中
            Environment.envData.put(key,value);
        }
    }

    /**
     * 读取excel指定sheet的所有数据
     * @param //file 文件对象
     * @param sheetNum  sheet编号  从0开始
     * @return
     */
    public List<ExcelPojo> readAllExcelData(int sheetNum){
        File file=new File(Constants.EXCEL_FILE_PATH);
        //导入参数的对象
        ImportParams importParams = new ImportParams();
        //读取第二个sheet
        importParams.setStartSheetIndex(sheetNum-1);
        //读取excel
        List<ExcelPojo> listDatas = ExcelImportUtil.importExcel(file, ExcelPojo.class, importParams);
        return listDatas;
    }

    /**
     * 读取Excel指定行的数据
     * @param //file 文件对象
     * @param sheetNum  sheet编号  从0开始
     * @param startRow  读取开始的行，默认从0开始
     * @param readRow  读取多少行
     * @return
     */
    public List<ExcelPojo> readSpecifyExcelData(int sheetNum, int startRow, int readRow){
        File file=new File(Constants.EXCEL_FILE_PATH);
        //导入参数的对象
        ImportParams importParams = new ImportParams();
        //读取第二个sheet
        importParams.setStartSheetIndex(sheetNum-1);
        //设置读取的起始行是第一行（除去表头）
        importParams.setStartRows(startRow);
        //设置读取的行数
        importParams.setReadRows(readRow);
        //读取excel
//        List<ExcelPojo> listDatas = ExcelImportUtil.importExcel(file, ExcelPojo.class, importParams);
//        return listDatas;
        return ExcelImportUtil.importExcel(file, ExcelPojo.class, importParams);
    }

    /**
     * 读取Excel指定行到最后一行的数据
     * @param //file 文件对象
     * @param sheetNum  sheet编号  从0开始
     * @param startRow  读取开始的行，默认从0开始
     * @return
     */
    public List<ExcelPojo> readSpecifyExcelData(int sheetNum, int startRow){
        File file=new File(Constants.EXCEL_FILE_PATH);
        //导入参数的对象
        ImportParams importParams = new ImportParams();
        //读取第二个sheet
        importParams.setStartSheetIndex(sheetNum-1);
        //设置读取的起始行是第一行（除去表头）
        importParams.setStartRows(startRow);
        //设置读取的行数
        //读取excel
//        List<ExcelPojo> listDatas = ExcelImportUtil.importExcel(file, ExcelPojo.class, importParams);
//        return listDatas;
        return ExcelImportUtil.importExcel(file, ExcelPojo.class, importParams);
    }

    /**
     * 从环境变量中取得对应的值，进行正则替换
     * @param orignalStr 原始字符串
     * @return  返回替换之后的字符串
     */
    //注意：此方法不会把字符串的双引号加上，做响应断言的时候要特别注意这一点
    public String regexReplace(String orignalStr){
        if(orignalStr != null){
            //Pattern:正则表达式匹配器
            Pattern pattern = Pattern.compile("\\{\\{(.*?)}}");
            //matcher:去匹配哪一个原始字符串，得到匹配对象
            Matcher matcher = pattern.matcher(orignalStr);
            //通过find方法查找，但是只find方法只会找一次，所以要用循环
            String result=orignalStr;//必须要先初始化这个变量
            while (matcher.find()) {
                //group(0)表示获取到整个匹配到的内容
                String outer = matcher.group(0);//{{member_id}}
                //group(1)表示获取{{}}包裹着的内容
                String inner = matcher.group(1);//member_id
                //从环境变量中提取到实际的值 member_id 101
                Object replaceStr=Environment.envData.get(inner);
                //replace  拿上一次替换的结果去替换
                result= result.replace(outer, replaceStr+"");
            }
        return result;
        }
        return orignalStr;
    }

    /**
     * 对用例数据替换（入参+请求头+接口地址+期望返回结果）
     * @param excelPojo excel对象
     */
    public ExcelPojo caseReplace(ExcelPojo excelPojo){
        //正则替换---参数替换
        String inputParams = regexReplace(excelPojo.getInputParams());
        excelPojo.setInputParams(inputParams);
        //正则替换---请求头替换
        String requestHeader = regexReplace(excelPojo.getRequestHeader());
        excelPojo.setRequestHeader(requestHeader);
        //正则替换---接口地址替换
        String url = regexReplace(excelPojo.getUrl());
        excelPojo.setUrl(url);
        //正则替换---期望返回值替换
        String expected = regexReplace(excelPojo.getExpected());
        excelPojo.setExpected(expected);
        //正则替换---数据库校验替换
        String DbAssert = regexReplace(excelPojo.getDbAssert());
        excelPojo.setDbAssert(DbAssert);
        return excelPojo;
    }


    //        得到的值是Gpath路径表达式
//        Object memberIdPath=extractMap.get("member_id");//得到"data.id"
//        memberId = resLogin.jsonPath().get(memberIdPath.toString());
//        Object tokenPath=extractMap.get("member_id");//得到"data.id"
//        token = resLogin.jsonPath().get(tokenPath.toString());
//        //存到环境变量中
//        Environment.memberId = memberId;
//        Environment.token = token;
}
