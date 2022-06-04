package me.obelmokhtar.demospringsecurityjwt.sec.filters;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/* Filtre requis par Spring Security. Il a pour but de
   - recupérer le token JWT envoyé ds les requetes, le verifier, le valider, recuperer les claims afin d'authentifier le user
     ds le context de Spring Security. Ce qui va permettre à Spring Security d'autoriser/interdire l'accès à la ressource.
   - Sinon, renvoyer au client un message et code d'erreur.
 */
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    // cette mtd s'exécute pour chaque requete envoyee a l'application
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // recuperer le header 'Authorization' qui contient le JWT de chaque requete reçue
        String authorizationToken = request.getHeader("Authorization");
        // verfier que le token existe ds le header et commence par le prefix 'Bearer '.
        // En effet, par convention il existe plsr préfixes(Basic, Bearer, NTML, OAuth2, ...)
        // utilisés par l'authentification HTTP. Le prefix 'Bearer' est utilisé lorsque
        // l'authentification est basée sur les token JWT.
        if (authorizationToken != null && authorizationToken.startsWith("Bearer ")) {

            try {
                // enlever le prefixe 'Bearer ' du token
                String authorizationTokenWithoutPrefix = authorizationToken.substring(7);
                // signer le JWT pr verifier qu il est valable
                // il faut utiliser le meme secret(clé privé) utilisé pr générer la signature du JWT initial
                // envoyé au client. C'est pas le cas pr RSA.
                Algorithm hmacAlgo = Algorithm.HMAC256(JwtAuthenticationFilter.SIGNATURE_SECRET);
                // creer un verifier pr verifier le token JWT
                JWTVerifier jwtVerifier = JWT.require(hmacAlgo).build();
                // verifier, parser et retourner le JWT contenant les claims(username, roles, ...)
                // en cas de prob de verification(JWT expiré, signature non valide), une exception sera levée
                DecodedJWT decodedJWT = jwtVerifier.verify(authorizationTokenWithoutPrefix);
                // recup username+roles
                String username = decodedJWT.getSubject();
                String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
                for (String role : roles) {
                    grantedAuthorities.add(new SimpleGrantedAuthority(role));
                }
                // creer un objet User de Spring pr authentifier l'utilisateur
                // pas besoin de passer le password(dailleurs n existe pas ds le token JWT),
                // juste username+roles suffit pr identifier le user
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(username, null, grantedAuthorities);
                // placer l'utilisateur ds le contexte de Spring Security, ce qui permet à Spring d'authentifier
                // le user et verif des autorisations d'accès lors de la demande d'une ressources.
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                // une fois le user est bien authentifié, Spring peut passer aux autres filtres
                filterChain.doFilter(request, response);

            } catch (Exception e) {
                // traiter le cas de prob de verification(JWT expiré, signature non valide)
                // renvoyer l erreur ds le header de la response
                response.setHeader("error-message", e.getMessage());
                // renvoyer le code HTTP de l erreur pr indiquer a l'utilisateur qu'il n'a pas
                // les droits d'accèder à la resource sollicitée.
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
            }
        } else { // si le JWT n existe pas ds la requete reçu
            // demander a Spring de passer au filtre suivant, tenant compte qu'il ne reconnait pas le user
            filterChain.doFilter(request, response);
        }

    }
}
