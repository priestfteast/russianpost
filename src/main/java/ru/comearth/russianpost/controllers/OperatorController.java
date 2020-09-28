package ru.comearth.russianpost.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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



    @RequestMapping({"/operators/showall","/operators/showall.html"})
    public String showoperators(Model model){
        model.addAttribute("operators",operatorService.getAllOperators());
        return OPERATORS_ALL;
    }


    @GetMapping({"/operators/{id}/show"})
    public String showById(@PathVariable String id, Model model){

        model.addAttribute("operator", operatorService.findById(Long.valueOf(id)));
        return "operators/show";
    }

    @GetMapping({"/operators/new"})
    public String newOperator(Model model){
        model.addAttribute("operator", new OperatorCommand());
        return "operators/operatorform";
    }

    @GetMapping({"/operators/{id}/update"})
    public String updateOperator(@PathVariable String id, Model model){

        model.addAttribute("operator", operatorService.findCommandById(Long.valueOf(id)));
        return "operators/operatorform";
    }

    @GetMapping("operators/{id}/delete")
    public String deleteById(@PathVariable String id){
        operatorService.deleteById(Long.valueOf(id));
        return "redirect:/operators";
    }

    @GetMapping("operators/{id}/fire")
    public String fireById(@PathVariable String id){
        operatorService.fireById(Long.valueOf(id));
        return "redirect:/"+OPERATORS_ALL;
    }

    @PostMapping("operators/save")
    public String saveOrUpdate (@Valid @ModelAttribute("operator") OperatorCommand operatorCommand, BindingResult bindingResult, Model model){

        if(bindingResult.hasErrors()){
            bindingResult.getAllErrors().forEach(objectError -> {
                System.out.println(objectError.toString());
            });
            return OPERATOR_FORM_URL;
        }

        OperatorCommand savedOperatorCommand = operatorService.saveOperatorCommand(operatorCommand);
        return "redirect:/operators/"+savedOperatorCommand.getId()+"/show";
    }

    @ExceptionHandler(NotFoundException.class)
    ModelAndView handleNotFoundException(NotFoundException ex){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("exception",ex.getMessage());
        modelAndView.setViewName("404Error");
        return modelAndView;
    }
}
