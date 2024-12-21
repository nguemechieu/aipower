package com.sopotek.aipower.component;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


import java.util.logging.Filter;
import java.util.logging.LogRecord;
@Getter
@Setter
@Component
public class LoggingFilter implements Filter {
    protected   static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);


    @Override
    public boolean isLoggable(LogRecord record) {
        return false;
    }
}