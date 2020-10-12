package ru.comearth.russianpost.services;

import ru.comearth.russianpost.domain.TimeStats;

import java.time.LocalDate;
import java.util.List;

public interface ChartService {
    List<LocalDate> getChartDays(LocalDate startDate, LocalDate endDate, String name, String stats);

    List<String> getCSATasString(LocalDate startDate, LocalDate endDate, String name, List<LocalDate> days);

    List<Double> getCSATasDouble(String name, List<LocalDate> days);

    List<Double> getDCSATasDouble(String name, List<LocalDate> days);

    List<TimeStats> getTimeStatsData(String name, List<LocalDate> days);

    List<Integer> getAHTData(String name, List<LocalDate> days);

    List<Integer> getHoldData(String name, List<LocalDate> days);

    int countDistinctByNameAndDateBetween(String name, LocalDate start, LocalDate end, String type);

    List<Double> getAhtByExperience(String name, LocalDate start, LocalDate end);

    TimeStats countAverageTimeStats(LocalDate start, LocalDate end);

    List<TimeStats> uniteDaysAndStats(List<LocalDate> days, List<TimeStats> stats, String operator);
}
