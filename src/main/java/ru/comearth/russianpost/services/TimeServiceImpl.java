package ru.comearth.russianpost.services;

import org.springframework.stereotype.Service;
import ru.comearth.russianpost.domain.TimeStats;
import ru.comearth.russianpost.repositories.CSATRepository;
import ru.comearth.russianpost.repositories.TimeStatsRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TimeServiceImpl implements TimeService {

    private final CSATRepository csatRepository;
    private final TimeStatsRepository timeStatsRepository;
    private final CSATService csatService;
    private final TimeStatsService timeStatsService;
    private final OperatorService operatorService;

    public TimeServiceImpl(CSATRepository csatRepository, TimeStatsRepository timeStatsRepository, CSATService csatService, TimeStatsService timeStatsService, OperatorService operatorService) {
        this.csatRepository = csatRepository;
        this.timeStatsRepository = timeStatsRepository;
        this.csatService = csatService;
        this.timeStatsService = timeStatsService;
        this.operatorService = operatorService;
    }


    @Override
    public List<Integer> getDigitsForWeeks(List<LocalDate> dates) {
        List<Integer> digits = new ArrayList<>();


        dates.forEach(date -> {

            if (digits.size() == 0) {

                if (date.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                    System.out.println("Добавляем первые две позиции  и это воскресенье - " + dates.indexOf(date));
                    digits.add(dates.indexOf(date));
                    digits.add(dates.indexOf(date));
                }

                if (!date.getDayOfWeek().equals(DayOfWeek.MONDAY) && !date.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
                    System.out.println("Добавляем первую позицию в список и это не понедельник - " + dates.indexOf(date));
                    digits.add(dates.indexOf(date));
                }
            }
            if (date.getDayOfWeek().equals(DayOfWeek.MONDAY) && !digits.contains(dates.indexOf(date))) {
                System.out.println("Добавляем в список понедельник - " + dates.indexOf(date));
                digits.add(dates.indexOf(date));
                if(dates.indexOf(date)==dates.size()-1){
                    digits.add(dates.indexOf(date));
                }
            }
            if (date.getDayOfWeek().equals(DayOfWeek.SUNDAY) && !digits.contains(dates.indexOf(date))) {
                System.out.println("Добавляем в список воскресенье - " + dates.indexOf(date));
                digits.add(dates.indexOf(date));
            }
            if (dates.indexOf(date) == dates.size() - 1 && digits.get(digits.size() - 1) != dates.indexOf(date)) {
                System.out.println("Добавляем в список последнюю дату - " + dates.indexOf(date));
                digits.add(dates.indexOf(date));
            }

        });
        return digits;
    }

    @Override
    public List<LocalDate> getLastFourWeeks(LocalDate end) {
        List<LocalDate> days = new ArrayList<>();
        days.add(end);
        int counter = 0;


        for (int i = 0; counter < 4; i++) {
            LocalDate day = end.minusDays(i);
                if(!days.contains(day))
                days.add(day);
                if(day.getDayOfWeek().equals(DayOfWeek.MONDAY))
                counter++;
            }
        Collections.reverse(days);
        return days;
    }

    @Override
    public List<LocalDate> getWeeksAsDates(List<LocalDate> dates) {

        List<Integer> digits = getDigitsForWeeks(dates);

        List<LocalDate> weeks = new ArrayList<>();

        digits.forEach(p -> weeks.add(dates.get(p)));

        return weeks;

    }

    @Override
    public List<String> getWeeksAsStrings(List<LocalDate> dates) {

        List<String> weeks = new ArrayList<>();
        StringBuilder week = new StringBuilder();

        for (LocalDate date : dates) {
            if (week.toString().endsWith(" - ")) {
                week.append(date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                weeks.add(week.toString());
                week = new StringBuilder();
            } else
                week.append(date.format(DateTimeFormatter.ofPattern("dd.MM")) + " - ");
        }

        return weeks;
    }

    @Override
    public List<Double> getAhtByWeeks(List<LocalDate> dates, String name) {
        dates.forEach(System.out::println);
        List<Double> result = new ArrayList<>();

        for (int i = 0; i < dates.size(); i++) {
            result.add(countAverageTimeStats(dates.get(i), dates.get(i + 1), name).getAHT());
            i++;
        }

        return result;
    }

    @Override
    public List<String> getDcsatByWeeks(List<LocalDate> dates, String name) {
        List<String> result = new ArrayList<>();

        for (int i = 0; i < dates.size(); i++) {
            if (name.equals("[all]")) {
                result.add(csatService.getOverallDCSAT(dates.get(i), dates.get(i + 1)));
            } else {
                result.add(csatService.getOperatorDCSAT(operatorService.findByName(name), dates.get(i), dates.get(i + 1)));
            }
            i++;
        }
        return result;
    }

    @Override
    public List<String> getCsatByWeeks(List<LocalDate> dates, String name) {
        List<String> result = new ArrayList<>();

        for (int i = 0; i < dates.size(); i++) {
            if (name.equals("[all]")) {
                result.add(csatService.getOverallCSAT(dates.get(i), dates.get(i + 1)));
            } else {
                result.add(csatService.getOperatorCSAT(operatorService.findByName(name), dates.get(i), dates.get(i + 1)));
            }
            i++;
        }
        return result;
    }

    @Override
    public TimeStats countAverageTimeStats(LocalDate start, LocalDate end, String name){
        switch (name) {
            case "[all]": return timeStatsService.countAverageStats(timeStatsRepository.findAllByDateBetween(start, end));
            default: return timeStatsService.countAverageStats
                    (timeStatsRepository.findAllByOperatorAndDateBetween(operatorService.findByName(name), start,end));
        }
    }


}
