package ru.comearth.russianpost.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.comearth.russianpost.domain.CSAT;
import ru.comearth.russianpost.domain.Operator;

import java.time.LocalDate;
import java.util.List;



public interface CSATRepository extends JpaRepository<CSAT,Long> {

    boolean existsByDate(LocalDate date);

    List<CSAT> findAllByDateBetween(LocalDate start, LocalDate end);

    List<CSAT> findAllByOperatorAndDateBetween(Operator operator, LocalDate start, LocalDate end);

    CSAT getFirstByDate(LocalDate date);

    CSAT getFirstByDateAndName(LocalDate date,String name);

    void deleteAllByDate(LocalDate date);

    @Query("SELECT DISTINCT date FROM CSAT")
    List<LocalDate> findDistinctDates();

    int countAllByDateBetween(LocalDate start, LocalDate end);

    @Query("SELECT  COUNT(distinct date) FROM CSAT where name=?1")
    int countByName(String name);

}
