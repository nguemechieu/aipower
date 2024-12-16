package com.sopotek.aipower.config;

import com.sopotek.aipower.service.UserService;
import de.codecentric.boot.admin.server.config.AdminServerProperties;
import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.events.InstanceEvent;
import de.codecentric.boot.admin.server.domain.values.InstanceId;
import de.codecentric.boot.admin.server.services.ApiMediaTypeHandler;
import de.codecentric.boot.admin.server.services.InfoUpdateTrigger;
import de.codecentric.boot.admin.server.services.InfoUpdater;
import de.codecentric.boot.admin.server.web.client.InstanceWebClient;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.function.BiFunction;

@Configuration
public class AdminServerConfig {
UserService userService;
@Autowired
    public AdminServerConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public Runtime getRuntimeShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down application...");
        }));
        return Runtime.getRuntime();
    }
    @Bean
    public InfoUpdateTrigger infoUpdateTrigger(AdminServerProperties adminServerProperties, TaskScheduler taskScheduler) {
        // Define a Flux Publisher for demonstration
        Publisher<InstanceEvent> instanceEventPublisher = Flux.empty();

        // ApiMediaTypeHandler initialization
        ApiMediaTypeHandler apiMediaTypeHandler = new ApiMediaTypeHandler();


        InstanceRepository repository = new InstanceRepository() {
            @Contract(pure = true)
            @Override
            public @NotNull Mono<Instance> save(@NotNull Instance app) {
                return Mono.empty(); // Replace with your actual implementation
            }

            @Contract(pure = true)
            @Override
            public @NotNull Flux<Instance> findAll() {
                return Flux.empty(); // Replace with your actual implementation
            }

            @Contract(pure = true)
            @Override
            public @NotNull Mono<Instance> find(@NotNull InstanceId id) {
                return Mono.empty(); // Replace with your actual implementation
            }

            @Override
            public @NotNull Flux<Instance> findByName(@NotNull String name) {
                return Flux.empty(); // Replace with your actual implementation
            }

            @Override
            public @NotNull Mono<Instance> compute(@NotNull InstanceId id, @NotNull BiFunction<InstanceId, Instance, Mono<Instance>> remappingFunction) {
                return Mono.empty(); // Replace with your actual implementation
            }

            @Override
            public @NotNull Mono<Instance> computeIfPresent(@NotNull InstanceId id, BiFunction<InstanceId, Instance, Mono<Instance>> remappingFunction) {
                return Mono.empty(); // Replace with your actual implementation
            }
        };

        // Initialize InstanceWebClient
        InstanceWebClient instanceWebClient = InstanceWebClient.builder().build();

        // Create InfoUpdater
        InfoUpdater infoUpdater = new InfoUpdater(
                repository,
                instanceWebClient,
                apiMediaTypeHandler
        );

        // Create and return the InfoUpdateTrigger bean
        return new InfoUpdateTrigger(
                infoUpdater,
                instanceEventPublisher,
                adminServerProperties.getMonitor().getInfoMaxBackoff(),
                Duration.ofSeconds(30), // Update interval
                adminServerProperties.getMonitor().getInfoLifetime()
        );
    }

    @Bean
    public TaskScheduler taskScheduler() {
        // Define a TaskScheduler for scheduling periodic updates
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(5);
        taskScheduler.setThreadNamePrefix("InfoUpdateTriggerScheduler-");
        taskScheduler.initialize();
        return taskScheduler;
    }
}
