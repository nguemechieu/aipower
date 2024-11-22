package com.sopotek.aipower.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.MetricsJmxConfig;
import com.hazelcast.config.MetricsManagementCenterConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@EnableCaching
public class HazelcastConfig {

    public HazelcastConfig() {
        // Constructor logic if required (optional)
    }

    @Bean
    public HazelcastInstance hazelcastInstance() {
        // Configure Hazelcast instance
        Config config = new Config();
        config.setClusterName("dev");

        // Configure network settings
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(true);

        // Configure CP subsystem
        config.getCPSubsystemConfig().setCPMemberCount(3);
        config.getJetConfig().setEnabled(true);
        // Enable debug metrics
        MetricsManagementCenterConfig managementCenterConfig = new MetricsManagementCenterConfig();
        managementCenterConfig.setEnabled(true);
        managementCenterConfig.setRetentionSeconds(60); // Retain metrics for 60 seconds
        config.getMetricsConfig().setManagementCenterConfig(managementCenterConfig);

        config.getMetricsConfig().setEnabled(true);
        MetricsJmxConfig jmxConfig = new MetricsJmxConfig();
        jmxConfig.setEnabled(true);
        config.getMetricsConfig().setJmxConfig(jmxConfig);

        // Create and return Hazelcast instance
        return Hazelcast.newHazelcastInstance(config);
    }



        @Bean
        public CacheManager cacheManager() {



            SimpleCacheManager cacheManager = new SimpleCacheManager();
            cacheManager.setCaches(List.of(new ConcurrentMapCache("users")));
            return cacheManager;
        }


}
