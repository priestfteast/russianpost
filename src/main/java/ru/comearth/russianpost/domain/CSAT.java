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
public class CSAT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private  String name;

    private Long overallScore;

    private Long interestScore;

    private Long profScore;

    private Long goodWillScore;

    private Long averageScore;

    private String phoneNumber;

    private String questionType;

    private String recordName;

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
