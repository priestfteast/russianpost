package ru.comearth.russianpost.services;

import ru.comearth.russianpost.domain.Operator;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

public interface RatingService {
    List<Double> getRating(HashSet<Operator> operators, LocalDate start, LocalDate end, String criterion);

    HashSet<Operator> getOperators(LocalDate start, LocalDate end, String criterion, String exp);

    TreeMap<String,Double> getSortedRating(HashSet<Operator> operators, List<Double> data);
}
