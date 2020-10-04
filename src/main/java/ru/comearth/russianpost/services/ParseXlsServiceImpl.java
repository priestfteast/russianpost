package ru.comearth.russianpost.services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import ru.comearth.russianpost.domain.CSAT;
import ru.comearth.russianpost.domain.TimeStats;
import ru.comearth.russianpost.repositories.CSATRepository;
import ru.comearth.russianpost.repositories.OperatorRepository;
import ru.comearth.russianpost.repositories.TimeStatsRepository;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ParseXlsServiceImpl implements ParseXlsService {

    private final OperatorRepository operatorRepository;
    private final CSATRepository csatRepository;
    private final TimeStatsRepository timeStatsRepository;

    public ParseXlsServiceImpl(OperatorRepository operatorRepository, CSATRepository csatRepository, TimeStatsRepository timeStatsRepository) {
        this.operatorRepository = operatorRepository;
        this.csatRepository = csatRepository;
        this.timeStatsRepository = timeStatsRepository;
    }

    @Override
    public List<CSAT> parseCSAT(Path path) throws Exception {

        List<CSAT> stats = new ArrayList<>();

        //  Getting distinct dates from CSAT BD
        List<LocalDate> dates = csatRepository.findDistinctDates();
        dates.forEach(System.out::println);

        File file = path.toFile();

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
                    throw new Exception("\n" + "Загруженный файл не является файлом статистики с CSAT!" + "\n" + "Загрузите корректный файл" + "\n");
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
                            case 2: {

                                String dateString = cell_data.replaceAll("\\s.*$", "");
                                LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd.MM.yyyy"));

                                //   Check if stats were not uploaded earlier
                                if(dates.contains(date)) {
                                    readbookXlsx.close();
                                    Files.deleteIfExists(file.toPath());
                                    throw new Exception("\n" + "Данные за " + date + " уже загружены на сервер!!!" + "\n" +
                                            "Удалите данные с сервера, либо загрузите корректный файл." + "\n");
                                }

                                csat.setDate(date);
                                break;
                            }
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

        readbookXlsx.close();
        Files.deleteIfExists(file.toPath());

        return stats;
    }

    @Override
    public List<TimeStats> parseTimeStats(Path path) throws Exception {

        List<TimeStats> stats = new ArrayList<>();

        //  Getting distinct dates from TimeStats BD
        List<LocalDate> dates = timeStatsRepository.findDistinctDates();
        dates.forEach(System.out::println);

        File file = path.toFile();

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
            if (row.getRowNum() == 0 && (!firstRowCell.equals("Дата") || row.getLastCellNum() != 13)) {
                readbookXlsx.close();
                Files.deleteIfExists(file.toPath());
                throw new Exception("\n" + "Загруженный файл не является файлом статистики c AHT!" + "\n" + "Загрузите корректный файл" + "\n");
            }


            if (row.getRowNum() > 0) {
                TimeStats timeStats = new TimeStats();

                for (int i = 0; i < row.getLastCellNum(); i++) {
                    String cell_data = dataFormatter.formatCellValue(row.getCell(i));

                    switch (i) {
                        case 0: {
                            String dateString = cell_data.replaceAll("\\s.*$", "");
                            LocalDate date = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("dd.MM.yy"));

                            //   Check if stats were not uploaded earlier
                            if (dates.contains(date)) {
                                readbookXlsx.close();
                                Files.deleteIfExists(file.toPath());
                                throw new Exception("\n" + "Данные за " + date + " уже загружены на сервер!!!" + "\n" +
                                        "Удалите данные с сервера, либо загрузите корректный файл." + "\n");
                            }

                            timeStats.setDate(date);
                            break;
                        }
                        case 1:
                            break;
                        case 2:
                            if (cell_data.equals("ИТОГО:")) {
                                i = row.getLastCellNum();
                                timeStats.setDate(LocalDate.parse("2000-01-01"));
                                break;
                            }
                        case 3:
                            timeStats.setName(cell_data);
                            break;
                        case 4:
                            timeStats.setTotalCalls(Double.valueOf(cell_data));
                            break;
                        case 5:
                            timeStats.setATT(Double.valueOf(cell_data));
                            break;
                        case 6:
                            timeStats.setACW(Double.valueOf(cell_data));
                            break;
                        case 7:
                            timeStats.setAHT(Double.valueOf(cell_data));
                            break;
                        case 8:
                            timeStats.setHold(Double.valueOf(cell_data));
                            break;
                        case 9:
                            timeStats.setASA(Double.valueOf(cell_data));
                            break;
                        case 10:
                            timeStats.setOccupancy(Double.valueOf(cell_data));
                            break;
                        case 11:
                            timeStats.setUtilization(Double.valueOf(cell_data));
                            break;
                        case 12:
                            timeStats.setTransferedCalls(Double.valueOf(cell_data));
                            break;
                    }
                }
                //  Check for "ИТОГО:" exclusion
                if (timeStats.getDate().equals(LocalDate.parse("2000-01-01")))
                    break;

                else {
                    //  Check if operator from .xlsx is present in DB & Check for "ИТОГО:" exclusion
                    if (timeStats.getName() != null && operatorRepository.findByFullName(timeStats.getName()) == null) {
                        readbookXlsx.close();
                        Files.deleteIfExists(file.toPath());
                        throw new Exception("\n" + "В базе нет данных об операторе - " + timeStats.getName() + "\n");
                    } else {
                        timeStats.setOperator(operatorRepository.findByFullName(timeStats.getName()));
                    }
                    //  Saving CSAT to list
                    stats.add(timeStats);
                }
            }
        }
        readbookXlsx.close();
        Files.deleteIfExists(file.toPath());

        return stats;
    }

}
