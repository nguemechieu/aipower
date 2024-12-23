package com.sopotek.aipower.domain;

public enum ENUM_SIGNAL  {
    BUY,SELL,HOLD;

    public String get(String action) {
        return ENUM_SIGNAL.valueOf(action.toUpperCase()).name();
    }
}
