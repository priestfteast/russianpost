package ru.comearth.russianpost.services;

import org.springframework.stereotype.Service;
import ru.comearth.russianpost.domain.CSAT;
import ru.comearth.russianpost.domain.Operator;
import ru.comearth.russianpost.repositories.CSATRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class CSATServiceImpl implements CSATService {

    private final CSATRepository csatRepository;

    public CSATServiceImpl(CSATRepository csatRepository) {
        this.csatRepository = csatRepository;
    }

    @Override
    public CSAT getOverallCSAT(LocalDate start, LocalDate end) {
        List<CSAT> stats = csatRepository.findAllByDateBetween(start, end);
        return countAverageCSAT(stats);
    }

    @Override
    public CSAT getOverallOperatorCSAT(Operator operator, LocalDate start, LocalDate end) {
        List<CSAT> stats = csatRepository.findAllByOperatorAndDateBetween(operator,start, end);
        return countAverageCSAT(stats);
    }

    @Override
    public CSAT countAverageCSAT(List<CSAT> stats) {

        CSAT result = new CSAT(stats.get(0).getDate() + " по " + stats.get(stats.size() - 1).getDate(),
                0.0,  0.0, 0.0, 0.0, 0.0, stats.get(0).getOperator());
        double size = (double) stats.size();
        stats.forEach(csat -> {
            result.setOverallScore(result.getOverallScore()+csat.getOverallScore());
            result.setInterestScore(result.getInterestScore()+csat.getInterestScore());
            result.setProfScore(result.getProfScore()+csat.getProfScore());
            result.setGoodWillScore(result.getGoodWillScore()+csat.getGoodWillScore());
            result.setAverageScore(result.getAverageScore()+csat.getAverageScore());
        });
        return new CSAT(result.getName(),result.getOverallScore()/size,result.getInterestScore()/size,
                result.getProfScore()/size, result.getGoodWillScore()/size,result.getAverageScore()/size,
                result.getOperator());
    }

    @Override
    public String getOperatorCSAT(Operator operator, LocalDate start, LocalDate end) {
        List<CSAT> stats = csatRepository.findAllByOperatorAndDateBetween(operator,start, end);

        double fourAndFive = stats.stream().filter(csat1 -> csat1.getAverageScore()>=4.0).count();
        double csat = fourAndFive*100/stats.size();

        return csat+"%";
    }

    @Override
    public String getAverageCSAT(LocalDate start, LocalDate end) {
        List<CSAT> stats = csatRepository.findAllByDateBetween(start, end);
        double fourAndFive = stats.stream().filter(csat1 -> csat1.getAverageScore()>=4.0).count();
        double csat = fourAndFive*100/stats.size();

        return csat+"%";
    }

    @Override
    public String getOperatorDCSAT(Operator operator, LocalDate start, LocalDate end) {
        List<CSAT> stats = csatRepository.findAllByOperatorAndDateBetween(operator,start, end);
        double ones = stats.stream().filter(csat1 -> csat1.getAverageScore()<2.0).count();
        double dcsat = ones*100/stats.size();

        return dcsat+"%";
    }

    @Override
    public String getAverageDCSAT(LocalDate start, LocalDate end) {
        List<CSAT> stats = csatRepository.findAllByDateBetween(start, end);
        double ones = stats.stream().filter(csat1 -> csat1.getAverageScore()<2.0).count();
        double dcsat = ones*100/stats.size();

        return dcsat+"%";
    }
}
