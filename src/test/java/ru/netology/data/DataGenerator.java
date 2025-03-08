package ru.netology.data;

import com.github.javafaker.Faker;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class DataGenerator {
    //Генератор данных для тестов.
    //Банковские карты и геттеры к ним.
    private static String approvedCardNumber = "4444 4444 4444 4441";
    private static String declinedCardNumber = "4444 4444 4444 4442";
    private static String otherBankCardNumber = "3456 5194 8316 7531";
    private static String shorterCardNumber = "4444 4444 4444 444";
    private static String shortestCardNumber = "4";

    public static String getApprovedCardNumber() {
        return approvedCardNumber;
    }

    public static String getDeclinedCardNumber() {
        return declinedCardNumber;
    }

    public static String getOtherBankCardNumber() {
        return otherBankCardNumber;
    }

    public static String getShorterCardNumber() {
        return shorterCardNumber;
    }

    public static String getShortestCardNumber() {
        return shortestCardNumber;
    }

    //Создание случайных данных.
    private static final Faker faker = new Faker(new Locale("en"));

    public static String generateMonth(int months) {//Метод генерации месяца.
        return LocalDate.now().plusMonths(months).format(DateTimeFormatter.ofPattern("MM"));
    }

    public static String generateYear(int years) {//Метод генерации года.
        return LocalDate.now().plusYears(years).format(DateTimeFormatter.ofPattern("yy"));
    }


    //Создаем держателя карты и CVV.
    public static String generateCardOwnerName() {
        return faker.name().firstName() + " " + faker.name().lastName();
    }

    private static String[] cvc = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"};
    private static Random randomCVC = new Random();

    public static String getRandomCVC() {
        return cvc[randomCVC.nextInt(cvc.length)] + cvc[randomCVC.nextInt(cvc.length)] + cvc[randomCVC.nextInt(cvc.length)];

    }

    //Карты для API тестов.
    public static CardData getApprovedCard() {
        return new CardData("4444 4444 4444 4441", "09", "26", generateCardOwnerName(), getRandomCVC());
    }

    public static CardData getDeclinedCard() {
        return new CardData("4444 4444 4444 4442", "09", "26", generateCardOwnerName(), getRandomCVC());
    }

    public static CardData getOtherCard() {
        return new CardData("3467 5437 3589 1589", "09", "26", generateCardOwnerName(), getRandomCVC());
    }

}