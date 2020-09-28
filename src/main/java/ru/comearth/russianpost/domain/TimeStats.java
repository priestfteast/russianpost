package ru.comearth.russianpost.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
public class TimeStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private LocalDate date;

    private  String name;

    private Long ACW;

    private Long ATT;

    private Long ASA;

    private Long AHT;

    private Long totalCalls;

    private Long transferedCalls;

    private Long occupancy;

    private Long utilization;


    @ManyToOne
    private Operator operator;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeStats timeStats = (TimeStats) o;
        return Objects.equals(id, timeStats.id) &&
                Objects.equals(date, timeStats.date) &&
                Objects.equals(name, timeStats.name) &&
                Objects.equals(AHT, timeStats.AHT);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, name, AHT);
    }

    @Override
    public String toString() {
        return
                "Показатели  " + name + " за " + date +
                ": AHT=" + AHT +
                ", totalCalls=" + totalCalls +
                ", occupancy=" + occupancy;
    }
}
