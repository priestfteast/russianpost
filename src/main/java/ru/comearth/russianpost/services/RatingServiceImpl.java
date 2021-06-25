package ru.comearth.russianpost.services;

import org.springframework.stereotype.Service;
import ru.comearth.russianpost.domain.CSAT;
import ru.comearth.russianpost.domain.Operator;
import ru.comearth.russianpost.domain.TimeStats;
import ru.comearth.russianpost.repositories.CSATRepository;
import ru.comearth.russianpost.repositories.TimeStatsRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RatingServiceImpl implements RatingService {

    private final CSATRepository csatRepository;
    private final TimeStatsRepository timeStatsRepository;
    private final CSATService csatService;
    private final TimeStatsService timeStatsService;
    private final OperatorService operatorService;

    public RatingServiceImpl(CSATRepository csatRepository, TimeStatsRepository timeStatsRepository, CSATService csatService, TimeStatsService timeStatsService, OperatorService operatorService) {
        this.csatRepository = csatRepository;
        this.timeStatsRepository = timeStatsRepository;
        this.csatService = csatService;
        this.timeStatsService = timeStatsService;
        this.operatorService = operatorService;
    }

    @Override
    public List<Double> getRating(HashSet<Operator> operators, LocalDate start, LocalDate end, String criterion){
        List<Double> result = new ArrayList<>();
        switch (criterion) {
            case "AHT":
                operators.forEach(operator -> result.add(timeStatsService.countAverageStats(timeStatsRepository.
                        findAllByOperatorAndDateBetween(operator, start, end)).getAHT()));
                break;
            case "CSAT":
                operators.forEach(operator -> result.add(Double.valueOf(csatService.getOperatorCSAT(operator, start, end)
                        .replaceAll("%.*$", ""))));
                break;
            case "CDSAT":
                operators.forEach(operator -> result.add(Double.valueOf(csatService.getOperatorDCSAT(operator, start, end)
                        .replaceAll("%.*$", ""))));
                break;
        }
        return result;
    }

    @Override
    public HashSet<Operator> getOperators(LocalDate start, LocalDate end, String criterion, String exp){

        List<TimeStats> timeStats = new ArrayList<>();
        List<CSAT> csatStats = new ArrayList<>();

        HashSet<Operator> operators = new HashSet<>();
        switch (criterion) {
            case "AHT" : timeStats = timeStatsRepository.findAllByDateBetween(start,end);
                operators = timeStats.stream().map(TimeStats::getOperator).collect(Collectors.toCollection(HashSet::new));
            break;
            case "CSAT" :
            case "CDSAT" : csatStats = csatRepository.findAllByDateBetween(start,end);
                operators = csatStats.stream().map(CSAT::getOperator).collect(Collectors.toCollection(HashSet::new));
                break;
        }

        operators = operators.stream().filter(operator -> operator.isFired()==false).collect(Collectors.toCollection(HashSet::new));

        switch (exp){
            case "all": return operators;
            case "first": return operators.stream().filter(operator -> ChronoUnit.DAYS.between(operator.getEmployementDate(),LocalDate.now())<=7)
                    .collect(Collectors.toCollection(HashSet::new));
            case "second": return operators.stream().filter(operator -> ChronoUnit.DAYS.between(operator.getEmployementDate(),LocalDate.now())>7
                    && ChronoUnit.DAYS.between(operator.getEmployementDate(),LocalDate.now())<=14).collect(Collectors.toCollection(HashSet::new));
            case "third": return operators.stream().filter(operator -> ChronoUnit.DAYS.between(operator.getEmployementDate(),LocalDate.now())>14
                    && ChronoUnit.DAYS.between(operator.getEmployementDate(),LocalDate.now())<=21).collect(Collectors.toCollection(HashSet::new));
            case "fourth": return operators.stream().filter(operator -> ChronoUnit.DAYS.between(operator.getEmployementDate(),LocalDate.now())>21
                    && ChronoUnit.DAYS.between(operator.getEmployementDate(),LocalDate.now())<=30).collect(Collectors.toCollection(HashSet::new));
            case "month": return operators.stream().filter(operator -> ChronoUnit.DAYS.between(operator.getEmployementDate(),LocalDate.now())>30
                    && ChronoUnit.DAYS.between(operator.getEmployementDate(),LocalDate.now())<=60).collect(Collectors.toCollection(HashSet::new));
            case "two months": return operators.stream().filter(operator -> ChronoUnit.DAYS.between(operator.getEmployementDate(),LocalDate.now())>60)
                   .collect(Collectors.toCollection(HashSet::new));
            default: return operators;
        }
    }

    @Override
    public TreeMap<String, Double> getSortedRating(HashSet<Operator> operators, List<Double> data, String endDate){
        TreeMap<String,Double> result = new TreeMap<>();
        int i = 0;

        for (Operator op:operators
             ) {

            int weeks = (int) ChronoUnit.WEEKS.between(op.getEmployementDate(),LocalDate.parse(endDate));
            String operInfo = String.format("%s. Работает месяцев:%d недель:%d ", op.getFullName(), weeks/4, weeks%4);

            result.put(operInfo, Math.floor(data.get(i) * 100) / 100);
            i++;
        }
        return result;
    }

    @Override
    public Integer[] countCategories(Collection<Double> data, String request) {
        Integer[] categories = {0,0,0,0};
        data.forEach(element -> {
            if(request.equals("AHT")) {
                if (element < 181)
                    categories[0] += 1;
                else if (element > 181 && element < 211)
                    categories[1] += 1;
                else if (element > 210 && element < 251)
                    categories[2] += 1;
                else
                    categories[3] += 1;
            }
            if(request.equals("CSAT")) {
                if (element > 94)
                    categories[0] += 1;
                else if (element < 95 && element > 89)
                    categories[1] += 1;
                else if (element < 90 && element > 84)
                    categories[2] += 1;
                else
                    categories[3] += 1;
            }
            if(request.equals("CDSAT")) {
                if (element <=2)
                    categories[0] += 1;
                else if (element > 2 && element <=3)
                    categories[1] += 1;
                else if (element > 3 && element <=5)
                    categories[2] += 1;
                else
                    categories[3] += 1;
            }
                });

        return categories;
    }
}
