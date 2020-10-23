package ru.comearth.russianpost;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;
import ru.comearth.russianpost.services.bot.Bot;

@SpringBootApplication
public class RussianpostApplication {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(RussianpostApplication.class, args);
        Bot test_habr_bot = new Bot("Co", "c");
        test_habr_bot.botConnect();
    }

}
