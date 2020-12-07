package com.Implementation;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Utils {

    public static void main(String[] args) {
        getRowCount();
        getCellData();
    }

    public static void getRowCount() {
        try {
            String excelpath = "./data/TestData.xlsx";
            XSSFWorkbook workbook = new XSSFWorkbook(excelpath);
            XSSFSheet sheet = workbook.getSheet("postCode");

            int rowCount = sheet.getPhysicalNumberOfRows();
            System.out.println("No of Rows :" + rowCount);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
            e.printStackTrace();
        }

    }

    public static void getCellData() {
        try {
            // String value= sheet.getRow(1).getCell(0).getStringCellValue();
            // double value= sheet.getRow(1).getCell(2).getNumericCellValue());

            String excelpath = "./data/TestData.xlsx";

            XSSFWorkbook workbook = new XSSFWorkbook(excelpath);
            XSSFSheet sheet = workbook.getSheet("postCode");

            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                List<String> rowData = new ArrayList<String>();

                Row rows = rowIterator.next();
                Iterator<Cell> cellIterator = rows.iterator();

                //easy and straight forward way is dataformatter:
                DataFormatter formatter = new DataFormatter();

                while (cellIterator.hasNext()) {
                    Cell cells = cellIterator.next();
                    System.out.println("cellsData ->" + cells.toString());
                    if (cells != null) {
                        rowData.add(cells.getStringCellValue().trim());
                    } else {
                        rowData.add("");
                    }
                }
                if (rowData.size() != 0) {
                    System.out.println(rowData.toString());
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
            e.printStackTrace();
        }
    }
}

