package com.talaty.configs;

import com.talaty.model.Admin;
import com.talaty.model.User;
import com.talaty.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByEmail("admin@gmail.com")) {
            User admin = new Admin();
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("admin1234"));
            admin.setUsername("admin");
            admin.setFirstName("Super");
            admin.setLastName("Admin");
            admin.setVerified(true);
            userRepository.save(admin);
        }
    }
}

