package ru.comearth.russianpost.commands;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.comearth.russianpost.domain.Shift;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class OperatorCommand {

        private Long id;

        private String name;

        @javax.validation.constraints.NotNull
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @PastOrPresent
        private LocalDate employementDate;

        private Shift shift;

    }