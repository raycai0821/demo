package com.demo.qa.utils;

import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelReader {
    private String filePath;
    private String sheetName;
    private Workbook workBook;
    private Sheet sheet;
    private List<String> columnHeaderList;
    private List<List<String>> listData;
    private List<Map<String, String>> mapData;
    private boolean flag;
    public Object[][] results;

    public ExcelReader(String filePath, String sheetName) {
        this.filePath = filePath;
        this.sheetName = sheetName;
        this.flag = false;
        this.load();
    }

    private void load() {
        FileInputStream inStream = null;
        try {
            inStream = new FileInputStream(new File(filePath));
            workBook = WorkbookFactory.create(inStream);
            sheet = workBook.getSheet(sheetName);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getCellValue(Cell cell) {
        String cellValue = "";
        DataFormatter formatter = new DataFormatter();
        if (cell != null) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        cellValue = formatter.formatCellValue(cell);
                    } else {
                        double value = cell.getNumericCellValue();
                        int intValue = (int) value;
                        cellValue = value - intValue == 0 ? String.valueOf(intValue) : String.valueOf(value);
                    }
                    break;
                case Cell.CELL_TYPE_STRING:
                    cellValue = cell.getStringCellValue();
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    cellValue = String.valueOf(cell.getBooleanCellValue());
                    break;
                case Cell.CELL_TYPE_FORMULA:
                    cellValue = String.valueOf(cell.getCellFormula());
                    break;
                case Cell.CELL_TYPE_BLANK:
                    cellValue = "";
                    break;
                case Cell.CELL_TYPE_ERROR:
                    cellValue = "";
                    break;
                default:
                    cellValue = cell.toString().trim();
                    break;
            }
        }
        return cellValue.trim();
    }

    private void getSheetData() {

        listData = new ArrayList<>();
        mapData = new ArrayList<>();
        columnHeaderList = new ArrayList<>();
        int numOfRows = sheet.getLastRowNum() + 1;
        for (int i = 0; i < numOfRows; i++) {
            Row row = sheet.getRow(i);
            Map<String, String> map = new HashMap<>();
            List<String> list = new ArrayList<>();

            if (row != null) {
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    Cell cell = row.getCell(j);
                    if (i == 0) {
                        columnHeaderList.add(getCellValue(cell));
                    } else {

                        map.put(columnHeaderList.get(j), this.getCellValue(cell));

                    }
                    list.add(this.getCellValue(cell));
                }
            }
            if (i > 0) {
                mapData.add(map);
            }
            listData.add(list);


        }

        flag = true;

        for (int i = 0; i < listData.size(); i++) {
            for (int j = 0; j < listData.get(i).size(); j++) {
                System.out.println(listData.get(i).get(j).toString());
            }
        }

    }

    public String getCellData(int row, int col) {
        if (row <= 0 || col <= 0) {
            return null;
        }
        if (!flag) {
            this.getSheetData();
        }
        if (listData.size() >= row && listData.get(row - 1).size() >= col) {
            return listData.get(row - 1).get(col - 1);
        } else {
            return null;
        }
    }

    public String getCellData(int row, String headerName) {
        if (row <= 0) {
            return null;
        }
        if (!flag) {
            this.getSheetData();
        }
        if (mapData.size() >= row && mapData.get(row - 1).containsKey(headerName)) {
            return mapData.get(row - 1).get(headerName);
        } else {
            return null;
        }
    }


    public Object[][] getSheetData2() {

        List<Object[]> result = new ArrayList<>();
        listData = new ArrayList<>();
        mapData = new ArrayList<>();
        columnHeaderList = new ArrayList<>();

        int numOfRows = sheet.getLastRowNum() + 1;
        System.out.println("总共有 " + numOfRows + "行 ！");
        for (int i = 0; i < numOfRows; i++) {
            Row row = sheet.getRow(i);
            Map<String, String> map = new HashMap<>();
            List<String> list = new ArrayList<>();
            Object[] o1 = new Object[row.getLastCellNum()];

            if (row != null) {
                for (int j = 0; j < row.getLastCellNum(); j++) {
                      //System.out.println("第 "+i+" 行--- row.getLastCellNum()===="+row.getLastCellNum());
                    Cell cell = row.getCell(j);
                    if (i == 0) {
                        o1[j] = this.getCellValue(cell);
                        //System.out.println(j+"------this.getCellValue(cell)="+this.getCellValue(cell));
                        columnHeaderList.add(getCellValue(cell));
                    } else {
                        o1[j] = this.getCellValue(cell);
                        System.out.println(j + "------this.getCellValue(cell)="+ this.getCellValue(cell));
                        map.put(columnHeaderList.get(j), this.getCellValue(cell));

                    }
                    list.add(this.getCellValue(cell));
                }
            }
            if (i > 0) {
                mapData.add(map);
            }
            result.add(o1);
            listData.add(list);
        }
        // 测试数据excel数据用 ；
//         for (int i = 0; i < result.size(); i++) {
//            for (int j = 0; j < result.get(i).length; j++) {
//                System.out.print(result.get(i)[j]+" | ");
//            }
//            System.out.println();
//        }
     
        results = new Object[result.size() - 1][];
        
//        System.out.print(result.size());
        for (int i = 0; i < result.size() - 1; i++) {
            results[i] = result.get(i+1);
//            System.out.println("results.length==" + results.length);
            //System.out.print(result.get(i+1));
        }
        flag = true;

        System.out.println("results.length==" + results.length);
        return results;
    }

    public static void main(String[] args) {
    	Object[][] obj1;
        ExcelReader eh = new ExcelReader("D:\\jobs\\XML\\Http_Request_Workbook_Data.xlsx", "Input");
        Object[][] sheetData2 = eh.getSheetData2();
        System.out.println(sheetData2.length + "------------");
        for (int i = 0; i < sheetData2.length; i++) {
            for (int j = 0; j < sheetData2[i].length; j++) {
                System.out.print(sheetData2[i][j] + " | ");
            }
            System.out.println();
        }


    }
}