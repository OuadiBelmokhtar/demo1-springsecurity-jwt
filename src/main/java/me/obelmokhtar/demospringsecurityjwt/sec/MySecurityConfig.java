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

        // exiger une authentification pr acceder à chaque resource
        //http.authorizeRequests().anyRequest().authenticated();
        // desactiver la protection contre les attaques CSRF
        http.csrf().disable();
        // desactiver la protection par defaut contre les frames HTML
        http.headers().frameOptions().disable();
        // afficher le form d'authentification lorsque l'utilisateur n'a pas les droits d'accéder à la resource demandée
        //http.formLogin();
    }

    // le role de cette mtd c'est de specifier les utilisateurs qui ont les droits d y acceder
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    
    }

}
