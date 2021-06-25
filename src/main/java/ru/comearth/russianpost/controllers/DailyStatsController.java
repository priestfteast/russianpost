package ru.comearth.russianpost.controllers;

import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.comearth.russianpost.services.bot.Bot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Controller
public class DailyStatsController {
    private static final Logger log = Logger.getLogger(DailyStatsController.class);
    private List<String> request = new ArrayList<>();
    private String error = null;


    @GetMapping("/dailystats")
    public String showStats(Model model) {
        List<String> initialRequest = new ArrayList<>();
        initialRequest.add(LocalDate.now().toString());
        initialRequest.add("0");
        initialRequest.add("0");
        initialRequest.add("0");

        try{
            request = (request.size() == 0) ? new ArrayList(initialRequest) : request;
            model.addAttribute("request", request);
            request.forEach(System.out::println);
            return "dailystats/dailystats";
        }
        catch (Exception e){
            error=e.getMessage()+"\n";
            model.addAttribute("error",error);
            e.printStackTrace();
            error=null;
            return "dailystats/dailystats";
        }
    }

    @PostMapping("postdailystats")
    public String postStats(@RequestParam MultiValueMap<String, String> formData) {

        String date = formData.getFirst("date"); request.set(0,date);
        String operators = formData.getFirst("operators");request.set(1,operators);
        String trainees = formData.getFirst("trainees");request.set(2,trainees);
        String hours = formData.getFirst("hours");request.set(3,hours);
        log.info("На "+date.replaceAll("-",".")+":"+"   В линии: "+operators+" чел.     В обучении: "+trainees+" чел.      Часов: "+hours);
        return "redirect:/dailystats";
    }




}
