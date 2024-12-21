package com.sopotek.aipower.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

@Component
public class LogCleanupService {

    private static final Logger logger = Logger.getLogger(LogCleanupService.class.getName());

    @Value("${log.cleanup.file.path}")
    private String logFilePath;

    @Value("${log.cleanup.size.threshold}")
    private long sizeThreshold;

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanUpLogs() {
        File logFile = new File(logFilePath);

        if (logFile.exists() && logFile.isFile()) {
            long fileSize = logFile.length();

            if (fileSize > sizeThreshold) {
                try {
                    new FileWriter(logFilePath, false).close();
                    logger.info("Log file truncated successfully: " + logFilePath);
                } catch (IOException e) {
                    logger.severe("Failed to clean up log file: " + e.getMessage());
                }
            } else {
                logger.info("Log file size is within the threshold: " + fileSize + " bytes.");
            }
        } else {
            logger.warning("Log file does not exist: " + logFilePath);
        }
    }
}
