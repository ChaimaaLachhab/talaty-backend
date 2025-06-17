package com.talaty.configs;

import com.talaty.enums.ApplicationStatus;
import com.talaty.model.Admin;
import com.talaty.model.Customer;
import com.talaty.model.EKYC;
import com.talaty.model.User;
import com.talaty.repository.EKYCRepository;
import com.talaty.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EKYCRepository ekycRepository;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    public DataInitializer(UserRepository userRepository, EKYCRepository ekycRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.ekycRepository = ekycRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        initializeAdminUser();
        initializeTestCustomer();
    }

    private void initializeAdminUser() {
        if (!userRepository.findByEmail("admin@talaty.ma").isPresent()) {
            Admin admin = new Admin();
            admin.setFirstName("Admin");
            admin.setLastName("Talaty");
            admin.setUsername("admin");
            admin.setEmail("admin@talaty.ma");
            admin.setPhone("+212600000000");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmailVerified(true);
            admin.setPhoneVerified(true);
            admin.setProfileCompletion(100.0);

            userRepository.save(admin);
            logger.info("Utilisateur admin créé: admin@talaty.ma / admin123");
        }
    }

    private void initializeTestCustomer() {
        if (!userRepository.findByEmail("test@talaty.ma").isPresent()) {
            Customer customer = new Customer();
            customer.setFirstName("Chaimaa");
            customer.setLastName("LACHHAB");
            customer.setUsername("chaimaa");
            customer.setEmail("chaima.lac013@gmail.com");
            customer.setPhone("+212615084216");
            customer.setPassword(passwordEncoder.encode("test123"));
            customer.setEmailVerified(true);
            customer.setPhoneVerified(false);

            Customer savedCustomer = userRepository.save(customer);

            createTestEKYC(savedCustomer);

            logger.info("Utilisateur test créé: chaima.lac013@gmail.com / test123");
        }
    }

    private void createTestEKYC(Customer customer) {
        EKYC ekyc = new EKYC();
        ekyc.setUser(customer);
        ekyc.setNationalId("K1234567");
        ekyc.setCompanyName("TechStart SARL");
        ekyc.setBusinessSector("Technologie");
        ekyc.setBusinessPurpose("Développement logiciels");
        ekyc.setAddress("123 Rue des Entrepreneurs");
        ekyc.setCity("Casablanca");
        ekyc.setCountry("Morocco");
        ekyc.setPostalCode("20000");
        ekyc.setCompanyRegistrationNumber("RC123456");
        ekyc.setCompanyEstablishedDate(LocalDate.of(2020, 1, 15));
        ekyc.setBankAccountNumber("012-345-1300005800018000-67");
        ekyc.setBankName("Banque Populaire");
        ekyc.setMonthlyRevenue(50000.0);
        ekyc.setAnnualRevenue(600000.0);
        ekyc.setNumberOfEmployees(8);
        ekyc.setRequestedCreditAmount(200000.0);
        ekyc.setCreditPurpose("Extension des activités");
        ekyc.setStatus(ApplicationStatus.DRAFT);
        ekyc.setScore(0);
        ekyc.setMaxScore(100);

        EKYC savedEkyc = ekycRepository.save(ekyc);
        customer.setEkyc(savedEkyc);
        customer.setProfileCompletion(60.0);
        userRepository.save(customer);
    }
}

