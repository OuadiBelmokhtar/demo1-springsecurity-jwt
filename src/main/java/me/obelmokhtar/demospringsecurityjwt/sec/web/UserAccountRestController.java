package me.obelmokhtar.demospringsecurityjwt.sec.web;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import me.obelmokhtar.demospringsecurityjwt.sec.MySecurityConfig;
import me.obelmokhtar.demospringsecurityjwt.sec.entities.AppRole;
import me.obelmokhtar.demospringsecurityjwt.sec.entities.AppUser;
import me.obelmokhtar.demospringsecurityjwt.sec.filters.JwtAuthenticationFilter;
import me.obelmokhtar.demospringsecurityjwt.sec.services.UserAccountService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class UserAccountRestController {
    private UserAccountService userAccountService;

    public UserAccountRestController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    @GetMapping(path = "/refreshToken")
    // les deux objets passés en arg seront injectés par Spring
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1. Recuperer et verifier le refresh-token reçu ds le header 'Authorization'. Ce sont les memes
        //    instructions que ds la mtd JwtAuthorizationFilter.doFilterInternal.
        String refreshToken = request.getHeader("Authorization");
        if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
            try {
                String refreshTokenWithoutPrefix = refreshToken.substring(7);
                Algorithm hmacAlgo = Algorithm.HMAC384(JwtAuthenticationFilter.SIGNATURE_SECRET);
                JWTVerifier jwtVerifier = JWT.require(hmacAlgo).build();
                DecodedJWT decodedJWT = jwtVerifier.verify(refreshTokenWithoutPrefix);
                // recuprer le username, les roles c est pas la peine, car on va les recharger de la BD.
                String username = decodedJWT.getSubject();
                // une fois nous avons username, on peut verifier la blacklist
                // 2. Recharger les donnees de cet utilisateur de la BD, pr prendre en compte s il y a
                //    des changements ds les roles ou password.
                AppUser appUser = userAccountService.loadUserByUserName(username);
                // 3. Generer un nouvel accessToken
                String theNewJwtAccessToken = JWT.create().withSubject(username)
                        // la duree d'expiration en milliseconde (10min)
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                        .withIssuer(request.getRequestURL().toString())
                        // pr les roles, il faut passer une List pr que ça soit serialisee correctement en JSON.
                        .withClaim("roles", appUser.getUserRoles().stream().map(role -> role.getRoleName()).collect(Collectors.toList()))
                        .sign(hmacAlgo);
                // 4. Creer et definir le payload+signature du Refresh JWT
                String theNewJwtRefreshToken = JWT.create()
                        .withSubject(appUser.getUsername())
                        .withIssuer(request.getRequestURL().toString())
                        // la duree d'expiration en milliseconde généralement plus longue (1 annee)
                        .withExpiresAt(new Date(System.currentTimeMillis() + (1 * 365 * 24 * 60 * 60 * 1000)))
                        // pas besoin de mettre les infos d'accès (roles), le refreshToken c'est juste un token avec
                        // une durrée de vie plus longue par rapport a accessToken
                        //.withClaim("roles", authenticatedUser.getAuthorities().stream().map(ga -> ga.getAuthority().toString())
                        // .collect(Collectors.toList()))
                        // calculer la signature et signer le JWT
                        .sign(hmacAlgo);
                // 5. Envoyer les deux tokens ds le CORPS de la response ds une map en format JSON.
                // Noter bien que c'est possible de les envoyer ds deux headers ds la response, mais ce n'est pas pratique.
                Map<String, String> idTokens = new HashMap<>();
                idTokens.put("access-token", theNewJwtAccessToken);
                idTokens.put("refresh-token", theNewJwtRefreshToken);
                // indiquer au client que le corp de la response est en format JSON
                response.setContentType("application/json");
                // ObjectMapper est utilisé par Spring pr serialiser un objet en format JSON
                new ObjectMapper().writeValue(response.getOutputStream(), idTokens);
            }catch(Exception e){
                response.setHeader("error-message", e.getMessage());
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            }
        }else{
            throw new RuntimeException("Refresh token required");
        }
    }

    @PostMapping(path = "/users")
    // si on utilise @PostAuthorize, l'insertion sera executé avant la verif du role
    @PreAuthorize("hasAuthority('ADMIN')")
    AppUser addUser(@RequestBody AppUser appUser) {
        return userAccountService.addNewUser(appUser);
    }

    @GetMapping(path = "/users")
    // Cette mtd ne pourra etre executée que par les users disposant le role USER OU ADMIN
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
        // equivalent a
        //@Secured({"USER", "ADMIN"}) mais ça marche pas ds cet exemple, vu qu'il y a une autre
        // config a utiliser. voir https://www.baeldung.com/spring-security-method-security pr plus de details
        // equivalent a
        // @RolesAllowed({"USER", "ADMIN"}) mais ça marche pas ds cet exemple
    List<AppUser> getAllUsers() {
        return userAccountService.getAllUsers();
    }

    @PostMapping(path = "roles")
    @PreAuthorize("hasAuthority('ADMIN')")
    AppRole addRole(@RequestBody AppRole appRole) {
        return userAccountService.addNewRole(appRole);
    }

    @PostMapping(path = "/addRoleToUser")
    @PreAuthorize("hasAuthority('ADMIN')")
    void addRoleToUser(@RequestBody AppUserRoleForm appUserRoleForm) {
        userAccountService.addRoleToUser(appUserRoleForm.getRoleName(), appUserRoleForm.getUsername());
    }
}

@Data
class AppUserRoleForm {
    private String username;
    private String roleName;
}
