package ru.comearth.russianpost.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.comearth.russianpost.domain.TimeStats;

public interface TimeStatsRepository extends JpaRepository<TimeStats,Long> {
}
