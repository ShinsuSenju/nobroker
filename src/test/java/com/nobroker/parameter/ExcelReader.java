package com.nobroker.parameter;

import java.io.FileInputStream;
import java.io.IOException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.DataProvider;

public class ExcelReader {
	FileInputStream fileInput;
	XSSFWorkbook workbook;

	public ExcelReader() {
		try {
			fileInput = new FileInputStream(PropertyReader.getDataFromPropertyFile("excel_path"));
			workbook = new XSSFWorkbook(fileInput);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// For DataProvider: always reads columns as (String, int, int, String, String, String, String, String, String)
	public Object[][] getSheetData(int sheetNo) {
		Sheet sheet = workbook.getSheetAt(sheetNo);
		int rowCount = sheet.getPhysicalNumberOfRows();
		int colCount = sheet.getRow(0).getLastCellNum();
		Object[][] data = new Object[rowCount][colCount];
		for (int i = 0; i < rowCount; i++) {
			Row row = sheet.getRow(i);
			if (row == null) continue;
			// 0: BHK (String)
			data[i][0] = row.getCell(0).getStringCellValue();
			// 1: Min Price (int)
			data[i][1] = (int) row.getCell(1).getNumericCellValue();
			// 2: Max Price (int)
			data[i][2] = (int) row.getCell(2).getNumericCellValue();
			// 3: Availability (String)
			data[i][3] = row.getCell(3).getStringCellValue();
			// 4: Tenant Type (String)
			data[i][4] = row.getCell(4).getStringCellValue();
			// 5: Property Type (String)
			data[i][5] = row.getCell(5).getStringCellValue();
			// 6: Furnishing (String)
			data[i][6] = row.getCell(6).getStringCellValue();
			// 7: Parking (String)
			data[i][7] = row.getCell(7).getStringCellValue();
		}
		return data;
	}

	@DataProvider(name = "filterDataProvider")
	public static Object[][] filterDataProvider() {
		ExcelReader reader = new ExcelReader();
		Object[][] data = reader.getSheetData(0); // 0 = first sheet
		reader.close();
		return data;
	}

	public void close() {
		try {
			if (workbook != null) workbook.close();
			if (fileInput != null) fileInput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
