package ru.comearth.russianpost.services;

import ru.comearth.russianpost.domain.Operator;
import ru.comearth.russianpost.domain.TimeStats;

import java.time.LocalDate;
import java.util.List;

public interface TimeStatsService {

    TimeStats getOverallStats(LocalDate start, LocalDate end) ;

    TimeStats getOverallOperatorStats(Operator operator, LocalDate start, LocalDate end) ;

    TimeStats countAverageStats(List<TimeStats> stats);

    void saveAll(List<TimeStats> stats);

    void removeAllByDate(LocalDate date) throws Exception;

}
