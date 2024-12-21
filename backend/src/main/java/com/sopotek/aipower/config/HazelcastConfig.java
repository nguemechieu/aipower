package com.sopotek.aipower.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
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
            SimpleCacheManager cacheManager = new SimpleCacheManager();
            cacheManager.setCaches(List.of(new ConcurrentMapCache("users")));
            return cacheManager;
        }


}
