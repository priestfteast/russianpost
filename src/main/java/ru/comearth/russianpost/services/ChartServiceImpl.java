package ru.comearth.russianpost.services;

import org.springframework.stereotype.Service;
import ru.comearth.russianpost.domain.CSAT;
import ru.comearth.russianpost.domain.Operator;
import ru.comearth.russianpost.domain.TimeStats;
import ru.comearth.russianpost.repositories.CSATRepository;
import ru.comearth.russianpost.repositories.TimeStatsRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;



@Service
public class ChartServiceImpl implements ChartService {

    private final CSATRepository csatRepository;
    private final TimeStatsRepository timeStatsRepository;
    private final CSATService csatService;
    private final TimeStatsService timeStatsService;
    private final OperatorService operatorService;

    public ChartServiceImpl(CSATRepository csatRepository, TimeStatsRepository timeStatsRepository, CSATService csatService, TimeStatsService timeStatsService, OperatorService operatorService) {
        this.csatRepository = csatRepository;
        this.timeStatsRepository = timeStatsRepository;
        this.csatService = csatService;
        this.timeStatsService = timeStatsService;
        this.operatorService = operatorService;
    }

    @Override
    public List<LocalDate> getChartDays(LocalDate startDate, LocalDate endDate, String name, String stats) {

        List<LocalDate> totalDates = getAllDates(startDate,endDate);

        List<LocalDate> dates = new ArrayList<>();

        if (name.equals("[all]")) {
            switch (stats) {
                case "CSAT":
                    return totalDates.stream().filter(date -> csatRepository.getFirstByDate(date) != null).collect(Collectors.toList());
                case "AHT":
                    return totalDates.stream().filter(date -> timeStatsRepository.getFirstByDate(date) != null).collect(Collectors.toList());
            }
        } else {
            switch (stats) {
                case "CSAT":
                    return totalDates.stream().filter(date -> csatRepository.getFirstByDateAndName(date, name) != null).collect(Collectors.toList());
                case "AHT":
                    return totalDates.stream().filter(date -> timeStatsRepository.getFirstByDateAndName(date, name) != null).collect(Collectors.toList());
            }
        }
        return dates;
    }

    @Override
    public List<String> getCSATasString(LocalDate startDate, LocalDate endDate, String name, List<LocalDate> days)  {

        List<String> result = new ArrayList<>();

        if (name.equals("[all]")) {
            List<Operator> operators = operatorService.getAllOperators("[all]");
            operators.forEach(operator-> {
                StringBuilder sb = new StringBuilder();
                sb.append(operator.getFullName()+" CSAT = ");
                sb.append(csatService.getOperatorCSAT(operator, startDate, endDate));
                sb.append(" DCSAT = "+ csatService.getOperatorDCSAT(operator, startDate, endDate));
                sb.append(" за "+startDate+ "-"+endDate);
                if(!sb.toString().contains("NaN%"))
                result.add(sb.toString().replaceAll("\\.",","));
            });
            String overAllStats = " За период с "+startDate+ " по "+endDate+
                    " общий CSAT  = "+ csatService.getOverallCSAT( startDate, endDate)+
                    " общий DCSAT  = "+csatService.getOverallDCSAT(startDate,endDate);
            result.add(0,overAllStats.replaceAll("\\.",","));
        }
        else {
            Operator operator = operatorService.findByName(name);

            days.forEach(date-> {
                StringBuilder sb = new StringBuilder();
                sb.append(operator.getFullName()+" CSAT = ");
                sb.append(csatService.getOperatorCSAT(operator, date, date));
                sb.append(" DCSAT = "+ csatService.getOperatorDCSAT(operator, date, date));
                sb.append(" за "+date);
                if(!sb.toString().contains("NaN%"))
                    result.add(sb.toString().replaceAll("\\.",","));
            });
            String overAllStats = " За период с "+startDate+ " по "+endDate+
                    " общий CSAT  = "+ csatService.getOperatorCSAT(operator, startDate, endDate)+
                    " общий DCSAT  = "+csatService.getOperatorDCSAT(operator, startDate,endDate);
            result.add(0,overAllStats.replaceAll("\\.",","));
        }
            return result;

    }

    @Override
    public List<Double> getCSATasDouble( String name, List<LocalDate> days) {
        List<Double> result = new ArrayList<>();

        if (name.equals("[all]")) {
            days.forEach(date -> result.add(Double.valueOf(csatService.getOverallCSAT(date,date).replaceAll("%.*$",""))));
        }
        else {
            Operator operator = operatorService.findByName(name);
            days.forEach(date -> result.add(Double.valueOf(csatService.getOperatorCSAT(operator, date,date).replaceAll("%.*$",""))));
        }
        return result;
    }

    @Override
    public List<Double> getDCSATasDouble(String name, List<LocalDate> days) {
        List<Double> result = new ArrayList<>();

        if (name.equals("[all]")) {
            days.forEach(date -> result.add(Double.valueOf(csatService.getOverallDCSAT(date,date).replaceAll("%.*$",""))));
        }
        else {
            Operator operator = operatorService.findByName(name);
            days.forEach(date -> result.add(Double.valueOf(csatService.getOperatorDCSAT(operator, date,date).replaceAll("%.*$",""))));
        }
        return result;
    }

    @Override
    public List<TimeStats> getTimeStatsData(String name, List<LocalDate> days) {
        List<TimeStats> result = new ArrayList<>();

        if (name.equals("[all]")) {
            if(days.size()==1)
                result.addAll(timeStatsRepository.findAllByDateBetween(days.get(0),days.get(0)));
            else
                days.forEach(date -> result.add(timeStatsService.getOverallStats(date,date)));
        }
        else {
            Operator operator = operatorService.findByName(name);
            days.forEach(date -> result.add(timeStatsService.getOverallOperatorStats(operator, date,date)));
        }
        return result;
    }

    @Override
    public List<Integer> getAHTData( String name, List<LocalDate> days) {
        List<Integer> result = new ArrayList<>();

        if (name.equals("[all]")) {
            days.forEach(date -> result.add(timeStatsService.getOverallStats(date,date).getAHT().intValue()));
        }
        else {
            Operator operator = operatorService.findByName(name);

            days.forEach(date -> result.add(timeStatsService.getOverallOperatorStats(operator, date,date).getAHT().intValue()));
        }
        return result;
    }

    @Override
    public List<Integer> getHoldData(String name, List<LocalDate> days) {
        List<Integer> result = new ArrayList<>();

        if (name.equals("[all]")) {
            days.forEach(date -> result.add(timeStatsService.getOverallStats(date,date).getHold().intValue()));
        }
        else {
            Operator operator = operatorService.findByName(name);
            days.forEach(date -> result.add(timeStatsService.getOverallOperatorStats(operator, date,date).getHold().intValue()));
        }
        return result;
    }

    List<LocalDate> getAllDates(LocalDate startDate, LocalDate endDate){
        List<LocalDate> totalDates = new ArrayList<>();
        while (!startDate.isAfter(endDate)) {
            totalDates.add(startDate);
            startDate = startDate.plusDays(1);
        }
        return totalDates;
    }


    @Override
    public int countDistinctByNameAndDateBetween(String name, LocalDate start, LocalDate end, String type) {
        HashSet<String> names = new HashSet<>();
        List<CSAT> csats=new ArrayList<>();
        List<TimeStats> timeStats=new ArrayList<>();
        if(type.equals("CSAT")) {
            csats = csatRepository.findAllByDateBetween(start, end);
            csats.forEach(csat -> names.add(csat.getName()));
        }
        else
        {
            timeStats = timeStatsRepository.findAllByDateBetween(start, end);
            timeStats.forEach(timeStats1 -> names.add(timeStats1.getName()));
        }
        return names.size();
    }

    @Override
    public List<Double> getAhtByExperience(String name, LocalDate start, LocalDate end){
        List<Double> result = new ArrayList<>();
                List<TimeStats> stats = (name.equals("[all]")) ? timeStatsRepository.findAllByDateBetween(start, end) :
                timeStatsRepository.findAllByOperatorAndDateBetween(operatorService.findByName(name),start,end);
        result.add(timeStatsService.countAverageStats(stats.stream().filter(stats1->
                ChronoUnit.DAYS.between(stats1.getOperator().getEmployementDate(), stats1.getDate())<=7).collect(Collectors.toList())).getAHT());
        result.add(timeStatsService.countAverageStats(stats.stream().filter(stats1->ChronoUnit.DAYS.between(stats1.getOperator().
                getEmployementDate(), stats1.getDate())>7 && ChronoUnit.DAYS.between(stats1.getOperator().getEmployementDate(), stats1.getDate())<=14)
                .collect(Collectors.toList())).getAHT());
        result.add(timeStatsService.countAverageStats(stats.stream().filter(stats1->ChronoUnit.DAYS.between(stats1.getOperator().
                getEmployementDate(), stats1.getDate())>14 && ChronoUnit.DAYS.between(stats1.getOperator().getEmployementDate(), stats1.getDate())<=21)
                .collect(Collectors.toList())).getAHT());
        result.add(timeStatsService.countAverageStats(stats.stream().filter(stats1->ChronoUnit.DAYS.between(stats1.getOperator().
                getEmployementDate(), stats1.getDate())>21 && ChronoUnit.DAYS.between(stats1.getOperator().getEmployementDate(), stats1.getDate())<=30)
                .collect(Collectors.toList())).getAHT());
        result.add(timeStatsService.countAverageStats(stats.stream().filter(stats1->ChronoUnit.DAYS.between(stats1.getOperator().
                getEmployementDate(), stats1.getDate())>30 && ChronoUnit.DAYS.between(stats1.getOperator().getEmployementDate(), stats1.getDate())<=60)
                .collect(Collectors.toList())).getAHT());
        result.add(timeStatsService.countAverageStats(stats.stream().filter(stats1->ChronoUnit.DAYS.between(stats1.getOperator().
                getEmployementDate(), stats1.getDate())>60).collect(Collectors.toList())).getAHT());

        return result;

    }

    @Override
    public int countAllShifts(String query, String name) {

        if(query.equals("AHT"))
            return timeStatsRepository.countByName(name);
        else
            return csatRepository.countByName(name);
    }

    @Override
    public TimeStats countAverageTimeStats(LocalDate start, LocalDate end, String name){
        switch (name) {
            case "[all]": return timeStatsService.countAverageStats(timeStatsRepository.findAllByDateBetween(start, end));
            default: return timeStatsService.countAverageStats
                    (timeStatsRepository.findAllByOperatorAndDateBetween(operatorService.findByName(name), start,end));
        }
    }

    @Override
    public List<TimeStats> uniteDaysAndStats(List<LocalDate> days, List<TimeStats> stats, String operator){

            if(days.size()!=stats.size())
                return stats;

            for (int i = 0; i < stats.size(); i++) {
                stats.get(i).setDate(days.get(i));
                if(operator.equals("[all]"))
                    stats.get(i).setName("All operators");
                else
                    stats.get(i).setName(operator);
            }

            return stats;
    }


}
