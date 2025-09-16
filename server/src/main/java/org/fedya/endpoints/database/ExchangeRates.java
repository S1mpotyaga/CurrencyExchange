package org.fedya.endpoints.database;

import lombok.Data;
import org.fedya.endpoints.dto.CurrencyDTO;
import org.fedya.endpoints.dto.ExchangeRateDTO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.ArrayList;

@Data
@Repository
public class ExchangeRates {

    private static List<ExchangeRateDTO> exchangeRates = new ArrayList<>();

    {
        exchangeRates.add(new ExchangeRateDTO(0, new CurrencyDTO(1, "USD", "Unated States dollar", "$"), new CurrencyDTO(3, "RUB", "Russian Ruble", "₽"), 0.99));
    }

    public static List<ExchangeRateDTO> getAllExchangeRates(){
        return exchangeRates;
    }
    public static int isRegistered(String baseCurrencyCode, String targetCurrencyCode) {
        if (exchangeRates == null){
            return 500;
        }
        ExchangeRateDTO result = findByCodesCurrency(baseCurrencyCode, targetCurrencyCode);
        if (result != null){
            return 1;
        }
        return 0;
    }

    public static ExchangeRateDTO findByCodesCurrency(String baseCurrencyCode, String targetCurrencyCode){
        for (ExchangeRateDTO current: exchangeRates){
            if (current.getBaseCurrency().getCode().equals(baseCurrencyCode) && current.getTargetCurrency().getCode().equals(targetCurrencyCode)){
                return current;
            }
        }
        return null;
    }

    public static ExchangeRateDTO addExchangeRate(String baseCurrencyDTO, String targetCurrencyDTO, Double rate){
        CurrencyDTO baseCurrency = Currencies.findByCode(baseCurrencyDTO);
        CurrencyDTO targetCurrency = Currencies.findByCode(targetCurrencyDTO);
        ExchangeRateDTO result = new ExchangeRateDTO(exchangeRates.size() + 1, baseCurrency, targetCurrency, rate);
        exchangeRates.add(result);
        return result;
    }
}
