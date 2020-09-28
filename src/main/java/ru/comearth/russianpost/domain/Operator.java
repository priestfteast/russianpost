package ru.comearth.russianpost.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Entity
@Data
@NoArgsConstructor
public class Operator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private LocalDate employementDate;

    private boolean fired;

    private LocalDate retirementDate;

    @Enumerated(value = EnumType.STRING)
    private Shift shift;

    @OneToMany
    private List<TimeStats> dailyStats = new ArrayList<>();

    @OneToMany
    private List<CSAT> dailyCSAT = new ArrayList<>();


    @Override
    public String toString() {
        return "Operator: " +
                 name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operator operator = (Operator) o;
        return Objects.equals(id, operator.id) &&
                Objects.equals(name, operator.name) &&
                Objects.equals(employementDate, operator.employementDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, employementDate);
    }

}