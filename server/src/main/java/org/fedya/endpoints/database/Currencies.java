package org.fedya.endpoints.database;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.fedya.endpoints.dto.CurrencyDTO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


@Data
@AllArgsConstructor
@Repository
public class Currencies {

    private static List<CurrencyDTO> currencies = new ArrayList<>();

    {
        currencies.add(new CurrencyDTO(1, "USD", "United States dollar", "$"));
        currencies.add(new CurrencyDTO(2, "EUR", "Euro", "€"));
        currencies.add(new CurrencyDTO(3, "RUB", "Russian Ruble", "₽"));
    }

    public static List<CurrencyDTO> getAllCurrencies(){
        return currencies;
    }

    public static CurrencyDTO findByCode(String code){
        for (CurrencyDTO currency: currencies){
            if (currency.getCode().equals(code)){
                return currency;
            }
        }
        return null;
    }

    public static int isRegistered(String code){
        if (currencies == null){
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

    public static CurrencyDTO addCurrency(CurrencyDTO currency){
        currency.setId(currencies.size() + 1);
        currencies.add(currency);
        return currency;
    }
}
