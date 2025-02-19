package com.sopotek.aipower.config;

import com.sopotek.aipower.domain.Role;
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
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

@Setter
@Getter
@Configuration
public class AdminServerConfig {

    private static final Logger LOG = LoggerFactory.getLogger(AdminServerConfig.class);
@Autowired
    public AdminServerConfig(@NotNull InstanceRepository ree, UserRepository userRepository, ApiMediaTypeHandler apiMediaTypeHandler, InstanceWebClient instanceWebClient) {
        this.userRepository = userRepository;
        this.apiMediaTypeHandler = apiMediaTypeHandler;
        this.instanceWebClient = instanceWebClient;
        this.rre = ree;
//        User admin = buildAdminUser();
//        admin.setFailedLoginAttempts(0);
//        admin.setResetToken("wertyu");
//
//        userRepository.saveOrUpdate(admin);
    }

    /**
     * Builds an admin user object based on the environment settings.
     */

    private @NotNull User buildAdminUser() {


        Role role = new Role();
        role.setRoleId(1L);
        role.setRoleName("ADMIN");


        User user = new User();
        user.setUsername("Admin");
        user.setPassword("Admin123");
        user.setFirstName("Admin");
        user.setLastName("Admin");
        user.setEmail("nguemechieu@live.com");
        user.setBirthdate("04/03/1990");
        user.setGender("Male");
        user.setBio("Administrator");
        user.setCountry("France");
        user.setPhoneNumber("+33612345678");
        user.setSecurityQuestion("What is your favorite color?");
        user.setSecurityAnswer("Blue");
        Role role2 = new Role();
        role2.setRoleId(2L);
        role2.setRoleName("USER");
        user.setRoles(Set.of(role, role2));
        user.setAddress("123 rue de la ville");
        user.setCity("Paris");
        user.setZipCode("75008");

        return user;
    }
    InstanceWebClient instanceWebClient;
    UserRepository userRepository;
    ApiMediaTypeHandler apiMediaTypeHandler;
//    private final String ADMIN_SERVER_URL = "http://localhost:8080";

    @Bean
    public Runtime runtimeShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> LOG.info("Shutting down application...")));
        return Runtime.getRuntime();
    }



    @Bean
    public InfoUpdateTrigger infoUpdateTrigger(

                                               InstanceRepository repository) {
        // Initialize InfoUpdater with the repository, instanceWebClient, and apiMediaTypeHandler
        InfoUpdater infoUpdater = new InfoUpdater(repository, instanceWebClient, apiMediaTypeHandler);
        return new InfoUpdateTrigger(infoUpdater, Flux.empty(), Duration.ZERO,Duration.ZERO,Duration.ofDays(7));
            }


    private void handleUserLoginAttempts(@NotNull Instance instance) {
        String username = instance.getRegistration().getName();
        Optional <User>user = userRepository.findByUsername(username);

       if (user.isPresent()) {
           if (user.get().getFailedLoginAttempts() >= 5) {
               user.get().setFailedLoginAttempts(0);
               user.get().setResetToken(null);
               userRepository.saveOrUpdate(user.get());
               LOG.info("Reset failed login attempts and token for user: {}", username);
           }
       }else
           LOG.info("User: {} not found", username);


    }

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
            LOG.info("Updating info for instance: {}", instance.getId());
        });
        return scheduler;
    }
}
