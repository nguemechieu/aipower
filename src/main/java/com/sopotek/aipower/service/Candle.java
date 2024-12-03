package com.sopotek.aipower.service;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class Candle {


    private Date time;
    private double open;
    private double high;

    public Candle() {
    }

    private double low;

    public Candle(Date time, double open, double high, double low, double close, long volume) {
        this.time = time;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.weightedAverage = (open + high + low + close) / 4;
    }

    private double close;
    private long volume;
    private double weightedAverage;
}
