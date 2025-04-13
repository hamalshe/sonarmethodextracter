package com.myorg.sonarlint;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelFileReader {

    private static final Logger log = LogManager.getLogger(ExcelFileReader.class);

    public static List<Map<String, Integer>> readAllRows(String excelPath, String sheetName) throws IOException {
        Workbook workbook = null;
        List<Map<String, Integer>> rows = new ArrayList<Map<String, Integer>>();
        String filePath = (excelPath);
        try {
            workbook = WorkbookFactory.create(new File(filePath));
            Iterator<Sheet> sheetIterator = workbook.sheetIterator();
            Sheet sheet = null;
            while (sheetIterator.hasNext()) {
                sheet = sheetIterator.next();
                //System.out.println("sheet name "+ sheet.getSheetName());
                if (sheet.getSheetName().equalsIgnoreCase(sheetName)) {
                    break;
                }
            }
            rows = getAllDataRow(sheet);
            workbook.close();
        } catch (Exception e) {
            log.info(e, e);
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }
        return rows;
    }

    private static List<Map<String, Integer>> getAllDataRow(Sheet sheet) {
        Map<String, Integer> dataMap;
        List<Map<String, Integer>> allDataMap = new ArrayList<Map<String, Integer>>();

        if (sheet != null) {
            Row headerRow = sheet.getRow(0);
            for (int i = 0; i <= sheet.getLastRowNum(); i++) {
                if (i == 0) {
                    continue;
                } else if (i > 0) {
                    Row dataRow = sheet.getRow(i);
                    String mapKey = "";
                    Integer lineNumber = 0;
                    dataMap = new HashMap<String, Integer>();
                    for (int j = 0; j < headerRow.getLastCellNum(); j++) {
                        if (getCellValue(headerRow.getCell(j)).contains("component")) {
                            mapKey = getCellValue(dataRow.getCell(j));
                        }if (getCellValue(headerRow.getCell(j)).contains("textRange/startLine")) {
                            lineNumber = new BigDecimal(getCellValue(dataRow.getCell(j))).intValue();
                        }
                    }
                    dataMap.put(mapKey, lineNumber);
                    allDataMap.add(dataMap);
                }

            }
        }
        return allDataMap;
    }

    private static String getCellValue(Cell cell) {
        if (cell != null) {
            switch (cell.getCellType()) {
                case BOOLEAN:
                    return Boolean.toString(cell.getBooleanCellValue());
                case STRING:
                    return cell.getRichStringCellValue().getString();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue().toString();
                    } else {
                        return Double.toString(cell.getNumericCellValue());
                    }
                case BLANK:
                    return "";
                default:
                    return "";
            }
        } else
            return "";
    }

    public static void main(String[] args) throws IOException {
        List<Map<String, Integer>> result = ExcelFileReader.readAllRows("D:\\2025\\Apr-2025\\result_2025-04-13.xlsx", "sonarbugreport");
        for (Map<String, Integer> row : result) {
            for (Map.Entry<String, Integer> obj : row.entrySet()) {
                System.out.print(obj.getKey() + "    ");
                System.out.println(obj.getValue());
            }
        }
    }
}