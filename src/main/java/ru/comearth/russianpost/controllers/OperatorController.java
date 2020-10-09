package ru.comearth.russianpost.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.comearth.russianpost.commands.OperatorCommand;
import ru.comearth.russianpost.exceptions.NotFoundException;
import ru.comearth.russianpost.services.OperatorService;

import javax.validation.Valid;

@Controller

public class OperatorController {
    private final String OPERATOR_FORM_URL = "operators/operatorform";
    private final String OPERATORS_ALL = "operators/showall";
    private final OperatorService operatorService;

    public OperatorController(OperatorService operatorService) {
        this.operatorService = operatorService;
    }

    private String request ="[actual]";
    private String error=null;

    @RequestMapping({"/operators/showall","/operators/showall.html"})
    public String showoperators(Model model){
        model.addAttribute("operators",operatorService.getAllOperators(request));
        model.addAttribute("request",request);
        return OPERATORS_ALL;
    }

    @PostMapping("/filter")
    public String filter (@RequestBody MultiValueMap<String, String> formData){
        request =formData.values().toArray()[0].toString();
        System.out.println(formData.values().toArray()[0]);

        return "redirect:/"+OPERATORS_ALL;
    }


    @GetMapping({"/operators/new"})
    public String newOperator(Model model){
        model.addAttribute("operator", new OperatorCommand());
        model.addAttribute("error", error);
        error=null;
        return "operators/operatorform";
    }

    @GetMapping({"/operators/{id}/update"})
    public String updateOperator(@PathVariable String id, Model model){

        model.addAttribute("operator", operatorService.findCommandById(Long.valueOf(id)));
        model.addAttribute("error", error);
        error=null;
        return "operators/operatorform";
    }

    @GetMapping("operators/{id}/delete")
    public String deleteById(@PathVariable String id){
        operatorService.deleteById(Long.valueOf(id));
        return "redirect:/operators";
    }

    @GetMapping("/operators/{id}/fireform")
    public String fireForm(@PathVariable String id, Model model){
        model.addAttribute("operator",operatorService.findCommandById(Long.valueOf(id)));
        return "operators/fireform";
    }

    @PostMapping("operators/fire")
    public String fireById(@Valid @ModelAttribute("operator") OperatorCommand operatorCommand, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            bindingResult.getAllErrors().forEach(objectError -> {
                System.out.println(objectError.toString());
            });
            model.addAttribute("operator",operatorCommand);
            return "/operators/fireform";
        }

        operatorCommand.setFired(true);
        try {
            operatorService.saveOperatorCommand(operatorCommand);
        } catch (Exception e) {
            error=e.getMessage();
            e.printStackTrace();
        }
        return "redirect:/operators/showall";
    }

    @PostMapping("operators/save")
    public String saveOrUpdate (@Valid @ModelAttribute("operator") OperatorCommand operatorCommand, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            bindingResult.getAllErrors().forEach(objectError -> {
                System.out.println(objectError.toString());
            });
             return OPERATOR_FORM_URL;
        }

        try {
            operatorService.saveOperatorCommand(operatorCommand);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            error=e.getMessage();
            e.printStackTrace();
            return "redirect:/operators/new";
        }
        return "redirect:/operators/showall";
    }

    @ExceptionHandler(NotFoundException.class)
    ModelAndView handleNotFoundException(NotFoundException ex){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("exception",ex.getMessage());
        modelAndView.setViewName("404Error");
        return modelAndView;
    }
}
