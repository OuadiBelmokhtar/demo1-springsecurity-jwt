package me.obelmokhtar.demospringsecurityjwt;

import me.obelmokhtar.demospringsecurityjwt.sec.entities.AppRole;
import me.obelmokhtar.demospringsecurityjwt.sec.entities.AppUser;
import me.obelmokhtar.demospringsecurityjwt.sec.services.UserAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

@SpringBootApplication
/* - Activer la definition des autorisations d acces aux ressources sous forme des @nnotations.
   - Cela permet d'utiliser les @nnotations pour definir des autorisations d acces aux méthodes qlq
     soit leurs emplacements(ds la couche service ou web/rest).
   - prePostEnabled = true: enables Spring Security @PreAuthorize and @PostAuthorize Annotations.
     e.g.: @PreAuthorize("hasRole('USER')"), @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
   - securedEnabled = true: determines if the @Secured annotation should be enabled.
     e.g.: @Secured("USER"), @Secured({"USER", "ADMIN" })
   - jsr250Enabled = true: allows us to use the @RoleAllowed annotation.
     e.g.: @RolesAllowed("USER"), @RolesAllowed({"USER", "ADMIN" })
   - plus d'info sur: https://www.baeldung.com/spring-security-method-security
*/
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class DemoSpringsecurityJwtApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoSpringsecurityJwtApplication.class, args);
    }

    // Creer un objet et le placer ds le contexte Spring, afin de l'injecter ds les autres classes
    @Bean
    PasswordEncoder getPasswordEncoder(){
        // utiliser BCrypt comme algorithme de cryptage
        return new BCryptPasswordEncoder();
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
