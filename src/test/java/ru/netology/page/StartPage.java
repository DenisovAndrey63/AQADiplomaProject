package ru.netology.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class StartPage {
    //CSS селекторы для стартовой страницы

    private SelenideElement headerStart = $("h2.heading");
    private SelenideElement payButton = $(withText("Купить"));
    private SelenideElement creditButton = $(withText("Купить в кредит"));

    public StartPage() {
        headerStart.shouldBe(visible);
    }//Проверяем видимость стартовой страницы

    public PaymentPage payment() {//Жмем кнопку купить на стартовой
        payButton.click();
        return new PaymentPage();
    }

    public CreditPage creditPay() {//Жмем кнопку купить в кредит на стартовой
        creditButton.click();
        return new CreditPage();
    }
}
