package org.fedya.endpoints;

import lombok.AllArgsConstructor;
import org.fedya.endpoints.database.Currencies;
import org.fedya.endpoints.dto.CurrencyDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/currencies")
public class CurrenciesEndpoints {

    @GetMapping
    public ResponseEntity<List<CurrencyDTO>> getAllCurrencies() {
        List<CurrencyDTO> currencies = Currencies.getAllCurrencies();
        if (currencies == null) {
            return ResponseEntity.status(HTTPStatus.DATABASE_NOT_AVAILABLE.getStatusCode()).build();
        }
        return ResponseEntity.ok(currencies);
    }

    @GetMapping("/{code}")
    public ResponseEntity<CurrencyDTO> getCurrency(@PathVariable("code") String code) {
        CurrencyDTO currency = Currencies.findByCode(code);
        if (currency != null){
            return ResponseEntity.ok(currency);
        }
        return ResponseEntity.status(HTTPStatus.CURRENCY_NOT_FOUND.getStatusCode()).build();
    }

    @PostMapping
    public ResponseEntity<CurrencyDTO> postCurrency(@RequestParam(required = false) String name, @RequestParam(required = false) String code, @RequestParam(required = false) String sign){
        if (name == null || code == null || sign == null){
            return ResponseEntity.status(HTTPStatus.FIELD_CURRENCY_MISSING.getStatusCode()).build();
        }
        int statusRegistered = Currencies.isRegistered(code);
        if (statusRegistered == 500){
            return ResponseEntity.status(HTTPStatus.DATABASE_NOT_AVAILABLE.getStatusCode()).build();
        }
        if (statusRegistered == 1){
            return ResponseEntity.status(HTTPStatus.CURRENCY_ALREADY_REGISTERED.getStatusCode()).build();
        }
        CurrencyDTO result = Currencies.addCurrency(new CurrencyDTO(code, name, sign));
        return ResponseEntity.ok(result);
    }
}
