package ru.comearth.russianpost.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.comearth.russianpost.domain.CSAT;
import ru.comearth.russianpost.domain.TimeStats;
import ru.comearth.russianpost.services.CSATService;
import ru.comearth.russianpost.services.OperatorService;
import ru.comearth.russianpost.services.TimeStatsService;

import java.time.LocalDate;

@Controller
public class IndexController {

    private final OperatorService operatorService;
    private final TimeStatsService timeStatsService;
    private final CSATService csatService;

    public IndexController(OperatorService operatorService, TimeStatsService timeStatsService, CSATService csatService) {
        this.operatorService = operatorService;
        this.timeStatsService = timeStatsService;
        this.csatService = csatService;
    }


    @GetMapping({"/","/index","/index.html"})
    public String showDailyStats(Model model) {

        try {
            int allOperators = operatorService.getAllOperators("[all]").size();
            int actualOperators = operatorService.getAllOperators("[actual]").size();
            int firedOperators = operatorService.getAllOperators("[fired]").size();

            LocalDate endDate = LocalDate.now();
            LocalDate startDate = LocalDate.parse(endDate.toString().substring(0, endDate.toString().length() - 2) + "01");
            TimeStats stats = timeStatsService.getOverallStats(startDate,endDate);


            String csat = csatService.getOverallCSAT(startDate,endDate).split(" ")[0];
            String dcsat = csatService.getOverallDCSAT(startDate,endDate).split(" ")[0];
            int csatCount = csatService.countCSATs(startDate,endDate);

            model.addAttribute("date", LocalDate.now());
            model.addAttribute("actualoperators", actualOperators);
            model.addAttribute("alloperators", allOperators);
            model.addAttribute("firedoperators", firedOperators);
            model.addAttribute("stats", stats);
            model.addAttribute("csat", csat);
            model.addAttribute("dcsat", dcsat);
            model.addAttribute("csatcount", csatCount);


            return "index/index";
        }
        catch (Exception e){
            String error=e.getMessage()+"\n";
            e.printStackTrace();
            model.addAttribute("error",error);
            return "/index/index";
        }
    }



}
