package ru.comearth.russianpost.services;

import javassist.NotFoundException;
import ru.comearth.russianpost.commands.OperatorCommand;
import ru.comearth.russianpost.domain.Operator;

import java.util.List;

public interface OperatorService {
    List<Operator> getAllOperators();
    Operator findById(Long l);
    OperatorCommand findCommandById(Long l);
    OperatorCommand saveOperatorCommand(OperatorCommand operatorCommand);
    void deleteById(Long idToDelete);
    void fireById(Long id);
}
