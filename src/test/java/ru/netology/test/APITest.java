package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.netology.SQL.SQLHelperPayment;
import ru.netology.data.APIHelper;
import ru.netology.data.CardData;
import ru.netology.data.DataGenerator;

import static io.restassured.RestAssured.given;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.netology.data.APIHelper.*;
import static ru.netology.data.DataGenerator.*;

public class APITest {

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }


    @Test
    @DisplayName("3.1. POST запрос с данными одобренной дебетовой карты")
    void paymentApprovedCardAPI() {//Получает объект, представляющий валидную карту, которая должна быть одобрена при оплате.
        SQLHelperPayment.dropTables(); // Удаляет старые данные перед тестом
        val validApprovedCard = getApprovedCard();// Из Датагенератора берем валидную карту
        val status = queryForm(validApprovedCard, "/api/v1/pay", 200);//Отправляет POST запрос формой из АПИхелпера с данными валидной карты и ожидаем ответ 200 от сервера
        SQLHelperPayment.checkSuccessPayByCard();//Вызывает метод, который проверяет, что оплата была успешно проведена в базе данных
    }


    @Test
    @DisplayName("3.2. POST запрос с данными отклоненной дебетовой карты")
    void paymentDeclinedCardAPI() {
        SQLHelperPayment.dropTables(); // Удаляет старые данные перед тестом
        val validDeclinedCard = getDeclinedCard();
        val status = queryForm(validDeclinedCard, "/api/v1/pay", 200);
        SQLHelperPayment.checkFailurePayByCard();

    }

    @Test
    @DisplayName("3.3. POST запрос с данными одобренной кредитной карты")
    void creditApprovedCardAPI() {// баг с credit id
        SQLHelperPayment.dropTables(); // Удаляет старые данные перед тестом
        val validApprovedCard = getApprovedCard();
        val status = queryForm(validApprovedCard, "/api/v1/credit", 200);
        SQLHelperPayment.checkSuccessCredit();
    }


    @Test
    @DisplayName("3.4. POST запрос с данными отклоненной кредитной карты")
    void creditDeclinedCardAPI() {// баг
        SQLHelperPayment.dropTables(); // Удаляет старые данные перед тестом
        val validDeclinedCard = getDeclinedCard();
        val status = queryForm(validDeclinedCard, "/api/v1/credit", 200);
        SQLHelperPayment.checkFailureCredit();
    }

    @Test
    @DisplayName("3.5. POST запрос с данными дебетовой карты другого банка")
    void paymentOtherCardAPI() {//Баг, статус ответа 500
        val otherBankCardPay = getOtherCard();
        val status = queryForm(otherBankCardPay, "/api/v1/pay", 400);
    }

    @Test
    @DisplayName("3.6. POST запрос с данными кредитной карты другого банка")
    void creditOtherCardAPI() {//Баг, статус ответа 500
        val otherBankCardCredit = getOtherCard();
        val status = queryForm(otherBankCardCredit, "/api/v1/credit", 400);
    }
}

