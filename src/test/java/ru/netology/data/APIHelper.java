package ru.netology.data;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class APIHelper {


    public static RequestSpecification requestSpec = new RequestSpecBuilder()// Создаем спецификацию запроса для REST API с помощью REST ASSURED
            .setBaseUri("http://localhost")//Указываем базовый URL, который будет использоваться для всех запросов
            .setPort(8080)//Указываем порт, который будет использоваться для соединения с сервером. В данном случае это порт 8080
            .setAccept(ContentType.JSON)//Указывает, что клиент ожидает получить ответ в формате JSON.
            .setContentType(ContentType.JSON)//Указывает, что тело запроса будет отправлено в формате JSON.
            .log(LogDetail.ALL)//Включает полное логирование всех деталей запроса и ответа. Это полезно для отладки и анализа запросов.
            .build();//Создает объект RequestSpecification, который можно использовать для построения запросов.


    public static String queryForm(CardData cardData, String url, int responseStatus) {//Метод отправляет POST-запрос к указанному URL с данными в теле запроса и проверяет статус ответа.
        return given()
                .spec(requestSpec)//указывает спецификацию запроса, которая была создана ранее.
                .body(cardData)//добавляет объект cardData в тело запроса.
                .when()
                .post(url)//отправляет POST-запрос к указанному URL.
                .then()
                .statusCode(responseStatus)// Проверяет, что статус ответа соответствует ожидаемому значению, переданному в параметре responseStatus.
                .extract().response().asString();// Извлекает полный ответ в виде строки
    }


}
