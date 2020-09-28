package ru.comearth.russianpost.services;


import org.springframework.transaction.annotation.Transactional;
import ru.comearth.russianpost.commands.OperatorCommand;
import ru.comearth.russianpost.converters.OperatorCommandToOperator;
import ru.comearth.russianpost.converters.OperatorToOperatorCommand;
import ru.comearth.russianpost.domain.Operator;
import ru.comearth.russianpost.exceptions.NotFoundException;
import ru.comearth.russianpost.repositories.OperatorRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
    public List<Operator> getAllOperators() {
        return operatorRepository.findAll();
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
    public OperatorCommand saveOperatorCommand(OperatorCommand operatorCommand) {
        Operator detachedOperator = operatorCommandToOperator.convert(operatorCommand);
        Operator savedOperator = operatorRepository.save(detachedOperator);
        return operatorToOperatorCommand.convert(savedOperator);
    }

    @Override
    public void deleteById(Long idToDelete) {
        operatorRepository.deleteById(idToDelete);
    }

    @Transactional
    @Override
    public void fireById(Long id) {
        Operator op = findById(id);
        op.setFired(true);
        op.setRetirementDate(LocalDate.now());
        operatorRepository.save(op);
    }
}
