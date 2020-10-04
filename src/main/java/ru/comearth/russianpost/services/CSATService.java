package ru.comearth.russianpost.services;

import ru.comearth.russianpost.domain.CSAT;
import ru.comearth.russianpost.domain.Operator;
import ru.comearth.russianpost.domain.TimeStats;

import java.time.LocalDate;
import java.util.List;

public interface CSATService {

    CSAT getOverallCSAT(LocalDate start, LocalDate end) ;

    CSAT getOverallOperatorCSAT(Operator operator, LocalDate start, LocalDate end) ;

    CSAT countAverageCSAT(List<CSAT> stats);

    String getOperatorCSAT(Operator operator, LocalDate start, LocalDate end);

    String getAverageCSAT(LocalDate start, LocalDate end);

    String getOperatorDCSAT(Operator operator, LocalDate start, LocalDate end);

    String getAverageDCSAT(LocalDate start, LocalDate end);

    void removeAllByDate(LocalDate date) throws Exception;

    void saveAll(List<CSAT> stats);

}
