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
import ru.comearth.russianpost.services.CSATService;
import ru.comearth.russianpost.services.ChartService;
import ru.comearth.russianpost.services.OperatorService;
import ru.comearth.russianpost.services.TimeStatsService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
public class ChartsController {

    private final OperatorService operatorService;
    private final ChartService chartService;
    private final CSATService csatService;
    private final TimeStatsService timeStatsService;

    public ChartsController(OperatorService operatorService, ChartService chartService, CSATService csatService, TimeStatsService timeStatsService) {
        this.operatorService = operatorService;
        this.chartService = chartService;
        this.csatService = csatService;
        this.timeStatsService = timeStatsService;
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

            ArrayList<Operator> operators = (ArrayList<Operator>) operatorService.getAllOperators(request.get(2));
            Collections.sort(operators, Comparator.comparing(Operator::getFullName));

            model.addAttribute("operators", operators);
            model.addAttribute("request", request);

            List<LocalDate> days = chartService.getChartDays(LocalDate.parse(request.get(0)),
                    LocalDate.parse(request.get(1)), request.get(2), request.get(3));

            if(request.get(3).equals("CSAT")){
                List<String> csatStrings = (days.size()>0)? chartService.getCSATasString(LocalDate.parse(request.get(0)),
                        LocalDate.parse(request.get(1)), request.get(2), days) : new ArrayList<>();
                model.addAttribute("csatstrings",csatStrings);
                List<Double> dcsats = (days.size()>0)? chartService.getDCSATasDouble(LocalDate.parse(request.get(0)),
                        LocalDate.parse(request.get(1)), request.get(2), days) : new ArrayList<>();
                List<Double> csats = (days.size()>0)? chartService.getCSATasDouble(LocalDate.parse(request.get(0)),
                        LocalDate.parse(request.get(1)), request.get(2), days) : new ArrayList<>();
                model.addAttribute("csats",csats);
                model.addAttribute("dcsats",dcsats);
               if(request.get(2).equals("[all]")) {
                   model.addAttribute("averagecsat", csatService.getOverallCSAT(LocalDate.parse(request.get(0)), LocalDate.parse(request.get(1))));
                   model.addAttribute("averagedcsat", csatService.getOverallDCSAT(LocalDate.parse(request.get(0)), LocalDate.parse(request.get(1))));
               }else {
                   model.addAttribute("averagecsat", csatService.getOperatorCSAT(operators.get(0),
                           LocalDate.parse(request.get(0)), LocalDate.parse(request.get(1))));
                   model.addAttribute("averagedcsat", csatService.getOperatorDCSAT(operators.get(0),
                           LocalDate.parse(request.get(0)), LocalDate.parse(request.get(1))));
               }
            }
            else
            {
                List<Integer> timeStats = (days.size()>0)? chartService.getAHTData(LocalDate.parse(request.get(0)),
                        LocalDate.parse(request.get(1)), request.get(2), days) : new ArrayList<>();

                List<Integer> target = Collections.nCopies(days.size(), 180);
                List<Integer> hold = (days.size()>0)? chartService.getHoldData(LocalDate.parse(request.get(0)),
                        LocalDate.parse(request.get(1)), request.get(2), days) : new ArrayList<>();
                Double averageAht = chartService.countAverageTimeStats(LocalDate.parse(request.get(0)),LocalDate.parse(request.get(1))).getAHT();
                Double averageHold = chartService.countAverageTimeStats(LocalDate.parse(request.get(0)),LocalDate.parse(request.get(1))).getHold();

                model.addAttribute("averageaht",averageAht.intValue()+" сек.");
                model.addAttribute("averagehold",averageHold.intValue()+" сек.");
                model.addAttribute("timestats",timeStats);
                model.addAttribute("hold",hold);
                model.addAttribute("target",target);

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

}
