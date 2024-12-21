package com.sopotek.aipower.config;

import com.sopotek.aipower.model.User;
import com.sopotek.aipower.repository.UserRepository;
import de.codecentric.boot.admin.server.config.AdminServerProperties;
import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.domain.values.InstanceId;
import de.codecentric.boot.admin.server.domain.values.Registration;
import de.codecentric.boot.admin.server.services.ApiMediaTypeHandler;
import de.codecentric.boot.admin.server.services.InfoUpdateTrigger;
import de.codecentric.boot.admin.server.services.InfoUpdater;
import de.codecentric.boot.admin.server.web.client.InstanceWebClient;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
@Setter
@Getter
@Configuration
public class AdminServerConfig {

    private static final Log LOG = LogFactory.getLog(AdminServerConfig.class);

    private  UserRepository userRepository;
    private ApiMediaTypeHandler apiMediaTypeHandler;

    @Autowired
    public AdminServerConfig(UserRepository userRepository, ApiMediaTypeHandler apiMediaTypeHandler,
                             InstanceWebClient instanceWebClient
                            ) {
        this.userRepository = userRepository;
        this.apiMediaTypeHandler = apiMediaTypeHandler;
        this.instanceWebClient = instanceWebClient;
        // Initialize instanceWebClient with the provided instanceWebClient

    }

    @Bean
    public Runtime runtimeShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> LOG.info("Shutting down application...")));
        return Runtime.getRuntime();
    }
    InstanceWebClient instanceWebClient;
    @Bean
    public InfoUpdateTrigger infoUpdateTrigger(AdminServerProperties adminServerProperties,

                                               InstanceRepository repository) {
        // Initialize InfoUpdater with the repository, instanceWebClient, and apiMediaTypeHandler

        InfoUpdater infoUpdater = new InfoUpdater(repository, instanceWebClient, apiMediaTypeHandler);
        // Schedule the infoUpdaters to run every 30 seconds
        // This is a placeholder for periodic updates, you might want to use a more suitable scheduling mechanism for your application
        // For example, using a Quartz scheduler or a Cron expression for scheduling the taskScheduler.schedule method
        // This method will run the infoUpdaters every 30 seconds and handle any errors that may occur during the update process
        // For example, by logging the error and then retrying the update after a backoff period.
        // You could also use a more advanced scheduling mechanism like the Spring Cloud Task Scheduler or a message broker like RabbitMQ to handle the periodic updates.

        // Handle user login attempts and update the failedLoginAttempts field in the UserRepository
        // This is a placeholder for user login attempts and you might want to use a more suitable authentication mechanism for your application
        // For example, using a JWT or OAuth2 authentication system to verify user credentials and track failed login attempts.
        // You could also use a more advanced authentication mechanism like Spring Security or a message broker like RabbitMQ to handle user login attempts.
        return new InfoUpdateTrigger(
                infoUpdater,
                Flux.empty(),  // Placeholder for the Flux to track the updates
                adminServerProperties.getMonitor().getInfoMaxBackoff(), // Max backoff duration from adminServerProperties
                Duration.ofSeconds(30), // Static backoff duration of 30 seconds for retries
                adminServerProperties.getMonitor().getInfoLifetime() // Info lifetime duration from adminServerProperties
        );

            }


    @Contract(value = " -> new", pure = true)
    private @NotNull InstanceRepository createInstanceRepository() {
        return new InstanceRepository() {
            @Override
            public @NotNull Mono<Instance> save(@NotNull Instance instance) {
                return Mono.fromCallable(() -> {
                    handleUserLoginAttempts(instance);
                    return instance;
                }).doOnError(error -> LOG.error("Error saving instance: " + error.getMessage(), error));
            }


            @Override
            public @NotNull Flux<Instance> findAll() {
                // Fetch all users from the UserRepository
                List<User> users = userRepository.findAll(); // Assuming userService extends JpaRepository<User, Long>

                // Map users to instances
                List<Instance> instances = users.stream()
                        .map(this::mapUserToInstance) // Map each user to an Instance
                        .toList();

                // Return the instances as a Flux
                return Flux.fromIterable(instances);
            }

            private Instance mapUserToInstance(Object user) {
                Instance instance = Instance.create(InstanceId.of(String.valueOf(((User) user).getId())));

                Registration.Builder register = Registration.builder();
                User userObj = (User) user; // Avoid repetitive casting

                register.name(userObj.getUsername());
                register.metadata(Map.of(
                        "email", userObj.getEmail(),
                        "password", userObj.getPassword(),
                        "failedLoginAttempts", String.valueOf(userObj.getFailedLoginAttempts()),
                        "resetToken", userObj.getResetToken(),
                        "fullName", userObj.getFullName()
                ));
                register.managementUrl("http://localhost:8080/actuator/health"); // Replace with the actual management URL
                instance = instance.register(register.build());
                return instance;
            }




            @Override
            public @NotNull Mono<Instance> find(@NotNull InstanceId id) {
                // Fetch user from the UserRepository by username derived from the InstanceId
                return Mono.fromCallable(() -> userRepository.findByUsername(id.getValue())) // Assuming id.getValue() returns username
                        .subscribeOn(Schedulers.boundedElastic()) // Run the blocking call on a dedicated thread pool
                        .flatMap(userOptional -> userOptional.map(user -> Mono.just(mapUserToInstance(user)))
                                .orElseGet(Mono::empty));
            }


            @Override
            public @NotNull Flux<Instance> findByName(@NotNull String name) {
                // Execute the blocking call on a separate thread pool to avoid blocking the main event loop
                return Mono.defer(() -> Mono.fromCallable(() -> userRepository.findByUsername(name))) // Wrap the blocking call
                        .subscribeOn(Schedulers.boundedElastic()) // Ensure the blocking operation runs on a dedicated thread pool
                        .flatMapMany(optionalUser ->
                                optionalUser.map(user -> {
                                    // Map User to Instance
                                    Instance instance = mapUserToInstance(user);
                                    return Flux.just(instance); // Return as a Flux of Instance
                                }).orElseGet(Flux::empty) // If no user found, return an empty Flux
                        );
            }
            @Override
            public @NotNull Mono<Instance> compute(@NotNull InstanceId id, @NotNull BiFunction<InstanceId, Instance, Mono<Instance>> remappingFunction) {
                // Proceed with further processing if needed, otherwise return the result
                // Return the remapped instance
                return Mono.defer(() -> {
                            // Find the instance by ID, assuming `findInstanceById` retrieves it from a database
                            return findInstanceById(id)
                                    .mapNotNull(instance -> {
                                        // Apply the remapping function to the instance, wrapping it in a Mono
                                        return remappingFunction.apply(id, instance).block(); // Apply remapping function (blocking in a bounded elastic thread)
                                    })
                                    .onErrorResume(e -> {
                                        // Handle the case where instance is not found or any other error
                                        return Mono.empty(); // Return empty Mono if no instance is found or error occurs
                                    });
                        })
                        .subscribeOn(Schedulers.boundedElastic()) // Ensure blocking operations run on a non-blocking thread pool
                        .flatMap(Mono::just);
            }





            @Override
            public @NotNull Mono<Instance> computeIfPresent(@NotNull InstanceId id, @NotNull BiFunction<InstanceId, Instance, Mono<Instance>> remappingFunction) {
                // Retrieve the instance by its ID
                return findInstanceById(id)
                        .flatMap(existingInstance -> {
                            // Apply the remapping function if the instance exists
                            return remappingFunction.apply(id, existingInstance);
                        })
                        .switchIfEmpty(Mono.empty()); // Return Mono.empty() if the instance doesn't exist
            }

        };
    }

    private Mono<Instance> findInstanceById(@NotNull InstanceId id) {
        return Mono.fromCallable(() -> userRepository.findByUsername(id.getValue()))
                .subscribeOn(Schedulers.boundedElastic()) // Ensure blocking operations run on a non-blocking thread pool
                .flatMap(optionalUser -> optionalUser
                        .map(user -> Mono.just(mapUserToInstance(user)))
                        .orElseGet(Mono::empty));
    }

    private Instance mapUserToInstance(Object user) {
        // Create a new Instance object and set its properties based on the User
        Instance instance = Instance.create(InstanceId.of(String.valueOf(((User) user).getId())));

        Registration.Builder register = Registration.builder();
        register.name(((User) user).getUsername());
        register.metadata(Map.of("email", ((User) user).getEmail()));
        register.managementUrl(
                "http://localhost:8080/actuator/health"); // Replace with the actual management URL);
        register.metadata(Map.of("password", ((User) user).getPassword()));
        register.metadata(Map.of("failedLoginAttempts", String.valueOf(((User) user).getFailedLoginAttempts())));
        register.metadata(Map.of("resetToken", ((User) user).getResetToken()));
        register.metadata(Map.of("fullName", ((User) user).getFullName()));
        return instance.register(register.build());
    }

    private void handleUserLoginAttempts(Instance instance) {
        String username = instance.getRegistration().getName();
        userRepository.findByUsername(username).ifPresent(user -> {
            if (user.getFailedLoginAttempts() >= 5) {
                user.setFailedLoginAttempts(0);
                user.setResetToken(null);
                userRepository.save(user);
                LOG.info("Reset failed login attempts and token for user: " + username);
            }
        });
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(Runtime.getRuntime().availableProcessors() * 2);
        scheduler.setThreadNamePrefix("InfoUpdateTriggerScheduler-");
        scheduler.initialize();
        return scheduler;
    }
}
