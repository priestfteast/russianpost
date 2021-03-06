package ru.comearth.russianpost.bootstrap;


import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.comearth.russianpost.domain.FireCause;
import ru.comearth.russianpost.domain.Operator;
import ru.comearth.russianpost.domain.Shift;
import ru.comearth.russianpost.repositories.OperatorRepository;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;



@Component
public class OperatorBootStrap implements ApplicationListener<ContextRefreshedEvent> {

    private final OperatorRepository operatorRepository;


    public OperatorBootStrap(OperatorRepository operatorRepository) {
        this.operatorRepository = operatorRepository;

    }

    private List<Operator> getOperators(){
        List<Operator> operators = new ArrayList<>(2);

        if(operatorRepository.findAll().iterator().hasNext())
            return operators;


//        Ivanov Ivan
        Operator ivanov = new Operator();
        ivanov.setFullName("Иванов Иван Иванович");
        ivanov.setEmployementDate(LocalDate.of(2020, Month.SEPTEMBER,14));
        ivanov.setShift(Shift.NINE_AM);


        //        Pereyaslova Maria
        Operator maria = new Operator();
        maria.setFullName("Переяслова Мария Игоревна");

        maria.setEmployementDate(LocalDate.of(2020, Month.SEPTEMBER,21));
        maria.setShift(Shift.ELEVEN_AM);

        //        Pereyaslova Maria
        Operator Imambek = new Operator();
        Imambek.setFullName("Нуршугалиев имамбек Алиевич");

        Imambek.setEmployementDate(LocalDate.of(2020, Month.SEPTEMBER,14));
        Imambek.setShift(Shift.TWELVE_AM);
        Imambek.setFireCause(FireCause.LOW_SALARY);
        Imambek.setFired(true);
        Imambek.setRetirementDate(LocalDate.of(2020, Month.SEPTEMBER,29));



        //add to return list
        operators.add(ivanov);
        operators.add(maria);
        operators.add(Imambek);

        return operators;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        operatorRepository.saveAll(getOperators());
    }
}
