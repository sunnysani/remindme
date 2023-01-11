package com.lawtk.lawtkschedulerbe.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtils {

    private Key key;
    private JwtParser parser;

    @Autowired
    public JWTUtils(@Value("${jwt.secret}") String key) {
        this.key = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));
        this.parser = Jwts.parserBuilder().setSigningKey(this.key).build();
    }

    public String getUserUsername(String token) {
        Claims claims = getClaims(token);
        return String.valueOf(claims.get("username"));
    }

    public Claims getClaims(final String token) {
        String parsedToken = token;
        if (token.startsWith("Bearer "))
            parsedToken = parsedToken.substring(7);
        Jws<Claims> claimsJws = this.parser.parseClaimsJws(parsedToken);
        return claimsJws.getBody();
    }

}
