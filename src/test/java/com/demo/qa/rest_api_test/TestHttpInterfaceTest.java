package com.demo.qa.rest_api_test;


import com.demo.qa.utils.ExcelReader;
import com.demo.qa.utils.ExcleUtil;
import com.demo.qa.utils.HttpInterfaceTest;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;


public class TestHttpInterfaceTest {
   public static HttpInterfaceTest ht ;
    ExcelReader ex ;
    static ExcleUtil excleUtil;
    @BeforeTest
    public void init(){
        String ExcelFilePath="D:\\jobs\\XML\\Http_Request_Workbook_Data.xlsx";
        //String ExcelFilePath1="D:\\jobs\\XML\\Http_Request_Workbook_Data1.xlsx";
        String sheetName="Input";
        //String sheetName1="Output";
        ht = new HttpInterfaceTest();
        ex = new ExcelReader(ExcelFilePath, sheetName);
        try {
            ExcleUtil.setExcleFile(ExcelFilePath,sheetName);
            //excleUtil.setExcleFile(ExcelFilePath1,sheetName1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(dataProvider = "dp")
    public void testSendPost(String ID ,String host, String call_suff, String Authorization,
    		String body, String expectResponse) throws Exception {
        System.out.println("rowNum="+ID+"；URL="+host + call_suff+" ;  paras="+body);
        Integer it = new Integer(ID);
         int row = it.intValue();
        if (body.contains("&")){
            String s1 =  ht.sendPost(host + call_suff,body);
            ExcleUtil.setCellData(row,5,s1);
            System.out.println(s1);
        }else {
            try {
            	System.out.println( "----row====" + row);
                JSONObject jsonObject = JSONObject.fromObject(body);
                String s  =  ht.sendPost(host + call_suff , jsonObject.toString());
                if(s.contains(expectResponse) ) {
                	
                ExcleUtil.setCellData(row,6,s);
                ExcleUtil.setCellData(row, 7, "PASS");
                } else {
                    ExcleUtil.setCellData(row,6,s);
                    ExcleUtil.setCellData(row, 7, "FAILED");
                }
                
                System.out.println(s);
            }catch (JSONException exception){

                System.out.println("标题行不能进行转换123！");
            }

        }


    }
    @DataProvider
    public Object[][] dp(){
     Object[][] sheetData2 = ex.getSheetData2();
//           System.out.println(sheetData2.length + "--------1----");
//        for (int i = 1; i < sheetData2.length; i++) {
//            for (int j = 0; j < sheetData2[i].length; j++) {
//                System.out.print(sheetData2[i][j] + " | ");
//            }
//            System.out.println();
//        }


        return  sheetData2 ;

    }

}