package com.myorg.sonarlint;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SonarLintMethodNameExtracter {
    public static void main(String[] args) throws IOException {
        //System.out.println(Arrays.toString(args));
        StringBuilder sourceDirBuilder = new StringBuilder("");
        StringBuilder sheetNameBuilder = new StringBuilder("");
        StringBuilder excelFileNameBuilder = new StringBuilder("");
        final int argslength = args.length;
        for (int i=0; i<argslength; i++) {
            final String arg = args[i];
            if ("-s".equals(arg) && (i+1) < argslength) {
                sourceDirBuilder.append(args[i+1]);
                break;
            }
        }
        final String sourceDir = sourceDirBuilder.toString();
        for (int i=0; i<argslength; i++) {
            final String arg = args[i];
            if ("-sheet".equals(arg) && (i+1) < argslength) {
                sheetNameBuilder.append(args[i+1]);
                break;
            }
        }
        for (int i=0; i<argslength; i++) {
            final String arg = args[i];
            if (arg.toLowerCase().endsWith(".xlsx")) {
                excelFileNameBuilder.append(arg);
                break;
            }
        }
        final String excelFile = excelFileNameBuilder.toString();
        //System.out.println("excelFile = "+excelFile);
        final String sheetName = sheetNameBuilder.toString();
        //System.out.println("sheetName = "+sheetName);
        //System.out.println(sourceDir);
        //final String excelFile = "D:\\Sonar Reports\\sonar_may_release_project.xlsx";
        //final String sheetName = "sonar_may_release_project";
        List<Map<String, Integer>> result = ExcelFileReader.readAllRows(excelFile, sheetName);
        JavaFileAnalyzer javaFileAnalyzer = JavaFileAnalyzer.getInstance();
        ExcelFileWriter excelFileWriter = new ExcelFileWriter(excelFile);
        Sheet sheet = excelFileWriter.getSheet(sheetName);
        if (sheet != null) {
            Row headerRow = sheet.getRow(0);
            System.out.print("Processing Data.");
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row dataRow = sheet.getRow(i);
                System.out.print("\b");
                if(i % 2 > 0 && i == 1){
                    System.out.print(".");
                }else if (i % 2 > 0 && i > 1){
                    System.out.print("\b");
                    System.out.print(".");
                }else{
                    System.out.print("..");
                }

                for (int j = 0; j < headerRow.getLastCellNum(); j++) {
                    final Cell headerCell = headerRow.getCell(j);
                    if (excelFileWriter.getCellValue(headerCell).contains("Method")) {
                        Cell currentWritingCell = dataRow.createCell(j);
                        Map<String, Integer> row = result.get(i - 1);
                        for (Map.Entry<String, Integer> obj : row.entrySet()) {
                            javaFileAnalyzer.parseJavaFile(sourceDir, obj.getKey());
                            final String methodName = javaFileAnalyzer.findMethodName(obj.getKey(), obj.getValue());
                            /*System.out.print(obj.getKey()+ "\t");
                            System.out.print(obj.getValue()+ "\t");
                            System.out.println(methodName);*/
                            currentWritingCell.setCellValue(methodName);
                        }
                    }
                }
            }
            System.out.println(".");
            excelFileWriter.writeToExcel(sheet);
            excelFileWriter.closeFile();
            System.out.println("Processing Completed.");
        }
    }
}