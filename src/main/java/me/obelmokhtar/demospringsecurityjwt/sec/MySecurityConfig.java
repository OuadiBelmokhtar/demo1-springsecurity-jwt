package me.obelmokhtar.demospringsecurityjwt.sec;

import me.obelmokhtar.demospringsecurityjwt.sec.entities.AppUser;
import me.obelmokhtar.demospringsecurityjwt.sec.filters.JwtAuthenticationFilter;
import me.obelmokhtar.demospringsecurityjwt.sec.filters.JwtAuthorizationFilter;
import me.obelmokhtar.demospringsecurityjwt.sec.repositories.AppUserRepository;
import me.obelmokhtar.demospringsecurityjwt.sec.services.UserAccountService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.ArrayList;
import java.util.Collection;

@Configuration
@EnableWebSecurity
public class MySecurityConfig extends WebSecurityConfigurerAdapter {

    private UserAccountService userAccountService;

    public MySecurityConfig(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    // le role de cette mtd c'est de spécifier les autorisations d’accès aux ressources exposées par l’application
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        System.out.println("MySecurityConfig.configure(HttpSecurity http)");
        // autoriser l'accès à ttes les fonctionnalités. Ce qui va ignorer le formulaire
        // d'authentification demandant de saisir le password généré par Spring Security
        //http.authorizeRequests().anyRequest().permitAll();

        // desactiver la protection contre les attaques CSRF, car CSRF est basé sur les sessions,
        // alors que l'auth stateless ne les utilise pas.
        http.csrf().disable();
        // demander a Spring de ne pas utiliser les sessions stockées coté serveur
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // autoriser l’accès à la console H2 sans authentification
        http.authorizeRequests().antMatchers("/h2-console/**").permitAll();
        // autoriser l’accès à cette mtd pr pouvoir renouveler le accessToken sans authentification
        http.authorizeRequests().antMatchers("/refreshToken/**").permitAll();
        // le path /login est par defaut autorise par Spring Security
       // http.authorizeRequests().antMatchers("/login/**").permitAll();
        // definir les autorisations(roles) d acces aux ressources
        // NB: la definition des autorisations devraient etre
        // placees AVANT la ligne http.authorizeRequests().anyRequest().authenticated();, sinon Exception.
       // http.authorizeRequests().antMatchers(HttpMethod.POST, "/users/**").hasAuthority("ADMIN");
       // http.authorizeRequests().antMatchers(HttpMethod.GET, "/users/**").hasAuthority("USER");
        // exiger une authentification pr acceder à chaque resource
        http.authorizeRequests().anyRequest().authenticated();
        // desactiver la protection par defaut contre les frames HTML
        http.headers().frameOptions().disable();
        // afficher le form d'authentification lorsque l'utilisateur n'a pas les droits d'accéder à la resource demandée
        //http.formLogin();
        // Enregistrer le filtre JwtAuthenticationFilter
        //authenticationManagerBean() est un bean injecté sous dessous
        http.addFilter(new JwtAuthenticationFilter(authenticationManagerBean()));
        // Enregistrer le filtre JwtAuthorizationFilter
        // lorsqu'on a plsr filtres qui traitent les requetes reçues, addFilterBefore() permet de bien definir
        // l'ordre d'exec des filtres. Ds notre cas, on veut intercepter chaque requete reçue
        // via JwtAuthorizationFilter.doFilterInternal(), alors on va le definir comme le premier a s'executer.
        http.addFilterBefore(new JwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    /* Cette mtd sera invoquee par Spring suite a JwtAuthenticationFilter.attemptAuthentication().
       Elle permet de recuperer le username+pwd+roles de la BD et de les retourner a Spring sous forme d'un objet User
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        System.out.println("MySecurityConfig.configure(AuthenticationManagerBuilder auth)");
        // type d'authentification qui consiste à utiliser la mémoire pr retrouver les utilisateurs et leurs droits d'accès
        // auth.inMemoryAuthentication();
        // type d'authentification qui consiste à utiliser des requêtes SQL pr retrouver les utilisateurs et leurs droits d'accès
        //auth.jdbcAuthentication();
        // type d'authentification qui consiste à définir notre propre démarche pr retrouver
        // les utilisateurs et leurs droits d'accès
        auth.userDetailsService(new UserDetailsService() {
            // cette mtd sera exécutée par Spring Security just après la reception du username+password de
            // la mtd JwtAuthenticationFilter.attemptAuthentication().
            // Elle accepte comme argument le username reçu ds la requete(POST /login) ou saisi ds le formLogin et retourne
            // un objet User de Spring représentant le user authentifié
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                System.out.println("MySecurityConfig.configure(AuthenticationManagerBuilder auth).loadUserByUsername()");
                // recuperer les details sur le user authentifié
                AppUser appUser = userAccountService.loadUserByUserName(username);
                // charger les roles du user stockés ds la BD ds une collection de type GrantedAuthority
                Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
                appUser.getUserRoles().forEach(role -> {
                    //SimpleGrantedAuthority implemente l interface GrantedAuthority
                    grantedAuthorities.add(new SimpleGrantedAuthority((role.getRoleName())));
                });
                // retourner a Spring les details sur le user authentifié(username+password+roles)
                // à ce point, Spring Security va s'occuper du rest:
                // - comparer les mots de passe(en utilisant BCrypt)
                // - comparer les rôles que possède le user avec les droits d'accès que possède la ressource demandée
                // - autoriser ou bien interdire le user d y accéder
                return new User(appUser.getUsername(), appUser.getPassword(), grantedAuthorities);
            }
        });
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
