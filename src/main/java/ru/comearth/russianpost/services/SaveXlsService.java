package ru.comearth.russianpost.services;

import ru.comearth.russianpost.domain.TimeStats;

import java.io.File;
import java.util.List;

public interface SaveXlsService {
    File saveTimeStatsToXLS(List<String> request, List<TimeStats> stats, TimeStats overAllStats) throws Exception;

    File saveCSATStatsToXLS(List<String> request, List<String> stats) throws Exception;
}
