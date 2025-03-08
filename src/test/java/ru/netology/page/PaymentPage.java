package ru.netology.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.SelenideElement;
import ru.netology.data.CardData;

import ru.netology.data.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class PaymentPage {
    //CSS-селекторы для элементов страницы для покупки тура по дебетовой карте
// Хедер страницы Оплата по карте и кнопка купить.
    private SelenideElement payHeader = $(withText("Оплата по карте"));// Проверяю наличие хедера


    //Форма для ввода данных об оплате.
    private SelenideElement paymentHeader = $("[class='form form_size_m form_theme_alfa-on-white']");// Проверяю наличие формы для заполнения

    //Параметры карты.
    private SelenideElement cardNumberField = $("[placeholder= '0000 0000 0000 0000']");
    private SelenideElement cardMonthField = $("[placeholder= '08']");
    private SelenideElement cardYearField = $("[placeholder= '22']");
    private SelenideElement cardOwnerField = $(byText("Владелец")).parent().$(".input__control");
    private SelenideElement cardCVCField = $("[placeholder= '999']");


    //Разные кнопки, оповещения.
    private SelenideElement continueButton = $(withText("Продолжить"));
    private SelenideElement succeedNotification = $(".notification_status_ok");
    private SelenideElement failedNotification = $(".notification_status_error");


    public PaymentPage() {
        paymentHeader.shouldBe(visible);
    }// Конструктор, проверяем наличие формы оплаты.

    //Сообщение об успешной или неуспешной регистрации.
    public void paymentSuccessfulNotification() {
        succeedNotification.shouldHave(text("Успешно Операция одобрена Банком."), Duration.ofSeconds(15)).shouldBe(visible);
    }

    public void paymentFailedNotification() {
        failedNotification.shouldHave(text("Ошибка! Банк отказал в проведении операции."), Duration.ofSeconds(15)).shouldBe(visible);
    }

    public void getInsertCardData(CardData cardData) {// Метод для заполнения данных карты и нажатие кнопки продолжить
        cardNumberField.setValue(cardData.getNumber());
        cardMonthField.setValue(cardData.getMonth());
        cardYearField.setValue(cardData.getYear());
        cardOwnerField.setValue(cardData.getOwner());
        cardCVCField.setValue(cardData.getCvc());
        continueButton.click();
    }

    // Сообщения о неверном формате и пустом поле
    public void verifyInvalidFormatPay() {
        $(".input__sub").shouldBe(visible).shouldHave(text("Неверный формат"), Duration.ofSeconds(15));
    }

    public void verifyInvalidCardValidityPeriodPay() {
        $(".input__sub").shouldBe(visible)
                .shouldHave(text("Неверно указан срок действия карты"), Duration.ofSeconds(15));
    }

    public void verifyCardExpiredPay() {
        $(".input__sub").shouldBe(visible)
                .shouldHave(text("Истёк срок действия карты"), Duration.ofSeconds(15));
    }

    public void verifyEmptyFieldPay() {
        $(".input__sub").shouldBe(visible)
                .shouldHave(text("Поле обязательно для заполнения"), Duration.ofSeconds(15));
    }

    public void verifyAllFieldsFilledPay() {
        $$(".input__sub").shouldHave(CollectionCondition.size(5))
                .shouldHave(CollectionCondition.texts("Поле обязательно для заполнения"));
    }

    public void verifyInvalidOwnerPay() {//По идее в требованиях должно быть такое сообщение
        $(".input__sub").shouldBe(visible)
                .shouldHave(text("Введите имя и фамилию, указанные на карте"), Duration.ofSeconds(15));
    }


}
