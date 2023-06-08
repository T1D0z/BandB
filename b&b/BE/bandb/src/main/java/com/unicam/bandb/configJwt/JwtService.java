package com.unicam.bandb.configJwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY="34753778214125442A462D4A614E645267556B58703273357638792F423F4528";

    //ritorna come stringa l'email partendo dal pacchetto Jwt
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    //estrae un singolo claim dal jwt
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) //tempo indicativo
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact(); //genera e ritorna il token
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    //ritorna se il token è scaduto o meno
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    //estrae la scadenza
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    //estrae tutti i claims del jwt
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //decodifica la chiave da 256bit
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
