package ru.comearth.russianpost.services;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.comearth.russianpost.domain.CSAT;
import ru.comearth.russianpost.domain.TimeStats;

import java.nio.file.Path;
import java.util.List;

public interface ParseXlsService {
    List<CSAT> parseCSAT(Path path) throws Exception;
    List<TimeStats> parseTimeStats(Path path)throws Exception;
}
