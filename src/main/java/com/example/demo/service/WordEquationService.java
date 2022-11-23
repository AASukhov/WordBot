package com.example.demo.service;

import com.example.demo.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@Service
public class WordEquationService {

    @Autowired
    WordRepository repository;

    @Value("${spring.datasource.url}")
    String dataBase;

    @Value("${spring.datasource.username}")
    String dbUserName;

    @Value("${spring.datasource.password}")
    String dbPassword;

    public String TurkWordGiving(){
        String url = dataBase;
        String username = dbUserName;
        String password = dbPassword;
        String turkish ="";
        try (Connection connection = DriverManager.getConnection(url,username,password)){
            Class.forName("org.postgresql.Driver").getDeclaredConstructor().newInstance();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM words ORDER BY random() LIMIT 1");
            turkish = resultSet.getString(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return turkish;
    }

    public String rusMeaningChecking(String turkWord){
        String url = dataBase;
        String username = dbUserName;
        String password = dbPassword;
        String text ="";
        String query = "SELECT russian_word FROM words WHERE turkish_word=" + turkWord;
        try (Connection connection = DriverManager.getConnection(url,username,password)){
            Class.forName("org.postgresql.Driver").getDeclaredConstructor().newInstance();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            text = resultSet.getString(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }

    public String positiveAnswer() {
        double i = Math.random();
        int k = (int)(i * 100);
        if (0 <= k && k < 33) {
            return "Верно!";
        } else if (33 <= k && k < 67) {
            return "Вы правы";
        } else {
            return "Ты молодец";
        }
    }

    public String negativeAnswer() {
        double i = Math.random();
        int k = (int)(i * 100);
        if (0 <= k && k < 33) {
            return "Неверно!";
        } else if (33 <= k && k < 67) {
            return "К сожалению, неверно";
        } else {
            return "Неправильно, попробуй еще раз";
        }
    }
}
