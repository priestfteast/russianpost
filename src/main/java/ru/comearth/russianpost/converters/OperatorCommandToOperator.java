package ru.comearth.russianpost.converters;

import lombok.Synchronized;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import ru.comearth.russianpost.commands.OperatorCommand;
import ru.comearth.russianpost.domain.Operator;

@Component
public class OperatorCommandToOperator implements Converter<OperatorCommand, Operator> {

    @Nullable
    @Synchronized
    @Override
    public Operator convert(OperatorCommand operatorCommand) {
        if(operatorCommand==null) {
            return null;
        }

        Operator operator = new Operator();
        operator.setId(operatorCommand.getId());
        operator.setName(operatorCommand.getName());
        operator.setEmployementDate(operatorCommand.getEmployementDate());
        operator.setShift(operatorCommand.getShift());
        operator.setFired(false);

        return operator;
    }
}
