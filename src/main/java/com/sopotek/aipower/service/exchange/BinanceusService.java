package com.sopotek.aipower.service.exchange;

import com.sopotek.aipower.component.Binanceus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Getter
@Setter

@Service
public class BinanceusService {

    Binanceus binanceus ;
    @Autowired
    public BinanceusService(Binanceus binanceus) {
        this.binanceus = binanceus;
    }









}
