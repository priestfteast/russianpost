package ru.comearth.russianpost.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.comearth.russianpost.domain.CSAT;
import ru.comearth.russianpost.domain.Operator;

import java.time.LocalDate;
import java.util.List;


public interface CSATRepository extends JpaRepository<CSAT,Long> {

    List<CSAT> findAllByDateBetween(LocalDate start, LocalDate end);

    List<CSAT> findAllByOperatorAndDateBetween(Operator operator, LocalDate start, LocalDate end);
}
