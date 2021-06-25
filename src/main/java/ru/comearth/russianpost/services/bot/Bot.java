package ru.comearth.russianpost.services.bot;

import guru.springframework.norris.chuck.ChuckNorrisInfoContributor;
import guru.springframework.norris.chuck.ChuckNorrisQuotes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ru.comearth.russianpost.controllers.DailyStatsController;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
@NoArgsConstructor
public class Bot extends TelegramLongPollingBot {
    private static final Logger log = Logger.getLogger(Bot.class);

    private final ChuckNorrisQuotes chuckNorrisQuotes = new ChuckNorrisQuotes();

    final int RECONNECT_PAUSE =10000;

    @Setter
    @Getter
    String userName;
    @Setter
    @Getter
    String token;

    @Override
    public void onUpdateReceived(Update update) {
        Message message = new Message();
        if(update.hasMessage()) {
            message = update.getMessage();
        }

            Long chatid=message.getChatId();


        /* sendMsg(message);*/
        SendMessage sendMessage=new SendMessage();
        try {
            sendMessage.setText(getAnswer(message.getText()))
            .setChatId(chatid);
            execute(sendMessage) ;
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("new Update recieve: "+update.getMessage().getText()+".  Answer was: "+sendMessage.getText());
    }

    @Override
    public String getBotUsername() {
        return userName;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    public void botConnect() {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(this);
            log.info("TelegramAPI started. Look for messages");
        } catch (TelegramApiRequestException e) {
            log.error("Cant Connect. Pause " + RECONNECT_PAUSE / 1000 + "sec and try again. Error: " + e.getMessage());
            try {
                Thread.sleep(RECONNECT_PAUSE);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
                return;
            }
            botConnect();
        }
    }

    public String getAnswer(String request) throws Exception {

            if(request.toLowerCase().contains("прив") || request.toLowerCase().contains("добр") ||
                request.toLowerCase().contains("здравству") || request.toLowerCase().contains("hello")
                || request.toLowerCase().contains("hi") || request.toLowerCase().contains("greet"))
            return "Бон жур, мон ами!";
            else if(request.toLowerCase().contains("bye") || request.toLowerCase().contains("пока") &&
                    !request.toLowerCase().contains("покаж")  || request.toLowerCase().contains("счастлив") ||
                request.toLowerCase().contains("досвид") || request.toLowerCase().contains("до свид")
                || request.toLowerCase().contains("всего добр"))
            return "Адьё!";
            else if(request.toLowerCase().contains("пасиб") || request.toLowerCase().contains("благодар")
                    || request.toLowerCase().contains("than"))
                return "Рад стараться, Ваше Высокоблагородие";
           else if (request.toLowerCase().contains("как") && request.toLowerCase().contains("дел")) {
                return readDailyStats();
            }
            else if(request.toLowerCase().contains("чак") || request.toLowerCase().contains("норрис") ||
                    request.toLowerCase().contains("Chuck") || request.toLowerCase().contains("Norris") ||
                    request.toLowerCase().contains("шутк") || request.toLowerCase().contains("анекдот") ||
                    request.toLowerCase().contains("шутеечк") || request.toLowerCase().contains("шуточк")
            )
                return chuckNorrisQuotes.getRandomQuote();
            else if(request.toLowerCase().contains("погода") || request.toLowerCase().contains("погоду")
            )
                return getWeather();
            else if(request.toLowerCase().contains("курс") || request.toLowerCase().contains("доллар")||
                    request.toLowerCase().contains("валют")
            )
                return getCurrencyRate();
            else
                return "Я узкоспециализированный бот. Отвечаю на вопрос 'как дела?'. " +
                        "\n Также могу рассказать шутку о Чаке Норрисе, показать погоду в Пензе иликурс доллара.";

        }

        public String readDailyStats() throws IOException {
            ReversedLinesFileReader fr = new ReversedLinesFileReader(new File("C:/java/log_file.log"));
            String ch;

            do {
                ch = fr.readLine();
               if(ch.contains("DailyStatsController"))
                   return ch.substring(ch.lastIndexOf("-")+1);
            } while (ch != null);
            fr.close();
            return "Данные не найдены:(";
        }
    public static String getCurrencyRate() throws Exception {
        String url = "https://www.cbr-xml-daily.ru/daily_json.js";

        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        Object obj2 = new JSONParser().parse(response.toString());
        JSONObject jo = (JSONObject) obj2;
        Object obj3 = new JSONParser().parse(jo.get("Valute").toString());
        JSONObject jo2 = (JSONObject) obj3;
        JSONObject jo3 =  (JSONObject)jo2.get("USD");
        String quotes = jo3.get("Value").toString();
        String resp = "на " + LocalDate.now()+ " один USD стоит "+quotes + " rub";

        return resp;
    }
    public static String getWeather() throws Exception {
        String url = "http://api.openweathermap.org/data/2.5/weather?q=Penza&units=metric&appid=a6c03a410806a83f7f40201a8c8e251e";

        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        Object obj2 = new JSONParser().parse(response.toString());
        JSONObject jo = (JSONObject) obj2;
        Object obj3 = new JSONParser().parse(jo.get("main").toString());
        JSONObject jo2 = (JSONObject) obj3;
        String quotes = jo2.get("temp").toString();
        String resp = LocalDate.now()+ " температура в Пензе "+quotes + "С";

        return resp;
    }

    }
