package ru.comearth.russianpost.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.comearth.russianpost.domain.Operator;
import ru.comearth.russianpost.services.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

@Controller
public class RatingsController {

    private final OperatorService operatorService;
    private final ChartService chartService;
    private final CSATService csatService;
    private final TimeStatsService timeStatsService;
    private final RatingService ratingService;

    public RatingsController(OperatorService operatorService, ChartService chartService, CSATService csatService, TimeStatsService timeStatsService, RatingService ratingService) {
        this.operatorService = operatorService;
        this.chartService = chartService;
        this.csatService = csatService;
        this.timeStatsService = timeStatsService;
        this.ratingService = ratingService;
    }


    private List<String> request = new ArrayList<>();
    private String error = null;

    @GetMapping("/ratings")
    public String showStats(Model model) {
        List<String> initialRequest = new ArrayList<>();
        initialRequest.add(LocalDate.now().toString().substring(0, 8) + "01");
        initialRequest.add(LocalDate.now().toString());
        initialRequest.add("AHT");
        initialRequest.add("all");
        initialRequest.add("null");

        try{
            request = (request.size() == 0) ? new ArrayList(initialRequest) : request;
            HashSet<Operator> operators = ratingService.getOperators(LocalDate.parse(request.get(0)),
                    LocalDate.parse(request.get(1)),request.get(2),request.get(3));
            List<Double> data = ratingService.getRating(operators,LocalDate.parse(request.get(0)),
                    LocalDate.parse(request.get(1)),request.get(2));
            TreeMap<String, Double> dataMap = ratingService.getSortedRating(operators, data);

            model.addAttribute("request", request);
            model.addAttribute("operators", dataMap.keySet());
            model.addAttribute("data", dataMap.values());

            return "ratings/ratings";
        }
        catch (Exception e){
            error=e.getMessage()+"\n";
            model.addAttribute("error",error);
            e.printStackTrace();
            error=null;
            return "ratings/ratings";
        }
    }

    @PostMapping("postratings")
    public String viewRatings(@RequestParam MultiValueMap<String, String> formData) throws Exception {


        String startdate = formData.getFirst("startdate"); request.set(0,startdate);
        String enddate = formData.getFirst("enddate");request.set(1,enddate);
        String operator = formData.getFirst("criterion");request.set(2,operator);
        String criterion = formData.getFirst("exp");request.set(3,criterion);
        request.set(4,"not null");

        return "redirect:/ratings";
    }

}
