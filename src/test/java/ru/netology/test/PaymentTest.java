package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.SQL.SQLHelperPayment;
import ru.netology.data.CardData;

import ru.netology.data.DataGenerator;
import ru.netology.page.PaymentPage;
import ru.netology.page.StartPage;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataGenerator.*;

public class PaymentTest {
    //Тестирование покупки тура по дебетовой карте.


    @BeforeAll
    static void setUpAll() {//Включаем генерацию отчета.
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @BeforeEach
    void entry() {//Заходим на сайт и нажимаем кнопку купить.
        open("http://localhost:8080");

    }

    @AfterAll
    static void tearDown() {//Выключаем генерацию отчета.
        SelenideLogger.removeListener("allure");
    }


    @Test
    @DisplayName("1.1. Успешная покупка.")
    public void approvedPathPayTest() {
        SQLHelperPayment.dropTables(); // Удаляет старые данные перед тестом
        var startPage = new StartPage();//Создаем стартовую страницу
        var paymentPage = startPage.payment();//Жмем на кнопку купить на стартовой
        CardData card = new CardData(getApprovedCardNumber(), generateMonth(1), generateYear(1), generateCardOwnerName(), getRandomCVC());// Создаем карту с данными
        paymentPage.getInsertCardData(card);//Заполняем поля данными карты
        paymentPage.paymentSuccessfulNotification();
        assertEquals("APPROVED", SQLHelperPayment.getCardStatusApproved());
    }

    @Test
    @DisplayName("1.2. Карта отклонена.")
    public void declinedPathPayTest() {//Баг.
        SQLHelperPayment.dropTables(); // Удаляет старые данные перед тестом
        var startPage = new StartPage();
        var paymentPage = startPage.payment();
        CardData card = new CardData(getDeclinedCardNumber(), generateMonth(1), generateYear(1), generateCardOwnerName(), getRandomCVC());
        paymentPage.getInsertCardData(card);
        paymentPage.paymentFailedNotification();
        assertEquals("DECLINED", SQLHelperPayment.getCardStatusDeclined());
    }

    @Test
    @DisplayName("1.3. Карта другого банка отклонена.")
    public void otherBanksCardPayTest() {
        var startPage = new StartPage();
        var paymentPage = startPage.payment();
        CardData card = new CardData(getOtherBankCardNumber(), generateMonth(1), generateYear(1), generateCardOwnerName(), getRandomCVC());
        paymentPage.getInsertCardData(card);
        paymentPage.paymentFailedNotification();
    }

    @Test
    @DisplayName("1.4. Пустые поля.")
    public void emptyFieldsPayTest() {//Баг-почти везде неверный формат
        var startPage = new StartPage();
        var paymentPage = startPage.payment();
        CardData card = new CardData("", "", "", "", "");
        paymentPage.getInsertCardData(card);
        paymentPage.verifyAllFieldsFilledPay();
    }

    @Test
    @DisplayName("1.5. Пустое поле 'Номер карты'.")//Баг
    public void emptyCardNumberPayTest() {
        var startPage = new StartPage();
        var paymentPage = startPage.payment();
        CardData card = new CardData("", generateMonth(1), generateYear(1), generateCardOwnerName(), getRandomCVC());
        paymentPage.getInsertCardData(card);
        paymentPage.verifyEmptyFieldPay();
    }

    @Test
    @DisplayName("1.6. Неполный номер карты '15 знаков'.")
    public void shorterCardNumberPayTest() {
        var startPage = new StartPage();
        var paymentPage = startPage.payment();
        CardData card = new CardData(getShorterCardNumber(), generateMonth(1), generateYear(1), generateCardOwnerName(), getRandomCVC());
        paymentPage.getInsertCardData(card);
        paymentPage.verifyInvalidFormatPay();
    }

    @Test
    @DisplayName("1.7. Неполный номер карты '1 знаков'.")
    public void shortestCardNumberPayTest() {
        var startPage = new StartPage();
        var paymentPage = startPage.payment();
        CardData card = new CardData(getShortestCardNumber(), generateMonth(1), generateYear(1), generateCardOwnerName(), getRandomCVC());
        paymentPage.getInsertCardData(card);
        paymentPage.verifyInvalidFormatPay();
    }

    @Test
    @DisplayName("1.8. Пустое поле 'Месяц'.")//баг
    public void emptyMonthPayTest() {
        var startPage = new StartPage();
        var paymentPage = startPage.payment();
        CardData card = new CardData(getApprovedCardNumber(), "", generateYear(1), generateCardOwnerName(), getRandomCVC());
        paymentPage.getInsertCardData(card);
        paymentPage.verifyEmptyFieldPay();
    }

    @Test
    @DisplayName("1.9. Неккоректный номер месяца '1 знак'")
    public void oneNumberMonthPayTest() {
        var startPage = new StartPage();
        var paymentPage = startPage.payment();
        CardData card = new CardData(getApprovedCardNumber(), "3", generateYear(1), generateCardOwnerName(), getRandomCVC());
        paymentPage.getInsertCardData(card);
        paymentPage.verifyInvalidFormatPay();
    }

    @Test
    @DisplayName("1.10. Неккоректный номер месяца '2 ноля'")
    public void twoZeroNumberMonthPayTest() {//баг
        var startPage = new StartPage();
        var paymentPage = startPage.payment();
        CardData card = new CardData(getApprovedCardNumber(), "00", generateYear(1), generateCardOwnerName(), getRandomCVC());
        paymentPage.getInsertCardData(card);
        paymentPage.verifyInvalidFormatPay();
    }

    @Test
    @DisplayName("1.11. Неккоректный номер месяца 'больше 12'")
    public void moreThanTwelveNumberMonthPayTest() {
        var startPage = new StartPage();
        var paymentPage = startPage.payment();
        CardData card = new CardData(getApprovedCardNumber(), "13", generateYear(1), generateCardOwnerName(), getRandomCVC());
        paymentPage.getInsertCardData(card);
        paymentPage.verifyInvalidCardValidityPeriodPay();
    }

    @Test
    @DisplayName("1.12. Неккоректный номер месяца 'прошлый месяц'")
    public void lastMonthPayTest() {
        var startPage = new StartPage();
        var paymentPage = startPage.payment();
        CardData card = new CardData(getApprovedCardNumber(), generateMonth(-1), generateYear(0), generateCardOwnerName(), getRandomCVC());
        paymentPage.getInsertCardData(card);
        paymentPage.verifyInvalidCardValidityPeriodPay();
    }

    @Test
    @DisplayName("1.13. Пустое поле 'Год'.")//баг
    public void emptyYearPayTest() {
        var startPage = new StartPage();
        var paymentPage = startPage.payment();
        CardData card = new CardData(getApprovedCardNumber(), generateMonth(1), "", generateCardOwnerName(), getRandomCVC());
        paymentPage.getInsertCardData(card);
        paymentPage.verifyEmptyFieldPay();
    }

    @Test
    @DisplayName("1.14. Неккоректный номер года 'Ноль'.")
    public void zeroYearPayTest() {
        var startPage = new StartPage();
        var paymentPage = startPage.payment();
        CardData card = new CardData(getApprovedCardNumber(), generateMonth(1), "0", generateCardOwnerName(), getRandomCVC());
        paymentPage.getInsertCardData(card);
        paymentPage.verifyInvalidFormatPay();
    }

    @Test
    @DisplayName("1.15. Неккоректный номер года '2 ноля'.")
    public void twoZeroYearPayTest() {
        var startPage = new StartPage();
        var paymentPage = startPage.payment();
        CardData card = new CardData(getApprovedCardNumber(), generateMonth(1), "00", generateCardOwnerName(), getRandomCVC());
        paymentPage.getInsertCardData(card);
        paymentPage.verifyCardExpiredPay();
    }

    @Test
    @DisplayName("1.16. Неккоректный номер года 'Прошлый год'.")
    public void lastYearPayTest() {
        var startPage = new StartPage();
        var paymentPage = startPage.payment();
        CardData card = new CardData(getApprovedCardNumber(), generateMonth(1), generateYear(-1), generateCardOwnerName(), getRandomCVC());
        paymentPage.getInsertCardData(card);
        paymentPage.verifyCardExpiredPay();
    }

    @Test
    @DisplayName("1.17. Неккоректный номер года 'Более 6 лет от текущего'.")
    public void farFutureYearPayTest() {
        var startPage = new StartPage();
        var paymentPage = startPage.payment();
        CardData card = new CardData(getApprovedCardNumber(), generateMonth(1), generateYear(6), generateCardOwnerName(), getRandomCVC());
        paymentPage.getInsertCardData(card);
        paymentPage.verifyInvalidCardValidityPeriodPay();
    }

    @Test
    @DisplayName("1.18. Пустое поле 'Владелец'.")
    public void emptyCardOwnerPayTest() {
        var startPage = new StartPage();
        var paymentPage = startPage.payment();
        CardData card = new CardData(getApprovedCardNumber(), generateMonth(1), generateYear(1), "", getRandomCVC());
        paymentPage.getInsertCardData(card);
        paymentPage.verifyEmptyFieldPay();
    }

    @Test
    @DisplayName("1.19.1. Неккоректное имя владельца '1 слово на английском'.")
    public void oneNameEngCardOwnerPayTest() {//Баг
        var startPage = new StartPage();
        var paymentPage = startPage.payment();
        CardData card = new CardData(getApprovedCardNumber(), generateMonth(1), generateYear(1), "OBAMA", getRandomCVC());
        paymentPage.getInsertCardData(card);
        paymentPage.verifyInvalidOwnerPay();
    }

    @Test
    @DisplayName("1.19.2. Неккоректное имя владельца '1 слово на русском'.")
    public void oneNameRusCardOwnerPayTest() {//Баг
        var startPage = new StartPage();
        var paymentPage = startPage.payment();
        CardData card = new CardData(getApprovedCardNumber(), generateMonth(1), generateYear(1), "Димитрий", getRandomCVC());
        paymentPage.getInsertCardData(card);
        paymentPage.verifyInvalidFormatPay();
    }

    @Test
    @DisplayName("1.19.3. Неккоректное имя владельца '2 слова на русском'.")
    public void FullNameRusCardOwnerPayTest() {//Баг
        var startPage = new StartPage();
        var paymentPage = startPage.payment();
        CardData card = new CardData(getApprovedCardNumber(), generateMonth(1), generateYear(1), "Димитрий Иванов", getRandomCVC());
        paymentPage.getInsertCardData(card);
        paymentPage.verifyInvalidFormatPay();
    }

    @Test
    @DisplayName("1.20. Пустое поле CVC/CVV.")//
    public void emptyCVCPayTest() {
        var startPage = new StartPage();
        var paymentPage = startPage.payment();
        CardData card = new CardData(getApprovedCardNumber(), generateMonth(1), generateYear(1), generateCardOwnerName(), "");
        paymentPage.getInsertCardData(card);
        paymentPage.verifyEmptyFieldPay();
    }

    @Test
    @DisplayName("1.21. Неккоректный номер CVC/CVV '2 цифры'.")
    public void wrongCVCPayTest() {
        var startPage = new StartPage();
        var paymentPage = startPage.payment();
        CardData card = new CardData(getApprovedCardNumber(), generateMonth(1), generateYear(1), generateCardOwnerName(), "45");
        paymentPage.getInsertCardData(card);
        paymentPage.verifyInvalidFormatPay();
    }

    @Test
    @DisplayName("1.22. Неккоректный номер CVC/CVV 'Три ноля'.")
    public void tripleZeroCVCPayTest() {//баг
        var startPage = new StartPage();
        var paymentPage = startPage.payment();
        CardData card = new CardData(getApprovedCardNumber(), generateMonth(1), generateYear(1), generateCardOwnerName(), "000");
        paymentPage.getInsertCardData(card);
        paymentPage.verifyInvalidFormatPay();
    }


}
