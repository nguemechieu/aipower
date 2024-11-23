package com.sopotek.aipower.network.stellar;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.ToString
@lombok.EqualsAndHashCode
public class StellarLumens {
    private String account;
    private String balance;
    private String currency;


}
