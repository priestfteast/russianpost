package ru.comearth.russianpost.domain;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
public class CSAT {

    public CSAT(String name, Double overallScore, Double interestScore, Double profScore, Double goodWillScore, Double averageScore, Operator operator) {
        this.name = name;
        this.overallScore = overallScore;
        this.interestScore = interestScore;
        this.profScore = profScore;
        this.goodWillScore = goodWillScore;
        this.averageScore = averageScore;
        this.operator = operator;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private  String name;

    private Double overallScore;

    private Double interestScore;

    private Double profScore;

    private Double goodWillScore;

    private Double averageScore;

    private Double phoneNumber;

    private Double questionType;

    private Double recordName;

    @ManyToOne
    private Operator operator;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CSAT csat = (CSAT) o;
        return Objects.equals(id, csat.id) &&
                Objects.equals(date, csat.date) &&
                Objects.equals(name, csat.name) &&
                Objects.equals(phoneNumber, csat.phoneNumber) &&
                Objects.equals(recordName, csat.recordName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, name, phoneNumber, recordName);
    }

    @Override
    public String toString() {
        return " Звонок принят: "+ date +
                ", Оператором: " + name +
                ", CSAT = " + averageScore;
    }
}
