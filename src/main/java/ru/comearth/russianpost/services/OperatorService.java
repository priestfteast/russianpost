package ru.comearth.russianpost.services;

import ru.comearth.russianpost.commands.OperatorCommand;
import ru.comearth.russianpost.domain.Operator;

import java.util.List;

public interface OperatorService {
    List<Operator> getAllOperators(String request);
    Operator findById(Long l);
    OperatorCommand findCommandById(Long l);
    OperatorCommand saveOperatorCommand(OperatorCommand operatorCommand) throws Exception;
    void deleteById(Long idToDelete);
}
