package com.rst.jwt_example.jwt;

import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
public class JwtService {

    private String username = "user";
    private String password = "loginpassword";
    private String secretKey = "012345678901234567890123456789012345";

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public String getRawSecretKey(){
        return secretKey;
    }

    public SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public boolean loginAction(LoginForm loginForm){
        if(loginForm.getUsername().equals(this.getUsername()) && loginForm.getPassword().equals(this.getPassword()))
            return true;
        return false;
    }

    public UsernameJwt getUsernameJwt(String username){
        return new UsernameJwt(username, this.getRawSecretKey());
    }
}
