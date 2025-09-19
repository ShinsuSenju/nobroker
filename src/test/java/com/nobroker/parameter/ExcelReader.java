package com.nobroker.parameter;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.DataProvider;

public class ExcelReader {

    private FileInputStream fileInput;
    private XSSFWorkbook workbook;

    // Constructor: Loads Excel file using path from property file
    public ExcelReader() {
        try {
            fileInput = new FileInputStream(PropertyReader.getDataFromPropertyFile("excel_path"));
            workbook = new XSSFWorkbook(fileInput);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Reads data from the specified sheet and returns it as a 2D Object array
    public Object[][] getSheetData(int sheetNo) {
        Sheet sheet = workbook.getSheetAt(sheetNo);
        int rowCount = sheet.getPhysicalNumberOfRows();
        int colCount = sheet.getRow(0).getLastCellNum();

        Object[][] data = new Object[rowCount][colCount];

        for (int i = 0; i < rowCount; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            data[i][0] = row.getCell(0).getStringCellValue();                  // BHK (String)
            data[i][1] = (int) row.getCell(1).getNumericCellValue();      // Min Price (int)
            data[i][2] = (int) row.getCell(2).getNumericCellValue();      // Max Price (int)
            data[i][3] = row.getCell(3).getStringCellValue();                  // Availability (String)
            data[i][4] = row.getCell(4).getStringCellValue();                  // Tenant Type (String)
            data[i][5] = row.getCell(5).getStringCellValue();                  // Property Type (String)
            data[i][6] = row.getCell(6).getStringCellValue();                  // Furnishing (String)
            data[i][7] = row.getCell(7).getStringCellValue();                  // Parking (String)
        }

        return data;
    }

    // DataProvider for filter test
    @DataProvider(name = "filterDataProvider")
    public static Object[][] filterDataProvider() {
        ExcelReader reader = new ExcelReader();
        Object[][] data = reader.getSheetData(0);
        reader.close();
        return data;
    }

    // DataProvider for reset filter test
    @DataProvider(name = "resetFilterDataProvider")
    public static Object[][] resetFilterDataProvider() {
        ExcelReader reader = new ExcelReader();
        Object[][] allData = reader.getSheetData(0);
        reader.close();
        return new Object[][] { allData[1] };
    }


    // Closes workbook and file input stream
    public void close() {
        try {
            if (workbook != null) workbook.close();
            if (fileInput != null) fileInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
