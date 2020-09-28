package ru.comearth.russianpost.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.comearth.russianpost.domain.Operator;

public interface OperatorRepository extends JpaRepository<Operator,Long> {
}
