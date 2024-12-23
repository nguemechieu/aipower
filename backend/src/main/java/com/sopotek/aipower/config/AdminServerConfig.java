package com.sopotek.aipower.config;

import com.sopotek.aipower.domain.User;
import com.sopotek.aipower.repository.UserRepository;
import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.domain.entities.InstanceRepository;
import de.codecentric.boot.admin.server.services.ApiMediaTypeHandler;
import de.codecentric.boot.admin.server.services.InfoUpdateTrigger;
import de.codecentric.boot.admin.server.services.InfoUpdater;
import de.codecentric.boot.admin.server.web.client.InstanceWebClient;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Optional;

@Setter
@Getter
@Configuration
public class AdminServerConfig {

    private static final Log LOG = LogFactory.getLog(AdminServerConfig.class);
@Autowired
    public AdminServerConfig(UserRepository userRepository, ApiMediaTypeHandler apiMediaTypeHandler, InstanceWebClient instanceWebClient) {
        this.userRepository = userRepository;
        this.apiMediaTypeHandler = apiMediaTypeHandler;
        this.instanceWebClient = instanceWebClient;
    }


    private  UserRepository userRepository;
    private ApiMediaTypeHandler apiMediaTypeHandler;


    @Bean
    public Runtime runtimeShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> LOG.info("Shutting down application...")));
        return Runtime.getRuntime();
    }
    InstanceWebClient instanceWebClient;
    @Bean
    public InfoUpdateTrigger infoUpdateTrigger(

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
                Flux.empty(), Duration.ZERO,Duration.ZERO,Duration.ofDays(7));


            }


    private void handleUserLoginAttempts(Instance instance) {
        String username = instance.getRegistration().getName();
        Optional <User>user = userRepository.findByUsername(username);
       if (user.isPresent()) {


           if (user.get().getFailedLoginAttempts() >= 5) {
               user.get().setFailedLoginAttempts(0);
               user.get().setResetToken(null);
               userRepository.saveOrUpdate(user.get());
               LOG.info("Reset failed login attempts and token for user: " + username);
           }
       }
    }
@Autowired
    @NotNull InstanceRepository rre ;
    @Bean
    public TaskScheduler taskScheduler() {


        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(Runtime.getRuntime().availableProcessors() * 2);
        scheduler.setThreadNamePrefix("InfoUpdateTriggerScheduler-");
        scheduler.initialize();
        rre.findAll().subscribe(instance -> {
            handleUserLoginAttempts(instance);
            // Perform additional tasks here if needed (e.g., sending email, updating database, etc.)
            LOG.info("Updating info for instance: " + instance.getId());
        });
        return scheduler;
    }
}
