package me.obelmokhtar.demospringsecurityjwt.sec;

public class JWTUtil {
    // clef privée utilisé lors de la génération de la signature(crypter) du JWT avant de l'envoyer au client.
    // Ainsi que lors de la vérification de la signature(décrypter) du JWT lors de la reception d'une requete client.
    public static final String SIGNATURE_SECRET = "mySecret1234";
    public static final String JWT_HEADER_NAME="Authorization";

}
