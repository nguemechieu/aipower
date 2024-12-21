package com.sopotek.aipower.service;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CacheService {

    private static  final Log LOG = LogFactory.getLog(CacheService.class);
    private final CacheManager cacheManager;

    public CacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void clearCache(String cacheName) {
        if (cacheManager.getCache(cacheName) != null) {
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
            LOG.info("Cache '" + cacheName + "' cleared.");
        } else {
            LOG.info("Cache '" + cacheName + "' does not exist.");
        }
    }
}
