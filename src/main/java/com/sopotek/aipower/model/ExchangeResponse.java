package com.sopotek.aipower.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.stellar.sdk.Asset;

@Getter
@Setter
@AllArgsConstructor
public class ExchangeResponse {
    private String pair;
    private String status;
    private String timestamp;
    private String bid;
    private String ask;

  String flags;
Asset baseAsset;
Asset counterAsset;
double rate;
double minimumAmount;
double maximumAmount;


}
