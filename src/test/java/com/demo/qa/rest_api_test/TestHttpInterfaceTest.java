package com.demo.qa.rest_api_test;


import com.demo.qa.utils.ExcelReader;
import com.demo.qa.utils.ExcleUtil;
import com.demo.qa.utils.HttpInterfaceTest;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


public class TestHttpInterfaceTest {
    public static HttpInterfaceTest ht;
    ExcelReader ex;
    static ExcleUtil excleUtil;

    @BeforeTest
    @Parameters("workBook")
    public void init(String path) {
        String sheetName = "Input";
        System.out.println(path);
        ht = new HttpInterfaceTest();
        ex = new ExcelReader(path, sheetName);
        try {
            ExcleUtil.setExcleFile(path, sheetName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(dataProvider = "dp")
    public void testSendPost(String ID, String call_type, String host, String call_suff,
                             String AcceptLanguage, String body, String expectResponse) throws Exception {
        System.out.println("rowNum=" + ID + "；URL=" + host + call_suff + " ;  paras=" + body);
        Integer it = new Integer(ID);
        String s = null;
        int row = it.intValue();
        if (body.contains("&")) {
            //不知道干嘛，没用
            String s1 = ht.sendPost(host + call_suff, body, AcceptLanguage);
            ExcleUtil.setCellData(row, 5, s1);
            System.out.println(s1);
        } else {//正常开始请求
            try {
                System.out.println("----row====" + row);
                //如果case中type为get则调用sendget
                if (call_type.equals("GET")) {
                    s = ht.sendGet(host, call_suff, AcceptLanguage);
                }//否则调用sendpost
                else {
                    JSONObject jsonObject = JSONObject.fromObject(body);
                    s = ht.sendPost(host + call_suff, jsonObject.toString(), AcceptLanguage);
                }
                //判断结果并写入excel
                if (s.contains(expectResponse)) {

                    ExcleUtil.setCellData(row, 7, s);
                    ExcleUtil.setCellData(row, 8, "PASS");
                } else {
                    ExcleUtil.setCellData(row, 7, s);
                    ExcleUtil.setCellData(row, 8, "FAILED");
                }

                System.out.println(s);
            } catch (JSONException exception) {

                System.out.println("标题行不能进行转换！");
            }

        }
    }

    @DataProvider

    public Object[][] dp() {
        Object[][] sheetData = ex.getSheetData2();
        return sheetData;

    }

}