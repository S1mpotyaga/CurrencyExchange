package org.fedya.endpoints.database;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.fedya.endpoints.dto.CurrencyDTO;
import org.fedya.endpoints.dto.ExchangeRateDTO;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.ArrayList;

@Data
@Component
// репозиторий это про бд, в стандартной программе обычно не бывает больше одного репозитория
@RequiredArgsConstructor
public class ExchangeRates {

    private List<ExchangeRateDTO> exchangeRates = new ArrayList<>();
    private final Currencies currencies;
    // внедряется так как есть RequiredArgsConstructor и он только для поля final

    //опять же @PostConstruct
    @PostConstruct
    public void init(){
        exchangeRates.add(new ExchangeRateDTO(0, new CurrencyDTO(1, "USD", "Unated States dollar", "$"), new CurrencyDTO(3, "RUB", "Russian Ruble", "₽"), 0.99));

    }

    public List<ExchangeRateDTO> getAllExchangeRates() {
        return exchangeRates;
    }

    public int isRegistered(String baseCurrencyCode, String targetCurrencyCode) {
        if (exchangeRates == null) {
            return 500;
        }
        ExchangeRateDTO result = findByCodesCurrency(baseCurrencyCode, targetCurrencyCode);
        if (result != null) {
            return 1;
        }
        return 0;
    }

    public ExchangeRateDTO findByCodesCurrency(String baseCurrencyCode, String targetCurrencyCode) {
        for (ExchangeRateDTO current : exchangeRates) {
            if (current.getBaseCurrency().getCode().equals(baseCurrencyCode) && current.getTargetCurrency().getCode().equals(targetCurrencyCode)) {
                return current;
            }
        }
        return null;
    }

    public ExchangeRateDTO addExchangeRate(String baseCurrencyDTO, String targetCurrencyDTO, Double rate) {
        CurrencyDTO baseCurrency = currencies.findByCode(baseCurrencyDTO);
        CurrencyDTO targetCurrency = currencies.findByCode(targetCurrencyDTO);
        ExchangeRateDTO result = new ExchangeRateDTO(exchangeRates.size() + 1, baseCurrency, targetCurrency, rate);
        exchangeRates.add(result);
        return result;
    }
}
