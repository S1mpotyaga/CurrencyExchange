package org.fedya.endpoints;

import lombok.RequiredArgsConstructor;
import org.fedya.endpoints.database.Currencies;
import org.fedya.endpoints.database.ExchangeRates;
import org.fedya.endpoints.dto.ExchangeRateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ExchangeRatesEndpoints {
    private final ExchangeRates exchangeRates;
    private final Currencies currencies;

    @Autowired
    public ExchangeRatesEndpoints(ExchangeRates exchangeRates, Currencies currencies) {
        this.exchangeRates = exchangeRates;
        this.currencies = currencies;
        // просто еще один удобный и понятный способ внедрения бина, по факту то
        // что генерится lombok когда ты ставишь аннотацию RequiredArgsConstructor
    }

    @GetMapping("/exchangeRates")
    public ResponseEntity<List<ExchangeRateDTO>> getAllExchangeRates() {
        List<ExchangeRateDTO> result = exchangeRates.getAllExchangeRates();
        if (result == null) {
            return ResponseEntity.status(HTTPStatus.DATABASE_NOT_AVAILABLE.getStatusCode()).build();
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/exchangeRate/{pair}")
    public ResponseEntity<ExchangeRateDTO> findExchangeRate(@PathVariable String pair) {
        if (pair.length() != 6) {
            return ResponseEntity.status(HTTPStatus.FIELD_CURRENCY_MISSING.getStatusCode()).build();
        }
        String baseCurrencyCode = pair.substring(0, 3).toUpperCase();
        String targetCurrencyCode = pair.substring(3).toUpperCase();
        int status = exchangeRates.isRegistered(baseCurrencyCode, targetCurrencyCode);
        if (status == 500) {
            return ResponseEntity.status(HTTPStatus.DATABASE_NOT_AVAILABLE.getStatusCode()).build();
        }
        if (status == 0) {
            return ResponseEntity.status(HTTPStatus.EXCHANGE_RATE_NOT_FOUND.getStatusCode()).build();
        }
        return ResponseEntity.ok(exchangeRates.findByCodesCurrency(baseCurrencyCode, targetCurrencyCode));
    }

    @PostMapping("/exchangeRates")
    public ResponseEntity<ExchangeRateDTO> addExchangeRate(@RequestParam(required = false) String baseCurrencyCode, @RequestParam(required = false) String targetCurrencyCode, @RequestParam(required = false) Double rate) {
        if (baseCurrencyCode == null || targetCurrencyCode == null || rate == null) {
            return ResponseEntity.status(HTTPStatus.FIELD_EXCHANGE_RATE_MISSING.getStatusCode()).build();
        }
        int statusExchangeRate = exchangeRates.isRegistered(baseCurrencyCode, targetCurrencyCode);
        if (statusExchangeRate == 500) {
            return ResponseEntity.status(HTTPStatus.DATABASE_NOT_AVAILABLE.getStatusCode()).build();
        }
        if (statusExchangeRate == 1) {
            return ResponseEntity.status(HTTPStatus.EXCHANGE_RATE_ALREADY_REGISTERED.getStatusCode()).build();
        }
        int statusBaseCurrency = currencies.isRegistered(baseCurrencyCode);
        int statusTargetCurrency = currencies.isRegistered(targetCurrencyCode);
        if (statusBaseCurrency == 0 || statusTargetCurrency == 0) {
            return ResponseEntity.status(HTTPStatus.CURRENCY_NOT_FOUND.getStatusCode()).build();
        }
        return ResponseEntity.ok(exchangeRates.addExchangeRate(baseCurrencyCode, targetCurrencyCode, rate));
    }
}