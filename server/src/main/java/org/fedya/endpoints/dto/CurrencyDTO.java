package org.fedya.endpoints.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class CurrencyDTO {

    private Integer id;
    private String code;
    private String name;
    private String sign;

    public CurrencyDTO(String code, String name, String sign){
        id = null;
        this.name = name;
        this.code = code;
        this.sign = sign;
    }
}