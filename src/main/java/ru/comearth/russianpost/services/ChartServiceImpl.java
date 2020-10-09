package ru.comearth.russianpost.services;

import org.springframework.stereotype.Service;
import ru.comearth.russianpost.domain.CSAT;
import ru.comearth.russianpost.domain.Operator;
import ru.comearth.russianpost.domain.TimeStats;
import ru.comearth.russianpost.repositories.CSATRepository;
import ru.comearth.russianpost.repositories.TimeStatsRepository;

import java.time.LocalDate;
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
                sb.append(" За период с "+startDate+ " по "+endDate+" CSAT "+operator.getFullName() +" = ");
                sb.append(csatService.getOperatorCSAT(operator, startDate, endDate));
                sb.append(" DCSAT = "+ csatService.getOperatorDCSAT(operator, startDate, endDate));
                if(!sb.toString().contains("NaN%"))
                result.add(sb.toString().replaceAll("\\.",","));
            });
            String overAllStats = " За период с "+startDate+ " по "+endDate+" общий CSAT  = "+ csatService.getOverallCSAT( startDate, endDate)+
                    " общий DCSAT  = "+csatService.getOverallDCSAT(startDate,endDate);
            result.add(0,overAllStats.replaceAll("\\.",","));
        }
        else {
            Operator operator = operatorService.findByName(name);

            days.forEach(date-> {
                StringBuilder sb = new StringBuilder();
                sb.append(" За  "+date+" CSAT "+operator.getFullName() +" = ");
                sb.append(csatService.getOperatorCSAT(operator, date, date));
                sb.append(" DCSAT = "+ csatService.getOperatorDCSAT(operator, date, date));
                if(!sb.toString().contains("NaN%"))
                    result.add(sb.toString().replaceAll("\\.",","));
            });
            String overAllStats = " За период с "+startDate+ " по "+endDate+" общий CSAT  = "+ csatService.getOperatorCSAT(operator, startDate, endDate)+
                    " общий DCSAT  = "+csatService.getOperatorDCSAT(operator, startDate,endDate);
            result.add(0,overAllStats.replaceAll("\\.",","));
        }
            return result;

    }

    @Override
    public List<Double> getCSATasDouble(LocalDate startDate, LocalDate endDate, String name, List<LocalDate> days) {
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
    public List<Double> getDCSATasDouble(LocalDate startDate, LocalDate endDate, String name, List<LocalDate> days) {
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
    public List<Integer> getAHTData(LocalDate startDate, LocalDate endDate, String name, List<LocalDate> days) {
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
    public List<Integer> getHoldData(LocalDate startDate, LocalDate endDate, String name, List<LocalDate> days) {
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
        result.add(timeStatsService.countAverageStats(stats.stream().filter(stats1->stats1.getOperator().getEmployementDate().
                until(stats1.getDate()).getDays()<=7).collect(Collectors.toList())).getAHT());
        result.add(timeStatsService.countAverageStats(stats.stream().filter(stats1->stats1.getOperator().getEmployementDate().
                until(stats1.getDate()).getDays()>7 && stats1.getOperator().getEmployementDate().
                until(stats1.getDate()).getDays()<=14).collect(Collectors.toList())).getAHT());
        result.add(timeStatsService.countAverageStats(stats.stream().filter(stats1->stats1.getOperator().getEmployementDate().
                until(stats1.getDate()).getDays()>=14 && stats1.getOperator().getEmployementDate().
                until(stats1.getDate()).getDays()<=21).collect(Collectors.toList())).getAHT());
        result.add(timeStatsService.countAverageStats(stats.stream().filter(stats1->stats1.getOperator().getEmployementDate().
                until(stats1.getDate()).getDays()>21 && stats1.getOperator().getEmployementDate().
                until(stats1.getDate()).getDays()<=30).collect(Collectors.toList())).getAHT());
        result.add(timeStatsService.countAverageStats(stats.stream().filter(stats1->stats1.getOperator().getEmployementDate().
                until(stats1.getDate()).getDays()>=30).collect(Collectors.toList())).getAHT());

        return result;

    }

    @Override
    public TimeStats countAverageTimeStats(LocalDate start, LocalDate end){
        return timeStatsService.countAverageStats(timeStatsRepository.findAllByDateBetween(start,end));
    }

}
