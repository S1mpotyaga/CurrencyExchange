package org.fedya.endpoints;

import org.fedya.endpoints.database.Currencies;
import org.fedya.endpoints.database.ExchangeRates;
import org.fedya.endpoints.dto.ExchangeRateDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ExchangeRatesEndpoints {

    @GetMapping("/exchangeRates")
    public ResponseEntity<List<ExchangeRateDTO>> getAllExchangeRates(){
        List<ExchangeRateDTO> result = ExchangeRates.getAllExchangeRates();
        if (result == null){
            return ResponseEntity.status(HTTPStatus.DATABASE_NOT_AVAILABLE.getStatusCode()).build();
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/exchangeRate/{pair}")
    public ResponseEntity<ExchangeRateDTO> findExchangeRate(@PathVariable String pair){
        if (pair.length() != 6) {
            return ResponseEntity.status(HTTPStatus.FIELD_CURRENCY_MISSING.getStatusCode()).build();
        }
        String baseCurrencyCode = pair.substring(0,3).toUpperCase();
        String targetCurrencyCode = pair.substring(3).toUpperCase();
        int status = ExchangeRates.isRegistered(baseCurrencyCode, targetCurrencyCode);
        if (status == 500){
            return ResponseEntity.status(HTTPStatus.DATABASE_NOT_AVAILABLE.getStatusCode()).build();
        }
        if (status == 0){
            return ResponseEntity.status(HTTPStatus.EXCHANGE_RATE_NOT_FOUND.getStatusCode()).build();
        }
        return ResponseEntity.ok(ExchangeRates.findByCodesCurrency(baseCurrencyCode, targetCurrencyCode));
    }

    @PostMapping("/exchangeRates")
    public ResponseEntity<ExchangeRateDTO> addExchangeRate(@RequestParam(required = false) String baseCurrencyCode, @RequestParam(required = false) String targetCurrencyCode, @RequestParam(required = false) Double rate){
        if (baseCurrencyCode == null || targetCurrencyCode == null || rate == null){
            return ResponseEntity.status(HTTPStatus.FIELD_EXCHANGE_RATE_MISSING.getStatusCode()).build();
        }
        int statusExchangeRate = ExchangeRates.isRegistered(baseCurrencyCode, targetCurrencyCode);
        if (statusExchangeRate == 500){
            return ResponseEntity.status(HTTPStatus.DATABASE_NOT_AVAILABLE.getStatusCode()).build();
        }
        if (statusExchangeRate == 1){
            return ResponseEntity.status(HTTPStatus.EXCHANGE_RATE_ALREADY_REGISTERED.getStatusCode()).build();
        }
        int statusBaseCurrency = Currencies.isRegistered(baseCurrencyCode);
        int statusTargetCurrency = Currencies.isRegistered(targetCurrencyCode);
        if (statusBaseCurrency == 0 || statusTargetCurrency == 0){
            return ResponseEntity.status(HTTPStatus.CURRENCY_NOT_FOUND.getStatusCode()).build();
        }
        return ResponseEntity.ok(ExchangeRates.addExchangeRate(baseCurrencyCode, targetCurrencyCode, rate));
    }
}