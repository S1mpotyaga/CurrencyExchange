package org.fedya.endpoints.database;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.fedya.endpoints.dto.CurrencyDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Data
@RequiredArgsConstructor
@Component
public class CurrenciesRepository {

    private volatile List<CurrencyDTO> currencies = new ArrayList<>();

    @PostConstruct
    public void init() {
        currencies.add(new CurrencyDTO(1, "USD", "United States dollar", "$"));
        currencies.add(new CurrencyDTO(2, "EUR", "Euro", "€"));
        currencies.add(new CurrencyDTO(3, "RUB", "Russian Ruble", "₽"));
    }

    public CurrencyDTO findByCode(String code) {
        for (CurrencyDTO currency : currencies) {
            if (currency.getCode().equals(code)) {
                return currency;
            }
        }
        return null;
    }

    public int isRegistered(String code) {
        if (currencies == null) {
            return 500;
        }
        int status = 0;
        for (CurrencyDTO current : currencies) {
            if (code.equals(current.getCode())) {
                status = 1;
                break;
            }
        }
        return status;
    }

    public CurrencyDTO addCurrency(CurrencyDTO currency) {
        currency.setId(currencies.size() + 1);
        currencies.add(currency);
        return currency;
    }
}
