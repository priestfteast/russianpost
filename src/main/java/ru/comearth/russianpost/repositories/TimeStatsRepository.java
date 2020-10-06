package ru.comearth.russianpost.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.comearth.russianpost.domain.Operator;
import ru.comearth.russianpost.domain.TimeStats;

import java.time.LocalDate;
import java.util.List;

public interface TimeStatsRepository extends JpaRepository<TimeStats,Long> {

    List<TimeStats> findAllByDateBetween(LocalDate start, LocalDate end);

    List<TimeStats> findAllByOperatorAndDateBetween(Operator operator, LocalDate start, LocalDate end);

    TimeStats getFirstByDate(LocalDate date);

    TimeStats getFirstByDateAndName(LocalDate date,String name);

    void deleteAllByDate(LocalDate date);

    @Query("SELECT DISTINCT date FROM TimeStats")
    List<LocalDate> findDistinctDates();
}
