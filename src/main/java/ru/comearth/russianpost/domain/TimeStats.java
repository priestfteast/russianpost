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

    public TimeStats(String name, Double ACW, Double ATT, Double ASA, Double AHT, Double hold, Double totalCalls, Double transferedCalls, Double occupancy, Double utilization, Operator operator) {
        this.name = name;
        this.ACW = ACW;
        this.ATT = ATT;
        this.ASA = ASA;
        this.AHT = AHT;
        this.hold=hold;
        this.totalCalls = totalCalls;
        this.transferedCalls = transferedCalls;
        this.occupancy = occupancy;
        this.utilization = utilization;
        this.operator = operator;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private  String name;

    private Double ACW;

    private Double ATT;

    private Double ASA;

    private Double AHT;

    private Double hold;

    private Double totalCalls;

    private Double transferedCalls;

    private Double occupancy;

    private Double utilization;


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
