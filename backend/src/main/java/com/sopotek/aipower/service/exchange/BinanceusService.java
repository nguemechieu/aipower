package com.sopotek.aipower.service.exchange;

import com.sopotek.aipower.routes.api.binanceus.BinanceUs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Getter
@Setter

@Service
public class BinanceusService {

    BinanceUs binanceus ;
    @Autowired
    public BinanceusService(BinanceUs binanceus) {
        this.binanceus = binanceus;
    }









}
