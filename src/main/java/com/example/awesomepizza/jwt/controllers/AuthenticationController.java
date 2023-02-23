package com.example.awesomepizza.jwt.controllers;

import com.example.awesomepizza.domain.User;
import com.example.awesomepizza.jwt.models.AuthenticationRequest;
import com.example.awesomepizza.jwt.models.AuthenticationResponse;
import com.example.awesomepizza.jwt.service.ApplicationUserDetailsService;
import com.example.awesomepizza.jwt.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ApplicationUserDetailsService userDetailsService;

    @RequestMapping(value = "/authenticate")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthenticationResponse authenticate(
            @RequestBody AuthenticationRequest req
    ) throws Exception {
        User user;

        try {
            user = userDetailsService.authenticate(
                    req.getEmail(), req.getPassword()
            );
        }
        catch (BadCredentialsException exception) {
            throw new Exception("Incorrect username or password", exception);
        }

        var userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        System.out.println(userDetails);
        var jwt = jwtUtil.generateToken(userDetails);

        return new AuthenticationResponse(jwt);
    }
}
