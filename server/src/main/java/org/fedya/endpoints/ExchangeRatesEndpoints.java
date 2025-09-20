package org.fedya.endpoints;

import org.fedya.endpoints.database.CurrenciesRepository;
import org.fedya.endpoints.database.ExchangeRateRepository;
import org.fedya.endpoints.dto.ExchangeRateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ExchangeRatesEndpoints {
    private final ExchangeRateRepository exchangeRateRepository;
    private final CurrenciesRepository currenciesRepository;

    @Autowired
    public ExchangeRatesEndpoints(ExchangeRateRepository exchangeRateRepository, CurrenciesRepository currenciesRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
        this.currenciesRepository = currenciesRepository;
    }

    @GetMapping("/exchangeRates")
    public ResponseEntity<List<ExchangeRateDTO>> getAllExchangeRates() {
        List<ExchangeRateDTO> result = exchangeRateRepository.getExchangeRates();
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
        int status = exchangeRateRepository.isRegistered(baseCurrencyCode, targetCurrencyCode);
        if (status == 500) {
            return ResponseEntity.status(HTTPStatus.DATABASE_NOT_AVAILABLE.getStatusCode()).build();
        }
        if (status == 0) {
            return ResponseEntity.status(HTTPStatus.EXCHANGE_RATE_NOT_FOUND.getStatusCode()).build();
        }
        return ResponseEntity.ok(exchangeRateRepository.findByCodes(baseCurrencyCode, targetCurrencyCode));
    }

    @PostMapping("/exchangeRates")
    public ResponseEntity<ExchangeRateDTO> addExchangeRate(@RequestParam(required = false) String baseCurrencyCode, @RequestParam(required = false) String targetCurrencyCode, @RequestParam(required = false) Double rate) {
        if (baseCurrencyCode == null || targetCurrencyCode == null || rate == null) {
            return ResponseEntity.status(HTTPStatus.FIELD_EXCHANGE_RATE_MISSING.getStatusCode()).build();
        }
        int statusExchangeRate = exchangeRateRepository.isRegistered(baseCurrencyCode, targetCurrencyCode);
        if (statusExchangeRate == 500) {
            return ResponseEntity.status(HTTPStatus.DATABASE_NOT_AVAILABLE.getStatusCode()).build();
        }
        if (statusExchangeRate == 1) {
            return ResponseEntity.status(HTTPStatus.EXCHANGE_RATE_ALREADY_REGISTERED.getStatusCode()).build();
        }
        int statusBaseCurrency = currenciesRepository.isRegistered(baseCurrencyCode);
        int statusTargetCurrency = currenciesRepository.isRegistered(targetCurrencyCode);
        if (statusBaseCurrency == 0 || statusTargetCurrency == 0) {
            return ResponseEntity.status(HTTPStatus.CURRENCY_NOT_FOUND.getStatusCode()).build();
        }
        return ResponseEntity.ok(exchangeRateRepository.addExchangeRate(baseCurrencyCode, targetCurrencyCode, rate));
    }

    @PatchMapping("/exchangeRate/{pair}")
    public ResponseEntity<ExchangeRateDTO> changeRate(@PathVariable String pair, @RequestParam(required = false) Double rate) {
        if (rate == null) {
            return ResponseEntity.status(HTTPStatus.FIELD_EXCHANGE_RATE_MISSING.getStatusCode()).build();
        }
        String baseCurrency = pair.substring(0, 3);
        String targetCurrency = pair.substring(3, 6);
        ExchangeRateDTO exchangeRate = exchangeRateRepository.changeRate(baseCurrency, targetCurrency, rate);
        if (exchangeRate == null) {
            return ResponseEntity.status(HTTPStatus.EXCHANGE_RATE_NOT_FOUND.getStatusCode()).build();
        }
        return ResponseEntity.ok(exchangeRate);
    }

    @GetMapping("/exchange")
    public ResponseEntity<?> currencyExchange(@RequestParam(required = false) String from, @RequestParam(required = false) String to, @RequestParam(required = false) Double amount) {
        if (from == null || to == null || amount == null) {
            return ResponseEntity.status(HTTPStatus.FIELD_EXCHANGE_RATE_MISSING.getStatusCode()).body(HTTPStatus.getJSONMessage(HTTPStatus.FIELD_EXCHANGE_RATE_MISSING));
        }
        Object result = exchangeRateRepository.calculate(from, to, amount);
        if (result instanceof HTTPStatus tmp) {
            return ResponseEntity.status(tmp.getStatusCode()).body(tmp.getStatusMessage());
        }
        return ResponseEntity.ok(result);
    }
}