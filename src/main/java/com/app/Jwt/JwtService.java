package com.app.Jwt;

import com.app.persistence.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String secretKey = "IUfVPfCkCApOTSRoHB4onr6XIjDCyBEdPlPGXLAgpoc";
    public String getToken(UserEntity user){
        return getToken(new HashMap<>(), user);
    }

    //Creamos el token
    public String getToken(Map<String, Object> extraClaims, UserEntity user){
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+1000*60*24))
                .signWith(getSignatureKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    //Metodo para obtener la firma
    public Key getSignatureKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //Obtenemos todos los claims
    private Claims getAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignatureKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //Obtenemos un claim especifico
    public <T> T getClaim(String token, Function<Claims, T> claimsTFunction){
        Claims claims = getAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    //Validamos si el token es valido comprobando el username que se encuentra en el token y su expiracion
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    //Obtener expiracion del token
    private Date getExpiration(String token){
        return getClaim(token, Claims::getExpiration);
    }

    //Comprobar si esta expirado o no
    private boolean isTokenExpired(String token){
        return getExpiration(token).before(new Date(System.currentTimeMillis()));
    }
}
