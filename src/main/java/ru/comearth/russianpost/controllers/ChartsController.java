package ru.comearth.russianpost.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.comearth.russianpost.domain.CSAT;
import ru.comearth.russianpost.domain.Operator;
import ru.comearth.russianpost.domain.TimeStats;
import ru.comearth.russianpost.services.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Controller
public class ChartsController {

    private final OperatorService operatorService;
    private final ChartService chartService;
    private final CSATService csatService;
    private final TimeStatsService timeStatsService;
    private final TimeService timeService;

    public ChartsController(OperatorService operatorService, ChartService chartService, CSATService csatService, TimeStatsService timeStatsService, TimeService timeService) {
        this.operatorService = operatorService;
        this.chartService = chartService;
        this.csatService = csatService;
        this.timeStatsService = timeStatsService;
        this.timeService = timeService;
    }

    private List<CSAT> csats = new ArrayList<>();
    private List<TimeStats> timeStats = new ArrayList<>();
    private List<String> request = new ArrayList<>();
    private String error = null;

    @GetMapping("/charts")
    public String showStats(Model model) {
        List<String> initialRequest = new ArrayList<>();
        initialRequest.add(LocalDate.now().toString().substring(0, 8) + "01");
        initialRequest.add(LocalDate.now().toString());
        initialRequest.add("[all]");
        initialRequest.add("CSAT");
        initialRequest.add("null");

        try{
        request = (request.size() == 0) ? new ArrayList(initialRequest) : request;

            List<Operator> operators = operatorService.getAllOperators("[actual]");
            Collections.sort(operators, Comparator.comparing(Operator::getFullName));

            model.addAttribute("operators", operators);
            model.addAttribute("request", request);

            List<LocalDate> days = chartService.getChartDays(LocalDate.parse(request.get(0)),
                    LocalDate.parse(request.get(1)), request.get(2), request.get(3));
            List<LocalDate> allDays = Stream.iterate(LocalDate.parse(request.get(0)), date -> date.plusDays(1))
                    .limit(ChronoUnit.DAYS.between(LocalDate.parse(request.get(0)), LocalDate.parse(request.get(1)).plusDays(1))).collect(Collectors.toList());

            List<LocalDate> weeks = timeService.getWeeksAsDates(allDays);
            model.addAttribute("weeks", timeService.getWeeksAsStrings(weeks));

            List<Double> ahtByWeeks = timeService.getAhtByWeeks(weeks,request.get(2));
            List<String> dcsatByWeeks = timeService.getDcsatByWeeks(weeks,request.get(2));
            List<String> csatByWeeks = timeService.getCsatByWeeks(weeks,request.get(2));
            model.addAttribute("ahtbyweeks", ahtByWeeks);
            model.addAttribute("dcsatbyweeks",dcsatByWeeks);
            model.addAttribute("csatbyweeks",csatByWeeks);



            if(!request.get(2).equals("[all]")) {
                Operator op =operatorService.findByName(request.get(2));
                int weeks2 = (int) ChronoUnit.WEEKS.between(op.getEmployementDate(),LocalDate.parse(request.get(1)));
                String operInfo = String.format("Работает с %s (месяцев:%d недель:%d всего смен:%d)", op.getEmployementDate().toString(),
                        weeks2/4, weeks2%4, chartService.countAllShifts(request.get(3), request.get(2)));
                model.addAttribute("operinfo",operInfo);
            }



            if(request.get(3).equals("CSAT")){

                List<Double> dcsats = (days.size()>0)? chartService.getDCSATasDouble(request.get(2), days) : new ArrayList<>();
                List<Double> csats = (days.size()>0)? chartService.getCSATasDouble(request.get(2), days) : new ArrayList<>();
                model.addAttribute("csats",csats);
                model.addAttribute("dcsats",dcsats);
               if(request.get(2).equals("[all]")) {
                   model.addAttribute("averagecsat", csatService.getOverallCSAT(LocalDate.parse(request.get(0)), LocalDate.parse(request.get(1))));
                   model.addAttribute("averagedcsat", csatService.getOverallDCSAT(LocalDate.parse(request.get(0)), LocalDate.parse(request.get(1))));
               }else {
                   model.addAttribute("averagecsat", csatService.getOperatorCSAT(operatorService.findByName(request.get(2)),
                           LocalDate.parse(request.get(0)), LocalDate.parse(request.get(1))));
                   model.addAttribute("averagedcsat", csatService.getOperatorDCSAT(operatorService.findByName(request.get(2)),
                           LocalDate.parse(request.get(0)), LocalDate.parse(request.get(1))));
               }
            }
            else
            {
                List<Integer> timeStats = (days.size()>0)? chartService.getAHTData(request.get(2), days) : new ArrayList<>();

                List<Integer> target = Collections.nCopies(days.size(), 180);
                List<Integer> hold = (days.size()>0)? chartService.getHoldData(request.get(2), days) : new ArrayList<>();
                Double averageAht = chartService.countAverageTimeStats(LocalDate.parse(request.get(0)),LocalDate.parse(request.get(1)),request.get(2)).getAHT();
                Double averageHold = chartService.countAverageTimeStats(LocalDate.parse(request.get(0)),LocalDate.parse(request.get(1)),request.get(2)).getHold();
                List<Double> ahtByExperience = chartService.getAhtByExperience(request.get(2),
                        LocalDate.parse(request.get(0)), LocalDate.parse(request.get(1)));

                model.addAttribute("averageaht",averageAht.intValue()+" сек.");
                model.addAttribute("averagehold",averageHold.intValue()+" сек.");
                model.addAttribute("timestats",timeStats);
                model.addAttribute("hold",hold);
                model.addAttribute("target",target);
                model.addAttribute("ahtbyexperience",ahtByExperience);

            }




            model.addAttribute("days", days);
            model.addAttribute("opers",chartService.countDistinctByNameAndDateBetween(request.get(2),  LocalDate.parse(request.get(0)),
                    LocalDate.parse(request.get(1)), request.get(3)));


            request = new ArrayList<>(initialRequest);

            return "charts/charts";
        }
        catch (Exception e){
            error=e.getMessage()+"\n";
            model.addAttribute("error",error);
            e.printStackTrace();
            error=null;
            return "charts/charts";
        }
    }

    @PostMapping("postcharts")
    public String viewRatings(@RequestParam MultiValueMap<String, String> formData) throws Exception {

        csats = new ArrayList<>();
        timeStats = new ArrayList<>();

        String startdate = formData.getFirst("startdate"); request.set(0,startdate);
        String enddate = formData.getFirst("enddate");request.set(1,enddate);
        String operator = formData.getFirst("operator");request.set(2,operator);
        String criterion = formData.getFirst("criterion");request.set(3,criterion);
        request.set(4,"not null");

        return "redirect:/charts";
    }


    public static void main(String[] args) {

        List<LocalDate> dates = Stream.iterate(LocalDate.parse("2020-11-01"), date -> date.plusDays(1))
                .limit(ChronoUnit.DAYS.between(LocalDate.parse("2020-11-01"), LocalDate.now().plusDays(1))).collect(Collectors.toList());

       dates.forEach(System.out::println);
    }

}
