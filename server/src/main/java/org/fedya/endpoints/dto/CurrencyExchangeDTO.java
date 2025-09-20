package org.fedya.endpoints.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyExchangeDTO {

    private CurrencyDTO baseCurrency;
    private CurrencyDTO targetCurrency;
    private Double rate;
    private Double amount;
    private Double convertedAmount;

    public CurrencyExchangeDTO(ExchangeRateDTO exchangeRateDTO, boolean flagConvert) {
        this.baseCurrency = exchangeRateDTO.getBaseCurrency();
        this.targetCurrency = exchangeRateDTO.getTargetCurrency();
        this.rate = exchangeRateDTO.getRate();
        if (flagConvert) {
            this.baseCurrency = exchangeRateDTO.getTargetCurrency();
            this.targetCurrency = exchangeRateDTO.getBaseCurrency();
            this.rate = 1 / exchangeRateDTO.getRate();
        }
    }

    public void calculateConvertAmount() {
        this.convertedAmount = this.rate * this.amount;
    }
}
