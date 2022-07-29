package ru.netology.SQL;

import lombok.SneakyThrows;
import lombok.val;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class SQLHelperPayment {

    @SneakyThrows
    private static Connection connection() {
        return DriverManager.getConnection
                (System.getProperty("dataBase.url"),
                        System.getProperty("username"),
                        System.getProperty("password")
                );
    }


    @SneakyThrows
    public static void dropTables() {
        try (
                var dataStmt = connection().createStatement();//Создаем абстракцию выполнения запроса
        ) {
            val dropTableCRE = "DELETE FROM credit_request_entity";//Удаляю данные таблиц
            val dropTablePE = "DELETE FROM payment_entity";
            val dropTable = "DELETE FROM order_entity";
            dataStmt.executeUpdate(dropTableCRE);
            dataStmt.executeUpdate(dropTablePE);
            dataStmt.executeUpdate(dropTable);
            System.out.println("Data deleted in Data Base tables...");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @SneakyThrows

    public static String getCardStatusApproved() {
        val cardStatusApproved = "SELECT status FROM payment_entity";//Запрашиваю данные стобца статус таблицы
        try (
                Statement dataStmt = connection().createStatement();//Создаем абстракцию выполнения запроса
        ) {
            try (ResultSet rs = dataStmt.executeQuery(cardStatusApproved)) {//Выполняем запрос
                if (rs.next()) {//Узнаем, есть след. строка и передв.курсор на нее
                    return rs.getString(1);
                }
            }
        }
        return "Error";
    }


    @SneakyThrows
    public static String getCardStatusDeclined() {
        val cardStatusDeclined = "SELECT status FROM payment_entity";
        try (
                val dataStmt = connection().createStatement();
        ) {
            try (val rs = dataStmt.executeQuery(cardStatusDeclined)) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        }
        return "Error";
    }


    @SneakyThrows
    public static String getCardStatusApprovedForCredit() {
        val cardStatusApproved = "SELECT status FROM credit_request_entity";//Запрашиваю данные стобца статус таблицы
        try (
                Statement dataStmt = connection().createStatement();//Создаем абстракцию выполнения запроса
        ) {
            try (ResultSet rs = dataStmt.executeQuery(cardStatusApproved)) {//Выполняем запрос
                if (rs.next()) {//Узнаем, есть след. строка и передв.курсор на нее
                    return rs.getString(1);
                }
            }
        }
        return "Error";
    }

    @SneakyThrows
    public static String getCardStatusDeclinedForCredit() {
        val cardStatusDeclined = "SELECT status FROM credit_request_entity";
        try (
                val dataStmt = connection().createStatement();
        ) {
            try (val rs = dataStmt.executeQuery(cardStatusDeclined)) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        }
        return "Error";
    }

    @SneakyThrows
    private static String getAmountPaymentEntity() {
        var query = "SELECT amount FROM payment_entity";
        try (
                val dataStmt = connection().createStatement();
        ) {

            try (var rs = dataStmt.executeQuery(query)) {
                rs.next();
                return rs.getString("amount");
            }
        }
    }

    @SneakyThrows
    private static String getCreatedPaymentEntity() {
        var query = "SELECT created FROM payment_entity";
        try (
                val dataStmt = connection().createStatement();
        ) {

            try (var rs = dataStmt.executeQuery(query)) {
                rs.next();
                return rs.getString("created").substring(0, 15);
            }
        }
    }

    @SneakyThrows
    private static String getCreatedOrderEntity() {
        var query = "SELECT created FROM order_entity";
        try (
                val dataStmt = connection().createStatement();
        ) {
            try (var rs = dataStmt.executeQuery(query)) {
                rs.next();
                return rs.getString("created").substring(0, 15);
            }
        }
    }

    @SneakyThrows
    private static String getTransactionIdPaymentEntity() {
        var query = "SELECT transaction_id FROM payment_entity";
        try (
                val dataStmt = connection().createStatement();
        ) {
            try (var rs = dataStmt.executeQuery(query)) {
                rs.next();
                return rs.getString("transaction_id");
            }
        }
    }

    @SneakyThrows
    private static String getPaymentIdOrderEntity() {
        var query = "SELECT payment_id FROM order_entity";
        try (
                val dataStmt = connection().createStatement();
        ) {
            try (var rs = dataStmt.executeQuery(query)) {
                rs.next();
                return rs.getString("payment_id");
            }
        }
    }

    @SneakyThrows
    private static String getCreditIdOrderEntity() {
        var query = "SELECT credit_id FROM order_entity";
        try (
                val dataStmt = connection().createStatement();
        ) {
            try (var rs = dataStmt.executeQuery(query)) {
                rs.next();
                return rs.getString("credit_id");
            }
        }
    }

    @SneakyThrows
    private static String getBankIdCreditRequestEntity() {
        var query = "SELECT bank_id FROM credit_request_entity";
        try (
                val dataStmt = connection().createStatement();
        ) {
            try (var rs = dataStmt.executeQuery(query)) {
                rs.next();
                return rs.getString("bank_id");
            }
        }
    }

    @SneakyThrows
    private static String getCreatedCreditRequestEntity() {
        var query = "SELECT created FROM credit_request_entity";
        try (
                val dataStmt = connection().createStatement();
        ) {
            try (var rs = dataStmt.executeQuery(query)) {
                rs.next();
                return rs.getString("created").substring(0, 15);
            }
        }
    }


    public static void checkSuccessPayByCard() {
        assertEquals("APPROVED", getCardStatusApproved());
        assertEquals("4500000", getAmountPaymentEntity());
        assertEquals(getCreatedPaymentEntity(), getCreatedOrderEntity());
        assertEquals(getTransactionIdPaymentEntity(), getPaymentIdOrderEntity());
        assertNull(getCreditIdOrderEntity());
    }

    public static void checkFailurePayByCard() {
        assertEquals("DECLINED", getCardStatusDeclined());
        assertEquals("4500000", getAmountPaymentEntity());
        assertEquals(getCreatedPaymentEntity(), getCreatedOrderEntity());
        assertEquals(getTransactionIdPaymentEntity(), getPaymentIdOrderEntity());
        assertNull(getCreditIdOrderEntity());
    }

    public static void checkSuccessCredit() {
        assertEquals("APPROVED", getCardStatusApprovedForCredit());
        assertEquals(getBankIdCreditRequestEntity(), getCreditIdOrderEntity());
        assertEquals(getCreatedCreditRequestEntity(), getCreatedOrderEntity());
        assertNull(getPaymentIdOrderEntity());
    }

    public static void checkFailureCredit() {
        assertEquals("DECLINED", getCardStatusDeclinedForCredit());
        assertEquals(getBankIdCreditRequestEntity(), getCreditIdOrderEntity());
        assertEquals(getCreatedCreditRequestEntity(), getCreatedOrderEntity());
        assertNull(getPaymentIdOrderEntity());
    }
}

