package com.test.day02;

import cn.afterturn.easypoi.excel.annotation.Excel;


/**
 * @Project:java26_maven_jiekou
 * @Forum:http://testingpai.com
 * @Copyright:2021
 * @Author:xt
 * @Date:2021-03-31 22:31
 * @DESC:pojo是实体类的意思   excel表头映射内容  一一映射
 **/
public class ExcelPojo {
    @Excel(name="序号(caseId)")
    private int caseId;
    @Excel(name="接口模块(interfase)")
    private String interfaseName;
    @Excel(name="用例标题(title)")
    private String title;
    @Excel(name="请求头(requestHeader)")
    private String requestHeader;
    @Excel(name="请求方式(method)")
    private String method;
    @Excel(name="接口地址(url)")
    private String url;
    @Excel(name="参数输入(inputParams)")
    private String inputParams;
    @Excel(name="期望返回结果(expected)")
    private String expected;


    public int getCaseId() {
        return caseId;
    }

    public void setCaseId(int caseId) {
        this.caseId = caseId;
    }

    public String getInterfaseName() {
        return interfaseName;
    }

    public void setInterfaseName(String interfaseName) {
        this.interfaseName = interfaseName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRequestHeader() {
        return requestHeader;
    }

    public void setRequestHeader(String requestHeader) {
        this.requestHeader = requestHeader;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getInputParams() {
        return inputParams;
    }

    public void setInputParams(String inputParams) {
        this.inputParams = inputParams;
    }

    public String getExpected() {
        return expected;
    }

    public void setExpected(String expected) {
        this.expected = expected;
    }

    @Override
    public String toString() {
        return "ExcelPojo{" +
                "caseId=" + caseId +
                ", interfaseName='" + interfaseName + '\'' +
                ", title='" + title + '\'' +
                ", requestHeader='" + requestHeader + '\'' +
                ", method='" + method + '\'' +
                ", url='" + url + '\'' +
                ", inputParams='" + inputParams + '\'' +
                ", expected='" + expected + '\'' +
                '}';
    }
}
