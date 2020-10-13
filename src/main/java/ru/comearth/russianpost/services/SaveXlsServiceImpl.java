package ru.comearth.russianpost.services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import ru.comearth.russianpost.domain.CSAT;
import ru.comearth.russianpost.domain.TimeStats;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class SaveXlsServiceImpl implements SaveXlsService {

    @Override
    public File saveTimeStatsToXLS(List<String> request, List<TimeStats> stats, TimeStats overAllStats) throws Exception {

        XSSFWorkbook workBook = new XSSFWorkbook();
        XSSFSheet sheet = workBook.createSheet("statistics");
        CellStyle style1 = getFirstStyle(workBook);
        CellStyle style2 = getSecondStyle(workBook);

        int rownum = 1;
        Cell cell = sheet.createRow(rownum).createCell(0);
        cell.setCellValue(request.get(0)+" - "+request.get(1));
        cell.setCellStyle(style1);
        rownum+=2;

        String[] list = {"ФИО","Дата","AHT", "Hold"};
        int cellnum = 0;
        Row row = sheet.createRow(rownum++);
        for (String s : list) {
            cell = row.createCell(cellnum++);
            cell.setCellValue(s);
            cell.setCellStyle(style1);
        }


        for (TimeStats ts : stats) {
            row = sheet.createRow(rownum++);

            Object[] objArr = {ts.getName(),ts.getDate(), ts.getAHT(), ts.getHold()};
            cellnum = 0;
            for (Object obj : objArr) {
                cell = row.createCell(cellnum++);
                cell.setCellStyle(style2);
                saveCellValue(cell,obj,style1);
            }
        }


        Object[] list2 = {"Итого:","",overAllStats.getAHT(),overAllStats.getHold()};
        cellnum = 0;
        Row row2 = sheet.createRow(++rownum);
        for (Object obj : list2) {
            cell = row2.createCell(cellnum++);
            cell.setCellStyle(style2);
            saveCellValue(cell,obj,style1);
        }

        sheet.autoSizeColumn(0);   sheet.autoSizeColumn(1);

        File file = File.createTempFile("data",".xlsx");
        FileOutputStream out = new FileOutputStream(file);
        workBook.write(out);
        out.close();
        System.out.println("Excel written successfully..");
        workBook.close();

        return file;
    }

    public CellStyle getFirstStyle(Workbook workBook){
        CellStyle style1 = workBook.createCellStyle();
        Font font1 = workBook.createFont();
        font1.setBold(true);
        style1.setAlignment(HorizontalAlignment.CENTER);
        style1.setFont(font1);
        style1.setWrapText(true);
        return style1;
    }

    public CellStyle getSecondStyle(Workbook workBook){
        CellStyle style3 = workBook.createCellStyle();
        style3.setDataFormat(workBook.createDataFormat().getFormat("0.00%"));
        return style3;
    }

    public CellStyle getThirdStyle(Workbook workBook){
        CellStyle style2 = workBook.createCellStyle();
        Font font2 = workBook.createFont();
        font2.setBold(false);
        style2.setAlignment(HorizontalAlignment.CENTER);
        style2.setFont(font2);
        return style2;
    }

    public void saveCellValue(Cell cell, Object obj, CellStyle style){

        if (obj instanceof String)
            cell.setCellValue((String) obj);
        else if (obj instanceof String && obj.toString().endsWith("%")) {
            cell.setCellValue(obj.toString().replaceAll("%",""));
            cell.setCellStyle(style);
        }
        else if (obj instanceof LocalDate)
            cell.setCellValue(obj.toString());
        else if (obj instanceof Double)
            cell.setCellValue((Double) obj);
        else if (obj instanceof Integer)
            cell.setCellValue((int) obj);
    }

    @Override
    public File saveCSATStatsToXLS(List<String> request, List<String> stats) throws Exception {

        XSSFWorkbook workBook = new XSSFWorkbook();
        XSSFSheet sheet = workBook.createSheet("statistics");
        CellStyle style1 = getFirstStyle(workBook);
        CellStyle style2 = getSecondStyle(workBook);
        CellStyle style3 = getThirdStyle(workBook);


        int rownum = 1;
        Cell cell = sheet.createRow(rownum).createCell(0);
        cell.setCellValue(request.get(0)+" - "+request.get(1));
        cell.setCellStyle(style1);
        rownum+=2;

        String[] list = {"ФИО", "Дата", "CSAT", "DCSAT"};
        int cellnum = 0;
        Row row = sheet.createRow(rownum++);
        for (String s : list) {
            cell = row.createCell(cellnum++);
            cell.setCellValue(s);
            cell.setCellStyle(style1);
        }


        for (String csat : stats) {
            if(csat.contains("За"))
                continue;
            row = sheet.createRow(rownum++);
            String[] array = csat.split(" ");
            Object[] objArr = {array[0]+" "+array[1]+" "+array[2], array[array.length-1], array[5],array[11]};
            cellnum = 0;
            for (Object obj : objArr) {
                cell = row.createCell(cellnum++);
                cell.setCellStyle(style2);
                saveCellValue(cell,obj,style3);
            }
        }


        Object[] list2 = {"Итого:","",stats.get(0).split(" ")[11],stats.get(0).split(" ")[19]};
        cellnum = 0;
        Row row2 = sheet.createRow(++rownum);
        for (Object obj : list2) {
            cell = row2.createCell(cellnum++);
            saveCellValue(cell,obj,style3);
        }

        sheet.autoSizeColumn(0);  sheet.autoSizeColumn(1);

        File file = File.createTempFile("data",".xlsx");
        FileOutputStream out = new FileOutputStream(file);
        workBook.write(out);
        out.close();
        System.out.println("Excel written successfully..");
        workBook.close();

        return file;
    }

}
