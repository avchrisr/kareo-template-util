package com.chrisr.template_util.controller;

import com.chrisr.template_util.repository.entity.User;
import com.chrisr.template_util.response.ApiResponse;
import com.chrisr.template_util.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
public class UserRestControllerImpl implements UserRestController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Autowired
    public UserRestControllerImpl(PasswordEncoder passwordEncoder, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }


    @Override
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            removeSensitiveInfoFromUser(user);
        }
        return ResponseEntity.ok().body(users);
    }

    @Override
    public ResponseEntity<User> getUserById(@PathVariable(name = "id") long id) {
        User user = userService.getUserById(id);
        removeSensitiveInfoFromUser(user);
        return ResponseEntity.ok().body(user);
    }

    @Override
    public ResponseEntity<User> addUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.addUser(user);
        return ResponseEntity.ok().body(user);
    }

    @Override
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable(name = "id") long id) {
        userService.deleteUserById(id);
        String responseMessage = String.format("User with id = %s has been deleted.", id);
        return ResponseEntity.ok().body(new ApiResponse(true, responseMessage));
    }

    private void removeSensitiveInfoFromUser(User user) {
        user.setPassword(null);
    }



    // TODO: to be removed ----------------------------

    @GetMapping("/public/create-jwt")
    public String createJwt() {

        final String SECRET_KEY = "MY_SECRET_KEY";

        String id = "USER-12345";
        String issuer = "MY_APP";
        String subject = "chrisr";
        long ttlMillis = 360000L;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //Let's set the JWT Claims
        JwtBuilder builder = Jwts.builder().setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY);

        //if it has been specified, let's add the expiration
        if (ttlMillis > 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        // adds custom properties (k/v pair)
        builder.claim("scope", "my custom scope");

        //Builds the JWT and serializes it to a compact, URL-safe string
        return builder.compact();
    }

    @PostMapping("/public/decode-jwt")
    public String decodeJwt(@RequestBody String jwt) {

        final String SECRET_KEY = "MY_SECRET_KEY";


        //This line will throw an exception if it is not a signed JWS (as expected)
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(jwt).getBody();

/*
        try {
            Claims claim = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(jwt).getBody();
            String subject = claim.getSubject();
            // OK, you can trust this JWT

        } catch (SignatureException e) {
            // don't trust this JWT!

        }
*/

        return claims.toString();
    }
}