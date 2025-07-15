package com.digitalwallet.config;

import com.digitalwallet.entity.AppUser;
import com.digitalwallet.repository.AppUserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration

public class AdminInitializer {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(AppUserRepository appUserRepository, PasswordEncoder passwordEncoder) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Bean
    public CommandLineRunner initAdminUser() {
        return args -> {
            String defaultAdminUsername = "admin";

            if (appUserRepository.findByUsername(defaultAdminUsername).isEmpty()) {
                AppUser admin =
                        new AppUser(null, defaultAdminUsername, passwordEncoder.encode("admin123"), "EMPLOYEE", null, null);


                appUserRepository.save(admin);
                System.out.println("Default admin user created: username=admin, password=admin123");
            }
        };
    }
}
