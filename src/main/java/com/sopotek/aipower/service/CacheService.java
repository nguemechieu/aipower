package com.sopotek.aipower.service;

import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CacheService {

    private final CacheManager cacheManager;

    public CacheService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void clearCache(String cacheName) {
        if (cacheManager.getCache(cacheName) != null) {
            Objects.requireNonNull(cacheManager.getCache(cacheName)).clear();
            System.out.println("Cache '" + cacheName + "' cleared.");
        } else {
            System.out.println("Cache '" + cacheName + "' does not exist.");
        }
    }
}
