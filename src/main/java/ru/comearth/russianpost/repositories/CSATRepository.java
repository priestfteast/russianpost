package ru.comearth.russianpost.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.comearth.russianpost.domain.CSAT;

public interface CSATRepository extends JpaRepository<CSAT,Long> {
}
