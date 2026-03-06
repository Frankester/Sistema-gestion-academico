package com.francocallero.sistema.de.gestion.academico.servicios;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

@Service
public class TokenService {


    private final String HASH_ALGORITMO= "SHA-256";
    private final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

    public String generarToken(){
        byte[] randomBytes = new byte[32]; // 256 bits
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomBytes);

        return encoder.encodeToString(randomBytes);//transformar a caracteres válidos
    }


    public String hash(String token){
        try{
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITMO);

            byte[] encodedhash = digest.digest(token.getBytes(StandardCharsets.UTF_8));

            //transformar a hexa
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        }catch (NoSuchAlgorithmException ex){
            throw new RuntimeException(ex);
        }

    }
}
