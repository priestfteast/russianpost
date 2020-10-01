package ru.comearth.russianpost.services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.comearth.russianpost.domain.CSAT;
import ru.comearth.russianpost.repositories.CSATRepository;
import ru.comearth.russianpost.repositories.OperatorRepository;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class XlsServiceImpl implements XlsService {

    private final OperatorRepository operatorRepository;
    private final CSATRepository csatRepository;

    public XlsServiceImpl(OperatorRepository operatorRepository, CSATRepository csatRepository) {
        this.operatorRepository = operatorRepository;
        this.csatRepository = csatRepository;
    }

    @Override
    public List<CSAT> parseCSAT(Path path) throws Exception {
        List<CSAT> stats = new ArrayList<>();
        File file = null;
        LocalDate date;
        try {
            System.out.println(Files.list(path).findFirst().get().toString());
            file = new File(Files.list(path).findFirst().get().toString());

            // Creating a Workbook from an Excel file (.xls or .xlsx)
            XSSFWorkbook readbookXlsx;
            if(!file.getName().endsWith(".xlsx")) {
                Files.deleteIfExists(file.toPath());
                throw new Exception("\n" + "Загруженный файл не является файлом EXCEL!" + "\n" + "Загрузите корректный файл" + "\n");
            }
            else
                readbookXlsx = new XSSFWorkbook(new FileInputStream(file));

            // Getting the Sheet at index zero
            Sheet readsheet = readbookXlsx.getSheetAt(0);

            // Create a DataFormatter to format and get each cell's value as String
            DataFormatter dataFormatter = new DataFormatter();

            //   Iterating throw sheet
            for (Row row : readsheet) {

                String firstRowCell = dataFormatter.formatCellValue(row.getCell(0));

                //   Check if .xlsx file is correct
                if (row.getRowNum() == 0 && (!firstRowCell.equals("ФИО оператора") || row.getLastCellNum() != 12)) {
                    readbookXlsx.close();
                    Files.deleteIfExists(file.toPath());
                    throw new Exception("\n" + "Загруженный файл не является файлом статистики!" + "\n" + "Загрузите корректный файл" + "\n");
                } else {
                    String dateString = readsheet.getRow(1).getCell(2).getStringCellValue().replaceAll("\\s.*$", "");
                    date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                }

                //   Check if stats were not uploaded earlier
                if(csatRepository.existsByDate(date)) {
                    readbookXlsx.close();
                    Files.deleteIfExists(file.toPath());
                    throw new Exception("\n" + "Данные за " + date + " уже загружены на сервер!!!" + "\n" +
                            "Удалите данные с сервера, либо загрузите корректный файл." + "\n");
                }

                if (row.getRowNum() > 0) {
                    CSAT csat = new CSAT();

                    for (int i = 0; i < row.getLastCellNum(); i++) {
                        String cell_data = dataFormatter.formatCellValue(row.getCell(i));

                        switch (i) {
                            case 0:
                                csat.setName(cell_data);
                                break;
                            case 1:
                                break;
                            case 2:
                                csat.setDate(date);
                                break;
                            case 3:
                                csat.setPhoneNumber(cell_data);
                                break;
                            case 4:
                                csat.setQuestionType(cell_data);
                                break;
                            case 5:
                                csat.setOverallScore(Double.valueOf(cell_data));
                                break;
                            case 6:
                                csat.setInterestScore(Double.valueOf(cell_data));
                                break;
                            case 7:
                                csat.setProfScore(Double.valueOf(cell_data));
                                break;
                            case 8:
                                csat.setGoodWillScore(Double.valueOf(cell_data));
                                break;
                            case 9:
                                csat.setUsefullInfo(Double.valueOf(cell_data));
                                break;
                            case 10:
                                csat.setAverageScore(Double.valueOf(cell_data));
                                break;
                            case 11:
                                csat.setRecordName(cell_data);
                                break;
                        }
                    }

                //  Check if operator from .xlsx is present in DB
                    if (operatorRepository.findByFullName(csat.getName()) == null) {
                        readbookXlsx.close();
                        Files.deleteIfExists(file.toPath());
                        throw new Exception("\n" + "В базе нет данных об операторе - "+csat.getName() + "\n");
                    } else {
                        csat.setOperator(operatorRepository.findByFullName(csat.getName()));
                    }
                //  Saving CSAT to list
                    stats.add(csat);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        stats.forEach(System.out::println);
//        System.out.println(stats.size());
        return stats;
    }


}
