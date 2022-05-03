package com.rst.jwt_example.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;

public class UsernameJwt {
    private Key secretKey;
    private Map<String, Object> claims = new HashMap<String, Object>();
    private String jws;

    public UsernameJwt(String username, String secretString){
        secretKey = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
        jws = Jwts.builder()
               .claim("username", username)
               .signWith(secretKey, SignatureAlgorithm.HS256)
               .compact();
    }

    public UsernameJwt(String jws){
        this.jws = jws;

    }

    public String parseUsername(Key secretKey) throws JwtException{
        Jws<Claims> claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(this.jws);
        return claims.getBody().get("username").toString();
    }

    @Override
    public String toString(){
        return jws;
    }
}
