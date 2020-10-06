package ru.comearth.russianpost.services;

import ru.comearth.russianpost.domain.TimeStats;

import java.time.LocalDate;
import java.util.List;

public interface ChartService {
    List<LocalDate> getChartDays(LocalDate startDate, LocalDate endDate, String name, String stats);

    List<String> getCSATasString(LocalDate startDate, LocalDate endDate, String name, List<LocalDate> days);

    List<Double> getCSATasDouble(LocalDate startDate, LocalDate endDate, String name, List<LocalDate> days);

    List<Double> getDCSATasDouble(LocalDate startDate, LocalDate endDate, String name, List<LocalDate> days);

    List<Integer> getAHTData(LocalDate startDate, LocalDate endDate, String name, List<LocalDate> days);

    List<Integer> getHoldData(LocalDate startDate, LocalDate endDate, String name, List<LocalDate> days);

    int countDistinctByNameAndDateBetween(String name, LocalDate start, LocalDate end, String type);

    TimeStats countAverageTimeStats(LocalDate start, LocalDate end);
}
