package com.lemon.data;

/**
 * @Project:java26_maven_jiekou
 * @Forum:http://testingpai.com
 * @Copyright:2021
 * @Author:xt
 * @Date:2021-04-09 18:40
 * @DESC:常量类
 **/
public class Constants {
    //日志输出配置：控制台(false) or 日志文件中(true)
    public static final boolean LOG_TO_FILE=true;
    //Excel文件的路径
    public static final String EXCEL_FILE_PATH="src/test/resources/api_testcases_futureloan_v3.xls";
    //接口的url地址
    public static final String BASE_URL="http://api.lemonban.com/futureloan";
    //数据库URI地址
    public static final String DB_BASE_URI="api.lemonban.com/";
    //数据库名称
    public static final String DB_NAME="futureloan";
    //数据库用户名
    public static final String DB_USERNAME="future";
    //数据库密码
    public static final String DB_PWDR="123456";

}
