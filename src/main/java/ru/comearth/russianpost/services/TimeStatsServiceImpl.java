package ru.comearth.russianpost.services;

import org.springframework.stereotype.Service;
import ru.comearth.russianpost.domain.Operator;
import ru.comearth.russianpost.domain.TimeStats;
import ru.comearth.russianpost.repositories.TimeStatsRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class TimeStatsServiceImpl implements TimeStatsService {

    private final TimeStatsRepository timeStatsRepository;

    public TimeStatsServiceImpl(TimeStatsRepository timeStatsRepository) {
        this.timeStatsRepository = timeStatsRepository;
    }

    @Override
    public void saveAll(List<TimeStats> stats) {
        timeStatsRepository.saveAll(stats);
        System.out.println("Saved "+stats.size()+" TimeStats records");
    }

    @Override
    public void removeAllByDate(LocalDate date) throws Exception {
        if(timeStatsRepository.getFirstByDate(date)==null) {
            throw new Exception("В базе нет данных " +
                    "по статистике за " + date +".\n"+
                    " Выберите другую дату" +".\n");
        }
        timeStatsRepository.deleteAllByDate(date);
        System.out.println("Deleted statistics for "+date);
    }

    @Override
    public TimeStats getOverallStats(LocalDate start, LocalDate end)  {

        List<TimeStats> stats = timeStatsRepository.findAllByDateBetween(start,end);

        return countAverageStats(stats);
    }

    @Override
    public TimeStats getOverallOperatorStats(Operator operator,LocalDate start, LocalDate end) {

        List<TimeStats> stats = timeStatsRepository.findAllByOperatorAndDateBetween(operator,start,end);

        return countAverageStats(stats);
    }

    @Override
    public TimeStats countAverageStats(List<TimeStats> stats) {
        TimeStats result = new TimeStats("average",0.0, 0.0, 0.0, 0.0,0.0, 0.0,
                0.0, 0.0, 0.0);
        if(stats.size()==0) return result;
        double size = (double) stats.size();
        stats.forEach(timeStats -> {
            result.setACW(result.getACW() + timeStats.getACW());
            result.setAHT(result.getAHT() + timeStats.getAHT());
            result.setASA(result.getASA() + timeStats.getASA());
            result.setATT(result.getATT() + timeStats.getATT());
            result.setHold(result.getHold()+timeStats.getHold());
            result.setOccupancy(result.getOccupancy() + timeStats.getOccupancy());
            result.setUtilization(result.getUtilization() + timeStats.getUtilization());
            result.setTotalCalls(result.getTotalCalls() + timeStats.getTotalCalls());
            result.setTransferedCalls(result.getTransferedCalls() + timeStats.getTransferedCalls());
        });
        return new TimeStats(result.getName(),result.getACW()/size,result.getATT()/size,result.getASA()/size,
                result.getAHT()/size,result.getHold()/size,result.getTotalCalls(),result.getTransferedCalls(),
                result.getOccupancy()/size,result.getUtilization()/size);
    }
}
