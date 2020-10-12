package ru.comearth.russianpost.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.comearth.russianpost.domain.Operator;
import ru.comearth.russianpost.domain.TimeStats;
import ru.comearth.russianpost.services.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
public class StatsController {

    private final OperatorService operatorService;
    private final ChartService chartService;
    private final CSATService csatService;
    private final TimeStatsService timeStatsService;
    private final RatingService ratingService;

    public StatsController(OperatorService operatorService, ChartService chartService, CSATService csatService, TimeStatsService timeStatsService, RatingService ratingService) {
        this.operatorService = operatorService;
        this.chartService = chartService;
        this.csatService = csatService;
        this.timeStatsService = timeStatsService;
        this.ratingService = ratingService;
    }

    private List<String> request = new ArrayList<>();
    private String error = null;

    @GetMapping("/statistics")
    public String showStats(Model model) {
        List<String> initialRequest = new ArrayList<>();
        initialRequest.add(LocalDate.now().toString().substring(0, 8) + "01");
        initialRequest.add(LocalDate.now().toString());
        initialRequest.add("[all]");
        initialRequest.add("CSAT");
        initialRequest.add("null");

        try{
            request = (request.size() == 0) ? new ArrayList(initialRequest) : request;
            List<LocalDate> days = chartService.getChartDays(LocalDate.parse(request.get(0)),
                    LocalDate.parse(request.get(1)), request.get(2), request.get(3));
            model.addAttribute("days", days);

            ArrayList<Operator> operators = (ArrayList<Operator>) operatorService.getAllOperators(request.get(2));
            Collections.sort(operators, Comparator.comparing(Operator::getFullName));

            model.addAttribute("operators", operators);

            if(request.get(3).equals("CSAT")){
                List<String> csatStrings = (days.size()>0)? chartService.getCSATasString(LocalDate.parse(request.get(0)),
                        LocalDate.parse(request.get(1)), request.get(2), days) : new ArrayList<>();
                model.addAttribute("csatstrings",csatStrings);
            }
            else
            {
                List<TimeStats> timeStats = (days.size()>0)? chartService.getTimeStatsData(request.get(2),days) : new ArrayList<>();
                timeStats = chartService.uniteDaysAndStats(days,timeStats,request.get(2));
                model.addAttribute("timestats",timeStats);
                model.addAttribute("overallstats",timeStatsService.countAverageStats(timeStats));
            }

            model.addAttribute("request", request);

            return "statistics/statistics";
        }
        catch (Exception e){
            error=e.getMessage()+"\n";
            model.addAttribute("error",error);
            e.printStackTrace();
            error=null;
            return "statistics/statistics";
        }
    }

    @PostMapping("poststats")
    public String viewRatings(@RequestParam MultiValueMap<String, String> formData) {


        String startdate = formData.getFirst("startdate"); request.set(0,startdate);
        String enddate = formData.getFirst("enddate");request.set(1,enddate);
        String operator = formData.getFirst("operator");request.set(2,operator);
        String criterion = formData.getFirst("criterion");request.set(3,criterion);
        request.set(4,"not null");

        return "redirect:/statistics";
    }

}
