package org.fedya.endpoints.database;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.fedya.endpoints.HTTPStatus;
import org.fedya.endpoints.dto.CurrencyDTO;
import org.fedya.endpoints.dto.CurrencyExchangeDTO;
import org.fedya.endpoints.dto.ExchangeRateDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ArrayList;

@Data
@Component
@RequiredArgsConstructor
public class ExchangeRateRepository {

    private List<ExchangeRateDTO> exchangeRates = new ArrayList<>();
    private final CurrenciesRepository currenciesRepository;

    @PostConstruct
    public void init() {
        exchangeRates.add(new ExchangeRateDTO(0, new CurrencyDTO(1, "USD", "Unated States dollar", "$"), new CurrencyDTO(3, "RUB", "Russian Ruble", "₽"), 0.01));
        exchangeRates.add(new ExchangeRateDTO(1, new CurrencyDTO(1, "USD", "Unated States dollar", "$"), new CurrencyDTO(2, "EUR", "Euro", "€"), 1.5));
    }

    public int isRegistered(String baseCurrencyCode, String targetCurrencyCode) {
        if (exchangeRates == null) {
            return 500;
        }
        ExchangeRateDTO result = findByCodes(baseCurrencyCode, targetCurrencyCode);
        if (result != null) {
            return 1;
        }
        return 0;
    }

    public ExchangeRateDTO findByCodes(String baseCurrencyCode, String targetCurrencyCode) {
        for (ExchangeRateDTO current : exchangeRates) {
            if (current.getBaseCurrency().getCode().equals(baseCurrencyCode) && current.getTargetCurrency().getCode().equals(targetCurrencyCode)) {
                return current;
            }
        }
        return null;
    }

    public ExchangeRateDTO addExchangeRate(String baseCurrencyDTO, String targetCurrencyDTO, Double rate) {
        CurrencyDTO baseCurrency = currenciesRepository.findByCode(baseCurrencyDTO);
        CurrencyDTO targetCurrency = currenciesRepository.findByCode(targetCurrencyDTO);
        ExchangeRateDTO result = new ExchangeRateDTO(exchangeRates.size() + 1, baseCurrency, targetCurrency, rate);
        exchangeRates.add(result);
        return result;
    }

    public ExchangeRateDTO changeRate(String baseCurrency, String targetCurrency, Double rate) {
        ExchangeRateDTO exchangeRate = findByCodes(baseCurrency, targetCurrency);
        if (exchangeRate == null) {
            return null;
        }
        exchangeRate.setRate(rate);
        return exchangeRate;
    }

    public Object calculate(String from, String to, Double amount) {
        ExchangeRateDTO fromTo = findByCodes(from, to);
        if (fromTo != null) {
            return convertAmount(fromTo, amount, false);
        }
        ExchangeRateDTO toFrom = findByCodes(to, from);
        if (toFrom != null) {
            return convertAmount(toFrom, amount, true);
        }
        ExchangeRateDTO usdFrom = findByCodes("USD", from);
        ExchangeRateDTO usdTo = findByCodes("USD", to);
        if (usdFrom != null && usdTo != null) {
            return convertAmountFromUSD(usdFrom, usdTo, amount);
        }
        return HTTPStatus.getJSONMessage(HTTPStatus.EXCHANGE_RATE_NOT_FOUND);
    }

    private CurrencyExchangeDTO convertAmountFromUSD(ExchangeRateDTO usdFrom, ExchangeRateDTO usdTo, Double amount) {
        ExchangeRateDTO fromTo = new ExchangeRateDTO(-1, usdFrom.getTargetCurrency(), usdTo.getTargetCurrency(), convertRate(usdFrom.getRate()) * usdTo.getRate());
        return convertAmount(fromTo, amount, false);
    }

    private CurrencyExchangeDTO convertAmount(ExchangeRateDTO exchangeRate, Double amount, boolean flagConevrtRate) {
        CurrencyExchangeDTO result = new CurrencyExchangeDTO(exchangeRate, flagConevrtRate);
        result.setAmount(amount);
        result.calculateConvertAmount();
        return result;
    }

    private Double convertRate(Double currentRate) {
        return 1 / currentRate;
    }
}