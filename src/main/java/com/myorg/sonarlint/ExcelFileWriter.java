package com.myorg.sonarlint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Iterator;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelFileWriter {
    private Workbook workbook = null;
    private File file = null;
    private static final Object READ_LOCK = new Object();
    private static final Object WRITE_LOCK = new Object();
    private static final Logger log = LogManager.getLogger(ExcelFileWriter.class);

    private ExcelFileWriter(){}

    public ExcelFileWriter(String excelPath){
        synchronized (this){
            file = new File(excelPath);
        }
    }

    public Sheet getSheet(String sheetName) {
        synchronized (READ_LOCK){
            Sheet sheet = null;
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                workbook = WorkbookFactory.create(fis);
                Iterator<Sheet> sheetIterator = workbook.sheetIterator();

                while (sheetIterator.hasNext()) {
                    Sheet localsheet = sheetIterator.next();
                    if (localsheet.getSheetName().equalsIgnoreCase(sheetName)) {
                        sheet = localsheet;
                        break;
                    }
                }
            } catch (Exception e) {
                log.info("Error in getSheet method", e);
            } finally {
                try{
                    if (fis != null) {
                        fis.close();
                    }
                } catch(Exception e){log.error(e);}
            }
            return sheet;
        }
    }

    public void writeToExcel(Sheet sheet) {
        synchronized (WRITE_LOCK){
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(file);
                workbook = sheet.getWorkbook();
                workbook.write(outputStream);
                outputStream.flush();
            } catch (Exception e) {
                log.info("Error in writeToExcel", e);
            } finally {
                try{
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch(Exception e) {log.error(e);}
            }
        }
    }

    public void closeFile(){
        try{
            if (workbook != null) {
                workbook.close();
                workbook = null;
            }
        } catch(Exception e){log.error(e);}
    }

    public static String getCellValue(Cell cell) {
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
}