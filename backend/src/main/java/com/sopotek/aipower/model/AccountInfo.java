package com.sopotek.aipower.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountInfo {
    private boolean canTrade;
    private boolean canWithdraw;
    private boolean canDeposit;
    private List<Balance> balances;

    @Getter
    @Setter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Balance {
        private String asset;
        private String free;
        private String locked;
    }
}
