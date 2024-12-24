package com.sopotek.aipower.routes.api;

import com.sopotek.aipower.service.CacheService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v3/cache")
public class CacheController {

    private final CacheService cacheService;

    public CacheController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @DeleteMapping("/{cacheName}")
    public String clearCache(@PathVariable String cacheName) {
        cacheService.clearCache(cacheName);
        return "Cache '" + cacheName + "' cleared.";
    }

}
