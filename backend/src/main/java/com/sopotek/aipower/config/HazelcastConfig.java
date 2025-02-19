package com.sopotek.aipower.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Configuration
@EnableCaching
public class HazelcastConfig {

    ConcurrentMapCacheManager cacheManager;

    public HazelcastConfig() {
        // Constructor logic if required (optional)
    }


    @Bean
    public HazelcastInstance hazelcastInstance() {
        Config config = new Config();
        config.getCPSubsystemConfig().setCPMemberCount(0); // Disable CP subsystem
        config.setClusterName("dev-cluster");

        JoinConfig joinConfig = config.getNetworkConfig().getJoin();
        joinConfig.getMulticastConfig().setEnabled(false);
        joinConfig.getTcpIpConfig().setEnabled(true).addMember("127.0.0.1");

        return Hazelcast.newHazelcastInstance(config);
    }
    @Bean
    public CacheManager cacheManager() {
         cacheManager = new ConcurrentMapCacheManager("users");
        Collection<String> cashes= new ArrayList<>();
        cashes.add("users");
        cashes.add("products");
        cashes.add("roles");
        cashes.add("trades");
        cacheManager.setCacheNames(cashes);
        return cacheManager;
    }










}