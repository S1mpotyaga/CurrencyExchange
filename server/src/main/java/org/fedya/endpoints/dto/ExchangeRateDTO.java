package org.fedya.endpoints.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateDTO {

    private Integer id;
    private CurrencyDTO baseCurrency;
    private CurrencyDTO targetCurrency;
    private Double rate;
}