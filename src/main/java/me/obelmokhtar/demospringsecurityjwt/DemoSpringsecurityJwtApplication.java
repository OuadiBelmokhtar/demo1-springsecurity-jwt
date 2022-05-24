package me.obelmokhtar.demospringsecurityjwt;

import me.obelmokhtar.demospringsecurityjwt.sec.entities.AppRole;
import me.obelmokhtar.demospringsecurityjwt.sec.entities.AppUser;
import me.obelmokhtar.demospringsecurityjwt.sec.services.UserAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;

@SpringBootApplication
public class DemoSpringsecurityJwtApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoSpringsecurityJwtApplication.class, args);
    }

    @Bean
    CommandLineRunner start(UserAccountService userAccountService) {
        return args -> {
            userAccountService.addNewRole(new AppRole(null, "USER"));
            userAccountService.addNewRole(new AppRole(null, "ADMIN"));
            userAccountService.addNewRole(new AppRole(null, "CUSTOMER_MANAGER"));
            userAccountService.addNewRole(new AppRole(null, "PRODUCT_MANAGER"));
            userAccountService.addNewRole(new AppRole(null, "BILLS_MANAGER"));

            userAccountService.addNewUser(new AppUser(null,"user1", "1234", new ArrayList<>()));
            userAccountService.addNewUser(new AppUser(null,"admin", "1234", new ArrayList<>()));
            userAccountService.addNewUser(new AppUser(null,"user2", "1234", new ArrayList<>()));
            userAccountService.addNewUser(new AppUser(null,"user3", "1234", new ArrayList<>()));
            userAccountService.addNewUser(new AppUser(null,"user4", "1234", new ArrayList<>()));

            userAccountService.addRoleToUser("USER", "user1");
            userAccountService.addRoleToUser("USER", "admin");
            userAccountService.addRoleToUser("ADMIN", "admin");
            userAccountService.addRoleToUser("USER", "user2");
            userAccountService.addRoleToUser("CUSTOMER_MANAGER", "user2");
            userAccountService.addRoleToUser("USER", "user3");
            userAccountService.addRoleToUser("PRODUCT_MANAGER", "user3");
            userAccountService.addRoleToUser("USER", "user4");
            userAccountService.addRoleToUser("BILLS_MANAGER", "user4");
        };
    }

}
