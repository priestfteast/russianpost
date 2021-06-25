package ru.comearth.russianpost.services;

import ru.comearth.russianpost.domain.TimeStats;

import java.time.LocalDate;
import java.util.List;

public interface TimeService {
    List<Integer> getDigitsForWeeks(List<LocalDate> dates);
    List<LocalDate> getWeeksAsDates(List<LocalDate> dates);
    List<LocalDate> getLastFourWeeks(LocalDate end);
    List<String> getWeeksAsStrings(List<LocalDate> dates);
    List<Double> getAhtByWeeks( List<LocalDate> dates, String name);
    List<String> getDcsatByWeeks(List<LocalDate> dates, String name);
    List<String> getCsatByWeeks(List<LocalDate> dates, String name);
    TimeStats countAverageTimeStats(LocalDate start, LocalDate end, String name);
}
