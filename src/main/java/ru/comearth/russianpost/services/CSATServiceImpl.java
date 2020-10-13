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
    public CSAT getAverageCSAT(LocalDate start, LocalDate end) {
        List<CSAT> stats = csatRepository.findAllByDateBetween(start, end);
        return countAverageCSAT(stats);
    }

    @Override
    public CSAT getAverageOperatorCSAT(Operator operator, LocalDate start, LocalDate end) {
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

        double fourAndFive = stats.stream().filter(csat1 -> csat1.getOverallScore()>=4.0).count();
        double totalCalls = stats.stream().filter(csat1 -> csat1.getOverallScore()>0.0).count();
        double csat = fourAndFive*100/totalCalls;

        return cutDouble(csat)+"% ("+fourAndFive+" из "+totalCalls+")";
    }

    @Override
    public String getOverallCSAT(LocalDate start, LocalDate end) {
        List<CSAT> stats = csatRepository.findAllByDateBetween(start, end);
        double fourAndFive = stats.stream().filter(csat1 -> csat1.getOverallScore()>=4.0).count();
        double totalCalls = stats.stream().filter(csat1 -> csat1.getOverallScore()>0.0).count();
        double csat = fourAndFive*100/totalCalls;

        return cutDouble(csat)+"% ("+fourAndFive+" из "+totalCalls+")";
    }

    @Override
    public String getOperatorDCSAT(Operator operator, LocalDate start, LocalDate end) {
        List<CSAT> stats = csatRepository.findAllByOperatorAndDateBetween(operator,start, end);
        double ones = stats.stream().filter(csat1 -> csat1.getOverallScore()==1.0).count();
        double totalCalls = stats.stream().filter(csat1 -> csat1.getOverallScore()>0.0).count();
        double dcsat = ones*100/totalCalls;

        return cutDouble(dcsat)+"% ("+ones+" из "+totalCalls+")";
    }

    @Override
    public String getOverallDCSAT(LocalDate start, LocalDate end) {
        List<CSAT> stats = csatRepository.findAllByDateBetween(start, end);
        double ones = stats.stream().filter(csat1 -> csat1.getOverallScore()==1.0).count();
        double totalCalls = stats.stream().filter(csat1 -> csat1.getOverallScore()>0.0).count();
        double dcsat = ones*100/totalCalls;

        return cutDouble(dcsat)+"% ("+ones+" из "+totalCalls+")";
    }

    @Override
    public void saveAll(List<CSAT> stats) {
        csatRepository.saveAll(stats);
        System.out.println("Saved "+stats.size()+" CSAT records");
    }

    @Override
    public void removeAllByDate(LocalDate date) throws Exception {
        if(csatRepository.getFirstByDate(date)==null) {
            throw new Exception("В базе нет данных " +
                    "по статистике за " + date +".\n"+
                    " Выберите другую дату" +".\n");
        }
       csatRepository.deleteAllByDate(date);
        System.out.println("Deleted statistics for "+date);
    }

    public String cutDouble (Double number){
        return String.format("%.3f",number).replaceFirst(",",".");
    }

}
