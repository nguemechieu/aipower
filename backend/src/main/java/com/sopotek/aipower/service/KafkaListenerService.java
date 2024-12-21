package com.sopotek.aipower.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaListenerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaListenerService.class);

    @KafkaListener(topics = "test-topic", groupId = "test-group")
    public void listen(String message) {
        logger.info("Received message: {}", message);
    }


}
