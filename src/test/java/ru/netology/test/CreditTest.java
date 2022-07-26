package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;

import ru.netology.SQL.SQLHelperPayment;
import ru.netology.data.CardData;
import ru.netology.page.CreditPage;
import ru.netology.page.StartPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataGenerator.*;

public class CreditTest {
    //Тестирование покупки тура в кредит.

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
    @DisplayName("2.1. Успешная покупка.")
    public void approvedPathCreditTest() {
        SQLHelperPayment.dropTables(); // Удаляет старые данные перед тестом
        var startPage = new StartPage();//Создаем стартовую страницу
        var creditPage = startPage.creditPay();//Жмем на кнопку купить на стартовой
        CardData card = new CardData(getApprovedCardNumber(), generateMonth(1), generateYear(1), generateCardOwnerName(), getRandomCVC());// Создаем карту с данными
        creditPage.getInsertCardDataForCredit(card);//Заполняем поля данными карты
        creditPage.creditSuccessfulNotification();
        assertEquals("APPROVED", SQLHelperPayment.getCardStatusApprovedForCredit());
    }

    @Test
    @DisplayName("2.2. Карта отклонена.")
    public void declinedPathCreditTest() {//Баг.
        SQLHelperPayment.dropTables(); // Удаляет старые данные перед тестом
        var startPage = new StartPage();
        var creditPage = startPage.creditPay();
        CardData card = new CardData(getDeclinedCardNumber(), generateMonth(1), generateYear(1), generateCardOwnerName(), getRandomCVC());
        creditPage.getInsertCardDataForCredit(card);
        creditPage.creditFailedNotification();
        assertEquals("DECLINED", SQLHelperPayment.getCardStatusDeclinedForCredit());
    }

    @Test
    @DisplayName("2.3. Карта другого банка отклонена.")
    public void otherBanksCardCreditTest() {
        var startPage = new StartPage();
        var creditPage = startPage.creditPay();
        CardData card = new CardData(getOtherBankCardNumber(), generateMonth(1), generateYear(1), generateCardOwnerName(), getRandomCVC());
        creditPage.getInsertCardDataForCredit(card);
        creditPage.creditFailedNotification();
    }

    @Test
    @DisplayName("2.4. Пустые поля.")//Баг
    public void emptyFieldsTest() {
        var startPage = new StartPage();
        var creditPage = startPage.creditPay();
        CardData card = new CardData("", "", "", "", "");
        creditPage.getInsertCardDataForCredit(card);
        creditPage.verifyAllFieldsFilledCredit();//
    }

    @Test
    @DisplayName("2.5. Пустое поле 'Номер карты'.")//баг
    public void emptyCardNumberCreditTest() {
        var startPage = new StartPage();
        var creditPage = startPage.creditPay();
        CardData card = new CardData("", generateMonth(1), generateYear(1), generateCardOwnerName(), getRandomCVC());
        creditPage.getInsertCardDataForCredit(card);
        creditPage.verifyEmptyFieldCredit();
    }

    @Test
    @DisplayName("2.6. Неполный номер карты '15 знаков'.")
    public void shorterCardNumberCreditTest() {
        var startPage = new StartPage();
        var creditPage = startPage.creditPay();
        CardData card = new CardData(getShorterCardNumber(), generateMonth(1), generateYear(1), generateCardOwnerName(), getRandomCVC());
        creditPage.getInsertCardDataForCredit(card);
        creditPage.verifyInvalidFormatCredit();
    }

    @Test
    @DisplayName("2.7. Неполный номер карты '1 знаков'.")
    public void shortestCardNumberTest() {
        var startPage = new StartPage();
        var creditPage = startPage.creditPay();
        CardData card = new CardData(getShortestCardNumber(), generateMonth(1), generateYear(1), generateCardOwnerName(), getRandomCVC());
        creditPage.getInsertCardDataForCredit(card);
        creditPage.verifyInvalidFormatCredit();
    }

    @Test
    @DisplayName("2.8. Пустое поле 'Месяц'.")//баг
    public void emptyMonthCreditTest() {
        var startPage = new StartPage();
        var creditPage = startPage.creditPay();
        CardData card = new CardData(getApprovedCardNumber(), "", generateYear(1), generateCardOwnerName(), getRandomCVC());
        creditPage.getInsertCardDataForCredit(card);
        creditPage.verifyEmptyFieldCredit();
    }


    @Test
    @DisplayName("2.9. Неккоректный номер месяца '1 знак'")
    public void oneNumberMonthCreditTest() {
        var startPage = new StartPage();
        var creditPage = startPage.creditPay();
        CardData card = new CardData(getApprovedCardNumber(), "3", generateYear(1), generateCardOwnerName(), getRandomCVC());
        creditPage.getInsertCardDataForCredit(card);
        creditPage.verifyInvalidFormatCredit();
    }

    @Test
    @DisplayName("2.10. Неккоректный номер месяца '2 ноля'")
    public void twoZeroNumberMonthCreditTest() {//баг
        var startPage = new StartPage();
        var creditPage = startPage.creditPay();
        CardData card = new CardData(getApprovedCardNumber(), "00", generateYear(1), generateCardOwnerName(), getRandomCVC());
        creditPage.getInsertCardDataForCredit(card);
        creditPage.verifyInvalidFormatCredit();
    }

    @Test
    @DisplayName("2.11. Неккоректный номер месяца 'больше 12'")
    public void moreThanTwelveNumberMonthCreditTest() {
        var startPage = new StartPage();
        var creditPage = startPage.creditPay();
        CardData card = new CardData(getApprovedCardNumber(), "13", generateYear(1), generateCardOwnerName(), getRandomCVC());
        creditPage.getInsertCardDataForCredit(card);
        creditPage.verifyInvalidCardValidityPeriodCredit();
    }

    @Test
    @DisplayName("2.12. Неккоректный номер месяца 'прошлый месяц'")
    public void lastMonthPayTest() {
        var startPage = new StartPage();
        var creditPage = startPage.creditPay();
        CardData card = new CardData(getApprovedCardNumber(), generateMonth(-1), generateYear(0), generateCardOwnerName(), getRandomCVC());
        creditPage.getInsertCardDataForCredit(card);
        creditPage.verifyInvalidCardValidityPeriodCredit();
    }

    @Test
    @DisplayName("2.13. Пустое поле 'Год'.")//баг
    public void emptyYearCreditTest() {
        var startPage = new StartPage();
        var creditPage = startPage.creditPay();
        CardData card = new CardData(getApprovedCardNumber(), generateMonth(1), "", generateCardOwnerName(), getRandomCVC());
        creditPage.getInsertCardDataForCredit(card);
        creditPage.verifyEmptyFieldCredit();
    }

    @Test
    @DisplayName("2.14. Неккоректный номер года 'Ноль'.")
    public void zeroYearCreditTest() {
        var startPage = new StartPage();
        var creditPage = startPage.creditPay();
        CardData card = new CardData(getApprovedCardNumber(), generateMonth(1), "0", generateCardOwnerName(), getRandomCVC());
        creditPage.getInsertCardDataForCredit(card);
        creditPage.verifyInvalidFormatCredit();
    }

    @Test
    @DisplayName("2.15. Неккоректный номер года '2 ноля'.")
    public void twoZeroYearCreditTest() {
        var startPage = new StartPage();
        var creditPage = startPage.creditPay();
        CardData card = new CardData(getApprovedCardNumber(), generateMonth(1), "00", generateCardOwnerName(), getRandomCVC());
        creditPage.getInsertCardDataForCredit(card);
        creditPage.verifyCardExpiredCredit();
    }

    @Test
    @DisplayName("2.16. Неккоректный номер года 'Прошлый год'.")
    public void lastYearCreditTest() {
        var startPage = new StartPage();
        var creditPage = startPage.creditPay();
        CardData card = new CardData(getApprovedCardNumber(), generateMonth(1), generateYear(-1), generateCardOwnerName(), getRandomCVC());
        creditPage.getInsertCardDataForCredit(card);
        creditPage.verifyCardExpiredCredit();
    }

    @Test
    @DisplayName("2.17. Неккоректный номер года 'Более 6 лет от текущего'.")
    public void farFutureYearCreditTest() {
        var startPage = new StartPage();
        var creditPage = startPage.creditPay();
        CardData card = new CardData(getApprovedCardNumber(), generateMonth(1), generateYear(6), generateCardOwnerName(), getRandomCVC());
        creditPage.getInsertCardDataForCredit(card);
        creditPage.verifyInvalidCardValidityPeriodCredit();
    }


    @Test
    @DisplayName("2.18. Пустое поле 'Владелец'.")
    public void emptyCardOwnerTest() {
        var startPage = new StartPage();
        var creditPage = startPage.creditPay();
        CardData card = new CardData(getApprovedCardNumber(), generateMonth(1), generateYear(1), "", getRandomCVC());
        creditPage.getInsertCardDataForCredit(card);
        creditPage.verifyEmptyFieldCredit();

    }

    @Test
    @DisplayName("2.19.1. Неккоректное имя владельца '1 слово на английском'.")
    public void oneNameEngCardOwnerCreditTest() {//Баг
        var startPage = new StartPage();
        var creditPage = startPage.creditPay();
        CardData card = new CardData(getApprovedCardNumber(), generateMonth(1), generateYear(1), "OBAMA", getRandomCVC());
        creditPage.getInsertCardDataForCredit(card);
        creditPage.verifyInvalidOwnerCredit();
    }

    @Test
    @DisplayName("2.19.2. Неккоректное имя владельца '1 слово на русском'.")
    public void oneNameRusCardOwnerCreditTest() {//Баг
        var startPage = new StartPage();
        var creditPage = startPage.creditPay();
        CardData card = new CardData(getApprovedCardNumber(), generateMonth(1), generateYear(1), "Димитрий", getRandomCVC());
        creditPage.getInsertCardDataForCredit(card);
        creditPage.verifyInvalidOwnerCredit();
    }

    @Test
    @DisplayName("2.19.3. Неккоректное имя владельца '2 слова на русском'.")
    public void FullNameRusCardOwnerCreditTest() {//Баг
        var startPage = new StartPage();
        var creditPage = startPage.creditPay();
        CardData card = new CardData(getApprovedCardNumber(), generateMonth(1), generateYear(1), "Димитрий Иванов", getRandomCVC());
        creditPage.getInsertCardDataForCredit(card);
        creditPage.verifyInvalidOwnerCredit();
    }

    @Test
    @DisplayName("2.20. Пустое поле CVC/CVV.")//
    public void emptyCVCTest() {
        var startPage = new StartPage();
        var creditPage = startPage.creditPay();
        CardData card = new CardData(getApprovedCardNumber(), generateMonth(1), generateYear(1), generateCardOwnerName(), "");
        creditPage.getInsertCardDataForCredit(card);
        creditPage.verifyEmptyFieldCredit();
    }

    @Test
    @DisplayName("2.21. Неккоректный номер CVC/CVV '2 цифры'.")
    public void wrongCVCTest() {
        var startPage = new StartPage();
        var creditPage = startPage.creditPay();
        CardData card = new CardData(getApprovedCardNumber(), generateMonth(1), generateYear(1), generateCardOwnerName(), "45");
        creditPage.getInsertCardDataForCredit(card);
        creditPage.verifyInvalidFormatCredit();
    }

    @Test
    @DisplayName("2.22. Неккоректный номер CVC/CVV 'Три ноля'.")
    public void tripleZeroCVCTest() {//баг
        var startPage = new StartPage();
        var creditPage = startPage.creditPay();
        CardData card = new CardData(getApprovedCardNumber(), generateMonth(1), generateYear(1), generateCardOwnerName(), "000");
        creditPage.getInsertCardDataForCredit(card);
        creditPage.verifyInvalidFormatCredit();
    }
}