package com.docparser.springboot.service;

import com.docparser.springboot.Repository.UserRepository;
import com.docparser.springboot.model.Users;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    private static final String SECRET_KEY = "ana7263nsnakka838";
    public String generateToken(String ipAddress){
    Instant now = Instant.now();
    Instant expirationTime = now.plusSeconds(3600);
        return Jwts.builder().setSubject(ipAddress)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expirationTime))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();

}
    public String getIpAddressFromToken(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
    }

    public String generateAndSaveUserInfo(String ipAddress){
        String token = generateToken(ipAddress);
        Users user= new Users();
        user.setCreatedDate(Instant.now());
        user.setIpAddress(ipAddress);
        user.setTokenID(token);
        userRepository.save(user);
        return token;
    }
}


