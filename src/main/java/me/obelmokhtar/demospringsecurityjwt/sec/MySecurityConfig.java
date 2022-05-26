package me.obelmokhtar.demospringsecurityjwt.sec;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class MySecurityConfig extends WebSecurityConfigurerAdapter {

    // le role de cette mtd c'est de spécifier les autorisations d’accès aux ressources exposées par l’application
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // autoriser l'accès à ttes les fonctionnalités. Ce qui va ignorer le formulaire
        // d'authentification demandant de saisir le password généré par Spring Security
        http.authorizeRequests().anyRequest().permitAll();
        // desactiver la protection contre les attaques CSRF
        http.csrf().disable();
        // desactiver la protection par defaut contre les frames HTML
        http.headers().frameOptions().disable();
    }

    // le role de cette mtd c'est de specifier les utilisateurs qui ont les droits d y acceder
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

    }

}
