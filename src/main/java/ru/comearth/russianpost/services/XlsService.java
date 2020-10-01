package ru.comearth.russianpost.services;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.comearth.russianpost.domain.CSAT;

import java.nio.file.Path;
import java.util.List;

public interface XlsService {
    List<CSAT> parseCSAT(Path path) throws Exception;

}
