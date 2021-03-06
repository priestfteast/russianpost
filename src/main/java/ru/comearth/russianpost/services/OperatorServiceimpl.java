package ru.comearth.russianpost.services;


import org.springframework.stereotype.Service;
import ru.comearth.russianpost.commands.OperatorCommand;
import ru.comearth.russianpost.converters.OperatorCommandToOperator;
import ru.comearth.russianpost.converters.OperatorToOperatorCommand;
import ru.comearth.russianpost.domain.Operator;
import ru.comearth.russianpost.exceptions.NotFoundException;
import ru.comearth.russianpost.repositories.OperatorRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OperatorServiceimpl implements OperatorService {

    private final OperatorToOperatorCommand operatorToOperatorCommand;
    private final OperatorCommandToOperator operatorCommandToOperator;


    private final OperatorRepository operatorRepository;

    public OperatorServiceimpl(OperatorToOperatorCommand operatorToOperatorCommand, OperatorCommandToOperator operatorCommandToOperator, OperatorRepository operatorRepository) {
        this.operatorToOperatorCommand = operatorToOperatorCommand;
        this.operatorCommandToOperator = operatorCommandToOperator;
        this.operatorRepository = operatorRepository;
    }

    @Override
    public List<Operator> getAllOperators(String request) {

        switch (request){
            case "[all]": return operatorRepository.findAll();
            case "[actual]": return operatorRepository.findAll().stream().filter(operator -> !operator.isFired()).collect(Collectors.toList());
            case "[fired]" : return operatorRepository.findAll().stream().filter(operator -> operator.isFired()).collect(Collectors.toList());
            default: return operatorRepository.findAll();
        }
    }

    @Override
    public Integer[] getOperatorsByMonth() {
        Integer[] operators = new Integer[]{0,0,0};
        List<Operator> allOperators = operatorRepository.findAll();
        operators[0] = Math.toIntExact(allOperators.stream().filter(operator -> !operator.isFired()).count());
        operators[1] = Math.toIntExact(allOperators.stream().filter(operator ->
                operator.getEmployementDate().getMonth().equals(LocalDate.now().getMonth())).count());
        operators[2] = Math.toIntExact(allOperators.stream().filter(operator ->
                operator.getRetirementDate()!=null && operator.getRetirementDate().getMonth().equals(LocalDate.now().getMonth())).count());
        return operators;
    }

    @Override
    public Operator findById(Long l)  {
        Optional<Operator> operatorOptional = operatorRepository.findById(l);
        if(operatorOptional.isEmpty())
            throw new NotFoundException("There is no operator with ID: "+l.toString());

        return operatorOptional.get();
    }

    @Override
    public OperatorCommand findCommandById(Long l) {
        return operatorToOperatorCommand.convert(findById(l));
    }

    @Override
    public Operator findByName(String name) {
        return operatorRepository.findByFullName(name);
    }

    @Override
    public OperatorCommand saveOperatorCommand(OperatorCommand operatorCommand) throws Exception {
        Operator detachedOperator = operatorCommandToOperator.convert(operatorCommand);
        if(operatorRepository.findByFullName(detachedOperator.getFullName())!=null) {
            if (operatorRepository.findByFullName(detachedOperator.getFullName()).getEmployementDate().equals(detachedOperator.getEmployementDate()) &&
                    operatorRepository.findByFullName(detachedOperator.getFullName()).getShift().equals(detachedOperator.getShift()))
                throw new Exception("Идентичный оператор уже сохранен в базе, " + operatorCommand.getFullName() + "." +
                        " Измените Имя, смену или дату трудоустройства.");
        }
        Operator savedOperator = operatorRepository.save(detachedOperator);
        return operatorToOperatorCommand.convert(savedOperator);
    }

    @Override
    public void deleteById(Long idToDelete) {
        operatorRepository.deleteById(idToDelete);
    }


}
