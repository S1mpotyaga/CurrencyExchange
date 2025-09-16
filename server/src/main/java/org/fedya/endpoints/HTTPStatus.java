package org.fedya.endpoints;


import lombok.Getter;

@Getter
public enum HTTPStatus {

    SUCCESS(200, "Request completed successfully."),
    DATABASE_NOT_AVAILABLE(500, "Database is not available."),
    CURRENCY_NOT_FOUND(404, "Currency is not found."),
    FIELD_CURRENCY_MISSING(400, "Currency field missing."),
    CURRENCY_ALREADY_REGISTERED(409, "Currency is already registered."),
    EXCHANGE_RATE_NOT_FOUND(404, "Exchange rate not found."),
    FIELD_EXCHANGE_RATE_MISSING(400, "Exchange rate field is missing."),
    EXCHANGE_RATE_ALREADY_REGISTERED(409, "Exchange rate is already registered.");


    private int statusCode;
    private String statusMessage;

    HTTPStatus(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }
}
