package com.example.demo.service;

import com.example.demo.configurations.BotConfig;
import com.example.demo.repository.WordRepository;
import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Slf4j
@Service
public class TestBotService extends TelegramLongPollingBot {

    @Autowired
    private WordRepository repository;

    @Autowired
    private WordEquationService service;

    final BotConfig config;

    public TestBotService(BotConfig config) {
        this.config = config;
        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "get a welcome message"));
        listofCommands.add(new BotCommand("/help", "info how to use this bot"));
        try {
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()){
            String messageTest = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (messageTest) {
                case "/start":
                    startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/help":
                    sendMessage(chatId, "Этот телеграм бот является учебным проектом \n " +
                            "Используйте клавиатуру для навигации");
                case "Practice":
                    String k = service.TurkWordGiving();
                    sendMessage(chatId, "Режим практики.\n" +
                            "Сейчас тебе будут попадаться слова на турецком, тебе нужно будет отвечать на русском");
                    sendMessage(chatId,k);
                    sendMessage(chatId,"Напишите перевод");
                    String answer = update.getMessage().getText();
                    if (answer.equals(service.rusMeaningChecking(k))) {
                        service.positiveAnswer();
                    } else {
                        service.negativeAnswer();
                    }
                    break;
                default: sendMessage(chatId, "Sorry, command was no recognized");
            }
        }
    }

    private void startCommandReceived(long chatId, String name){
        String answerParsed = EmojiParser.parseToUnicode("Hello, " + name + ", nice to meet you!" + " :blush:");
        sendMessage(chatId, answerParsed);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage msg = new SendMessage();
        msg.setChatId(String.valueOf(chatId));
        msg.setText(textToSend);

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        msg.setReplyMarkup(keyboardMarkup);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add("Practice");
        keyboardRows.add(firstRow);
        keyboardMarkup.setKeyboard(keyboardRows);

        msg.setReplyMarkup(keyboardMarkup);
        try{
            execute(msg);
        } catch (TelegramApiException e) {
            log.error("Error occured " + e.getMessage());
        }
    }
}
