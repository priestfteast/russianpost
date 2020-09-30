package ru.comearth.russianpost.commands;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.lang.Nullable;
import ru.comearth.russianpost.domain.FireCause;
import ru.comearth.russianpost.domain.Shift;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class OperatorCommand {

        private Long id;

        @NotEmpty
        @Size(min = 2, max = 30)
        private String fullName;

        @javax.validation.constraints.NotNull
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @PastOrPresent
        private LocalDate employementDate;

        private boolean fired;

        @javax.validation.constraints.NotNull
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        @PastOrPresent
        private LocalDate retirementDate;

        private Shift shift;

        private FireCause fireCause;

    }