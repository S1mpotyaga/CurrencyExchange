package org.fedya.endpoints.database;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.fedya.endpoints.dto.CurrencyDTO;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


@Data
@RequiredArgsConstructor
//ставим конструктор только на обящательные поля, иначе спринг будет тебовать внедрить бин List<CurrencyDTO> currencies
@Component
// репозиторий ставится только для бд, здесь с позиции логики программы это никак не влияет
// но программисты не поймут тебя
public class Currencies {

    private List<CurrencyDTO> currencies = new ArrayList<>();

    // для инициализации один раз, как в твоем случае используют аннотацию @PostConstruct нгад методом
    // наша задача в спринге все что можно передавать ему на контроль, и желательно сделать лист потокобезопасным
    @PostConstruct
    public void init() {
        currencies.add(new CurrencyDTO(1, "USD", "United States dollar", "$"));
        currencies.add(new CurrencyDTO(2, "EUR", "Euro", "€"));
        currencies.add(new CurrencyDTO(3, "RUB", "Russian Ruble", "₽"));

    }

    public List<CurrencyDTO> getAllCurrencies() {
        return currencies;
        //поставил @Data => есть Getter, но если поле не статик)
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
